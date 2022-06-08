import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime, Interval } from 'luxon';
import { switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_RESPONSABLE_ECONOMICO_KEY = marker('csp.proyecto-responsable-economico');
const PROYECTO_RESPONSABLE_ECONOMICO_FECHA_INICIO_KEY = marker('csp.proyecto-responsable-economico.fecha-inicio');
const PROYECTO_RESPONSABLE_ECONOMICO_FECHA_FIN_KEY = marker('csp.proyecto-responsable-economico.fecha-fin');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoResponsableEconomicoModalData {
  entidad: IProyectoResponsableEconomico;
  selectedEntidades: IProyectoResponsableEconomico[];
  fechaInicioMin: DateTime;
  fechaFinMax: DateTime;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './proyecto-responsable-economico-modal.component.html',
  styleUrls: ['./proyecto-responsable-economico-modal.component.scss']
})
export class ProyectoResponsableEconomicoModalComponent
  extends DialogFormComponent<ProyectoResponsableEconomicoModalData> implements OnInit {

  textSaveOrUpdate: string;
  title: string;

  msgParamEntity = {};
  msgParamFechaInicio = {};
  msgParamFechaFin = {};

  readonly requiredFechaInicio;
  requiredFechaFin;

  get TipoColectivo() {
    return TipoColectivo;
  }

  constructor(
    matDialogRef: MatDialogRef<ProyectoResponsableEconomicoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoResponsableEconomicoModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, data.isEdit);

    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

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
      PROYECTO_RESPONSABLE_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
    this.translate.get(
      PROYECTO_RESPONSABLE_ECONOMICO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaInicio = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
    this.translate.get(
      PROYECTO_RESPONSABLE_ECONOMICO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaFin = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    if (this.data.isEdit) {
      this.translate.get(
        PROYECTO_RESPONSABLE_ECONOMICO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_RESPONSABLE_ECONOMICO_KEY,
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
        responsable: new FormControl(this.data?.entidad?.persona, [Validators.required]),
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

    if (this.data.readonly) {
      formGroup.disable();
    }

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

  protected getValue(): ProyectoResponsableEconomicoModalData {
    this.data.entidad.persona = this.formGroup.get('responsable').value;
    this.data.entidad.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.entidad.fechaFin = this.formGroup.get('fechaFin').value;
    return this.data;
  }

}
