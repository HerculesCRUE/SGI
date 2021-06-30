import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER } from '@core/converters/csp/solicitud-proyecto-entidad-financiadora-ajena.converter';
import { ISolicitudProyectoEntidadFinanciadoraAjenaBackend } from '@core/models/csp/backend/solicitud-proyecto-entidad-financiadora-ajena-backend';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

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
}
