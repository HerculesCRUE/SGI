import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { ProyectoConceptoGastoActionService } from '../../proyecto-concepto-gasto.action.service';
import { ProyectoConceptoGastoDatosGeneralesFragment } from './proyecto-concepto-gasto-datos-generales.fragment';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_CONCEPTO_GASTO_KEY = marker('csp.proyecto-concepto-gasto');
const PROYECTO_CONCEPTO_GASTO_COSTES_INDIRECTOS_KEY = marker('csp.proyecto-concepto-gasto.costes-indirectos');


@Component({
  templateUrl: './proyecto-concepto-gasto-datos-generales.component.html',
  styleUrls: ['./proyecto-concepto-gasto-datos-generales.component.scss']
})
export class ProyectoConceptoGastoDatosGeneralesComponent
  extends FormFragmentComponent<IProyectoConceptoGasto> implements OnInit, OnDestroy {
  formPart: ProyectoConceptoGastoDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;

  private subscriptions: Subscription[] = [];
  conceptosGasto$: Observable<IConceptoGasto[]>;

  textSaveOrUpdate: string;

  msgParamConceptoGastoEntity = {};
  msgParamCostesIndirectos = {};

  textoToolTip: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    conceptoGastoService: ConceptoGastoService,
    public readonly actionService: ProyectoConceptoGastoActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ProyectoConceptoGastoDatosGeneralesFragment;
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';

    this.conceptosGasto$ = conceptoGastoService.findAll().pipe(
      map(response => response.items)
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.fragment.getKey() ? MSG_ACEPTAR : MSG_ANADIR;
  }

  copyToProyecto(): void {
    this.formPart.getFormGroup().controls.conceptoGasto.setValue(this.formPart.getFormGroup().controls.conceptoGastoConvocatoria.value);

    if (this.actionService.permitido) {
      this.formPart.getFormGroup().controls.importeMaximo.setValue(this.formPart.getFormGroup().controls.importeMaximoConvocatoria.value);
    }

    this.formPart.getFormGroup().controls.fechaInicio.setValue(this.formPart.getFormGroup().controls.fechaInicioConvocatoria.value);
    this.formPart.getFormGroup().controls.fechaFin.setValue(this.formPart.getFormGroup().controls.fechaFinConvocatoria.value);
    this.formPart.getFormGroup().controls.observaciones.setValue(this.formPart.getFormGroup().controls.observacionesConvocatoria.value);

    this.formPart.getFormGroup().controls.costesIndirectos.setValue(
      this.formPart.getFormGroup().controls.costesIndirectosConvocatoria.value);
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConceptoGastoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_COSTES_INDIRECTOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConceptoGastoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });


  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(subscription => subscription.unsubscribe());
  }

}
