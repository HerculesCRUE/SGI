import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UnidadGestionService extends SgiMutableRestService<number, IUnidadGestion, IUnidadGestion> {

  private static readonly MAPPING = '/unidades';
  constructor(private readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      UnidadGestionService.name,
      `${environment.serviceServers.usr}${UnidadGestionService.MAPPING}`,
      http, null
    );
  }

  /**
   * Recupera las unidades de gestión restringidas por los permisos del usuario logueado.
   * @param options opciones de búsqueda.
   */
  findAllRestringidos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IUnidadGestion>> {
    return this.find<IUnidadGestion, IUnidadGestion>(`${this.endpointUrl}/restringidos`, options);
  }

  /**
   * Recupera una unidad de gestión por el id recibido.
   * @param id id de la unidad de gestión.
   * @returns unidad de gestión.
   */
  findById(id: number): Observable<IUnidadGestion> {
    return this.http.get<IUnidadGestion>(`${this.endpointUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => {
        this.logger.error(error);
        return throwError(error);
      }),
      map(response => {
        return response;
      })
    );

  }
}
