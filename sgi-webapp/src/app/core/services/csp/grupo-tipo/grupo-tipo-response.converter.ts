import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoTipo } from '@core/models/csp/grupo-tipo';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoTipoResponse } from './grupo-tipo-response';

class GrupoTipoResponseConverter
  extends SgiBaseConverter<IGrupoTipoResponse, IGrupoTipo> {
  toTarget(value: IGrupoTipoResponse): IGrupoTipo {
    if (!value) {
      return value as unknown as IGrupoTipo;
    }
    return {
      id: value.id,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      tipo: value.tipo
    };
  }

  fromTarget(value: IGrupoTipo): IGrupoTipoResponse {
    if (!value) {
      return value as unknown as IGrupoTipoResponse;
    }
    return {
      id: value.id,
      grupoId: value.grupo.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      tipo: value.tipo
    };
  }
}

export const GRUPO_TIPO_RESPONSE_CONVERTER = new GrupoTipoResponseConverter();
