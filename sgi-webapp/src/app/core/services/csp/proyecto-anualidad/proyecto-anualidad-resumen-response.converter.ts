import { IProyectoAnualidadResumen } from '@core/models/csp/proyecto-anualidad-resumen';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAnualidadResumenResponse } from './proyecto-anualidad-resumen-response';

class ProyectoAnualidadResumenResponseConverter extends SgiBaseConverter<IProyectoAnualidadResumenResponse, IProyectoAnualidadResumen>{
  toTarget(value: IProyectoAnualidadResumenResponse): IProyectoAnualidadResumen {
    if (!value) {
      return value as unknown as IProyectoAnualidadResumen;
    }
    return {
      id: value.id,
      anio: value.anio,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      totalGastosConcedido: value.totalGastosConcedido,
      totalGastosPresupuesto: value.totalGastosPresupuesto,
      totalIngresos: value.totalIngresos,
      presupuestar: value.presupuestar,
      enviadoSge: value.enviadoSge
    };
  }
  fromTarget(value: IProyectoAnualidadResumen): IProyectoAnualidadResumenResponse {
    if (!value) {
      return value as unknown as IProyectoAnualidadResumenResponse;
    }
    return {
      id: value.id,
      anio: value.anio,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      totalGastosConcedido: value.totalGastosConcedido,
      totalGastosPresupuesto: value.totalGastosPresupuesto,
      totalIngresos: value.totalIngresos,
      presupuestar: value.presupuestar,
      enviadoSge: value.enviadoSge
    };
  }
}

export const PROYECTO_ANUALIDAD_RESUMEN_RESPONSE_CONVERTER = new ProyectoAnualidadResumenResponseConverter();
