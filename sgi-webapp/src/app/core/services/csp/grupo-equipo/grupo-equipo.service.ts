import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate,
  mixinFindAll, mixinFindById, SgiRestBaseService
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGrupoEquipoUpdateRequest } from './grupo-equipo-update-request';
import { IGrupoEquipoResponse } from './grupo-equipo-response';
import { GRUPO_EQUIPO_RESPONSE_CONVERTER } from './grupo-equipo-response.converter';
import { GRUPO_EQUIPO_UPDATE_REQUEST_CONVERTER } from './grupo-equipo-update-request.converter';
import { GRUPO_EQUIPO_CREATE_REQUEST_CONVERTER } from './grupo-equipo-create-request.converter';

// tslint:disable-next-line: variable-name
const _GrupoEquipoMixinBase:
  CreateCtor<IGrupoEquipo, IGrupoEquipo, IGrupoEquipoUpdateRequest, IGrupoEquipoResponse> &
  FindByIdCtor<number, IGrupoEquipo, IGrupoEquipoResponse> &
  FindAllCtor<IGrupoEquipo, IGrupoEquipoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        SgiRestBaseService,
        GRUPO_EQUIPO_CREATE_REQUEST_CONVERTER,
        GRUPO_EQUIPO_RESPONSE_CONVERTER
      ),
      GRUPO_EQUIPO_RESPONSE_CONVERTER
    ),
    GRUPO_EQUIPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoEquipoService extends _GrupoEquipoMixinBase {
  private static readonly MAPPING = '/gruposequipos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoEquipoService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Actualiza el listado de IDGrupoEquipo asociados a un IGrupoEquipo
   *
   * @param id Id del IGrupoEquipo
   * @param entities Listado de IGrupoEquipo
   */
  updateList(id: number, entities: IGrupoEquipo[]): Observable<IGrupoEquipo[]> {
    return this.http.patch<IGrupoEquipoResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_EQUIPO_UPDATE_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_EQUIPO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

  /**
   * Devuelve el personaRef del usuario actual y de todos los miembros de los equipos en los que sea investigador principal
   */
  findMiembrosEquipoInvestigador(): Observable<string[]> {
    return this.get<string[]>(`${this.endpointUrl}/investigador`);
  }

  /**
   * Comprueba si existe un miembro adscrito en el rango de fechas del equipo de investigación
   *
   * @param idGrupoEquipo Identificador del grupo equipo investigación
   */
  existsLineaInvestigadorInFechasGrupoEquipo(idGrupoEquipo: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idGrupoEquipo}/gruposlineasinvestigadores`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
