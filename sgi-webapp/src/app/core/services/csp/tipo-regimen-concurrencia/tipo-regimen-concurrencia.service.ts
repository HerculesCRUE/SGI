import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoRegimenConcurrencia } from '@core/models/csp/tipos-configuracion';
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
import { ITipoRegimenConcurrenciaRequest } from './tipo-regimen-concurrencia-request';
import { TIPO_REGIMEN_CONCURRENCIA_REQUEST_CONVERTER } from './tipo-regimen-concurrencia-request.converter';
import { ITipoRegimenConcurrenciaResponse } from './tipo-regimen-concurrencia-response';
import { TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER } from './tipo-regimen-concurrencia-response.converter';

// tslint:disable-next-line: variable-name
const _TipoRegimenConcurrenciaServiceMixinBase:
  CreateCtor<ITipoRegimenConcurrencia, ITipoRegimenConcurrencia, ITipoRegimenConcurrenciaRequest, ITipoRegimenConcurrenciaResponse> &
  UpdateCtor<number, ITipoRegimenConcurrencia, ITipoRegimenConcurrencia, ITipoRegimenConcurrenciaRequest, ITipoRegimenConcurrenciaResponse> &
  FindByIdCtor<number, ITipoRegimenConcurrencia, ITipoRegimenConcurrenciaResponse> &
  FindAllCtor<ITipoRegimenConcurrencia, ITipoRegimenConcurrenciaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_REGIMEN_CONCURRENCIA_REQUEST_CONVERTER,
          TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER
        ),
        TIPO_REGIMEN_CONCURRENCIA_REQUEST_CONVERTER,
        TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER
      ),
      TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER
    ),
    TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoRegimenConcurrenciaService extends _TipoRegimenConcurrenciaServiceMixinBase {
  private static readonly MAPPING = '/tiporegimenconcurrencias';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoRegimenConcurrenciaService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoRegimenConcurrencia>> {
    return this.find<ITipoRegimenConcurrenciaResponse, ITipoRegimenConcurrencia>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar tipo regimen concurrencia
   * @param id identificador.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar tipo regimen concurrencia
   * @param id identificador.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

}
