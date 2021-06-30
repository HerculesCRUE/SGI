import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_MODALIDAD_CONVERTER } from '@core/converters/csp/solicitud-modalidad.converter';
import { ISolicitudModalidadBackend } from '@core/models/csp/backend/solicitud-modalidad-backend';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SolicitudModalidadService extends SgiMutableRestService<number, ISolicitudModalidadBackend, ISolicitudModalidad> {
  static readonly MAPPING = '/solicitudmodalidades';

  constructor(protected http: HttpClient) {
    super(
      SolicitudModalidadService.name,
      `${environment.serviceServers.csp}${SolicitudModalidadService.MAPPING}`,
      http,
      SOLICITUD_MODALIDAD_CONVERTER
    );
  }

}
