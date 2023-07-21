import { ITipoRegimenConcurrencia } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoRegimenConcurrenciaResponse } from './tipo-regimen-concurrencia-response';

class TipoRegimenConcurrenciaResponseConverter extends SgiBaseConverter<ITipoRegimenConcurrenciaResponse, ITipoRegimenConcurrencia>{
  toTarget(value: ITipoRegimenConcurrenciaResponse): ITipoRegimenConcurrencia {
    if (!value) {
      return value as unknown as ITipoRegimenConcurrencia;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      activo: value.activo
    };
  }
  fromTarget(value: ITipoRegimenConcurrencia): ITipoRegimenConcurrenciaResponse {
    if (!value) {
      return value as unknown as ITipoRegimenConcurrenciaResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      activo: value.activo
    };
  }
}

export const TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER = new TipoRegimenConcurrenciaResponseConverter();
