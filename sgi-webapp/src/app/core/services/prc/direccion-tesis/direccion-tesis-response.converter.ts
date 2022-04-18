import { IDireccionTesis } from '@core/models/prc/direccion-tesis';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IDireccionTesisResponse } from './direccion-tesis-response';

class DireccionTesisResponseConverter extends SgiBaseConverter<IDireccionTesisResponse, IDireccionTesis>{
  toTarget(value: IDireccionTesisResponse): IDireccionTesis {
    if (!value) {
      return value as unknown as IDireccionTesis;
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
      fechaDefensa: LuxonUtils.fromBackend(value.fechaDefensa),
      produccionCientificaRef: value.produccionCientificaRef,
      tituloTrabajo: value.tituloTrabajo,
    };
  }
  fromTarget(value: IDireccionTesis): IDireccionTesisResponse {
    if (!value) {
      return value as unknown as IDireccionTesisResponse;
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
      fechaDefensa: LuxonUtils.toBackend(value.fechaDefensa),
      produccionCientificaRef: value.produccionCientificaRef,
      tituloTrabajo: value.tituloTrabajo,
    };
  }
}

export const DIRECCION_TESIS_RESPONSE_CONVERTER = new DireccionTesisResponseConverter();
