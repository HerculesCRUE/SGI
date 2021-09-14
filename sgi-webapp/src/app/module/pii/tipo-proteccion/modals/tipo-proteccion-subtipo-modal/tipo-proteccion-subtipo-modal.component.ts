import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';

const SUBTIPO_PROTECCION_KEY = marker('pii.subtipo-proteccion');
const SUBTIPO_PROTECCION_NOMBRE = marker('pii.tipo-proteccion.nombre');
const SUBTIPO_PROTECCION_DESCRIPCION = marker('pii.tipo-proteccion.descripcion');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-tipo-proteccion-subtipo-modal',
  templateUrl: './tipo-proteccion-subtipo-modal.component.html',
  styleUrls: ['./tipo-proteccion-subtipo-modal.component.scss']
})
export class TipoProteccionSubtipoModalComponent
  extends BaseModalComponent<StatusWrapper<ITipoProteccion>, TipoProteccionSubtipoModalComponent> implements OnInit, OnDestroy {

  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  title: string;

  textSaveOrUpdate: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoProteccionSubtipoModalComponent>,
    @Inject(MAT_DIALOG_DATA) subtipoProteccion: StatusWrapper<ITipoProteccion>,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, subtipoProteccion);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SUBTIPO_PROTECCION_NOMBRE,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SUBTIPO_PROTECCION_DESCRIPCION,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);

    this.textSaveOrUpdate = MSG_ACEPTAR;
  }

  protected getDatosForm(): StatusWrapper<ITipoProteccion> {
    if (this.formGroup.touched) {
      this.entity.value.nombre = this.formGroup.controls.nombre.value;
      this.entity.value.descripcion = this.formGroup.controls.descripcion.value;
      if (!this.entity.created) {
        this.entity.setEdited();
      }
    }

    return this.entity;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.entity?.value.nombre, [Validators.maxLength(50)]),
      descripcion: new FormControl(this.entity?.value.descripcion, [Validators.maxLength(250)]),
    });

    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

}
