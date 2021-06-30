import { IEstadoMemoriaBackend } from '@core/models/eti/backend/estado-memoria-backend';
import { IEstadoMemoria } from '@core/models/eti/estado-memoria';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { MEMORIA_CONVERTER } from './memoria.converter';

class EstadoMemoriaConverter extends SgiBaseConverter<IEstadoMemoriaBackend, IEstadoMemoria> {
  toTarget(value: IEstadoMemoriaBackend): IEstadoMemoria {
    if (!value) {
      return value as unknown as IEstadoMemoria;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      tipoEstadoMemoria: value.tipoEstadoMemoria,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado)
    };
  }

  fromTarget(value: IEstadoMemoria): IEstadoMemoriaBackend {
    if (!value) {
      return value as unknown as IEstadoMemoriaBackend;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      tipoEstadoMemoria: value.tipoEstadoMemoria,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado)
    };
  }
}

export const ESTADO_MEMORIA_CONVERTER = new EstadoMemoriaConverter();
