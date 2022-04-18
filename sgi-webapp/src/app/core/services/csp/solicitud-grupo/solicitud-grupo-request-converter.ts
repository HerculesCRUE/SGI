import { IGrupo } from '@core/models/csp/grupo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudGrupo } from '@core/models/csp/solicitud-grupo';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudGrupoRequest } from './solicitud-grupo-request';

class SolicitudGrupoRequestConverter
  extends SgiBaseConverter<ISolicitudGrupoRequest, ISolicitudGrupo> {
  toTarget(value: ISolicitudGrupoRequest): ISolicitudGrupo {
    if (!value) {
      return value as unknown as ISolicitudGrupo;
    }
    return {
      id: undefined,
      grupo: { id: value.grupoId } as IGrupo,
      solicitud: { id: value.solicitudId } as ISolicitud
    };
  }

  fromTarget(value: ISolicitudGrupo): ISolicitudGrupoRequest {
    if (!value) {
      return value as unknown as ISolicitudGrupoRequest;
    }
    return {
      grupoId: value.grupo.id,
      solicitudId: value.solicitud.id
    };
  }
}

export const SOLICITUD_GRUPO_REQUEST_CONVERTER = new SolicitudGrupoRequestConverter();
