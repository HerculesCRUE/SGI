import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_AREA_CONOCIMIENTO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-area-conocimiento.converter';
import { ISolicitudProyectoAreaConocimientoBackend } from '@core/models/csp/backend/solicitud-proyecto-area-conocimiento-backend';
import { ISolicitudProyectoAreaConocimiento } from '@core/models/csp/solicitud-proyecto-area-conocimiento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoAreaConocimientoService
  extends SgiMutableRestService<number, ISolicitudProyectoAreaConocimientoBackend, ISolicitudProyectoAreaConocimiento> {
  private static readonly MAPPING = '/solicitud-proyecto-areas-conocimiento';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoAreaConocimientoService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoAreaConocimientoService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_AREA_CONOCIMIENTO_CONVERTER
    );
  }
}
