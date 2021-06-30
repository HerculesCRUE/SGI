import { IActa } from '@core/models/eti/acta';
import { IActaBackend } from '@core/models/eti/backend/acta-backend';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_CONVERTER } from './convocatoria-reunion.converter';

class ActaConverter extends SgiBaseConverter<IActaBackend, IActa> {
  toTarget(value: IActaBackend): IActa {
    if (!value) {
      return value as unknown as IActa;
    }
    return {
      id: value.id,
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.toTarget(value.convocatoriaReunion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaFin: value.horaFin,
      minutoFin: value.minutoFin,
      resumen: value.resumen,
      numero: value.numero,
      inactiva: value.inactiva,
      activo: value.activo,
      estadoActual: value.estadoActual
    };
  }

  fromTarget(value: IActa): IActaBackend {
    if (!value) {
      return value as unknown as IActaBackend;
    }
    return {
      id: value.id,
      convocatoriaReunion: CONVOCATORIA_REUNION_CONVERTER.fromTarget(value.convocatoriaReunion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaFin: value.horaFin,
      minutoFin: value.minutoFin,
      resumen: value.resumen,
      numero: value.numero,
      inactiva: value.inactiva,
      activo: value.activo,
      estadoActual: value.estadoActual
    };
  }
}

export const ACTA_CONVERTER = new ActaConverter();
