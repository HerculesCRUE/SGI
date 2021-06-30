import { IProyectoEntidadGestoraBackend } from '@core/models/csp/backend/proyecto-entidad-gestora-backend';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoEntidadGestoraConverter extends SgiBaseConverter<IProyectoEntidadGestoraBackend, IProyectoEntidadGestora> {

  toTarget(value: IProyectoEntidadGestoraBackend): IProyectoEntidadGestora {
    if (!value) {
      return value as unknown as IProyectoEntidadGestora;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      empresa: { id: value.entidadRef } as IEmpresa
    };
  }

  fromTarget(value: IProyectoEntidadGestora): IProyectoEntidadGestoraBackend {
    if (!value) {
      return value as unknown as IProyectoEntidadGestoraBackend;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      entidadRef: value.empresa?.id
    };
  }
}

export const PROYECTO_ENTIDAD_GESTORA_CONVERTER = new ProyectoEntidadGestoraConverter();
