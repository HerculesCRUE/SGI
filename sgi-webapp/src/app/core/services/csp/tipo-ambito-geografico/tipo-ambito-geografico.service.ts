import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
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
import { ITipoAmbitoGeograficoResponse } from './tipo-ambito-geografico-response';
import { ITipoAmbitoGeograficoRequest } from './tipo-ambito-geografico-request';
import { TIPO_AMBITO_GEOGRAFICO_REQUEST_CONVERTER } from './tipo-ambito-geografico-request.converter';
import { TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER } from './tipo-ambito-geografico-response.converter';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipos-configuracion';

// tslint:disable-next-line: variable-name
const _TipoAmbitoGeograficoServiceMixinBase:
  CreateCtor<ITipoAmbitoGeografico, ITipoAmbitoGeografico, ITipoAmbitoGeograficoRequest, ITipoAmbitoGeograficoResponse> &
  UpdateCtor<number, ITipoAmbitoGeografico, ITipoAmbitoGeografico, ITipoAmbitoGeograficoRequest, ITipoAmbitoGeograficoResponse> &
  FindByIdCtor<number, ITipoAmbitoGeografico, ITipoAmbitoGeograficoResponse> &
  FindAllCtor<ITipoAmbitoGeografico, ITipoAmbitoGeograficoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_AMBITO_GEOGRAFICO_REQUEST_CONVERTER,
          TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER
        ),
        TIPO_AMBITO_GEOGRAFICO_REQUEST_CONVERTER,
        TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER
      ),
      TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER
    ),
    TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoAmbitoGeograficoService extends _TipoAmbitoGeograficoServiceMixinBase {
  private static readonly MAPPING = '/tipoambitogeograficos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoAmbitoGeograficoService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoAmbitoGeografico>> {
    return this.find<ITipoAmbitoGeograficoResponse, ITipoAmbitoGeografico>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar tipo ambito geografico
   * @param id identificador.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar tipo ambito geografico
   * @param id identificador.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

}
