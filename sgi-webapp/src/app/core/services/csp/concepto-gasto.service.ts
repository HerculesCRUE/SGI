import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor, mixinCreate, mixinFindAll, mixinUpdate } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IConceptoGastoResponse } from './concepto-gasto/concepto-gasto-response';
import { CONCEPTO_GASTO_RESPONSE_CONVERTER } from './concepto-gasto/concepto-gasto-response.converter';

const _ConceptoGastoServiceMixinBase:
  CreateCtor<IConceptoGasto, IConceptoGasto, IConceptoGastoResponse, IConceptoGastoResponse> &
  UpdateCtor<number, IConceptoGasto, IConceptoGasto, IConceptoGastoResponse, IConceptoGastoResponse> &
  FindAllCtor<IConceptoGasto, IConceptoGastoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        CONCEPTO_GASTO_RESPONSE_CONVERTER,
        CONCEPTO_GASTO_RESPONSE_CONVERTER
      ),
      CONCEPTO_GASTO_RESPONSE_CONVERTER,
      CONCEPTO_GASTO_RESPONSE_CONVERTER
    ),
    CONCEPTO_GASTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConceptoGastoService extends _ConceptoGastoServiceMixinBase {
  private static readonly MAPPING = '/conceptogastos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConceptoGastoService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConceptoGasto>> {
    return this.find<IConceptoGastoResponse, IConceptoGasto>(`${this.endpointUrl}/todos`, options, CONCEPTO_GASTO_RESPONSE_CONVERTER);
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
    const returnValue = this.find<IConceptoGastoResponse, IConceptoGasto>(
      `${this.endpointUrl}/${id}/no-proyectoagrupacion`,
      options, CONCEPTO_GASTO_RESPONSE_CONVERTER
    );
    return returnValue;
  }

}
