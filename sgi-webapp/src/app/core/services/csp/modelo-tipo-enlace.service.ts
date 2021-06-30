import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoEnlaceService extends SgiRestService<number, IModeloTipoEnlace> {
  private static readonly MAPPING = '/modelotipoenlaces';

  constructor(protected http: HttpClient) {
    super(
      ModeloTipoEnlaceService.name,
      `${environment.serviceServers.csp}${ModeloTipoEnlaceService.MAPPING}`,
      http
    );
  }
}
