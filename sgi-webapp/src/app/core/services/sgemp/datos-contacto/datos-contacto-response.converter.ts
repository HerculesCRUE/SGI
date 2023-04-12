import { IDatosContacto } from '@core/models/sgemp/datos-contacto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IDatosContactoResponse } from './datos-contacto-response';

class DatosContactoResponseConverter extends
  SgiBaseConverter<IDatosContactoResponse, IDatosContacto>{
  toTarget(value: IDatosContactoResponse): IDatosContacto {
    if (!value) {
      return value as unknown as IDatosContacto;
    }
    return {
      direccion: value.direccion
    };
  }
  fromTarget(value: IDatosContacto): IDatosContactoResponse {
    if (!value) {
      return value as unknown as IDatosContactoResponse;
    }
    return {
      direccion: value.direccion
    };
  }
}

export const DATOS_CONTACTO_RESPONSE_CONVERTER = new DatosContactoResponseConverter();
