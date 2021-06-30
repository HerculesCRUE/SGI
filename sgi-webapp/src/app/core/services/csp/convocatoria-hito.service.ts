import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_HITO_CONVERTER } from '@core/converters/csp/convocatoria-hito.converter';
import { IConvocatoriaHitoBackend } from '@core/models/csp/backend/convocatoria-hito-backend';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaHitoService extends SgiMutableRestService<number, IConvocatoriaHitoBackend, IConvocatoriaHito> {
  private static readonly MAPPING = '/convocatoriahitos';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaHitoService.name,
      `${environment.serviceServers.csp}${ConvocatoriaHitoService.MAPPING}`,
      http,
      CONVOCATORIA_HITO_CONVERTER
    );
  }

}
