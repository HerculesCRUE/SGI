import { IProyectoProyectoSgeBackend } from '@core/models/csp/backend/proyecto-proyecto-sge-backend';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoProyectoSgeConverter
  extends SgiBaseConverter<IProyectoProyectoSgeBackend, IProyectoProyectoSge> {

  toTarget(value: IProyectoProyectoSgeBackend): IProyectoProyectoSge {
    if (!value) {
      return value as unknown as IProyectoProyectoSge;
    }
    return {
      id: value.id,
      proyectoSge: { id: value.proyectoSgeRef } as IProyectoSge,
      proyecto: { id: value.proyectoId } as IProyecto
    };
  }

  fromTarget(value: IProyectoProyectoSge): IProyectoProyectoSgeBackend {
    if (!value) {
      return value as unknown as IProyectoProyectoSgeBackend;
    }
    return {
      id: value.id,
      proyectoSgeRef: value.proyectoSge.id,
      proyectoId: value.proyecto.id
    };
  }
}

export const PROYECTO_PROYECTO_SGE_CONVERTER = new ProyectoProyectoSgeConverter();
