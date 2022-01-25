import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ConvocatoriaConceptoGastoActionService } from '../../convocatoria-concepto-gasto.action.service';
import { ConvocatoriaConceptoGastoDatosGeneralesFragment } from './convocatoria-concepto-gasto-datos-generales.fragment';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_ELEGIBILIDAD_CONCEPTO_GASTO_KEY = marker('csp.convocatoria-elegibilidad.concepto-gasto.concepto-gasto');
const CONVOCATORIA_ELEGIBILIDAD_GASTO_COSTES_INDIRECTOS_KEY = marker('csp.convocatoria-elegibilidad-concepto-gasto.costes-indirectos');

@Component({
  templateUrl: './convocatoria-concepto-gasto-datos-generales.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-datos-generales.component.scss']
})
export class ConvocatoriaConceptoGastoDatosGeneralesComponent
  extends FormFragmentComponent<IConvocatoriaConceptoGasto> implements OnInit, OnDestroy {
  formPart: ConvocatoriaConceptoGastoDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;

  private subscriptions: Subscription[] = [];

  textSaveOrUpdate: string;

  msgParamConceptoGastoEntity = {};
  msgParamCostesIndirectos = {};

  constructor(
    public readonly actionService: ConvocatoriaConceptoGastoActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ConvocatoriaConceptoGastoDatosGeneralesFragment;
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.fragment.getKey() ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_ELEGIBILIDAD_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConceptoGastoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONVOCATORIA_ELEGIBILIDAD_GASTO_COSTES_INDIRECTOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCostesIndirectos = { entity: value, ...MSG_PARAMS.GENDER.MALE });

  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(subscription => subscription.unsubscribe());
  }

}
