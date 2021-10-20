import { IProyectoAnualidadNotificacionSge } from '@core/models/csp/proyecto-anualidad-notificacion-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAnualidadNotificacionSgeRequest } from './proyecto-anualidad-notificacion-sge-request';

class ProyectoAnualidadNotificacionSgeRequestConverter extends
  SgiBaseConverter<IProyectoAnualidadNotificacionSgeRequest, IProyectoAnualidadNotificacionSge>{
  toTarget(value: IProyectoAnualidadNotificacionSgeRequest): IProyectoAnualidadNotificacionSge {
    if (!value) {
      return value as unknown as IProyectoAnualidadNotificacionSge;
    }
    return {
      id: undefined,
      anio: value.anio,
      proyectoFechaInicio: LuxonUtils.fromBackend(value.proyectoFechaInicio),
      proyectoFechaFin: LuxonUtils.fromBackend(value.proyectoFechaFin),
      totalGastos: value.totalGastos,
      totalIngresos: value.totalIngresos,
      proyectoTitulo: value.proyectoTitulo,
      proyectoAcronimo: value.proyectoAcronimo,
      proyectoEstado: value.proyectoEstado,
      proyectoSgeRef: value.proyectoSgeRef,
      proyectoId: value.proyectoId,
      enviadoSge: value.enviadoSge
    };
  }
  fromTarget(value: IProyectoAnualidadNotificacionSge): IProyectoAnualidadNotificacionSgeRequest {
    if (!value) {
      return value as unknown as IProyectoAnualidadNotificacionSgeRequest;
    }
    return {
      anio: value.anio,
      proyectoFechaInicio: LuxonUtils.toBackend(value.proyectoFechaInicio),
      proyectoFechaFin: LuxonUtils.toBackend(value.proyectoFechaFin),
      totalGastos: value.totalGastos,
      totalIngresos: value.totalIngresos,
      proyectoTitulo: value.proyectoTitulo,
      proyectoAcronimo: value.proyectoAcronimo,
      proyectoEstado: value.proyectoEstado,
      proyectoSgeRef: value.proyectoSgeRef,
      proyectoId: value.proyectoId,
      enviadoSge: value.enviadoSge
    };
  }
}

export const PROYECTO_ANUALIDAD_NOTIFICACION_SGE_REQUEST_CONVERTER = new ProyectoAnualidadNotificacionSgeRequestConverter();
