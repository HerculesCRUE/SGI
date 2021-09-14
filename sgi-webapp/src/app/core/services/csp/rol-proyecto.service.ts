import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRolProyecto, Orden } from '@core/models/csp/rol-proyecto';
import { environment } from '@env';
import { SgiReadOnlyMutableRestService, SgiRestListResult } from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RolProyectoService extends SgiReadOnlyMutableRestService<number, IRolProyecto, IRolProyecto> {
  private static readonly MAPPING = '/rolproyectos';

  constructor(protected http: HttpClient) {
    super(
      RolProyectoService.name,
      `${environment.serviceServers.csp}${RolProyectoService.MAPPING}`,
      http,
      null
    );
  }

  findPrincipal(): Observable<IRolProyecto> {
    return this.http.get<IRolProyecto>(`${this.endpointUrl}/principal`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      }),
      map(response => {
        return response;
      })
    );

  }

  /**
   * Recupera listado de RolProyectoColectivo a partir del RolProyecto.
   * @param id RolProyecto
   * @return listado RolProyectoColectivo
   */
  findAllColectivos(id: number): Observable<SgiRestListResult<string>> {
    const endpointUrl = `${this.endpointUrl}/${id}/colectivos`;
    return this.find<string, string>(endpointUrl, null);
  }
}
