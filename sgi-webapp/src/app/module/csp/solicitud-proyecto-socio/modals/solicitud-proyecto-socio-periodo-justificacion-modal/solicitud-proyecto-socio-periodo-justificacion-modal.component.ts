import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { NumberValidator } from '@core/validators/number-validator';
import { IRange, RangeValidator } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_KEY = marker('csp.solicitud-proyecto-periodo-justificacion');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_MES_FINAL_KEY = marker('csp.proyecto-socio-periodo-justificacion.mes-fin');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_MES_INICIAL_KEY = marker('csp.proyecto-socio-periodo-justificacion.mes-inicio');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_OBSERVACIONES_KEY = marker('title.csp.proyecto-socio-periodo-justificacion.observaciones');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface SolicitudProyectoSocioPeriodoJustificacionModalData {
  periodoJustificacion: ISolicitudProyectoSocioPeriodoJustificacion;
  duracion: number;
  empresa: IEmpresa;
  selectedPeriodoJustificaciones: ISolicitudProyectoSocioPeriodoJustificacion[];
  mesInicioSolicitudProyectoSocio: number;
  mesFinSolicitudProyectoSocio: number;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './solicitud-proyecto-socio-periodo-justificacion-modal.component.html',
  styleUrls: ['./solicitud-proyecto-socio-periodo-justificacion-modal.component.scss']
})
export class SolicitudProyectoSocioPeriodoJustificacionModalComponent
  extends BaseModalComponent<SolicitudProyectoSocioPeriodoJustificacionModalData, SolicitudProyectoSocioPeriodoJustificacionModalComponent>
  implements OnInit {
  textSaveOrUpdate: string;

  msgParamMesFinalEntity = {};
  msgParamMesInicialEntity = {};
  msgParamObservacionesEntity = {};

  title: string;

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudProyectoSocioPeriodoJustificacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudProyectoSocioPeriodoJustificacionModalData,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_MES_FINAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMesFinalEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_MES_INICIAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMesInicialEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    if (this.data.isEdit) {
      this.translate.get(
        PROYECTO_SOCIO_PERIODO_JUSTIFICACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_SOCIO_PERIODO_JUSTIFICACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected getFormGroup(): FormGroup {
    const rangosPeriodosExistentes = this.data.selectedPeriodoJustificaciones?.map(
      periodoJustificacion => {
        const value: IRange = {
          inicio: periodoJustificacion.mesInicial,
          fin: periodoJustificacion.mesFinal
        };
        return value;
      }
    );
    const mesInicio = this.data.mesInicioSolicitudProyectoSocio;
    const mesFinal = this.data.mesFinSolicitudProyectoSocio;
    const duracion = this.data.duracion;
    const formGroup = new FormGroup(
      {
        nombre: new FormControl({
          value: this.data.empresa?.nombre,
          disabled: true
        }),
        numPeriodo: new FormControl({
          value: this.data.periodoJustificacion.numPeriodo,
          disabled: true
        }),
        mesInicial: new FormControl(this.data.periodoJustificacion.mesInicial, [
          Validators.required,
          Validators.min(mesInicio ? mesInicio : 1),
          Validators.max(mesFinal ? mesFinal : (isNaN(duracion) ? GLOBAL_CONSTANTS.integerMaxValue : duracion)),
        ]),
        mesFinal: new FormControl(this.data.periodoJustificacion.mesFinal, [
          Validators.required,
          Validators.min(1),
          Validators.max(mesFinal ? mesFinal : (isNaN(duracion) ? GLOBAL_CONSTANTS.integerMaxValue : duracion)),
        ]),
        fechaInicio: new FormControl(this.data.periodoJustificacion.fechaInicio, []),
        fechaFin: new FormControl(this.data.periodoJustificacion.fechaFin, []),
        observaciones: new FormControl(this.data.periodoJustificacion.observaciones, [Validators.maxLength(2000)]),
      },
      {
        validators: [
          NumberValidator.isAfterOrEqual('mesInicial', 'mesFinal'),
          NumberValidator.isAfter('fechaInicio', 'fechaFin'),
          RangeValidator.notOverlaps('mesInicial', 'mesFinal', rangosPeriodosExistentes)
        ]
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }
    // Si es el primer periodo este ha de comenzar en el mes 1
    // Si no es es el primero, deberá ser siempre consecutivo al anterior periodo de justificación
    const mesInicialControl = formGroup.get('mesInicial');
    const mesFinalControl = formGroup.get('mesFinal');
    this.subscriptions.push(mesInicialControl.valueChanges.subscribe(value => {
      if (this.data.selectedPeriodoJustificaciones.length === 0
        || this.data.selectedPeriodoJustificaciones[0].mesInicial !== 1) {
        if (value && value > 1) {
          mesInicialControl.setErrors({ initial: true });
          mesInicialControl.markAsTouched({ onlySelf: true });
        } else if (mesInicialControl.errors) {
          delete mesInicialControl.errors.initial;
          mesInicialControl.updateValueAndValidity({ onlySelf: true, emitEvent: false });
        }
      } else {
        if (value && (
          (this.data.periodoJustificacion.numPeriodo > 1
            && value !== this.data.selectedPeriodoJustificaciones[
              this.data.periodoJustificacion.numPeriodo - 2].mesFinal + 1)
          || ((this.data.periodoJustificacion.numPeriodo < 1
            || this.data.periodoJustificacion.numPeriodo == null)
            && value !== this.data.selectedPeriodoJustificaciones[
              this.data.selectedPeriodoJustificaciones.length - 1].mesFinal + 1)
        )) {
          mesInicialControl.setErrors({ wrongOrder: true });
          mesInicialControl.markAsTouched({ onlySelf: true });
        } else if (mesInicialControl.errors) {
          delete mesInicialControl.errors.wrongOrder;
          mesInicialControl.updateValueAndValidity({ onlySelf: true, emitEvent: false });
        }
      }
      if (value && value > mesFinalControl.value) {
        mesFinalControl.setErrors({ afterOrEqual: true });
        mesFinalControl.markAsTouched({ onlySelf: true });
      } else if (mesFinalControl.errors) {
        delete mesFinalControl.errors.afterOrEqual;
        mesFinalControl.updateValueAndValidity({ onlySelf: true, emitEvent: false });
      }
    }));

    return formGroup;
  }

  protected getDatosForm(): SolicitudProyectoSocioPeriodoJustificacionModalData {
    this.data.periodoJustificacion.mesInicial = this.formGroup.get('mesInicial').value;
    this.data.periodoJustificacion.mesFinal = this.formGroup.get('mesFinal').value;
    this.data.periodoJustificacion.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.periodoJustificacion.fechaFin = this.formGroup.get('fechaFin').value;
    this.data.periodoJustificacion.observaciones = this.formGroup.get('observaciones').value;
    return this.data;
  }
}
