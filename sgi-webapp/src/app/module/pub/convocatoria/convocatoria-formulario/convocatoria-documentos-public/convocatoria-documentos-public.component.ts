import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Group } from '@core/services/action-service';
import { DocumentoPublicService } from '@core/services/sgdoc/documento-public.service';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent } from '@shared/file-upload/file-upload.component';
import { Subscription } from 'rxjs';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaDocumentosPublicFragment, NodeDocumento } from './convocatoria-documentos-public.fragment';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const MSG_FILE_NOT_FOUND_ERROR = marker('error.file.info');
const CONVOCATORIA_DOCUMENTO_KEY = marker('csp.convocatoria-documento');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-convocatoria-documentos-public',
  templateUrl: './convocatoria-documentos-public.component.html',
  styleUrls: ['./convocatoria-documentos-public.component.scss']
})
export class ConvocatoriaDocumentosPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaDocumentosPublicFragment;
  private subscriptions = [] as Subscription[];

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  treeControl: FlatTreeControl<NodeDocumento>;
  private treeFlattener: MatTreeFlattener<NodeDocumento, NodeDocumento>;
  dataSource: MatTreeFlatDataSource<NodeDocumento, NodeDocumento>;

  viewingNode: NodeDocumento;
  viewMode = VIEW_MODE.NONE;

  group = new Group();
  get formGroup(): FormGroup {
    return this.group.form;
  }

  msgParamEntity = {};

  private getLevel = (node: NodeDocumento) => node.level;
  private isExpandable = (node: NodeDocumento) => node.childs.length > 0;
  private getChildren = (node: NodeDocumento): NodeDocumento[] => node.childs;
  private transformer = (node: NodeDocumento, level: number) => node;

  hasChild = (_: number, node: NodeDocumento) => node.childs.length > 0;

  constructor(
    public actionService: ConvocatoriaPublicActionService,
    private documentoService: DocumentoPublicService,
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

    this.formPart = this.fragment as ConvocatoriaDocumentosPublicFragment;

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
        Validators.maxLength(50)
      ]),
      fichero: new FormControl(null, Validators.required),
      fase: new FormControl(null),
      tipoDocumento: new FormControl(null),
      publico: new FormControl(true, Validators.required),
      observaciones: new FormControl('')
    }));
    this.group.initialize();

    this.switchToNone();
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });
  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showNodeDetails(node: NodeDocumento) {
    this.viewingNode = node;
    if (!node.fichero && node.documento?.value.documentoRef) {
      this.subscriptions.push(this.documentoService.getInfoFichero(node.documento.value.documentoRef).subscribe(
        (info) => {
          node.fichero = info;
          this.switchToView();
        },
        () => {
          // TODO: Eliminar cuando los datos sean consistentens
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
    this.formGroup.get('fichero').patchValue(node?.fichero);
    this.formGroup.get('fase').setValue(node?.documento?.value?.tipoFase?.nombre);
    this.formGroup.get('tipoDocumento').patchValue(node?.documento?.value?.tipoDocumento?.nombre);
    this.formGroup.get('publico').patchValue(node?.documento?.value?.publico);
    this.formGroup.get('observaciones').patchValue(node?.documento?.value?.observaciones);

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

  downloadFile(node: NodeDocumento): void {
    this.subscriptions.push(this.documentoService.downloadFichero(node.fichero.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, node.fichero.nombre);
      },
      () => {
        this.snackBar.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }
}
