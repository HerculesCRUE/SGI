import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { ModeloEjecucionService } from './modelo-ejecucion.service';

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoDocumentoService extends SgiRestService<number, IModeloTipoDocumento> {
  private static readonly MAPPING = '/modelotipodocumentos';

  constructor(protected http: HttpClient) {
    super(
      ModeloEjecucionService.name,
      `${environment.serviceServers.csp}${ModeloTipoDocumentoService.MAPPING}`,
      http
    );
  }
}
