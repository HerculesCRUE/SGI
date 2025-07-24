import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { SOLICITUD_MODALIDAD_RESPONSE_CONVERTER } from '@core/services/csp/solicitud-modalidad/solicitud-modalidad-response.converter';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISolicitudModalidadResponse } from './solicitud-modalidad/solicitud-modalidad-response';

@Injectable({
  providedIn: 'root'
})
export class SolicitudModalidadPublicService extends SgiRestBaseService {
  private static readonly SOLICITUD_ID = '{solicitudId}';
  private static readonly MAPPING = `/solicitudes/${SolicitudModalidadPublicService.SOLICITUD_ID}/solicitudmodalidades`;
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudModalidadPublicService.PUBLIC_PREFIX}${SolicitudModalidadPublicService.MAPPING}`,
      http
    );
  }

  create(solicitudPublicId: string, solicitudModalidad: ISolicitudModalidad): Observable<ISolicitudModalidad> {
    return this.http.post<ISolicitudModalidadResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}`,
      SOLICITUD_MODALIDAD_RESPONSE_CONVERTER.fromTarget(solicitudModalidad)
    ).pipe(
      map(response => SOLICITUD_MODALIDAD_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  update(
    solicitudPublicId: string,
    solicitudModalidadId: number,
    solicitudModalidad: ISolicitudModalidad
  ): Observable<ISolicitudModalidad> {
    return this.http.put<ISolicitudModalidadResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitudModalidadId}`,
      SOLICITUD_MODALIDAD_RESPONSE_CONVERTER.fromTarget(solicitudModalidad)
    ).pipe(
      map(response => SOLICITUD_MODALIDAD_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  delete(solicitudPublicId: string, solicitudModalidadId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitudModalidadId}`,
    );
  }

  private getEndpointUrl(solicitudPublicId: string): string {
    return this.endpointUrl.replace(SolicitudModalidadPublicService.SOLICITUD_ID, solicitudPublicId);
  }

}
