import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProteccion, TIPO_PROPIEDAD_MAP } from '@core/models/pii/tipo-proteccion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const TIPO_PROTECCION_KEY = marker('pii.tipo-proteccion');
const TIPO_PROTECCION_NOMBRE_KEY = marker('pii.tipo-proteccion.nombre');
const TIPO_PROTECCION_DESCRIPCION_KEY = marker('pii.tipo-proteccion.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-pii-tipo-proteccion-modal',
  templateUrl: './pii-tipo-proteccion-modal.component.html',
  styleUrls: ['./pii-tipo-proteccion-modal.component.scss']
})
export class PiiTipoProteccionModalComponent
  extends BaseModalComponent<ITipoProteccion, PiiTipoProteccionModalComponent> implements OnInit, OnDestroy {

  tipoProteccion: ITipoProteccion;
  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  title: string;
  textSaveOrUpdate: string;

  protected getDatosForm(): ITipoProteccion {
    this.tipoProteccion.nombre = this.formGroup.controls.nombre.value;
    this.tipoProteccion.descripcion = this.formGroup.controls.descripcion.value;
    this.tipoProteccion.tipoPropiedad = this.formGroup.controls.tipoPropiedad.value;
    return this.tipoProteccion;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoProteccion?.nombre),
      descripcion: new FormControl(this.tipoProteccion?.descripcion),
      tipoPropiedad: new FormControl(this.tipoProteccion?.tipoPropiedad),
    });
    return formGroup;
  }

  get TIPO_PROPIEDAD_MAP() {
    return TIPO_PROPIEDAD_MAP;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<PiiTipoProteccionModalComponent>,
    @Inject(MAT_DIALOG_DATA) tipoProteccion: ITipoProteccion,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, tipoProteccion);
    if (tipoProteccion) {
      this.tipoProteccion = { ...tipoProteccion };
    } else {
      this.tipoProteccion = { activo: true } as ITipoProteccion;
    }
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
      TIPO_PROTECCION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TIPO_PROTECCION_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.tipoProteccion.nombre) {

      this.translate.get(
        TIPO_PROTECCION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        TIPO_PROTECCION_KEY,
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

