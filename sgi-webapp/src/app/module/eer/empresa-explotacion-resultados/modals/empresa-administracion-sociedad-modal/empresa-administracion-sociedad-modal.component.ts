import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresaAdministracionSociedad, TIPO_ADMINISTRACION_SOCIEDAD_MAP } from '@core/models/eer/empresa-administracion-sociedad';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MIEMBRO_EMPRESA_EQUIPO_ADMINISTRACION_KEY = marker('eer.empresa-administracion-sociedad.miembro');
const EMPRESA_ADMINISTRACION_SOCIEDAD_FECHA_INICIO_KEY = marker('eer.empresa-administracion-sociedad.fecha-inicio');
const EMPRESA_ADMINISTRACION_SOCIEDAD_PERSONA_KEY = marker('eer.empresa-administracion-sociedad.persona');
const EMPRESA_ADMINISTRACION_TIPO_ADMINISTRACION_KEY = marker('eer.empresa-administracion-sociedad.tipo-administracion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface EmpresaAdministracionSociedadModalData {
  titleEntity: string;
  selectedEntidades: IEmpresaAdministracionSociedad[];
  entidad: IEmpresaAdministracionSociedad;
}

@Component({
  templateUrl: './empresa-administracion-sociedad-modal.component.html',
  styleUrls: ['./empresa-administracion-sociedad-modal.component.scss']
})
export class EmpresaAdministracionSociedadModalComponent extends DialogFormComponent<EmpresaAdministracionSociedadModalData> implements OnInit {
  TIPO_COLECTIVO = TipoColectivo;
  FormGroupUtil = FormGroupUtil;

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  msgParamMiembroEntity = {};
  msgParamParticipacionEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamPersonaEntity = {};
  msgParamEntidadEntity = {};
  msgParamTipoAdministracionEntity = {};
  title: string;

  get TIPO_ADMINISTRACION_MAP() {
    return TIPO_ADMINISTRACION_SOCIEDAD_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<EmpresaAdministracionSociedadModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EmpresaAdministracionSociedadModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.entidad?.id);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.entidad?.id ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      MIEMBRO_EMPRESA_EQUIPO_ADMINISTRACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMiembroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_ADMINISTRACION_SOCIEDAD_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_ADMINISTRACION_SOCIEDAD_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPersonaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_ADMINISTRACION_TIPO_ADMINISTRACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoAdministracionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

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
        miembroEquipoAdministracion: new FormControl(this.data.entidad?.miembroEquipoAdministracion, Validators.required),
        tipoAdministracion: new FormControl(this.data?.entidad?.tipoAdministracion, Validators.required),
        fechaInicio: new FormControl(this.data?.entidad?.fechaInicio, Validators.required),
        fechaFin: new FormControl(this.data?.entidad?.fechaFin)
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );
    return formGroup;
  }

  protected getValue(): EmpresaAdministracionSociedadModalData {
    this.data.entidad.miembroEquipoAdministracion = this.formGroup.get('miembroEquipoAdministracion').value;
    this.data.entidad.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.entidad.fechaFin = this.formGroup.get('fechaFin').value;
    this.data.entidad.tipoAdministracion = this.formGroup.get('tipoAdministracion').value;
    return this.data;
  }

}
