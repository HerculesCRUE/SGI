import { MEMORIA_CONVERTER } from '@core/converters/eti/memoria.converter';
import { IEstadoMemoria } from '@core/models/eti/estado-memoria';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEstadoMemoriaResponse } from './estado-memoria-response';

class EstadoMemoriaResponseConverter extends SgiBaseConverter<IEstadoMemoriaResponse, IEstadoMemoria> {
  toTarget(value: IEstadoMemoriaResponse): IEstadoMemoria {
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

  fromTarget(value: IEstadoMemoria): IEstadoMemoriaResponse {
    if (!value) {
      return value as unknown as IEstadoMemoriaResponse;
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

export const ESTADO_MEMORIA_RESPONSE_CONVERTER = new EstadoMemoriaResponseConverter();
