import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_HITO_CONVERTER } from '@core/converters/csp/solicitud-hito.converter';
import { ISolicitudHitoBackend } from '@core/models/csp/backend/solicitud-hito-backend';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SolicitudHitoService extends SgiMutableRestService<number, ISolicitudHitoBackend, ISolicitudHito> {
  static readonly MAPPING = '/solicitudhitos';

  constructor(protected http: HttpClient) {
    super(
      SolicitudHitoService.name,
      `${environment.serviceServers.csp}${SolicitudHitoService.MAPPING}`,
      http,
      SOLICITUD_HITO_CONVERTER
    );
  }
}
