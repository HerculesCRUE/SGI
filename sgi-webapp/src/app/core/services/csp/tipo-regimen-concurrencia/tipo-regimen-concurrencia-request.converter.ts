import { ITipoRegimenConcurrencia } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoRegimenConcurrenciaRequest } from './tipo-regimen-concurrencia-request';

class TipoRegimenConcurrenciaRequestConverter extends SgiBaseConverter<ITipoRegimenConcurrenciaRequest, ITipoRegimenConcurrencia>{
  toTarget(value: ITipoRegimenConcurrenciaRequest): ITipoRegimenConcurrencia {
    if (!value) {
      return value as unknown as ITipoRegimenConcurrencia;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      activo: true
    };
  }
  fromTarget(value: ITipoRegimenConcurrencia): ITipoRegimenConcurrenciaRequest {
    if (!value) {
      return value as unknown as ITipoRegimenConcurrenciaRequest;
    }
    return {
      nombre: value.nombre
    };
  }
}

export const TIPO_REGIMEN_CONCURRENCIA_REQUEST_CONVERTER = new TipoRegimenConcurrenciaRequestConverter();
