import { IDatosPersonales } from '@core/models/sgp/datos-personales';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IDatosPersonalesRequest } from './datos-personales-request';

class DatosPersonalesRequestConverter
  extends SgiBaseConverter<IDatosPersonalesRequest, IDatosPersonales> {
  toTarget(value: IDatosPersonalesRequest): IDatosPersonales {
    if (!value) {
      return value as unknown as IDatosPersonales;
    }
    return {
      id: value.id,
      paisNacimiento: value.paisNacimiento,
      fechaNacimiento: LuxonUtils.fromBackend(value.fechaNacimiento),
      ciudadNacimiento: value.ciudadNacimiento,
      comAuntonomaNacimiento: value.comAuntonomaNacimiento
    };
  }

  fromTarget(value: IDatosPersonales): IDatosPersonalesRequest {
    if (!value) {
      return value as unknown as IDatosPersonalesRequest;
    }
    return {
      id: value.id,
      paisNacimiento: value.paisNacimiento,
      fechaNacimiento: LuxonUtils.toBackend(value.fechaNacimiento),
      ciudadNacimiento: value.ciudadNacimiento,
      comAuntonomaNacimiento: value.comAuntonomaNacimiento
    };
  }
}

export const DATOS_PERSONALES_REQUEST_CONVERTER = new DatosPersonalesRequestConverter();
