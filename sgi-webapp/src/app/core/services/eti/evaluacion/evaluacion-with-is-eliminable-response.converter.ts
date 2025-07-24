import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { IEvaluacionWithIsEliminableResponse } from '@core/services/eti/evaluacion/evaluacion-with-is-eliminable-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { COMITE_RESPONSE_CONVERTER } from '../comite/comite-response.converter';
import { CONVOCATORIA_REUNION_RESPONSE_CONVERTER } from '../convocatoria-reunion/convocatoria-reunion-response.converter';
import { EVALUADOR_RESPONSE_CONVERTER } from '../evaluador/evaluador-response.converter';
import { MEMORIA_RESPONSE_CONVERTER } from '../memoria/memoria-response.converter';

class EvaluacionWithIsEliminableResponseConverter extends SgiBaseConverter<IEvaluacionWithIsEliminableResponse, IEvaluacionWithIsEliminable> {
  toTarget(value: IEvaluacionWithIsEliminableResponse): IEvaluacionWithIsEliminable {
    if (!value) {
      return value as unknown as IEvaluacionWithIsEliminable;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      comite: COMITE_RESPONSE_CONVERTER.toTarget(value.comite),
      convocatoriaReunion: CONVOCATORIA_REUNION_RESPONSE_CONVERTER.toTarget(value.convocatoriaReunion),
      tipoEvaluacion: value.tipoEvaluacion,
      version: value.version,
      dictamen: value.dictamen,
      evaluador1: EVALUADOR_RESPONSE_CONVERTER.toTarget(value.evaluador1),
      evaluador2: EVALUADOR_RESPONSE_CONVERTER.toTarget(value.evaluador2),
      fechaDictamen: LuxonUtils.fromBackend(value.fechaDictamen),
      esRevMinima: value.esRevMinima,
      activo: value.activo,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      eliminable: value.eliminable
    };
  }

  fromTarget(value: IEvaluacionWithIsEliminable): IEvaluacionWithIsEliminableResponse {
    if (!value) {
      return value as unknown as IEvaluacionWithIsEliminableResponse;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      comite: COMITE_RESPONSE_CONVERTER.fromTarget(value.comite),
      convocatoriaReunion: CONVOCATORIA_REUNION_RESPONSE_CONVERTER.fromTarget(value.convocatoriaReunion),
      tipoEvaluacion: value.tipoEvaluacion,
      version: value.version,
      dictamen: value.dictamen,
      evaluador1: EVALUADOR_RESPONSE_CONVERTER.fromTarget(value.evaluador1),
      evaluador2: EVALUADOR_RESPONSE_CONVERTER.fromTarget(value.evaluador2),
      fechaDictamen: LuxonUtils.toBackend(value.fechaDictamen),
      esRevMinima: value.esRevMinima,
      activo: value.activo,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      eliminable: value.eliminable
    };
  }
}

export const EVALUACION_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER = new EvaluacionWithIsEliminableResponseConverter();
