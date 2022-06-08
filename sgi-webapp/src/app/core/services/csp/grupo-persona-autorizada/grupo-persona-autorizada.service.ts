import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { GRUPO_PERSONA_AUTORIZADA_REQUEST_CONVERTER } from './grupo-persona-autorizada-request.converter';
import { IGrupoPersonaAutorizadaResponse } from './grupo-persona-autorizada-response';
import { GRUPO_PERSONA_AUTORIZADA_RESPONSE_CONVERTER } from './grupo-persona-autorizada-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoPersonaAutorizadaMixinBase:
  FindByIdCtor<number, IGrupoPersonaAutorizada, IGrupoPersonaAutorizadaResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    GRUPO_PERSONA_AUTORIZADA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoPersonaAutorizadaService extends _GrupoPersonaAutorizadaMixinBase {
  private static readonly MAPPING = '/grupospersonasautorizadas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoPersonaAutorizadaService.MAPPING}`,
      http,
    );
  }

  /**
   * Actualiza el listado de IDGrupoPersonaAutorizada asociados a un IGrupoPersonaAutorizada
   *
   * @param id Id del IGrupoPersonaAutorizada
   * @param entities Listado de IGrupoPersonaAutorizada
   */
  updateList(id: number, entities: IGrupoPersonaAutorizada[]): Observable<IGrupoPersonaAutorizada[]> {
    return this.http.patch<IGrupoPersonaAutorizadaResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_PERSONA_AUTORIZADA_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_PERSONA_AUTORIZADA_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
