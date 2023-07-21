import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRolSocio } from '@core/models/csp/rol-socio';
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
import { IRolSocioRequest } from './rol-socio-request';
import { ROL_SOCIO_REQUEST_CONVERTER } from './rol-socio-request.converter';
import { IRolSocioResponse } from './rol-socio-response';
import { ROL_SOCIO_RESPONSE_CONVERTER } from './rol-socio-response.converter';
// tslint:disable-next-line: variable-name
const _RolSocioServiceMixinBase:
  CreateCtor<IRolSocio, IRolSocio, IRolSocioRequest, IRolSocioResponse> &
  UpdateCtor<number, IRolSocio, IRolSocio, IRolSocioRequest, IRolSocioResponse> &
  FindByIdCtor<number, IRolSocio, IRolSocioResponse> &
  FindAllCtor<IRolSocio, IRolSocioResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          ROL_SOCIO_REQUEST_CONVERTER,
          ROL_SOCIO_RESPONSE_CONVERTER
        ),
        ROL_SOCIO_REQUEST_CONVERTER,
        ROL_SOCIO_RESPONSE_CONVERTER
      ),
      ROL_SOCIO_RESPONSE_CONVERTER
    ),
    ROL_SOCIO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RolSocioService extends _RolSocioServiceMixinBase {
  private static readonly MAPPING = '/rolsocios';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${RolSocioService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IRolSocio>> {
    return this.find<IRolSocioResponse, IRolSocio>(
      `${this.endpointUrl}/todos`,
      options,
      ROL_SOCIO_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar rol socio de proyecto
   * @param id identificador.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar rol socio de proyecto
   * @param id identificador.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

}
