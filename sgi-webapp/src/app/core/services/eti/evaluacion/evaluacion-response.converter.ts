import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { COMITE_RESPONSE_CONVERTER } from '../comite/comite-response.converter';
import { CONVOCATORIA_REUNION_RESPONSE_CONVERTER } from '../convocatoria-reunion/convocatoria-reunion-response.converter';
import { EVALUADOR_RESPONSE_CONVERTER } from '../evaluador/evaluador-response.converter';
import { MEMORIA_RESPONSE_CONVERTER } from '../memoria/memoria-response.converter';
import { IEvaluacionResponse } from './evaluacion-response';

class EvaluacionResponseConverter extends SgiBaseConverter<IEvaluacionResponse, IEvaluacion> {
  toTarget(value: IEvaluacionResponse): IEvaluacion {
    if (!value) {
      return value as unknown as IEvaluacion;
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
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      activo: value.activo
    };
  }

  fromTarget(value: IEvaluacion): IEvaluacionResponse {
    if (!value) {
      return value as unknown as IEvaluacionResponse;
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
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      activo: value.activo
    };
  }
}

export const EVALUACION_RESPONSE_CONVERTER = new EvaluacionResponseConverter();
