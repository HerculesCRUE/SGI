import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
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
import { ITipoProteccionRequest } from './tipo-proteccion-request';
import { TIPO_PROTECCION_REQUEST_CONVERTER } from './tipo-proteccion-request.converter';
import { ITipoProteccionResponse } from './tipo-proteccion-response';
import { TIPO_PROTECCION_RESPONSE_CONVERTER } from './tipo-proteccion-response.converter';

// tslint:disable-next-line: variable-name
const _TipoProteccionServiceMixinBase:
  CreateCtor<ITipoProteccion, ITipoProteccion, ITipoProteccionRequest, ITipoProteccionResponse> &
  UpdateCtor<number, ITipoProteccion, ITipoProteccion, ITipoProteccionRequest, ITipoProteccionResponse> &
  FindByIdCtor<number, ITipoProteccion, ITipoProteccionResponse> &
  FindAllCtor<ITipoProteccion, ITipoProteccionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_PROTECCION_REQUEST_CONVERTER,
          TIPO_PROTECCION_RESPONSE_CONVERTER
        ),
        TIPO_PROTECCION_REQUEST_CONVERTER,
        TIPO_PROTECCION_RESPONSE_CONVERTER
      ),
      TIPO_PROTECCION_RESPONSE_CONVERTER
    ),
    TIPO_PROTECCION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoProteccionService extends _TipoProteccionServiceMixinBase {
  private static readonly MAPPING = '/tiposproteccion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${TipoProteccionService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoProteccion>> {
    return this.find<ITipoProteccionResponse, ITipoProteccion>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_PROTECCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar un Tipo de Resultado
   * @param options opciones de búsqueda.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar Tipo de Protección
   * @param options Opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

}
