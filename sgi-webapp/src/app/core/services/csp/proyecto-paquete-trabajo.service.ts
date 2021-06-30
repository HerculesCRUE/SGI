import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_PAQUETE_TRABAJO_CONVERTER } from '@core/converters/csp/proyecto-paquete-trabajo.converter';
import { IProyectoPaqueteTrabajoBackend } from '@core/models/csp/backend/proyecto-paquete-trabajo-backend';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPaqueteTrabajoService extends SgiMutableRestService<number, IProyectoPaqueteTrabajoBackend, IProyectoPaqueteTrabajo> {
  private static readonly MAPPING = '/proyectopaquetetrabajos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoPaqueteTrabajoService.name,
      `${environment.serviceServers.csp}${ProyectoPaqueteTrabajoService.MAPPING}`,
      http,
      PROYECTO_PAQUETE_TRABAJO_CONVERTER
    );
  }

}
