import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPeriodoJustificacionRequest } from './proyecto-periodo-justificacion-request';

class ProyectoPeriodoJustificacionRequestConverter
  extends SgiBaseConverter<IProyectoPeriodoJustificacionRequest, IProyectoPeriodoJustificacion> {
  toTarget(value: IProyectoPeriodoJustificacionRequest): IProyectoPeriodoJustificacion {
    if (!value) {
      return value as unknown as IProyectoPeriodoJustificacion;
    }
    return {
      id: value.id,
      proyecto: {
        id: value.proyectoId
      } as IProyecto,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.fromBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.fromBackend(value.fechaFinPresentacion),
      tipoJustificacion: value.tipoJustificacion,
      observaciones: value.observaciones,
      convocatoriaPeriodoJustificacionId: value.convocatoriaPeriodoJustificacionId
    };
  }

  fromTarget(value: IProyectoPeriodoJustificacion): IProyectoPeriodoJustificacionRequest {
    if (!value) {
      return value as unknown as IProyectoPeriodoJustificacionRequest;
    }
    return {
      id: value.id,
      proyectoId: value.proyecto.id,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      tipoJustificacion: value.tipoJustificacion,
      observaciones: value.observaciones,
      convocatoriaPeriodoJustificacionId: value.convocatoriaPeriodoJustificacionId
    };
  }
}

export const PROYECTO_PERIODO_JUSTIFICACION_REQUEST_CONVERTER = new ProyectoPeriodoJustificacionRequestConverter();
