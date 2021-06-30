import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { ModeloEjecucionActionService } from '../../modelo-ejecucion.action.service';

const MODELO_EJECUCION_NOMBRE_KEY = marker('csp.modelo-ejecucion.nombre');

@Component({
  selector: 'sgi-modelo-ejecucion-datos-generales',
  templateUrl: './modelo-ejecucion-datos-generales.component.html',
  styleUrls: ['./modelo-ejecucion-datos-generales.component.scss']
})
export class ModeloEjecucionDatosGeneralesComponent extends FormFragmentComponent<IModeloEjecucion> {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  key: number;

  msgParamNombreEntity = {};

  constructor(
    readonly actionService: ModeloEjecucionActionService,
    private readonly translate: TranslateService

  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);

    this.key = this.fragment.getKey() as number;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

}
