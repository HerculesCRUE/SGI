import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { TipoSeguimiento, TIPO_SEGUIMIENTO_MAP } from '@core/enums/tipo-seguimiento';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
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

export interface IConvocatoriaSeguimientoCientificoModalData {
  duracion: number;
  convocatoriaSeguimientoCientifico: IConvocatoriaPeriodoSeguimientoCientifico;
  convocatoriaSeguimientoCientificoList: StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>[];
}

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_KEY = marker('csp.convocatoria-periodo-seguimiento-cientifico');
const CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_MES_FIN_KEY = marker('csp.convocatoria-seguimiento-cientifico.mes-final');
const CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_MES_INICIO_KEY = marker('csp.convocatoria-seguimiento-cientifico.mes-inicial');
const CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_NUMERO_PERIODO_KEY = marker('csp.convocatoria-seguimiento-cientifico.numero-periodo');
const CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_TIPO_SEGUIMIENTO_KEY = marker('csp.convocatoria-seguimiento-cientifico.tipo-seguimiento');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './convocatoria-seguimiento-cientifico-modal.component.html',
  styleUrls: ['./convocatoria-seguimiento-cientifico-modal.component.scss']
})
export class ConvocatoriaSeguimientoCientificoModalComponent
  extends BaseModalComponent<IConvocatoriaPeriodoSeguimientoCientifico, ConvocatoriaSeguimientoCientificoModalComponent> implements OnInit {

  fxLayoutProperties: FxLayoutProperties;

  FormGroupUtil = FormGroupUtil;
  textSaveOrUpdate: string;
  title: string;

  msgParamNumeroPeriodoEntity = {};
  msgParamMesFinEntity = {};
  msgParamMesInicioEntity = {};
  msgParamTipoSeguimiento = {};
  msgParamObservacionesEntity = {};

  get TIPO_SEGUIMIENTO_MAP() {
    return TIPO_SEGUIMIENTO_MAP;
  }

  constructor(
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: IConvocatoriaSeguimientoCientificoModalData,
    public matDialogRef: MatDialogRef<ConvocatoriaSeguimientoCientificoModalComponent>,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.convocatoriaSeguimientoCientifico);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data?.convocatoriaSeguimientoCientifico?.mesInicial ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_NUMERO_PERIODO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNumeroPeriodoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_MES_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMesInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_MES_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMesFinEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_MES_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMesInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_TIPO_SEGUIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoSeguimiento = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.convocatoriaSeguimientoCientifico?.numPeriodo) {
      this.translate.get(
        CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_KEY,
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
    const rangosPeriodosExistentes = this.data.convocatoriaSeguimientoCientificoList
      .filter(seguimientoCientifico => seguimientoCientifico.value?.mesInicial !== this.data.convocatoriaSeguimientoCientifico?.mesInicial)
      .map(seguimientoCientifico => {
        return {
          inicio: seguimientoCientifico.value.mesInicial,
          fin: seguimientoCientifico.value.mesFinal
        };
      });

    const periodoSeguimientoFinal = this.data.convocatoriaSeguimientoCientificoList
      .find(periodoSeguimiento => periodoSeguimiento.value.tipoSeguimiento === TipoSeguimiento.FINAL
        && periodoSeguimiento.value.mesInicial !== this.data.convocatoriaSeguimientoCientifico.mesInicial);

    const ultimoseguimientoCientificoNoFinal = this.data.convocatoriaSeguimientoCientificoList
      .filter(seguimientoCientifico => seguimientoCientifico.value.mesInicial !== this.data.convocatoriaSeguimientoCientifico.mesInicial)
      .sort((a, b) => (b.value.mesInicial > a.value.mesInicial) ? 1 : ((a.value.mesInicial > b.value.mesInicial) ? -1 : 0)).find(c => true);

    const formGroup = new FormGroup({
      numPeriodo: new FormControl({
        value: this.data.convocatoriaSeguimientoCientifico?.numPeriodo,
        disabled: true
      }),
      desdeMes: new FormControl(this.data.convocatoriaSeguimientoCientifico?.mesInicial, [Validators.required, Validators.min(1)]),
      hastaMes: new FormControl(this.data.convocatoriaSeguimientoCientifico?.mesFinal, [Validators.required, Validators.min(2)]),
      fechaInicio: new FormControl(this.data.convocatoriaSeguimientoCientifico?.fechaInicioPresentacion, []),
      fechaFin: new FormControl(this.data.convocatoriaSeguimientoCientifico?.fechaFinPresentacion, []),
      tipoSeguimiento: new FormControl(this.data.convocatoriaSeguimientoCientifico?.tipoSeguimiento, [Validators.required]),
      observaciones: new FormControl(this.data.convocatoriaSeguimientoCientifico?.observaciones, [Validators.maxLength(2000)])
    }, {
      validators: [
        this.isFinalUltimoPeriodo(ultimoseguimientoCientificoNoFinal?.value.mesFinal),
        NumberValidator.isAfter('desdeMes', 'hastaMes'),
        RangeValidator.notOverlaps('desdeMes', 'hastaMes', rangosPeriodosExistentes),
        DateValidator.isAfter('fechaInicio', 'fechaFin')]
    });

    // Si la convocatoria tiene duracion el mesFinal no puede superarla
    if (this.data.duracion) {
      formGroup.get('hastaMes').setValidators([
        Validators.max(this.data.duracion),
        formGroup.get('hastaMes').validator
      ]);
    }

    // Si ya existe un periodo final tiene que ser el ultimo y solo puede haber uno
    if (periodoSeguimientoFinal) {
      formGroup.get('tipoSeguimiento').setValidators([
        StringValidator.notIn([TipoSeguimiento.FINAL]),
        formGroup.get('tipoSeguimiento').validator
      ]);
      formGroup.get('desdeMes').setValidators([
        Validators.max(periodoSeguimientoFinal.value.mesInicial),
        formGroup.get('desdeMes').validator
      ]);
    }

    return formGroup;
  }

  protected getDatosForm(): IConvocatoriaPeriodoSeguimientoCientifico {
    const convocatoriaSeguimientoCientifico = this.data.convocatoriaSeguimientoCientifico;
    convocatoriaSeguimientoCientifico.numPeriodo = this.formGroup.get('numPeriodo').value;
    convocatoriaSeguimientoCientifico.mesInicial = this.formGroup.get('desdeMes').value;
    convocatoriaSeguimientoCientifico.mesFinal = this.formGroup.get('hastaMes').value;
    convocatoriaSeguimientoCientifico.fechaInicioPresentacion = this.formGroup.get('fechaInicio').value;
    convocatoriaSeguimientoCientifico.fechaFinPresentacion = this.formGroup.get('fechaFin').value;
    convocatoriaSeguimientoCientifico.tipoSeguimiento = this.formGroup.get('tipoSeguimiento').value;
    convocatoriaSeguimientoCientifico.observaciones = this.formGroup.get('observaciones').value;

    return convocatoriaSeguimientoCientifico;
  }

  /**
   * Recalcula el numero de periodo en funcion de la ordenacion por mes inicial de todos los periodos.
   */
  recalcularNumPeriodo(): void {
    let numPeriodo = 1;
    const mesInicial = this.formGroup.get('desdeMes').value;

    this.data.convocatoriaSeguimientoCientificoList.forEach(c => {
      if (mesInicial > c.value.mesInicial) {
        numPeriodo++;
      }
    });

    this.formGroup.get('numPeriodo').setValue(numPeriodo);
  }

  /**
   * Comprueba el mes sea el ultimo (empieza despues del ultimo periodo no final).
   *
   * @param mesFinUltimoPeriodoNoFinal Mes de fin del ultimo periodo que no es de tipo final.
   */
  private isFinalUltimoPeriodo(mesFinUltimoPeriodoNoFinal: number): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {
      const mesInicioControl = formGroup.controls.desdeMes;
      if (!mesFinUltimoPeriodoNoFinal || (mesInicioControl.errors && !mesInicioControl.errors.finalNotLast)) {
        return;
      }
    };
  }

}
