import { IComiteEditorial } from '@core/models/prc/comite-editorial';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IComiteEditorialResponse } from './comite-editorial-response';

class ComiteEditorialResponseConverter extends SgiBaseConverter<IComiteEditorialResponse, IComiteEditorial> {

  toTarget(value: IComiteEditorialResponse): IComiteEditorial {
    if (!value) {
      return value as unknown as IComiteEditorial;
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
      nombre: value.nombre,
      produccionCientificaRef: value.produccionCientificaRef,
    };
  }

  fromTarget(value: IComiteEditorial): IComiteEditorialResponse {
    if (!value) {
      return value as unknown as IComiteEditorialResponse;
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
      nombre: value.nombre,
      produccionCientificaRef: value.produccionCientificaRef,
    };
  }

}

export const COMITE_EDITORIAL_RESPONSE_CONVERTER = new ComiteEditorialResponseConverter();
