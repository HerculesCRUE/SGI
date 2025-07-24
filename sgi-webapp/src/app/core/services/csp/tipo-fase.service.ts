import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoFaseResponse } from './tipo-fase/tipo-fase-response';
import { TIPO_FASE_RESPONSE_CONVERTER } from './tipo-fase/tipo-fase-response.converter';


// tslint:disable-next-line: variable-name
const _TipoFaseServiceMixinBase:
  CreateCtor<ITipoFase, ITipoFase, ITipoFaseResponse, ITipoFaseResponse> &
  UpdateCtor<number, ITipoFase, ITipoFase, ITipoFaseResponse, ITipoFaseResponse> &
  FindAllCtor<ITipoFase, ITipoFaseResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TIPO_FASE_RESPONSE_CONVERTER,
        TIPO_FASE_RESPONSE_CONVERTER
      ),
      TIPO_FASE_RESPONSE_CONVERTER,
      TIPO_FASE_RESPONSE_CONVERTER
    ),
    TIPO_FASE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoFaseService extends _TipoFaseServiceMixinBase {
  private static readonly MAPPING = '/tipofases';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoFaseService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFase>> {
    return this.find<ITipoFaseResponse, ITipoFase>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_FASE_RESPONSE_CONVERTER
    );
  }

  /**
   * Desactivar tipo fase
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
