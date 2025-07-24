import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoFinalidadResponse } from './tipo-finalidad/tipo-finalidad-response';
import { TIPO_FINALIDAD_RESPONSE_CONVERTER } from './tipo-finalidad/tipo-finalidad-response.converter';

// tslint:disable-next-line: variable-name
const _TipoFinalidadServiceMixinBase:
  CreateCtor<ITipoFinalidad, ITipoFinalidad, ITipoFinalidadResponse, ITipoFinalidadResponse> &
  UpdateCtor<number, ITipoFinalidad, ITipoFinalidad, ITipoFinalidadResponse, ITipoFinalidadResponse> &
  FindByIdCtor<number, ITipoFinalidad, ITipoFinalidadResponse> &
  FindAllCtor<ITipoFinalidad, ITipoFinalidadResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_FINALIDAD_RESPONSE_CONVERTER,
          TIPO_FINALIDAD_RESPONSE_CONVERTER
        ),
        TIPO_FINALIDAD_RESPONSE_CONVERTER,
        TIPO_FINALIDAD_RESPONSE_CONVERTER
      ),
      TIPO_FINALIDAD_RESPONSE_CONVERTER
    ),
    TIPO_FINALIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoFinalidadService extends _TipoFinalidadServiceMixinBase {
  private static readonly MAPPING = '/tipofinalidades';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoFinalidadService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFinalidad>> {
    return this.find<ITipoFinalidadResponse, ITipoFinalidad>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_FINALIDAD_RESPONSE_CONVERTER
    );
  }

  /**
   * Desactivar tipo finalidad
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar tipo fase
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
