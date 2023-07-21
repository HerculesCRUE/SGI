import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoFacturacionResponse } from './tipo-facturacion-response';

class TipoFacturacionResponseConverter extends SgiBaseConverter<ITipoFacturacionResponse, ITipoFacturacion> {

  toTarget(value: ITipoFacturacionResponse): ITipoFacturacion {
    return !value ? value as unknown as ITipoFacturacion : {
      id: value.id,
      nombre: value.nombre,
      incluirEnComunicado: value.incluirEnComunicado,
      activo: value.activo
    };
  }

  fromTarget(value: ITipoFacturacion): ITipoFacturacionResponse {
    return !value ? value as unknown as ITipoFacturacionResponse : {
      id: value.id,
      nombre: value.nombre,
      incluirEnComunicado: value.incluirEnComunicado,
      activo: value.activo
    };
  }
}

export const TIPO_FACTURACION_RESPONSE_CONVERTER = new TipoFacturacionResponseConverter();
