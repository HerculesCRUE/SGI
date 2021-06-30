import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoProrroga, TIPO_MAP } from '@core/models/csp/proyecto-prorroga';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ProyectoProrrogaActionService } from '../../proyecto-prorroga.action.service';
import { ProyectoProrrogaDatosGeneralesFragment } from './proyecto-prorroga-datos-generales.fragment';

const PROYECTO_PRORROGA_OBSERVACIONES = marker('csp.proyecto-prorroga.observaciones');
@Component({
  selector: 'sgi-solicitud-proyecto-prorroga-datos-generales',
  templateUrl: './proyecto-prorroga-datos-generales.component.html',
  styleUrls: ['./proyecto-prorroga-datos-generales.component.scss']
})
export class ProyectoProrrogaDatosGeneralesComponent extends FormFragmentComponent<IProyectoProrroga>
  implements OnInit, OnDestroy {
  formPart: ProyectoProrrogaDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  fxFlexProperties3: FxFlexProperties;
  private subscriptions: Subscription[] = [];
  periodoSeguimientosSelectedProyecto: IProyectoProrroga[] = [];
  FormGroupUtil = FormGroupUtil;
  msgParamOvservaciones = {};

  get TIPO_MAP() {
    return TIPO_MAP;
  }

  constructor(
    protected actionService: ProyectoProrrogaActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ProyectoProrrogaDatosGeneralesFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexProperties3 = new FxFlexProperties();
    this.fxFlexProperties3.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PRORROGA_OBSERVACIONES,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamOvservaciones = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }


}
