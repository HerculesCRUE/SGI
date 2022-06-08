import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ProyectoSocioPeriodoJustificacionActionService } from '../../proyecto-socio-periodo-justificacion.action.service';
import { ProyectoSocioPeriodoJustificacionDatosGeneralesFragment } from './proyecto-socio-periodo-justificacion-datos-generales.fragment';

const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FECHA_FIN_KEY = marker('csp.proyecto-socio-periodo-justificacion.fecha-fin');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FECHA_FIN_PRESENTACION_KEY = marker('csp.proyecto-socio-periodo-justificacion.fecha-fin-presentacion');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FECHA_INICIO_KEY = marker('csp.proyecto-socio-periodo-justificacion.fecha-inicio');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY = marker('csp.proyecto-socio-periodo-justificacion.fecha-inicio-presentacion');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_OBSERVACIONES_KEY = marker('csp.proyecto-socio-periodo-justificacion.observaciones');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_IMPORTE_JUSTIFICADO_KEY = marker('csp.proyecto-socio-periodo-justificacion.importe-justificado');

@Component({
  selector: 'sgi-proyecto-socio-periodo-justificacion-datos-generales',
  templateUrl: './proyecto-socio-periodo-justificacion-datos-generales.component.html',
  styleUrls: ['./proyecto-socio-periodo-justificacion-datos-generales.component.scss']
})
export class ProyectoSocioPeriodoJustificacionDatosGeneralesComponent extends
  FormFragmentComponent<IProyectoSocioPeriodoJustificacion> implements OnInit, OnDestroy {
  formPart: ProyectoSocioPeriodoJustificacionDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  private subscriptions: Subscription[] = [];

  msgParamFechaFin = {};
  msgParamFechaFinPresentacion = {};
  msgParamFechaInicio = {};
  msgParamFechaInicioPresentacion = {};
  msgParamObservacionesEntity = {};
  msgParamImporteJustificadoEntity = {};

  constructor(
    protected actionService: ProyectoSocioPeriodoJustificacionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ProyectoSocioPeriodoJustificacionDatosGeneralesFragment;
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
    this.setupI18N();
  }


  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFin = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicio = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FECHA_FIN_PRESENTACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaFinPresentacion = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaInicioPresentacion = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamObservacionesEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL }
    );

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_IMPORTE_JUSTIFICADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamImporteJustificadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
