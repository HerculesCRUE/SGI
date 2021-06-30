import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TipoHitoService extends SgiRestService<number, ITipoHito> {
  private static readonly MAPPING = '/tipohitos';

  constructor(protected http: HttpClient) {
    super(
      TipoHitoService.name,
      `${environment.serviceServers.csp}${TipoHitoService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoHito>> {
    return this.find<ITipoHito, ITipoHito>(`${this.endpointUrl}/todos`, options);
  }

  /**
   * Desactivar tipo hito
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar tipo hito
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }


}

