import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitanteExterno } from '@core/models/csp/solicitante-externo';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SOLICITANTE_EXTERNO_REQUEST_CONVERTER } from './solicitante-externo-request.converter';
import { ISolicitanteExternoResponse } from './solicitante-externo-response';
import { SOLICITANTE_EXTERNO_RESPONSE_CONVERTER } from './solicitante-externo-response.converter';

@Injectable({
  providedIn: 'root'
})
export class SolicitanteExternoPublicService extends SgiRestBaseService {
  private static readonly SOLICITUD_ID = '{solicitudId}';
  private static readonly MAPPING = `/solicitudes/${SolicitanteExternoPublicService.SOLICITUD_ID}/solicitantes-externos`;
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitanteExternoPublicService.PUBLIC_PREFIX}${SolicitanteExternoPublicService.MAPPING}`,
      http,
    );
  }

  create(solicitante: ISolicitanteExterno): Observable<ISolicitanteExterno> {
    return this.http.post<ISolicitanteExternoResponse>(
      `${this.getEndpointUrl('new')}`,
      SOLICITANTE_EXTERNO_REQUEST_CONVERTER.fromTarget(solicitante)
    ).pipe(
      map(response => SOLICITANTE_EXTERNO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  update(
    solicitudPublicId: string,
    solicitanteId: number,
    solicitante: ISolicitanteExterno
  ): Observable<ISolicitanteExterno> {
    return this.http.put<ISolicitanteExternoResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitanteId}`,
      SOLICITANTE_EXTERNO_REQUEST_CONVERTER.fromTarget(solicitante)
    ).pipe(
      map(response => SOLICITANTE_EXTERNO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  private getEndpointUrl(solicitudPublicId: string): string {
    return this.endpointUrl.replace(SolicitanteExternoPublicService.SOLICITUD_ID, solicitudPublicId);
  }

}
