import { IAsistente } from '@core/models/eti/asistente';
import { IAsistenteBackend } from '@core/models/eti/backend/asistente-backend';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_CONVERTER } from './convocatoria-reunion.converter';
import { EVALUADOR_CONVERTER } from './evaluador.converter';

class AsistenteConverter extends SgiBaseConverter<IAsistenteBackend, IAsistente> {
  toTarget(value: IAsistenteBackend): IAsistente {
    if (!value) {
      return value as unknown as IAsistente;
    }
    return {
      id: value.id,
      evaluador: EVALUADOR_CONVERTER.toTarget(value.evaluador),
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.toTarget(value.convocatoriaReunion),
      asistencia: value.asistencia,
      motivo: value.motivo
    };
  }

  fromTarget(value: IAsistente): IAsistenteBackend {
    if (!value) {
      return value as unknown as IAsistenteBackend;
    }
    return {
      id: value.id,
      evaluador: EVALUADOR_CONVERTER.fromTarget(value.evaluador),
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.fromTarget(value.convocatoriaReunion),
      asistencia: value.asistencia,
      motivo: value.motivo
    };
  }
}

export const ASISTENTE_CONVERTER = new AsistenteConverter();
