import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IComite } from '@core/models/eti/comite';
import { IConfiguracion } from '@core/models/eti/configuracion';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ComiteService } from '@core/services/eti/comite.service';
import { ConfiguracionService } from '@core/services/eti/configuracion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { TipoConvocatoriaReunionService } from '@core/services/eti/tipo-convocatoria-reunion.service';
import { TipoEvaluacionService } from '@core/services/eti/tipo-evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

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
  comiteListado: IComite[];
  comitesFiltrados: Observable<IComite[]>;
  tipoEvaluacionListado: TipoEvaluacion[];
  tipoEvaluacionFiltrados: Observable<TipoEvaluacion[]>;
  tiposConvocatoriaReunion: TipoConvocatoriaReunion[];

  private numLimiteDiasEvaluar = null;

  constructor(
    private readonly logger: NGXLogger,
    private readonly evaluadorService: EvaluadorService,
    private readonly personaService: PersonaService,
    private readonly comiteService: ComiteService,
    private readonly tipoEvaluacionService: TipoEvaluacionService,
    private readonly tipoConvocatoriaReunionService: TipoConvocatoriaReunionService,
    protected readonly snackBarService: SnackBarService,
    private readonly configuracionService: ConfiguracionService
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
      comite: new FormControl(''),
      fechaEvaluacionInicio: new FormControl(null),
      fechaEvaluacionFin: new FormControl(null),
      memoriaNumReferencia: new FormControl(''),
      tipoConvocatoria: new FormControl(''),
      tipoEvaluacion: new FormControl('')
    });
    this.loadNumDiasLimiteEvaluar();
    this.loadComites();
    this.loadTipoEvaluacion();
    this.loadConvocatorias();
  }

  onClearFilters(): void {
    super.onClearFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  protected createObservable(): Observable<SgiRestListResult<IEvaluacion>> {
    return this.evaluadorService.getEvaluaciones(this.getFindOptions());
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
    this.evaluaciones.map((evaluacion) => {
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

  /**
   * Carga todas los comites existentes
   */
  private loadComites(): void {
    this.suscripciones.push(
      this.comiteService.findAll().subscribe(
        (res: SgiRestListResult<IComite>) => {
          if (res) {
            this.comiteListado = res.items;
            this.comitesFiltrados = this.formGroup.controls.comite.valueChanges
              .pipe(
                startWith(''),
                map(valor => this.filterComites(valor))
              );
          } else {
            this.comiteListado = [];
          }
        })
    );
  }

  /**
   * Carga los tipos de evaluacion: Memoria y Retrospectiva
   */
  private loadTipoEvaluacion(): void {
    this.suscripciones.push(
      this.tipoEvaluacionService.findTipoEvaluacionMemoriaRetrospectiva().subscribe(
        (res: SgiRestListResult<TipoEvaluacion>) => {
          if (res) {
            this.tipoEvaluacionListado = res.items;
            this.tipoEvaluacionFiltrados = this.formGroup.controls.tipoEvaluacion.valueChanges
              .pipe(
                startWith(''),
                map(valor => this.filterTipoEvaluacion(valor))
              );
          } else {
            this.tipoEvaluacionListado = [];
          }
        })
    );
  }

  /**
   * Carga todas las convocatorias existentes
   */
  private loadConvocatorias() {
    this.suscripciones.push(
      this.tipoConvocatoriaReunionService.findAll().subscribe(
        (res: SgiRestListResult<TipoConvocatoriaReunion>) => {
          this.tiposConvocatoriaReunion = res.items;
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }

  /**
   * Filtro de campo autocompletable comité.
   *
   * @param filtro valor a filtrar (string o nombre comité).
   * @return lista de comités filtrados.
   */
  private filterComites(filtro: string | IComite): IComite[] {
    const valorLog = filtro instanceof String ? filtro : JSON.stringify(filtro);
    const result = this.comiteListado.filter(
      (comite: IComite) => comite.comite.toLowerCase().includes(
        typeof filtro === 'string' ? filtro.toLowerCase() : filtro.comite.toLowerCase()
      )
    );
    return result;
  }

  /**
   * Filtro de campo autocompletable tipo evaluacion.
   *
   * @param filtro valor a filtrar (string o nombre tipo evaluacion).
   * @return lista de tipo evaluacion filtrados.
   */
  private filterTipoEvaluacion(filtro: string | TipoEvaluacion): TipoEvaluacion[] {
    const valorLog = filtro instanceof String ? filtro : JSON.stringify(filtro);
    const result = this.tipoEvaluacionListado.filter(
      (tipoEvaluacion: TipoEvaluacion) => tipoEvaluacion.nombre.toLowerCase().includes(
        typeof filtro === 'string' ? filtro.toLowerCase() : filtro.nombre.toLowerCase()
      )
    );
    return result;
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comité
   * @returns nombre comité
   */
  getNombreComite(comite: IComite): string {
    return comite?.comite;
  }

  /**
   * Devuelve el nombre de un tipo evaluacion.
   * @param tipoEvaluacion tipo de evaluación
   * @returns nombre de un tipo de evaluación
   */
  getNombreTipoEvaluacion(tipoEvaluacion: TipoEvaluacion): string {
    return tipoEvaluacion?.nombre;
  }

  /**
   * Devuelve el nombre de un tipo de convocatoria.
   * @param tipoConvocatoriaReunion Tipo de convocatoria
   * @returns El nombre del tipo de convocatoria
   */
  getNombreTipoConvocatoriaReunion(tipoConvocatoriaReunion: TipoConvocatoriaReunion): string {
    return tipoConvocatoriaReunion?.nombre;
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('memoria.comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
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

    return filter;
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
}
