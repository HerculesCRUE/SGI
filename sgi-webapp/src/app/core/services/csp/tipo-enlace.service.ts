import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoEnlaceResponse } from './tipo-enlace/tipo-enlace-response';
import { TIPO_ENLACE_RESPONSE_CONVERTER } from './tipo-enlace/tipo-enlace-response.converter';

// tslint:disable-next-line: variable-name
const _TipoEnlaceServiceMixinBase:
  CreateCtor<ITipoEnlace, ITipoEnlace, ITipoEnlaceResponse, ITipoEnlaceResponse> &
  UpdateCtor<number, ITipoEnlace, ITipoEnlace, ITipoEnlaceResponse, ITipoEnlaceResponse> &
  FindAllCtor<ITipoEnlace, ITipoEnlaceResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TIPO_ENLACE_RESPONSE_CONVERTER,
        TIPO_ENLACE_RESPONSE_CONVERTER
      ),
      TIPO_ENLACE_RESPONSE_CONVERTER,
      TIPO_ENLACE_RESPONSE_CONVERTER
    ),
    TIPO_ENLACE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoEnlaceService extends _TipoEnlaceServiceMixinBase {
  private static readonly MAPPING = '/tipoenlaces';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoEnlaceService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoEnlace>> {
    return this.find<ITipoEnlaceResponse, ITipoEnlace>(`${this.endpointUrl}/todos`, options, TIPO_ENLACE_RESPONSE_CONVERTER);
  }

  /**
   * Desactivar tipo enlace
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar tipo enlace
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
