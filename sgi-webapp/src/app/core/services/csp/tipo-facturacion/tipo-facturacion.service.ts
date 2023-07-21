import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoFacturacionRequest } from './tipo-facturacion-request';
import { TIPO_FACTURACION_REQUEST_CONVERTER } from './tipo-facturacion-request.converter';
import { ITipoFacturacionResponse } from './tipo-facturacion-response';
import { TIPO_FACTURACION_RESPONSE_CONVERTER } from './tipo-facturacion-response.converter';

// tslint:disable-next-line: variable-name
const _TipoFacturacionServiceMixinBase: FindAllCtor<ITipoFacturacion, ITipoFacturacionResponse> &
  CreateCtor<ITipoFacturacion, ITipoFacturacion, ITipoFacturacionRequest, ITipoFacturacionRequest> &
  UpdateCtor<number, ITipoFacturacion, ITipoFacturacion, ITipoFacturacionRequest, ITipoFacturacionRequest> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TIPO_FACTURACION_REQUEST_CONVERTER,
        TIPO_FACTURACION_RESPONSE_CONVERTER
      ),
      TIPO_FACTURACION_REQUEST_CONVERTER,
      TIPO_FACTURACION_RESPONSE_CONVERTER
    ),
    TIPO_FACTURACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoFacturacionService extends _TipoFacturacionServiceMixinBase {

  private static readonly MAPPING = '/tiposfacturacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoFacturacionService.MAPPING}`,
      http,
    );
  }

  /**
 * Muestra activos y no activos
 *
 * @param options opciones de búsqueda.
 */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFacturacion>> {
    return this.find<ITipoFacturacionResponse, ITipoFacturacion>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_FACTURACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar tipo facturacion
   * @param id identificador.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar tipo facturacion
   * @param id identificador.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

}
