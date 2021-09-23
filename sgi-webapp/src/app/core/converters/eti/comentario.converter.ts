import { IComentarioBackend } from '@core/models/eti/backend/comentario-backend';
import { IComentario } from '@core/models/eti/comentario';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EVALUACION_CONVERTER } from './evaluacion.converter';
import { MEMORIA_CONVERTER } from './memoria.converter';

class ComentarioConverter extends SgiBaseConverter<IComentarioBackend, IComentario> {
  toTarget(value: IComentarioBackend): IComentario {
    if (!value) {
      return value as unknown as IComentario;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      apartado: value.apartado,
      evaluacion: EVALUACION_CONVERTER.toTarget(value.evaluacion),
      tipoComentario: value.tipoComentario,
      texto: value.texto,
      evaluador: { id: value.lastModifiedBy ?? value.createdBy } as IPersona
    };
  }

  fromTarget(value: IComentario): IComentarioBackend {
    if (!value) {
      return value as unknown as IComentarioBackend;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      apartado: value.apartado,
      evaluacion: EVALUACION_CONVERTER.fromTarget(value.evaluacion),
      tipoComentario: value.tipoComentario,
      texto: value.texto,
      createdBy: null,
      lastModifiedBy: null
    };
  }
}

export const COMENTARIO_CONVERTER = new ComentarioConverter();
