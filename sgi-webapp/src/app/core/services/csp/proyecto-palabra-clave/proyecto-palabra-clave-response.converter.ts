import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPalabraClave } from '@core/models/csp/proyecto-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPalabraClaveResponse } from './proyecto-palabra-clave-response';

class IProyectoPalabraClaveResponseConverter extends SgiBaseConverter<IProyectoPalabraClaveResponse, IProyectoPalabraClave> {

  toTarget(value: IProyectoPalabraClaveResponse): IProyectoPalabraClave {
    if (!value) {
      return value as unknown as IProyectoPalabraClave;
    }

    return {
      id: value.id,
      proyecto: { id: value.proyectoId } as IProyecto,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: IProyectoPalabraClave): IProyectoPalabraClaveResponse {
    if (!value) {
      return value as unknown as IProyectoPalabraClaveResponse;
    }

    return {
      id: value.id,
      proyectoId: value.proyecto?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const PROYECTO_PALABRACLAVE_RESPONSE_CONVERTER = new IProyectoPalabraClaveResponseConverter();
