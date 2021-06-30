import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_PLAZO_CONVERTER } from '@core/converters/csp/proyecto-plazo.converter';
import { IProyectoPlazoBackend } from '@core/models/csp/backend/proyecto-plazo-backend';
import { IProyectoPlazos } from '@core/models/csp/proyecto-plazo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPlazoService extends SgiMutableRestService<number, IProyectoPlazoBackend, IProyectoPlazos> {
  private static readonly MAPPING = '/proyectofases';

  constructor(protected http: HttpClient) {
    super(
      ProyectoPlazoService.name,
      `${environment.serviceServers.csp}${ProyectoPlazoService.MAPPING}`,
      http,
      PROYECTO_PLAZO_CONVERTER
    );
  }

}
