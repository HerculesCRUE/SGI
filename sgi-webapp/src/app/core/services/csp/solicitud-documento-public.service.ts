import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_DOCUMENTO_CONVERTER } from '@core/converters/csp/solicitud-documento.converter';
import { ISolicitudDocumentoBackend } from '@core/models/csp/backend/solicitud-documento-backend';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudDocumentoPublicService extends SgiRestBaseService {
  private static readonly SOLICITUD_ID = '{solicitudId}';
  private static readonly MAPPING = `/solicitudes/${SolicitudDocumentoPublicService.SOLICITUD_ID}/solicituddocumentos`;
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudDocumentoPublicService.PUBLIC_PREFIX}${SolicitudDocumentoPublicService.MAPPING}`,
      http
    );
  }

  create(solicitudPublicId: string, solicitudDocumento: ISolicitudDocumento): Observable<ISolicitudDocumento> {
    return this.http.post<ISolicitudDocumentoBackend>(
      `${this.getEndpointUrl(solicitudPublicId)}`,
      SOLICITUD_DOCUMENTO_CONVERTER.fromTarget(solicitudDocumento)
    ).pipe(
      map(response => SOLICITUD_DOCUMENTO_CONVERTER.toTarget(response))
    );
  }

  update(
    solicitudPublicId: string,
    solicitudDocumentoId: number,
    solicitudDocumento: ISolicitudDocumento
  ): Observable<ISolicitudDocumento> {
    return this.http.put<ISolicitudDocumentoBackend>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitudDocumentoId}`,
      SOLICITUD_DOCUMENTO_CONVERTER.fromTarget(solicitudDocumento)
    ).pipe(
      map(response => SOLICITUD_DOCUMENTO_CONVERTER.toTarget(response))
    );
  }

  delete(solicitudPublicId: string, solicitudDocumentoId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitudDocumentoId}`,
    );
  }

  private getEndpointUrl(solicitudPublicId: string): string {
    return this.endpointUrl.replace(SolicitudDocumentoPublicService.SOLICITUD_ID, solicitudPublicId);
  }


}
