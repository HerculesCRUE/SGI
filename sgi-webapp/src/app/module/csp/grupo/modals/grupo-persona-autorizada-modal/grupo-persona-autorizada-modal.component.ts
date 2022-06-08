import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime, Interval } from 'luxon';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_PERSONA_AUTORIZADA_FECHA_FIN_KEY = marker('csp.grupo-persona-autorizada.fecha-fin');
const GRUPO_PERSONA_AUTORIZADA_FECHA_INICIO_KEY = marker('csp.grupo-persona-autorizada.fecha-inicio');
const GRUPO_PERSONA_AUTORIZADA_PERSONA_AUTORIZADA_KEY = marker('csp.grupo-persona-autorizada');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface GrupoPersonaAutorizadaModalData {
  titleEntity: string;
  selectedEntidades: IGrupoPersonaAutorizada[];
  entidad: IGrupoPersonaAutorizada;
  fechaInicioMin: DateTime;
  fechaFinMax: DateTime;
  isEdit: boolean;
}

@Component({
  templateUrl: './grupo-persona-autorizada-modal.component.html',
  styleUrls: ['./grupo-persona-autorizada-modal.component.scss']
})
export class GrupoPersonaAutorizadaModalComponent extends DialogFormComponent<GrupoPersonaAutorizadaModalData> implements OnInit {

  TIPO_COLECTIVO = TipoColectivo;

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  rolesGrupo$: Observable<IRolProyecto[]>;
  colectivosIdRolParticipacion: string[];

  readonly requiredFechaInicio;
  requiredFechaFin;

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamMiembroEntity = {};
  msgParamRolParticipacionEntity = {};
  msgParamHoraEntity = {};
  msgParamDedicacionEntity = {};
  msgParamParticipacionEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<GrupoPersonaAutorizadaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoPersonaAutorizadaModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.isEdit);

    this.textSaveOrUpdate = this.data?.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

    this.data.selectedEntidades?.sort((a, b) => {
      if (!!!a.fechaInicio) {
        return -1;
      }
      return a.fechaInicio.toMillis() - b.fechaInicio.toMillis();
    });
    this.requiredFechaInicio = !!this.data.selectedEntidades?.length;
    this.requiredFechaFin = this.data.selectedEntidades?.some(select => !!!select.fechaFin);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      GRUPO_PERSONA_AUTORIZADA_PERSONA_AUTORIZADA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMiembroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_PERSONA_AUTORIZADA_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      GRUPO_PERSONA_AUTORIZADA_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });


    if (this.data?.isEdit) {
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
        miembro: new FormControl(this.data?.entidad?.persona, [Validators.required]),
        fechaInicio: new FormControl(this.data?.entidad?.fechaInicio),
        fechaFin: new FormControl(this.data?.entidad?.fechaFin),
      }
    );

    this.setupValidators(formGroup);

    this.subscriptions.push(formGroup.controls.fechaInicio.valueChanges.subscribe(
      (value: DateTime) => {
        if (
          this.requiredFechaInicio
          && this.data.selectedEntidades.length > 0
          && value
          && value.toMillis() <= this.data.selectedEntidades[0].fechaInicio.toMillis()
        ) {
          this.requiredFechaFin = true;
        }
        else {
          this.requiredFechaFin = this.data.selectedEntidades.some(select => !!!select.fechaFin);
        }
      }
    ));

    this.subscriptions.push(formGroup.controls.fechaInicio.statusChanges.subscribe(
      () => {
        if (formGroup.controls.fechaFin.value) {
          formGroup.controls.fechaFin.markAsTouched();
          formGroup.controls.fechaFin.updateValueAndValidity();
        }
      }
    ));

    return formGroup;
  }

  private setupValidators(formGroup: FormGroup): void {
    const intervals: Interval[] = this.data.selectedEntidades?.map(responsableEconomico => {
      return Interval.fromDateTimes(
        responsableEconomico.fechaInicio ? responsableEconomico.fechaInicio : this.data.fechaInicioMin,
        responsableEconomico.fechaFin ? responsableEconomico.fechaFin : this.data.fechaFinMax
      );
    });

    if (this.requiredFechaInicio) {
      formGroup.controls.fechaInicio.setValidators([
        Validators.required,
        DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax),
        DateValidator.notOverlaps(intervals),
        DateValidator.notOverlapsDependentForStart(intervals, formGroup.controls.fechaFin)
      ]);
    }
    else {
      formGroup.controls.fechaInicio.setValidators([
        DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax),
        DateValidator.notOverlaps(intervals),
        DateValidator.notOverlapsDependentForStart(intervals, formGroup.controls.fechaFin)
      ]);
    }
    if (this.requiredFechaFin) {
      formGroup.controls.fechaFin.setValidators([
        Validators.required,
        DateValidator.isAfterOther(formGroup.controls.fechaInicio),
        DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax),
        DateValidator.notOverlaps(intervals),
        DateValidator.notOverlapsDependentForEnd(intervals, formGroup.controls.fechaInicio)
      ]);
    }
    else {
      formGroup.controls.fechaFin.setValidators([
        DateValidator.isAfterOther(formGroup.controls.fechaInicio),
        DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax),
        DateValidator.notOverlaps(intervals),
        DateValidator.notOverlapsDependentForEnd(intervals, formGroup.controls.fechaInicio)
      ]);
    }
  }

  protected getValue(): GrupoPersonaAutorizadaModalData {
    this.data.entidad.persona = this.formGroup.get('miembro').value;
    this.data.entidad.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.entidad.fechaFin = this.formGroup.get('fechaFin').value;
    return this.data;
  }

}
