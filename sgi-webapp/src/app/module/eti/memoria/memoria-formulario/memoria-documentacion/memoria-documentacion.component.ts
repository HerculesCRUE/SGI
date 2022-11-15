import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { COMITE } from '@core/models/eti/comite';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { ESTADO_RETROSPECTIVA, IEstadoRetrospectiva } from '@core/models/eti/estado-retrospectiva';
import { FORMULARIO } from '@core/models/eti/formulario';
import { ESTADO_MEMORIA, TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaDocumentacionMemoriaModalComponent, MemoriaDocumentacionMemoriaModalData } from '../../modals/memoria-documentacion-memoria-modal/memoria-documentacion-memoria-modal.component';
import { MemoriaDocumentacionFragment, TIPO_DOCUMENTACION } from './memoria-documentacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const DOCUMENTACION_KEY = marker('eti.documentacion');
const DOCUMENTO_KEY = marker('eti.memoria.documento');

@Component({
  selector: 'sgi-memoria-documentacion',
  templateUrl: './memoria-documentacion.component.html',
  styleUrls: ['./memoria-documentacion.component.scss']
})
export class MemoriaDocumentacionComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: MemoriaDocumentacionFragment;

  private subscriptions: Subscription[] = [];

  readonly dataSourceDocumentoMemoria = new MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>();
  readonly dataSourceSeguimientoAnual = new MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>();
  readonly dataSourceSeguimientoFinal = new MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>();
  readonly dataSourceRetrospectiva = new MatTableDataSource<StatusWrapper<IDocumentacionMemoria>>();

  @ViewChild('paginatorDocumentacionMemoria', { static: true }) private paginatorDocumentacionMemoria: MatPaginator;
  @ViewChild('paginatorSeguimientoAnual', { static: true }) private paginatorSeguimientoAnual: MatPaginator;
  @ViewChild('paginatorSeguimientoFinal', { static: true }) private paginatorSeguimientoFinal: MatPaginator;
  @ViewChild('paginatorRetrospectiva', { static: true }) private paginatorRetrospectiva: MatPaginator;

  @ViewChild('sortDocumentacionMemoria', { static: true }) private sortDocumentacionMemoria: MatSort;
  @ViewChild('sortSeguimientoAnual', { static: true }) private sortSeguimientoAnual: MatSort;
  @ViewChild('sortSeguimientoFinal', { static: true }) private sortSeguimientoFinal: MatSort;
  @ViewChild('sortRetrospectiva', { static: true }) private sortRetrospectiva: MatSort;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumnsDocumentoMemoria: string[] = ['tipoDocumento', 'nombre', 'acciones'];
  elementosPaginaDocumentoMemoria: number[] = [5, 10, 25, 100];

  displayedColumnsSeguimientoAnual: string[] = ['nombre', 'acciones'];
  elementosPaginaSeguimientoAnual: number[] = [5, 10, 25, 100];

  displayedColumnsSeguimientoFinal: string[] = ['nombre', 'acciones'];
  elementosPaginaSeguimientoFinal: number[] = [5, 10, 25, 100];

  displayedColumnsRetrospectiva: string[] = ['nombre', 'acciones'];
  elementosPaginaRetrospectiva: number[] = [5, 10, 25, 100];

  msgParamDocumentoEntity = {};
  private textoDelete: string;

  private sortingDataAccesor = (wrapper: StatusWrapper<IDocumentacionMemoria>, property: string) => {
    switch (property) {
      case 'tipoDocumento':
        return wrapper.value.tipoDocumento?.nombre;
      default:
        return wrapper.value[property];
    }
  }

  get formularioComite(): FORMULARIO {
    return this.actionService.getComite().id;
  }

  private get estadoMemoria(): TipoEstadoMemoria {
    return this.actionService.getEstadoMemoria();
  }

  private get estadoRetrospectiva(): IEstadoRetrospectiva {
    return this.actionService.getRetrospectiva()?.estadoRetrospectiva;
  }

  get FORMULARIO() {
    return FORMULARIO;
  }

  get TIPO_DOCUMENTACION() {
    return TIPO_DOCUMENTACION;
  }

  get showRetrospectiva(): boolean {
    return this.actionService.getComite().id === COMITE.CEEA;
  }

  constructor(
    private readonly dialogService: DialogService,
    private matDialog: MatDialog,
    private actionService: MemoriaActionService,
    private readonly documentoService: DocumentoService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService) {

    super(actionService.FRAGMENT.DOCUMENTACION, actionService);

    this.formPart = this.fragment as MemoriaDocumentacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSourceDocumentoMemoria.paginator = this.paginatorDocumentacionMemoria;
    this.dataSourceDocumentoMemoria.sort = this.sortDocumentacionMemoria;
    this.subscriptions.push(this.formPart.documentacionesMemoria$.subscribe(elements => {
      this.dataSourceDocumentoMemoria.data = elements;
    }));

    this.dataSourceDocumentoMemoria.sortingDataAccessor = this.sortingDataAccesor;

    this.dataSourceSeguimientoAnual.paginator = this.paginatorSeguimientoAnual;
    this.dataSourceSeguimientoAnual.sort = this.sortSeguimientoAnual;
    this.subscriptions.push(this.formPart.documentacionesSeguimientoAnual$.subscribe(elements => {
      this.dataSourceSeguimientoAnual.data = elements;
    }));

    this.dataSourceSeguimientoAnual.sortingDataAccessor = this.sortingDataAccesor;

    this.dataSourceSeguimientoFinal.paginator = this.paginatorSeguimientoFinal;
    this.dataSourceSeguimientoFinal.sort = this.sortSeguimientoFinal;
    this.subscriptions.push(this.formPart.documentacionesSeguimientoFinal$.subscribe(elements => {
      this.dataSourceSeguimientoFinal.data = elements;
    }));

    this.dataSourceSeguimientoFinal.sortingDataAccessor = this.sortingDataAccesor;

    this.dataSourceRetrospectiva.paginator = this.paginatorRetrospectiva;
    this.dataSourceRetrospectiva.sort = this.sortRetrospectiva;
    this.subscriptions.push(this.formPart.documentacionesRetrospectiva$.subscribe(elements => {
      this.dataSourceRetrospectiva.data = elements;
    }));

    this.dataSourceRetrospectiva.sortingDataAccessor = this.sortingDataAccesor;
  }

  private setupI18N(): void {
    this.translate.get(
      DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      DOCUMENTACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  openModalDocumentacion(tipoDocumentacion: TIPO_DOCUMENTACION): void {
    let tipoEvaluacion: TIPO_EVALUACION;
    switch (tipoDocumentacion) {
      case TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL:
        tipoEvaluacion = TIPO_EVALUACION.SEGUIMIENTO_ANUAL;
        break;
      case TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL:
        tipoEvaluacion = TIPO_EVALUACION.SEGUIMIENTO_FINAL;
        break;
      case TIPO_DOCUMENTACION.RETROSPECTIVA:
        tipoEvaluacion = TIPO_EVALUACION.RETROSPECTIVA;
        break;
      case TIPO_DOCUMENTACION.INICIAL:
        tipoEvaluacion = TIPO_EVALUACION.MEMORIA;
        break;
    }
    const data: MemoriaDocumentacionMemoriaModalData = {
      memoriaId: this.fragment.getKey() as number,
      tipoEvaluacion,
      showTipoDocumentos: this.formPart.isInvestigador && !this.actionService.readonly,
      comite: this.actionService.getComite()
    };
    const config = {
      data
    };

    const dialogRef = this.matDialog.open(MemoriaDocumentacionMemoriaModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (documentacion: IDocumentacionMemoria) => {
        if (documentacion) {
          this.formPart.addDocumento(tipoDocumentacion, documentacion);
        }
      }
    );
  }

  deleteDocumentacion(tipoDocumento: TIPO_DOCUMENTACION, wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>): void {
    const dialogSubscription = this.dialogService.showConfirmation(
      this.textoDelete
    ).pipe(switchMap((accept) => {
      if (accept) {
        return of(this.formPart.deleteDocumento(tipoDocumento, wrappedDocumentacion));
      }
      return of();
    })).subscribe();

    this.subscriptions.push(dialogSubscription);
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  isEditAllowed(tipoDocumentacion: TIPO_DOCUMENTACION): boolean {
    if (this.actionService.readonly) {
      if (this.authService.hasAuthority('ETI-MEM-EDOC') && tipoDocumentacion === TIPO_DOCUMENTACION.INICIAL) {
        return this.estadoMemoria.id >= ESTADO_MEMORIA.EN_EVALUACION;
      }
      return false;
    }
    switch (tipoDocumentacion) {
      case TIPO_DOCUMENTACION.INICIAL:
        return this.estadoMemoria.id === ESTADO_MEMORIA.NO_PROCEDE_EVALUAR
          ? false
          : this.estadoMemoria.id === ESTADO_MEMORIA.EN_ELABORACION
          || this.estadoMemoria.id === ESTADO_MEMORIA.COMPLETADA
          || this.estadoMemoria.id === ESTADO_MEMORIA.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS
          || this.estadoMemoria.id === ESTADO_MEMORIA.PENDIENTE_CORRECCIONES
          || this.estadoMemoria.id >= ESTADO_MEMORIA.FIN_EVALUACION;
      case TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL:
        return this.estadoMemoria.id === ESTADO_MEMORIA.FIN_EVALUACION
          || this.estadoMemoria.id === ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_ANUAL;
      case TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL:
        return this.estadoMemoria.id === ESTADO_MEMORIA.FIN_EVALUACION_SEGUIMIENTO_ANUAL
          || this.estadoMemoria.id === ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_FINAL
          || this.estadoMemoria.id === ESTADO_MEMORIA.EN_ACLARACION_SEGUIMIENTO_FINAL;
      case TIPO_DOCUMENTACION.RETROSPECTIVA:
        return this.estadoRetrospectiva?.id === ESTADO_RETROSPECTIVA.PENDIENTE
          || this.estadoRetrospectiva?.id === ESTADO_RETROSPECTIVA.COMPLETADA;
    }

  }

  downloadDocumentacion(documentacionMemoria: IDocumentacionMemoria) {
    let action$: Observable<Blob>;
    if (documentacionMemoria.documento.nombre) {
      action$ = this.documentoService.downloadFichero(documentacionMemoria.documento.documentoRef);
    }
    else {
      action$ = this.documentoService.getInfoFichero(documentacionMemoria.documento.documentoRef).pipe(
        switchMap(documento => {
          documentacionMemoria.documento = documento;
          return this.documentoService.downloadFichero(documento.documentoRef);
        })
      );
    }
    this.subscriptions.push(action$.subscribe(
      (response) => triggerDownloadToUser(response, documentacionMemoria.documento.nombre)
    ));
  }

}
