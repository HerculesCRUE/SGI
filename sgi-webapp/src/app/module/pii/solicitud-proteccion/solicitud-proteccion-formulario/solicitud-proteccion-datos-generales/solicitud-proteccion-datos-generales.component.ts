import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { SolicitudProteccionActionService } from '../../solicitud-proteccion.action.service';
import { SolicitudProteccionDatosGeneralesFragment } from './solicitud-proteccion-datos-generales.fragment';

const MSG_ERROR_INIT = marker('error.load');
const SOLICITUD_PROTECCION_TITULO_KEY = marker('pii.solicitud-proteccion.titulo');
const SOLICITUD_PROTECCION_VIA_PROTECCION_KEY = marker('pii.solicitud-proteccion.via-proteccion');
const SOLICITUD_PROTECCION_PAIS_KEY = marker('pii.solicitud-proteccion.pais');
const SOLICITUD_PROTECCION_FECHA_PRIORIDAD_KEY = marker('pii.solicitud-proteccion.fecha-prioridad');
const SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_KEY = marker('pii.solicitud-proteccion.fecha-fin-prioridad-form');
const SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_PCT_KEY = marker('pii.solicitud-proteccion.fecha-fin-prioridad-pct');
const SOLICITUD_PROTECCION_NUMERO_SOLICITUD_KEY = marker('pii.solicitud-proteccion.numero-solicitud-form');
const SOLICITUD_PROTECCION_COMENTARIOS_KEY = marker('pii.solicitud-proteccion.comentarios');
const SOLICITUD_PROTECCION_NUMERO_REGISTRO_KEY = marker('pii.solicitud-proteccion.numero-registro-form');
const SOLICITUD_PROTECCION_FECHA_SOLICITUD_KEY = marker('pii.solicitud-proteccion.fecha-solicitud');

@Component({
  selector: 'sgi-solicitud-proteccion-datos-generales',
  templateUrl: './solicitud-proteccion-datos-generales.component.html',
  styleUrls: ['./solicitud-proteccion-datos-generales.component.scss']
})
export class SolicitudProteccionDatosGeneralesComponent extends FormFragmentComponent<ISolicitudProteccion> implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  formPart: SolicitudProteccionDatosGeneralesFragment;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexProperties33: FxFlexProperties;
  fxFlexProperties66: FxFlexProperties;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamTituloEntity = {};
  msgParamPaisEntity = {};
  msgParamViaProteccionEntity = {};
  msgParamFechaPrioridadEntity = {};
  msgParamFechaFinPrioridadEntity = {};
  msgParamFechaFinPrioridadPCTEntity = {};
  msgParamNumeroSolicitudEntity = {};
  msgParamComentariosEntity = {};
  msgParamNumeroRegistroEntity = {};
  msgParamFechaSolicitudEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get suitableMsgParamEntityFechaPrioridad() {
    return this.formPart.tipoPropiedad === TipoPropiedad.INDUSTRIAL && !this.formPart.solicitudesBefore
      ? this.msgParamFechaPrioridadEntity
      : this.msgParamFechaSolicitudEntity;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected actionService: SolicitudProteccionActionService,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SolicitudProteccionDatosGeneralesFragment;
    this.initFlexProperties();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private initFlexProperties(): void {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'column';

    this.fxFlexProperties33 = new FxFlexProperties();
    this.fxFlexProperties33.sm = '0 1 calc(33%-10px)';
    this.fxFlexProperties33.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties33.gtMd = '0 1 calc(33%-10px)';
    this.fxFlexProperties33.order = '2';

    this.fxFlexProperties66 = new FxFlexProperties();
    this.fxFlexProperties66.sm = '0 1 calc(66%-10px)';
    this.fxFlexProperties66.md = '0 1 calc(66%-10px)';
    this.fxFlexProperties66.gtMd = '0 1 calc(66%-10px)';
    this.fxFlexProperties66.order = '1';
  }

  private setupI18N(): void {

    this.translate.get(
      SOLICITUD_PROTECCION_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamViaProteccionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_PAIS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPaisEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_FECHA_PRIORIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaPrioridadEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaFinPrioridadEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_PCT_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaFinPrioridadPCTEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_NUMERO_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamNumeroSolicitudEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_COMENTARIOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentariosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SOLICITUD_PROTECCION_NUMERO_REGISTRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamNumeroRegistroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_FECHA_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaSolicitudEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }
}
