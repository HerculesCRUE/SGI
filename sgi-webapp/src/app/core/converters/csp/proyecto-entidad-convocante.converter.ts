import { IProyectoEntidadConvocanteBackend } from '@core/models/csp/backend/proyecto-entidad-convocante-backend';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoEntidadConvocanteConverter extends SgiBaseConverter<IProyectoEntidadConvocanteBackend, IProyectoEntidadConvocante> {

  toTarget(value: IProyectoEntidadConvocanteBackend): IProyectoEntidadConvocante {
    if (!value) {
      return value as unknown as IProyectoEntidadConvocante;
    }
    return {
      id: value.id,
      entidad: { id: value.entidadRef } as IEmpresa,
      programaConvocatoria: value.programaConvocatoria,
      programa: value.programa
    };
  }

  fromTarget(value: IProyectoEntidadConvocante): IProyectoEntidadConvocanteBackend {
    if (!value) {
      return value as unknown as IProyectoEntidadConvocanteBackend;
    }
    return {
      id: value.id,
      entidadRef: value.entidad?.id,
      programaConvocatoria: value.programaConvocatoria,
      programa: value.programa
    };
  }
}

export const PROYECTO_ENTIDAD_CONVOCANTE_CONVERTER = new ProyectoEntidadConvocanteConverter();
