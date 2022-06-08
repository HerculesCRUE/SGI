import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IGrupoEnlaceRequest } from './grupo-enlace-request';
import { GRUPO_ENLACE_REQUEST_CONVERTER } from './grupo-enlace-request.converter';
import { IGrupoEnlaceResponse } from './grupo-enlace-response';
import { GRUPO_ENLACE_RESPONSE_CONVERTER } from './grupo-enlace-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoEnlaceMixinBase:
  CreateCtor<IGrupoEnlace, IGrupoEnlace, IGrupoEnlaceRequest, IGrupoEnlaceResponse> &
  UpdateCtor<number, IGrupoEnlace, IGrupoEnlace, IGrupoEnlaceRequest, IGrupoEnlaceResponse> &
  FindByIdCtor<number, IGrupoEnlace, IGrupoEnlaceResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        GRUPO_ENLACE_REQUEST_CONVERTER,
        GRUPO_ENLACE_RESPONSE_CONVERTER
      ),
      GRUPO_ENLACE_REQUEST_CONVERTER,
      GRUPO_ENLACE_RESPONSE_CONVERTER
    ),
    GRUPO_ENLACE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoEnlaceService extends _GrupoEnlaceMixinBase {
  private static readonly MAPPING = '/grupoenlaces';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoEnlaceService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
