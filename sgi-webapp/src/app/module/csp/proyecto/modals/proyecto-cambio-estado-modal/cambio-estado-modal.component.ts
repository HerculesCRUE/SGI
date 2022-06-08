import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { SgiError, ValidationError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP, IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ErrorUtils } from '@core/utils/error-utils';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError, filter, map, switchMap } from 'rxjs/operators';

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
const PROYECTO_PAQUETES_TRABAJO = marker('csp.proyecto.permite-paquetes-trabajo');
const MSG_PROYECTO_EQUIPO_MIEMBROS = marker('msg.csp.proyecto.equipo-miembros-obligatorio');
const MSG_CAMBIO_ESTADO_ERROR = marker('msg.csp.proyecto.cambio-estado.error');

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
export class CambioEstadoModalComponent extends DialogActionComponent<IEstadoProyecto> implements OnInit {

  msgParamComentarioEntity = {};
  msgParamNuevoEstadoEntity = {};
  msgProyectoFinalidadRequired: string;
  msgProyectoAmbitoGeograficoRequired: string;
  msgProyectoConfidencialRequired: string;
  msgProyectoCoordinadoRequired: string;
  msgProyectoCoordinadorExternoRequired: string;
  msgProyectoPaquetesTrabajoRequired: string;
  msgProyectoMiembrosEquipoRequired: string;
  msgCambioEstadoError: string;

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
    private proyectoService: ProyectoService
  ) {
    super(matDialogRef, true);

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
    this.matDialogRef.updateSize('20vw');
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
    this.msgProyectoPaquetesTrabajoRequired = this.translate.instant(MSG_FIELD_REQUIRED, { field: this.translate.instant(PROYECTO_PAQUETES_TRABAJO) });
    this.msgProyectoMiembrosEquipoRequired = this.translate.instant(MSG_PROYECTO_EQUIPO_MIEMBROS);
    this.msgCambioEstadoError = this.translate.instant(MSG_CAMBIO_ESTADO_ERROR);
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
      catchError((error) => {
        if (error instanceof SgiError) {
          error.managed = true;
          this.snackBarService.showError(error);
        }
        return throwError(error);
      }),
      map(() => estadoNew)
    );
  }

  private validateCambioEstado(estado: Estado): Observable<never | void> {
    const validationErrors: ValidationError[] = [];

    if (estado === Estado.CONCEDIDO) {
      validationErrors.push(...this.validateRequiredFields(), ...this.validateProyectoHasMiembrosEquipo());
    }

    if (validationErrors.length > 0) {
      return throwError(ErrorUtils.toValidationProblem(this.msgCambioEstadoError, validationErrors));
    }

    return of(void 0);
  }

  private validateRequiredFields(): ValidationError[] {
    const problems: ValidationError[] = [];

    if (!this.data.proyecto.finalidad) {
      problems.push(this.buildValidationError(this.msgProyectoFinalidadRequired));
    }

    if (!this.data.proyecto.ambitoGeografico) {
      problems.push(this.buildValidationError(this.msgProyectoAmbitoGeograficoRequired));
    }

    if (this.data.proyecto.confidencial === undefined || this.data.proyecto.confidencial === null) {
      problems.push(this.buildValidationError(this.msgProyectoConfidencialRequired));
    }

    if (this.data.proyecto.coordinado === undefined || this.data.proyecto.coordinado === null) {
      problems.push(this.buildValidationError(this.msgProyectoCoordinadoRequired));
    }

    if (!!this.data.proyecto.coordinado && (this.data.proyecto.coordinadorExterno === undefined || this.data.proyecto.coordinadorExterno === null)) {
      problems.push(this.buildValidationError(this.msgProyectoCoordinadorExternoRequired));
    }

    if (this.data.proyecto.permitePaquetesTrabajo === undefined || this.data.proyecto.permitePaquetesTrabajo === null) {
      problems.push(this.buildValidationError(this.msgProyectoPaquetesTrabajoRequired));
    }

    return problems;
  }

  private validateProyectoHasMiembrosEquipo(): ValidationError[] {
    const problems: ValidationError[] = [];

    if (!this.data.proyectoHasMiembrosEquipo) {
      problems.push(this.buildValidationError(this.msgProyectoMiembrosEquipoRequired));
    }

    return problems;
  }

  private buildValidationError(msgError: string): ValidationError {
    return {
      error: msgError,
      field: undefined
    };
  }

}
