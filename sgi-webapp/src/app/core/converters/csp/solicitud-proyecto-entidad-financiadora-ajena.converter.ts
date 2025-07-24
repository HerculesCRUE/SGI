import { ISolicitudProyectoEntidadFinanciadoraAjenaBackend } from '@core/models/csp/backend/solicitud-proyecto-entidad-financiadora-ajena-backend';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FUENTE_FINANCIACION_RESPONSE_CONVERTER } from '@core/services/csp/fuente-financiacion/fuente-financiacion-response.converter';
import { TIPO_FINANCIACION_RESPONSE_CONVERTER } from '@core/services/csp/tipo-financiacion/tipo-financiacion-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoEntidadFinanciadoraAjenaConverter extends
  SgiBaseConverter<ISolicitudProyectoEntidadFinanciadoraAjenaBackend, ISolicitudProyectoEntidadFinanciadoraAjena> {

  toTarget(value: ISolicitudProyectoEntidadFinanciadoraAjenaBackend): ISolicitudProyectoEntidadFinanciadoraAjena {
    if (!value) {
      return value as unknown as ISolicitudProyectoEntidadFinanciadoraAjena;
    }
    return {
      id: value.id,
      empresa: { id: value.entidadRef } as IEmpresa,
      solicitudProyectoId: value.solicitudProyectoId,
      fuenteFinanciacion: FUENTE_FINANCIACION_RESPONSE_CONVERTER.toTarget(value.fuenteFinanciacion),
      tipoFinanciacion: TIPO_FINANCIACION_RESPONSE_CONVERTER.toTarget(value.tipoFinanciacion),
      porcentajeFinanciacion: value.porcentajeFinanciacion,
      importeFinanciacion: value.importeFinanciacion
    };
  }

  fromTarget(value: ISolicitudProyectoEntidadFinanciadoraAjena): ISolicitudProyectoEntidadFinanciadoraAjenaBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoEntidadFinanciadoraAjenaBackend;
    }
    return {
      id: value.id,
      entidadRef: value.empresa.id,
      solicitudProyectoId: value.solicitudProyectoId,
      fuenteFinanciacion: FUENTE_FINANCIACION_RESPONSE_CONVERTER.fromTarget(value.fuenteFinanciacion),
      tipoFinanciacion: TIPO_FINANCIACION_RESPONSE_CONVERTER.fromTarget(value.tipoFinanciacion),
      porcentajeFinanciacion: value.porcentajeFinanciacion,
      importeFinanciacion: value.importeFinanciacion
    };
  }
}

export const SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER = new SolicitudProyectoEntidadFinanciadoraAjenaConverter();
