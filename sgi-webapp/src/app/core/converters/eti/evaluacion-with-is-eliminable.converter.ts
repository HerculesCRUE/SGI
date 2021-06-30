import { IEvaluacionWithIsEliminableBackend } from '@core/models/eti/backend/evaluacion-with-is-eliminable-backend';
import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_CONVERTER } from './convocatoria-reunion.converter';
import { EVALUADOR_CONVERTER } from './evaluador.converter';
import { MEMORIA_CONVERTER } from './memoria.converter';

class EvaluacionWithIsEliminableConverter extends SgiBaseConverter<IEvaluacionWithIsEliminableBackend, IEvaluacionWithIsEliminable> {
  toTarget(value: IEvaluacionWithIsEliminableBackend): IEvaluacionWithIsEliminable {
    if (!value) {
      return value as unknown as IEvaluacionWithIsEliminable;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      comite: value.comite,
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.toTarget(value.convocatoriaReunion),
      tipoEvaluacion: value.tipoEvaluacion,
      version: value.version,
      dictamen: value.dictamen,
      evaluador1: EVALUADOR_CONVERTER.toTarget(value.evaluador1),
      evaluador2: EVALUADOR_CONVERTER.toTarget(value.evaluador2),
      fechaDictamen: LuxonUtils.fromBackend(value.fechaDictamen),
      esRevMinima: value.esRevMinima,
      activo: value.activo,
      comentario: value.comentario,
      eliminable: value.eliminable
    };
  }

  fromTarget(value: IEvaluacionWithIsEliminable): IEvaluacionWithIsEliminableBackend {
    if (!value) {
      return value as unknown as IEvaluacionWithIsEliminableBackend;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      comite: value.comite,
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.fromTarget(value.convocatoriaReunion),
      tipoEvaluacion: value.tipoEvaluacion,
      version: value.version,
      dictamen: value.dictamen,
      evaluador1: EVALUADOR_CONVERTER.fromTarget(value.evaluador1),
      evaluador2: EVALUADOR_CONVERTER.fromTarget(value.evaluador2),
      fechaDictamen: LuxonUtils.toBackend(value.fechaDictamen),
      esRevMinima: value.esRevMinima,
      activo: value.activo,
      comentario: value.comentario,
      eliminable: value.eliminable
    };
  }
}

export const EVALUACION_WITH_IS_ELIMINABLE_CONVERTER = new EvaluacionWithIsEliminableConverter();
