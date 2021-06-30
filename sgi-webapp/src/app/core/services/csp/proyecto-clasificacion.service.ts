import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_CLASIFICACION_CONVERTER } from '@core/converters/csp/proyecto-clasificacion.converter';
import { IProyectoClasificacionBackend } from '@core/models/csp/backend/proyecto-clasificacion-backend';
import { IProyectoClasificacion } from '@core/models/csp/proyecto-clasificacion';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoClasificacionService
  extends SgiMutableRestService<number, IProyectoClasificacionBackend, IProyectoClasificacion> {
  private static readonly MAPPING = '/proyecto-clasificaciones';

  constructor(protected http: HttpClient) {
    super(
      ProyectoClasificacionService.name,
      `${environment.serviceServers.csp}${ProyectoClasificacionService.MAPPING}`,
      http,
      PROYECTO_CLASIFICACION_CONVERTER
    );
  }
}
