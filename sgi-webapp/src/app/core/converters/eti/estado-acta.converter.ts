import { IEstadoActaBackend } from '@core/models/eti/backend/estado-acta-backend';
import { IEstadoActa } from '@core/models/eti/estado-acta';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ACTA_CONVERTER } from './acta.converter';

class EstadoActaConverter extends SgiBaseConverter<IEstadoActaBackend, IEstadoActa> {
  toTarget(value: IEstadoActaBackend): IEstadoActa {
    if (!value) {
      return value as unknown as IEstadoActa;
    }
    return {
      id: value.id,
      acta: ACTA_CONVERTER.toTarget(value.acta),
      tipoEstadoActa: value.tipoEstadoActa,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado)
    };
  }

  fromTarget(value: IEstadoActa): IEstadoActaBackend {
    if (!value) {
      return value as unknown as IEstadoActaBackend;
    }
    return {
      id: value.id,
      acta: ACTA_CONVERTER.fromTarget(value.acta),
      tipoEstadoActa: value.tipoEstadoActa,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado)
    };
  }
}

export const ESTADO_ACTA_CONVERTER = new EstadoActaConverter();
