import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IIncidenciaDocumentacionRequerimientoResponse } from './incidencia-documentacion-requerimiento-response';

class IncidenciaDocumentacionRequerimientoResponseConverter
  extends SgiBaseConverter<IIncidenciaDocumentacionRequerimientoResponse, IIncidenciaDocumentacionRequerimiento> {
  toTarget(value: IIncidenciaDocumentacionRequerimientoResponse): IIncidenciaDocumentacionRequerimiento {
    if (!value) {
      return value as unknown as IIncidenciaDocumentacionRequerimiento;
    }
    return {
      id: value.id,
      alegacion: value.alegacion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.alegacion) : [],
      incidencia: value.incidencia ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.incidencia) : [],
      nombreDocumento: value.nombreDocumento ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombreDocumento) : [],
      requerimientoJustificacion: value.requerimientoJustificacionId ?
        { id: value.requerimientoJustificacionId } as IRequerimientoJustificacion : null
    };
  }

  fromTarget(value: IIncidenciaDocumentacionRequerimiento): IIncidenciaDocumentacionRequerimientoResponse {
    throw new Error('Method not implemented.');
  }
}

export const INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER = new IncidenciaDocumentacionRequerimientoResponseConverter();
