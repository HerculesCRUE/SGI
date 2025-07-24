import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor, mixinCreate, mixinFindAll, mixinUpdate } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoHitoResponse } from './tipo-hito/tipo-hito-response';
import { TIPO_HITO_RESPONSE_CONVERTER } from './tipo-hito/tipo-hito-response.converter';

const _TipoHitoServiceMixinBase:
  CreateCtor<ITipoHito, ITipoHito, ITipoHitoResponse, ITipoHitoResponse> &
  UpdateCtor<number, ITipoHito, ITipoHito, ITipoHitoResponse, ITipoHitoResponse> &
  FindAllCtor<ITipoHito, ITipoHitoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TIPO_HITO_RESPONSE_CONVERTER,
        TIPO_HITO_RESPONSE_CONVERTER
      ),
      TIPO_HITO_RESPONSE_CONVERTER,
      TIPO_HITO_RESPONSE_CONVERTER
    ),
    TIPO_HITO_RESPONSE_CONVERTER
  );


@Injectable({
  providedIn: 'root'
})
export class TipoHitoService extends _TipoHitoServiceMixinBase {
  private static readonly MAPPING = '/tipohitos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoHitoService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoHito>> {
    return this.find<ITipoHitoResponse, ITipoHito>(`${this.endpointUrl}/todos`, options, TIPO_HITO_RESPONSE_CONVERTER);
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

