import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-presupuesto.converter';
import { ISolicitudProyectoPresupuestoBackend } from '@core/models/csp/backend/solicitud-proyecto-presupuesto-backend';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoPresupuestoService
  extends SgiMutableRestService<number, ISolicitudProyectoPresupuestoBackend, ISolicitudProyectoPresupuesto>  {
  private static readonly MAPPING = '/solicitudproyectopresupuestos';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoPresupuestoService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoPresupuestoService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER
    );
  }

}
