import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhMemoriaResponse } from './solicitud-rrhh-memoria-response';

class SolicitudRrhhMemoriaResponseConverter
  extends SgiBaseConverter<ISolicitudRrhhMemoriaResponse, ISolicitudRrhhMemoria> {
  toTarget(value: ISolicitudRrhhMemoriaResponse): ISolicitudRrhhMemoria {
    if (!value) {
      return value as unknown as ISolicitudRrhhMemoria;
    }
    return {
      tituloTrabajo: value.tituloTrabajo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.tituloTrabajo) : [],
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.resumen) : []
    };
  }

  fromTarget(value: ISolicitudRrhhMemoria): ISolicitudRrhhMemoriaResponse {
    if (!value) {
      return value as unknown as ISolicitudRrhhMemoriaResponse;
    }
    return {
      tituloTrabajo: value.tituloTrabajo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.tituloTrabajo) : [],
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.resumen) : []
    };
  }
}

export const SOLICITUD_RRHH_MEMORIA_RESPONSE_CONVERTER = new SolicitudRrhhMemoriaResponseConverter();
