import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TipoFinalidadService extends SgiRestService<number, ITipoFinalidad> {
  private static readonly MAPPING = '/tipofinalidades';

  constructor(protected http: HttpClient) {
    super(
      TipoFinalidadService.name,
      `${environment.serviceServers.csp}${TipoFinalidadService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFinalidad>> {
    return this.find<ITipoFinalidad, ITipoFinalidad>(`${this.endpointUrl}/todos`, options);
  }

  /**
   * Desactivar tipo finalidad
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar tipo fase
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
