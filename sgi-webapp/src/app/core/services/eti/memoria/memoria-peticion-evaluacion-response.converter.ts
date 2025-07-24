import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { COMITE_RESPONSE_CONVERTER } from '@core/services/eti/comite/comite-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { RETROSPECTIVA_RESPONSE_CONVERTER } from '../retrospectiva/retrospectiva-response.converter';
import { IMemoriaPeticionEvaluacionResponse } from './memoria-peticion-evaluacion-response';

class MemoriaPeticionEvaluacionConverter extends SgiBaseConverter<IMemoriaPeticionEvaluacionResponse, IMemoriaPeticionEvaluacion> {
  toTarget(value: IMemoriaPeticionEvaluacionResponse): IMemoriaPeticionEvaluacion {
    if (!value) {
      return value as unknown as IMemoriaPeticionEvaluacion;
    }
    return {
      id: value.id,
      responsableRef: value.responsableRef,
      numReferencia: value.numReferencia,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      comite: COMITE_RESPONSE_CONVERTER.toTarget(value.comite),
      estadoActual: value.estadoActual,
      requiereRetrospectiva: value.requiereRetrospectiva,
      retrospectiva: RETROSPECTIVA_RESPONSE_CONVERTER.toTarget(value.retrospectiva),
      fechaEvaluacion: LuxonUtils.fromBackend(value.fechaEvaluacion),
      fechaLimite: LuxonUtils.fromBackend(value.fechaLimite),
      isResponsable: value.isResponsable,
      activo: value.activo,
      solicitante: { id: value.solicitanteRef } as IPersona,
      version: value.version
    };
  }

  fromTarget(value: IMemoriaPeticionEvaluacion): IMemoriaPeticionEvaluacionResponse {
    if (!value) {
      return value as unknown as IMemoriaPeticionEvaluacionResponse;
    }
    return {
      id: value.id,
      responsableRef: value.responsableRef,
      numReferencia: value.numReferencia,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
      comite: COMITE_RESPONSE_CONVERTER.fromTarget(value.comite),
      estadoActual: value.estadoActual,
      requiereRetrospectiva: value.requiereRetrospectiva,
      retrospectiva: RETROSPECTIVA_RESPONSE_CONVERTER.fromTarget(value.retrospectiva),
      fechaEvaluacion: LuxonUtils.toBackend(value.fechaEvaluacion),
      fechaLimite: LuxonUtils.toBackend(value.fechaLimite),
      isResponsable: value.isResponsable,
      activo: value.activo,
      solicitanteRef: value.solicitante.id,
      version: value.version
    };
  }
}

export const MEMORIA_PETICION_EVALUACION_RESPONSE_CONVERTER = new MemoriaPeticionEvaluacionConverter();
