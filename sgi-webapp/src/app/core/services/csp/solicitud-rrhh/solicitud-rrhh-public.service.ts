import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { ISolicitudRrhhRequisitoCategoria } from '@core/models/csp/solicitud-rrhh-requisito-categoria';
import { ISolicitudRrhhRequisitoNivelAcademico } from '@core/models/csp/solicitud-rrhh-requisito-nivel-academico';
import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { environment } from '@env';
import {
  SgiRestBaseService, SgiRestFindOptions, SgiRestListResult
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  ISolicitudRrhhRequisitoCategoriaResponse
} from '../solicitud-rrhh-requisito-categoria/solicitud-rrhh-requisito-categoria-response';
import { SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER } from '../solicitud-rrhh-requisito-categoria/solicitud-rrhh-requisito-categoria-response.converter';
import { ISolicitudRrhhRequisitoNivelAcademicoResponse } from '../solicitud-rrhh-requisito-nivel-academico/solicitud-rrhh-requisito-nivel-academico-response';
import { SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER } from '../solicitud-rrhh-requisito-nivel-academico/solicitud-rrhh-requisito-nivel-academico-response.converter';
import { SOLICITUD_RRHH_MEMORIA_REQUEST_CONVERTER } from './solicitud-rrhh-memoria-request.converter';
import { ISolicitudRrhhMemoriaResponse } from './solicitud-rrhh-memoria-response';
import { SOLICITUD_RRHH_MEMORIA_RESPONSE_CONVERTER } from './solicitud-rrhh-memoria-response.converter';
import { SOLICITUD_RRHH_REQUEST_CONVERTER } from './solicitud-rrhh-request.converter';
import { ISolicitudRrhhResponse } from './solicitud-rrhh-response';
import { SOLICITUD_RRHH_RESPONSE_CONVERTER } from './solicitud-rrhh-response.converter';
import { SOLICITUD_RRHH_TUTOR_REQUEST_CONVERTER } from './solicitud-rrhh-tutor-request.converter';
import { ISolicitudRrhhTutorResponse } from './solicitud-rrhh-tutor-response';
import { SOLICITUD_RRHH_TUTOR_RESPONSE_CONVERTER } from './solicitud-rrhh-tutor-response.converter';

@Injectable({
  providedIn: 'root'
})
export class SolicitudRrhhPublicService extends SgiRestBaseService {
  private static readonly SOLICITUD_ID = '{solicitudId}';
  private static readonly MAPPING = `/solicitudes/${SolicitudRrhhPublicService.SOLICITUD_ID}/solicitudes-rrhh`;
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudRrhhPublicService.PUBLIC_PREFIX}${SolicitudRrhhPublicService.MAPPING}`,
      http,
    );
  }

  create(solicitudPublicId: string, solicitud: ISolicitudRrhh): Observable<ISolicitudRrhh> {
    return this.http.post<ISolicitudRrhhResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}`,
      SOLICITUD_RRHH_REQUEST_CONVERTER.fromTarget(solicitud)
    ).pipe(
      map(response => SOLICITUD_RRHH_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  update(
    solicitudPublicId: string,
    solicitanteId: number,
    solicitante: ISolicitudRrhh
  ): Observable<ISolicitudRrhh> {
    return this.http.put<ISolicitudRrhhResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/${solicitanteId}`,
      SOLICITUD_RRHH_REQUEST_CONVERTER.fromTarget(solicitante)
    ).pipe(
      map(response => SOLICITUD_RRHH_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  findTutor(solicitudPublicId: string): Observable<ISolicitudRrhhTutor> {
    return this.http.get<ISolicitudRrhhTutorResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/tutor`
    ).pipe(
      map(response => SOLICITUD_RRHH_TUTOR_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  updateTutor(solicitudPublicId: string, tutor: ISolicitudRrhhTutor): Observable<ISolicitudRrhhTutor> {
    return this.http.patch<ISolicitudRrhhTutorResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/tutor`,
      SOLICITUD_RRHH_TUTOR_REQUEST_CONVERTER.fromTarget(tutor)
    ).pipe(
      map(response => SOLICITUD_RRHH_TUTOR_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  findMemoria(solicitudPublicId: string): Observable<ISolicitudRrhhMemoria> {
    return this.http.get<ISolicitudRrhhMemoriaResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/memoria`
    ).pipe(
      map(response => SOLICITUD_RRHH_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  updateMemoria(solicitudPublicId: string, memoria: ISolicitudRrhhMemoria): Observable<ISolicitudRrhhMemoria> {
    return this.http.patch<ISolicitudRrhhMemoriaResponse>(
      `${this.getEndpointUrl(solicitudPublicId)}/memoria`,
      SOLICITUD_RRHH_MEMORIA_REQUEST_CONVERTER.fromTarget(memoria)
    ).pipe(
      map(response => SOLICITUD_RRHH_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  findAllRequisitosCategoriaAcreditados(
    solicitudPublicId: string,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<ISolicitudRrhhRequisitoCategoria>> {
    return this.find<ISolicitudRrhhRequisitoCategoriaResponse, ISolicitudRrhhRequisitoCategoria>(
      `${this.getEndpointUrl(solicitudPublicId)}/requisitos-categoria`,
      options,
      SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER
    );
  }

  findAllRequisitosNivelAcademicoAcreditados(
    solicitudPublicId: string,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<ISolicitudRrhhRequisitoNivelAcademico>> {
    return this.find<ISolicitudRrhhRequisitoNivelAcademicoResponse, ISolicitudRrhhRequisitoNivelAcademico>(
      `${this.getEndpointUrl(solicitudPublicId)}/requisitos-nivel-academico`,
      options,
      SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER
    );
  }

  private getEndpointUrl(solicitudPublicId: string): string {
    return this.endpointUrl.replace(SolicitudRrhhPublicService.SOLICITUD_ID, solicitudPublicId);
  }

}
