import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudGrupo } from '@core/models/csp/solicitud-grupo';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { ISolicitudGrupoRequest } from './solicitud-grupo-request';
import { SOLICITUD_GRUPO_REQUEST_CONVERTER } from './solicitud-grupo-request-converter';
import { ISolicitudGrupoResponse } from './solicitud-grupo-response';
import { SOLICITUD_GRUPO_RESPONSE_CONVERTER } from './solicitud-grupo-response-converter';

// tslint:disable-next-line: variable-name
const _SolicitudGrupoMixinBase:
  CreateCtor<ISolicitudGrupo, ISolicitudGrupo, ISolicitudGrupoRequest, ISolicitudGrupoResponse> &
  UpdateCtor<number, ISolicitudGrupo, ISolicitudGrupo, ISolicitudGrupoRequest, ISolicitudGrupoResponse> &
  FindByIdCtor<number, ISolicitudGrupo, ISolicitudGrupoResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        SOLICITUD_GRUPO_REQUEST_CONVERTER,
        SOLICITUD_GRUPO_RESPONSE_CONVERTER
      ),
      SOLICITUD_GRUPO_REQUEST_CONVERTER,
      SOLICITUD_GRUPO_RESPONSE_CONVERTER
    ),
    SOLICITUD_GRUPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudGrupoService extends _SolicitudGrupoMixinBase {
  private static readonly MAPPING = '/solicitudgrupos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudGrupoService.MAPPING}`,
      http,
    );
  }
}
