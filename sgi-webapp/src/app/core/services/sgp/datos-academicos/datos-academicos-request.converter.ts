import { IProyecto } from '@core/models/csp/proyecto';
import { IDatosAcademicos } from '@core/models/sgp/datos-academicos';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IDatosAcademicosRequest } from './datos-academicos-request';

class DatosAcademicosRequestConverter
  extends SgiBaseConverter<IDatosAcademicosRequest, IDatosAcademicos> {
  toTarget(value: IDatosAcademicosRequest): IDatosAcademicos {
    if (!value) {
      return value as unknown as IDatosAcademicos;
    }
    return {
      id: value.id,
      nivelAcademico: value.nivelAcademico,
      fechaObtencion: LuxonUtils.fromBackend(value.fechaObtencion)
    };
  }

  fromTarget(value: IDatosAcademicos): IDatosAcademicosRequest {
    if (!value) {
      return value as unknown as IDatosAcademicosRequest;
    }
    return {
      id: value.id,
      nivelAcademico: value.nivelAcademico,
      fechaObtencion: LuxonUtils.toBackend(value.fechaObtencion)
    };
  }
}

export const DATOS_ACADEMICOS_REQUEST_CONVERTER = new DatosAcademicosRequestConverter();
