import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SGE_CONVERTER } from '@core/converters/sge/proyecto-sge.converter';
import { IProyectoSgeBackend } from '@core/models/sge/backend/proyecto-sge-backend';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSgeService extends SgiMutableRestService<string, IProyectoSgeBackend, IProyectoSge>{
  private static readonly MAPPING = '/proyectos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoSgeService.name,
      `${environment.serviceServers.sge}${ProyectoSgeService.MAPPING}`,
      http,
      PROYECTO_SGE_CONVERTER
    );
  }

}
