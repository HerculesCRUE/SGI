import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IInvencionIngresoRequest } from './invencion-ingreso-request';
import { INVENCION_INGRESO_REQUEST_CONVERTER } from './invencion-ingreso-request.converter';
import { IInvencionIngresoResponse } from './invencion-ingreso-response';
import { INVENCION_INGRESO_RESPONSE_CONVERTER } from './invencion-ingreso-response.converter';

// tslint:disable-next-line: variable-name
const _InvencionIngresoServiceMixinBase:
  CreateCtor<IInvencionIngreso, IInvencionIngreso, IInvencionIngresoRequest, IInvencionIngresoResponse> &
  UpdateCtor<number, IInvencionIngreso, IInvencionIngreso, IInvencionIngresoRequest, IInvencionIngresoResponse> &
  typeof SgiRestBaseService =
  mixinCreate(
    mixinUpdate(
      SgiRestBaseService,
      INVENCION_INGRESO_REQUEST_CONVERTER,
      INVENCION_INGRESO_RESPONSE_CONVERTER
    ),
    INVENCION_INGRESO_REQUEST_CONVERTER,
    INVENCION_INGRESO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InvencionIngresoService extends _InvencionIngresoServiceMixinBase {

  private static readonly MAPPING = '/invencioningresos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${InvencionIngresoService.MAPPING}`,
      http,
    );
  }
}
