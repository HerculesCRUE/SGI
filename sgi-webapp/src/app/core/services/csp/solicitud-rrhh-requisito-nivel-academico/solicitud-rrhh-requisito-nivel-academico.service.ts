import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudRrhhRequisitoNivelAcademico } from '@core/models/csp/solicitud-rrhh-requisito-nivel-academico';
import { environment } from '@env';
import { CreateCtor, mixinCreate, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ISolicitudRrhhRequisitoNivelAcademicoRequest } from './solicitud-rrhh-requisito-nivel-academico-request';
import { SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_REQUEST_CONVERTER } from './solicitud-rrhh-requisito-nivel-academico-request.converter';
import { ISolicitudRrhhRequisitoNivelAcademicoResponse } from './solicitud-rrhh-requisito-nivel-academico-response';
import { SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER } from './solicitud-rrhh-requisito-nivel-academico-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudRrhhRequisitoNivelAcademicoMixinBase:
  CreateCtor<
    ISolicitudRrhhRequisitoNivelAcademico,
    ISolicitudRrhhRequisitoNivelAcademico,
    ISolicitudRrhhRequisitoNivelAcademicoRequest,
    ISolicitudRrhhRequisitoNivelAcademicoResponse
  > &
  typeof SgiRestBaseService = mixinCreate(
    SgiRestBaseService,
    SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_REQUEST_CONVERTER,
    SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudRrhhRequisitoNivelAcademicoService extends _SolicitudRrhhRequisitoNivelAcademicoMixinBase {
  private static readonly MAPPING = '/solicitud-rrhh-requisitos-nivel-academico';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudRrhhRequisitoNivelAcademicoService.MAPPING}`,
      http,
    );
  }


  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
