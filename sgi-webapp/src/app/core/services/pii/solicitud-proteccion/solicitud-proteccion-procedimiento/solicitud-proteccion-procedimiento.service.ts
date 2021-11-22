import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProcedimiento } from '@core/models/pii/procedimiento';
import { IProcedimientoDocumento } from '@core/models/pii/procedimiento-documento';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IProcedimientoDocumentoResponse } from '../solicitud-proteccion-procedimiento-documento/solicitud-proteccion-procedimiento-documento-response';
import { SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_RESPONSE_CONVERTER } from '../solicitud-proteccion-procedimiento-documento/solicitud-proteccion-procedimiento-documento-response.converter';
import { IProcedimientoRequest } from './solicitud-proteccion-procedimiento-request';
import { SOLICITUD_PROTECCION_PROCEDIMIENTO_REQUEST_CONVERTER } from './solicitud-proteccion-procedimiento-request.converter';
import { IProcedimientoResponse } from './solicitud-proteccion-procedimiento-response';
import { SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER } from './solicitud-proteccion-procedimiento-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudProteccionProcedimientoServiceMixinBase:
  CreateCtor<IProcedimiento, IProcedimiento, IProcedimientoRequest, IProcedimientoResponse> &
  UpdateCtor<number, IProcedimiento, IProcedimiento, IProcedimientoRequest, IProcedimientoResponse> &
  FindByIdCtor<number, IProcedimiento, IProcedimientoResponse> &
  FindAllCtor<IProcedimiento, IProcedimientoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          SOLICITUD_PROTECCION_PROCEDIMIENTO_REQUEST_CONVERTER,
          SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER
        ),
        SOLICITUD_PROTECCION_PROCEDIMIENTO_REQUEST_CONVERTER,
        SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER
      ),
      SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER
    ),
    SOLICITUD_PROTECCION_PROCEDIMIENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudProteccionProcedimientoService extends _SolicitudProteccionProcedimientoServiceMixinBase {

  private static readonly MAPPING = '/procedimientos';

  constructor(protected http: HttpClient) {
    super(`${environment.serviceServers.pii}${SolicitudProteccionProcedimientoService.MAPPING}`,
      http);
  }

  /**
   * Obtiene los {@link IProcedimientoDocumento} relacionados a un {@link IProcedimiento}
   *
   * @param procedimientoId Id del {@link IProcedimiento}
   * @returns Lista de {@link IProcedimientoDocumento} asociados al {@link IProcedimiento} pasado por parámetros
   */
  findProcedimientoDocumentosBySolicitudId(procedimientoId: number): Observable<SgiRestListResult<IProcedimientoDocumento>> {

    return this.find<IProcedimientoDocumentoResponse, IProcedimientoDocumento>(
      `${this.endpointUrl}/${procedimientoId}/documentos`,
      {},
      SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_RESPONSE_CONVERTER
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
