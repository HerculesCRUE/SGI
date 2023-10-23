import { IEstadoMemoriaRequest } from '@core/services/eti/estado-memoria/estado-memoria-request';
import { IEstadoMemoria } from '@core/models/eti/estado-memoria';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { MEMORIA_CONVERTER } from '../../../converters/eti/memoria.converter';

class EstadoMemoriaRequestConverter extends SgiBaseConverter<IEstadoMemoriaRequest, IEstadoMemoria> {
  toTarget(value: IEstadoMemoriaRequest): IEstadoMemoria {
    if (!value) {
      return value as unknown as IEstadoMemoria;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      tipoEstadoMemoria: value.tipoEstadoMemoria,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }

  fromTarget(value: IEstadoMemoria): IEstadoMemoriaRequest {
    if (!value) {
      return value as unknown as IEstadoMemoriaRequest;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      tipoEstadoMemoria: value.tipoEstadoMemoria,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }
}

export const ESTADO_MEMORIA_REQUEST_CONVERTER = new EstadoMemoriaRequestConverter();
