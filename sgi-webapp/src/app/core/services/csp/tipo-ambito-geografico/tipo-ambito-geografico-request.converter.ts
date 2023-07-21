import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoAmbitoGeograficoRequest } from './tipo-ambito-geografico-request';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipos-configuracion';

class TipoAmbitoGeograficoRequestConverter extends SgiBaseConverter<ITipoAmbitoGeograficoRequest, ITipoAmbitoGeografico>{
  toTarget(value: ITipoAmbitoGeograficoRequest): ITipoAmbitoGeografico {
    if (!value) {
      return value as unknown as ITipoAmbitoGeografico;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      activo: true
    };
  }
  fromTarget(value: ITipoAmbitoGeografico): ITipoAmbitoGeograficoRequest {
    if (!value) {
      return value as unknown as ITipoAmbitoGeograficoRequest;
    }
    return {
      nombre: value.nombre
    };
  }
}

export const TIPO_AMBITO_GEOGRAFICO_REQUEST_CONVERTER = new TipoAmbitoGeograficoRequestConverter();
