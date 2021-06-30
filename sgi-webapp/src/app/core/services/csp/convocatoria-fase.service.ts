import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_FASE_CONVERTER } from '@core/converters/csp/convocatoria-fase.converter';
import { IConvocatoriaFaseBackend } from '@core/models/csp/backend/convocatoria-fase-backend';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaFaseService extends SgiMutableRestService<number, IConvocatoriaFaseBackend, IConvocatoriaFase> {
  private static readonly MAPPING = '/convocatoriafases';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaFaseService.name,
      `${environment.serviceServers.csp}${ConvocatoriaFaseService.MAPPING}`,
      http,
      CONVOCATORIA_FASE_CONVERTER
    );
  }
}
