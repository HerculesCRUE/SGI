import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TipoHitoValidator } from '@core/validators/tipo-hito-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_HITO_FECHA_KEY = marker('csp.proyecto-hito.fecha');
const PROYECTO_HITO_TIPO_KEY = marker('csp.proyecto-hito.tipo');
const PROYECTO_HITO_COMENTARIO_KEY = marker('csp.proyecto-hito.comentario');
const PROYECTO_HITO_KEY = marker('csp.proyecto-hito');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoHitosModalComponentData {
  hitos: IProyectoHito[];
  hito: IProyectoHito;
  idModeloEjecucion: number;
  readonly: boolean;
}
@Component({
  templateUrl: './proyecto-hitos-modal.component.html',
  styleUrls: ['./proyecto-hitos-modal.component.scss']
})
export class ProyectoHitosModalComponent extends DialogFormComponent<ProyectoHitosModalComponentData> implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  msgParamFechaEntity = {};
  msgParamTipoEntity = {};
  msgParamComentarioEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<ProyectoHitosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoHitosModalComponentData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.hito?.tipoHito);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    const suscription = this.formGroup.controls.tipoHito.valueChanges.subscribe((value) => this.createValidatorDate(value));
    this.subscriptions.push(suscription);

    const suscriptionFecha = this.formGroup.controls.fecha.valueChanges.subscribe(() =>
      this.createValidatorDate(this.formGroup.controls.tipoHito.value));
    this.subscriptions.push(suscriptionFecha);

    this.textSaveOrUpdate = this.data?.hito?.tipoHito ? MSG_ACEPTAR : MSG_ANADIR;
    this.subscriptions.push(this.formGroup.get('fecha').valueChanges.subscribe(() => this.validarFecha())
    );
    this.validarFecha();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_HITO_FECHA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_HITO_COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_HITO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.hito?.tipoHito) {
      this.translate.get(
        PROYECTO_HITO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_HITO_KEY,
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


  /**
   * Si la fecha actual es inferior - Checkbox disabled
   * Si la fecha actual es superior - Checkbox enable
   */
  private validarFecha() {
    if (this.formGroup.get('fecha').value <= DateTime.now()) {
      this.formGroup.get('aviso').disable();
      this.formGroup.get('aviso').setValue(false);
    } else {
      this.formGroup.get('aviso').enable();
    }
  }

  /**
   * Validacion de fechas a la hora de seleccionar
   * un tipo de hito en el modal
   * @param tipoHito proyecto tipoHito
   */
  private createValidatorDate(tipoHito: ITipoHito): void {
    let fechas: DateTime[] = [];

    const proyectoHitos = this.data.hitos.filter(hito =>
      hito.tipoHito.id === tipoHito?.id);
    fechas = proyectoHitos.map(hito => hito.fecha);

    this.formGroup.setValidators([
      TipoHitoValidator.notInDate('fecha', fechas)
    ]);
  }

  protected getValue(): ProyectoHitosModalComponentData {
    this.data.hito.comentario = this.formGroup.controls.comentario.value;
    this.data.hito.fecha = this.formGroup.controls.fecha.value;
    this.data.hito.tipoHito = this.formGroup.controls.tipoHito.value;
    this.data.hito.generaAviso = this.formGroup.controls.aviso.value ? this.formGroup.controls.aviso.value : false;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoHito: new FormControl(this.data?.hito?.tipoHito, [Validators.required, IsEntityValidator.isValid()]),
      fecha: new FormControl(this.data?.hito?.fecha, [Validators.required]),
      comentario: new FormControl(this.data?.hito?.comentario, [Validators.maxLength(250)]),
      aviso: new FormControl(this.data?.hito?.generaAviso)
    });

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

}
