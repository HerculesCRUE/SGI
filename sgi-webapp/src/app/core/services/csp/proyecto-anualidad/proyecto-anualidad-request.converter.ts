import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAnualidadRequest } from './proyecto-anualidad-request';

class ProyectoAnualidadRequestConverter extends SgiBaseConverter<IProyectoAnualidadRequest, IProyectoAnualidad>{
  toTarget(value: IProyectoAnualidadRequest): IProyectoAnualidad {
    if (!value) {
      return value as unknown as IProyectoAnualidad;
    }
    return {
      id: undefined,
      proyecto: { id: value.proyectoId } as IProyecto,
      anio: value.anio,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      presupuestar: value.presupuestar,
      enviadoSge: value.enviadoSge
    };
  }
  fromTarget(value: IProyectoAnualidad): IProyectoAnualidadRequest {
    if (!value) {
      return value as unknown as IProyectoAnualidadRequest;
    }
    return {
      proyectoId: value.proyecto?.id,
      anio: value.anio,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      presupuestar: value.presupuestar,
      enviadoSge: value.enviadoSge
    };
  }
}

export const PROYECTO_ANUALIDAD_REQUEST_CONVERTER = new ProyectoAnualidadRequestConverter();
