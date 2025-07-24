import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoFinanciacionResponse } from './tipo-financiacion/tipo-financiacion-response';
import { TIPO_FINANCIACION_RESPONSE_CONVERTER } from './tipo-financiacion/tipo-financiacion-response.converter';

// tslint:disable-next-line: variable-name
const _TipoFinanciacionServiceMixinBase:
  CreateCtor<ITipoFinanciacion, ITipoFinanciacion, ITipoFinanciacionResponse, ITipoFinanciacionResponse> &
  UpdateCtor<number, ITipoFinanciacion, ITipoFinanciacion, ITipoFinanciacionResponse, ITipoFinanciacionResponse> &
  FindAllCtor<ITipoFinanciacion, ITipoFinanciacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TIPO_FINANCIACION_RESPONSE_CONVERTER,
        TIPO_FINANCIACION_RESPONSE_CONVERTER
      ),
      TIPO_FINANCIACION_RESPONSE_CONVERTER,
      TIPO_FINANCIACION_RESPONSE_CONVERTER
    ),
    TIPO_FINANCIACION_RESPONSE_CONVERTER
  );


@Injectable({
  providedIn: 'root'
})
export class TipoFinanciacionService extends _TipoFinanciacionServiceMixinBase {
  private static readonly MAPPING = '/tipofinanciaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoFinanciacionService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFinanciacion>> {
    return this.find<ITipoFinanciacionResponse, ITipoFinanciacion>(`${this.endpointUrl}/todos`, options, TIPO_FINANCIACION_RESPONSE_CONVERTER);
  }


  /**
   * Desactiva un tipo de financiacion
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

  /**
   * Reactivar fuentes de financiación
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, { id });
  }


}

