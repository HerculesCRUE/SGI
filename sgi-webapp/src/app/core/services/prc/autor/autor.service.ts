import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAutor } from '@core/models/prc/autor';
import { IAutorGrupo } from '@core/models/prc/autor-grupo';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IAutorGrupoResponse } from './autor-grupo/autor-grupo-response';
import { AUTOR_GRUPO_RESPONSE_CONVERTER } from './autor-grupo/autor-grupo-response.converter';
import { IAutorResponse } from './autor-response';
import { AUTOR_RESPONSE_CONVERTER } from './autor-response.converter';

// tslint:disable-next-line: variable-name
const _AutorServiceMixinBase:
  FindByIdCtor<number, IAutor, IAutorResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    AUTOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class AutorService extends _AutorServiceMixinBase {

  private static readonly MAPPING = '/autores';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${AutorService.MAPPING}`,
      http,
    );
  }

  /**
   * Obtiene todos los Grupos de un Autor dado el id
   * @param id id del Autor
   * @param options opciones de busqueda
   * @returns Grupos de un Autor
   */
  findGrupos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IAutorGrupo>> {
    return this.find<IAutorGrupoResponse, IAutorGrupo>(
      `${this.endpointUrl}/${id}/grupos`,
      options,
      AUTOR_GRUPO_RESPONSE_CONVERTER);
  }
}
