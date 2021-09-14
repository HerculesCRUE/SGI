import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { TipoPropiedad, TIPO_PROPIEDAD_MAP } from '@core/enums/tipo-propiedad';
import { MSG_PARAMS } from '@core/i18n';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { BehaviorSubject } from 'rxjs';

const VIA_PROTECCION_KEY = marker('pii.via-proteccion');
const VIA_PROTECCION_NOMBRE_KEY = marker('pii.via-proteccion.nombre');
const VIA_PROTECCION_DESCRIPCION_KEY = marker('pii.via-proteccion.descripcion');
const VIA_PROTECCION_TIPO_PROPIEDAD_KEY = marker('pii.via-proteccion.tipo-propiedad');
const VIA_PROTECCION_MESES_PRIORIDAD_KEY = marker('pii.via-proteccion.meses-prioridad');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-via-proteccion-modal',
  templateUrl: './via-proteccion-modal.component.html',
  styleUrls: ['./via-proteccion-modal.component.scss']
})
export class ViaProteccionModalComponent
  extends BaseModalComponent<IViaProteccion, ViaProteccionModalComponent> implements OnInit, OnDestroy {

  FormGroupUtil = FormGroupUtil;
  public fxLayoutProperties: FxLayoutProperties;
  public viaProteccion: IViaProteccion;
  public msgParamNombreEntity = {};
  public msgParamDescripcionEntity = {};
  public msgParamTipoPropiedadEntity = {};
  public msgParamMesesPrioridadEntity = {};

  public title: string;
  public textSaveOrUpdate: string;

  get TIPO_PROPIEDAD_MAP() {
    return TIPO_PROPIEDAD_MAP;
  }

  public isMesesPrioridadNeeded = new BehaviorSubject<boolean>(false);
  private lastTipoPrioridad: TipoPropiedad;

  constructor(
    protected snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ViaProteccionModalComponent>,
    @Inject(MAT_DIALOG_DATA) viaProteccion: IViaProteccion,
    private translate: TranslateService) {

    super(snackBarService, matDialogRef, viaProteccion);

    this.initLayoutProperties();

    this.viaProteccion = viaProteccion ? { ...viaProteccion } : { activo: true } as IViaProteccion;
  }

  protected getDatosForm(): IViaProteccion {
    return {
      ...this.viaProteccion,
      nombre: this.formGroup.controls.nombre.value,
      descripcion: this.formGroup.controls.descripcion.value,
      tipoPropiedad: this.formGroup.controls.tipoPropiedad.value,
      mesesPrioridad: this.formGroup.controls.mesesPrioridad.value,
      paisEspecifico: this.formGroup.controls.paisEspecifico.value,
      extensionInternacional: this.formGroup.controls.extensionInternacional.value,
      variosPaises: this.formGroup.controls.variosPaises.value
    };
  }
  protected getFormGroup(): FormGroup {

    const form = new FormGroup({
      nombre: new FormControl('', [Validators.maxLength(50), Validators.required]),
      descripcion: new FormControl('', [Validators.maxLength(250), Validators.required]),
      tipoPropiedad: new FormControl('', [Validators.required]),
      mesesPrioridad: new FormControl('', [Validators.required]),
      paisEspecifico: new FormControl('', []),
      extensionInternacional: new FormControl('', []),
      variosPaises: new FormControl('', [])
    });

    form.patchValue(this.viaProteccion);

    return form;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.showMesesPrioridad(this.viaProteccion.tipoPropiedad);
    this.subscribeOnFormGroupChanges();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  private setupI18N(): void {
    this.translate.get(
      VIA_PROTECCION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      VIA_PROTECCION_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.viaProteccion.nombre) {

      this.translate.get(
        VIA_PROTECCION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        VIA_PROTECCION_KEY,
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
    this.translate.get(
      VIA_PROTECCION_TIPO_PROPIEDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTipoPropiedadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      VIA_PROTECCION_MESES_PRIORIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) =>
      this.msgParamMesesPrioridadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });
  }

  private initLayoutProperties() {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
  }

  private showMesesPrioridad(tipoPropiedad: TipoPropiedad): void {
    if (tipoPropiedad === this.lastTipoPrioridad) {
      return;
    }
    this.lastTipoPrioridad = tipoPropiedad;

    if (tipoPropiedad === TipoPropiedad.INDUSTRIAL) {
      this.isMesesPrioridadNeeded.next(true);
      this.formGroup.controls?.mesesPrioridad.setValidators([Validators.required]);
      // Establecer valor para forzar que se muestre el label con el asterisco de requerido
      this.formGroup.controls?.mesesPrioridad.setValue(1);
    } else {
      this.isMesesPrioridadNeeded.next(false);
      this.formGroup.controls?.mesesPrioridad.setValue('');
      this.formGroup.controls?.mesesPrioridad.clearValidators();
    }
    this.formGroup.controls?.mesesPrioridad.updateValueAndValidity();
  }

  private verifyTipoPropiedad(): void {
    if (this.viaProteccion.tipoPropiedad === TipoPropiedad.INDUSTRIAL) {
      this.isMesesPrioridadNeeded.next(true);

    }
  }

  private subscribeOnFormGroupChanges(): void {
    this.subscriptions.push(
      this.formGroup.valueChanges
        .subscribe((values: IViaProteccion) => this.showMesesPrioridad(values.tipoPropiedad))
    );
  }

}
