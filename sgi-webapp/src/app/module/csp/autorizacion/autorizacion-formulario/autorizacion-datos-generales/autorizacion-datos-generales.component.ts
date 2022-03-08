import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { ESTADO_MAP } from '@core/models/csp/estado-autorizacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable } from 'rxjs';
import { AutorizacionActionService } from '../../autorizacion.action.service';
import { AutorizacionDatosGeneralesFragment, IAutorizacionDatosGeneralesData } from './autorizacion-datos-generales.fragment';

const AUTORIZACION_KEY = marker('csp.autorizacion');
const AUTORIZACION_TITULO_PROYECTO_KEY = marker('csp.autorizacion.titulo-proyecto');
const AUTORIZACION_DATOS_CONVOCATORIA_KEY = marker('csp.autorizacion.datos-convocatoria');
const AUTORIZACION_DATOS_ENTIDAD_KEY = marker('csp.autorizacion.datos-entidad');
const AUTORIZACION_INVESTIGADOR_PRINCIPAL_KEY = marker('csp.autorizacion.investigador-principal');
const AUTORIZACION_DATOS_IP_PROYECTO_KEY = marker('csp.autorizacion.datos-investigador-principal');
const AUTORIZACION_ENTIDAD_PARTICIPA_KEY = marker('csp.autorizacion.entidad-participa');
const AUTORIZACION_ESTADO_KEY = marker('csp.autorizacion.estado');


@Component({
  selector: 'sgi-autorizacion-datos-generales',
  templateUrl: './autorizacion-datos-generales.component.html',
  styleUrls: ['./autorizacion-datos-generales.component.scss']
})
export class AutorizacionDatosGeneralesComponent extends FormFragmentComponent<IAutorizacionDatosGeneralesData> implements OnInit {
  formPart: AutorizacionDatosGeneralesFragment;

  autorizaciones$: Observable<IAutorizacion[]>;

  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  msgParamAutorizacionEntity = {};
  msgParamTituloEntity = {};
  msgParamDatosConvocatoriaEntity = {};
  msgParamDatosEntidadEntity = {};
  msgParamObservacionesEntity = {};
  msgParamInvestigadorPrincipalEntity = {};
  msgParamDatosIpProyectoEntity = {};
  msgParamHorasDedicacionEntity = {};
  msgParamConvocatoriaEntity = {};
  msgParamEstadoEntity = {};
  msgParamEntidadParticipaEntity = {};

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    protected actionService: AutorizacionActionService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as AutorizacionDatosGeneralesFragment;

    this.fxFlexProperties2 = new FxFlexProperties();
    this.fxFlexProperties2.sm = '0 1 calc(72%)';
    this.fxFlexProperties2.md = '0 1 calc(66%)';
    this.fxFlexProperties2.gtMd = '0 1 calc(64%)';
    this.fxFlexProperties2.order = '2';

    this.fxFlexPropertiesEntidad = new FxFlexProperties();
    this.fxFlexPropertiesEntidad.sm = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.md = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.gtMd = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.order = '3';

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

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

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

  }

  private setupI18N(): void {

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAutorizacionEntity = { entity: value });

    this.translate.get(
      AUTORIZACION_TITULO_PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      AUTORIZACION_DATOS_CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamDatosConvocatoriaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      AUTORIZACION_DATOS_ENTIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamDatosEntidadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      AUTORIZACION_INVESTIGADOR_PRINCIPAL_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamInvestigadorPrincipalEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      AUTORIZACION_DATOS_IP_PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamDatosIpProyectoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      AUTORIZACION_ESTADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEstadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      AUTORIZACION_ENTIDAD_PARTICIPA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntidadParticipaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

  }
}
