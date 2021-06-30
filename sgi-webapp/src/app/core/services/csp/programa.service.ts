import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPrograma } from '@core/models/csp/programa';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http/';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProgramaService extends SgiRestService<number, IPrograma> {

  private static readonly MAPPING = '/programas';

  constructor(protected http: HttpClient) {
    super(
      ProgramaService.name,
      `${environment.serviceServers.csp}${ProgramaService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IPrograma>> {
    return this.find<IPrograma, IPrograma>(`${this.endpointUrl}/plan/todos`, options);
  }

  findAllPlan(options?: SgiRestFindOptions): Observable<SgiRestListResult<IPrograma>> {
    return this.find<IPrograma, IPrograma>(`${this.endpointUrl}/plan`, options);
  }

  findAllHijosPrograma(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IPrograma>> {
    return this.find<IPrograma, IPrograma>(`${this.endpointUrl}/${id}/hijos`, options);
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  activate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }
}
