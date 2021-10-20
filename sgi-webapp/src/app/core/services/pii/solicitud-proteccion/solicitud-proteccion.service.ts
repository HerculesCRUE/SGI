import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISolicitudProteccionRequest } from './solicitud-proteccion-request';
import { SOLICITUD_PROTECCION_REQUEST_CONVERTER } from './solicitud-proteccion-request.converter';
import { ISolicitudProteccionResponse } from './solicitud-proteccion-response';
import { SOLICITUD_PROTECCION_RESPONSE_CONVERTER } from './solicitud-proteccion-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudProteccionServiceMixinBase:
  CreateCtor<ISolicitudProteccion, ISolicitudProteccion, ISolicitudProteccionRequest, ISolicitudProteccionResponse> &
  UpdateCtor<number, ISolicitudProteccion, ISolicitudProteccion, ISolicitudProteccionRequest, ISolicitudProteccionResponse> &
  FindByIdCtor<number, ISolicitudProteccion, ISolicitudProteccionResponse> &
  FindAllCtor<ISolicitudProteccion, ISolicitudProteccionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          SOLICITUD_PROTECCION_REQUEST_CONVERTER,
          SOLICITUD_PROTECCION_RESPONSE_CONVERTER
        ),
        SOLICITUD_PROTECCION_REQUEST_CONVERTER,
        SOLICITUD_PROTECCION_RESPONSE_CONVERTER
      ),
      SOLICITUD_PROTECCION_RESPONSE_CONVERTER
    ),
    SOLICITUD_PROTECCION_RESPONSE_CONVERTER
  );
@Injectable({
  providedIn: 'root'
})
export class SolicitudProteccionService extends _SolicitudProteccionServiceMixinBase {

  private static readonly MAPPING = '/solicitudesproteccion';

  constructor(protected http: HttpClient) {
    super(`${environment.serviceServers.pii}${SolicitudProteccionService.MAPPING}`,
      http);
  }

  /**
   * Activar Solicitur de Proteccion
   * @param options opciones de búsqueda.
   */
  public activate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, {});
  }

  /**
   * Desactivar Solicitud de Proteccion
   * @param options Opciones de búsqueda.
   */
  public deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, {});
  }

  /**
   * Comprueba si existe una {@link ISolicitudProteccion} con el id pasado por parámetros
   *
   * @param id Id de la {@link ISolicitudProteccion}
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
