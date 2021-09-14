import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IInvencionGastoRequest } from './invencion-gasto-request';
import { INVENCION_GASTO_REQUEST_CONVERTER } from './invencion-gasto-request.converter';
import { IInvencionGastoResponse } from './invencion-gasto-response';
import { INVENCION_GASTO_RESPONSE_CONVERTER } from './invencion-gasto-response.converter';

// tslint:disable-next-line: variable-name
const _InvencionGastoServiceMixinBase:
  CreateCtor<IInvencionGasto, IInvencionGasto, IInvencionGastoRequest, IInvencionGastoResponse> &
  UpdateCtor<number, IInvencionGasto, IInvencionGasto, IInvencionGastoRequest, IInvencionGastoResponse> &
  typeof SgiRestBaseService =
  mixinCreate(
    mixinUpdate(
      SgiRestBaseService,
      INVENCION_GASTO_REQUEST_CONVERTER,
      INVENCION_GASTO_RESPONSE_CONVERTER
    ),
    INVENCION_GASTO_REQUEST_CONVERTER,
    INVENCION_GASTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InvencionGastoService extends _InvencionGastoServiceMixinBase {

  private static readonly MAPPING = '/invenciongastos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${InvencionGastoService.MAPPING}`,
      http,
    );
  }
}
