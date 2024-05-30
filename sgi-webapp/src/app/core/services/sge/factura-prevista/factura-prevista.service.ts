import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFacturaPrevista } from '@core/models/sge/factura-prevista';
import { environment } from '@env';
import { CreateCtor, SgiRestBaseService, UpdateCtor, mixinCreate, mixinUpdate } from '@sgi/framework/http';
import { IFacturaPrevistaRequest } from './factura-prevista-request';
import { FACTURA_PREVISTA_REQUEST_CONVERTER } from './factura-prevista-request.converter';
import { IFacturaPrevistaResponse } from './factura-prevista-response';
import { FACTURA_PREVISTA_RESPONSE_CONVERTER } from './factura-prevista-response.converter';

// tslint:disable-next-line: variable-name
const _FacturaPrevistaServiceMixinBase:
  CreateCtor<IFacturaPrevista, IFacturaPrevista, IFacturaPrevistaRequest, IFacturaPrevistaResponse> &
  UpdateCtor<string, IFacturaPrevista, IFacturaPrevista, IFacturaPrevistaRequest, IFacturaPrevistaResponse> &
  typeof SgiRestBaseService = mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      FACTURA_PREVISTA_REQUEST_CONVERTER,
      FACTURA_PREVISTA_RESPONSE_CONVERTER
    ),
    FACTURA_PREVISTA_REQUEST_CONVERTER,
    FACTURA_PREVISTA_RESPONSE_CONVERTER
  );

@Injectable({ providedIn: 'root' })
export class FacturaPrevistaService extends _FacturaPrevistaServiceMixinBase {

  private static readonly MAPPING = '/facturas-previstas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${FacturaPrevistaService.MAPPING}`,
      http
    );
  }

}
