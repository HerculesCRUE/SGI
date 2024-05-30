import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyectoSge } from '@core/models/sge/solicitud-proyecto-sge';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISolicitudProyectoSgeResponse } from './solicitud-proyecto-sge-response';
import { SOLICITUD_PROYECTO_SGE_RESPONSE_CONVERTER } from './solicitud-proyecto-sge-response.converter';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSgeService extends SgiRestBaseService {
  private static readonly MAPPING = '/solicitudes-proyecto';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${SolicitudProyectoSgeService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra todas las solicitudes pendientes asociadas al proyecto
   *
   * @param proyectoSgiId Identifiador del proyecto en el SGI
   */
  findPendientes(proyectoSgiId: number): Observable<ISolicitudProyectoSge[]> {
    return this.find<ISolicitudProyectoSgeResponse, ISolicitudProyectoSge>(
      `${this.endpointUrl}/${proyectoSgiId}/pendientes`,
      null,
      SOLICITUD_PROYECTO_SGE_RESPONSE_CONVERTER
    ).pipe(
      map(response => response.items)
    );
  }

}
