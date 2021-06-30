import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_CLASIFICACION_CONVERTER } from '@core/converters/csp/solicitud-proyecto-clasificacion.converter';
import { ISolicitudProyectoClasificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-clasificacion-backend';
import { ISolicitudProyectoClasificacion } from '@core/models/csp/solicitud-proyecto-clasificacion';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoClasificacionService
  extends SgiMutableRestService<number, ISolicitudProyectoClasificacionBackend, ISolicitudProyectoClasificacion> {
  private static readonly MAPPING = '/solicitud-proyecto-clasificaciones';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoClasificacionService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoClasificacionService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_CLASIFICACION_CONVERTER
    );
  }
}
