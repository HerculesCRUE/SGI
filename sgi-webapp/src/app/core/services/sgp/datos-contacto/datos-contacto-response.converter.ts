import { IDatosContacto } from '@core/models/sgp/datos-contacto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IDatosContactoResponse } from './datos-contacto-response';

class DatosContactoResponseConverter extends
  SgiBaseConverter<IDatosContactoResponse, IDatosContacto>{
  toTarget(value: IDatosContactoResponse): IDatosContacto {
    if (!value) {
      return value as unknown as IDatosContacto;
    }
    return {
      paisContacto: value.paisContacto,
      comAutonomaContacto: value.comAutonomaContacto,
      provinciaContacto: value.provinciaContacto,
      ciudadContacto: value.ciudadContacto,
      codigoPostalContacto: value.codigoPostalContacto,
      emails: value.emails,
      telefonos: value.telefonos,
      moviles: value.moviles,
      direccionContacto: value.direccionContacto
    };
  }
  fromTarget(value: IDatosContacto): IDatosContactoResponse {
    if (!value) {
      return value as unknown as IDatosContactoResponse;
    }
    return {
      paisContacto: value.paisContacto,
      comAutonomaContacto: value.comAutonomaContacto,
      provinciaContacto: value.provinciaContacto,
      ciudadContacto: value.ciudadContacto,
      codigoPostalContacto: value.codigoPostalContacto,
      emails: value.emails,
      telefonos: value.telefonos,
      moviles: value.moviles,
      direccionContacto: value.direccionContacto
    };
  }
}

export const DATOS_CONTACTO_RESPONSE_CONVERTER = new DatosContactoResponseConverter();
