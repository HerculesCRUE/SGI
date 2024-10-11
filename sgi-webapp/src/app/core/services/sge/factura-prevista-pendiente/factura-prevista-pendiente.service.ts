import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFacturaPrevistaPendiente } from '@core/models/sge/factura-prevista-pendiente';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { IFacturaPrevistaPendienteResponse } from './factura-prevista-pendiente-response';
import { FACTURA_PREVISTA_PENDIENTE_RESPONSE_CONVERTER } from './factura-prevista-pendiente-response.converter';

// tslint:disable-next-line: variable-name
const _FacturaPrevistaPendienteServiceMixinBase:
  FindAllCtor<IFacturaPrevistaPendiente, IFacturaPrevistaPendienteResponse> &
  typeof SgiRestBaseService = mixinFindAll(SgiRestBaseService, FACTURA_PREVISTA_PENDIENTE_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class FacturaPrevistaPendienteService extends _FacturaPrevistaPendienteServiceMixinBase {

  private static readonly MAPPING = '/facturas-previstas-pendientes';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${FacturaPrevistaPendienteService.MAPPING}`,
      http
    );
  }

}
