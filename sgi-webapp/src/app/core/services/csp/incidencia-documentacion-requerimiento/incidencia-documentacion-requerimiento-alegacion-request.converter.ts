import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IIncidenciaDocumentacionRequerimientoAlegacionRequest } from './incidencia-documentacion-requerimiento-alegacion-request';

class IncidenciaDocumentacionRequerimientoAlegacionRequestConverter
  extends SgiBaseConverter<IIncidenciaDocumentacionRequerimientoAlegacionRequest, IIncidenciaDocumentacionRequerimiento> {

  toTarget(value: IIncidenciaDocumentacionRequerimientoAlegacionRequest): IIncidenciaDocumentacionRequerimiento {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IIncidenciaDocumentacionRequerimiento): IIncidenciaDocumentacionRequerimientoAlegacionRequest {
    if (!value) {
      return value as unknown as IIncidenciaDocumentacionRequerimientoAlegacionRequest;
    }
    return {
      alegacion: value.alegacion
    };
  }
}

export const INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_ALEGACION_REQUEST_CONVERTER =
  new IncidenciaDocumentacionRequerimientoAlegacionRequestConverter();
