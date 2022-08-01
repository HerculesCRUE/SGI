import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudRrhhRequisitoCategoria } from '@core/models/csp/solicitud-rrhh-requisito-categoria';
import { environment } from '@env';
import { CreateCtor, mixinCreate, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ISolicitudRrhhRequisitoCategoriaRequest } from './solicitud-rrhh-requisito-categoria-request';
import { SOLICITUD_RRHH_REQUISITO_CATEGORIA_REQUEST_CONVERTER } from './solicitud-rrhh-requisito-categoria-request.converter';
import { ISolicitudRrhhRequisitoCategoriaResponse } from './solicitud-rrhh-requisito-categoria-response';
import { SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER } from './solicitud-rrhh-requisito-categoria-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudRrhhRequisitoCategoriaMixinBase:
  CreateCtor<
    ISolicitudRrhhRequisitoCategoria,
    ISolicitudRrhhRequisitoCategoria,
    ISolicitudRrhhRequisitoCategoriaRequest,
    ISolicitudRrhhRequisitoCategoriaResponse
  > &
  typeof SgiRestBaseService = mixinCreate(
    SgiRestBaseService,
    SOLICITUD_RRHH_REQUISITO_CATEGORIA_REQUEST_CONVERTER,
    SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudRrhhRequisitoCategoriaService extends _SolicitudRrhhRequisitoCategoriaMixinBase {
  private static readonly MAPPING = '/solicitud-rrhh-requisitos-categoria';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudRrhhRequisitoCategoriaService.MAPPING}`,
      http,
    );
  }


  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
