import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
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
import { ITipoOrigenFuenteFinanciacionRequest } from './tipo-origen-fuente-financiacion-request';
import { TIPO_ORIGEN_FUENTE_FINANCIACION_REQUEST_CONVERTER } from './tipo-origen-fuente-financiacion-request.converter';
import { ITipoOrigenFuenteFinanciacionResponse } from './tipo-origen-fuente-financiacion-response';
import { TIPO_ORIGEN_FUENTE_FINANCIACION_RESPONSE_CONVERTER } from './tipo-origen-fuente-financiacion-response.converter';

// tslint:disable-next-line: variable-name
const _TipoOrigenFuenteFinanciacionServiceMixinBase:
  CreateCtor<ITipoOrigenFuenteFinanciacion, ITipoOrigenFuenteFinanciacion, ITipoOrigenFuenteFinanciacionRequest, ITipoOrigenFuenteFinanciacionResponse> &
  UpdateCtor<number, ITipoOrigenFuenteFinanciacion, ITipoOrigenFuenteFinanciacion, ITipoOrigenFuenteFinanciacionRequest, ITipoOrigenFuenteFinanciacionResponse> &
  FindByIdCtor<number, ITipoOrigenFuenteFinanciacion, ITipoOrigenFuenteFinanciacionResponse> &
  FindAllCtor<ITipoOrigenFuenteFinanciacion, ITipoOrigenFuenteFinanciacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_ORIGEN_FUENTE_FINANCIACION_REQUEST_CONVERTER,
          TIPO_ORIGEN_FUENTE_FINANCIACION_RESPONSE_CONVERTER
        ),
        TIPO_ORIGEN_FUENTE_FINANCIACION_REQUEST_CONVERTER,
        TIPO_ORIGEN_FUENTE_FINANCIACION_RESPONSE_CONVERTER
      ),
      TIPO_ORIGEN_FUENTE_FINANCIACION_RESPONSE_CONVERTER
    ),
    TIPO_ORIGEN_FUENTE_FINANCIACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoOrigenFuenteFinanciacionService extends _TipoOrigenFuenteFinanciacionServiceMixinBase {
  private static readonly MAPPING = '/tipoorigenfuentefinanciaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoOrigenFuenteFinanciacionService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoOrigenFuenteFinanciacion>> {
    return this.find<ITipoOrigenFuenteFinanciacionResponse, ITipoOrigenFuenteFinanciacion>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_ORIGEN_FUENTE_FINANCIACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar tipo origen fuente financiación
   * @param id identificador.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar tipo origen fuente financiación
   * @param id identificador.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

}
