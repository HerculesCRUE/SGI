import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresaDocumento } from '@core/models/eer/empresa-documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Group } from '@core/services/action-service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { EmpresaExplotacionResultadosActionService } from '../../empresa-explotacion-resultados.action.service';
import { EmpresaExplotacionResultadosDocumentosFragment, NodeDocumento } from './empresa-explotacion-resultados-documentos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_UPLOAD_SUCCESS = marker('msg.file.upload.success');
const MSG_UPLOAD_ERROR = marker('error.file.upload');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const MSG_FILE_NOT_FOUND_ERROR = marker('error.file.info');
const EMPRESA_DOCUMENTO_KEY = marker('eer.empresa-documento');
const EMPRESA_DOCUMENTO_NOMBRE_KEY = marker('eer.empresa-documento.nombre');
const EMPRESA_DOCUMENTO_DOCUMENTO_KEY = marker('eer.empresa-documento.documento');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-empresa-explotacion-resultados-documentos',
  templateUrl: './empresa-explotacion-resultados-documentos.component.html',
  styleUrls: ['./empresa-explotacion-resultados-documentos.component.scss']
})
export class EmpresaExplotacionResultadosDocumentosComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: EmpresaExplotacionResultadosDocumentosFragment;
  private subscriptions = [] as Subscription[];

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  treeControl: FlatTreeControl<NodeDocumento>;
  private treeFlattener: MatTreeFlattener<NodeDocumento, NodeDocumento>;
  dataSource: MatTreeFlatDataSource<NodeDocumento, NodeDocumento>;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  viewingNode: NodeDocumento;
  viewMode = VIEW_MODE.NONE;

  group = new Group();
  get formGroup(): FormGroup {
    return this.group.form;
  }

  uploading = false;

  msgParamEntity = {};
  msgParamNombreEntity = {};
  msgParamDocumentoEntity = {};
  textoDelete: string;

  private getLevel = (node: NodeDocumento) => node.level;
  private isExpandable = (node: NodeDocumento) => node.childs.length > 0;
  private getChildren = (node: NodeDocumento): NodeDocumento[] => node.childs;
  private transformer = (node: NodeDocumento, level: number) => node;

  hasChild = (_: number, node: NodeDocumento) => node.childs.length > 0;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get VIEW_MODE() {
    return VIEW_MODE;
  }

  constructor(
    private dialogService: DialogService,
    public actionService: EmpresaExplotacionResultadosActionService,
    private documentoService: DocumentoService,
    private snackBar: SnackBarService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DOCUMENTOS, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as EmpresaExplotacionResultadosDocumentosFragment;

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeDocumento>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    this.subscriptions.push(this.formPart.documentos$.subscribe((documentos) => {
      this.dataSource.data = documentos;
    }));
    this.group.load(new FormGroup({
      nombre: new FormControl('', [
        Validators.required,
        Validators.maxLength(250)
      ]),
      tipoDocumento: new FormControl(null),
      subtipoDocumento: new FormControl(null),
      documento: new FormControl(null, Validators.required),
      comentarios: new FormControl('')
    }));
    this.group.initialize();

    this.subscriptions.push(this.formGroup.controls.tipoDocumento.valueChanges.subscribe(() => {
      if (this.viewMode === VIEW_MODE.EDIT || this.viewMode === VIEW_MODE.NEW) {
        this.formGroup.controls.subtipoDocumento.reset();
      }
    }));
    this.switchToNone();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showNodeDetails(node: NodeDocumento) {
    this.viewingNode = node;
    if (!node.documento.value.documento.nombre && node.documento?.value.documento.documentoRef) {
      this.subscriptions.push(this.documentoService.getInfoFichero(node.documento.value.documento.documentoRef).subscribe(
        (info) => {
          node.documento.value.documento = info;
          this.switchToView();
        },
        () => {
          this.snackBar.showError(MSG_FILE_NOT_FOUND_ERROR);
          this.switchToView();
        }
      ));
    }
    else {
      this.switchToView();
    }
  }

  hideNodeDetails() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
  }

  switchToNew() {
    const wrapper = new StatusWrapper<IEmpresaDocumento>({} as IEmpresaDocumento);
    const newNode: NodeDocumento = new NodeDocumento(null, undefined, 2, wrapper);
    this.viewMode = VIEW_MODE.NEW;
    this.viewingNode = newNode;
    this.loadDetails(this.viewingNode);
  }

  switchToEdit() {
    this.viewMode = VIEW_MODE.EDIT;
    this.loadDetails(this.viewingNode);
  }

  switchToView() {
    this.viewMode = VIEW_MODE.VIEW;
    this.loadDetails(this.viewingNode);
  }

  private switchToNone() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.loadDetails(undefined);
  }

  private loadDetails(node: NodeDocumento) {
    this.formGroup.enable();

    this.formGroup.reset();
    this.formGroup.get('nombre').patchValue(node?.documento?.value?.nombre);
    this.formGroup.get('tipoDocumento').patchValue(node?.documento?.value?.tipoDocumento);
    this.formGroup.get('subtipoDocumento').patchValue(node?.documento?.value?.subtipoDocumento);
    this.formGroup.get('documento').patchValue(node?.documento?.value?.documento);
    this.formGroup.get('comentarios').patchValue(node?.documento?.value?.comentarios);

    this.group.refreshInitialState(Boolean(node?.documento));
    if (this.viewMode !== VIEW_MODE.NEW && this.viewMode !== VIEW_MODE.EDIT) {
      this.formGroup.disable();
    }
  }

  cancelDetail() {
    if (this.viewMode === VIEW_MODE.EDIT) {
      this.switchToView();
    }
    else {
      this.switchToNone();
    }
  }

  acceptDetail() {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      if (this.viewMode === VIEW_MODE.NEW) {
        this.uploader.uploadSelection().subscribe(
          () => this.addNode(this.getDetailNode())
        );
      }
      else if (this.viewMode === VIEW_MODE.EDIT) {
        const currentDocumentoRef = this.viewingNode.documento.value.documento.documentoRef;
        this.uploader.uploadSelection().subscribe(
          () => this.updateNode(this.getDetailNode(), currentDocumentoRef)
        );
      }
    }
  }

  private getDetailNode(): NodeDocumento {
    const detail = this.viewingNode;
    detail.documento.value.nombre = this.formGroup.get('nombre').value;
    detail.title = detail.documento.value.nombre;
    detail.documento.value.tipoDocumento = this.formGroup.get('tipoDocumento').value;
    detail.documento.value.subtipoDocumento = this.formGroup.get('subtipoDocumento').value;
    detail.documento.value.documento = this.formGroup.get('documento').value;
    detail.documento.value.comentarios = this.formGroup.get('comentarios').value;

    return detail;
  }

  private addNode(node: NodeDocumento) {
    const createdNode = this.formPart.addNode(node);
    this.expandParents(createdNode);
    this.switchToNone();
  }

  private updateNode(node: NodeDocumento, currentDocumentoRef: string): void {
    this.formPart.updateNode(node, currentDocumentoRef);
    this.expandParents(node);
    this.switchToView();
  }

  private expandParents(node: NodeDocumento) {
    if (node.parent) {
      this.treeControl.expand(node.parent);
      this.expandParents(node.parent);
    }
  }

  deleteDetail() {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete, this.msgParamEntity).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteNode(this.viewingNode);
            this.switchToNone();
          }
        }
      )
    );
  }

  onUploadProgress(event: UploadEvent) {
    switch (event.status) {
      case 'start':
        this.uploading = true;
        break;
      case 'end':
        this.snackBar.showSuccess(MSG_UPLOAD_SUCCESS);
        this.uploading = false;
        break;
      case 'error':
        this.snackBar.showError(MSG_UPLOAD_ERROR);
        this.uploading = false;
        break;
    }
  }

  downloadFile(node: NodeDocumento): void {
    this.subscriptions.push(this.documentoService.downloadFichero(node.documento.value.documento.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, node.documento.value.documento.nombre);
      },
      () => {
        this.snackBar.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }

  private setupI18N(): void {
    this.translate.get(
      EMPRESA_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      EMPRESA_DOCUMENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_DOCUMENTO_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }
}
