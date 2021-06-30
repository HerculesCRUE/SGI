import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TipoDocumentoService extends SgiRestService<number, ITipoDocumento> {
  private static readonly MAPPING = '/tipodocumentos';

  constructor(protected http: HttpClient) {
    super(
      TipoDocumentoService.name,
      `${environment.serviceServers.csp}${TipoDocumentoService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoDocumento>> {
    return this.find<ITipoDocumento, ITipoDocumento>(`${this.endpointUrl}/todos`, options);
  }

  /**
   * Desactivar tipo documento
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar tipo documento
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
