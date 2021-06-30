import { Component, OnInit } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IEntidad } from '@core/models/csp/entidad';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { NGXLogger } from 'ngx-logger';
import { SolicitudProyectoPresupuestoActionService } from '../../solicitud-proyecto-presupuesto.action.service';
import { SolicitudProyectoPresupuestoDatosGeneralesFragment } from './solicitud-proyecto-presupuesto-datos-generales.fragment';

@Component({
  selector: 'sgi-solicitud-proyecto-presupuesto-datos-generales',
  templateUrl: './solicitud-proyecto-presupuesto-datos-generales.component.html',
  styleUrls: ['./solicitud-proyecto-presupuesto-datos-generales.component.scss']
})
export class SolicitudProyectoPresupuestoDatosGeneralesComponent extends FormFragmentComponent<IEntidad>
  implements OnInit {
  formPart: SolicitudProyectoPresupuestoDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;


  constructor(
    protected logger: NGXLogger,
    protected actionService: SolicitudProyectoPresupuestoActionService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SolicitudProyectoPresupuestoDatosGeneralesFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

}
