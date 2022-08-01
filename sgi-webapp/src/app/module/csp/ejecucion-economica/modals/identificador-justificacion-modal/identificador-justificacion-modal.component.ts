import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConfiguracion } from '@core/models/csp/configuracion';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { IProyectoPeriodoJustificacionWithTituloProyecto } from '../../ejecucion-economica-formulario/seguimiento-justificacion-resumen/seguimiento-justificacion-resumen.fragment';

const PERIODO_JUSTIFICACION_ID_JUSTIFICACION_KEY = marker('csp.identificador-justificacion.identificador-justificacion');
const PERIODO_JUSTIFICACION_FECHA_JUSTIFICACION_KEY =
  marker('csp.identificador-justificacion.fecha-presentacion-justificacion');

export interface IdentificadorJustificacionModalData {
  periodoJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>;
  configuracion: IConfiguracion;
  othersPeriodosJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[];
}

@Component({
  selector: 'sgi-identificador-justificacion-modal',
  templateUrl: './identificador-justificacion-modal.component.html',
  styleUrls: ['./identificador-justificacion-modal.component.scss']
})
export class IdentificadorJustificacionModalComponent extends
  DialogFormComponent<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>> implements OnInit {

  msgParamIdJustificacionEntity = {};
  msgParamFechaJustificacionEntity = {};

  get plantillaFormatoIdentificadorJustificacion() {
    return { mask: this.data.configuracion.plantillaFormatoIdentificadorJustificacion };
  }

  constructor(
    matDialogRef: MatDialogRef<IdentificadorJustificacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: IdentificadorJustificacionModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PERIODO_JUSTIFICACION_ID_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamIdJustificacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PERIODO_JUSTIFICACION_FECHA_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaJustificacionEntity = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup(
      {
        identificadorJustificacion: new FormControl(this.data.periodoJustificacion?.value.identificadorJustificacion, [
          Validators.required, Validators.pattern(this.data.configuracion.formatoIdentificadorJustificacion), Validators.maxLength(255),
          StringValidator.notIn(this.data.othersPeriodosJustificacion.
            map(periodoJustificacion => periodoJustificacion.value.identificadorJustificacion))
        ]),
        fechaPresentacionJustificacion: new FormControl(this.data.periodoJustificacion?.value.fechaPresentacionJustificacion, [
          Validators.required
        ]),

      }
    );
  }

  protected getValue(): StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto> {
    this.data.periodoJustificacion.value.identificadorJustificacion = this.formGroup.controls.identificadorJustificacion.value;
    this.data.periodoJustificacion.value.fechaPresentacionJustificacion = this.formGroup.controls.fechaPresentacionJustificacion.value;

    return this.data.periodoJustificacion;
  }
}
