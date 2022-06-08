import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoLineaClasificacion } from '@core/models/csp/grupo-linea-clasificacion';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { GRUPO_LINEA_CLASIFICACION_RESPONSE_CONVERTER } from './grupo-linea-clasificacion-response.converter';
import { IGrupoLineaClasificacionRequest } from './grupo-linea-clasificacion-request';
import { GRUPO_LINEA_CLASIFICACION_REQUEST_CONVERTER } from './grupo-linea-clasificacion-request.converter';
import { IGrupoLineaClasificacionResponse } from './grupo-linea-clasificacion-response';

// tslint:disable-next-line: variable-name
const _GrupoLineaClasificacionMixinBase:
  CreateCtor<IGrupoLineaClasificacion, IGrupoLineaClasificacion, IGrupoLineaClasificacionRequest, IGrupoLineaClasificacionResponse> &
  FindByIdCtor<number, IGrupoLineaClasificacion, IGrupoLineaClasificacionResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinCreate(
      SgiRestBaseService,
      GRUPO_LINEA_CLASIFICACION_REQUEST_CONVERTER,
      GRUPO_LINEA_CLASIFICACION_RESPONSE_CONVERTER
    ),
    GRUPO_LINEA_CLASIFICACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoLineaClasificacionService extends _GrupoLineaClasificacionMixinBase {
  private static readonly MAPPING = '/gruposlineasclasificaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoLineaClasificacionService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
