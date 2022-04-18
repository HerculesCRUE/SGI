import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { TipoProcedimientoService } from '@core/services/pii/tipo-procedimiento/tipo-procedimiento.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_PROCEDIMIENTO_KEY = marker('pii.tipo-procedimiento');
const TIPO_PROCEDIMIENTO_NOMBRE_KEY = marker('pii.tipo-procedimiento.nombre');
const TIPO_PROCEDIMIENTO_DESCRIPCION_KEY = marker('pii.tipo-procedimiento.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-procedimiento-modal',
  templateUrl: './tipo-procedimiento-modal.component.html',
  styleUrls: ['./tipo-procedimiento-modal.component.scss']
})
export class TipoProcedimientoModalComponent
  extends DialogActionComponent<ITipoProcedimiento> implements OnInit, OnDestroy {

  private readonly tipoProcedimiento: ITipoProcedimiento;
  public msgParamNombreEntity = {};
  public msgParamDescripcionEntity = {};
  public title: string;

  constructor(
    matDialogRef: MatDialogRef<TipoProcedimientoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoProcedimiento,
    private readonly tipoProcedimientoService: TipoProcedimientoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    this.tipoProcedimiento = this.isEdit() ? { ...data } : { activo: true } as ITipoProcedimiento;
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_PROCEDIMIENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TIPO_PROCEDIMIENTO_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TIPO_PROCEDIMIENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_PROCEDIMIENTO_KEY,
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

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getValue(): ITipoProcedimiento {
    return {
      ...this.tipoProcedimiento,
      nombre: this.formGroup.controls.nombre.value,
      descripcion: this.formGroup.controls.descripcion.value
    };
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.tipoProcedimiento?.nombre ?? '', [Validators.required, Validators.maxLength(50)]),
      descripcion: new FormControl(this.tipoProcedimiento?.descripcion ?? '', [Validators.required, Validators.maxLength(250)]),
    });
  }

  protected saveOrUpdate(): Observable<ITipoProcedimiento> {
    const tipoProcedimiento = this.getValue();
    return this.isEdit() ? this.tipoProcedimientoService.update(tipoProcedimiento.id, tipoProcedimiento) :
      this.tipoProcedimientoService.create(tipoProcedimiento);
  }

}
