import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';

const PERIODO_TITULARIDAD_KEY = marker('pii.invencion-titularidad.periodo-titularidad');
const PERIODO_TITULARIDAD_FECHA_INICIO = marker('pii.invencion-titularidad.periodo-titularidad.fecha-inicio-nueva');
const PERIODO_TITULARIDAD_FECHA_FIN = marker('pii.invencion-titularidad.periodo-titularidad.fecha-fin-anterior');
const MSG_ACEPTAR = marker('btn.ok');
const MSG_ADD = marker('btn.add');

export interface IPeriodoTitularidadModalData {
  periodoTitularidad: StatusWrapper<IPeriodoTitularidad>;
  /** Fecha de Inicio minima que puede tener un {@link IPeriodoTitularidad} al modificarse */
  fechaInicioMinima?: DateTime;
  showFechaFin?: boolean;
}

export interface IResultPeriodoTitularidadModalData {
  periodoTitularidad: StatusWrapper<IPeriodoTitularidad>;
}

@Component({
  selector: 'sgi-periodo-titularidad-modal',
  templateUrl: './periodo-titularidad-modal.component.html',
  styleUrls: ['./periodo-titularidad-modal.component.scss']
})
export class PeriodoTitularidadModalComponent extends DialogFormComponent<IPeriodoTitularidadModalData> implements OnInit {

  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamTitle = {};

  textSaveOrUpdate: string;

  constructor(
    matDialogRef: MatDialogRef<PeriodoTitularidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) private data: IPeriodoTitularidadModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!!data.periodoTitularidad.created);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PERIODO_TITULARIDAD_FECHA_INICIO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PERIODO_TITULARIDAD_FECHA_FIN,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PERIODO_TITULARIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTitle = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.textSaveOrUpdate = this.isEditionMode() ? MSG_ACEPTAR : MSG_ADD;
  }

  protected getValue(): IResultPeriodoTitularidadModalData {
    if (this.formGroup.touched) {
      this.data.periodoTitularidad.value.fechaInicio = this.formGroup.controls.fechaInicio.value;
      this.data.periodoTitularidad.value.fechaFinPrevious = this.formGroup.controls.fechaFin.value;
      if (!this.data.periodoTitularidad.created) {
        this.data.periodoTitularidad.setEdited();
      }
    }
    const result: IResultPeriodoTitularidadModalData = {
      periodoTitularidad: this.data.periodoTitularidad
    };

    return result;
  }

  protected buildFormGroup(): FormGroup {
    const fechaFinValidators: Array<ValidatorFn> = [DateValidator.minDate(this.data.fechaInicioMinima)];
    const fechaInicioValidators: Array<ValidatorFn> = [Validators.required];

    if (this.showFechaFin()) {
      fechaFinValidators.push(Validators.required);
    }
    const form: FormGroup = new FormGroup({
      fechaInicio: new FormControl(this.data?.periodoTitularidad.value.fechaInicio, []),
      fechaFin: new FormControl(this.data?.periodoTitularidad.value.fechaFin, [])
    });

    fechaInicioValidators.push(DateValidator.isAfterOther(form.controls.fechaFin));
    fechaFinValidators.push(DateValidator.isBeforeOther(form.controls.fechaInicio));

    form.controls.fechaInicio.setValidators(fechaInicioValidators);
    form.controls.fechaFin.setValidators(fechaFinValidators);

    return form;
  }

  public showFechaFin(): boolean {
    return this.data.showFechaFin ?? this.data.periodoTitularidad.created;
  }

  public isEditionMode() {
    return !this.data.periodoTitularidad.created;
  }
}
