import { IEvaluacionWithNumComentarioBackend } from '@core/models/eti/backend/evaluacion-with-num-comentario-backend';
import { IEvaluacionWithNumComentario } from '@core/models/eti/evaluacion-with-num-comentario';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EVALUACION_CONVERTER } from './evaluacion.converter';

class EvaluacionWithNumComentarioConverter extends SgiBaseConverter<IEvaluacionWithNumComentarioBackend, IEvaluacionWithNumComentario> {
  toTarget(value: IEvaluacionWithNumComentarioBackend): IEvaluacionWithNumComentario {
    if (!value) {
      return value as unknown as IEvaluacionWithNumComentario;
    }
    return {
      evaluacion: EVALUACION_CONVERTER.toTarget(value.evaluacion),
      numComentarios: value.numComentarios
    };
  }

  fromTarget(value: IEvaluacionWithNumComentario): IEvaluacionWithNumComentarioBackend {
    if (!value) {
      return value as unknown as IEvaluacionWithNumComentarioBackend;
    }
    return {
      evaluacion: EVALUACION_CONVERTER.fromTarget(value.evaluacion),
      numComentarios: value.numComentarios
    };
  }
}

export const EVALUACION_WITH_NUM_COMENTARIO_CONVERTER = new EvaluacionWithNumComentarioConverter();
