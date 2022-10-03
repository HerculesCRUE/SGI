import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CODIGO_ECONOMICO_GASTO_CONVERTER } from '@core/converters/sge/codigo-economico-gasto.converter';
import { ICodigoEconomicoGastoBackend } from '@core/models/sge/backend/codigo-economico-gasto-backend';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _CodigoEconomicoGastoMixinBase:
  FindByIdCtor<string, ICodigoEconomicoGasto, ICodigoEconomicoGastoBackend> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CODIGO_ECONOMICO_GASTO_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class CodigoEconomicoGastoPublicService extends _CodigoEconomicoGastoMixinBase {
  private static readonly MAPPING = '/codigos-economicos-gastos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${CodigoEconomicoGastoPublicService.PUBLIC_PREFIX}${CodigoEconomicoGastoPublicService.MAPPING}`,
      http
    );
  }

}
