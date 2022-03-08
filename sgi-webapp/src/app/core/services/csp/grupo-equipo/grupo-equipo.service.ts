import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IGrupoEquipoRequest } from './grupo-equipo-request';
import { IGrupoEquipoResponse } from './grupo-equipo-response';
import { GRUPO_EQUIPO_RESPONSE_CONVERTER } from './grupo-equipo-response.converter';
import { GRUPO_EQUIPO_REQUEST_CONVERTER } from './grupo-request.converter';

// tslint:disable-next-line: variable-name
const _GrupoEquipoMixinBase:
  CreateCtor<IGrupoEquipo, IGrupoEquipo, IGrupoEquipoRequest, IGrupoEquipoResponse> &
  UpdateCtor<number, IGrupoEquipo, IGrupoEquipo, IGrupoEquipoRequest, IGrupoEquipoResponse> &
  FindByIdCtor<number, IGrupoEquipo, IGrupoEquipoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        GRUPO_EQUIPO_REQUEST_CONVERTER,
        GRUPO_EQUIPO_RESPONSE_CONVERTER
      ),
      GRUPO_EQUIPO_REQUEST_CONVERTER,
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

}
