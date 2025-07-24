import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IPersona } from '@core/models/sgp/persona';
import { COMITE_RESPONSE_CONVERTER } from '@core/services/eti/comite/comite-response.converter';
import { IEvaluadorResponse } from '@core/services/eti/evaluador/evaluador-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class EvaluadorResponseConverter extends SgiBaseConverter<IEvaluadorResponse, IEvaluador> {
  toTarget(value: IEvaluadorResponse): IEvaluador {
    if (!value) {
      return value as unknown as IEvaluador;
    }
    return {
      id: value.id,
      comite: COMITE_RESPONSE_CONVERTER.toTarget(value.comite),
      cargoComite: value.cargoComite,
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.resumen) : [],
      fechaAlta: LuxonUtils.fromBackend(value.fechaAlta),
      fechaBaja: LuxonUtils.fromBackend(value.fechaBaja),
      persona: { id: value.personaRef } as IPersona,
      activo: value.activo
    };
  }

  fromTarget(value: IEvaluador): IEvaluadorResponse {
    if (!value) {
      return value as unknown as IEvaluadorResponse;
    }
    return {
      id: value.id,
      comite: COMITE_RESPONSE_CONVERTER.fromTarget(value.comite),
      cargoComite: value.cargoComite,
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.resumen) : [],
      fechaAlta: LuxonUtils.toBackend(value.fechaAlta),
      fechaBaja: LuxonUtils.toBackend(value.fechaBaja),
      personaRef: value.persona?.id,
      activo: value.activo
    };
  }
}

export const EVALUADOR_RESPONSE_CONVERTER = new EvaluadorResponseConverter();
