import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPalabraClave } from '@core/models/csp/proyecto-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPalabraClaveRequest } from './proyecto-palabra-clave-request';

class IProyectoPalabraClaveRequestConverter extends SgiBaseConverter<IProyectoPalabraClaveRequest, IProyectoPalabraClave> {

  toTarget(value: IProyectoPalabraClaveRequest): IProyectoPalabraClave {
    if (!value) {
      return value as unknown as IProyectoPalabraClave;
    }

    return {
      id: undefined,
      proyecto: { id: value.proyectoId } as IProyecto,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: IProyectoPalabraClave): IProyectoPalabraClaveRequest {
    if (!value) {
      return value as unknown as IProyectoPalabraClaveRequest;
    }

    return {
      proyectoId: value.proyecto?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const PROYECTO_PALABRACLAVE_REQUEST_CONVERTER = new IProyectoPalabraClaveRequestConverter();
