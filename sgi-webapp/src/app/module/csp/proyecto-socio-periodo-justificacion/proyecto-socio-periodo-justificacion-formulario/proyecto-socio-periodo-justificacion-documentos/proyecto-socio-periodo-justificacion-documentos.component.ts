import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocioPeriodoJustificacionDocumento } from '@core/models/csp/proyecto-socio-periodo-justificacion-documento';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Group } from '@core/services/action-service';
import { TipoDocumentoService } from '@core/services/csp/tipo-documento.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ProyectoSocioPeriodoJustificacionActionService } from '../../proyecto-socio-periodo-justificacion.action.service';
import { NodeDocumentoProyecto, ProyectoSocioPeriodoJustificacionDocumentosFragment } from './proyecto-socio-periodo-justificacion-documentos.fragment';

const MSG_FILE_NOT_FOUND_ERROR = marker('error.file.info');
const MSG_UPLOAD_SUCCESS = marker('msg.file.upload.success');
const MSG_UPLOAD_ERROR = marker('error.file.upload');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_COMENTARIO_KEY = marker('csp.documento.comentarios');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_KEY = marker('csp.documento');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FICHERO_KEY = marker('csp.documento.fichero');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_NOMBRE_KEY = marker('csp.documento.nombre');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_VISIBLE_KEY = marker('csp.proyecto-documento.visible');
const DOCUMENTO_KEY = marker('csp.documento');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-proyecto-socio-periodo-justificacion-documentos',
  templateUrl: './proyecto-socio-periodo-justificacion-documentos.component.html',
  styleUrls: ['./proyecto-socio-periodo-justificacion-documentos.component.scss']
})
export class ProyectoSocioPeriodoJustificacionDocumentosComponent extends FragmentComponent implements OnInit, OnDestroy {
  VIEW_MODE = VIEW_MODE;

  formPart: ProyectoSocioPeriodoJustificacionDocumentosFragment;
  private subscriptions = [] as Subscription[];

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamComentarioEntity = {};
  msgParamEntity = {};
  msgParamEntities = {};
  msgParamFicheroEntity = {};
  msgParamNombreEntity = {};
  msgParamVisibleEntity = {};
  textoDelete: string;

  treeControl: FlatTreeControl<NodeDocumentoProyecto>;
  private treeFlattener: MatTreeFlattener<NodeDocumentoProyecto, NodeDocumentoProyecto>;
  dataSource: MatTreeFlatDataSource<NodeDocumentoProyecto, NodeDocumentoProyecto>;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  viewingNode: NodeDocumentoProyecto;
  viewMode = VIEW_MODE.NONE;

  group = new Group();
  get formGroup(): FormGroup {
    return this.group.form;
  }

  uploading = false;

  disableUpload = true;
  tiposDocumento$: Observable<ITipoDocumento[]> = of([]);

  private getLevel = (node: NodeDocumentoProyecto) => node.level;
  private isExpandable = (node: NodeDocumentoProyecto) => node.childs.length > 0;
  private getChildren = (node: NodeDocumentoProyecto): NodeDocumentoProyecto[] => node.childs;
  private transformer = (node: NodeDocumentoProyecto, level: number) => node;

  hasChild = (_: number, node: NodeDocumentoProyecto) => node.childs.length > 0;
  compareTipoDocumento = (option: ITipoDocumento, value: ITipoDocumento) => option?.id === value?.id;

  get readonly(): boolean {
    return this.actionService.readonly;
  }

  constructor(
    private readonly logger: NGXLogger,
    private actionService: ProyectoSocioPeriodoJustificacionActionService,
    private documentoService: DocumentoService,
    private tipoDocumentoService: TipoDocumentoService,
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

    this.formPart = this.fragment as ProyectoSocioPeriodoJustificacionDocumentosFragment;

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeDocumentoProyecto>(this.getLevel, this.isExpandable);
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
      nombre: new FormControl('', Validators.required),
      fichero: new FormControl(null, Validators.required),
      tipoDocumento: new FormControl(null, IsEntityValidator.isValid),
      comentarios: new FormControl('', Validators.maxLength(2_000)),
      visible: new FormControl(true, [Validators.required])
    }));
    this.group.initialize();
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    this.tiposDocumento$ = this.tipoDocumentoService.findTodos(options).pipe(
      map(result => result.items)
    );
    this.switchToNone();
  }


  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FICHERO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFicheroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntities = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_VISIBLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamVisibleEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showNodeDetails(node: NodeDocumentoProyecto) {
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

  private loadDetails(node: NodeDocumentoProyecto) {
    this.formGroup.enable();

    this.formGroup.reset();
    this.formGroup.get('nombre').patchValue(node?.documento?.value?.nombre);
    this.formGroup.get('fichero').patchValue(node?.fichero);
    this.formGroup.get('tipoDocumento').patchValue(node?.documento?.value?.tipoDocumento);
    this.formGroup.get('comentarios').patchValue(node?.documento?.value?.comentario);
    this.formGroup.get('visible').patchValue(Boolean(node?.documento?.value?.visible));

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


  downloadFile(node: NodeDocumentoProyecto): void {
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
        const currentDocumentoRef = this.viewingNode.documento.value.documentoRef;
        this.uploader.uploadSelection().subscribe(
          () => this.updateNode(this.getDetailNode(), currentDocumentoRef)
        );
      }
    }
  }

  private getDetailNode(): NodeDocumentoProyecto {
    const detail = this.viewingNode;
    detail.documento.value.nombre = this.formGroup.get('nombre').value;
    detail.title = detail.documento.value.nombre;
    detail.documento.value.tipoDocumento = this.formGroup.get('tipoDocumento').value;
    detail.documento.value.comentario = this.formGroup.get('comentarios').value;
    detail.documento.value.visible = Boolean(this.formGroup.get('visible').value);
    detail.fichero = this.formGroup.get('fichero').value;
    return detail;
  }

  private addNode(node: NodeDocumentoProyecto): void {
    const createdNode = this.formPart.addNode(node);
    this.expandParents(createdNode);
    this.switchToNone();
  }

  private switchToNone() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.loadDetails(undefined);
  }

  private updateNode(node: NodeDocumentoProyecto, currentDocumentoRef: string): void {
    this.formPart.updateNode(node, currentDocumentoRef);
    this.expandParents(node);
    this.switchToView();
  }

  private expandParents(node: NodeDocumentoProyecto): void {
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
    const wrapper = new StatusWrapper<IProyectoSocioPeriodoJustificacionDocumento>({} as IProyectoSocioPeriodoJustificacionDocumento);
    wrapper.value.visible = true;
    const newNode: NodeDocumentoProyecto = new NodeDocumentoProyecto(null, undefined, 2, wrapper);
    this.viewMode = VIEW_MODE.NEW;
    this.viewingNode = newNode;
    this.loadDetails(this.viewingNode);
  }

}
