import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ISolicitudDocumentoResponse } from '@core/services/csp/solicitud-documento/solicitud-documento-response';
import { SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER } from '@core/services/csp/solicitud-documento/solicitud-documento-response.converter';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';

const _SolicitudDocumentoServiceMixinBase:
  CreateCtor<ISolicitudDocumento, ISolicitudDocumento, ISolicitudDocumentoResponse, ISolicitudDocumentoResponse> &
  UpdateCtor<number, ISolicitudDocumento, ISolicitudDocumento, ISolicitudDocumentoResponse, ISolicitudDocumentoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER,
      SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER
    ),
    SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER,
    SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudDocumentoService extends _SolicitudDocumentoServiceMixinBase {
  private static readonly MAPPING = '/solicituddocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudDocumentoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
