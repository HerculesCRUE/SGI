import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGastoJustificadoResponse } from './gasto-justificado-response';

class GastoJustificadoResponseConverter extends SgiBaseConverter<IGastoJustificadoResponse, IGastoJustificado>{

  toTarget(value: IGastoJustificadoResponse): IGastoJustificado {
    if (!value) {
      return value as unknown as IGastoJustificado;
    }

    return {
      id: value.id,
      columnas: value.columnas,
      justificacionId: value.justificacionId,
      proyectoId: value.proyectoId
    };
  }

  fromTarget(value: IGastoJustificado): IGastoJustificadoResponse {
    if (!value) {
      return value as unknown as IGastoJustificadoResponse;
    }

    return {
      id: value.id,
      columnas: value.columnas,
      justificacionId: value.justificacionId,
      proyectoId: value.proyectoId
    };
  }

}

export const GASTO_JUSTIFICADO_RESPONSE_CONVERTER = new GastoJustificadoResponseConverter();
