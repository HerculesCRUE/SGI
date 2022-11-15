import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudRrhhRequisitoNivelAcademico } from '@core/models/csp/solicitud-rrhh-requisito-nivel-academico';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_REQUEST_CONVERTER } from './solicitud-rrhh-requisito-nivel-academico-request.converter';
import { ISolicitudRrhhRequisitoNivelAcademicoResponse } from './solicitud-rrhh-requisito-nivel-academico-response';
import { SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER } from './solicitud-rrhh-requisito-nivel-academico-response.converter';

@Injectable({
  providedIn: 'root'
})
export class SolicitudRrhhRequisitoNivelAcademicoPublicService extends SgiRestBaseService {
  private static readonly SOLICITUD_ID = '{solicitudId}';
  private static readonly MAPPING = `/solicitudes/${SolicitudRrhhRequisitoNivelAcademicoPublicService.SOLICITUD_ID}/solicitud-rrhh-requisitos-nivel-academico`;
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudRrhhRequisitoNivelAcademicoPublicService.PUBLIC_PREFIX}${SolicitudRrhhRequisitoNivelAcademicoPublicService.MAPPING}`,
      http,
    );
  }

  create(
    solicitudPublicId: string,
    solicitudRrhhRequisitoCategoria: ISolicitudRrhhRequisitoNivelAcademico
  ): Observable<ISolicitudRrhhRequisitoNivelAcademico> {
    return this.http.post<ISolicitudRrhhRequisitoNivelAcademicoResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}`,
      SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_REQUEST_CONVERTER.fromTarget(solicitudRrhhRequisitoCategoria)
    ).pipe(
      map(response => SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  update(
    solicitudPublicId: string,
    solicitudRrhhRequisitoCategoriaId: number,
    solicitudRrhhRequisitoCategoria: ISolicitudRrhhRequisitoNivelAcademico
  ): Observable<ISolicitudRrhhRequisitoNivelAcademico> {
    return this.http.put<ISolicitudRrhhRequisitoNivelAcademicoResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitudRrhhRequisitoCategoriaId}`,
      SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_REQUEST_CONVERTER.fromTarget(solicitudRrhhRequisitoCategoria)
    ).pipe(
      map(response => SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  delete(solicitudPublicId: string, solicitudRrhhRequisitoCategoriaId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitudRrhhRequisitoCategoriaId}`,
    );
  }

  private getEndpointUrl(solicitudPublicId: string): string {
    return this.endpointUrl.replace(SolicitudRrhhRequisitoNivelAcademicoPublicService.SOLICITUD_ID, solicitudPublicId);
  }

}
