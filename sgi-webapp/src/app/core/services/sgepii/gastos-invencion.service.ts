import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColumna } from '@core/models/sgepii/columna';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { IDatoEconomicoDetalle } from '@core/models/sgepii/dato-economico-detalle';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class GastosInvencionService extends SgiRestBaseService {
  private static readonly MAPPING = '/gastos-invencion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgepii}${GastosInvencionService.MAPPING}`,
      http
    );
  }

  /**
   * Obtiene los gastos asociados a la Invención.
   * 
   * @param invencionId Id de la Invención
   * @returns Lista de Gastos asociados a la Invención
   */
  getGastos(invencionId: string): Observable<IDatoEconomico[]> {
    const filter = new RSQLSgiRestFilter('invencionId', SgiRestFilterOperator.EQUALS, invencionId);
    const options: SgiRestFindOptions = {
      filter
    };
    return this.find<IDatoEconomico, IDatoEconomico>(
      `${this.endpointUrl}`,
      options
    ).pipe(
      map(response => response.items)
    );
  }

  /**
   * Obtiene más información sobre el Gasto.
   * 
   * @param gastoId Id del Gasto
   * @returns Detalle del Gasto
   */
  getGastoDetalle(gastoId: string): Observable<IDatoEconomicoDetalle> {
    return this.get<IDatoEconomicoDetalle>(
      `${this.endpointUrl}/${gastoId}`,
    );
  }

  /**
   * Obtiene la metainformación sobre las columnas dinámicas del Gasto.
   * 
   * @param invencionId Id de la Invención
   * @returns Lista de las columnas
   */
  getColumnas(invencionId: string): Observable<IColumna[]> {
    const filter = new RSQLSgiRestFilter('invencionId', SgiRestFilterOperator.EQUALS, invencionId);
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
