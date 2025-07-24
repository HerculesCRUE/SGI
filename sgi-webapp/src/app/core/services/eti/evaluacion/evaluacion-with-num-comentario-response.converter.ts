import { IEvaluacionWithNumComentario } from '@core/models/eti/evaluacion-with-num-comentario';
import { IEvaluacionWithNumComentarioResponse } from '@core/services/eti/evaluacion/evaluacion-with-num-comentario-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EVALUACION_RESPONSE_CONVERTER } from './evaluacion-response.converter';

class EvaluacionWithNumComentarioResponseConverter extends SgiBaseConverter<IEvaluacionWithNumComentarioResponse, IEvaluacionWithNumComentario> {
  toTarget(value: IEvaluacionWithNumComentarioResponse): IEvaluacionWithNumComentario {
    if (!value) {
      return value as unknown as IEvaluacionWithNumComentario;
    }
    return {
      evaluacion: EVALUACION_RESPONSE_CONVERTER.toTarget(value.evaluacion),
      numComentarios: value.numComentarios
    };
  }

  fromTarget(value: IEvaluacionWithNumComentario): IEvaluacionWithNumComentarioResponse {
    if (!value) {
      return value as unknown as IEvaluacionWithNumComentarioResponse;
    }
    return {
      evaluacion: EVALUACION_RESPONSE_CONVERTER.fromTarget(value.evaluacion),
      numComentarios: value.numComentarios
    };
  }
}

export const EVALUACION_WITH_NUM_COMENTARIO_RESPONSE_CONVERTER = new EvaluacionWithNumComentarioResponseConverter();
