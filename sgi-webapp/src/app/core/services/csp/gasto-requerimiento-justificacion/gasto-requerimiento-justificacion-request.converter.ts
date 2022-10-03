import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGastoRequerimientoJustificacionRequest } from './gasto-requerimiento-justificacion-request';

class GastoRequerimientoJustificacionRequestConverter
  extends SgiBaseConverter<IGastoRequerimientoJustificacionRequest, IGastoRequerimientoJustificacion>{

  toTarget(value: IGastoRequerimientoJustificacionRequest): IGastoRequerimientoJustificacion {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IGastoRequerimientoJustificacion): IGastoRequerimientoJustificacionRequest {
    if (!value) {
      return value as unknown as IGastoRequerimientoJustificacionRequest;
    }
    return {
      aceptado: value.aceptado,
      alegacion: value.alegacion,
      gastoRef: value.gasto?.id,
      identificadorJustificacion: value.identificadorJustificacion,
      importeAceptado: value.importeAceptado,
      importeAlegado: value.importeAlegado,
      importeRechazado: value.importeRechazado,
      incidencia: value.incidencia,
      requerimientoJustificacionId: value.requerimientoJustificacion?.id
    };
  }
}

export const GASTO_REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER = new GastoRequerimientoJustificacionRequestConverter();
