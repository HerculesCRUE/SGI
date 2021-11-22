import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProcedimientoDocumento } from '@core/models/pii/procedimiento-documento';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IProcedimientoDocumentoRequest } from './solicitud-proteccion-procedimiento-documento-request';
import { SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_REQUEST_CONVERTER } from './solicitud-proteccion-procedimiento-documento-request.converter';
import { IProcedimientoDocumentoResponse } from './solicitud-proteccion-procedimiento-documento-response';
import { SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_RESPONSE_CONVERTER } from './solicitud-proteccion-procedimiento-documento-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudProteccionProcedimientoDocumentoServiceMixinBase:
  CreateCtor<IProcedimientoDocumento, IProcedimientoDocumento, IProcedimientoDocumentoRequest, IProcedimientoDocumentoResponse> &
  UpdateCtor<number, IProcedimientoDocumento, IProcedimientoDocumento, IProcedimientoDocumentoRequest, IProcedimientoDocumentoResponse> &
  FindByIdCtor<number, IProcedimientoDocumento, IProcedimientoDocumentoResponse> &
  FindAllCtor<IProcedimientoDocumento, IProcedimientoDocumentoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_REQUEST_CONVERTER,
          SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_RESPONSE_CONVERTER
        ),
        SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_REQUEST_CONVERTER,
        SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_RESPONSE_CONVERTER
      ),
      SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_RESPONSE_CONVERTER
    ),
    SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudProteccionProcedimientoDocumentoService extends _SolicitudProteccionProcedimientoDocumentoServiceMixinBase {

  private static readonly MAPPING = '/procedimientodocumentos';

  constructor(protected http: HttpClient) {
    super(`${environment.serviceServers.pii}${SolicitudProteccionProcedimientoDocumentoService.MAPPING}`,
      http);
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
