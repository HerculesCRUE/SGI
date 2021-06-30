import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER } from '@core/converters/csp/convocatoria-requisito-equipo.converter';
import { IConvocatoriaRequisitoEquipoBackend } from '@core/models/csp/backend/convocatoria-requisito-equipo-backend';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaRequisitoEquipoService
  extends SgiMutableRestService<number, IConvocatoriaRequisitoEquipoBackend, IConvocatoriaRequisitoEquipo> {
  private static readonly MAPPING = '/convocatoria-requisitoequipos';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaRequisitoEquipoService.name,
      `${environment.serviceServers.csp}${ConvocatoriaRequisitoEquipoService.MAPPING}`,
      http,
      CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER
    );
  }
}
