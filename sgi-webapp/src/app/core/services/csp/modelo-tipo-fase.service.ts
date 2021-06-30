import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { ModeloEjecucionService } from './modelo-ejecucion.service';

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoFaseService extends SgiRestService<number, IModeloTipoFase> {
  private static readonly MAPPING = '/modelotipofases';

  constructor(protected http: HttpClient) {
    super(
      ModeloEjecucionService.name,
      `${environment.serviceServers.csp}${ModeloTipoFaseService.MAPPING}`,
      http
    );
  }
}
