import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { TipoJustificacion, TIPO_JUSTIFICACION_MAP } from '@core/enums/tipo-justificacion';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { DateValidator } from '@core/validators/date-validator';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { switchMap } from 'rxjs/operators';
import { comparePeriodoJustificacion, getFechaFinPeriodoSeguimiento, getFechaInicioPeriodoSeguimiento } from 'src/app/module/csp/proyecto-periodo-seguimiento/proyecto-periodo-seguimiento.utils';

export interface IProyectoPeriodoJustificacionModalData {
  proyectoPeriodoJustificacion: IProyectoPeriodoJustificacion;
  proyectoPeriodoJustificacionList: IProyectoPeriodoJustificacion[];
  convocatoriaPeriodoJustificacion: IConvocatoriaPeriodoJustificacion;
  proyecto: IProyecto;
}

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_PERIODO_JUSTIFICACION_KEY = marker('csp.proyecto-periodo-justificacion');
const PROYECTO_PERIODO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY = marker('csp.proyecto-periodo-justificacion.fecha-inicio-presentacion');
const PROYECTO_PERIODO_JUSTIFICACION_FECHA_FIN_KEY = marker('csp.proyecto-periodo-justificacion.fecha-fin');
const PROYECTO_PERIODO_JUSTIFICACION_FECHA_INICIO_KEY = marker('csp.proyecto-periodo-justificacion.fecha-inicio');
const PROYECTO_PERIODO_JUSTIFICACION_PERIODO_KEY = marker('csp.proyecto-periodo-justificacion.periodo');
const PROYECTO_PERIODO_JUSTIFICACION_TIPO_KEY = marker('csp.proyecto-periodo-justificacion.tipoJustificacion');
const PROYECTO_PERIODO_JUSTIFICACION_OBSERVACIONES_KEY = marker('csp.proyecto-periodo-justificacion.observaciones');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-proyecto-periodo-justificacion-modal',
  templateUrl: './proyecto-periodo-justificacion-modal.component.html',
  styleUrls: ['./proyecto-periodo-justificacion-modal.component.scss']
})
export class ProyectoPeriodoJustificacionModalComponent
  extends BaseModalComponent<IProyectoPeriodoJustificacionModalData, ProyectoPeriodoJustificacionModalComponent> implements OnInit {

  fxFlexProperties2: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  FormGroupUtil = FormGroupUtil;

  textSaveOrUpdate: string;
  title: string;

  showDatosConvocatoriaPeriodoJustificacion: boolean;
  disabledCopy = false;

  msgParamFechaInicioPresentacionEntity = {};
  msgParamFechaFinPresentacionEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamObservacionesEntity = {};
  msgParamPeriodoEntity = {};
  msgParamTipoEntity = {};

  get TIPO_JUSTIFICACION_MAP() {
    return TIPO_JUSTIFICACION_MAP;
  }

  constructor(
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: IProyectoPeriodoJustificacionModalData,
    public matDialogRef: MatDialogRef<ProyectoPeriodoJustificacionModalComponent>,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);

    this.fxFlexProperties2 = new FxFlexProperties();
    this.fxFlexProperties2.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.checkShowDatosConvocatoriaPeriodoJustificacion(this.data.convocatoriaPeriodoJustificacion);

    if (this.data.convocatoriaPeriodoJustificacion) {
      this.subscriptions.push(this.formGroup.valueChanges.subscribe(
        () => {
          this.disabledCopy = !comparePeriodoJustificacion(this.data.convocatoriaPeriodoJustificacion,
            this.getDatosForm().proyectoPeriodoJustificacion, this.data.proyecto.fechaInicio, this.data.proyecto.fechaFin);
        }
      ));
    }

    this.textSaveOrUpdate = this.data.proyectoPeriodoJustificacion?.numPeriodo ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PERIODO_JUSTIFICACION_PERIODO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPeriodoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_PERIODO_JUSTIFICACION_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });


    this.translate.get(
      PROYECTO_PERIODO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioPresentacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PROYECTO_PERIODO_JUSTIFICACION_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_PERIODO_JUSTIFICACION_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PERIODO_JUSTIFICACION_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    if (this.data.proyectoPeriodoJustificacion?.numPeriodo) {
      this.translate.get(
        PROYECTO_PERIODO_JUSTIFICACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_PERIODO_JUSTIFICACION_KEY,
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

  protected getFormGroup(): FormGroup {
    const periodoJustificacionFinal = this.data.proyectoPeriodoJustificacionList?.find(
      periodoJustificacion => periodoJustificacion?.tipoJustificacion === TipoJustificacion.FINAL
        && periodoJustificacion?.fechaInicio !== this.data.proyectoPeriodoJustificacion?.fechaInicio);

    const ultimoPeriodoJustificacionNoFinal = this.data.proyectoPeriodoJustificacionList?.filter(
      periodoJustificacion => periodoJustificacion?.tipoJustificacion !== TipoJustificacion.FINAL
        && periodoJustificacion?.fechaInicio !== this.data.proyectoPeriodoJustificacion?.fechaInicio)
      .sort((a, b) => (b.fechaInicio > a.fechaInicio) ?
        1 : ((a.fechaInicio > b.fechaInicio) ? -1 : 0)).find(c => true);

    const formGroup = new FormGroup({
      numPeriodo: new FormControl({ value: this.data.proyectoPeriodoJustificacion?.numPeriodo, disabled: true }),
      tipoJustificacion: new FormControl(this.data.proyectoPeriodoJustificacion?.tipoJustificacion, Validators.required),
      fechaInicio: new FormControl(this.data.proyectoPeriodoJustificacion?.fechaInicio, [Validators.required]),
      fechaFin: new FormControl(this.data.proyectoPeriodoJustificacion?.fechaFin, [Validators.required]),
      fechaInicioPresentacion: new FormControl(this.data.proyectoPeriodoJustificacion?.fechaInicioPresentacion),
      fechaFinPresentacion: new FormControl(this.data.proyectoPeriodoJustificacion?.fechaFinPresentacion),
      observaciones: new FormControl(this.data.proyectoPeriodoJustificacion?.observaciones, Validators.maxLength(2000)),

      /*Convocatoria*/
      numPeriodoConvocatoria: new FormControl({ value: this.data.convocatoriaPeriodoJustificacion?.numPeriodo, disabled: true }),
      tipoJustificacionConvocatoria: new FormControl(
        { value: this.data.convocatoriaPeriodoJustificacion?.tipo, disabled: true }, [Validators.required]),
      fechaInicioConvocatoria: new FormControl(
        {
          value: this.data.convocatoriaPeriodoJustificacion?.mesInicial ? getFechaInicioPeriodoSeguimiento(this.data.proyecto.fechaInicio,
            this.data.convocatoriaPeriodoJustificacion.mesInicial) : null, disabled: true
        }, [Validators.required]),
      fechaFinConvocatoria: new FormControl(
        {
          value: this.data.convocatoriaPeriodoJustificacion?.mesInicial ?
            getFechaFinPeriodoSeguimiento(this.data.proyecto.fechaInicio, this.data.proyecto.fechaFin,
              this.data.convocatoriaPeriodoJustificacion.mesFinal) : null,
          disabled: true
        }, [Validators.required]),
      fechaInicioPresentacionConvocatoria: new FormControl(
        { value: this.data.convocatoriaPeriodoJustificacion?.fechaInicioPresentacion, disabled: true }),
      fechaFinPresentacionConvocatoria: new FormControl(
        { value: this.data.convocatoriaPeriodoJustificacion?.fechaFinPresentacion, disabled: true }),
      observacionesConvocatoria: new FormControl(
        { value: this.data.convocatoriaPeriodoJustificacion?.observaciones, disabled: true }, Validators.maxLength(2000))


    }, {
      validators: [
        this.isFinalUltimoPeriodo(ultimoPeriodoJustificacionNoFinal?.fechaFin),
        this.notOverlapsPeriodoJustificacion('fechaInicio', 'fechaFin', 'numPeriodo'),
        DateValidator.isAfter('fechaInicio', 'fechaFin')]
    });

    // Si ya existe un periodo final tiene que ser el ultimo y solo puede haber 1
    if (periodoJustificacionFinal) {
      formGroup.get('tipoJustificacion').setValidators([
        StringValidator.notIn([TipoJustificacion.FINAL]),
        formGroup.get('tipoJustificacion').validator
      ]);
    }

    if (this.data.convocatoriaPeriodoJustificacion) {
      if (this.data.proyectoPeriodoJustificacion) {
        this.disabledCopy = !comparePeriodoJustificacion(this.data.convocatoriaPeriodoJustificacion,
          this.data.proyectoPeriodoJustificacion, this.data.proyecto.fechaInicio, this.data.proyecto.fechaFin);
      } else {
        formGroup.controls.tipoJustificacion.disable();
        formGroup.controls.fechaInicio.disable();
        formGroup.controls.fechaFin.disable();
        formGroup.controls.fechaInicioPresentacion.disable();
        formGroup.controls.fechaFinPresentacion.disable();
        formGroup.controls.observaciones.disable();
      }
    }

    return formGroup;
  }

  protected getDatosForm(): IProyectoPeriodoJustificacionModalData {

    if (!this.data.proyectoPeriodoJustificacion) {
      this.data.proyectoPeriodoJustificacion = {} as IProyectoPeriodoJustificacion;
    }

    this.data.proyectoPeriodoJustificacion.numPeriodo = this.formGroup.get('numPeriodo').value;
    this.data.proyectoPeriodoJustificacion.tipoJustificacion = this.formGroup.get('tipoJustificacion').value;
    this.data.proyectoPeriodoJustificacion.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.proyectoPeriodoJustificacion.fechaFin = this.formGroup.get('fechaFin').value;
    this.data.proyectoPeriodoJustificacion.fechaInicioPresentacion = this.formGroup.get('fechaInicioPresentacion').value;
    this.data.proyectoPeriodoJustificacion.fechaFinPresentacion = this.formGroup.get('fechaFinPresentacion').value;
    this.data.proyectoPeriodoJustificacion.observaciones = this.formGroup.get('observaciones').value;
    this.data.proyectoPeriodoJustificacion.convocatoriaPeriodoJustificacionId = this.data.convocatoriaPeriodoJustificacion?.id;
    return this.data;
  }

  /**
   * Recalcula el numero de periodo en funcion de la ordenacion por mes inicial de todos los periodos.
   */
  recalcularNumPeriodo(): void {
    let numPeriodo = 1;
    const fechaInicio = this.formGroup.get('fechaInicio').value;

    this.data.proyectoPeriodoJustificacionList.forEach(c => {
      if (fechaInicio > c.fechaInicio) {
        numPeriodo++;
      }
    });

    this.formGroup.get('numPeriodo').setValue(numPeriodo);
  }

  private checkShowDatosConvocatoriaPeriodoJustificacion(convocatoriaPeriodoJustificacion: IConvocatoriaPeriodoJustificacion): void {
    this.showDatosConvocatoriaPeriodoJustificacion = !!convocatoriaPeriodoJustificacion;
  }

  /**
   * Comprueba que si el periodo es tipo final sea el ultimo (empiza despues del ultimo periodo no final).
   *
   * @param fechaFinUltimoPeriodoNoFinal Mes de fin del ultimo periodo que no es de tipo final.
   */
  private isFinalUltimoPeriodo(fechaFinUltimoPeriodoNoFinal: DateTime): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const tipoJustificacionControl = formGroup.get('tipoJustificacion');
      const fechaInicioControl = formGroup.get('fechaInicio');

      if (!fechaFinUltimoPeriodoNoFinal || (tipoJustificacionControl.errors && !tipoJustificacionControl.errors.finalNotLast)
        || (fechaInicioControl.errors && !fechaInicioControl.errors.finalNotLast)) {
        return;
      }

      const fechaInicioNumber = fechaInicioControl.value;
      const tipoJustificacionValue = tipoJustificacionControl.value;

      if (tipoJustificacionValue === TipoJustificacion.FINAL && fechaInicioNumber < fechaFinUltimoPeriodoNoFinal) {
        tipoJustificacionControl.setErrors({ finalNotLast: true });
        tipoJustificacionControl.markAsTouched({ onlySelf: true });
      } else if (tipoJustificacionControl.errors) {
        delete tipoJustificacionControl.errors.finalNotLast;
        tipoJustificacionControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

  copyToProyecto(): void {
    this.enableEditableControls();
    this.formGroup.get('fechaInicio').setValue(this.formGroup.get('fechaInicioConvocatoria').value);
    this.formGroup.get('fechaFin').setValue(this.formGroup.get('fechaFinConvocatoria').value);
    this.formGroup.get('fechaInicioPresentacion').setValue(
      this.formGroup.get('fechaInicioPresentacionConvocatoria').value);
    this.formGroup.get('fechaFinPresentacion').setValue(
      this.formGroup.get('fechaFinPresentacionConvocatoria').value);
    this.formGroup.get('observaciones').setValue(this.formGroup.get('observacionesConvocatoria').value);
    this.formGroup.get('tipoJustificacion').setValue(this.formGroup.get('tipoJustificacionConvocatoria').value);
  }

  /**
   * Comprueba que el rango de fechas entre los 2 campos indicados no se superpone con ninguno de los rangos
   *
   * @param startRangeFieldName Nombre del campo que indica el inicio del rango.
   * @param endRangeFieldName Nombre del campo que indica el fin del rango.
   */
  private notOverlapsPeriodoJustificacion(startRangeFieldName: string, endRangeFieldName: string, filterFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const inicioRangoControl = formGroup.controls[startRangeFieldName];
      const finRangoControl = formGroup.controls[endRangeFieldName];
      const filteredFieldControl = formGroup.controls[filterFieldName];

      const inicioRangoNumber = inicioRangoControl.value ? inicioRangoControl.value.toMillis() : Number.MIN_VALUE;
      const finRangoNumber = finRangoControl.value ? finRangoControl.value.toMillis() : Number.MAX_VALUE;
      let ranges;

      if (filteredFieldControl.value) {
        if ((inicioRangoControl.errors && !inicioRangoControl.errors.overlapped)
          || (finRangoControl.errors && !finRangoControl.errors.overlapped)) {
          return;
        }
        ranges = this.data.proyectoPeriodoJustificacionList.filter(
          periodo => periodo.numPeriodo !== filteredFieldControl.value
        ).map(periodo => {
          return {
            inicio: periodo.fechaInicio ? periodo.fechaInicio.toMillis() : Number.MIN_VALUE,
            fin: periodo.fechaFin ? periodo.fechaFin.toMillis() : Number.MAX_VALUE
          };
        });
      } else {
        ranges = this.data.proyectoPeriodoJustificacionList?.map(periodo => {
          return {
            inicio: periodo.fechaInicio ? periodo.fechaInicio.toMillis() : Number.MIN_VALUE,
            fin: periodo.fechaFin ? periodo.fechaFin.toMillis() : Number.MAX_VALUE
          };
        });
      }
      if (inicioRangoControl.value != null && finRangoControl.value != null) {

        if (ranges.some(r => inicioRangoNumber <= r.fin && r.inicio <= finRangoNumber)) {
          inicioRangoControl.setErrors({ overlapped: true });
          inicioRangoControl.markAsTouched({ onlySelf: true });

          finRangoControl.setErrors({ overlapped: true });
          finRangoControl.markAsTouched({ onlySelf: true });
        } else {
          if (inicioRangoControl.errors) {
            delete inicioRangoControl.errors.overlapped;
            inicioRangoControl.updateValueAndValidity({ onlySelf: true });
          }

          if (finRangoControl.errors) {
            delete finRangoControl.errors.overlapped;
            finRangoControl.updateValueAndValidity({ onlySelf: true });
          }
        }
      }
    };
  }

  private enableEditableControls(): void {
    this.formGroup.controls.tipoJustificacion.enable();
    this.formGroup.controls.fechaInicio.enable();
    this.formGroup.controls.fechaFin.enable();
    this.formGroup.controls.fechaInicioPresentacion.enable();
    this.formGroup.controls.fechaFinPresentacion.enable();
    this.formGroup.controls.observaciones.enable();
  }

}
