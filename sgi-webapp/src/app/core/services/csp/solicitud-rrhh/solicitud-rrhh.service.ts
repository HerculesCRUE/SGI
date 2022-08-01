import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { ISolicitudRrhhRequisitoCategoria } from '@core/models/csp/solicitud-rrhh-requisito-categoria';
import { ISolicitudRrhhRequisitoNivelAcademico } from '@core/models/csp/solicitud-rrhh-requisito-nivel-academico';
import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate,
  SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor
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
import { ISolicitudRrhhRequest } from './solicitud-rrhh-request';
import { SOLICITUD_RRHH_REQUEST_CONVERTER } from './solicitud-rrhh-request.converter';
import { ISolicitudRrhhResponse } from './solicitud-rrhh-response';
import { SOLICITUD_RRHH_RESPONSE_CONVERTER } from './solicitud-rrhh-response.converter';
import { SOLICITUD_RRHH_TUTOR_REQUEST_CONVERTER } from './solicitud-rrhh-tutor-request.converter';
import { ISolicitudRrhhTutorResponse } from './solicitud-rrhh-tutor-response';
import { SOLICITUD_RRHH_TUTOR_RESPONSE_CONVERTER } from './solicitud-rrhh-tutor-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudRrhhMixinBase:
  CreateCtor<ISolicitudRrhh, ISolicitudRrhh, ISolicitudRrhhRequest, ISolicitudRrhhResponse> &
  UpdateCtor<number, ISolicitudRrhh, ISolicitudRrhh, ISolicitudRrhhRequest, ISolicitudRrhhResponse> &
  FindByIdCtor<number, ISolicitudRrhh, ISolicitudRrhhResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        SOLICITUD_RRHH_REQUEST_CONVERTER,
        SOLICITUD_RRHH_RESPONSE_CONVERTER
      ),
      SOLICITUD_RRHH_REQUEST_CONVERTER,
      SOLICITUD_RRHH_RESPONSE_CONVERTER
    ),
    SOLICITUD_RRHH_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudRrhhService extends _SolicitudRrhhMixinBase {
  private static readonly MAPPING = '/solicitudes-rrhh';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudRrhhService.MAPPING}`,
      http,
    );
  }

  /**
   * Devuelve los datos del tutor de una solicitud de RRHH
   *
   * @param solicitudRrhhId Id de la solicitud
   */
  findTutor(solicitudRrhhId: number): Observable<ISolicitudRrhhTutor> {
    return this.http.get<ISolicitudRrhhTutorResponse>(
      `${this.endpointUrl}/${solicitudRrhhId}/tutor`
    ).pipe(
      map(response => SOLICITUD_RRHH_TUTOR_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Actualiza los datos del tutor de una solicitud de RRHH
   *
   * @param solicitudRrhhId Id de la solicitud
   * @param tutor ISolicitudRrhhTutor
   */
  updateTutor(solicitudRrhhId: number, tutor: ISolicitudRrhhTutor): Observable<ISolicitudRrhhTutor> {
    return this.http.patch<ISolicitudRrhhTutorResponse>(
      `${this.endpointUrl}/${solicitudRrhhId}/tutor`,
      SOLICITUD_RRHH_TUTOR_REQUEST_CONVERTER.fromTarget(tutor)
    ).pipe(
      map(response => SOLICITUD_RRHH_TUTOR_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Devuelve los datos de la memoria de una solicitud de RRHH
   *
   * @param solicitudRrhhId Id de la solicitud
   */
  findMemoria(solicitudRrhhId: number): Observable<ISolicitudRrhhMemoria> {
    return this.http.get<ISolicitudRrhhMemoriaResponse>(
      `${this.endpointUrl}/${solicitudRrhhId}/memoria`
    ).pipe(
      map(response => SOLICITUD_RRHH_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Actualiza los datos de la memoria de una solicitud de RRHH
   *
   * @param solicitudRrhhId Id de la solicitud
   * @param memoria ISolicitudRrhhMemoria
   */
  updateMemoria(solicitudRrhhId: number, memoria: ISolicitudRrhhMemoria): Observable<ISolicitudRrhhMemoria> {
    return this.http.patch<ISolicitudRrhhMemoriaResponse>(
      `${this.endpointUrl}/${solicitudRrhhId}/memoria`,
      SOLICITUD_RRHH_MEMORIA_REQUEST_CONVERTER.fromTarget(memoria)
    ).pipe(
      map(response => SOLICITUD_RRHH_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  findAllRequisitosCategoriaAcreditados(
    id: number,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<ISolicitudRrhhRequisitoCategoria>> {
    return this.find<ISolicitudRrhhRequisitoCategoriaResponse, ISolicitudRrhhRequisitoCategoria>(
      `${this.endpointUrl}/${id}/requisitos-categoria`,
      options,
      SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER
    );
  }

  findAllRequisitosNivelAcademicoAcreditados(
    id: number,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<ISolicitudRrhhRequisitoNivelAcademico>> {
    return this.find<ISolicitudRrhhRequisitoNivelAcademicoResponse, ISolicitudRrhhRequisitoNivelAcademico>(
      `${this.endpointUrl}/${id}/requisitos-nivel-academico`,
      options,
      SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER
    );
  }

}
