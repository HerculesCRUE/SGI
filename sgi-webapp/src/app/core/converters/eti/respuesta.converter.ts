import { IRespuestaBackend } from '@core/models/eti/backend/respuesta-backend';
import { IRespuesta } from '@core/models/eti/respuesta';
import { SgiBaseConverter } from '@sgi/framework/core';
import { MEMORIA_CONVERTER } from './memoria.converter';

class RespuestasConverter extends SgiBaseConverter<IRespuestaBackend, IRespuesta> {
  toTarget(value: IRespuestaBackend): IRespuesta {
    if (!value) {
      return value as unknown as IRespuesta;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      apartado: value.apartado,
      tipoDocumento: value.tipoDocumento,
      valor: value.valor
    };
  }

  fromTarget(value: IRespuesta): IRespuestaBackend {
    if (!value) {
      return value as unknown as IRespuestaBackend;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      apartado: value.apartado,
      tipoDocumento: value.tipoDocumento,
      valor: value.valor
    };
  }
}

export const RESPUESTA_CONVERTER = new RespuestasConverter();
