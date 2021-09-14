import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
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
import { ITipoProcedimientoRequest } from './tipo-procedimiento-request';
import { TIPO_PROCEDIMIENTO_REQUEST_CONVERTER } from './tipo-procedimiento-request.converter';
import { ITipoProcedimientoResponse } from './tipo-procedimiento-response';
import { TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER } from './tipo-procedimiento-response.converter';

// tslint:disable-next-line: variable-name
const _TipoProcedimientoServiceMixinBase:
  CreateCtor<ITipoProcedimiento, ITipoProcedimiento, ITipoProcedimientoRequest, ITipoProcedimientoResponse> &
  UpdateCtor<number, ITipoProcedimiento, ITipoProcedimiento, ITipoProcedimientoRequest, ITipoProcedimientoResponse> &
  FindByIdCtor<number, ITipoProcedimiento, ITipoProcedimientoResponse> &
  FindAllCtor<ITipoProcedimiento, ITipoProcedimientoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_PROCEDIMIENTO_REQUEST_CONVERTER,
          TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER
        ),
        TIPO_PROCEDIMIENTO_REQUEST_CONVERTER,
        TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER
      ),
      TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER
    ),
    TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER
  );
@Injectable({
  providedIn: 'root'
})
export class TipoProcedimientoService extends _TipoProcedimientoServiceMixinBase {

  private static readonly MAPPING = '/tiposprocedimiento';

  constructor(protected http: HttpClient) {
    super(`${environment.serviceServers.pii}${TipoProcedimientoService.MAPPING}`, http);
  }

  /**
   * Obtine objetos de TipoProcedimiento activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoProcedimiento>> {
    return this.find<ITipoProcedimientoResponse, ITipoProcedimiento>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar un Tipo de Procedimiento
   * @param id id del tipo a activar
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar Tipo de Procedimiento
   * @param id id del tipo a activar
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }
}
