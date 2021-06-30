import { IEvaluacionBackend } from '@core/models/eti/backend/evaluacion-backend';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_CONVERTER } from './convocatoria-reunion.converter';
import { EVALUADOR_CONVERTER } from './evaluador.converter';
import { MEMORIA_CONVERTER } from './memoria.converter';

class EvaluacionConverter extends SgiBaseConverter<IEvaluacionBackend, IEvaluacion> {
  toTarget(value: IEvaluacionBackend): IEvaluacion {
    if (!value) {
      return value as unknown as IEvaluacion;
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
      comentario: value.comentario,
      activo: value.activo
    };
  }

  fromTarget(value: IEvaluacion): IEvaluacionBackend {
    if (!value) {
      return value as unknown as IEvaluacionBackend;
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
      comentario: value.comentario,
      activo: value.activo
    };
  }
}

export const EVALUACION_CONVERTER = new EvaluacionConverter();
