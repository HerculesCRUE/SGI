import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersonaBackend } from '@core/models/sgp/backend/persona-backend';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';

class PersonaConverter extends SgiBaseConverter<IPersonaBackend, IPersona> {
  toTarget(value: IPersonaBackend): IPersona {
    if (!value) {
      return value as unknown as IPersona;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      apellidos: value.apellidos,
      sexo: value.sexo,
      tipoDocumento: value.tipoDocumento,
      numeroDocumento: value.numeroDocumento,
      vinculacion: value.vinculacion,
      datosAcademicos: value.datosAcademicos,
      entidad: value.empresaRef ? { id: value.empresaRef } as IEmpresa : undefined,
      personalPropio: value.personalPropio,
      entidadPropia: value.entidadPropiaRef ? { id: value.entidadPropiaRef } as IEmpresa : undefined,
      emails: value.emails,
    };
  }

  fromTarget(value: IPersona): IPersonaBackend {
    if (!value) {
      return value as unknown as IPersonaBackend;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      apellidos: value.apellidos,
      sexo: value.sexo,
      tipoDocumento: value.tipoDocumento,
      numeroDocumento: value.numeroDocumento,
      vinculacion: value.vinculacion,
      datosAcademicos: value.datosAcademicos,
      empresaRef: value.entidad?.id,
      personalPropio: value.personalPropio,
      entidadPropiaRef: value.entidadPropia?.id,
      emails: value.emails,
    };
  }
}

export const PERSONA_CONVERTER = new PersonaConverter();
