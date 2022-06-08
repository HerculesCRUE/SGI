import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoEquipoInstrumentalRequest } from './grupo-equipo-instrumental-request';

class GrupoEquipoInstrumentalRequestConverter
  extends SgiBaseConverter<IGrupoEquipoInstrumentalRequest, IGrupoEquipoInstrumental> {
  toTarget(value: IGrupoEquipoInstrumentalRequest): IGrupoEquipoInstrumental {
    if (!value) {
      return value as unknown as IGrupoEquipoInstrumental;
    }
    return {
      id: value.id,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      nombre: value.nombre,
      numRegistro: value.numRegistro,
      descripcion: value.descripcion
    };
  }

  fromTarget(value: IGrupoEquipoInstrumental): IGrupoEquipoInstrumentalRequest {
    if (!value) {
      return value as unknown as IGrupoEquipoInstrumentalRequest;
    }
    return {
      id: value.id,
      grupoId: value.grupo.id,
      nombre: value.nombre,
      numRegistro: value.numRegistro,
      descripcion: value.descripcion
    };
  }
}

export const GRUPO_EQUIPO_INSTRUMENTAL_REQUEST_CONVERTER = new GrupoEquipoInstrumentalRequestConverter();
