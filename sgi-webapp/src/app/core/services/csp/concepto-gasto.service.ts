import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConceptoGastoService extends SgiRestService<number, IConceptoGasto> {
  private static readonly MAPPING = '/conceptogastos';

  constructor(protected http: HttpClient) {
    super(
      ConceptoGastoService.name,
      `${environment.serviceServers.csp}${ConceptoGastoService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConceptoGasto>> {
    return this.find<IConceptoGasto, IConceptoGasto>(`${this.endpointUrl}/todos`, options);
  }

  /**
   * Reactivar conceptos de gasto
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, { id });
  }

  /**
   * Desactivar conceptos de gasto
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

  /**
   * Devuelve el listado de IAgrupacionGastoConcepto de un IProyectoAgrupacionGasto
   *
   * @param id Id del IProyectoAgrupacionGasto
   */
  findAllAgrupacionesGastoConceptoNotInAgrupacion(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IConceptoGasto>> {
    const returnValue = this.find<IConceptoGasto, IConceptoGasto>(
      `${this.endpointUrl}/${id}/no-proyectoagrupacion`,
      options
    );
    return returnValue;
  }

}
