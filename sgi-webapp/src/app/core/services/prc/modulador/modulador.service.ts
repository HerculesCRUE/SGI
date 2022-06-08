import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModulador } from '@core/models/prc/modulador';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate,
  mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { IModuladorRequest } from './modulador-request';
import { MODULADOR_REQUEST_CONVERTER } from './modulador-request.converter';
import { IModuladorResponse } from './modulador-response';
import { MODULADOR_RESPONSE_CONVERTER } from './modulador-response.converter';

// tslint:disable-next-line: variable-name
const _ModuladorMixinBase:
  FindByIdCtor<number, IModulador, IModuladorResponse> &
  CreateCtor<IModulador, IModulador, IModuladorRequest, IModuladorResponse> &
  UpdateCtor<number, IModulador, IModulador, IModuladorRequest, IModuladorResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinCreate(
      mixinUpdate(
        SgiRestBaseService,
        MODULADOR_REQUEST_CONVERTER,
        MODULADOR_RESPONSE_CONVERTER
      ),
      MODULADOR_REQUEST_CONVERTER,
      MODULADOR_RESPONSE_CONVERTER
    ),
    MODULADOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ModuladorService extends _ModuladorMixinBase {

  private static readonly MAPPING = '/moduladores';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ModuladorService.MAPPING}`,
      http,
    );
  }

}
