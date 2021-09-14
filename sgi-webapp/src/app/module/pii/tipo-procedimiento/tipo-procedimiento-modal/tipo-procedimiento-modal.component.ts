import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const TIPO_PROCEDIMIENTO_KEY = marker('pii.tipo-procedimiento');
const TIPO_PROCEDIMIENTO_NOMBRE_KEY = marker('pii.tipo-procedimiento.nombre');
const TIPO_PROCEDIMIENTO_DESCRIPCION_KEY = marker('pii.tipo-procedimiento.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-tipo-procedimiento-modal',
  templateUrl: './tipo-procedimiento-modal.component.html',
  styleUrls: ['./tipo-procedimiento-modal.component.scss']
})
export class TipoProcedimientoModalComponent
  extends BaseModalComponent<ITipoProcedimiento, TipoProcedimientoModalComponent> implements OnInit, OnDestroy {

  public fxLayoutProperties: FxLayoutProperties;
  public tipoProcedimiento: ITipoProcedimiento;
  public msgParamNombreEntity = {};
  public msgParamDescripcionEntity = {};
  public title: string;
  public textSaveOrUpdate: string;

  constructor(
    protected snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoProcedimientoModalComponent>,
    @Inject(MAT_DIALOG_DATA) tipoProcedimiento: ITipoProcedimiento,
    private translate: TranslateService) {

    super(snackBarService, matDialogRef, tipoProcedimiento);

    this.initLayoutProperties();

    this.tipoProcedimiento = tipoProcedimiento ? { ...tipoProcedimiento } : { activo: true } as ITipoProcedimiento;
  }

  private initLayoutProperties() {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
  }

  protected getDatosForm(): ITipoProcedimiento {

    return {
      ...this.tipoProcedimiento,
      nombre: this.formGroup.controls.nombre.value,
      descripcion: this.formGroup.controls.descripcion.value
    };
  }

  protected getFormGroup(): FormGroup {

    return new FormGroup({
      nombre: new FormControl(this.tipoProcedimiento?.nombre, [Validators.maxLength(50), Validators.required]),
      descripcion: new FormControl(this.tipoProcedimiento?.descripcion, [Validators.maxLength(250), Validators.required]),
    });
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
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

    if (this.tipoProcedimiento.nombre) {

      this.translate.get(
        TIPO_PROCEDIMIENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
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

      this.textSaveOrUpdate = MSG_ANADIR;
    }

  }

}