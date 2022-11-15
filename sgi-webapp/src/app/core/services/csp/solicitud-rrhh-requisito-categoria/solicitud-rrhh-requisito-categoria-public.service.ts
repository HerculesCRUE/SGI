import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudRrhhRequisitoCategoria } from '@core/models/csp/solicitud-rrhh-requisito-categoria';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SOLICITUD_RRHH_REQUISITO_CATEGORIA_REQUEST_CONVERTER } from './solicitud-rrhh-requisito-categoria-request.converter';
import { ISolicitudRrhhRequisitoCategoriaResponse } from './solicitud-rrhh-requisito-categoria-response';
import { SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER } from './solicitud-rrhh-requisito-categoria-response.converter';

@Injectable({
  providedIn: 'root'
})
export class SolicitudRrhhRequisitoCategoriaPublicService extends SgiRestBaseService {
  private static readonly SOLICITUD_ID = '{solicitudId}';
  private static readonly MAPPING = `/solicitudes/${SolicitudRrhhRequisitoCategoriaPublicService.SOLICITUD_ID}/solicitud-rrhh-requisitos-categoria`;
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudRrhhRequisitoCategoriaPublicService.PUBLIC_PREFIX}${SolicitudRrhhRequisitoCategoriaPublicService.MAPPING}`,
      http,
    );
  }

  create(
    solicitudPublicId: string,
    solicitudRrhhRequisitoCategoria: ISolicitudRrhhRequisitoCategoria
  ): Observable<ISolicitudRrhhRequisitoCategoria> {
    return this.http.post<ISolicitudRrhhRequisitoCategoriaResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}`,
      SOLICITUD_RRHH_REQUISITO_CATEGORIA_REQUEST_CONVERTER.fromTarget(solicitudRrhhRequisitoCategoria)
    ).pipe(
      map(response => SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  update(
    solicitudPublicId: string,
    solicitudRrhhRequisitoCategoriaId: number,
    solicitudRrhhRequisitoCategoria: ISolicitudRrhhRequisitoCategoria
  ): Observable<ISolicitudRrhhRequisitoCategoria> {
    return this.http.put<ISolicitudRrhhRequisitoCategoriaResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitudRrhhRequisitoCategoriaId}`,
      SOLICITUD_RRHH_REQUISITO_CATEGORIA_REQUEST_CONVERTER.fromTarget(solicitudRrhhRequisitoCategoria)
    ).pipe(
      map(response => SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  delete(solicitudPublicId: string, solicitudRrhhRequisitoCategoriaId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitudRrhhRequisitoCategoriaId}`,
    );
  }

  private getEndpointUrl(solicitudPublicId: string): string {
    return this.endpointUrl.replace(SolicitudRrhhRequisitoCategoriaPublicService.SOLICITUD_ID, solicitudPublicId);
  }

}
