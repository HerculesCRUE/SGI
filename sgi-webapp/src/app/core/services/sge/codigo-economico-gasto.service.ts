import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CODIGO_ECONOMICO_GASTO_CONVERTER } from '@core/converters/sge/codigo-economico-gasto.converter';
import { ICodigoEconomicoGastoBackend } from '@core/models/sge/backend/codigo-economico-gasto-backend';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class CodigoEconomicoGastoService extends SgiMutableRestService<string, ICodigoEconomicoGastoBackend, ICodigoEconomicoGasto>{
  private static readonly MAPPING = '/codigos-economicos-gastos';

  constructor(protected http: HttpClient) {
    super(
      CodigoEconomicoGastoService.name,
      `${environment.serviceServers.sge}${CodigoEconomicoGastoService.MAPPING}`,
      http,
      CODIGO_ECONOMICO_GASTO_CONVERTER
    );
  }

}
