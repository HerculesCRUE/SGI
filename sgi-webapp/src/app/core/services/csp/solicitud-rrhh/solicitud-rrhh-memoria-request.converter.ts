import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhMemoriaRequest } from './solicitud-rrhh-memoria-request';

class SolicitudRrhhMemoriaRequestConverter
  extends SgiBaseConverter<ISolicitudRrhhMemoriaRequest, ISolicitudRrhhMemoria> {
  toTarget(value: ISolicitudRrhhMemoriaRequest): ISolicitudRrhhMemoria {
    if (!value) {
      return value as unknown as ISolicitudRrhhMemoria;
    }
    return {
      tituloTrabajo: value.tituloTrabajo ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.tituloTrabajo) : [],
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.observaciones) : [],
      resumen: value.resumen ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.resumen) : []
    };
  }

  fromTarget(value: ISolicitudRrhhMemoria): ISolicitudRrhhMemoriaRequest {
    if (!value) {
      return value as unknown as ISolicitudRrhhMemoriaRequest;
    }
    return {
      tituloTrabajo: value.tituloTrabajo ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.tituloTrabajo) : [],
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.observaciones) : [],
      resumen: value.resumen ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.resumen) : [],
    };
  }
}

export const SOLICITUD_RRHH_MEMORIA_REQUEST_CONVERTER = new SolicitudRrhhMemoriaRequestConverter();
