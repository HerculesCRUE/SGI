import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAnualidadResponse } from './proyecto-anualidad-response';

class ProyectoAnualidadResponseConverter extends SgiBaseConverter<IProyectoAnualidadResponse, IProyectoAnualidad>{
  toTarget(value: IProyectoAnualidadResponse): IProyectoAnualidad {
    if (!value) {
      return value as unknown as IProyectoAnualidad;
    }
    return {
      id: value.id,
      proyecto: {
        id: value.proyectoId,
      } as IProyecto,
      anio: value.anio,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      presupuestar: value.presupuestar,
      enviadoSge: value.enviadoSge
    };
  }
  fromTarget(value: IProyectoAnualidad): IProyectoAnualidadResponse {
    if (!value) {
      return value as unknown as IProyectoAnualidadResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyecto.id,
      anio: value.anio,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      presupuestar: value.presupuestar,
      enviadoSge: value.enviadoSge
    };
  }
}

export const PROYECTO_ANUALIDAD_RESPONSE_CONVERTER = new ProyectoAnualidadResponseConverter();
