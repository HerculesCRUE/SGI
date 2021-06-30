import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root',
})
export class TipoConvocatoriaReunionService extends SgiRestService<number, TipoConvocatoriaReunion> {
  private static readonly MAPPING = '/tipoconvocatoriareuniones';

  constructor(protected http: HttpClient) {
    super(
      TipoConvocatoriaReunionService.name,
      `${environment.serviceServers.eti}${TipoConvocatoriaReunionService.MAPPING}`,
      http
    );
  }
}
