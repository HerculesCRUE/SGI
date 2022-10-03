import { IGastoJustificadoDetalle } from '@core/models/sge/gasto-justificado-detalle';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGastoJustificadoDetalleResponse } from './gasto-justificado-detalle-response';

class GastoJustificadoDetalleResponseConverter extends SgiBaseConverter<IGastoJustificadoDetalleResponse, IGastoJustificadoDetalle>{

  toTarget(value: IGastoJustificadoDetalleResponse): IGastoJustificadoDetalle {
    if (!value) {
      return value as unknown as IGastoJustificadoDetalle;
    }

    return {
      id: value.id,
      campos: value.campos,
      documentos: value.documentos,
      justificacionId: value.justificacionId,
      proyectoId: value.proyectoId
    };
  }

  fromTarget(value: IGastoJustificadoDetalle): IGastoJustificadoDetalleResponse {
    if (!value) {
      return value as unknown as IGastoJustificadoDetalleResponse;
    }

    return {
      id: value.id,
      campos: value.campos,
      documentos: value.documentos,
      justificacionId: value.justificacionId,
      proyectoId: value.proyectoId
    };
  }

}

export const GASTO_JUSTIFICADO_DETALLE_RESPONSE_CONVERTER = new GastoJustificadoDetalleResponseConverter();
