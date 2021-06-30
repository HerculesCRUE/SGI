import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_DOCUMENTO_CONVERTER } from '@core/converters/csp/solicitud-documento.converter';
import { ISolicitudDocumentoBackend } from '@core/models/csp/backend/solicitud-documento-backend';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SolicitudDocumentoService extends SgiMutableRestService<number, ISolicitudDocumentoBackend, ISolicitudDocumento> {
  private static readonly MAPPING = '/solicituddocumentos';

  constructor(protected http: HttpClient) {
    super(
      SolicitudDocumentoService.name,
      `${environment.serviceServers.csp}${SolicitudDocumentoService.MAPPING}`,
      http,
      SOLICITUD_DOCUMENTO_CONVERTER
    );
  }
}
