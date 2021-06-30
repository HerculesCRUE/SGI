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
    };
  }
}

export const PERSONA_CONVERTER = new PersonaConverter();
