import { IDatosPersonales } from '@core/models/sgp/datos-personales';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IDatosPersonalesResponse } from './datos-personales-response';

class DatosPersonalesResponseConverter extends
  SgiBaseConverter<IDatosPersonalesResponse, IDatosPersonales>{
  toTarget(value: IDatosPersonalesResponse): IDatosPersonales {
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
  fromTarget(value: IDatosPersonales): IDatosPersonalesResponse {
    if (!value) {
      return value as unknown as IDatosPersonalesResponse;
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

export const DATOS_PERSONALES_RESPONSE_CONVERTER = new DatosPersonalesResponseConverter();
