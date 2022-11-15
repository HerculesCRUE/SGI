import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FormFragmentComponent, FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IMemoria } from '@core/models/eti/memoria';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EvaluacionFormularioActionService } from '../evaluacion-formulario.action.service';
import {
  EvaluacionListadoAnteriorMemoriaComponent
} from '../evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { EvaluacionDatosMemoriaFragment } from './evaluacion-datos-memoria.fragment';

@Component({
  selector: 'sgi-evaluacion-datos-memoria',
  templateUrl: './evaluacion-datos-memoria.component.html',
  styleUrls: ['./evaluacion-datos-memoria.component.scss']
})
export class EvaluacionDatosMemoriaComponent extends FormFragmentComponent<IMemoria> implements AfterViewInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  @ViewChild('evaluaciones') evaluaciones: EvaluacionListadoAnteriorMemoriaComponent;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  formPart: EvaluacionDatosMemoriaFragment;

  constructor(
    public actionService: EvaluacionFormularioActionService
  ) {
    super(actionService.FRAGMENT.MEMORIA, actionService);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as EvaluacionDatosMemoriaFragment;
  }

  ngAfterViewInit(): void {
    this.evaluaciones.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.evaluaciones.evaluacionId = this.actionService.getEvaluacion()?.id;
    this.evaluaciones.ngAfterViewInit();
  }
}
