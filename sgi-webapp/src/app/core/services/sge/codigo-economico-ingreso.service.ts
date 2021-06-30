import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CODIGO_ECONOMICO_INGRESO_CONVERTER } from '@core/converters/sge/codigo-economico-ingreso.converter';
import { ICodigoEconomicoIngresoBackend } from '@core/models/sge/backend/codigo-economico-ingreso-backend';
import { ICodigoEconomicoIngreso } from '@core/models/sge/codigo-economico-ingreso';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class CodigoEconomicoIngresoService extends SgiMutableRestService<string, ICodigoEconomicoIngresoBackend, ICodigoEconomicoIngreso>{
  private static readonly MAPPING = '/codigos-economicos-gastos';

  constructor(protected http: HttpClient) {
    super(
      CodigoEconomicoIngresoService.name,
      `${environment.serviceServers.sge}${CodigoEconomicoIngresoService.MAPPING}`,
      http,
      CODIGO_ECONOMICO_INGRESO_CONVERTER
    );
  }

}
