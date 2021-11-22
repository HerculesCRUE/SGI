import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProcedimiento } from '@core/models/pii/procedimiento';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';

const SOLICITUD_PROTECCION_PROCEDIMIENTO_FECHA = marker('pii.solicitud-proteccion.procedimiento.fecha');
const SOLICITUD_PROTECCION_TIPO_PROCEDIMIENTO = marker('pii.tipo-procedimiento');
const SOLICITUD_PROTECCION_ACCION = marker('pii.solicitud-proteccion.procedimiento.accion');
const SOLICITUD_PROTECCION_COMENTARIOS = marker('pii.solicitud-proteccion.procedimiento.comentarios');
const SOLICITUD_PROTECCION_LABEL_EDITAR = marker('menu.pii.solicitud-proteccion.editar-procedimiento');
const SOLICITUD_PROTECCION_LABEL_CREAR = marker('menu.pii.solicitud-proteccion.procedimiento.label.nuevo');
const MSG_ACEPTAR = marker('btn.ok');
const MSG_ADD = marker('btn.add');

export interface ISolicitudProteccionProcedimientoModalData {
  procedimiento: StatusWrapper<IProcedimiento>;
  tiposProcedimiento: ITipoProcedimiento[];
}

@Component({
  selector: 'sgi-solicitud-proteccion-procedimiento-modal',
  templateUrl: './solicitud-proteccion-procedimiento-modal.component.html',
  styleUrls: ['./solicitud-proteccion-procedimiento-modal.component.scss']
})
export class SolicitudProteccionProcedimientoModalComponent
  extends BaseModalComponent<ISolicitudProteccionProcedimientoModalData, SolicitudProteccionProcedimientoModalComponent>
  implements OnInit, OnDestroy {

  private readonly ACCIONES_A_TOMAR_MAX_LENGTH = 500;
  private readonly COMENTARIOS_MAX_LENGTH = 2000;

  msgParamFechaEntity = {};
  msgParamTipoProcedimientoEntity = {};
  msgParamAccionATomarEntity = {};
  msgParamComentariosEntity = {};
  textSaveOrUpdate: string;
  textModalTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<SolicitudProteccionProcedimientoModalComponent>,
    @Inject(MAT_DIALOG_DATA) procedimientoData: ISolicitudProteccionProcedimientoModalData,
    private readonly translate: TranslateService) {
    super(snackBarService, matDialogRef, procedimientoData);
  }

  protected getDatosForm(): ISolicitudProteccionProcedimientoModalData {

    if (this.formGroup.touched) {
      this.entity.procedimiento.value.accionATomar = this.formGroup.controls.accionATomar.value;
      this.entity.procedimiento.value.comentarios = this.formGroup.controls.comentarios.value;
      this.entity.procedimiento.value.fecha = this.formGroup.controls.fecha.value;
      this.entity.procedimiento.value.fechaLimiteAccion = this.formGroup.controls.fechaLimiteAccion.value;
      this.entity.procedimiento.value.generarAviso = this.formGroup.controls.generarAviso.value;
      this.entity.procedimiento.value.tipoProcedimiento = this.formGroup.controls.tipoProcedimiento.value;
      if (!this.entity.procedimiento.created) {
        this.entity.procedimiento.setEdited();
      }
    }
    const result: ISolicitudProteccionProcedimientoModalData = {
      procedimiento: this.entity.procedimiento, tiposProcedimiento: this.entity.tiposProcedimiento
    };

    return result;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        accionATomar: new FormControl(this.entity?.procedimiento.value.accionATomar, [
          Validators.maxLength(this.ACCIONES_A_TOMAR_MAX_LENGTH)
        ]),
        comentarios: new FormControl(this.entity?.procedimiento.value.comentarios, [
          Validators.maxLength(this.COMENTARIOS_MAX_LENGTH)
        ]),
        fecha: new FormControl(this.entity?.procedimiento.value.fecha, [
          Validators.required,
        ]),
        fechaLimiteAccion: new FormControl(this.entity?.procedimiento.value.fechaLimiteAccion, []),
        generarAviso: new FormControl(this.entity?.procedimiento.value.generarAviso, []),
        tipoProcedimiento: new FormControl(this.entity?.procedimiento.value.tipoProcedimiento, [
          Validators.required,
        ]),
      },
    );

    return formGroup;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
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
      { ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    ).subscribe((value) =>
      this.msgParamAccionATomarEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
    this.translate.get(
      SOLICITUD_PROTECCION_COMENTARIOS
    ).subscribe((value) =>
      this.msgParamComentariosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  public isEditionMode() {
    return !this.entity.procedimiento.created;
  }

}
