import { IGrupo } from '@core/models/csp/grupo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudGrupo } from '@core/models/csp/solicitud-grupo';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudGrupoResponse } from './solicitud-grupo-response';

class SolicitudGrupoResponseConverter
  extends SgiBaseConverter<ISolicitudGrupoResponse, ISolicitudGrupo> {
  toTarget(value: ISolicitudGrupoResponse): ISolicitudGrupo {
    if (!value) {
      return value as unknown as ISolicitudGrupo;
    }
    return {
      id: value.id,
      grupo: { id: value.grupoId } as IGrupo,
      solicitud: { id: value.solicitudId } as ISolicitud
    };
  }

  fromTarget(value: ISolicitudGrupo): ISolicitudGrupoResponse {
    if (!value) {
      return value as unknown as ISolicitudGrupoResponse;
    }
    return {
      id: value.id,
      grupoId: value.grupo.id,
      solicitudId: value.solicitud.id
    };
  }
}

export const SOLICITUD_GRUPO_RESPONSE_CONVERTER = new SolicitudGrupoResponseConverter();
