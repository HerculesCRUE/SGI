import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_CONTEXTO_CONVERTER } from '@core/converters/csp/proyecto-contexto.converter';
import { IProyectoContextoBackend } from '@core/models/csp/backend/proyecto-contexto-backend';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ContextoProyectoService extends SgiMutableRestService<number, IProyectoContextoBackend, IProyectoContexto> {
  private static readonly MAPPING = '/proyecto-contextoproyectos';

  constructor(protected http: HttpClient) {
    super(
      ContextoProyectoService.name,
      `${environment.serviceServers.csp}${ContextoProyectoService.MAPPING}`,
      http,
      PROYECTO_CONTEXTO_CONVERTER
    );
  }

}
