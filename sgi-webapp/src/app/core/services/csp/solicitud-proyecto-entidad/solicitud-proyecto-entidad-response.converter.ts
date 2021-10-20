import { CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-financiadora.converter';
import { CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-gestora.converter';
import { SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER } from '@core/converters/csp/solicitud-proyecto-entidad-financiadora-ajena.converter';
import { ISolicitudProyectoEntidad } from '@core/models/csp/solicitud-proyecto-entidad';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoEntidadResponse } from './solicitud-proyecto-entidad-response';

class SolicitudProyectoEntidadResponseConverter
  extends SgiBaseConverter<ISolicitudProyectoEntidadResponse, ISolicitudProyectoEntidad>{
  toTarget(value: ISolicitudProyectoEntidadResponse): ISolicitudProyectoEntidad {
    if (!value) {
      return value as unknown as ISolicitudProyectoEntidad;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      solicitudProyectoEntidadFinanciadoraAjena: SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER
        .toTarget(value.solicitudProyectoEntidadFinanciadoraAjena),
      convocatoriaEntidadFinanciadora: CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER.toTarget(value.convocatoriaEntidadFinanciadora),
      convocatoriaEntidadGestora: CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER.toTarget(value.convocatoriaEntidadGestora)
    };
  }
  fromTarget(value: ISolicitudProyectoEntidad): ISolicitudProyectoEntidadResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoEntidadResponse;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      solicitudProyectoEntidadFinanciadoraAjena: SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER
        .fromTarget(value.solicitudProyectoEntidadFinanciadoraAjena),
      convocatoriaEntidadFinanciadora: CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER.fromTarget(value.convocatoriaEntidadFinanciadora),
      convocatoriaEntidadGestora: CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER.fromTarget(value.convocatoriaEntidadGestora)
    };
  }
}

export const SOLICITUD_PROYECTO_ENTIDAD_RESPONSE_CONVERTER = new SolicitudProyectoEntidadResponseConverter();
