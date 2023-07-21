import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoOrigenFuenteFinanciacionResponse } from './tipo-origen-fuente-financiacion-response';

class TipoOrigenFuenteFinanciacionResponseConverter extends SgiBaseConverter<ITipoOrigenFuenteFinanciacionResponse, ITipoOrigenFuenteFinanciacion>{
  toTarget(value: ITipoOrigenFuenteFinanciacionResponse): ITipoOrigenFuenteFinanciacion {
    if (!value) {
      return value as unknown as ITipoOrigenFuenteFinanciacion;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      activo: value.activo
    };
  }
  fromTarget(value: ITipoOrigenFuenteFinanciacion): ITipoOrigenFuenteFinanciacionResponse {
    if (!value) {
      return value as unknown as ITipoOrigenFuenteFinanciacionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      activo: value.activo
    };
  }
}

export const TIPO_ORIGEN_FUENTE_FINANCIACION_RESPONSE_CONVERTER = new TipoOrigenFuenteFinanciacionResponseConverter();
