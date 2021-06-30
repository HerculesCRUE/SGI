import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MODELO_UNIDAD_CONVERTER } from '@core/converters/csp/modelo-unidad.converter';
import { IModeloUnidadBackend } from '@core/models/csp/backend/modelo-unidad-backend';
import { IModeloUnidad } from '@core/models/csp/modelo-unidad';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ModeloUnidadService extends
  SgiMutableRestService<number, IModeloUnidadBackend, IModeloUnidad> {
  private static readonly MAPPING = '/modelounidades';

  constructor(protected http: HttpClient) {
    super(
      ModeloUnidadService.name,
      `${environment.serviceServers.csp}${ModeloUnidadService.MAPPING}`,
      http,
      MODELO_UNIDAD_CONVERTER
    );
  }

}
