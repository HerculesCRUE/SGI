import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoOrigenFuenteFinanciacionRequest } from './tipo-origen-fuente-financiacion-request';

class TipoOrigenFuenteFinanciacionRequestConverter extends SgiBaseConverter<ITipoOrigenFuenteFinanciacionRequest, ITipoOrigenFuenteFinanciacion>{
  toTarget(value: ITipoOrigenFuenteFinanciacionRequest): ITipoOrigenFuenteFinanciacion {
    if (!value) {
      return value as unknown as ITipoOrigenFuenteFinanciacion;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      activo: true
    };
  }
  fromTarget(value: ITipoOrigenFuenteFinanciacion): ITipoOrigenFuenteFinanciacionRequest {
    if (!value) {
      return value as unknown as ITipoOrigenFuenteFinanciacionRequest;
    }
    return {
      nombre: value.nombre
    };
  }
}

export const TIPO_ORIGEN_FUENTE_FINANCIACION_REQUEST_CONVERTER = new TipoOrigenFuenteFinanciacionRequestConverter();
