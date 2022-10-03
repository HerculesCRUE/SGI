import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPeriodoSeguimientoResponse } from './proyecto-periodo-seguimiento-response';

class ProyectoPeriodoSeguimientoResponseConverter
  extends SgiBaseConverter<IProyectoPeriodoSeguimientoResponse, IProyectoPeriodoSeguimiento>{
  toTarget(value: IProyectoPeriodoSeguimientoResponse): IProyectoPeriodoSeguimiento {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimiento;
    }
    return {
      id: value.id,
      convocatoriaPeriodoSeguimientoId: value.convocatoriaPeriodoSeguimientoId,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaFinPresentacion: LuxonUtils.fromBackend(value.fechaFinPresentacion),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaInicioPresentacion: LuxonUtils.fromBackend(value.fechaInicioPresentacion),
      numPeriodo: value.numPeriodo,
      observaciones: value.observaciones,
      proyectoId: value.proyectoId,
      tipoSeguimiento: value.tipoSeguimiento,
      fechaPresentacionDocumentacion: LuxonUtils.fromBackend(value.fechaPresentacionDocumentacion)
    };
  }
  fromTarget(value: IProyectoPeriodoSeguimiento): IProyectoPeriodoSeguimientoResponse {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimientoResponse;
    }
    return {
      id: value.id,
      convocatoriaPeriodoSeguimientoId: value.convocatoriaPeriodoSeguimientoId,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      numPeriodo: value.numPeriodo,
      observaciones: value.observaciones,
      proyectoId: value.proyectoId,
      tipoSeguimiento: value.tipoSeguimiento,
      fechaPresentacionDocumentacion: LuxonUtils.toBackend(value.fechaPresentacionDocumentacion)
    };
  }
}

export const PROYECTO_PERIODO_SEGUIMIENTO_RESPONSE_CONVERTER = new ProyectoPeriodoSeguimientoResponseConverter();
