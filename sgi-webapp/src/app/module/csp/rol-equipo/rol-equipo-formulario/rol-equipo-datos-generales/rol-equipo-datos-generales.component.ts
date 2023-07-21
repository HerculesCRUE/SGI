import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRolProyecto, EQUIPO_MAP, ORDEN_MAP } from '@core/models/csp/rol-proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { RolEquipoActionService } from '../../rol-equipo.action.service';

const ROL_EQUIPO_NOMBRE_KEY = marker('csp.rol-equipo.nombre');
const ROL_EQUIPO_ABREVIATURA_KEY = marker('csp.rol-equipo.abreviatura');
const ROL_EQUIPO_EQUIPO_KEY = marker('csp.rol-equipo.equipo');
const ROL_EQUIPO_PRINCIPAL_KEY = marker('csp.rol-equipo.principal');
const ROL_EQUIPO_BAREMABLEPRC_KEY = marker('csp.rol-equipo.baremablePRC');
const ROL_EQUIPO_DESCRIPCION_KEY = marker('csp.rol-equipo.descripcion');

@Component({
  selector: 'sgi-rol-equipo-datos-generales',
  templateUrl: './rol-equipo-datos-generales.component.html',
  styleUrls: ['./rol-equipo-datos-generales.component.scss']
})
export class RolEquipoDatosGeneralesComponent extends FormFragmentComponent<IRolProyecto> {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamNombreEntity = {};
  msgParamAbreviaturaEntity = {};
  msgParamEquipoEntity = {};
  msgParamPrincipalEntity = {};
  msgParamBaremablePRCEntity = {};
  msgParamDescripcionEntity = {};

  get EQUIPO_MAP() {
    return EQUIPO_MAP;
  }

  get ORDEN_MAP() {
    return ORDEN_MAP;
  }

  constructor(
    readonly actionService: RolEquipoActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '4';

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
      ROL_EQUIPO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ROL_EQUIPO_ABREVIATURA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAbreviaturaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ROL_EQUIPO_EQUIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEquipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ROL_EQUIPO_PRINCIPAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPrincipalEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ROL_EQUIPO_BAREMABLEPRC_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamBaremablePRCEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ROL_EQUIPO_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

}