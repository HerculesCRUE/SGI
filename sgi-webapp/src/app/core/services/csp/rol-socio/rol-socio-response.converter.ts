import { IRolSocio } from '@core/models/csp/rol-socio';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRolSocioResponse } from './rol-socio-response';

class RolSocioResponseConverter extends SgiBaseConverter<IRolSocioResponse, IRolSocio>{
  toTarget(value: IRolSocioResponse): IRolSocio {
    if (!value) {
      return value as unknown as IRolSocio;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      abreviatura: value.abreviatura,
      coordinador: value.coordinador,
      descripcion: value.descripcion,
      activo: value.activo
    };
  }
  fromTarget(value: IRolSocio): IRolSocioResponse {
    if (!value) {
      return value as unknown as IRolSocioResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      abreviatura: value.abreviatura,
      coordinador: value.coordinador,
      descripcion: value.descripcion,
      activo: value.activo
    };
  }
}

export const ROL_SOCIO_RESPONSE_CONVERTER = new RolSocioResponseConverter();
