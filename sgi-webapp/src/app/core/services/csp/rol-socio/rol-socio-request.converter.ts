import { IRolSocio } from '@core/models/csp/rol-socio';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRolSocioRequest } from './rol-socio-request';

class RolSocioRequestConverter extends SgiBaseConverter<IRolSocioRequest, IRolSocio>{
  toTarget(value: IRolSocioRequest): IRolSocio {
    if (!value) {
      return value as unknown as IRolSocio;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      abreviatura: value.abreviatura,
      coordinador: value.coordinador,
      descripcion: value.descripcion,
      activo: true
    };
  }
  fromTarget(value: IRolSocio): IRolSocioRequest {
    if (!value) {
      return value as unknown as IRolSocioRequest;
    }
    return {
      nombre: value.nombre,
      abreviatura: value.abreviatura,
      coordinador: value.coordinador,
      descripcion: value.descripcion,
    };
  }
}

export const ROL_SOCIO_REQUEST_CONVERTER = new RolSocioRequestConverter();
