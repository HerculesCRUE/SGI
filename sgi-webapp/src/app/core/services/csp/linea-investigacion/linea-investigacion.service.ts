import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
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
import { ILineaInvestigacionRequest } from './linea-investigacion-request';
import { LINEA_INVESTIGACION_REQUEST_CONVERTER } from './linea-investigacion-request.converter';
import { ILineaInvestigacionResponse } from './linea-investigacion-response';
import { LINEA_INVESTIGACION_RESPONSE_CONVERTER } from './linea-investigacion-response.converter';

// tslint:disable-next-line: variable-name
const _LineaInvestigacionServiceMixinBase:
  CreateCtor<ILineaInvestigacion, ILineaInvestigacion, ILineaInvestigacionRequest, ILineaInvestigacionResponse> &
  UpdateCtor<number, ILineaInvestigacion, ILineaInvestigacion, ILineaInvestigacionRequest, ILineaInvestigacionResponse> &
  FindByIdCtor<number, ILineaInvestigacion, ILineaInvestigacionResponse> &
  FindAllCtor<ILineaInvestigacion, ILineaInvestigacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          LINEA_INVESTIGACION_REQUEST_CONVERTER,
          LINEA_INVESTIGACION_RESPONSE_CONVERTER
        ),
        LINEA_INVESTIGACION_REQUEST_CONVERTER,
        LINEA_INVESTIGACION_RESPONSE_CONVERTER
      ),
      LINEA_INVESTIGACION_RESPONSE_CONVERTER
    ),
    LINEA_INVESTIGACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class LineaInvestigacionService extends _LineaInvestigacionServiceMixinBase {
  private static readonly MAPPING = '/lineasinvestigacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${LineaInvestigacionService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ILineaInvestigacion>> {
    return this.find<ILineaInvestigacionResponse, ILineaInvestigacion>(
      `${this.endpointUrl}/todos`,
      options,
      LINEA_INVESTIGACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Desactivar la línea de investigación
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar la línea de investigación
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
