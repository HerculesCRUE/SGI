import { IGrupoLineaClasificacion } from '@core/models/csp/grupo-linea-clasificacion';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoLineaClasificacionResponse } from './grupo-linea-clasificacion-response';

class GrupoLineaClasificacionResponseConverter
  extends SgiBaseConverter<IGrupoLineaClasificacionResponse, IGrupoLineaClasificacion> {
  toTarget(value: IGrupoLineaClasificacionResponse): IGrupoLineaClasificacion {
    if (!value) {
      return value as unknown as IGrupoLineaClasificacion;
    }
    return {
      id: value.id,
      grupoLineaInvestigacionId: undefined,
      clasificacion: value.clasificacionRef ? { id: value.clasificacionRef } as IClasificacion : undefined,
    };
  }

  fromTarget(value: IGrupoLineaClasificacion): IGrupoLineaClasificacionResponse {
    if (!value) {
      return value as unknown as IGrupoLineaClasificacionResponse;
    }
    return {
      id: value.id,
      clasificacionRef: value.clasificacion.id
    };
  }
}

export const GRUPO_LINEA_CLASIFICACION_RESPONSE_CONVERTER = new GrupoLineaClasificacionResponseConverter();
