import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER } from '@core/converters/csp/convocatoria-entidad-convocante.converter';
import { IConvocatoriaEntidadConvocanteBackend } from '@core/models/csp/backend/convocatoria-entidad-convocante-backend';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaEntidadConvocanteService
  extends SgiMutableRestService<number, IConvocatoriaEntidadConvocanteBackend, IConvocatoriaEntidadConvocante> {
  private static readonly MAPPING = '/convocatoriaentidadconvocantes';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaEntidadConvocanteService.name,
      `${environment.serviceServers.csp}${ConvocatoriaEntidadConvocanteService.MAPPING}`,
      http,
      CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER
    );
  }
}
