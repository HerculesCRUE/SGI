import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IConfiguracion } from '@core/models/eti/configuracion';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfiguracionService } from '@core/services/eti/configuracion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { TipoComentario } from '../../evaluacion/evaluacion-listado-export.service';
import { EvaluacionListadoExportModalComponent, IEvaluacionListadoModalData } from '../../evaluacion/modals/evaluacion-listado-export-modal/evaluacion-listado-export-modal.component';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-evaluacion-evaluador-listado',
  templateUrl: './evaluacion-evaluador-listado.component.html',
  styleUrls: ['./evaluacion-evaluador-listado.component.scss']
})
export class EvaluacionEvaluadorListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {

  evaluaciones: IEvaluacion[];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  private numLimiteDiasEvaluar = null;

  constructor(
    private readonly logger: NGXLogger,
    private readonly evaluadorService: EvaluadorService,
    private readonly personaService: PersonaService,
    protected readonly snackBarService: SnackBarService,
    private readonly configuracionService: ConfiguracionService,
    private matDialog: MatDialog
  ) {
    super(snackBarService, MSG_ERROR);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    super.ngOnInit();
    this.formGroup = new FormGroup({
      comite: new FormControl(null),
      fechaEvaluacionInicio: new FormControl(null),
      fechaEvaluacionFin: new FormControl(null),
      memoriaNumReferencia: new FormControl(''),
      tipoConvocatoria: new FormControl(null),
      tipoEvaluacion: new FormControl(null)
    });
    this.loadNumDiasLimiteEvaluar();
  }

  onClearFilters(): void {
    super.onClearFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluacion>> {
    return this.evaluadorService.getEvaluaciones(this.getFindOptions(reset));
  }

  protected initColumns() {
    this.columnas = ['memoria.comite.comite', 'tipoEvaluacion', 'convocatoriaReunion.fechaEvaluacion',
      'memoria.numReferencia', 'solicitante', 'version', 'acciones'];
  }

  protected loadTable(reset?: boolean) {
    const evaluaciones$ = this.getObservableLoadTable(reset);
    this.suscripciones.push(
      evaluaciones$.subscribe(
        (evaluaciones: IEvaluacion[]) => {
          if (evaluaciones) {
            this.evaluaciones = evaluaciones;
            this.loadSolicitantes();
          } else {
            this.evaluaciones = [];
          }
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        })
    );
  }

  /**
   * Carga los datos de los solicitantes de las evaluaciones
   */
  private loadSolicitantes(): void {
    this.evaluaciones.forEach((evaluacion) => {
      const personaId = evaluacion.memoria?.peticionEvaluacion?.solicitante?.id;
      if (personaId) {
        this.suscripciones.push(
          this.personaService.findById(personaId).subscribe(
            (persona: IPersona) => {
              evaluacion.memoria.peticionEvaluacion.solicitante = persona;
            }
          )
        );
      }
    });
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('memoria.comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and(
        'convocatoriaReunion.fechaEvaluacion',
        SgiRestFilterOperator.GREATHER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value)
      ).and(
        'convocatoriaReunion.fechaEvaluacion',
        SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaEvaluacionFin.value)
      ).and('memoria.numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.memoriaNumReferencia.value)
      .and('convocatoriaReunion.tipoConvocatoriaReunion.id', SgiRestFilterOperator.EQUALS, controls.tipoConvocatoria.value?.id?.toString())
      .and('tipoEvaluacion.id', SgiRestFilterOperator.EQUALS, controls.tipoEvaluacion.value?.id?.toString());
  }

  /**
   * Comprueba si se puede evaluar la evaluación
   * @param evaluacion la evaluación a evaluar
   * @return si es posible evaluar
   */
  public isPosibleEvaluar(evaluacion: IEvaluacion): boolean {
    const fechaLimite = DateTime.now().plus({ days: Number(this.numLimiteDiasEvaluar) });

    // Solo se comprueba la fecha si el estado actual de la memoria es "En Evaluación", si no se debe permitir siempre la evaluación
    if (evaluacion.memoria.estadoActual.id !== 5 ||
      (evaluacion.memoria.estadoActual.id === 5 && fechaLimite < evaluacion.convocatoriaReunion.fechaEvaluacion)) {
      return true;
    }

    return false;
  }

  /**
   * Carga la variable de configuración diasLimiteEvaluador
   */
  private loadNumDiasLimiteEvaluar() {
    const configSusbscription = this.configuracionService.getConfiguracion().subscribe(
      (configuracion: IConfiguracion) => {
        this.numLimiteDiasEvaluar = configuracion.diasLimiteEvaluador;
      }
    );

    this.suscripciones.push(configSusbscription);
  }

  public openExportModal() {
    const data: IEvaluacionListadoModalData = {
      findOptions: this.findOptions,
      tipoComentario: TipoComentario.EVALUADOR
    };

    const config = {
      data
    };
    this.matDialog.open(EvaluacionListadoExportModalComponent, config);
  }
}
