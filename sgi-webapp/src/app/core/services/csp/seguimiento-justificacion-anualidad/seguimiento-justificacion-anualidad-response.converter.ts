import { IProyectoPeriodoJustificacionSeguimiento } from '@core/models/csp/proyecto-periodo-justificacion-seguimiento';
import { ISeguimientoJustificacionAnualidad } from '@core/models/csp/seguimiento-justificacion-anualidad';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISeguimientoJustificacionAnualidadResponse } from './seguimiento-justificacion-anualidad-response';

class SeguimientoJustificacionAnualidadResponseConverter
  extends SgiBaseConverter<ISeguimientoJustificacionAnualidadResponse, ISeguimientoJustificacionAnualidad>{
  toTarget(value: ISeguimientoJustificacionAnualidadResponse): ISeguimientoJustificacionAnualidad {
    if (!value) {
      return value as unknown as ISeguimientoJustificacionAnualidad;
    }
    return {
      anio: value.anio,
      fechaPresentacionJustificacion: LuxonUtils.fromBackend(value.fechaPresentacionJustificacion),
      identificadorJustificacion: value.identificadorJustificacion,
      proyectoId: value.proyectoId,
      proyectoPeriodoJustificacionId: value.proyectoPeriodoJustificacionId,
      proyectoPeriodoJustificacionSeguimiento: value.proyectoPeriodoJustificacionSeguimientoId ?
        { id: value.proyectoPeriodoJustificacionSeguimientoId } as IProyectoPeriodoJustificacionSeguimiento : null
    };
  }
  fromTarget(value: ISeguimientoJustificacionAnualidad): ISeguimientoJustificacionAnualidadResponse {
    if (!value) {
      return value as unknown as ISeguimientoJustificacionAnualidadResponse;
    }
    return {
      anio: value.anio,
      fechaPresentacionJustificacion: LuxonUtils.toBackend(value.fechaPresentacionJustificacion),
      identificadorJustificacion: value.identificadorJustificacion,
      proyectoId: value.proyectoId,
      proyectoPeriodoJustificacionId: value.proyectoPeriodoJustificacionId,
      proyectoPeriodoJustificacionSeguimientoId: value.proyectoPeriodoJustificacionSeguimiento?.id
    };
  }
}

export const SEGUIMIENTO_JUSTIFICACION_ANUALIDAD_RESPONSE_CONVERTER = new SeguimientoJustificacionAnualidadResponseConverter();
