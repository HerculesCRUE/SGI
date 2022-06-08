import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoEquipoInstrumentalResponse } from './grupo-equipo-instrumental-response';

class GrupoEquipoInstrumentalResponseConverter
  extends SgiBaseConverter<IGrupoEquipoInstrumentalResponse, IGrupoEquipoInstrumental> {
  toTarget(value: IGrupoEquipoInstrumentalResponse): IGrupoEquipoInstrumental {
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

  fromTarget(value: IGrupoEquipoInstrumental): IGrupoEquipoInstrumentalResponse {
    if (!value) {
      return value as unknown as IGrupoEquipoInstrumentalResponse;
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

export const GRUPO_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER = new GrupoEquipoInstrumentalResponseConverter();
