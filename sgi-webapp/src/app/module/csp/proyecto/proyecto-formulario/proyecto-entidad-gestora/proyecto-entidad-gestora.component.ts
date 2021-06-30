import { Component, OnInit } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoEntidadGestoraFragment } from './proyecto-entidad-gestora.fragment';
@Component({
  selector: 'sgi-proyecto-entidad-gestora',
  templateUrl: './proyecto-entidad-gestora.component.html',
  styleUrls: ['./proyecto-entidad-gestora.component.scss']
})
export class ProyectoEntidadGestoraComponent extends FormFragmentComponent<IProyectoEntidadGestora> implements OnInit {

  formPart: ProyectoEntidadGestoraFragment;
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  // TODO: Cambiar cuando este definido
  entidades =
    [
      { label: 'Entidad', value: 1 },
      { label: 'Subentidad', value: 2 }
    ];

  constructor(
    actionService: ProyectoActionService
  ) {
    super(actionService.FRAGMENT.ENTIDAD_GESTORA, actionService);
    this.formPart = this.fragment as ProyectoEntidadGestoraFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '1';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '4';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    super.ngOnInit();
  }
}
