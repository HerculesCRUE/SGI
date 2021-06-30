import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER } from '@core/converters/csp/documento-requerido-solicitud.converter';
import { IDocumentoRequeridoSolicitudBackend } from '@core/models/csp/backend/documento-requerido-solicitud-backend';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class DocumentoRequeridoSolicitudService
  extends SgiMutableRestService<number, IDocumentoRequeridoSolicitudBackend, IDocumentoRequeridoSolicitud> {
  private static readonly MAPPING = '/documentorequiridosolicitudes';

  constructor(protected http: HttpClient) {
    super(
      DocumentoRequeridoSolicitudService.name,
      `${environment.serviceServers.csp}${DocumentoRequeridoSolicitudService.MAPPING}`,
      http,
      DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER
    );
  }
}
