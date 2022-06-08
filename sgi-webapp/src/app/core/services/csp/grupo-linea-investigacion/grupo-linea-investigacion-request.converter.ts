import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoLineaInvestigacionRequest } from './grupo-linea-investigacion-request';

class GrupoLineaInvestigacionRequestConverter
  extends SgiBaseConverter<IGrupoLineaInvestigacionRequest, IGrupoLineaInvestigacion> {
  toTarget(value: IGrupoLineaInvestigacionRequest): IGrupoLineaInvestigacion {
    if (!value) {
      return value as unknown as IGrupoLineaInvestigacion;
    }
    return {
      id: value.id,
      lineaInvestigacion: value.lineaInvestigacionId ? { id: value.lineaInvestigacionId } as ILineaInvestigacion : undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
    };
  }

  fromTarget(value: IGrupoLineaInvestigacion): IGrupoLineaInvestigacionRequest {
    if (!value) {
      return value as unknown as IGrupoLineaInvestigacionRequest;
    }
    return {
      id: value.id,
      lineaInvestigacionId: value.lineaInvestigacion.id,
      grupoId: value.grupo.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
    };
  }
}

export const GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER = new GrupoLineaInvestigacionRequestConverter();
