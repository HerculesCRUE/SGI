import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP, ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { SolicitudProteccionActionService } from '../../solicitud-proteccion.action.service';
import { SolicitudProteccionDatosGeneralesFragment } from './solicitud-proteccion-datos-generales.fragment';

const SOLICITUD_PROTECCION_TITULO_KEY = marker('pii.solicitud-proteccion.titulo');
const SOLICITUD_PROTECCION_VIA_PROTECCION_KEY = marker('pii.solicitud-proteccion.via-proteccion');
const SOLICITUD_PROTECCION_PAIS_KEY = marker('pii.solicitud-proteccion.pais');
const SOLICITUD_PROTECCION_FECHA_PRIORIDAD_KEY = marker('pii.solicitud-proteccion.fecha-prioridad');
const SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_KEY = marker('pii.solicitud-proteccion.fecha-fin-prioridad-form');
const SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_PCT_KEY = marker('pii.solicitud-proteccion.fecha-fin-prioridad-pct');
const SOLICITUD_PROTECCION_NUMERO_SOLICITUD_KEY = marker('pii.solicitud-proteccion.numero-solicitud-form');
const SOLICITUD_PROTECCION_NUMERO_PUBLICACION_KEY = marker('pii.solicitud-proteccion.numero-publicacion-form');
const SOLICITUD_PROTECCION_FECHA_PUBLICACION_KEY = marker('pii.solicitud-proteccion.fecha-publicacion-form');
const SOLICITUD_PROTECCION_NUMERO_CONCESION_KEY = marker('pii.solicitud-proteccion.numero-concesion-form');
const SOLICITUD_PROTECCION_FECHA_CONCESION_KEY = marker('pii.solicitud-proteccion.fecha-concesion-form');
const SOLICITUD_PROTECCION_FECHA_CADUCID_KEY = marker('pii.solicitud-proteccion.fecha-caducidad-form');
const SOLICITUD_PROTECCION_TIPO_CADUCIDAD_KEY = marker('pii.solicitud-proteccion.tipo-caducidad-form');
const SOLICITUD_PROTECCION_COMENTARIOS_KEY = marker('pii.solicitud-proteccion.comentarios');
const SOLICITUD_PROTECCION_NUMERO_REGISTRO_KEY = marker('pii.solicitud-proteccion.numero-registro-form');
const SOLICITUD_PROTECCION_FECHA_SOLICITUD_KEY = marker('pii.solicitud-proteccion.fecha-solicitud');
const SOLICITUD_PROTECCION_ESTADO_KEY = marker('pii.solicitud-proteccion.estado');

@Component({
  selector: 'sgi-solicitud-proteccion-datos-generales',
  templateUrl: './solicitud-proteccion-datos-generales.component.html',
  styleUrls: ['./solicitud-proteccion-datos-generales.component.scss']
})
export class SolicitudProteccionDatosGeneralesComponent extends FormFragmentComponent<ISolicitudProteccion> implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  FormGroupUtil = FormGroupUtil;
  Estado = Estado;

  formPart: SolicitudProteccionDatosGeneralesFragment;

  msgParamTituloEntity = {};
  msgParamPaisEntity = {};
  msgParamViaProteccionEntity = {};
  msgParamFechaPrioridadEntity = {};
  msgParamFechaFinPrioridadEntity = {};
  msgParamFechaFinPrioridadPCTEntity = {};
  msgParamNumeroSolicitudEntity = {};
  msgParamNumeroPublicacionEntity = {};
  msgParamFechaPublicacionEntity = {};
  msgParamNumeroConcesionEntity = {};
  msgParamFechaConcesionEntity = {};
  msgParamEstadoEntity = {};
  msgParamFechaCaducidEntity = {};
  msgParamTipoCaducidadEntity = {};
  msgParamComentariosEntity = {};
  msgParamNumeroRegistroEntity = {};
  msgParamFechaSolicitudEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get suitableMsgParamEntityFechaPrioridad() {
    return this.formPart.tipoPropiedad === TipoPropiedad.INDUSTRIAL && !this.formPart.solicitudesBefore
      ? this.msgParamFechaPrioridadEntity
      : this.msgParamFechaSolicitudEntity;
  }

  constructor(
    protected actionService: SolicitudProteccionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SolicitudProteccionDatosGeneralesFragment;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  public displayTextTipoCaducidad(tipoCaducidad: ITipoCaducidad) {
    return tipoCaducidad?.descripcion ?? '';
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
      SOLICITUD_PROTECCION_NUMERO_PUBLICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamNumeroPublicacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_FECHA_PUBLICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaPublicacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_NUMERO_CONCESION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamNumeroConcesionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_FECHA_CONCESION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaConcesionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_FECHA_CADUCID_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaCaducidEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_TIPO_CADUCIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTipoCaducidadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROTECCION_ESTADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamEstadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

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
