import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaRequisitosEquipoFragment } from './convocatoria-requisitos-equipo.fragment';

const CONVOCATORIA_REQUISITOS_EQUIPO_MODALIDAD_CONTRATO_KEY = marker('csp.convocatoria-requisito-equipo.modalidad-contrato');
const CONVOCATORIA_REQUISITOS_EQUIPO_NIVEL_ACADEMICO_KEY = marker('csp.convocatoria-requisito-equipo.nivel-academico');

@Component({
  selector: 'sgi-convocatoria-requisitos-equipo',
  templateUrl: './convocatoria-requisitos-equipo.component.html',
  styleUrls: ['./convocatoria-requisitos-equipo.component.scss']
})
export class ConvocatoriaRequisitosEquipoComponent extends FormFragmentComponent<IConvocatoriaRequisitoEquipo> implements OnInit {
  formPart: ConvocatoriaRequisitosEquipoFragment;
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
    super(actionService.FRAGMENT.REQUISITOS_EQUIPO, actionService);
    this.formPart = this.fragment as ConvocatoriaRequisitosEquipoFragment;

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
      CONVOCATORIA_REQUISITOS_EQUIPO_MODALIDAD_CONTRATO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamModalidadContratoEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_REQUISITOS_EQUIPO_NIVEL_ACADEMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNivelAcademicoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }
}
