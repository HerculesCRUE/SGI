import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGrupoEquipoInstrumentalRequest } from './grupo-equipo-instrumental-request';
import { IGrupoEquipoInstrumentalResponse } from './grupo-equipo-instrumental-response';
import { GRUPO_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER } from './grupo-equipo-instrumental-response.converter';
import { GRUPO_EQUIPO_INSTRUMENTAL_REQUEST_CONVERTER } from './grupo-equipo-instrumental-request.converter';

// tslint:disable-next-line: variable-name
const _GrupoEquipoInstrumentalMixinBase:
  CreateCtor<IGrupoEquipoInstrumental, IGrupoEquipoInstrumental, IGrupoEquipoInstrumentalRequest, IGrupoEquipoInstrumentalResponse> &
  UpdateCtor<number, IGrupoEquipoInstrumental, IGrupoEquipoInstrumental, IGrupoEquipoInstrumentalRequest, IGrupoEquipoInstrumentalResponse> &
  FindByIdCtor<number, IGrupoEquipoInstrumental, IGrupoEquipoInstrumentalResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        GRUPO_EQUIPO_INSTRUMENTAL_REQUEST_CONVERTER,
        GRUPO_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER
      ),
      GRUPO_EQUIPO_INSTRUMENTAL_REQUEST_CONVERTER,
      GRUPO_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER
    ),
    GRUPO_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoEquipoInstrumentalService extends _GrupoEquipoInstrumentalMixinBase {
  private static readonly MAPPING = '/gruposequiposinstrumentales';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoEquipoInstrumentalService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Actualiza el listado de IDGrupoEquipoInstrumental asociados a un IGrupoEquipoInstrumental
   *
   * @param id Id del IGrupoEquipoInstrumental
   * @param entities Listado de IGrupoEquipoInstrumental
   */
  updateList(id: number, entities: IGrupoEquipoInstrumental[]): Observable<IGrupoEquipoInstrumental[]> {
    return this.http.patch<IGrupoEquipoInstrumentalResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_EQUIPO_INSTRUMENTAL_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

  /**
   * Comprueba si existe un equipo instrumental en linea equipo instrumental
   *
   * @param idGrupoEquipo Identificador del grupo equipo instrumental
   */
  existsGrupoEquipoInstrumentalInGrupoLineaEquipoInstrumental(idGrupoEquipo: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idGrupoEquipo}/gruposlineasequiposinstrumentales`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
