import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Subscription } from 'rxjs';
import { ConvocatoriaConceptoGastoPublicActionService } from '../../convocatoria-concepto-gasto-public.action.service';
import { ConvocatoriaConceptoGastoDatosGeneralesPublicFragment } from './convocatoria-concepto-gasto-datos-generales-public.fragment';

@Component({
  templateUrl: './convocatoria-concepto-gasto-datos-generales-public.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-datos-generales-public.component.scss']
})
export class ConvocatoriaConceptoGastoDatosGeneralesPublicComponent
  extends FormFragmentComponent<IConvocatoriaConceptoGasto> implements OnInit, OnDestroy {
  formPart: ConvocatoriaConceptoGastoDatosGeneralesPublicFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;

  private subscriptions: Subscription[] = [];

  constructor(
    public readonly actionService: ConvocatoriaConceptoGastoPublicActionService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ConvocatoriaConceptoGastoDatosGeneralesPublicFragment;
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
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(subscription => subscription.unsubscribe());
  }

}
