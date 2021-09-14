import { IDatosAcademicos } from '@core/models/sgp/datos-academicos';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IDatosAcademicosResponse } from './datos-academicos-response';

class DatosAcademicosResponseConverter extends
  SgiBaseConverter<IDatosAcademicosResponse, IDatosAcademicos>{
  toTarget(value: IDatosAcademicosResponse): IDatosAcademicos {
    if (!value) {
      return value as unknown as IDatosAcademicos;
    }
    return {
      id: value.id,
      nivelAcademico: value.nivelAcademico,
      fechaObtencion: LuxonUtils.fromBackend(value.fechaObtencion)
    };
  }
  fromTarget(value: IDatosAcademicos): IDatosAcademicosResponse {
    if (!value) {
      return value as unknown as IDatosAcademicosResponse;
    }
    return {
      id: value.id,
      nivelAcademico: value.nivelAcademico,
      fechaObtencion: LuxonUtils.toBackend(value.fechaObtencion)
    };
  }
}

export const DATOS_ACADEMICOS_RESPONSE_CONVERTER = new DatosAcademicosResponseConverter();
