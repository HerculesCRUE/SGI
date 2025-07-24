import { ISolicitudProyectoSocioPeriodoJustificacionResponse } from '@core/services/csp/solicitud-proyecto-socio-periodo-justificacion/solicitud-proyecto-socio-periodo-justificacion-response';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';

class SolicitudProyectoSocioPeriodoJustificacionResponseConverter extends
  SgiBaseConverter<ISolicitudProyectoSocioPeriodoJustificacionResponse, ISolicitudProyectoSocioPeriodoJustificacion> {

  toTarget(value: ISolicitudProyectoSocioPeriodoJustificacionResponse): ISolicitudProyectoSocioPeriodoJustificacion {
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
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : []
    };
  }

  fromTarget(value: ISolicitudProyectoSocioPeriodoJustificacion): ISolicitudProyectoSocioPeriodoJustificacionResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioPeriodoJustificacionResponse;
    }
    return {
      id: value.id,
      solicitudProyectoSocioId: value.solicitudProyectoSocioId,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : []
    };
  }
}

export const SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER = new SolicitudProyectoSocioPeriodoJustificacionResponseConverter();
