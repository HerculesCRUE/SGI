import { IConvocatoriaReunionDatosGeneralesBackend } from '@core/models/eti/backend/convocatoria-reunion-datos-generales-backend';
import { IConvocatoriaReunionDatosGenerales } from '@core/models/eti/convocatoria-reunion-datos-generales';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaReunionDatosGeneralesConverter
  extends SgiBaseConverter<IConvocatoriaReunionDatosGeneralesBackend, IConvocatoriaReunionDatosGenerales> {
  toTarget(value: IConvocatoriaReunionDatosGeneralesBackend): IConvocatoriaReunionDatosGenerales {
    if (!value) {
      return value as unknown as IConvocatoriaReunionDatosGenerales;
    }
    return {
      id: value.id,
      comite: value.comite,
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      fechaEvaluacion: LuxonUtils.fromBackend(value.fechaEvaluacion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaInicioSegunda: value.horaInicioSegunda,
      minutoInicioSegunda: value.minutoInicioSegunda,
      fechaLimite: LuxonUtils.fromBackend(value.fechaLimite),
      videoconferencia: value.videoconferencia,
      lugar: value.lugar,
      ordenDia: value.ordenDia,
      anio: value.anio,
      numeroActa: value.numeroActa,
      fechaEnvio: LuxonUtils.fromBackend(value.fechaEnvio),
      activo: value.activo,
      codigo: `ACTA${value.numeroActa}/${LuxonUtils.fromBackend(value.fechaEvaluacion).year}/${value.comite.comite}`,
      numEvaluaciones: value.numEvaluaciones,
      idActa: value.idActa
    };
  }

  fromTarget(value: IConvocatoriaReunionDatosGenerales): IConvocatoriaReunionDatosGeneralesBackend {
    if (!value) {
      return value as unknown as IConvocatoriaReunionDatosGeneralesBackend;
    }
    return {
      id: value.id,
      comite: value.comite,
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      fechaEvaluacion: LuxonUtils.toBackend(value.fechaEvaluacion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaInicioSegunda: value.horaInicioSegunda,
      minutoInicioSegunda: value.minutoInicioSegunda,
      fechaLimite: LuxonUtils.toBackend(value.fechaLimite),
      videoconferencia: value.videoconferencia,
      lugar: value.lugar,
      ordenDia: value.ordenDia,
      anio: value.anio,
      numeroActa: value.numeroActa,
      fechaEnvio: LuxonUtils.toBackend(value.fechaEnvio),
      activo: value.activo,
      numEvaluaciones: value.numEvaluaciones,
      idActa: value.idActa
    };
  }
}

export const CONVOCATORIA_REUNION_DATOS_GENERALES_CONVERTER = new ConvocatoriaReunionDatosGeneralesConverter();
