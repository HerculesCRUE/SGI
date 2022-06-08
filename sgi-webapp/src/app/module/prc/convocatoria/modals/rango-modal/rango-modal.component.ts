import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRango, TipoTemporalidad, TIPO_TEMPORALIDAD_MAP } from '@core/models/prc/rango';
import { NumberValidator } from '@core/validators/number-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';
import { RangoValidator } from '../../validators/rango-validator';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TITLE_NEW_ENTITY = marker('title.new.entity');

const RANGO_TIPO_KEY = marker('prc.moduladores-rango.tipo');
const RANGO_DESDE_KEY = marker('prc.moduladores-rango.desde');
const RANGO_HASTA_KEY = marker('prc.moduladores-rango.hasta');
const RANGO_PUNTOS_KEY = marker('prc.moduladores-rango.puntos');

export interface RangoModalData {
  title: string;
  entidad: IRango;
  isEdit: boolean;
  readonly: boolean;
  hasRangoInicial: boolean;
  hasRangoFinal: boolean;
  rangoMaxHasta: number;
}

@Component({
  templateUrl: './rango-modal.component.html',
  styleUrls: ['./rango-modal.component.scss']
})
export class RangoModalComponent extends DialogFormComponent<RangoModalData> implements OnInit {

  textSaveOrUpdate: string;
  title: string;

  msgParamTipoEntity = {};
  msgParamDesdeEntity = {};
  msgParamHastaEntity = {};
  msgParamPuntosEntity = {};

  get TIPO_MAP() {
    return TIPO_TEMPORALIDAD_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<RangoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: RangoModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, data.isEdit);

    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
    this.title = this.data.title;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      RANGO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      RANGO_DESDE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDesdeEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      RANGO_HASTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamHastaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      RANGO_PUNTOS_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamPuntosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    if (!this.data.isEdit) {
      this.translate.get(
        this.title,
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

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        tipo: new FormControl(this.data.entidad.tipoTemporalidad, [
          Validators.required,
          RangoValidator.noDuplicateTipoTemporalidad(TipoTemporalidad.INICIAL, this.data.hasRangoInicial),
          RangoValidator.noDuplicateTipoTemporalidad(TipoTemporalidad.FINAL, this.data.hasRangoFinal)
        ]),
        desde: new FormControl(this.data.entidad.desde, [Validators.min(0), Validators.pattern(/^[0-9]*$/)]),
        hasta: new FormControl(this.data.entidad.hasta, [Validators.pattern(/^[0-9]*$/)]),
        puntos: new FormControl(this.data.entidad.puntos, [Validators.required]),
      },
      {
        validators: [
          NumberValidator.isAfterOptional('desde', 'hasta'),
        ]
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    } else if (this.data.isEdit) {
      this.configureFormGroupToNoEditableKeyFieldsEntity(formGroup);
    }

    this.initFormGroupSubscribes(formGroup);

    return formGroup;
  }

  protected getValue(): RangoModalData {
    this.data.entidad.tipoTemporalidad = this.formGroup.controls.tipo.value;
    this.data.entidad.desde = this.formGroup.controls.desde.value;
    this.data.entidad.hasta = this.formGroup.controls.hasta.value;
    this.data.entidad.puntos = this.formGroup.controls.puntos.value;
    return this.data;
  }

  private configureFormGroupToNoEditableKeyFieldsEntity(formGroup: FormGroup) {
    formGroup.controls.tipo.disable();
    formGroup.controls.desde.disable();
    formGroup.controls.hasta.disable();
  }

  private initFormGroupSubscribes(formGroup: FormGroup): void {
    this.subscriptions.push(
      formGroup.controls.tipo.valueChanges.subscribe((tipo: TipoTemporalidad) =>
        this.onTipoValueChanges(tipo, formGroup)
      )
    );
  }

  private onTipoValueChanges(tipo: TipoTemporalidad, formGroup: FormGroup): void {
    switch (tipo) {
      case TipoTemporalidad.INICIAL:
        formGroup.controls.desde.reset(0);
        formGroup.controls.desde.disable();
        formGroup.controls.hasta.reset();
        formGroup.controls.hasta.enable();
        break;
      case TipoTemporalidad.INTERMEDIO:
        formGroup.controls.desde.reset(this.getRangoNextDesde());
        formGroup.controls.desde.disable();
        formGroup.controls.hasta.reset();
        formGroup.controls.hasta.enable();
        break;
      case TipoTemporalidad.FINAL:
        formGroup.controls.desde.reset(this.getRangoNextDesde());
        formGroup.controls.desde.disable();
        formGroup.controls.hasta.reset();
        formGroup.controls.hasta.disable();
        break;
      default:
        break;
    }
  }

  /**
   * Siguiente valor al hasta del rango anterior
   */
  private getRangoNextDesde(): number {
    return (this.data.rangoMaxHasta ?? 0) + 1;
  }

}
