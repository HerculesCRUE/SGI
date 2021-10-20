import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColumna } from '@core/models/sgepii/columna';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class IngresosInvencionService extends SgiRestBaseService {
  private static readonly MAPPING = '/ingresos-invencion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgepii}${IngresosInvencionService.MAPPING}`,
      http
    );
  }

  /**
   * Obtiene los Ingresos asociados a la Invención.
   * 
   * @param proyectoId Id del proyecto asociado a la Invención.
   * @returns Lista de Ingresos asociados a la Invención.
   */
  getIngresos(proyectoId: string): Observable<IDatoEconomico[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoId);
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
   * Obtiene la metainformación sobre las columnas dinámicas del Ingreso.
   * 
   * @param proyectoId Id del proyecto asociado a la Invención.
   * @returns Lista de las columnas.
   */
  getColumnas(proyectoId: string): Observable<IColumna[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoId);
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
