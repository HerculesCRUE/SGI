import { IRolSocio } from '@core/models/csp/rol-socio';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRolSocioResponse } from './rol-socio-response';
import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';

class RolSocioResponseConverter extends SgiBaseConverter<IRolSocioResponse, IRolSocio> {
  toTarget(value: IRolSocioResponse): IRolSocio {
    if (!value) {
      return value as unknown as IRolSocio;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      abreviatura: value.abreviatura ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.abreviatura) : [],
      coordinador: value.coordinador,
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: IRolSocio): IRolSocioResponse {
    if (!value) {
      return value as unknown as IRolSocioResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      abreviatura: value.abreviatura ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.abreviatura) : [],
      coordinador: value.coordinador,
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const ROL_SOCIO_RESPONSE_CONVERTER = new RolSocioResponseConverter();
