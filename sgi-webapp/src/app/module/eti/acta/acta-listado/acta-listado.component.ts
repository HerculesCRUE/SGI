import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { ESTADO_ACTA_MAP } from '@core/models/eti/tipo-estado-acta';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { ActaService } from '@core/services/eti/acta.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { ActaListadoExportModalComponent, IActaListadoModalData } from '../modals/acta-listado-export-modal/acta-listado-export-modal.component';

const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_FINALIZAR_ERROR = marker('error.eti.acta.finalizar');
const MSG_FINALIZAR_SUCCESS = marker('msg.eti.acta.finalizar.success');
const ACTA_KEY = marker('eti.acta');
const MSG_REGISTRO_BLOCKCHAIN_OK = marker('msg.eti.acta.registro-blockchain.ok');
const MSG_REGISTRO_BLOCKCHAIN_ALTERADO = marker('msg.eti.acta.registro-blockchain.alterado');

@Component({
  selector: 'sgi-acta-listado',
  templateUrl: './acta-listado.component.html',
  styleUrls: ['./acta-listado.component.scss']
})
export class ActaListadoComponent extends AbstractTablePaginationComponent<IActaWithNumEvaluaciones> implements OnInit {

  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  actas$: Observable<IActaWithNumEvaluaciones[]> = of();

  finalizarSubscription: Subscription;

  textoCrear: string;
  private textoFinalizarError: string;

  blockchainEnable: boolean;

  get ESTADO_ACTA_MAP() {
    return ESTADO_ACTA_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly actasService: ActaService,
    protected readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private readonly documentoService: DocumentoService,
    private readonly matDialog: MatDialog,
    private readonly cnfService: ConfigService
  ) {
    super();

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.formGroup = new FormGroup({
      comite: new FormControl(null),
      fechaEvaluacionInicio: new FormControl(null, []),
      fechaEvaluacionFin: new FormControl(null, []),
      numeroActa: new FormControl('', []),
      tipoEstadoActa: new FormControl(null, [])
    });
  }

  private setupI18N(): void {
    this.translate.get(
      ACTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      MSG_FINALIZAR_ERROR
    ).subscribe((value) => this.textoFinalizarError = value);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IActaWithNumEvaluaciones>> {
    const observable$ = this.actasService.findActivasWithEvaluaciones(this.getFindOptions(reset));
    this.cnfService.isBlockchainEnable().subscribe(value => {
      this.blockchainEnable = value;
    });
    return observable$;
  }

  protected initColumns(): void {
    this.displayedColumns = ['convocatoriaReunion.comite', 'convocatoriaReunion.fechaEvaluacion', 'numero', 'convocatoriaReunion.tipoConvocatoriaReunion',
      'numeroIniciales', 'numeroRevisiones', 'numeroTotal', 'estadoActual.nombre', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter(
      'convocatoriaReunion.comite.id',
      SgiRestFilterOperator.EQUALS,
      controls.comite.value?.id?.toString()
    ).and(
      'convocatoriaReunion.fechaEvaluacion',
      SgiRestFilterOperator.GREATHER_OR_EQUAL,
      LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value)
    ).and(
      'convocatoriaReunion.fechaEvaluacion',
      SgiRestFilterOperator.LOWER_OR_EQUAL,
      LuxonUtils.toBackend(controls.fechaEvaluacionFin.value)
    ).and(
      'convocatoriaReunion.numeroActa',
      SgiRestFilterOperator.EQUALS,
      controls.numeroActa?.value.toString()
    ).and(
      'estadoActual.id',
      SgiRestFilterOperator.EQUALS,
      controls.tipoEstadoActa.value?.toString()
    );

    return filter;
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  protected loadTable(reset?: boolean) {
    this.actas$ = this.getObservableLoadTable(reset);
  }

  /**
   * Finaliza el acta con el id recibido.
   * @param actaId id del acta a finalizar.
   */
  finishActa(actaId: number) {
    this.finalizarSubscription = this.actasService.finishActa(actaId).subscribe((acta) => {
      this.snackBarService.showSuccess(MSG_FINALIZAR_SUCCESS);
      this.loadTable(false);
    },
      catchError((error) => {
        this.logger.error(error);
        // On error reset pagination values
        this.paginator.firstPage();
        this.totalElementos = 0;
        if (error instanceof SgiError) {
          this.processError(error);
        }
        else {
          this.processError(new SgiError(this.textoFinalizarError));
        }
        return of([]);
      }));
  }

  /**
   * Comprueba si una acta se encuentra en estado finalizada.
   * @param acta acta a comprobar.
   * @return indicador de si el acta se encuentra finalizada.
   */
  isFinalizada(acta: IActaWithNumEvaluaciones): boolean {
    return acta.estadoActa?.id === 2;
  }

  /**
   * Comprueba si una acta puede ser finalizado.
   * @param acta acta a comprobar.
   * @return indicador de si se puede finalizar el acta.
   */
  hasFinalizarActa(acta: IActaWithNumEvaluaciones): boolean {
    return acta.estadoActa.id === 1 && acta.numEvaluacionesNoEvaluadas === 0;
  }

  /**
   * Visualiza el informe seleccionado.
   * @param documentoRef Referencia del informe..
   */
  visualizarInforme(acta: IActaWithNumEvaluaciones): void {
    const documento: IDocumento = {} as IDocumento;
    if (this.isFinalizada(acta)) {
      this.documentoService.getInfoFichero(acta.documentoRef).pipe(
        switchMap((documentoInfo: IDocumento) => {
          documento.nombre = documentoInfo.nombre;
          return this.documentoService.downloadFichero(acta.documentoRef);
        })
      ).subscribe(response => {
        triggerDownloadToUser(response, documento.nombre);
      });
    } else {
      this.actasService.getDocumentoActa(acta.id).pipe(
        switchMap((documentoInfo: IDocumento) => {
          documento.nombre = documentoInfo.nombre;
          return this.documentoService.downloadFichero(documentoInfo.documentoRef);
        })
      ).subscribe(
        (response) => {
          triggerDownloadToUser(response, documento.nombre);
        },
        this.processError
      );
    }
  }

  openExportModal(): void {
    const data: IActaListadoModalData = {
      findOptions: this.findOptions
    };

    const config = {
      data
    };
    this.matDialog.open(ActaListadoExportModalComponent, config);
  }

  /**
   * Confirma el registro blockchain
   * @param actaId id del acta a confirmar.
   */
  confirmarRegistroBlockchain(actaId: number) {
    this.suscripciones.push(
      this.actasService.isRegistroBlockchainConfirmado(actaId).subscribe(
        (value) => {
          if (value) {
            this.snackBarService.showSuccess(MSG_REGISTRO_BLOCKCHAIN_OK);
          } else {
            this.snackBarService.showSuccess(MSG_REGISTRO_BLOCKCHAIN_ALTERADO);
          }
        },
        this.processError
      )
    );
  }

}
