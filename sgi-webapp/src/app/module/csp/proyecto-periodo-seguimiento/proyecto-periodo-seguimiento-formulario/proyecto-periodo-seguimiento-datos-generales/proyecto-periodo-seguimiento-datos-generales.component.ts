import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { TIPO_SEGUIMIENTO_MAP } from '@core/enums/tipo-seguimiento';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { merge, Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ProyectoPeriodoSeguimientoActionService } from '../../proyecto-periodo-seguimiento.action.service';
import { ProyectoPeriodoSeguimientoDatosGeneralesFragment } from './proyecto-periodo-seguimiento-datos-generales.fragment';

const PERIODO_SEGUIMIENTO_CIENTIFICO_OBSERVACIONES_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.observaciones');
const PERIODO_SEGUIMIENTO_CIENTIFICO_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico');
const PERIODO_SEGUIMIENTO_CIENTIFICO_TIPO_SEGUIMIENTO_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.tipo-seguimiento');

@Component({
  selector: 'sgi-solicitud-proyecto-periodo-seguimiento-datos-generales',
  templateUrl: './proyecto-periodo-seguimiento-datos-generales.component.html',
  styleUrls: ['./proyecto-periodo-seguimiento-datos-generales.component.scss']
})
export class ProyectoPeriodoSeguimientoDatosGeneralesComponent extends FormFragmentComponent<IProyectoPeriodoSeguimiento>
  implements OnInit, OnDestroy {
  formPart: ProyectoPeriodoSeguimientoDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  private subscriptions: Subscription[] = [];
  FormGroupUtil = FormGroupUtil;

  msgParamEntity = {};
  msgParamObservacionesEntity = {};
  msgParamTipoSeguimiento = {};

  get TIPO_SEGUIMIENTO_MAP() {
    return TIPO_SEGUIMIENTO_MAP;
  }

  constructor(
    protected actionService: ProyectoPeriodoSeguimientoActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ProyectoPeriodoSeguimientoDatosGeneralesFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(50%-10px)';
    this.fxFlexProperties.order = '2';

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

    this.subscriptions.push(
      merge(
        this.formGroup.controls.fechaInicio.valueChanges,
        this.formGroup.controls.fechaFin.valueChanges
      ).pipe(
        tap(() => this.checkOverlapsPeriodosSeguimiento())
      ).subscribe()
    );
  }

  copyToProyecto(): void {
    this.formPart.enableEditableControls();
    this.formPart.getFormGroup().controls.numPeriodo.setValue(this.formPart.getFormGroup().controls.numPeriodoConvocatoria.value);
    this.formPart.getFormGroup().controls.fechaInicio.setValue(this.formPart.getFormGroup().controls.fechaInicioConvocatoria.value);
    this.formPart.getFormGroup().controls.fechaFin.setValue(this.formPart.getFormGroup().controls.fechaFinConvocatoria.value);
    this.formPart.getFormGroup().controls.fechaInicioPresentacion.setValue(
      this.formPart.getFormGroup().controls.fechaInicioPresentacionConvocatoria.value);
    this.formPart.getFormGroup().controls.fechaFinPresentacion.setValue(
      this.formPart.getFormGroup().controls.fechaFinPresentacionConvocatoria.value);
    this.formPart.getFormGroup().controls.observaciones.setValue(this.formPart.getFormGroup().controls.observacionesConvocatoria.value);
    this.formPart.getFormGroup().controls.tipoSeguimiento.setValue(this.formPart.getFormGroup().controls.tipoSeguimientoConvocatoria.value);
    this.formGroup.markAllAsTouched();
  }

  private setupI18N(): void {
    this.translate.get(
      PERIODO_SEGUIMIENTO_CIENTIFICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PERIODO_SEGUIMIENTO_CIENTIFICO_TIPO_SEGUIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoSeguimiento = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PERIODO_SEGUIMIENTO_CIENTIFICO_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private checkOverlapsPeriodosSeguimiento(): void {
    const fechaInicioForm = this.formGroup.get('fechaInicio');
    const fechaFinForm = this.formGroup.get('fechaFin');

    const fechaInicio = fechaInicioForm.value ? fechaInicioForm.value.toMillis() : Number.MIN_VALUE;
    const fechaFin = fechaFinForm.value ? fechaFinForm.value.toMillis() : Number.MAX_VALUE;

    const ranges = this.formPart.proyectoPeriodosSeguimiento
      .map(periodo => {
        return {
          inicio: periodo.fechaInicio ? periodo.fechaInicio.toMillis() : Number.MIN_VALUE,
          fin: periodo.fechaFin ? periodo.fechaFin.toMillis() : Number.MAX_VALUE
        };
      });

    if (ranges.some(r => fechaInicio <= r.fin && r.inicio <= fechaFin)) {
      if (fechaInicioForm.value) {
        fechaInicioForm.setErrors({ overlapped: true });
        fechaInicioForm.markAsTouched({ onlySelf: true });
      }

      if (fechaFinForm.value) {
        fechaFinForm.setErrors({ overlapped: true });
        fechaFinForm.markAsTouched({ onlySelf: true });
      }

    } else {
      if (fechaInicioForm.errors) {
        delete fechaInicioForm.errors.overlapped;
        fechaInicioForm.updateValueAndValidity({ onlySelf: true });
      }

      if (fechaFinForm.errors) {
        delete fechaFinForm.errors.overlapped;
        fechaFinForm.updateValueAndValidity({ onlySelf: true });
      }

    }
  }
}
