import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitanteExterno } from '@core/models/csp/solicitante-externo';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate,
  SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { ISolicitanteExternoRequest } from './solicitante-externo-request';
import { SOLICITANTE_EXTERNO_REQUEST_CONVERTER } from './solicitante-externo-request.converter';
import { ISolicitanteExternoResponse } from './solicitante-externo-response';
import { SOLICITANTE_EXTERNO_RESPONSE_CONVERTER } from './solicitante-externo-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitanteExternoMixinBase:
  CreateCtor<ISolicitanteExterno, ISolicitanteExterno, ISolicitanteExternoRequest, ISolicitanteExternoResponse> &
  UpdateCtor<number, ISolicitanteExterno, ISolicitanteExterno, ISolicitanteExternoRequest, ISolicitanteExternoResponse> &
  FindByIdCtor<number, ISolicitanteExterno, ISolicitanteExternoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        SOLICITANTE_EXTERNO_REQUEST_CONVERTER,
        SOLICITANTE_EXTERNO_RESPONSE_CONVERTER
      ),
      SOLICITANTE_EXTERNO_REQUEST_CONVERTER,
      SOLICITANTE_EXTERNO_RESPONSE_CONVERTER
    ),
    SOLICITANTE_EXTERNO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitanteExternoService extends _SolicitanteExternoMixinBase {
  private static readonly MAPPING = '/solicitantes-externos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitanteExternoService.MAPPING}`,
      http,
    );
  }

}
