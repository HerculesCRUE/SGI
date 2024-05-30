import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { SgiError, ValidationError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, Estado, IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ErrorUtils } from '@core/utils/error-utils';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { Observable, of, throwError } from 'rxjs';
import { catchError, filter, map, switchMap } from 'rxjs/operators';

const SOLICITUD_CAMBIO_ESTADO_COMENTARIO = marker('csp.solicitud.estado-solicitud.comentario');
const SOLICITUD_CAMBIO_ESTADO_FECHA_ESTADO = marker('csp.solicitud.estado-solicitud.fecha');

const MSG_FIELD_REQUIRED = marker('error.required.field');
const MSG_DOCUMENTOS_CONVOCATORIA_REQUIRED = marker('msg.csp.solicitud.documentos-requeridos');
const SOLICITUD_PROYECTO_COORDINADO = marker('csp.solicitud-datos-proyecto-ficha-general.proyecto-coordinado');
const SOLICITUD_PROYECTO_ROL_UNIVERSIDAD = marker('csp.solicitud-datos-proyecto-ficha-general.rol-universidad');
const MSG_SOLICITUD_EQUIPO_SOLICITANTE_REQUIRED = marker('msg.csp.solicitud.solicitante-miembro-equipo');
const MSG_CAMBIO_ESTADO_CONFIRMACION = marker('confirmacion.csp.solicitud.cambio-estado');
const MSG_CAMBIO_ESTADO_ERROR = marker('msg.csp.solicitud.cambio-estado.error');

export interface SolicitudCambioEstadoModalComponentData {
  estadoActual: Estado;
  estadoNuevo: Estado;
  fechaEstado: DateTime;
  comentario: string;
  isInvestigador: boolean;
  hasRequiredDocumentos: boolean;
  solicitud: ISolicitud;
  solicitudProyecto: ISolicitudProyecto;
  isSolicitanteInSolicitudEquipo: boolean;
  isTutor: boolean;
}

export const ESTADO_MAP_INVESTIGADOR: Map<Estado, Map<Estado, string>> = new Map();
ESTADO_MAP_INVESTIGADOR.set(Estado.BORRADOR,
  new Map([[Estado.SOLICITADA, ESTADO_MAP.get(Estado.SOLICITADA)], [Estado.DESISTIDA, ESTADO_MAP.get(Estado.DESISTIDA)]]));
ESTADO_MAP_INVESTIGADOR.set(Estado.RECHAZADA,
  new Map([[Estado.SOLICITADA, ESTADO_MAP.get(Estado.SOLICITADA)], [Estado.DESISTIDA, ESTADO_MAP.get(Estado.DESISTIDA)]]));
ESTADO_MAP_INVESTIGADOR.set(Estado.SUBSANACION, new Map([
  [Estado.PRESENTADA_SUBSANACION, ESTADO_MAP.get(Estado.PRESENTADA_SUBSANACION)],
  [Estado.DESISTIDA, ESTADO_MAP.get(Estado.DESISTIDA)]
]));
ESTADO_MAP_INVESTIGADOR.set(Estado.EXCLUIDA_PROVISIONAL, new Map([
  [Estado.ALEGACION_FASE_ADMISION, ESTADO_MAP.get(Estado.ALEGACION_FASE_ADMISION)],
  [Estado.DESISTIDA, ESTADO_MAP.get(Estado.DESISTIDA)]
]));
ESTADO_MAP_INVESTIGADOR.set(Estado.EXCLUIDA_DEFINITIVA, new Map([
  [Estado.RECURSO_FASE_ADMISION, ESTADO_MAP.get(Estado.RECURSO_FASE_ADMISION)],
  [Estado.DESISTIDA, ESTADO_MAP.get(Estado.DESISTIDA)]
]));
ESTADO_MAP_INVESTIGADOR.set(Estado.DENEGADA_PROVISIONAL, new Map([
  [Estado.ALEGACION_FASE_PROVISIONAL, ESTADO_MAP.get(Estado.ALEGACION_FASE_PROVISIONAL)],
  [Estado.DESISTIDA, ESTADO_MAP.get(Estado.DESISTIDA)]
]));
ESTADO_MAP_INVESTIGADOR.set(Estado.DENEGADA, new Map([
  [Estado.RECURSO_FASE_CONCESION, ESTADO_MAP.get(Estado.RECURSO_FASE_CONCESION)],
  [Estado.DESISTIDA, ESTADO_MAP.get(Estado.DESISTIDA)]
]));

export const ESTADO_MAP_TUTOR: Map<Estado, Map<Estado, string>> = new Map();
ESTADO_MAP_TUTOR.set(Estado.SOLICITADA,
  new Map([[Estado.RECHAZADA, ESTADO_MAP.get(Estado.RECHAZADA)], [Estado.VALIDADA, ESTADO_MAP.get(Estado.VALIDADA)]]));

@Component({
  selector: 'sgi-cambio-estado-modal',
  templateUrl: './cambio-estado-modal.component.html',
  styleUrls: ['./cambio-estado-modal.component.scss']
})
export class CambioEstadoModalComponent extends DialogActionComponent<IEstadoSolicitud> implements OnInit {

  msgParamComentarioEntity = {};
  msgParamFechaEstadoEntity = {};
  msgDocumentosConvocatoriaRequired: string;
  msgSolicitudProyectoCoordinadoRequired: string;
  msgSolicitudProyectoRolUniversidadRequired: string;
  msgSolicitudEquipoSolicitanteRequired: string;
  msgCambioEstadoError: string;

  readonly estadosNuevos: Map<string, string>;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<CambioEstadoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudCambioEstadoModalComponentData,
    protected snackBarService: SnackBarService,
    private solicitudService: SolicitudService,
    private confirmDialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, true);

    if (this.data?.isTutor) {
      this.estadosNuevos = ESTADO_MAP_TUTOR.get(this.data.estadoActual);
    } else if (this.data?.isInvestigador) {
      this.estadosNuevos = ESTADO_MAP_INVESTIGADOR.get(this.data.estadoActual);
    } else {
      const estados = new Map<string, string>();
      ESTADO_MAP.forEach((value, key) => {
        if (key !== this.data.estadoActual) {
          estados.set(key, value);
        }
      });
      this.estadosNuevos = estados;

    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_CAMBIO_ESTADO_COMENTARIO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SOLICITUD_CAMBIO_ESTADO_FECHA_ESTADO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaEstadoEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.FEMALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.msgDocumentosConvocatoriaRequired = this.translate.instant(MSG_DOCUMENTOS_CONVOCATORIA_REQUIRED);
    this.msgSolicitudProyectoCoordinadoRequired = this.translate.instant(
      MSG_FIELD_REQUIRED,
      { field: this.translate.instant(SOLICITUD_PROYECTO_COORDINADO) }
    );
    this.msgSolicitudProyectoRolUniversidadRequired = this.translate.instant(
      MSG_FIELD_REQUIRED,
      { field: this.translate.instant(SOLICITUD_PROYECTO_ROL_UNIVERSIDAD) }
    );
    this.msgSolicitudEquipoSolicitanteRequired = this.translate.instant(MSG_SOLICITUD_EQUIPO_SOLICITANTE_REQUIRED);
    this.msgCambioEstadoError = this.translate.instant(MSG_CAMBIO_ESTADO_ERROR);
  }

  protected getValue(): IEstadoSolicitud {
    return {
      id: undefined,
      solicitudId: this.data.solicitud.id,
      estado: this.formGroup.controls.estadoNuevo.value,
      fechaEstado: this.formGroup.controls.fechaEstado.value,
      comentario: this.formGroup.controls.comentario.value
    };
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      estadoActual: new FormControl({ value: this.data.estadoActual, disabled: true }),
      estadoNuevo: new FormControl(this.data.estadoNuevo, Validators.required),
      fechaEstado: new FormControl({ value: DateTime.now(), disabled: this.data.isInvestigador }, Validators.required),
      comentario: new FormControl('', [Validators.maxLength(2000)])
    });
  }

  protected saveOrUpdate(): Observable<IEstadoSolicitud> {
    const estadoNew = this.getValue();

    return this.confirmDialogService.showConfirmation(MSG_CAMBIO_ESTADO_CONFIRMACION).pipe(
      filter(aceptado => !!aceptado),
      switchMap(() => this.validateCambioEstado(estadoNew.estado)),
      switchMap(() => this.solicitudService.cambiarEstado(this.data.solicitud.id, estadoNew)),
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

    if (![Estado.DESISTIDA, Estado.RENUNCIADA].includes(estado)) {
      validationErrors.push(...this.validateRequiredDocumentos());
      if (this.data.solicitud.formularioSolicitud === FormularioSolicitud.PROYECTO) {
        validationErrors.push(...this.validateSolicitudProyecto());
      }
    }

    if (validationErrors.length > 0) {
      return throwError(ErrorUtils.toValidationProblem(this.msgCambioEstadoError, validationErrors));
    }

    return of(void 0);
  }

  private validateRequiredDocumentos(): ValidationError[] {
    const problems: ValidationError[] = [];
    if (!this.data.hasRequiredDocumentos) {
      problems.push(this.buildValidationError(this.msgDocumentosConvocatoriaRequired));
    }
    return problems;
  }

  private validateSolicitudProyecto(): ValidationError[] {
    if (this.data.isInvestigador) {
      return this.validateSolicitudProyectoInvestigador();
    } else {
      return this.validateSolicitudProyectoUnidadGestion();
    }
  }

  private validateSolicitudProyectoUnidadGestion(): ValidationError[] {
    return [
      ...this.validateCoordinadoFilled(),
      ...this.validateCoordinadorExternoFilled(),
      ...this.validateSolicitanteInSolicitudEquipo()
    ];
  }

  private validateSolicitudProyectoInvestigador(): ValidationError[] {
    return [
      ...this.validateSolicitanteInSolicitudEquipo()
    ];
  }

  private validateCoordinadoFilled(): ValidationError[] {
    const problems: ValidationError[] = [];
    if (this.data.solicitudProyecto.coordinado === undefined || this.data.solicitudProyecto.coordinado === null) {
      problems.push(this.buildValidationError(this.msgSolicitudProyectoCoordinadoRequired));
    }

    return problems;
  }

  private validateCoordinadorExternoFilled(): ValidationError[] {
    const problems: ValidationError[] = [];
    if (!!this.data.solicitudProyecto.coordinado
      && (this.data.solicitudProyecto.rolUniversidad === undefined || this.data.solicitudProyecto.rolUniversidad === null)) {
      problems.push(this.buildValidationError(this.msgSolicitudProyectoRolUniversidadRequired));
    }

    return problems;
  }

  private validateSolicitanteInSolicitudEquipo(): ValidationError[] {
    const problems: ValidationError[] = [];

    if (!this.data.isSolicitanteInSolicitudEquipo) {
      problems.push(this.buildValidationError(this.msgSolicitudEquipoSolicitanteRequired));
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
