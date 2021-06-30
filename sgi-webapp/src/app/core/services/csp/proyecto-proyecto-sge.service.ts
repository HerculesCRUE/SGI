import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_PROYECTO_SGE_CONVERTER } from '@core/converters/csp/proyecto-proyecto-sge.converter';
import { IProyectoProyectoSgeBackend } from '@core/models/csp/backend/proyecto-proyecto-sge-backend';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoProyectoSgeService
  extends SgiMutableRestService<number, IProyectoProyectoSgeBackend, IProyectoProyectoSge> {
  private static readonly MAPPING = '/proyecto-proyectos-sge';

  constructor(protected http: HttpClient) {
    super(
      ProyectoProyectoSgeService.name,
      `${environment.serviceServers.csp}${ProyectoProyectoSgeService.MAPPING}`,
      http,
      PROYECTO_PROYECTO_SGE_CONVERTER
    );
  }
}
