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
      alegacion: value.alegacion,
      incidencia: value.incidencia,
      nombreDocumento: value.nombreDocumento,
      requerimientoJustificacion: value.requerimientoJustificacionId ?
        { id: value.requerimientoJustificacionId } as IRequerimientoJustificacion : null
    };
  }

  fromTarget(value: IIncidenciaDocumentacionRequerimiento): IIncidenciaDocumentacionRequerimientoResponse {
    throw new Error('Method not implemented.');
  }
}

export const INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_RESPONSE_CONVERTER = new IncidenciaDocumentacionRequerimientoResponseConverter();
