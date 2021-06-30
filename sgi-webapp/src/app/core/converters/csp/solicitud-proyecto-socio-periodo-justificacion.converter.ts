import { ISolicitudProyectoSocioPeriodoJustificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-periodo-justificacion-backend';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoSocioPeriodoJustificacionConverter extends
  SgiBaseConverter<ISolicitudProyectoSocioPeriodoJustificacionBackend, ISolicitudProyectoSocioPeriodoJustificacion> {

  toTarget(value: ISolicitudProyectoSocioPeriodoJustificacionBackend): ISolicitudProyectoSocioPeriodoJustificacion {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioPeriodoJustificacion;
    }
    return {
      id: value.id,
      solicitudProyectoSocioId: value.solicitudProyectoSocioId,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      observaciones: value.observaciones
    };
  }

  fromTarget(value: ISolicitudProyectoSocioPeriodoJustificacion): ISolicitudProyectoSocioPeriodoJustificacionBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioPeriodoJustificacionBackend;
    }
    return {
      id: value.id,
      solicitudProyectoSocioId: value.solicitudProyectoSocioId,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      observaciones: value.observaciones
    };
  }
}

export const SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER = new SolicitudProyectoSocioPeriodoJustificacionConverter();
