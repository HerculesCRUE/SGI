import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IAsistente } from '@core/models/eti/asistente';
import { IAsistenteResponse } from '@core/services/eti/asistente/asistente-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_RESPONSE_CONVERTER } from '../convocatoria-reunion/convocatoria-reunion-response.converter';
import { EVALUADOR_RESPONSE_CONVERTER } from '../evaluador/evaluador-response.converter';

class AsistenteResponseConverter extends SgiBaseConverter<IAsistenteResponse, IAsistente> {
  toTarget(value: IAsistenteResponse): IAsistente {
    if (!value) {
      return value as unknown as IAsistente;
    }
    return {
      id: value.id,
      evaluador: EVALUADOR_RESPONSE_CONVERTER.toTarget(value.evaluador),
      convocatoriaReunion: CONVOCATORIA_REUNION_RESPONSE_CONVERTER.toTarget(value.convocatoriaReunion),
      asistencia: value.asistencia,
      motivo: value.motivo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.motivo) : []
    };
  }

  fromTarget(value: IAsistente): IAsistenteResponse {
    if (!value) {
      return value as unknown as IAsistenteResponse;
    }
    return {
      id: value.id,
      evaluador: EVALUADOR_RESPONSE_CONVERTER.fromTarget(value.evaluador),
      convocatoriaReunion: CONVOCATORIA_REUNION_RESPONSE_CONVERTER.fromTarget(value.convocatoriaReunion),
      asistencia: value.asistencia,
      motivo: value.motivo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.motivo) : []
    };
  }
}

export const ASISTENTE_RESPONSE_CONVERTER = new AsistenteResponseConverter();
