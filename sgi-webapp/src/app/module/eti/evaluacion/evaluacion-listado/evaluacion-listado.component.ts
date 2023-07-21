import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { TIPO_CONVOCATORIA_REUNION } from '@core/models/eti/tipo-convocatoria-reunion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { TipoComentario } from '../evaluacion-listado-export.service';
import { EvaluacionListadoExportModalComponent, IEvaluacionListadoModalData } from '../modals/evaluacion-listado-export-modal/evaluacion-listado-export-modal.component';

const EVALUACION_KEY = marker('eti.evaluacion');
const MSG_SUCCESS_ENVIADO = marker('msg.envio-comunicado.entity.success');
const MSG_ERROR_ENVIADO = marker('msg.envio-comunicado.entity.error');

@Component({
  selector: 'sgi-evaluacion-listado',
  templateUrl: './evaluacion-listado.component.html',
  styleUrls: ['./evaluacion-listado.component.scss']
})
export class EvaluacionListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  totalElementos: number;

  textoEnviadoSuccess: string;
  textoEnviadoError: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  evaluaciones$: Observable<IEvaluacion[]> = of();

  buscadorFormGrou: FormGroup;

  private limiteRegistrosExportacionExcel: string;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_ETICA;
  }

  get TIPO_CONVOCATORIA() {
    return TIPO_CONVOCATORIA_REUNION;
  }

  constructor(
    private readonly evaluacionesService: EvaluacionService,
    protected readonly snackBarService: SnackBarService,
    protected readonly personaService: PersonaService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService
  ) {
    super();

    this.totalElementos = 0;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(13%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.suscripciones = [];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.formGroup = new FormGroup({
      comite: new FormControl(null, []),
      fechaEvaluacionInicio: new FormControl(null, []),
      fechaEvaluacionFin: new FormControl(null, []),
      referenciaMemoria: new FormControl('', []),
      tipoConvocatoriaReunion: new FormControl(null, []),
      solicitante: new FormControl('', []),
      tipoEvaluacion: new FormControl(null, [])
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-evaluacion-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private setupI18N(): void {
    this.translate.get(
      EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_ENVIADO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEnviadoSuccess = value);

    this.translate.get(
      EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_ENVIADO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEnviadoError = value);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluacion>> {
    return this.evaluacionesService.findAllByMemoriaAndRetrospectivaEnEvaluacion(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.displayedColumns = ['memoria.comite.comite', 'tipoEvaluacion', 'memoria.tipoMemoria.nombre', 'fechaDictamen', 'memoria.numReferencia', 'solicitante',
      'dictamen.nombre', 'version', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('memoria.comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('tipoEvaluacion.id', SgiRestFilterOperator.EQUALS, controls.tipoEvaluacion.value?.id?.toString())
      .and('fechaDictamen', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value))
      .and('fechaDictamen', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionFin.value))
      .and('memoria.numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.referenciaMemoria.value)
      .and(
        'convocatoriaReunion.tipoConvocatoriaReunion.id',
        SgiRestFilterOperator.EQUALS,
        controls.tipoConvocatoriaReunion.value?.id?.toString()
      ).and('memoria.peticionEvaluacion.personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id);

    return filter;
  }

  protected loadTable(reset?: boolean) {
    this.evaluaciones$ = this.getObservableLoadTable(reset).pipe(
      switchMap((evaluaciones) => {
        return from(evaluaciones).pipe(
          mergeMap(evaluacion => {
            const personaId = evaluacion.memoria?.peticionEvaluacion?.solicitante?.id;
            if (personaId) {
              return this.personaService.findById(personaId).pipe(
                map(persona => {
                  evaluacion.memoria.peticionEvaluacion.solicitante = persona;
                  return evaluacion;
                })
              );
            }
            return of(evaluacion);
          }),
          map(() => evaluaciones)
        );
      })
    );
  }

  /**
   * Clean filters an reload the table
   */
  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  public openExportModal() {
    const data: IEvaluacionListadoModalData = {
      findOptions: this.findOptions,
      tipoComentario: TipoComentario.GESTOR,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(EvaluacionListadoExportModalComponent, config);
  }

  /**
   * Notificar de cambios en la memoria realizados
   * @param evaluacionId id de la evaluación modificada que se quiere notificar.
   * @param event evento lanzado.
   */
  notificarCambiosMemoria(evaluacionId: number, $event: Event): void {
    this.evaluacionesService.enviarComunicado(evaluacionId).subscribe(
      (response) => {
        if (response) {
          this.snackBarService.showSuccess(this.textoEnviadoSuccess);
          this.loadTable();
        } else {
          this.processError(new SgiError(this.textoEnviadoError));
        }
      },
      (error) => {
        if (error instanceof SgiError) {
          this.processError(error);
        }
        else {
          this.processError(new SgiError(this.textoEnviadoError));
        }
      }
    );
  }

}
