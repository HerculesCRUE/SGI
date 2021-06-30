import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-gestora.converter';
import { IConvocatoriaEntidadGestoraBackend } from '@core/models/csp/backend/convocatoria-entidad-gestora-backend';
import { IConvocatoriaEntidadGestora } from '@core/models/csp/convocatoria-entidad-gestora';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaEntidadGestoraService
  extends SgiMutableRestService<number, IConvocatoriaEntidadGestoraBackend, IConvocatoriaEntidadGestora> {
  private static readonly MAPPING = '/convocatoriaentidadgestoras';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaEntidadGestoraService.name,
      `${environment.serviceServers.csp}${ConvocatoriaEntidadGestoraService.MAPPING}`,
      http,
      CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER
    );
  }
}
