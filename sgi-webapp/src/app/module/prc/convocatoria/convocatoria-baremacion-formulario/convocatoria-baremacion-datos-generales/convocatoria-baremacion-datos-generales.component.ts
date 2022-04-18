import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { ConvocatoriaBaremacionActionService } from '../../convocatoria-baremacion.action.service';
import { ConvocatoriaBaremacionDatosGeneralesFragment } from './convocatoria-baremacion-datos-generales.fragment';

const CONVOCATORIA_BAREMACION_NOMBRE_KEY = marker('prc.convocatoria.nombre');
const CONVOCATORIA_BAREMACION_ANIO_KEY = marker('prc.convocatoria.anio');
const CONVOCATORIA_BAREMACION_ANIOS_BAREMABLES_KEY = marker('prc.convocatoria.anios-baremables');
const CONVOCATORIA_BAREMACION_ULTIMO_ANIO_KEY = marker('prc.convocatoria.ultimo-anio');
const CONVOCATORIA_BAREMACION_IMPORTE_TOTAL_KEY = marker('prc.convocatoria.importe-total');
const CONVOCATORIA_BAREMACION_PARTIDA_PRESUPUESTARIA_KEY = marker('prc.convocatoria.partida-presupuestaria');

@Component({
  selector: 'sgi-convocatoria-baremacion-datos-generales',
  templateUrl: './convocatoria-baremacion-datos-generales.component.html',
  styleUrls: ['./convocatoria-baremacion-datos-generales.component.scss']
})
export class ConvocatoriaBaremacionDatosGeneralesComponent
  extends FormFragmentComponent<IConvocatoriaBaremacion> implements OnInit {

  fxLayoutProperties: FxLayoutProperties;
  formPart: ConvocatoriaBaremacionDatosGeneralesFragment;

  msgParamNombreEntity = {};
  msgParamAnioEntity = {};
  msgParamAniosBaremablesEntity = {};
  msgParamUltimoAnioEntity = {};
  msgParamImporteTotalEntity = {};
  msgParamPartidaPresupuestariaEntity = {};

  constructor(
    public readonly actionService: ConvocatoriaBaremacionActionService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ConvocatoriaBaremacionDatosGeneralesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initFlexProperties();
    this.setupI18N();
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_BAREMACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      CONVOCATORIA_BAREMACION_ANIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAnioEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      CONVOCATORIA_BAREMACION_ANIOS_BAREMABLES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAniosBaremablesEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL
    });

    this.translate.get(
      CONVOCATORIA_BAREMACION_ULTIMO_ANIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUltimoAnioEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      CONVOCATORIA_BAREMACION_IMPORTE_TOTAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImporteTotalEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      CONVOCATORIA_BAREMACION_PARTIDA_PRESUPUESTARIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPartidaPresupuestariaEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
  }
}
