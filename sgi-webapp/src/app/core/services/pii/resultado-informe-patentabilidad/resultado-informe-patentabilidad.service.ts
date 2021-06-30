import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { IResultadoInformePatentibilidad } from '@core/models/pii/resultado-informe-patentabilidad';
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
import { IResultadoInformePatentibilidadRequest } from './resultado-informe-patentabilidad-request';
import { RESULTADO_INFORME_PATENTABILIDAD_REQUEST_CONVERTER } from './resultado-informe-patentabilidad-request.converter';
import { IResultadoInformePatentibilidadResponse } from './resultado-informe-patentabilidad-response';
import { RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER } from './resultado-informe-patentabilidad-response.converter';

// tslint:disable-next-line: variable-name
const _ResultadoInformePatentabilidadServiceMixinBase:
  CreateCtor<IResultadoInformePatentibilidad, IResultadoInformePatentibilidad, IResultadoInformePatentibilidadRequest, IResultadoInformePatentibilidadResponse> &
  UpdateCtor<number, IResultadoInformePatentibilidad, IResultadoInformePatentibilidad, IResultadoInformePatentibilidadRequest, IResultadoInformePatentibilidadResponse> &
  FindByIdCtor<number, IResultadoInformePatentibilidad, IResultadoInformePatentibilidadResponse> &
  FindAllCtor<IResultadoInformePatentibilidad, IResultadoInformePatentibilidadResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          RESULTADO_INFORME_PATENTABILIDAD_REQUEST_CONVERTER,
          RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER
        ),
        RESULTADO_INFORME_PATENTABILIDAD_REQUEST_CONVERTER,
        RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER
      ),
      RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER
    ),
    RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ResultadoInformePatentabilidadService extends _ResultadoInformePatentabilidadServiceMixinBase {
  private static readonly MAPPING = '/resultadosinformepatentabilidad';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${ResultadoInformePatentabilidadService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IResultadoInformePatentibilidad>> {
    return this.find<IResultadoInformePatentibilidadResponse, IResultadoInformePatentibilidad>(
      `${this.endpointUrl}/todos`,
      options,
      RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar un Resultado de Informe de Patentabilidad
   * @param options opciones de búsqueda.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar un Resultado de Informe de Patentabilidad
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

}
