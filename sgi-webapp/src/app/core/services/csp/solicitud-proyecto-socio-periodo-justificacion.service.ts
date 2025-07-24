import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { ISolicitudProyectoSocioPeriodoJustificacionResponse } from '@core/services/csp/solicitud-proyecto-socio-periodo-justificacion/solicitud-proyecto-socio-periodo-justificacion-response';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER } from './solicitud-proyecto-socio-periodo-justificacion/solicitud-proyecto-socio-periodo-justificacion-response.converter';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSocioPeriodoJustificacionService extends SgiRestBaseService {
  private static readonly MAPPING = '/solicitudproyectosocioperiodojustificaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudProyectoSocioPeriodoJustificacionService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoSocioPeriodoJustificacion[]):
    Observable<ISolicitudProyectoSocioPeriodoJustificacion[]> {
    return this.http.patch<ISolicitudProyectoSocioPeriodoJustificacionResponse[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }
}
