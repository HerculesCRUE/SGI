import { IConvocatoriaReunionBackend } from '@core/models/eti/backend/convocatoria-reunion-backend';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaReunionConverter extends SgiBaseConverter<IConvocatoriaReunionBackend, IConvocatoriaReunion> {
  toTarget(value: IConvocatoriaReunionBackend): IConvocatoriaReunion {
    if (!value) {
      return value as unknown as IConvocatoriaReunion;
    }
    return {
      id: value.id,
      comite: value.comite,
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      fechaEvaluacion: LuxonUtils.fromBackend(value.fechaEvaluacion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      fechaLimite: LuxonUtils.fromBackend(value.fechaLimite),
      lugar: value.lugar,
      ordenDia: value.ordenDia,
      anio: value.anio,
      numeroActa: value.numeroActa,
      fechaEnvio: LuxonUtils.fromBackend(value.fechaEnvio),
      activo: value.activo,
      codigo: `ACTA${value.numeroActa}/${LuxonUtils.fromBackend(value.fechaEvaluacion).year}/${value.comite.comite}`
    };
  }

  fromTarget(value: IConvocatoriaReunion): IConvocatoriaReunionBackend {
    if (!value) {
      return value as unknown as IConvocatoriaReunionBackend;
    }
    return {
      id: value.id,
      comite: value.comite,
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      fechaEvaluacion: LuxonUtils.toBackend(value.fechaEvaluacion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      fechaLimite: LuxonUtils.toBackend(value.fechaLimite),
      lugar: value.lugar,
      ordenDia: value.ordenDia,
      anio: value.anio,
      numeroActa: value.numeroActa,
      fechaEnvio: LuxonUtils.toBackend(value.fechaEnvio),
      activo: value.activo
    };
  }
}

export const CONVOCATORIA_REUNION_CONVERTER = new ConvocatoriaReunionConverter();
