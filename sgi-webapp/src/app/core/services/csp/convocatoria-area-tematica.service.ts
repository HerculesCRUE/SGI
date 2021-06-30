import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_AREA_TEMATICA_CONVERTER } from '@core/converters/csp/convocatoria-area-tematica.converter';
import { IConvocatoriaAreaTematicaBackend } from '@core/models/csp/backend/convocatoria-area-tematica-backend';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaAreaTematicaService
  extends SgiMutableRestService<number, IConvocatoriaAreaTematicaBackend, IConvocatoriaAreaTematica> {
  private static readonly MAPPING = '/convocatoriaareatematicas';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaAreaTematicaService.name,
      `${environment.serviceServers.csp}${ConvocatoriaAreaTematicaService.MAPPING}`,
      http,
      CONVOCATORIA_AREA_TEMATICA_CONVERTER
    );
  }
}
