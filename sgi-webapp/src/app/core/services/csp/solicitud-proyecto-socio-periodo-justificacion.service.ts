import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio-periodo-justificacion.converter';
import { ISolicitudProyectoSocioPeriodoJustificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-periodo-justificacion-backend';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSocioPeriodoJustificacionService
  extends SgiMutableRestService<number, ISolicitudProyectoSocioPeriodoJustificacionBackend, ISolicitudProyectoSocioPeriodoJustificacion> {
  private static readonly MAPPING = '/solicitudproyectosocioperiodojustificaciones';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoSocioPeriodoJustificacionService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoSocioPeriodoJustificacionService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoSocioPeriodoJustificacion[]):
    Observable<ISolicitudProyectoSocioPeriodoJustificacion[]> {
    return this.http.patch<ISolicitudProyectoSocioPeriodoJustificacionBackend[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
