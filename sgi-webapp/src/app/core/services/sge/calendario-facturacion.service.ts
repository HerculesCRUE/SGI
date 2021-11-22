import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColumna } from '@core/models/sge/columna';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { environment } from '@env';
import {
  RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSort, SgiRestSortDirection
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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

  getColumnasFacturasEmitidas(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, true);
  }

  private getDatosEconomicos(
    sort: SgiRestSort,
    proyectoEconomicoId: string,
    fechaFacturaRange?: { desde: string, hasta: string }
  ): Observable<IDatoEconomico[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId);
    if (fechaFacturaRange?.desde && fechaFacturaRange?.hasta) {
      filter.and('fechaFactura', SgiRestFilterOperator.GREATHER_OR_EQUAL, fechaFacturaRange.desde)
        .and('fechaFactura', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaFacturaRange.hasta);
    }
    const options: SgiRestFindOptions = {
      filter,
      sort
    };
    return this.find<IDatoEconomico, IDatoEconomico>(
      `${this.endpointUrl}`,
      options
    ).pipe(
      map(response => response.items)
    );
  }

  getFacturasEmitidas(
    proyectoEconomicoId: string,
    fechaFacturaRange?: { desde: string, hasta: string },
  ): Observable<IDatoEconomico[]> {
    const sort = new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC);
    return this.getDatosEconomicos(
      sort,
      proyectoEconomicoId,
      fechaFacturaRange
    );
  }

  getFacturaEmitidaDetalle(id: string): Observable<IDatoEconomicoDetalle> {
    return this.http.get<IDatoEconomicoDetalle>(`${this.endpointUrl}/${id}`, { params: {} });
  }

}
