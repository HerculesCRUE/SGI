import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IConfiguracion } from '@core/models/eti/configuracion';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { MEMORIA_TIPO_MAP } from '@core/models/eti/memoria';
import { TIPO_CONVOCATORIA_REUNION } from '@core/models/eti/tipo-convocatoria-reunion';
import { TIPO_EVALUACION_MAP } from '@core/models/eti/tipo-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { ConfiguracionService } from '@core/services/eti/configuracion.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { TipoComentario } from '../../evaluacion/evaluacion-listado-export.service';
import { EvaluacionListadoExportModalComponent, IEvaluacionListadoModalData } from '../../evaluacion/modals/evaluacion-listado-export-modal/evaluacion-listado-export-modal.component';

export interface IEvaluacionWithComentariosEnviados extends IEvaluacion {
  enviada: boolean;
  permitirEnviarComentarios: boolean;
}

const MSG_ENVIAR_COMENTARIO = marker('msg.enviar.comentario');
const MSG_ENVIAR_COMENTARIO_SUCCESS = marker('msg.enviar.comentario.success');

@Component({
  selector: 'sgi-evaluacion-evaluador-listado',
  templateUrl: './evaluacion-evaluador-listado.component.html',
  styleUrls: ['./evaluacion-evaluador-listado.component.scss']
})
export class EvaluacionEvaluadorListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {

  evaluaciones: IEvaluacionWithComentariosEnviados[];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  private numLimiteDiasEvaluar = null;

  private limiteRegistrosExportacionExcel: string;

  private textoEnviarComentario: string;
  private textoEnviarComentarioSuccess: string;

  private usuarioRef: string;

  get TIPO_CONVOCATORIA() {
    return TIPO_CONVOCATORIA_REUNION;
  }

  get TIPO_EVALUACION_MAP() {
    return TIPO_EVALUACION_MAP;
  }

  get MEMORIA_TIPO_MAP() {
    return MEMORIA_TIPO_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly evaluadorService: EvaluadorService,
    private readonly personaService: PersonaService,
    private readonly configuracionService: ConfiguracionService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService,
    private readonly evaluacionService: EvaluacionService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService
  ) {
    super(translate);

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

    this.usuarioRef = this.authService.authStatus$.value.userRefId;

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-evaluador-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  protected setupI18N(): void {
    this.translate.get(
      MSG_ENVIAR_COMENTARIO
    ).subscribe((value) => this.textoEnviarComentario = value);

    this.translate.get(
      MSG_ENVIAR_COMENTARIO_SUCCESS
    ).subscribe((value) => this.textoEnviarComentarioSuccess = value);
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluacion>> {
    return this.evaluadorService.getEvaluaciones(this.getFindOptions(reset));
  }

  protected initColumns() {
    this.columnas = ['memoria.comite.codigo', 'tipoEvaluacion', 'memoria.tipo',
      'memoria.numReferencia', 'version', 'solicitante', 'convocatoriaReunion.fechaEvaluacion', 'enviada', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.suscripciones.push(
      this.getObservableLoadTable(reset).pipe(
        map((evaluaciones: IEvaluacion[]) =>
          this.sortByIsEvaluador1orEvaluador2(evaluaciones)
        ),
        switchMap((evaluacionesSorted: IEvaluacionWithComentariosEnviados[]) =>
          this.loadSolicitantes(evaluacionesSorted).pipe(
            switchMap(updatedSolicitantes =>
              this.loadEvaluacionWithComentariosEnviados(updatedSolicitantes).pipe(
                switchMap(updatedComentarios =>
                  this.loadExistsEvaluacionWithComentarioAbiertos(updatedComentarios)
                )
              )
            )
          )
        ),
        tap((evaluacionesCompletas: IEvaluacionWithComentariosEnviados[]) => {
          this.evaluaciones = evaluacionesCompletas;
        })
      ).subscribe({
        next: () => { },
        error: (error: any) => this.processError(error)
      })
    );
  }

  private sortByIsEvaluador1orEvaluador2(evaluaciones: IEvaluacion[]): IEvaluacion[] {
    return evaluaciones.sort((a, b) => {
      if ((a.evaluador1.persona.id !== this.usuarioRef && b.evaluador1.persona.id === this.usuarioRef)
        || (a.evaluador2.persona.id !== this.usuarioRef && b.evaluador2.persona.id === this.usuarioRef)) {
        return 1;
      }
      if ((a.evaluador1.persona.id === this.usuarioRef && b.evaluador1.persona.id !== this.usuarioRef)
        || (a.evaluador2.persona.id === this.usuarioRef && b.evaluador2.persona.id !== this.usuarioRef)) {
        return -1;
      }
      return 0;
    });
  }

  /**
   * Carga los datos de los solicitantes de las evaluaciones
   */
  private loadSolicitantes(evaluaciones: IEvaluacionWithComentariosEnviados[]): Observable<IEvaluacionWithComentariosEnviados[]> {
    return forkJoin(evaluaciones.map((evaluacion) => {
      const personaRef = evaluacion.memoria?.peticionEvaluacion?.solicitante?.id;
      if (personaRef) {
        return this.personaService.findById(personaRef).pipe(
          map((persona: IPersona) => {
            evaluacion.memoria!.peticionEvaluacion!.solicitante = persona;
            return evaluacion
          }),
          catchError((error) => {
            this.logger.error(error);
            return of(evaluacion);
          })
        );
      }
      return of(evaluacion);
    }));
  }

  private loadEvaluacionWithComentariosEnviados(evaluaciones: IEvaluacionWithComentariosEnviados[]): Observable<IEvaluacionWithComentariosEnviados[]> {
    return forkJoin(evaluaciones.map((evaluacion) =>
      this.evaluacionService.isComentariosEvaluadorEnviados(evaluacion.id).pipe(
        map((res: boolean) => {
          evaluacion.enviada = res;
          return evaluacion;
        }),
        catchError((error) => {
          this.processError(error);
          return of(evaluacion);
        }),
        map(() => evaluacion)
      )
    ));
  }

  private loadExistsEvaluacionWithComentarioAbiertos(evaluaciones: IEvaluacionWithComentariosEnviados[]): Observable<IEvaluacionWithComentariosEnviados[]> {
    return forkJoin(evaluaciones.map((evaluacion) =>
      this.evaluacionService.isPosibleEnviarComentarios(evaluacion.id).pipe(
        map((res: boolean) => {
          evaluacion.permitirEnviarComentarios = res;
          return evaluacion;
        }),
        catchError((error) => {
          this.processError(error);
          return of(evaluacion);
        }),
        map(() => evaluacion)
      )
    ));
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
      tipoComentario: TipoComentario.EVALUADOR,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(EvaluacionListadoExportModalComponent, config);
  }

  public enviarComentarios(idEvaluacion: number) {
    const enviarComentariosDialogSubscription = this.dialogService.showConfirmation(this.textoEnviarComentario).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          const enviarComentariosSubscription = this.evaluacionService
            .enviarComentarios(idEvaluacion)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(this.textoEnviarComentarioSuccess);
            });
          this.suscripciones.push(enviarComentariosSubscription);
        }
        aceptado = false;
      });
    this.suscripciones.push(enviarComentariosDialogSubscription);
  }
}
