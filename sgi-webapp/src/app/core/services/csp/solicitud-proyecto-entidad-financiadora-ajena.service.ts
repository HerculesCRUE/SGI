import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER } from '@core/converters/csp/solicitud-proyecto-entidad-financiadora-ajena.converter';
import { ISolicitudProyectoEntidadFinanciadoraAjenaBackend } from '@core/models/csp/backend/solicitud-proyecto-entidad-financiadora-ajena-backend';
import { ISolicitudProyectoEntidad } from '@core/models/csp/solicitud-proyecto-entidad';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISolicitudProyectoEntidadResponse } from './solicitud-proyecto-entidad/solicitud-proyecto-entidad-response';
import { SOLICITUD_PROYECTO_ENTIDAD_RESPONSE_CONVERTER } from './solicitud-proyecto-entidad/solicitud-proyecto-entidad-response.converter';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoEntidadFinanciadoraAjenaService
  extends SgiMutableRestService<number, ISolicitudProyectoEntidadFinanciadoraAjenaBackend, ISolicitudProyectoEntidadFinanciadoraAjena> {
  private static readonly MAPPING = '/solicitudproyectoentidadfinanciadoraajenas';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoEntidadFinanciadoraAjenaService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoEntidadFinanciadoraAjenaService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER
    );
  }

  /**
   * Comprueba la existencia de presupuestos de una solicitud para una entidad financiadora ajena
   *
   * @param id Id de la Entidad
   * @returns **true** si existe alguna relación, **false** en cualquier otro caso
   */
  hasSolicitudProyectoPresupuestoEntidad(id: number): Observable<boolean> {
    return this.http.head(
      `${this.endpointUrl}/${id}/solicitudproyectopresupuestos`,
      { observe: 'response' }
    ).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Devuelve la ISolicitudProyectoEntidad de la SolicitudProyectoEntidadFinanciadoraAjena
   *
   * @param id Id de la Entidad
   */
  getSolicitudProyectoEntidad(id: number): Observable<ISolicitudProyectoEntidad> {
    return this.http.get<ISolicitudProyectoEntidadResponse>(
      `${this.endpointUrl}/${id}/solicitudproyectoentidad`
    ).pipe(
      map(response => SOLICITUD_PROYECTO_ENTIDAD_RESPONSE_CONVERTER.toTarget(response))
    );
  }

}
