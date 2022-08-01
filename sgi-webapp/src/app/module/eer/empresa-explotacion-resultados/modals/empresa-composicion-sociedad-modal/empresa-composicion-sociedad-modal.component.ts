import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresaComposicionSociedad, TIPO_APORTACION_COMPOSICION_SOCIEDAD_MAP } from '@core/models/eer/empresa-composicion-sociedad';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MIEMBRO_EQUIPO_EMPRESA_EQUIPO_EMPRENDEDOR_MIEMBRO_KEY = marker('eer.empresa-composicion-sociedad.miembro');
const EMPRESA_COMPOSICION_SOCIEDAD_PARTICIPACION_KEY = marker('eer.empresa-composicion-sociedad.participacion');
const EMPRESA_COMPOSICION_SOCIEDAD_FECHA_INICIO_KEY = marker('eer.empresa-composicion-sociedad.fecha-inicio');
const EMPRESA_COMPOSICION_SOCIEDAD_PERSONA_KEY = marker('eer.empresa-composicion-sociedad.persona');
const EMPRESA_COMPOSICION_SOCIEDAD_ENTIDAD_KEY = marker('eer.empresa-composicion-sociedad.entidad');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface EmpresaComposicionSociedadModalData {
  titleEntity: string;
  selectedEntidades: IEmpresaComposicionSociedad[];
  entidad: IEmpresaComposicionSociedad;
}

@Component({
  templateUrl: './empresa-composicion-sociedad-modal.component.html',
  styleUrls: ['./empresa-composicion-sociedad-modal.component.scss']
})
export class EmpresaComposicionSociedadModalComponent extends DialogFormComponent<EmpresaComposicionSociedadModalData> implements OnInit {
  TIPO_COLECTIVO = TipoColectivo;
  FormGroupUtil = FormGroupUtil;

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  msgParamMiembroEntity = {};
  msgParamParticipacionEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamPersonaEntity = {};
  msgParamEntidadEntity = {};
  title: string;

  get TIPO_APORTACION_MAP() {
    return TIPO_APORTACION_COMPOSICION_SOCIEDAD_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<EmpresaComposicionSociedadModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EmpresaComposicionSociedadModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.entidad?.id);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.entidad?.id ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(
      this.formGroup.get('miembroSociedad').valueChanges
        .subscribe((value) => this.addValidators(value))
    );

    this.subscriptions.push(
      this.formGroup.get('participacion').valueChanges
        .subscribe(() => this.validateParticipacion())
    );
  }

  private setupI18N(): void {
    this.translate.get(
      MIEMBRO_EQUIPO_EMPRESA_EQUIPO_EMPRENDEDOR_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMiembroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_COMPOSICION_SOCIEDAD_PARTICIPACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamParticipacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_COMPOSICION_SOCIEDAD_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_COMPOSICION_SOCIEDAD_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPersonaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_COMPOSICION_SOCIEDAD_ENTIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntidadEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.entidad?.id) {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        this.data.titleEntity,
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
        miembroSociedad: new FormControl(true, Validators.required),
        miembroSociedadPersona: new FormControl(this.data.entidad?.miembroSociedadPersona),
        miembroSociedadEmpresa: new FormControl(this.data.entidad?.miembroSociedadEmpresa),
        participacion: new FormControl(this.data?.entidad?.participacion,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(100)]),
        tipoAportacion: new FormControl(this.data?.entidad?.tipoAportacion, Validators.required),
        fechaInicio: new FormControl(this.data?.entidad?.fechaInicio, Validators.required),
        fechaFin: new FormControl(this.data?.entidad?.fechaFin),
        capitalSocial: new FormControl(this.data?.entidad?.capitalSocial, [
          Validators.min(1),
          Validators.max(2_147_483_647)
        ])
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );

    this.addValidators(this.data.entidad, formGroup);

    return formGroup;
  }

  protected getValue(): EmpresaComposicionSociedadModalData {
    if (this.formGroup.get('miembroSociedad').value) {
      this.data.entidad.miembroSociedadPersona = this.formGroup.get('miembroSociedadPersona').value;
      this.data.entidad.miembroSociedadEmpresa = null;
    } else {
      this.data.entidad.miembroSociedadEmpresa = this.formGroup.get('miembroSociedadEmpresa').value;
      this.data.entidad.miembroSociedadPersona = null;
    }
    this.data.entidad.capitalSocial = this.formGroup.get('capitalSocial').value;
    this.data.entidad.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.entidad.fechaFin = this.formGroup.get('fechaFin').value;
    this.data.entidad.participacion = this.formGroup.get('participacion').value;
    this.data.entidad.tipoAportacion = this.formGroup.get('tipoAportacion').value;
    return this.data;
  }

  private deleteError(formControl: AbstractControl, errorName: string): void {
    if (formControl.errors) {
      delete formControl.errors[errorName];
      if (Object.keys(formControl.errors).length === 0) {
        formControl.setErrors(null);
      }
    }
  }

  private addError(formControl: AbstractControl, errorName: string): void {
    if (!formControl.errors) {
      formControl.setErrors({});
    }
    formControl.errors[errorName] = true;
    formControl.markAsTouched({ onlySelf: true });
  }

  private addValidators(entidad: IEmpresaComposicionSociedad, form?: FormGroup) {
    let value = true;
    if (!form) {
      form = this.formGroup;
      value = form.get('miembroSociedad').value;
    }

    if (entidad?.miembroSociedadPersona?.id || entidad?.miembroSociedadEmpresa?.id) {
      value = entidad?.miembroSociedadPersona !== undefined && entidad?.miembroSociedadPersona !== null;
    }

    if (value) {
      form.get('miembroSociedadPersona').setValidators([Validators.required]);
      form.get('miembroSociedadEmpresa').setValidators(null);
    } else {
      form.get('miembroSociedadPersona').setValidators(null);
      form.get('miembroSociedadEmpresa').setValidators([Validators.required]);
    }

    form.get('miembroSociedad').setValue(value);
    form.get('miembroSociedad').updateValueAndValidity();
    form.get('miembroSociedadPersona').updateValueAndValidity();
    form.get('miembroSociedadEmpresa').updateValueAndValidity();

  }

  private validateParticipacion() {
    const participacionForm = this.formGroup.get('participacion');
    let participacion = 0;
    this.data.selectedEntidades.forEach(entidad => {
      participacion += entidad.participacion as number;
    });

    if (participacion + participacionForm.value > 100) {
      this.addError(participacionForm, 'invalidSum');
    } else if (participacionForm.errors) {
      this.deleteError(participacionForm, 'invalidSum');
    }

  }

}
