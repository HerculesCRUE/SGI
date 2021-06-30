import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatTree, MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Group } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SolicitudActionService } from '../../solicitud.action.service';
import { NodeDocumentoSolicitud, SolicitudDocumentosFragment } from './solicitud-documentos.fragment';

const MSG_FILE_NOT_FOUND_ERROR = marker('error.file.info');
const MSG_UPLOAD_SUCCESS = marker('msg.file.upload.success');
const MSG_UPLOAD_ERROR = marker('error.file.upload');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_DOCUMENTOS_KEY = marker('csp.documento');
const SOLICITUD_DOCUMENTO_FICHERO_KEY = marker('csp.documento.fichero');
const SOLICITUD_DOCUMENTO_NOMBRE_KEY = marker('csp.documento.nombre');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-solicitud-documentos',
  templateUrl: './solicitud-documentos.component.html',
  styleUrls: ['./solicitud-documentos.component.scss']
})
export class SolicitudDocumentosComponent extends FragmentComponent implements OnInit, OnDestroy {
  VIEW_MODE = VIEW_MODE;

  formPart: SolicitudDocumentosFragment;
  private subscriptions = [] as Subscription[];

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  @ViewChild(MatTree, { static: true }) private matTree: MatTree<NodeDocumentoSolicitud>;
  treeControl: FlatTreeControl<NodeDocumentoSolicitud>;
  private treeFlattener: MatTreeFlattener<NodeDocumentoSolicitud, NodeDocumentoSolicitud>;
  dataSource: MatTreeFlatDataSource<NodeDocumentoSolicitud, NodeDocumentoSolicitud>;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  viewingNode: NodeDocumentoSolicitud;
  viewMode = VIEW_MODE.NONE;

  group = new Group();
  get formGroup(): FormGroup {
    return this.group.form;
  }

  uploading = false;

  disableUpload = true;
  tiposDocumento: ITipoDocumento[] = [];

  msgParamEntity = {};
  msgParamNombreEntity = {};
  msgParamFicheroEntity = {};
  textoDelete: string;

  private getLevel = (node: NodeDocumentoSolicitud) => node.level;
  private isExpandable = (node: NodeDocumentoSolicitud) => node.childs.length > 0;
  private getChildren = (node: NodeDocumentoSolicitud): NodeDocumentoSolicitud[] => node.childs;
  private transformer = (node: NodeDocumentoSolicitud, level: number) => node;

  hasChild = (_: number, node: NodeDocumentoSolicitud) => node.childs.length > 0;
  compareTipoDocumento = (option: ITipoDocumento, value: ITipoDocumento) => option?.id === value?.id;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    public actionService: SolicitudActionService,
    private documentoService: DocumentoService,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    private snackBar: SnackBarService,
    private dialogService: DialogService,
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

    this.formPart = this.fragment as SolicitudDocumentosFragment;

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeDocumentoSolicitud>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.formPart.documentos$.subscribe(
      (documentos) => {
        this.dataSource.data = documentos;
      },
      (error) => {
        this.logger.error(error);
      }
    );
    this.subscriptions.push(subcription);
    this.group.load(new FormGroup({
      nombre: new FormControl('', [
        Validators.required,
        Validators.maxLength(50)
      ]),
      fichero: new FormControl(null, Validators.required),
      tipoDocumento: new FormControl(null, IsEntityValidator.isValid),
      comentarios: new FormControl('')
    }));

    if (this.formPart.readonly) {
      this.group?.form?.disable();
    }

    this.group.initialize();
    const convocatoriaId = this.actionService.convocatoriaId;
    if (convocatoriaId) {
      this.subscriptions.push(
        this.configuracionSolicitudService.findAllTipoDocumentosFasePresentacion(convocatoriaId)
          .pipe(
            map(tipoDocumentos => tipoDocumentos.items)
          ).subscribe(
            (tipos) => {
              this.tiposDocumento = this.sortTipoDocumentos(tipos);
            }
          )
      );
    }
    this.switchToNone();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_DOCUMENTOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_DOCUMENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });


    this.translate.get(
      SOLICITUD_DOCUMENTO_FICHERO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFicheroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_DOCUMENTOS_KEY,
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

  private sortTipoDocumentos(tipoDocumentos: ITipoDocumento[]): ITipoDocumento[] {
    return tipoDocumentos.sort((a, b) => {
      if (a.nombre < b.nombre) {
        return -1;
      }
      if (a.nombre > b.nombre) {
        return 1;
      }
      return 0;
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showNodeDetails(node: NodeDocumentoSolicitud) {
    this.viewingNode = node;
    if (!node.fichero && node.documento?.value.documentoRef) {
      this.subscriptions.push(this.documentoService.getInfoFichero(node.documento.value.documentoRef).subscribe(
        (info) => {
          node.fichero = info;
          this.switchToView();
        },
        (error) => {
          // TODO: Eliminar cuando los datos sean consistentes
          this.logger.error(error);
          this.snackBar.showError(MSG_FILE_NOT_FOUND_ERROR);
          this.switchToView();
        }
      ));
    } else {
      this.switchToView();
    }
  }

  switchToView() {
    this.viewMode = VIEW_MODE.VIEW;
    this.loadDetails(this.viewingNode);
  }

  private loadDetails(node: NodeDocumentoSolicitud) {
    this.formGroup.enable();

    this.formGroup.reset();
    this.formGroup.get('nombre').patchValue(node?.documento?.value?.nombre);
    this.formGroup.get('fichero').patchValue(node?.fichero);
    this.formGroup.get('tipoDocumento').patchValue(node?.documento?.value?.tipoDocumento);
    this.formGroup.get('comentarios').patchValue(node?.documento?.value?.comentario);

    this.group.refreshInitialState(Boolean(node?.documento));
    if (this.viewMode !== VIEW_MODE.NEW && this.viewMode !== VIEW_MODE.EDIT) {
      this.formGroup.disable();
    }
  }

  hideNodeDetails() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
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

  downloadFile(node: NodeDocumentoSolicitud): void {
    this.subscriptions.push(this.documentoService.downloadFichero(node.fichero.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, node.fichero.nombre);
      },
      (error) => {
        this.logger.error(error);
        this.snackBar.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }

  acceptDetail(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      if (this.viewMode === VIEW_MODE.NEW) {
        this.uploader.uploadSelection().subscribe(
          () => this.addNode(this.getDetailNode())
        );
      }
      else if (this.viewMode === VIEW_MODE.EDIT) {
        this.uploader.uploadSelection().subscribe(
          () => this.updateNode(this.getDetailNode())
        );
      }
    }
  }

  private getDetailNode(): NodeDocumentoSolicitud {
    const detail = this.viewingNode;
    detail.documento.value.nombre = this.formGroup.get('nombre').value;
    detail.title = detail.documento.value.nombre;
    detail.documento.value.tipoDocumento = this.formGroup.get('tipoDocumento').value;
    detail.documento.value.comentario = this.formGroup.get('comentarios').value;
    detail.fichero = this.formGroup.get('fichero').value;
    return detail;
  }

  private addNode(node: NodeDocumentoSolicitud): void {
    const createdNode = this.formPart.addNode(node);
    this.refreshTree(this.formPart.documentos$.value);
    this.expandParents(createdNode);
    this.switchToNone();
  }

  private switchToNone() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.loadDetails(undefined);
  }

  private updateNode(node: NodeDocumentoSolicitud): void {
    this.formPart.updateNode(node);
    this.expandParents(node);
    this.switchToView();
  }

  private expandParents(node: NodeDocumentoSolicitud): void {
    if (node.parent) {
      this.treeControl.expand(node.parent);
      this.expandParents(node.parent);
    }
  }

  cancelDetail(): void {
    if (this.viewMode === VIEW_MODE.EDIT) {
      this.switchToView();
    }
    else {
      this.switchToNone();
    }
  }

  switchToEdit(): void {
    this.viewMode = VIEW_MODE.EDIT;
    this.loadDetails(this.viewingNode);
    this.formGroup.get('tipoDocumento').disable();
  }

  deleteDetail(): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteNode(this.viewingNode);
            this.switchToNone();
          }
        }
      )
    );
  }

  switchToNew(): void {
    const wrapper = new StatusWrapper<ISolicitudDocumento>({} as ISolicitudDocumento);
    const newNode: NodeDocumentoSolicitud = new NodeDocumentoSolicitud(null, undefined, 2, false, wrapper);
    this.viewMode = VIEW_MODE.NEW;
    this.viewingNode = newNode;
    this.loadDetails(this.viewingNode);
  }

  private refreshTree(nodes: NodeDocumentoSolicitud[]) {
    // There is no way to update the tree without recreating it,
    // because CDK Tree detect changes only when a root nodes changes
    // See: https://github.com/angular/components/issues/11381
    this.matTree.renderNodeChanges([]);
    this.matTree.renderNodeChanges(nodes);
  }

}
