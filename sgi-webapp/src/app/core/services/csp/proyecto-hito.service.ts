import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_HITO_CONVERTER } from '@core/converters/csp/proyecto-hito.converter';
import { IProyectoHitoBackend } from '@core/models/csp/backend/proyecto-hito-backend';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoHitoService extends SgiMutableRestService<number, IProyectoHitoBackend, IProyectoHito> {
  private static readonly MAPPING = '/proyectohitos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoHitoService.name,
      `${environment.serviceServers.csp}${ProyectoHitoService.MAPPING}`,
      http,
      PROYECTO_HITO_CONVERTER
    );
  }

}
