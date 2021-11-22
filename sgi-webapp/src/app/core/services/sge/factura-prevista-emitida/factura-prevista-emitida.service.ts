import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFacturaPrevistaEmitida } from '@core/models/sge/factura-prevista-emitida';
import { PROYECTO_FACTURACION_RESPONSE_CONVERTER } from '@core/services/csp/proyecto-facturacion/proyecto-facturacion-response.converter';
import { environment } from '@env';
import { FindByIdCtor, SgiRestBaseService, mixinFindById, RSQLSgiRestFilter, SgiRestFindOptions, FindAllCtor, mixinFindAll } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IFacturaPrevistaEmitidaResponse } from './factura-prevista-emitida-response';
import { FACTURA_PREVISTA_EMITIDA_RESPONSE_CONVERTER } from './factura-prevista-emitida-response.converter';

// tslint:disable-next-line: variable-name
const _FacturaPrevistaEmitidaServiceMixinBase:
  FindByIdCtor<string, IFacturaPrevistaEmitida, IFacturaPrevistaEmitidaResponse> &
  FindAllCtor<IFacturaPrevistaEmitida, IFacturaPrevistaEmitidaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService), FACTURA_PREVISTA_EMITIDA_RESPONSE_CONVERTER);

@Injectable({ providedIn: 'root' })
export class FacturaPrevistaEmitidaService extends _FacturaPrevistaEmitidaServiceMixinBase {

  private static readonly MAPPING = '/facturas-previstas-emitidas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${FacturaPrevistaEmitidaService.MAPPING}`,
      http
    );
  }

}
