import { IObraArtistica } from '@core/models/prc/obra-artistica';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IObraArtisticaResponse } from './obra-artistica-response';

class ObraArtisticaResponseConverter extends SgiBaseConverter<IObraArtisticaResponse, IObraArtistica>{
  toTarget(value: IObraArtisticaResponse): IObraArtistica {
    if (!value) {
      return value as unknown as IObraArtistica;
    }
    return {
      id: value.id,
      epigrafe: value.epigrafe,
      estado: value.estado ? {
        id: value.estado.id,
        comentario: value.estado.comentario,
        estado: value.estado.estado,
        fecha: LuxonUtils.fromBackend(value.estado.fecha)
      } : null,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      produccionCientificaRef: value.produccionCientificaRef,
      descripcion: value.descripcion,
    };
  }
  fromTarget(value: IObraArtistica): IObraArtisticaResponse {
    if (!value) {
      return value as unknown as IObraArtisticaResponse;
    }
    return {
      id: value.id,
      epigrafe: value.epigrafe,
      estado: value.estado ? {
        id: value.estado.id,
        comentario: value.estado.comentario,
        estado: value.estado.estado,
        fecha: LuxonUtils.toBackend(value.estado.fecha)
      } : null,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      produccionCientificaRef: value.produccionCientificaRef,
      descripcion: value.descripcion,
    };
  }
}

export const OBRA_ARTISTICA_RESPONSE_CONVERTER = new ObraArtisticaResponseConverter();
