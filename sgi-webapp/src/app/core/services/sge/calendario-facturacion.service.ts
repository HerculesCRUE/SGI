import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColumna } from '@core/models/sge/columna';
import { IFacturaEmitida } from '@core/models/sge/factura-emitida';
import { IFacturaEmitidaDetalle } from '@core/models/sge/factura-emitida-detalle';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { environment } from '@env';
import {
  RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection
} from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IFacturaEmitidaResponse } from './factura-emitida/factura-emitida-response';
import { FACTURA_EMITIDA_RESPONSE_CONVERTER } from './factura-emitida/factura-emitida-response.converter';

@Injectable({
  providedIn: 'root'
})
export class CalendarioFacturacionService extends SgiRestBaseService {
  private static readonly MAPPING = '/facturas-emitidas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${CalendarioFacturacionService.MAPPING}`,
      http
    );
  }

  getColumnasFacturasEmitidas(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, true);
  }

  getFacturasEmitidas(
    proyectoEconomicoId: string,
    fechaFacturaRange?: { desde: DateTime, hasta: DateTime },
  ): Observable<IFacturaEmitida[]> {
    const sort = new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC);
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId);

    if (fechaFacturaRange?.desde) {
      filter.and('fechaFactura', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(fechaFacturaRange.desde, true));
    }

    if (fechaFacturaRange?.hasta) {
      filter.and('fechaFactura', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(fechaFacturaRange.hasta, true));
    }

    const options: SgiRestFindOptions = {
      filter,
      sort
    };
    return this.find<IFacturaEmitidaResponse, IFacturaEmitida>(
      `${this.endpointUrl}`,
      options,
      FACTURA_EMITIDA_RESPONSE_CONVERTER
    ).pipe(
      map(response => response.items)
    );
  }

  getFacturaEmitidaDetalle(id: string): Observable<IFacturaEmitidaDetalle> {
    return this.http.get<IFacturaEmitidaDetalle>(`${this.endpointUrl}/${id}`, { params: {} });
  }

  private getColumnas(proyectoEconomicoId: string, reducido = false): Observable<IColumna[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId)
      .and('reducida', SgiRestFilterOperator.EQUALS, `${reducido}`);
    const options: SgiRestFindOptions = {
      filter
    };
    return this.find<IColumna, IColumna>(
      `${this.endpointUrl}/columnas`,
      options
    ).pipe(
      map(response => response.items)
    );
  }

}
