import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { IActaWithNumEvaluacionesBackend } from '@core/models/eti/backend/acta-with-num-evaluaciones-backend';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ActaWithNumEvaluacionesConverter extends SgiBaseConverter<IActaWithNumEvaluacionesBackend, IActaWithNumEvaluaciones> {
  toTarget(value: IActaWithNumEvaluacionesBackend): IActaWithNumEvaluaciones {
    if (!value) {
      return value as unknown as IActaWithNumEvaluaciones;
    }
    return {
      id: value.id,
      comite: value.comite,
      fechaEvaluacion: LuxonUtils.fromBackend(value.fechaEvaluacion),
      numeroActa: value.numeroActa,
      convocatoria: value.convocatoria,
      numEvaluaciones: value.numEvaluaciones,
      numRevisiones: value.numRevisiones,
      numTotal: value.numTotal,
      estadoActa: value.estadoActa,
      numEvaluacionesNoEvaluadas: value.numEvaluacionesNoEvaluadas
    };
  }

  fromTarget(value: IActaWithNumEvaluaciones): IActaWithNumEvaluacionesBackend {
    if (!value) {
      return value as unknown as IActaWithNumEvaluacionesBackend;
    }
    return {
      id: value.id,
      comite: value.comite,
      fechaEvaluacion: LuxonUtils.toBackend(value.fechaEvaluacion),
      numeroActa: value.numeroActa,
      convocatoria: value.convocatoria,
      numEvaluaciones: value.numEvaluaciones,
      numRevisiones: value.numRevisiones,
      numTotal: value.numTotal,
      estadoActa: value.estadoActa,
      numEvaluacionesNoEvaluadas: value.numEvaluacionesNoEvaluadas
    };
  }
}

export const ACTA_WITH_NUM_EVALUACIONES_CONVERTER = new ActaWithNumEvaluacionesConverter();
