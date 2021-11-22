import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoFacturacionRequest } from './tipo-facturacion-request';

class TipoFacturacionRequestConverter extends SgiBaseConverter<ITipoFacturacionRequest, ITipoFacturacion> {

  toTarget(value: ITipoFacturacionRequest): ITipoFacturacion {
    return !value ? value as unknown as ITipoFacturacion :
      {
        id: undefined,
        nombre: value.nombre
      };
  }

  fromTarget(value: ITipoFacturacion): ITipoFacturacionRequest {
    return !value ? value as unknown as ITipoFacturacionRequest :
      {
        nombre: value.nombre
      };
  }

}

export const TIPO_FACTURACION_REQUEST_CONVERTER = new TipoFacturacionRequestConverter();
