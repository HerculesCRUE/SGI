import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProcedimiento } from '@core/models/pii/procedimiento';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';

const SOLICITUD_PROTECCION_PROCEDIMIENTO_FECHA = marker('pii.solicitud-proteccion.procedimiento.fecha');
const SOLICITUD_PROTECCION_FECHA_LIMITE = marker('pii.solicitud-proteccion.procedimiento.fecha-limite');
const SOLICITUD_PROTECCION_TIPO_PROCEDIMIENTO = marker('pii.tipo-procedimiento');
const SOLICITUD_PROTECCION_ACCION = marker('pii.solicitud-proteccion.procedimiento.accion');
const SOLICITUD_PROTECCION_COMENTARIOS = marker('pii.solicitud-proteccion.procedimiento.comentarios');
const SOLICITUD_PROTECCION_LABEL_EDITAR = marker('menu.pii.solicitud-proteccion.editar-procedimiento');
const SOLICITUD_PROTECCION_LABEL_CREAR = marker('menu.pii.solicitud-proteccion.procedimiento.label.nuevo');
const MSG_ACEPTAR = marker('btn.ok');
const MSG_ADD = marker('btn.add');

export interface ISolicitudProteccionProcedimientoModalData {
  procedimiento: StatusWrapper<IProcedimiento>;
}

@Component({
  selector: 'sgi-solicitud-proteccion-procedimiento-modal',
  templateUrl: './solicitud-proteccion-procedimiento-modal.component.html',
  styleUrls: ['./solicitud-proteccion-procedimiento-modal.component.scss']
})
export class SolicitudProteccionProcedimientoModalComponent
  extends DialogFormComponent<ISolicitudProteccionProcedimientoModalData> implements OnInit {

  private readonly ACCIONES_A_TOMAR_MAX_LENGTH = 500;
  private readonly COMENTARIOS_MAX_LENGTH = 2000;

  msgParamFechaEntity = {};
  msgParamTipoProcedimientoEntity = {};
  msgParamAccionATomarEntity = {};
  msgParamComentariosEntity = {};
  msgParamFechaLimiteAccionEntity = {};
  textSaveOrUpdate: string;
  textModalTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudProteccionProcedimientoModalComponent>,
    @Inject(MAT_DIALOG_DATA) private data: ISolicitudProteccionProcedimientoModalData,
    private readonly translate: TranslateService) {
    super(matDialogRef, !data.procedimiento.created);
  }

  protected getValue(): ISolicitudProteccionProcedimientoModalData {

    if (this.formGroup.touched) {
      this.data.procedimiento.value.accionATomar = this.formGroup.controls.accionATomar.value;
      this.data.procedimiento.value.comentarios = this.formGroup.controls.comentarios.value;
      this.data.procedimiento.value.fecha = this.formGroup.controls.fecha.value;
      this.data.procedimiento.value.fechaLimiteAccion = this.formGroup.controls.fechaLimiteAccion.value;
      this.data.procedimiento.value.generarAviso = this.formGroup.controls.generarAviso.value;
      this.data.procedimiento.value.tipoProcedimiento = this.formGroup.controls.tipoProcedimiento.value;
      if (!this.data.procedimiento.created) {
        this.data.procedimiento.setEdited();
      }
    }
    const result: ISolicitudProteccionProcedimientoModalData = {
      procedimiento: this.data.procedimiento
    };

    return result;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        accionATomar: new FormControl(this.data?.procedimiento.value.accionATomar, [
          Validators.maxLength(this.ACCIONES_A_TOMAR_MAX_LENGTH)
        ]),
        comentarios: new FormControl(this.data?.procedimiento.value.comentarios, [
          Validators.maxLength(this.COMENTARIOS_MAX_LENGTH)
        ]),
        fecha: new FormControl(this.data?.procedimiento.value.fecha, [
          Validators.required,
        ]),
        fechaLimiteAccion: new FormControl(this.data?.procedimiento.value.fechaLimiteAccion, []),
        generarAviso: new FormControl(this.data?.procedimiento.value.generarAviso, []),
        tipoProcedimiento: new FormControl(this.data?.procedimiento.value.tipoProcedimiento, [
          Validators.required,
        ]),
      },
    );

    this.subscriptions.push(
      this.onGenerarAvisoValueChangeSubscription(formGroup)
    );
    this.subscriptions.push(
      this.onFechaValueChangeSubscription(formGroup)
    );

    return formGroup;
  }

  private onFechaValueChangeSubscription(formGroup: FormGroup) {
    return formGroup.controls.fecha.valueChanges.subscribe((date: DateTime) => {
      const now = DateTime.local(DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0, 0);

      if (date && date < now) {
        formGroup.controls.generarAviso.setValue(false);
        formGroup.controls.generarAviso.disable({ onlySelf: true });
      } else {
        formGroup.controls.generarAviso.enable({ onlySelf: true });
      }
    });
  }

  private onGenerarAvisoValueChangeSubscription(formGroup: FormGroup) {
    return formGroup.controls.generarAviso.valueChanges.subscribe((value: boolean) => {
      if (value) {
        formGroup.controls.fechaLimiteAccion.setValidators([Validators.required]);
        formGroup.controls.accionATomar.setValidators([Validators.required, Validators.maxLength(this.ACCIONES_A_TOMAR_MAX_LENGTH)]);
      } else {
        formGroup.controls.fechaLimiteAccion.setValidators([]);
        formGroup.controls.accionATomar.setValidators([Validators.maxLength(this.ACCIONES_A_TOMAR_MAX_LENGTH)]);
      }
      formGroup.controls.fechaLimiteAccion.updateValueAndValidity({ onlySelf: true });
      formGroup.controls.accionATomar.updateValueAndValidity({ onlySelf: true });
    });
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.textSaveOrUpdate = this.isEditionMode() ? MSG_ACEPTAR : MSG_ADD;
    this.textModalTitle = this.isEditionMode() ? SOLICITUD_PROTECCION_LABEL_EDITAR : SOLICITUD_PROTECCION_LABEL_CREAR;

    this.translate.get(
      SOLICITUD_PROTECCION_PROCEDIMIENTO_FECHA, {
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    }
    ).subscribe((value) =>
      this.msgParamFechaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
    this.translate.get(
      SOLICITUD_PROTECCION_TIPO_PROCEDIMIENTO, MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTipoProcedimientoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
    this.translate.get(
      SOLICITUD_PROTECCION_ACCION,
      { ...MSG_PARAMS.CARDINALIRY.PLURAL }
    ).subscribe((value) =>
      this.msgParamAccionATomarEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });
    this.translate.get(
      SOLICITUD_PROTECCION_COMENTARIOS
    ).subscribe((value) =>
      this.msgParamComentariosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      SOLICITUD_PROTECCION_FECHA_LIMITE,
      { ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    ).subscribe((value: any) =>
      this.msgParamFechaLimiteAccionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  public isEditionMode() {
    return !this.data.procedimiento.created;
  }

}
