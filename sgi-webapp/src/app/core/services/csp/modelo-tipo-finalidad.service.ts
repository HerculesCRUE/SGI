import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoFinalidadService extends SgiRestService<number, IModeloTipoFinalidad> {
  private static readonly MAPPING = '/modelotipofinalidades';

  constructor(protected http: HttpClient) {
    super(
      ModeloTipoFinalidadService.name,
      `${environment.serviceServers.csp}${ModeloTipoFinalidadService.MAPPING}`,
      http
    );
  }
}
