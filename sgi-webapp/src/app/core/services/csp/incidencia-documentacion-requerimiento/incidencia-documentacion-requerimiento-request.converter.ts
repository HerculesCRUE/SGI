import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IIncidenciaDocumentacionRequerimientoRequest } from './incidencia-documentacion-requerimiento-request';

class IncidenciaDocumentacionRequerimientoIncidenciaRequestConverter
  extends SgiBaseConverter<IIncidenciaDocumentacionRequerimientoRequest, IIncidenciaDocumentacionRequerimiento> {

  toTarget(value: IIncidenciaDocumentacionRequerimientoRequest): IIncidenciaDocumentacionRequerimiento {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IIncidenciaDocumentacionRequerimiento): IIncidenciaDocumentacionRequerimientoRequest {
    if (!value) {
      return value as unknown as IIncidenciaDocumentacionRequerimientoRequest;
    }
    return {
      incidencia: value.incidencia,
      nombreDocumento: value.nombreDocumento,
      requerimientoJustificacionId: value.requerimientoJustificacion?.id
    };
  }
}

export const INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_INCIDENCIA_REQUEST_CONVERTER =
  new IncidenciaDocumentacionRequerimientoIncidenciaRequestConverter();
