import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TipoFaseService extends SgiRestService<number, ITipoFase> {
  private static readonly MAPPING = '/tipofases';

  constructor(protected http: HttpClient) {
    super(
      TipoFaseService.name,
      `${environment.serviceServers.csp}${TipoFaseService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFase>> {
    return this.find<ITipoFase, ITipoFase>(`${this.endpointUrl}/todos`, options);
  }

  /**
   * Desactivar tipo fase
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
