import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs/internal/Observable';
import { IDocumentoRequeridoSolicitudResponse } from './documento-requerido-solicitud/documento-requerido-solicitud-response';
import { DOCUMENTO_REQUERIDO_SOLICITUD_RESPONSE_CONVERTER } from './documento-requerido-solicitud/documento-requerido-solicitud-response.converter';

const _ConvocatoriaDocumentoRequeridoServiceMixinBase:
  CreateCtor<IDocumentoRequeridoSolicitud, IDocumentoRequeridoSolicitud, IDocumentoRequeridoSolicitudResponse, IDocumentoRequeridoSolicitudResponse> &
  UpdateCtor<number, IDocumentoRequeridoSolicitud, IDocumentoRequeridoSolicitud, IDocumentoRequeridoSolicitudResponse, IDocumentoRequeridoSolicitudResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      DOCUMENTO_REQUERIDO_SOLICITUD_RESPONSE_CONVERTER,
      DOCUMENTO_REQUERIDO_SOLICITUD_RESPONSE_CONVERTER
    ),
    DOCUMENTO_REQUERIDO_SOLICITUD_RESPONSE_CONVERTER,
    DOCUMENTO_REQUERIDO_SOLICITUD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class DocumentoRequeridoSolicitudService extends _ConvocatoriaDocumentoRequeridoServiceMixinBase {
  private static readonly MAPPING = '/documentorequiridosolicitudes';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${DocumentoRequeridoSolicitudService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
