import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { SOLICITUD_MODALIDAD_RESPONSE_CONVERTER } from '@core/services/csp/solicitud-modalidad/solicitud-modalidad-response.converter';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ISolicitudModalidadResponse } from './solicitud-modalidad/solicitud-modalidad-response';

// tslint:disable-next-line: variable-name
const _SolicitudModalidadServiceMixinBase:
  CreateCtor<ISolicitudModalidad, ISolicitudModalidad, ISolicitudModalidadResponse, ISolicitudModalidadResponse> &
  UpdateCtor<number, ISolicitudModalidad, ISolicitudModalidad, ISolicitudModalidadResponse, ISolicitudModalidadResponse> &
  typeof SgiRestBaseService = mixinCreate(
    mixinUpdate(
      SgiRestBaseService,
      SOLICITUD_MODALIDAD_RESPONSE_CONVERTER,
      SOLICITUD_MODALIDAD_RESPONSE_CONVERTER
    ),
    SOLICITUD_MODALIDAD_RESPONSE_CONVERTER,
    SOLICITUD_MODALIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudModalidadService extends _SolicitudModalidadServiceMixinBase {
  static readonly MAPPING = '/solicitudmodalidades';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudModalidadService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
