import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { IActaWithNumEvaluacionesResponse } from '@core/services/eti/acta/acta-with-num-evaluaciones-response';
import { ACTA_DOCUMENTO_RESPONSE_CONVERTER } from '@core/services/eti/acta/acta-documento-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ActaWithNumEvaluacionesResponseConverter extends SgiBaseConverter<IActaWithNumEvaluacionesResponse, IActaWithNumEvaluaciones> {
  toTarget(value: IActaWithNumEvaluacionesResponse): IActaWithNumEvaluaciones {
    if (!value) {
      return value as unknown as IActaWithNumEvaluaciones;
    }
    return {
      id: value.id,
      comite: value.comite,
      fechaEvaluacion: LuxonUtils.fromBackend(value.fechaEvaluacion),
      numeroActa: value.numeroActa,
      tipoConvocatoria: value.tipoConvocatoria,
      numEvaluaciones: value.numEvaluaciones,
      numRevisiones: value.numRevisiones,
      numTotal: value.numTotal,
      estadoActa: value.estadoActa,
      numEvaluacionesNoEvaluadas: value.numEvaluacionesNoEvaluadas,
      documentos: value.documentos?.length ? ACTA_DOCUMENTO_RESPONSE_CONVERTER.toTargetArray(value.documentos) : [],
    };
  }

  fromTarget(value: IActaWithNumEvaluaciones): IActaWithNumEvaluacionesResponse {
    if (!value) {
      return value as unknown as IActaWithNumEvaluacionesResponse;
    }
    return {
      id: value.id,
      comite: value.comite,
      fechaEvaluacion: LuxonUtils.toBackend(value.fechaEvaluacion),
      numeroActa: value.numeroActa,
      tipoConvocatoria: value.tipoConvocatoria,
      numEvaluaciones: value.numEvaluaciones,
      numRevisiones: value.numRevisiones,
      numTotal: value.numTotal,
      estadoActa: value.estadoActa,
      numEvaluacionesNoEvaluadas: value.numEvaluacionesNoEvaluadas,
      documentos: value.documentos?.length ? ACTA_DOCUMENTO_RESPONSE_CONVERTER.fromTargetArray(value.documentos) : [],
    };
  }
}

export const ACTA_WITH_NUM_EVALUACIONES_RESPONSE_CONVERTER = new ActaWithNumEvaluacionesResponseConverter();
