import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor,
  mixinCreate, mixinFindAll, mixinFindById,
  mixinUpdate, SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { IRepartoGastoRequest } from './reparto-gasto-request';
import { REPARTO_GASTO_REQUEST_CONVERTER } from './reparto-gasto-request.converter';
import { IRepartoGastoResponse } from './reparto-gasto-response';
import { REPARTO_GASTO_RESPONSE_CONVERTER } from './reparto-gasto-response.converter';

// tslint:disable-next-line: variable-name
const _RepartoGastoServiceMixinBase:
  FindAllCtor<IRepartoGasto, IRepartoGastoResponse> &
  FindByIdCtor<number, IRepartoGasto, IRepartoGastoResponse> &
  CreateCtor<IRepartoGasto, IRepartoGasto, IRepartoGastoRequest, IRepartoGastoResponse> &
  UpdateCtor<number, IRepartoGasto, IRepartoGasto, IRepartoGastoRequest, IRepartoGastoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          REPARTO_GASTO_REQUEST_CONVERTER,
          REPARTO_GASTO_RESPONSE_CONVERTER
        ),
        REPARTO_GASTO_REQUEST_CONVERTER,
        REPARTO_GASTO_RESPONSE_CONVERTER
      ),
      REPARTO_GASTO_RESPONSE_CONVERTER
    ),
    REPARTO_GASTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RepartoGastoService extends _RepartoGastoServiceMixinBase {

  private static readonly MAPPING = '/repartogastos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${RepartoGastoService.MAPPING}`,
      http,
    );
  }
}
