import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IComentario } from '@core/models/eti/comentario';
import { IPersona } from '@core/models/sgp/persona';
import { IComentarioResponse } from '@core/services/eti/comentario/comentario-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { APARTADO_RESPONSE_CONVERTER } from '../apartado/apartado-response.converter';
import { EVALUACION_RESPONSE_CONVERTER } from '../evaluacion/evaluacion-response.converter';
import { MEMORIA_RESPONSE_CONVERTER } from '../memoria/memoria-response.converter';

class ComentarioResponseConverter extends SgiBaseConverter<IComentarioResponse, IComentario> {
  toTarget(value: IComentarioResponse): IComentario {
    if (!value) {
      return value as unknown as IComentario;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      apartado: APARTADO_RESPONSE_CONVERTER.toTarget(value.apartado),
      evaluacion: EVALUACION_RESPONSE_CONVERTER.toTarget(value.evaluacion),
      tipoComentario: value.tipoComentario,
      texto: value.texto ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.texto) : [],
      evaluador: { id: value.lastModifiedBy ?? value.createdBy } as IPersona,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado)
    };
  }

  fromTarget(value: IComentario): IComentarioResponse {
    if (!value) {
      return value as unknown as IComentarioResponse;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      apartado: APARTADO_RESPONSE_CONVERTER.fromTarget(value.apartado),
      evaluacion: EVALUACION_RESPONSE_CONVERTER.fromTarget(value.evaluacion),
      tipoComentario: value.tipoComentario,
      texto: value.texto ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.texto) : [],
      createdBy: null,
      lastModifiedBy: null,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado)
    };
  }
}

export const COMENTARIO_RESPONSE_CONVERTER = new ComentarioResponseConverter();
