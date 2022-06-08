import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoLineaEquipoInstrumental } from '@core/models/csp/grupo-linea-equipo-instrumental';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { GRUPO_LINEA_EQUIPO_INSTRUMENTAL_REQUEST_CONVERTER } from './grupo-linea-equipo-instrumental-request.converter';
import { IGrupoLineaEquipoInstrumentalResponse } from './grupo-linea-equipo-instrumental-response';
import { GRUPO_LINEA_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER } from './grupo-linea-equipo-instrumental-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoLineaEquipoInstrumentalMixinBase:
  FindByIdCtor<number, IGrupoLineaEquipoInstrumental, IGrupoLineaEquipoInstrumentalResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    GRUPO_LINEA_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoLineaEquipoInstrumentalService extends _GrupoLineaEquipoInstrumentalMixinBase {
  private static readonly MAPPING = '/gruposlineasequiposinstrumentales';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoLineaEquipoInstrumentalService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Actualiza el listado de GrupoLineaEquipoInstrumental asociados a un grupo
   *
   * @param id Id del IGrupoLineaEquipoInstrumental
   * @param entities Listado de IGrupoLineaEquipoInstrumental
   */
  updateList(id: number, entities: IGrupoLineaEquipoInstrumental[]): Observable<IGrupoLineaEquipoInstrumental[]> {
    return this.http.patch<IGrupoLineaEquipoInstrumentalResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_LINEA_EQUIPO_INSTRUMENTAL_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_LINEA_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
