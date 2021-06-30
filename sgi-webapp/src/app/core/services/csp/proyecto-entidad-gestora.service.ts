import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_ENTIDAD_GESTORA_CONVERTER } from '@core/converters/csp/proyecto-entidad-gestora.converter';
import { IProyectoEntidadGestoraBackend } from '@core/models/csp/backend/proyecto-entidad-gestora-backend';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoEntidadGestoraService extends SgiMutableRestService<number, IProyectoEntidadGestoraBackend, IProyectoEntidadGestora> {
  private static readonly MAPPING = '/proyectoentidadgestoras';

  constructor(protected http: HttpClient) {
    super(
      ProyectoEntidadGestoraService.name,
      `${environment.serviceServers.csp}${ProyectoEntidadGestoraService.MAPPING}`,
      http,
      PROYECTO_ENTIDAD_GESTORA_CONVERTER
    );
  }
}
