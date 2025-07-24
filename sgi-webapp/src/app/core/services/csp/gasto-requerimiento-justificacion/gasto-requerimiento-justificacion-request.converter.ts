import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGastoRequerimientoJustificacionRequest } from './gasto-requerimiento-justificacion-request';

class GastoRequerimientoJustificacionRequestConverter
  extends SgiBaseConverter<IGastoRequerimientoJustificacionRequest, IGastoRequerimientoJustificacion> {

  toTarget(value: IGastoRequerimientoJustificacionRequest): IGastoRequerimientoJustificacion {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IGastoRequerimientoJustificacion): IGastoRequerimientoJustificacionRequest {
    if (!value) {
      return value as unknown as IGastoRequerimientoJustificacionRequest;
    }
    return {
      aceptado: value.aceptado,
      alegacion: value.alegacion ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.alegacion) : [],
      gastoRef: value.gasto?.id,
      identificadorJustificacion: value.identificadorJustificacion,
      importeAceptado: value.importeAceptado,
      importeAlegado: value.importeAlegado,
      importeRechazado: value.importeRechazado,
      incidencia: value.incidencia ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.incidencia) : [],
      requerimientoJustificacionId: value.requerimientoJustificacion?.id
    };
  }
}

export const GASTO_REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER = new GastoRequerimientoJustificacionRequestConverter();
