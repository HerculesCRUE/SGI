import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_ENLACE_CONVERTER } from '@core/converters/csp/convocatoria-enlace.converter';
import { IConvocatoriaEnlaceBackend } from '@core/models/csp/backend/convocatoria-enlace-backend';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaEnlaceService extends SgiMutableRestService<number, IConvocatoriaEnlaceBackend, IConvocatoriaEnlace> {
  private static readonly MAPPING = '/convocatoriaenlaces';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaEnlaceService.name,
      `${environment.serviceServers.csp}${ConvocatoriaEnlaceService.MAPPING}`,
      http,
      CONVOCATORIA_ENLACE_CONVERTER
    );
  }

}
