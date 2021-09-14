import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { TipoJustificacion, TIPO_JUSTIFICACION_MAP } from '@core/enums/tipo-justificacion';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { NumberValidator } from '@core/validators/number-validator';
import { RangeValidator } from '@core/validators/range-validator';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

export interface IConvocatoriaPeriodoJustificacionModalData {
  duracion: number;
  convocatoriaPeriodoJustificacion: IConvocatoriaPeriodoJustificacion;
  convocatoriaPeriodoJustificacionList: StatusWrapper<IConvocatoriaPeriodoJustificacion>[];
}

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_PERIODO_JUSTIFICACION_KEY = marker('csp.convocatoria-periodo-justificacion');
const CONVOCATORIA_PERIODO_JUSTIFICACION_FECHA_INICIO_KEY = marker('csp.convocatoria-periodo-justificacion.fecha-inicio');
const CONVOCATORIA_PERIODO_JUSTIFICACION_MES_FIN_KEY = marker('csp.convocatoria-periodo-justificacion.mes-fin');
const CONVOCATORIA_PERIODO_JUSTIFICACION_MES_INICIO_KEY = marker('csp.convocatoria-periodo-justificacion.mes-inicio');
const CONVOCTORIA_PERIODO_JUSTIFICACION_PERIODO_KEY = marker('csp.convocatoria-periodo-justificacion.periodo');
const CONVOCTORIA_PERIODO_JUSTIFICACION_TIPO_KEY = marker('csp.convocatoria-periodo-justificacion.tipo');
const CONVOCATORIA_PERIODO_JUSTIFICACION_OBSERVACIONES_KEY = marker('csp.convocatoria-periodo-justificacion.observaciones');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './convocatoria-periodos-justificacion-modal.component.html',
  styleUrls: ['./convocatoria-periodos-justificacion-modal.component.scss']
})
export class ConvocatoriaPeriodosJustificacionModalComponent
  extends BaseModalComponent<IConvocatoriaPeriodoJustificacion, ConvocatoriaPeriodosJustificacionModalComponent> implements OnInit {

  fxFlexProperties2: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  FormGroupUtil = FormGroupUtil;

  textSaveOrUpdate: string;
  title: string;

  msgParamFechaInicioEntity = {};
  msgParamMesInicioEntity = {};
  msgParamMesFinEntity = {};
  msgParamObservacionesEntity = {};
  msgParamPeriodoEntity = {};
  msgParamTipoEntity = {};

  get TIPO_JUSTIFICACION_MAP() {
    return TIPO_JUSTIFICACION_MAP;
  }

  constructor(
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: IConvocatoriaPeriodoJustificacionModalData,
    public matDialogRef: MatDialogRef<ConvocatoriaPeriodosJustificacionModalComponent>,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.convocatoriaPeriodoJustificacion);

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
    this.textSaveOrUpdate = this.data.convocatoriaPeriodoJustificacion?.numPeriodo ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCTORIA_PERIODO_JUSTIFICACION_PERIODO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPeriodoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONVOCTORIA_PERIODO_JUSTIFICACION_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });


    this.translate.get(
      CONVOCATORIA_PERIODO_JUSTIFICACION_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      CONVOCATORIA_PERIODO_JUSTIFICACION_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      CONVOCATORIA_PERIODO_JUSTIFICACION_MES_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMesInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_PERIODO_JUSTIFICACION_MES_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMesFinEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data.convocatoriaPeriodoJustificacion?.numPeriodo) {
      this.translate.get(
        CONVOCATORIA_PERIODO_JUSTIFICACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONVOCATORIA_PERIODO_JUSTIFICACION_KEY,
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
    const rangosPeriodosExistentes = this.data.convocatoriaPeriodoJustificacionList
      .filter(periodoJustificacion =>
        periodoJustificacion.value.mesInicial !== this.data.convocatoriaPeriodoJustificacion.mesInicial)
      .map(periodoJustificacion => {
        return {
          inicio: periodoJustificacion.value.mesInicial,
          fin: periodoJustificacion.value.mesFinal
        };
      });

    const periodoJustificacionFinal = this.data.convocatoriaPeriodoJustificacionList
      .find(periodoJustificacion => periodoJustificacion.value.tipo === TipoJustificacion.FINAL
        && periodoJustificacion.value.mesInicial !== this.data.convocatoriaPeriodoJustificacion.mesInicial);

    const ultimoPeriodoJustificacionNoFinal = this.data.convocatoriaPeriodoJustificacionList
      .filter(periodoJustificacion => periodoJustificacion.value.tipo !== TipoJustificacion.FINAL
        && periodoJustificacion.value.mesInicial !== this.data.convocatoriaPeriodoJustificacion.mesInicial)
      .sort((a, b) => (b.value.mesInicial > a.value.mesInicial) ? 1 : ((a.value.mesInicial > b.value.mesInicial) ? -1 : 0)).find(c => true);

    const formGroup = new FormGroup({
      numPeriodo: new FormControl(this.data.convocatoriaPeriodoJustificacion?.numPeriodo, Validators.required),
      tipo: new FormControl(this.data.convocatoriaPeriodoJustificacion?.tipo, Validators.required),
      desdeMes: new FormControl(this.data.convocatoriaPeriodoJustificacion?.mesInicial, [Validators.required, Validators.min(1)]),
      hastaMes: new FormControl(this.data.convocatoriaPeriodoJustificacion?.mesFinal, [Validators.required, Validators.min(1)]),
      fechaInicio: new FormControl(this.data.convocatoriaPeriodoJustificacion?.fechaInicioPresentacion),
      fechaFin: new FormControl(this.data.convocatoriaPeriodoJustificacion?.fechaFinPresentacion),
      observaciones: new FormControl(this.data.convocatoriaPeriodoJustificacion?.observaciones, Validators.maxLength(2000))
    }, {
      validators: [
        this.isFinalUltimoPeriodo(ultimoPeriodoJustificacionNoFinal?.value.mesFinal),
        NumberValidator.isAfterOrEqual('desdeMes', 'hastaMes'),
        RangeValidator.notOverlaps('desdeMes', 'hastaMes', rangosPeriodosExistentes),
        DateValidator.isAfter('fechaInicio', 'fechaFin')]
    });

    // Si ya existe un periodo final tiene que ser el ultimo y solo puede haber 1
    if (periodoJustificacionFinal !== undefined) {
      formGroup.get('tipo').setValidators([
        StringValidator.notIn([TipoJustificacion.FINAL]),
        formGroup.get('tipo').validator
      ]);

      formGroup.get('desdeMes').setValidators([
        Validators.max(periodoJustificacionFinal.value.mesInicial),
        formGroup.get('desdeMes').validator
      ]);
    }

    // Si es el primer periodo este ha de comenzar en el mes 1
    // Si no es es el primero, deberá ser siempre consecutivo al anterior periodo de justificación
    const desdeMesControl = formGroup.get('desdeMes');
    const hastaMesControl = formGroup.get('hastaMes');
    this.subscriptions.push(desdeMesControl.valueChanges.subscribe(value => {
      if (this.data.convocatoriaPeriodoJustificacionList.length === 0
        || this.data.convocatoriaPeriodoJustificacionList[0].value.mesInicial !== 1) {
        if (value && value > 1) {
          desdeMesControl.setErrors({ initial: true });
          desdeMesControl.markAsTouched({ onlySelf: true });
        } else if (desdeMesControl.errors) {
          delete desdeMesControl.errors.initial;
          desdeMesControl.updateValueAndValidity({ onlySelf: true, emitEvent: false });
        }
      } else {
        if (value && (
          (this.data.convocatoriaPeriodoJustificacion.numPeriodo > 1
            && value !== this.data.convocatoriaPeriodoJustificacionList[
              this.data.convocatoriaPeriodoJustificacion.numPeriodo - 2].value.mesFinal + 1)
          || ((this.data.convocatoriaPeriodoJustificacion.numPeriodo < 1
            || this.data.convocatoriaPeriodoJustificacion.numPeriodo == null)
            && value !== this.data.convocatoriaPeriodoJustificacionList[
              this.data.convocatoriaPeriodoJustificacionList.length - 1].value.mesFinal + 1)
        )) {
          desdeMesControl.setErrors({ wrongOrder: true });
          desdeMesControl.markAsTouched({ onlySelf: true });
        } else if (desdeMesControl.errors) {
          delete desdeMesControl.errors.wrongOrder;
          desdeMesControl.updateValueAndValidity({ onlySelf: true, emitEvent: false });
        }
      }
      if (value && value > hastaMesControl.value) {
        hastaMesControl.setErrors({ afterOrEqual: true });
        hastaMesControl.markAsTouched({ onlySelf: true });
      } else if (hastaMesControl.errors) {
        delete hastaMesControl.errors.afterOrEqual;
        hastaMesControl.updateValueAndValidity({ onlySelf: true, emitEvent: false });
      }
    }));
    // Si la convocatoria tiene duracion el mesFinal no puede superarla
    if (this.data.duracion) {
      formGroup.get('hastaMes').setValidators([
        Validators.max(this.data.duracion),
        formGroup.get('hastaMes').validator
      ]);
    }
    return formGroup;
  }

  protected getDatosForm(): IConvocatoriaPeriodoJustificacion {
    const convocatoriaPeriodoJustificacion = this.data.convocatoriaPeriodoJustificacion;
    convocatoriaPeriodoJustificacion.numPeriodo = this.formGroup.get('numPeriodo').value;
    convocatoriaPeriodoJustificacion.tipo = this.formGroup.get('tipo').value;
    convocatoriaPeriodoJustificacion.mesInicial = this.formGroup.get('desdeMes').value;
    convocatoriaPeriodoJustificacion.mesFinal = this.formGroup.get('hastaMes').value;
    convocatoriaPeriodoJustificacion.fechaInicioPresentacion = this.formGroup.get('fechaInicio').value;
    convocatoriaPeriodoJustificacion.fechaFinPresentacion = this.formGroup.get('fechaFin').value;
    convocatoriaPeriodoJustificacion.observaciones = this.formGroup.get('observaciones').value;
    return convocatoriaPeriodoJustificacion;
  }

  /**
   * Recalcula el numero de periodo en funcion de la ordenacion por mes inicial de todos los periodos.
   */
  recalcularNumPeriodo(): void {
    let numPeriodo = 1;
    const mesInicial = this.formGroup.get('desdeMes').value;

    this.data.convocatoriaPeriodoJustificacionList.forEach(c => {
      if (mesInicial > c.value.mesInicial) {
        numPeriodo++;
      }
    });

    this.formGroup.get('numPeriodo').setValue(numPeriodo);
  }

  /**
   * Comprueba que si el periodo es tipo final sea el ultimo (empiza despues del ultimo periodo no final).
   *
   * @param mesFinUltimoPeriodoNoFinal Mes de fin del ultimo periodo que no es de tipo final.
   */
  private isFinalUltimoPeriodo(mesFinUltimoPeriodoNoFinal: number): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const tipoJustificacionControl = formGroup.controls.tipo;
      const mesInicioControl = formGroup.controls.desdeMes;

      if (!mesFinUltimoPeriodoNoFinal || (tipoJustificacionControl.errors && !tipoJustificacionControl.errors.finalNotLast)
        || (mesInicioControl.errors && !mesInicioControl.errors.finalNotLast)) {
        return;
      }

      const mesInicioNumber = mesInicioControl.value;
      const tipoJustificacionValue = tipoJustificacionControl.value;

      if (tipoJustificacionValue === TipoJustificacion.FINAL && mesInicioNumber < mesFinUltimoPeriodoNoFinal) {
        tipoJustificacionControl.setErrors({ finalNotLast: true });
        tipoJustificacionControl.markAsTouched({ onlySelf: true });
      } else if (tipoJustificacionControl.errors) {
        delete tipoJustificacionControl.errors.finalNotLast;
        tipoJustificacionControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

}
