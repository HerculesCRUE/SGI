import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGastoRequerimientoJustificacionResponse } from './gasto-requerimiento-justificacion-response';

class GastoRequerimientoJustificacionResponseConverter
  extends SgiBaseConverter<IGastoRequerimientoJustificacionResponse, IGastoRequerimientoJustificacion>{
  toTarget(value: IGastoRequerimientoJustificacionResponse): IGastoRequerimientoJustificacion {
    if (!value) {
      return value as unknown as IGastoRequerimientoJustificacion;
    }
    return {
      id: value.id,
      aceptado: value.aceptado,
      alegacion: value.alegacion,
      gasto: value.gastoRef ? { id: value.gastoRef } as IGastoJustificado : null,
      identificadorJustificacion: value.identificadorJustificacion,
      importeAceptado: value.importeAceptado,
      importeAlegado: value.importeAlegado,
      importeRechazado: value.importeRechazado,
      incidencia: value.incidencia,
      requerimientoJustificacion: value.requerimientoJustificacionId ?
        { id: value.requerimientoJustificacionId } as IRequerimientoJustificacion : null
    };
  }
  fromTarget(value: IGastoRequerimientoJustificacion): IGastoRequerimientoJustificacionResponse {
    if (!value) {
      return value as unknown as IGastoRequerimientoJustificacionResponse;
    }
    return {
      id: value.id,
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

export const GASTO_REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER = new GastoRequerimientoJustificacionResponseConverter();
