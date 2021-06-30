import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_AREA_CONOCIMIENTO_CONVERTER } from '@core/converters/csp/proyecto-area-conocimiento.converter';
import { IProyectoAreaConocimientoBackend } from '@core/models/csp/backend/proyecto-area-conocimiento-backend';
import { IProyectoAreaConocimiento } from '@core/models/csp/proyecto-area-conocimiento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoAreaConocimientoService
  extends SgiMutableRestService<number, IProyectoAreaConocimientoBackend, IProyectoAreaConocimiento> {
  private static readonly MAPPING = '/proyecto-areas-conocimiento';

  constructor(protected http: HttpClient) {
    super(
      ProyectoAreaConocimientoService.name,
      `${environment.serviceServers.csp}${ProyectoAreaConocimientoService.MAPPING}`,
      http,
      PROYECTO_AREA_CONOCIMIENTO_CONVERTER
    );
  }
}
