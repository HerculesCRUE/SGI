import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoHitoService extends SgiRestService<number, IModeloTipoHito> {
  private static readonly MAPPING = '/modelotipohitos';

  constructor(protected http: HttpClient) {
    super(
      ModeloTipoHitoService.name,
      `${environment.serviceServers.csp}${ModeloTipoHitoService.MAPPING}`,
      http
    );
  }
}
