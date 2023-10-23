import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IEvaluacionWithComentariosEnviados } from '../../evaluacion-evaluador/evaluacion-evaluador-listado/evaluacion-evaluador-listado.component';
import { ISeguimientoListadoModalData, SeguimientoListadoExportModalComponent } from '../modals/seguimiento-listado-export-modal/seguimiento-listado-export-modal.component';
import { RolPersona } from '../seguimiento-listado-export.service';

const MSG_ENVIAR_COMENTARIO = marker('msg.enviar.comentario');
const MSG_ENVIAR_COMENTARIO_SUCCESS = marker('msg.enviar.comentario.success');

@Component({
  selector: 'sgi-seguimiento-listado',
  templateUrl: './seguimiento-listado.component.html',
  styleUrls: ['./seguimiento-listado.component.scss']
})
export class SeguimientoListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {
  evaluaciones: IEvaluacionWithComentariosEnviados[];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  private limiteRegistrosExportacionExcel: string;

  private textoEnviarComentario: string;
  private textoEnviarComentarioSuccess: string;

  private usuarioRef: string;

  constructor(
    private readonly personaService: PersonaService,
    private readonly evaluadorService: EvaluadorService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService,
    private readonly evaluacionService: EvaluacionService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService
  ) {
    super();
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
    this.setupI18N();
    this.formGroup = new FormGroup({
      comite: new FormControl(null),
      fechaEvaluacionInicio: new FormControl(null),
      fechaEvaluacionFin: new FormControl(null),
      memoriaNumReferencia: new FormControl(''),
      tipoConvocatoria: new FormControl(''),
      tipoEvaluacion: new FormControl(null)
    });
    this.usuarioRef = this.authService.authStatus$.value.userRefId;
    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-seguimiento-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private setupI18N(): void {
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
    const observable$ = this.evaluadorService.getSeguimientos(this.getFindOptions(reset));
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['memoria.comite.comite', 'tipoEvaluacion.nombre', 'memoria.tipoMemoria.nombre',
      'memoria.numReferencia', 'version', 'solicitante', 'convocatoriaReunion.fechaEvaluacion', 'enviada', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    const evaluaciones$ = this.getObservableLoadTable(reset);
    this.suscripciones.push(
      evaluaciones$.subscribe(
        (evaluaciones) => {
          this.evaluaciones = [];
          if (evaluaciones) {
            this.evaluaciones = this.sortByIsEvaluador1orEvaluador2(evaluaciones) as IEvaluacionWithComentariosEnviados[];
            this.loadSolicitantes();
            this.loadEvaluacionWithComentariosEnviados();
            this.loadExistsEvaluacionWithComentarioAbiertos();
          }
        },
        (error) => {
          this.processError(error);
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
  private loadSolicitantes(): void {
    this.evaluaciones.forEach((evaluacion) => {
      const personaRef = evaluacion.memoria?.peticionEvaluacion?.solicitante?.id;
      if (personaRef) {
        this.suscripciones.push(
          this.personaService.findById(personaRef).subscribe(
            (persona: IPersona) => evaluacion.memoria.peticionEvaluacion.solicitante = persona,
            (error) => {
              this.processError(error);
            }
          )
        );
      }
    });
  }

  private loadEvaluacionWithComentariosEnviados(): void {
    this.evaluaciones.forEach((evaluacion) => {
      this.suscripciones.push(
        this.evaluacionService.isComentariosEvaluadorEnviados(evaluacion.id).subscribe(
          (res: boolean) => {
            evaluacion.enviada = res;
          },
          (error) => {
            this.processError(error);
          }
        )
      );
    })
  }

  private loadExistsEvaluacionWithComentarioAbiertos(): void {
    this.evaluaciones.forEach((evaluacion) => {
      this.suscripciones.push(
        this.evaluacionService.isPosibleEnviarComentarios(evaluacion.id).subscribe(
          (res: boolean) => {
            evaluacion.permitirEnviarComentarios = res;
          },
          (error) => {
            this.processError(error);
          }
        )
      );
    })
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
      .and('tipoEvaluacion.id', SgiRestFilterOperator.EQUALS, controls.tipoEvaluacion.value?.id?.toString());

    return filter;
  }

  public openExportModal() {
    const data: ISeguimientoListadoModalData = {
      findOptions: this.findOptions,
      rolPersona: RolPersona.EVALUADOR,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(SeguimientoListadoExportModalComponent, config);
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
