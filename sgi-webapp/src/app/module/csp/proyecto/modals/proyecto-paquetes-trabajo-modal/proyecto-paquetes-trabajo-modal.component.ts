import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { DateValidator } from '@core/validators/date-validator';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PAQUETE_TRABAJO_DESCRIPCION_KEY = marker('csp.proyecto-paquete-trabajo.descripcion');
const PAQUETE_TRABAJO_FECHA_FIN_KEY = marker('csp.proyecto-paquete-trabajo.fecha-fin');
const PAQUETE_TRABAJO_FECHA_INICIO_KEY = marker('csp.proyecto-paquete-trabajo.fecha-inicio');
const PAQUETE_TRABAJO_NOMBRE_KEY = marker('csp.proyecto-paquete-trabajo.nombre');
const PAQUETE_TRABAJO_PERSONA_MES_KEY = marker('csp.proyecto-paquete-trabajo.persona-mes');
const PAQUETE_TRABAJO_KEY = marker('csp.proyecto-paquete-trabajo');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface PaquetesTrabajoModalData {
  paquetesTrabajo: IProyectoPaqueteTrabajo[];
  fechaInicio: DateTime;
  fechaFin: DateTime;
  paqueteTrabajo: IProyectoPaqueteTrabajo;
  readonly: boolean;
}

@Component({
  selector: 'sgi-proyecto-paquetes-trabajo-modal',
  templateUrl: './proyecto-paquetes-trabajo-modal.component.html',
  styleUrls: ['./proyecto-paquetes-trabajo-modal.component.scss']
})
export class ProyectoPaquetesTrabajoModalComponent extends DialogFormComponent<PaquetesTrabajoModalData> implements OnInit, OnDestroy {

  textSaveOrUpdate: string;

  msgParamDescripcionEntity = {};
  msgParamNombreEntity = {};
  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamPersonaMesEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<ProyectoPaquetesTrabajoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PaquetesTrabajoModalData,
    private readonly translate: TranslateService) {
    super(matDialogRef, !!data?.paqueteTrabajo?.nombre);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data?.paqueteTrabajo?.nombre ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      PAQUETE_TRABAJO_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PAQUETE_TRABAJO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PAQUETE_TRABAJO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PAQUETE_TRABAJO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PAQUETE_TRABAJO_PERSONA_MES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPersonaMesEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.paqueteTrabajo?.nombre) {
      this.translate.get(
        PAQUETE_TRABAJO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PAQUETE_TRABAJO_KEY,
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

  protected getValue(): PaquetesTrabajoModalData {
    this.data.paqueteTrabajo.nombre = this.formGroup.controls.nombre.value;
    this.data.paqueteTrabajo.fechaFin = this.formGroup.controls.fechaFin.value;
    this.data.paqueteTrabajo.fechaInicio = this.formGroup.controls.fechaInicio.value;
    this.data.paqueteTrabajo.personaMes = this.formGroup.controls.personaMes.value;
    this.data.paqueteTrabajo.descripcion = this.formGroup.controls.descripcion.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.data?.paqueteTrabajo?.nombre,
        [Validators.maxLength(250), Validators.required,
        StringValidator.notIn(this.data?.paquetesTrabajo?.map(paquete => paquete?.nombre))]),
      fechaInicio: new FormControl(this.data?.paqueteTrabajo?.fechaInicio, [Validators.required]),
      fechaFin: new FormControl(this.data?.paqueteTrabajo?.fechaFin, Validators.required),
      personaMes: new FormControl(this.data?.paqueteTrabajo?.personaMes, [
        Validators.min(0), Validators.max(9999), Validators.required]),
      descripcion: new FormControl(this.data?.paqueteTrabajo?.descripcion, [Validators.maxLength(250)])
    },
      {
        validators: [
          ValidarRangoProyecto.rangoProyecto('fechaInicio', 'fechaFin', this.data),
          DateValidator.isAfter('fechaInicio', 'fechaFin')]
      });

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

}

/**
 * Validar rango de fechas de proyecto
 * con lo insertado en el formulario
 */
export class ValidarRangoProyecto {

  static rangoProyecto(fechaInicioInput: string, fechaFinInput: string, paqueteTrabajo: PaquetesTrabajoModalData): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const fechaInicioForm = formGroup.controls[fechaInicioInput];
      const fechaFinForm = formGroup.controls[fechaFinInput];

      if ((fechaInicioForm.errors && !fechaInicioForm.errors.invalid ||
        fechaFinForm.errors && !fechaFinForm.errors.invalid)) {
        return;
      }

      if (formGroup.controls.fechaInicio.value !== null && formGroup.controls.fechaFin.value !== null) {
        if (!((paqueteTrabajo.fechaInicio <= fechaInicioForm.value) &&
          (paqueteTrabajo.fechaFin >= fechaFinForm.value))) {
          fechaInicioForm.setErrors({ invalid: true });
          fechaInicioForm.markAsTouched({ onlySelf: true });
          fechaFinForm.setErrors({ invalid: true });
          fechaFinForm.markAsTouched({ onlySelf: true });
        } else {
          fechaFinForm.setErrors(null);
          fechaInicioForm.setErrors(null);
        }

      }
    };
  }
}
