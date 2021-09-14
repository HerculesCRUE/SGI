import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGastoDetalleResponse } from './gasto-detalle-response';

class GastoDetalleResponseConverter extends SgiBaseConverter<IGastoDetalleResponse, IDatoEconomicoDetalle>{
  toTarget(value: IGastoDetalleResponse): IDatoEconomicoDetalle {
    if (!value) {
      return value as unknown as IDatoEconomicoDetalle;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      partidaPresupuestaria: value.partidaPresupuestaria,
      codigoEconomico: value.codigoEconomico,
      anualidad: value.anualidad,
      campos: value.campos,
      documentos: value.documentos
    };
  }
  fromTarget(value: IDatoEconomicoDetalle): IGastoDetalleResponse {
    if (!value) {
      return value as unknown as IGastoDetalleResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      partidaPresupuestaria: value.partidaPresupuestaria,
      codigoEconomico: value.codigoEconomico,
      anualidad: value.anualidad,
      campos: value.campos,
      documentos: value.documentos
    };
  }
}

export const GASTO_DETALLE_RESPONSE_CONVERTER = new GastoDetalleResponseConverter();
