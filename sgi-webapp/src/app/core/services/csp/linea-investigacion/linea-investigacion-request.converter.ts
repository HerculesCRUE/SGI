import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ILineaInvestigacionRequest } from './linea-investigacion-request';

class LineaInvestigacionRequestConverter extends SgiBaseConverter<ILineaInvestigacionRequest, ILineaInvestigacion>{
  toTarget(value: ILineaInvestigacionRequest): ILineaInvestigacion {
    if (!value) {
      return value as unknown as ILineaInvestigacion;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      activo: true
    };
  }
  fromTarget(value: ILineaInvestigacion): ILineaInvestigacionRequest {
    if (!value) {
      return value as unknown as ILineaInvestigacionRequest;
    }
    return {
      nombre: value.nombre,
    };
  }
}

export const LINEA_INVESTIGACION_REQUEST_CONVERTER = new LineaInvestigacionRequestConverter();
