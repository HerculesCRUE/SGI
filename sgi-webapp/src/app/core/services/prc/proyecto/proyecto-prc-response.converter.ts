import { IProyectoResumen } from '@core/models/csp/proyecto-resumen';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IProyectoPrc } from '@core/models/prc/proyecto-prc';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPrcResponse } from './proyecto-prc-response';

class ProyectoPrcResponseConverter extends SgiBaseConverter<IProyectoPrcResponse, IProyectoPrc>{
  toTarget(value: IProyectoPrcResponse): IProyectoPrc {
    if (!value) {
      return value as unknown as IProyectoPrc;
    }
    return {
      id: value.id,
      produccionCientifica: value.produccionCientificaId ?
        { id: value.produccionCientificaId } as IProduccionCientifica : null,
      proyecto: value.proyectoRef ?
        { id: value.proyectoRef } as IProyectoResumen : null
    };
  }
  fromTarget(value: IProyectoPrc): IProyectoPrcResponse {
    if (!value) {
      return value as unknown as IProyectoPrcResponse;
    }
    return {
      id: value.id,
      produccionCientificaId: value.produccionCientifica?.id,
      proyectoRef: value.proyecto?.id
    };
  }
}

export const PROYECTO_PRC_RESPONSE_CONVERTER = new ProyectoPrcResponseConverter();
