import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyectoAnualidadNotificacionSge } from '@core/models/csp/proyecto-anualidad-notificacion-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAnualidadNotificacionSgeResponse } from './proyecto-anualidad-notificacion-sge-response';

class ProyectoAnualidadNotificacionSgeResponseConverter
  extends SgiBaseConverter<IProyectoAnualidadNotificacionSgeResponse, IProyectoAnualidadNotificacionSge>{
  toTarget(value: IProyectoAnualidadNotificacionSgeResponse): IProyectoAnualidadNotificacionSge {
    if (!value) {
      return value as unknown as IProyectoAnualidadNotificacionSge;
    }
    return {
      id: value.id,
      anio: value.anio,
      proyectoFechaInicio: LuxonUtils.fromBackend(value.proyectoFechaInicio),
      proyectoFechaFin: LuxonUtils.fromBackend(value.proyectoFechaFin),
      totalGastos: value.totalGastos,
      totalIngresos: value.totalIngresos,
      proyectoTitulo: value.proyectoTitulo,
      proyectoAcronimo: value.proyectoAcronimo,
      proyectoEstado: value.proyectoEstado?.estado,
      proyectoSgeRef: value.proyectoSgeRef,
      proyectoId: value.proyectoId,
      enviadoSge: value.enviadoSge
    };
  }
  fromTarget(value: IProyectoAnualidadNotificacionSge): IProyectoAnualidadNotificacionSgeResponse {
    if (!value) {
      return value as unknown as IProyectoAnualidadNotificacionSgeResponse;
    }
    return {
      id: value.id,
      anio: value.anio,
      proyectoFechaInicio: LuxonUtils.toBackend(value.proyectoFechaInicio),
      proyectoFechaFin: LuxonUtils.toBackend(value.proyectoFechaFin),
      totalGastos: value.totalGastos,
      totalIngresos: value.totalIngresos,
      proyectoTitulo: value.proyectoTitulo,
      proyectoAcronimo: value.proyectoAcronimo,
      proyectoEstado: { estado: value.proyectoEstado } as IEstadoProyecto,
      proyectoSgeRef: value.proyectoSgeRef,
      proyectoId: value.proyectoId,
      enviadoSge: value.enviadoSge
    };
  }
}

export const PROYECTO_ANUALIDAD_NOTIFICACION_SGE_RESPONSE_CONVERTER = new ProyectoAnualidadNotificacionSgeResponseConverter();
