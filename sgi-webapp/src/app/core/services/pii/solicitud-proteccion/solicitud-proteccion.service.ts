import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPaisValidado } from '@core/models/pii/pais-validado';
import { IProcedimiento } from '@core/models/pii/procedimiento';
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
  SgiRestBaseService, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IPaisValidadoResponse } from './pais-validado/pais-validado-response';
import { PAIS_VALIDADO_RESPONSE_CONVERTER } from './pais-validado/pais-validado-response.converter';
import { IProcedimientoResponse } from './solicitud-proteccion-procedimiento/solicitud-proteccion-procedimiento-response';
import { SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER } from './solicitud-proteccion-procedimiento/solicitud-proteccion-procedimiento-response.converter';
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
   * Elimina la solicitud de proteccion
   * @param id Id de la {@link ISolicitudProteccion}
   */
  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
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

  /*
   * Devuelve todos los {@link IPaisValidado} asociados a la {@link ISolicitudProteccion} pasada por parametros
   * @param invencionId Id de la {@link ISolicitudProteccion}
   * @returns Listado de {@link IPaisValidado}
   */
  findPaisesValidadosBySolicitudProteccionId(solicitudProteccionId: number): Observable<SgiRestListResult<IPaisValidado>> {
    return this.find<IPaisValidadoResponse, IPaisValidado>(
      `${this.endpointUrl}/${solicitudProteccionId}/paisesvalidados`,
      {},
      PAIS_VALIDADO_RESPONSE_CONVERTER
    );
  }

  /**
   * Obtiene los {@link IProcedimiento} relacionados a una {@link ISolicitudProteccion}
   *
   * @param solicitudId Id de la {@link ISolicitudProteccion}
   * @returns Lista de {@link IProcedimiento} asociados a la {@link ISolicitudProteccion} pasada por parámetros
   */
  findProcedimientosBySolicitudId(solicitudId: number): Observable<SgiRestListResult<IProcedimiento>> {

    return this.find<IProcedimientoResponse, IProcedimiento>(
      `${this.endpointUrl}/${solicitudId}/procedimientos`,
      {},
      SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Indica si la solicitud de proteccion tiene relacionados algún pais validado
   *
   * @param id Id de la solicitud de proteccion
   * @returns **true** si tiene relaciones, **false** en cualquier otro caso
   */
  hasPaisesValidados(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/paisesvalidados`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Indica si la solicitud de proteccion tiene relacionados algún procedimiento
   *
   * @param id Id de la solicitud de proteccion
   * @returns **true** si tiene relaciones, **false** en cualquier otro caso
   */
  hasProcedimientos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/procedimientos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Indica si la solicitud de proteccion tiene relacionados algún gasto
   *
   * @param id Id de la solicitud de proteccion
   * @returns **true** si tiene relaciones, **false** en cualquier otro caso
   */
  hasInvencionGastos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/invenciongastos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
