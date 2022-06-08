import { IGrupoLineaClasificacion } from '@core/models/csp/grupo-linea-clasificacion';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoLineaClasificacionRequest } from './grupo-linea-clasificacion-request';

class GrupoLineaClasificacionRequestConverter
  extends SgiBaseConverter<IGrupoLineaClasificacionRequest, IGrupoLineaClasificacion> {
  toTarget(value: IGrupoLineaClasificacionRequest): IGrupoLineaClasificacion {
    if (!value) {
      return value as unknown as IGrupoLineaClasificacion;
    }
    return {
      id: undefined,
      grupoLineaInvestigacionId: value.grupoLineaInvestigacionId,
      clasificacion: { id: value.clasificacionRef } as IClasificacion
    };
  }

  fromTarget(value: IGrupoLineaClasificacion): IGrupoLineaClasificacionRequest {
    if (!value) {
      return value as unknown as IGrupoLineaClasificacionRequest;
    }
    return {
      grupoLineaInvestigacionId: value.grupoLineaInvestigacionId,
      clasificacionRef: value.clasificacion.id
    };
  }
}

export const GRUPO_LINEA_CLASIFICACION_REQUEST_CONVERTER = new GrupoLineaClasificacionRequestConverter();
