import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-financiadora.converter';
import { IConvocatoriaEntidadFinanciadoraBackend } from '@core/models/csp/backend/convocatoria-entidad-financiadora-backend';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaEntidadFinanciadoraService
  extends SgiMutableRestService<number, IConvocatoriaEntidadFinanciadoraBackend, IConvocatoriaEntidadFinanciadora> {
  private static readonly MAPPING = '/convocatoriaentidadfinanciadoras';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaEntidadFinanciadoraService.name,
      `${environment.serviceServers.csp}${ConvocatoriaEntidadFinanciadoraService.MAPPING}`,
      http,
      CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER
    );
  }
}
