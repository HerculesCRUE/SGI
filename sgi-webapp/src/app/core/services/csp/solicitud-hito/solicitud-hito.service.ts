import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ISolicitudHitoRequest } from './solicitud-hito-request';
import { SOLICITUD_HITO_REQUEST_CONVERTER } from './solicitud-hito-request.converter';
import { ISolicitudHitoResponse } from './solicitud-hito-response';
import { SOLICITUD_HITO_RESPONSE_CONVERTER } from './solicitud-hito-response.converter';


// tslint:disable-next-line: variable-name
const _SolicitudHitoServiceMixinBase:
  CreateCtor<ISolicitudHito, ISolicitudHito, ISolicitudHitoRequest, ISolicitudHitoResponse> &
  UpdateCtor<number, ISolicitudHito, ISolicitudHito, ISolicitudHitoRequest, ISolicitudHitoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      SOLICITUD_HITO_REQUEST_CONVERTER,
      SOLICITUD_HITO_RESPONSE_CONVERTER
    ),
    SOLICITUD_HITO_REQUEST_CONVERTER,
    SOLICITUD_HITO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudHitoService extends _SolicitudHitoServiceMixinBase {
  private static readonly MAPPING = '/solicitudhitos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudHitoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
