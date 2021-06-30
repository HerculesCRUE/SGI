import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_ENTIDAD_FINANCIADORA_CONVERTER } from '@core/converters/csp/proyecto-entidad-financiadora.converter';
import { IProyectoEntidadFinanciadoraBackend } from '@core/models/csp/backend/proyecto-entidad-financiadora-backend';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoEntidadFinanciadoraService
  extends SgiMutableRestService<number, IProyectoEntidadFinanciadoraBackend, IProyectoEntidadFinanciadora> {
  private static readonly MAPPING = '/proyectoentidadfinanciadoras';

  constructor(protected http: HttpClient) {
    super(
      ProyectoEntidadFinanciadoraService.name,
      `${environment.serviceServers.csp}${ProyectoEntidadFinanciadoraService.MAPPING}`,
      http,
      PROYECTO_ENTIDAD_FINANCIADORA_CONVERTER
    );
  }
}
