import { IMemoriaPeticionEvaluacionBackend } from '@core/models/eti/backend/memoria-peticion-evaluacion-backend';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { RETROSPECTIVA_CONVERTER } from './retrospectiva.converter';

class MemoriaPeticionEvaluacionConverter extends SgiBaseConverter<IMemoriaPeticionEvaluacionBackend, IMemoriaPeticionEvaluacion> {
  toTarget(value: IMemoriaPeticionEvaluacionBackend): IMemoriaPeticionEvaluacion {
    if (!value) {
      return value as unknown as IMemoriaPeticionEvaluacion;
    }
    return {
      id: value.id,
      numReferencia: value.numReferencia,
      titulo: value.titulo,
      comite: value.comite,
      estadoActual: value.estadoActual,
      requiereRetrospectiva: value.requiereRetrospectiva,
      retrospectiva: RETROSPECTIVA_CONVERTER.toTarget(value.retrospectiva),
      fechaEvaluacion: LuxonUtils.fromBackend(value.fechaEvaluacion),
      fechaLimite: LuxonUtils.fromBackend(value.fechaLimite),
      isResponsable: value.isResponsable,
      activo: value.activo,
      solicitanteRef: value.solicitanteRef
    };
  }

  fromTarget(value: IMemoriaPeticionEvaluacion): IMemoriaPeticionEvaluacionBackend {
    if (!value) {
      return value as unknown as IMemoriaPeticionEvaluacionBackend;
    }
    return {
      id: value.id,
      numReferencia: value.numReferencia,
      titulo: value.titulo,
      comite: value.comite,
      estadoActual: value.estadoActual,
      requiereRetrospectiva: value.requiereRetrospectiva,
      retrospectiva: RETROSPECTIVA_CONVERTER.fromTarget(value.retrospectiva),
      fechaEvaluacion: LuxonUtils.toBackend(value.fechaEvaluacion),
      fechaLimite: LuxonUtils.toBackend(value.fechaLimite),
      isResponsable: value.isResponsable,
      activo: value.activo,
      solicitanteRef: value.solicitanteRef
    };
  }
}

export const MEMORIA_PETICION_EVALUACION_CONVERTER = new MemoriaPeticionEvaluacionConverter();
