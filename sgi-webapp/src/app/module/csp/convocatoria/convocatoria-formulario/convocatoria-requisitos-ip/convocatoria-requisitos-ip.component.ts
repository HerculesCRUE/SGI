import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaRequisitosIPFragment } from './convocatoria-requisitos-ip.fragment';


const CONVOCATORIA_REQUISITOS_IP_MODALIDAD_CONTRATOS_KEY = marker('csp.convocatoria-requisito-ip.modalidad-contratos');
const CONVOCATORIA_REQUISITOS_IP_NIVEL_ACADEMICO_KEY = marker('csp.convocatoria-requisito-ip.nivel-academico');

@Component({
  selector: 'sgi-convocatoria-requisitos-ip',
  templateUrl: './convocatoria-requisitos-ip.component.html',
  styleUrls: ['./convocatoria-requisitos-ip.component.scss']
})

export class ConvocatoriaRequisitosIPComponent extends FormFragmentComponent<IConvocatoriaRequisitoIP> implements OnInit {
  formPart: ConvocatoriaRequisitosIPFragment;
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  msgParamModalidadContratoEntity = {};
  msgParamNivelAcademicoEntity = {};

  constructor(
    protected actionService: ConvocatoriaActionService,
    public translate: TranslateService
  ) {
    super(actionService.FRAGMENT.REQUISITOS_IP, actionService);
    this.formPart = this.fragment as ConvocatoriaRequisitosIPFragment;

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
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_REQUISITOS_IP_MODALIDAD_CONTRATOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamModalidadContratoEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_REQUISITOS_IP_NIVEL_ACADEMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNivelAcademicoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }


}
