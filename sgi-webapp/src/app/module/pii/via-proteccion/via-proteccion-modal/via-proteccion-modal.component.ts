import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { TipoPropiedad, TIPO_PROPIEDAD_MAP } from '@core/enums/tipo-propiedad';
import { MSG_PARAMS } from '@core/i18n';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { ViaProteccionService } from '@core/services/pii/via-proteccion/via-proteccion.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const VIA_PROTECCION_KEY = marker('pii.via-proteccion');
const VIA_PROTECCION_NOMBRE_KEY = marker('pii.via-proteccion.nombre');
const VIA_PROTECCION_DESCRIPCION_KEY = marker('pii.via-proteccion.descripcion');
const VIA_PROTECCION_TIPO_PROPIEDAD_KEY = marker('pii.via-proteccion.tipo-propiedad');
const VIA_PROTECCION_MESES_PRIORIDAD_KEY = marker('pii.via-proteccion.meses-prioridad');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-via-proteccion-modal',
  templateUrl: './via-proteccion-modal.component.html',
  styleUrls: ['./via-proteccion-modal.component.scss']
})
export class ViaProteccionModalComponent extends DialogActionComponent<IViaProteccion> implements OnInit, OnDestroy {

  private readonly viaProteccion: IViaProteccion;
  public msgParamNombreEntity = {};
  public msgParamDescripcionEntity = {};
  public msgParamTipoPropiedadEntity = {};
  public msgParamMesesPrioridadEntity = {};

  public title: string;

  get TIPO_PROPIEDAD_MAP() {
    return TIPO_PROPIEDAD_MAP;
  }

  public isMesesPrioridadNeeded = new BehaviorSubject<boolean>(false);
  private lastTipoPrioridad: TipoPropiedad;

  constructor(
    matDialogRef: MatDialogRef<ViaProteccionModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: IViaProteccion,
    private readonly viaProteccionService: ViaProteccionService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    this.viaProteccion = this.isEdit() ? { ...data } : { activo: true } as IViaProteccion;
  }

  protected getValue(): IViaProteccion {
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

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      nombre: new FormControl(this.viaProteccion.nombre ?? '', [Validators.maxLength(50), Validators.required]),
      descripcion: new FormControl(this.viaProteccion.descripcion ?? '', [Validators.maxLength(250), Validators.required]),
      tipoPropiedad: new FormControl(this.viaProteccion.tipoPropiedad ?? null, [Validators.required]),
      mesesPrioridad: new FormControl(this.viaProteccion.mesesPrioridad ?? 1, [Validators.required]),
      paisEspecifico: new FormControl(this.viaProteccion.paisEspecifico ?? false, []),
      extensionInternacional: new FormControl(this.viaProteccion.extensionInternacional ?? false, []),
      variosPaises: new FormControl(this.viaProteccion.variosPaises ?? false, [])
    });

    return form;
  }

  protected saveOrUpdate(): Observable<IViaProteccion> {
    const viaProteccion = this.getValue();
    return this.isEdit() ? this.viaProteccionService.update(viaProteccion.id, viaProteccion) :
      this.viaProteccionService.create(viaProteccion);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    if (this.viaProteccion.tipoPropiedad === TipoPropiedad.INDUSTRIAL) {
      this.isMesesPrioridadNeeded.next(true);
    }

    this.subscribeOnFormGroupChanges();
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

    if (this.isEdit()) {
      this.translate.get(
        VIA_PROTECCION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        VIA_PROTECCION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
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
      this.formGroup.controls?.mesesPrioridad.setValue(1);
      this.formGroup.controls?.mesesPrioridad.clearValidators();
    }
    this.formGroup.controls?.mesesPrioridad.updateValueAndValidity();
  }

  private subscribeOnFormGroupChanges(): void {
    this.subscriptions.push(
      this.formGroup.valueChanges
        .subscribe((values: IViaProteccion) => this.showMesesPrioridad(values.tipoPropiedad))
    );
  }
}
