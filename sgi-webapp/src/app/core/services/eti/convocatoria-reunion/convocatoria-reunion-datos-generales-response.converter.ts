import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaReunionDatosGenerales } from '@core/models/eti/convocatoria-reunion-datos-generales';
import { COMITE_RESPONSE_CONVERTER } from '@core/services/eti/comite/comite-response.converter';
import { IConvocatoriaReunionDatosGeneralesResponse } from '@core/services/eti/convocatoria-reunion/convocatoria-reunion-datos-generales-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaReunionDatosGeneralesResponseConverter
  extends SgiBaseConverter<IConvocatoriaReunionDatosGeneralesResponse, IConvocatoriaReunionDatosGenerales> {
  toTarget(value: IConvocatoriaReunionDatosGeneralesResponse): IConvocatoriaReunionDatosGenerales {
    if (!value) {
      return value as unknown as IConvocatoriaReunionDatosGenerales;
    }
    return {
      id: value.id,
      comite: COMITE_RESPONSE_CONVERTER.toTarget(value.comite),
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      fechaEvaluacion: LuxonUtils.fromBackend(value.fechaEvaluacion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaInicioSegunda: value.horaInicioSegunda,
      minutoInicioSegunda: value.minutoInicioSegunda,
      fechaLimite: LuxonUtils.fromBackend(value.fechaLimite),
      videoconferencia: value.videoconferencia,
      lugar: value.lugar ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.lugar) : [],
      ordenDia: value.ordenDia ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.ordenDia) : [],
      anio: value.anio,
      numeroActa: value.numeroActa,
      fechaEnvio: LuxonUtils.fromBackend(value.fechaEnvio),
      activo: value.activo,
      codigo: `ACTA${value.numeroActa}/${LuxonUtils.fromBackend(value.fechaEvaluacion).year}/${value.comite.codigo}`,
      numEvaluaciones: value.numEvaluaciones,
      idActa: value.idActa
    };
  }

  fromTarget(value: IConvocatoriaReunionDatosGenerales): IConvocatoriaReunionDatosGeneralesResponse {
    if (!value) {
      return value as unknown as IConvocatoriaReunionDatosGeneralesResponse;
    }
    return {
      id: value.id,
      comite: COMITE_RESPONSE_CONVERTER.fromTarget(value.comite),
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      fechaEvaluacion: LuxonUtils.toBackend(value.fechaEvaluacion),
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaInicioSegunda: value.horaInicioSegunda,
      minutoInicioSegunda: value.minutoInicioSegunda,
      fechaLimite: LuxonUtils.toBackend(value.fechaLimite),
      videoconferencia: value.videoconferencia,
      lugar: value.lugar ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.lugar) : [],
      ordenDia: value.ordenDia ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.ordenDia) : [],
      anio: value.anio,
      numeroActa: value.numeroActa,
      fechaEnvio: LuxonUtils.toBackend(value.fechaEnvio),
      activo: value.activo,
      numEvaluaciones: value.numEvaluaciones,
      idActa: value.idActa
    };
  }
}

export const CONVOCATORIA_REUNION_DATOS_GENERALES_RESPONSE_CONVERTER = new ConvocatoriaReunionDatosGeneralesResponseConverter();
