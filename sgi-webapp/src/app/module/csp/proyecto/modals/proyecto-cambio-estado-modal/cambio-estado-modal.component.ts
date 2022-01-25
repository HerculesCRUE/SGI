import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { Problem, ValidationHttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP, IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of, throwError } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';

const PROYECTO_CAMBIO_ESTADO_COMENTARIO = marker('csp.proyecto.estado-proyecto.comentario');
const PROYECTO_CAMBIO_ESTADO_NUEVO_ESTADO = marker('csp.proyecto.cambio-estado.nuevo');
const MSG_CAMBIO_ESTADO_CONFIRMACION = marker('confirmacion.csp.proyecto.cambio-estado');
const MSG_ENTITY_REQUIRED = marker('error.required.entity');
const MSG_FIELD_REQUIRED = marker('error.required.field');
const PROYECTO_FINALIDAD = marker('csp.proyecto.finalidad');
const PROYECTO_AMBITO_GEOGRAFICO = marker('csp.proyecto.ambito-geografico');
const PROYECTO_CONFIDENCIAL = marker('csp.proyecto.confidencial');
const PROYECTO_COORDINADO = marker('csp.proyecto.proyecto-coordinado');
const PROYECTO_COORDINADOR_EXTERNO = marker('csp.proyecto.coordinador-externo');
const PROYECTO_TIMESHEET = marker('csp.proyecto.timesheet');
const PROYECTO_PAQUETES_TRABAJO = marker('csp.proyecto.permite-paquetes-trabajo');
const PROYECTO_COSTE_HORA = marker('csp.proyecto.calculo-coste-personal');
const MSG_PROYECTO_EQUIPO_MIEMBROS = marker('msg.csp.proyecto.equipo-miembros-obligatorio');

export interface ProyectoCambioEstadoModalComponentData {
  estadoActual: Estado;
  estadoNuevo: Estado;
  comentario: string;
  proyecto: IProyecto;
  proyectoHasMiembrosEquipo: boolean;
}

@Component({
  selector: 'sgi-cambio-estado-modal',
  templateUrl: './cambio-estado-modal.component.html',
  styleUrls: ['./cambio-estado-modal.component.scss']
})
export class CambioEstadoModalComponent
  extends DialogActionComponent<ProyectoCambioEstadoModalComponentData, IEstadoProyecto> {

  fxLayoutProperties: FxLayoutProperties;

  msgParamComentarioEntity = {};
  msgParamNuevoEstadoEntity = {};
  msgProyectoFinalidadRequired: string;
  msgProyectoAmbitoGeograficoRequired: string;
  msgProyectoConfidencialRequired: string;
  msgProyectoCoordinadoRequired: string;
  msgProyectoCoordinadorExternoRequired: string;
  msgProyectoTimesheetRequired: string;
  msgProyectoPaquetesTrabajoRequired: string;
  msgProyectoCosteHoraRequired: string;
  msgProyectoMiembrosEquipoRequired: string;

  readonly estadosNuevos: Map<string, string>;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<CambioEstadoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoCambioEstadoModalComponentData,
    protected snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private confirmDialogService: DialogService,
    private proyectoService: ProyectoService) {
    super(matDialogRef, true);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    const estados = new Map<string, string>();
    ESTADO_MAP.forEach((value, key) => {
      if (key !== this.data.estadoActual) {
        estados.set(key, value);
      }
    });
    this.estadosNuevos = estados;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_CAMBIO_ESTADO_COMENTARIO
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_CAMBIO_ESTADO_NUEVO_ESTADO
    ).subscribe((value) => this.msgParamNuevoEstadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.msgProyectoFinalidadRequired = this.translate.instant(
      MSG_ENTITY_REQUIRED,
      { entity: this.translate.instant(PROYECTO_FINALIDAD), ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.msgProyectoAmbitoGeograficoRequired = this.translate.instant(
      MSG_ENTITY_REQUIRED,
      { entity: this.translate.instant(PROYECTO_AMBITO_GEOGRAFICO), ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.msgProyectoConfidencialRequired = this.translate.instant(MSG_FIELD_REQUIRED, { field: this.translate.instant(PROYECTO_CONFIDENCIAL) });
    this.msgProyectoCoordinadoRequired = this.translate.instant(MSG_FIELD_REQUIRED, { field: this.translate.instant(PROYECTO_COORDINADO) });
    this.msgProyectoCoordinadorExternoRequired = this.translate.instant(MSG_FIELD_REQUIRED, { field: this.translate.instant(PROYECTO_COORDINADOR_EXTERNO) });
    this.msgProyectoTimesheetRequired = this.translate.instant(MSG_FIELD_REQUIRED, { field: this.translate.instant(PROYECTO_TIMESHEET) });
    this.msgProyectoPaquetesTrabajoRequired = this.translate.instant(MSG_FIELD_REQUIRED, { field: this.translate.instant(PROYECTO_PAQUETES_TRABAJO) });
    this.msgProyectoCosteHoraRequired = this.translate.instant(MSG_FIELD_REQUIRED, { field: this.translate.instant(PROYECTO_COSTE_HORA) });
    this.msgProyectoMiembrosEquipoRequired = this.translate.instant(MSG_PROYECTO_EQUIPO_MIEMBROS);
  }

  protected getValue(): IEstadoProyecto {
    return {
      id: undefined,
      proyectoId: this.data.proyecto.id,
      estado: this.formGroup.controls.estadoNuevo.value,
      fechaEstado: undefined,
      comentario: this.formGroup.controls.comentario.value
    };
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      estadoActual: new FormControl({ value: this.data.estadoActual, disabled: true }),
      estadoNuevo: new FormControl(this.data.estadoNuevo, [Validators.required]),
      comentario: new FormControl('', [Validators.maxLength(2000)])
    });
  }

  protected saveOrUpdate(): Observable<IEstadoProyecto> {
    const estadoNew = this.getValue();

    return this.confirmDialogService.showConfirmation(MSG_CAMBIO_ESTADO_CONFIRMACION).pipe(
      filter(aceptado => !!aceptado),
      switchMap(() => this.validateCambioEstado(estadoNew.estado)),
      switchMap(() => this.proyectoService.cambiarEstado(this.data.proyecto.id, estadoNew)),
      map(() => estadoNew)
    );
  }

  private validateCambioEstado(estado: Estado): Observable<never | void> {
    const problems: Problem[] = [];

    if (estado === Estado.CONCEDIDO) {
      problems.push(...this.validateRequiredFields(), ...this.validateProyectoHasMiembrosEquipo());
    }

    if (problems.length > 0) {
      return throwError(problems);
    }

    return of(void 0);
  }

  private validateRequiredFields(): Problem[] {
    const problems: Problem[] = [];

    if (!this.data.proyecto.finalidad) {
      problems.push(this.buildValidationProblem(this.msgProyectoFinalidadRequired));
    }

    if (!this.data.proyecto.ambitoGeografico) {
      problems.push(this.buildValidationProblem(this.msgProyectoAmbitoGeograficoRequired));
    }

    if (this.data.proyecto.confidencial === undefined || this.data.proyecto.confidencial === null) {
      problems.push(this.buildValidationProblem(this.msgProyectoConfidencialRequired));
    }

    if (this.data.proyecto.coordinado === undefined || this.data.proyecto.coordinado === null) {
      problems.push(this.buildValidationProblem(this.msgProyectoCoordinadoRequired));
    }

    if (!!this.data.proyecto.coordinado && (this.data.proyecto.coordinadorExterno === undefined || this.data.proyecto.coordinadorExterno === null)) {
      problems.push(this.buildValidationProblem(this.msgProyectoCoordinadorExternoRequired));
    }

    if (this.data.proyecto.timesheet === undefined || this.data.proyecto.timesheet === null) {
      problems.push(this.buildValidationProblem(this.msgProyectoTimesheetRequired));
    }

    if (this.data.proyecto.permitePaquetesTrabajo === undefined || this.data.proyecto.permitePaquetesTrabajo === null) {
      problems.push(this.buildValidationProblem(this.msgProyectoPaquetesTrabajoRequired));
    }

    if (this.data.proyecto.costeHora === undefined || this.data.proyecto.costeHora === null) {
      problems.push(this.buildValidationProblem(this.msgProyectoCosteHoraRequired));
    }

    return problems;
  }

  private validateProyectoHasMiembrosEquipo(): Problem[] {
    const problems: Problem[] = [];

    if (!this.data.proyectoHasMiembrosEquipo) {
      problems.push(this.buildValidationProblem(this.msgProyectoMiembrosEquipoRequired));
    }

    return problems;
  }

  private buildValidationProblem(msgError: string): ValidationHttpProblem {
    return new ValidationHttpProblem({ title: msgError } as Problem);
  }

}
