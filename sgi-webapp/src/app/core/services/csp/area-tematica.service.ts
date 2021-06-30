import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AreaTematicaService extends SgiRestService<number, IAreaTematica> {
  private static readonly MAPPING = '/areatematicas';

  constructor(protected http: HttpClient) {
    super(
      AreaTematicaService.name,
      `${environment.serviceServers.csp}${AreaTematicaService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra las areas temáticas que tienen padre a NULL
   * @param options opciones de búsqueda.
   */
  findAllGrupo(options?: SgiRestFindOptions): Observable<SgiRestListResult<IAreaTematica>> {
    return this.find<IAreaTematica, IAreaTematica>(`${this.endpointUrl}/grupo`, options);
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IAreaTematica>> {
    return this.find<IAreaTematica, IAreaTematica>(`${this.endpointUrl}/grupo/todos`, options);
  }

  /**
   * Encuentra todos los hijos del padre (mat-tree)
   * @param id numero padre
   * @param options opciones de busqueda
   */
  findAllHijosArea(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IAreaTematica>> {
    return this.find<IAreaTematica, IAreaTematica>(`${this.endpointUrl}/${id}/hijos`, options);
  }

  /**
   * Desactivar area tematica
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar area tematica
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }
}
