import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IFuenteFinanciacionRequest } from './fuente-financiacion-request';
import { FUENTE_FINANCIACION_REQUEST_CONVERTER } from './fuente-financiacion-request.converter';
import { IFuenteFinanciacionResponse } from './fuente-financiacion-response';
import { FUENTE_FINANCIACION_RESPONSE_CONVERTER } from './fuente-financiacion-response.converter';

// tslint:disable-next-line: variable-name
const _FuenteFinanciacionServiceMixinBase:
  CreateCtor<IFuenteFinanciacion, IFuenteFinanciacion, IFuenteFinanciacionRequest, IFuenteFinanciacionResponse> &
  UpdateCtor<number, IFuenteFinanciacion, IFuenteFinanciacion, IFuenteFinanciacionRequest, IFuenteFinanciacionResponse> &
  FindByIdCtor<number, IFuenteFinanciacion, IFuenteFinanciacionResponse> &
  FindAllCtor<IFuenteFinanciacion, IFuenteFinanciacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          FUENTE_FINANCIACION_REQUEST_CONVERTER,
          FUENTE_FINANCIACION_RESPONSE_CONVERTER
        ),
        FUENTE_FINANCIACION_REQUEST_CONVERTER,
        FUENTE_FINANCIACION_RESPONSE_CONVERTER
      ),
      FUENTE_FINANCIACION_RESPONSE_CONVERTER
    ),
    FUENTE_FINANCIACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class FuenteFinanciacionService extends _FuenteFinanciacionServiceMixinBase {
  private static readonly MAPPING = '/fuentesfinanciacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${FuenteFinanciacionService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IFuenteFinanciacion>> {
    return this.find<IFuenteFinanciacionResponse, IFuenteFinanciacion>(
      `${this.endpointUrl}/todos`,
      options,
      FUENTE_FINANCIACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar fuentes de financiación
   * @param options opciones de búsqueda.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar fuentes de financiación
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

}
