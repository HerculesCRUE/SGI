import { IEstadoActa } from '@core/models/eti/estado-acta';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ACTA_RESPONSE_CONVERTER } from '../acta/acta-response.converter';
import { IEstadoActaResponse } from './estado-acta-response';

class EstadoActaResponseConverter extends SgiBaseConverter<IEstadoActaResponse, IEstadoActa> {
  toTarget(value: IEstadoActaResponse): IEstadoActa {
    if (!value) {
      return value as unknown as IEstadoActa;
    }
    return {
      id: value.id,
      acta: ACTA_RESPONSE_CONVERTER.toTarget(value.acta),
      tipoEstadoActa: value.tipoEstadoActa,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado)
    };
  }

  fromTarget(value: IEstadoActa): IEstadoActaResponse {
    if (!value) {
      return value as unknown as IEstadoActaResponse;
    }
    return {
      id: value.id,
      acta: ACTA_RESPONSE_CONVERTER.fromTarget(value.acta),
      tipoEstadoActa: value.tipoEstadoActa,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado)
    };
  }
}

export const ESTADO_ACTA_RESPONSE_CONVERTER = new EstadoActaResponseConverter();
