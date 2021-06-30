import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoDocumento } from '@core/models/csp/proyecto-documento';
import { ITipoDocumento, ITipoFase } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Group } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { NGXLogger } from 'ngx-logger';
import { of, Subscription } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { map, switchMap, tap } from 'rxjs/operators';
import { ProyectoActionService } from '../../proyecto.action.service';
import { NodeDocumento, ProyectoDocumentosFragment } from './proyecto-documentos.fragment';

const MSG_FILE_NOT_FOUND_ERROR = marker('error.file.info');
const MSG_UPLOAD_SUCCESS = marker('msg.file.upload.success');
const MSG_UPLOAD_ERROR = marker('error.file.upload');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const MSG_DELETE = marker('msg.delete.entity');
const DOCUMENTO_KEY = marker('csp.documento');
const PROYECTO_DOCUMENTO_FICHERO_KEY = marker('csp.proyecto-documento.fichero');
const PROYECTO_DOCUMENTO_NOMBRE_KEY = marker('csp.documento.nombre');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-proyecto-documentos',
  templateUrl: './proyecto-documentos.component.html',
  styleUrls: ['./proyecto-documentos.component.scss']
})
export class ProyectoDocumentosComponent extends FragmentComponent implements OnInit, OnDestroy {

  VIEW_MODE = VIEW_MODE;

  private subscriptions = [] as Subscription[];

  formPart: ProyectoDocumentosFragment;

  viewingNode: NodeDocumento;
  viewMode = VIEW_MODE.NONE;

  docReadonly: boolean;

  group = new Group();
  get formGroup(): FormGroup {
    return this.group.form;
  }

  uploading = false;
  tiposDocumento: ITipoDocumento[] = [];
  tipoFases$: Observable<ITipoFase[]> = of([]);

  msgParamEntity = {};
  msgParamFicheroEntity = {};
  msgParamNombreEntity = {};
  textoDelete: string;

  private tipoDocumentosFase = new Map<number, ITipoDocumento[]>();

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  treeControl: FlatTreeControl<NodeDocumento>;
  private treeFlattener: MatTreeFlattener<NodeDocumento, NodeDocumento>;
  dataSource: MatTreeFlatDataSource<NodeDocumento, NodeDocumento>;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  private getLevel = (node: NodeDocumento) => node.level;
  private isExpandable = (node: NodeDocumento) => node.childs.length > 0;
  private getChildren = (node: NodeDocumento): NodeDocumento[] => node.childs;
  private transformer = (node: NodeDocumento, level: number) => node;

  hasChild = (_: number, node: NodeDocumento) => node.childs.length > 0;
  compareFase = (option: ITipoFase, value: ITipoFase) => option?.id === value?.id;
  compareTipoDocumento = (option: ITipoDocumento, value: ITipoDocumento) => option?.id === value?.id;


  constructor(
    protected logger: NGXLogger,
    private dialogService: DialogService,
    public actionService: ProyectoActionService,
    private snackBarService: SnackBarService,
    private modeloEjecucionService: ModeloEjecucionService,
    private documentoService: DocumentoService,
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

    this.formPart = this.fragment as ProyectoDocumentosFragment;

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeDocumento>(this.getLevel, this.isExpandable);
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
        this.logger.error(ProyectoDocumentosComponent.name, 'ngOnInit()', error);
      }
    );
    this.subscriptions.push(subcription);
    this.group.load(new FormGroup({
      nombre: new FormControl('', Validators.required),
      fichero: new FormControl(null, Validators.required),
      tipoFase: new FormControl(null, IsEntityValidator.isValid),
      tipoDocumento: new FormControl(null, IsEntityValidator.isValid),
      comentarios: new FormControl(''),
      visible: new FormControl('')
    }));
    this.group.initialize();

    const idModeloEjecucion = this.actionService.modeloEjecucionId;
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyecto', SgiRestFilterOperator.EQUALS, 'true')
    };
    this.subscriptions.push(
      this.modeloEjecucionService.findModeloTipoDocumento(idModeloEjecucion).pipe(
        tap(() => {
          this.tipoFases$ = this.modeloEjecucionService.findModeloTipoFaseModeloEjecucion(idModeloEjecucion, options).pipe(
            map(modeloTipoFases => modeloTipoFases.items.map(modeloTipoFase => modeloTipoFase.tipoFase))
          );
        })
      ).subscribe(
        (tipos) => {
          tipos.items.forEach((tipo) => {
            const idTipoFase = tipo.modeloTipoFase ? tipo.modeloTipoFase.tipoFase.id : null;
            let tiposDocumentos = this.tipoDocumentosFase.get(idTipoFase);
            if (!tiposDocumentos) {
              tiposDocumentos = [];
              this.tipoDocumentosFase.set(idTipoFase, tiposDocumentos);
            }
            tiposDocumentos.push(tipo.tipoDocumento);
          });
        }
      )
    );
    this.subscriptions.push(this.formGroup.controls.tipoFase.valueChanges.subscribe((value: ITipoFase) => {
      if (this.viewMode === VIEW_MODE.EDIT || this.viewMode === VIEW_MODE.NEW) {
        this.formGroup.controls.tipoDocumento.reset();
      }
      this.tiposDocumento = this.tipoDocumentosFase.get(value ? value.id : null);
    }));
    this.switchToNone();
  }

  private setupI18N(): void {
    this.translate.get(
      DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_DOCUMENTO_FICHERO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFicheroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_DOCUMENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      DOCUMENTO_KEY,
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

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
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

  private switchToNone() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.loadDetails(undefined);
  }

  private loadDetails(node: NodeDocumento) {
    this.formGroup.enable();
    this.formGroup.get('tipoDocumento').enable();

    this.formGroup.reset();
    this.formGroup.get('nombre').patchValue(node?.documento?.value?.nombre);
    this.formGroup.get('fichero').patchValue(node?.fichero);

    this.formGroup.get('tipoFase').patchValue(node?.documento?.value?.tipoFase);
    this.formGroup.get('tipoDocumento').patchValue(node?.documento?.value?.tipoDocumento);
    this.formGroup.get('comentarios').patchValue(node?.documento?.value?.comentario);
    this.formGroup.get('visible').patchValue(node?.documento?.value?.visible);

    this.docReadonly = node?.readonly;

    this.group.refreshInitialState(Boolean(node?.documento));
    if (this.viewMode !== VIEW_MODE.NEW && this.viewMode !== VIEW_MODE.EDIT) {
      this.formGroup.disable();
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
  }

  switchToView() {
    this.viewMode = VIEW_MODE.VIEW;
    this.loadDetails(this.viewingNode);
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
        this.snackBarService.showSuccess(MSG_UPLOAD_SUCCESS);
        this.uploading = false;
        break;
      case 'error':
        this.snackBarService.showError(MSG_UPLOAD_ERROR);
        this.uploading = false;
        break;
    }
  }

  downloadFile(node: NodeDocumento): void {
    this.subscriptions.push(this.documentoService.downloadFichero(node.fichero.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, node.fichero.nombre);
      },
      (error) => {
        this.snackBarService.showError(MSG_DOWNLOAD_ERROR);
        this.logger.error(ProyectoDocumentosComponent.name, `downloadFile()`, error);
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

  private getDetailNode(): NodeDocumento {
    const detail = this.viewingNode;
    detail.documento.value.nombre = this.formGroup.get('nombre').value;
    detail.title = detail.documento.value.nombre;

    detail.documento.value.tipoFase = this.formGroup.get('tipoFase').value;
    detail.documento.value.tipoDocumento = this.formGroup.get('tipoDocumento').value;
    detail.documento.value.comentario = this.formGroup.get('comentarios').value;
    detail.fichero = this.formGroup.get('fichero').value;
    detail.documento.value.visible = this.formGroup.get('visible').value ? this.formGroup.get('visible').value : false;
    return detail;
  }

  private addNode(node: NodeDocumento): void {
    const createdNode = this.formPart.addNode(node);
    this.expandParents(createdNode);
    this.switchToNone();
  }

  private updateNode(node: NodeDocumento): void {
    this.formPart.updateNode(node);
    this.expandParents(node);
    this.switchToView();
  }

  private expandParents(node: NodeDocumento): void {
    if (node.parent) {
      this.treeControl.expand(node.parent);
      this.expandParents(node.parent);
    }
  }

  switchToNew(): void {
    const wrapper = new StatusWrapper<IProyectoDocumento>({} as IProyectoDocumento);
    const newNode: NodeDocumento = new NodeDocumento(null, undefined, 2, wrapper);
    this.viewMode = VIEW_MODE.NEW;
    this.viewingNode = newNode;
    this.loadDetails(this.viewingNode);
  }

  showNodeDetails(node: NodeDocumento) {
    this.viewingNode = node;
    if (!node.fichero && node.documento?.value.documentoRef) {
      this.subscriptions.push(this.documentoService.getInfoFichero(node.documento.value.documentoRef).subscribe(
        (info) => {
          node.fichero = info;
          this.switchToView();
        },
        (error) => {
          // TODO: Eliminar cuando los datos sean consistentes
          this.snackBarService.showError(MSG_FILE_NOT_FOUND_ERROR);
          this.switchToView();
          this.logger.error(ProyectoDocumentosComponent.name, `showNodeDetails(node: ${node})`, error);
        }
      ));
    } else {
      this.switchToView();
    }
  }

}
