import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TipoEnlaceService extends SgiRestService<number, ITipoEnlace> {
  private static readonly MAPPING = '/tipoenlaces';

  constructor(protected http: HttpClient) {
    super(
      TipoEnlaceService.name,
      `${environment.serviceServers.csp}${TipoEnlaceService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoEnlace>> {
    return this.find<ITipoEnlace, ITipoEnlace>(`${this.endpointUrl}/todos`, options);
  }

  /**
   * Desactivar tipo enlace
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar tipo enlace
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
