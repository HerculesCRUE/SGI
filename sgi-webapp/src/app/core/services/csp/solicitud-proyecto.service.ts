import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_CONVERTER } from '@core/converters/csp/solicitud-proyecto.converter';
import { ISolicitudProyectoBackend } from '@core/models/csp/backend/solicitud-proyecto-backend';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoService extends SgiMutableRestService<number, ISolicitudProyectoBackend, ISolicitudProyecto> {
  private static readonly MAPPING = '/solicitudproyecto';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_CONVERTER
    );
  }

  /**
   * Comprueba si SolicitudProyectoDatos tiene SolicitudProyectoPresupuesto
   * relacionado
   *
   * @param id solicitudProyectoDatos
   */
  hasSolicitudPresupuesto(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudpresupuesto`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si SolicitudProyectoDatos tiene SolicitudProyectoSocio
   * relacionado
   *
   * @param id solicitudProyectoDatos
   */
  hasSolicitudSocio(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudsocio`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
