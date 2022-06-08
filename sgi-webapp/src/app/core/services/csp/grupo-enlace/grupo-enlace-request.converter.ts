import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoEnlaceRequest } from './grupo-enlace-request';

class GrupoEnlaceRequestConverter
  extends SgiBaseConverter<IGrupoEnlaceRequest, IGrupoEnlace> {
  toTarget(value: IGrupoEnlaceRequest): IGrupoEnlace {
    if (!value) {
      return value as unknown as IGrupoEnlace;
    }
    return {
      id: undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      enlace: value.enlace
    };
  }

  fromTarget(value: IGrupoEnlace): IGrupoEnlaceRequest {
    if (!value) {
      return value as unknown as IGrupoEnlaceRequest;
    }
    return {
      grupoId: value.grupo.id,
      enlace: value.enlace
    };
  }
}

export const GRUPO_ENLACE_REQUEST_CONVERTER = new GrupoEnlaceRequestConverter();
