import { IComentarioBackend } from '@core/models/eti/backend/comentario-backend';
import { IComentario } from '@core/models/eti/comentario';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EVALUACION_CONVERTER } from './evaluacion.converter';
import { MEMORIA_CONVERTER } from './memoria.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';

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
      evaluador: { id: value.lastModifiedBy ?? value.createdBy } as IPersona,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado)
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
      lastModifiedBy: null,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado)
    };
  }
}

export const COMENTARIO_CONVERTER = new ComentarioConverter();
