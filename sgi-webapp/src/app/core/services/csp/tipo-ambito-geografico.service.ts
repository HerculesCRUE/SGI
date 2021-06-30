import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { environment } from '@env';
import { SgiReadOnlyMutableRestService, SgiRestService } from '@sgi/framework/http/';

@Injectable({
  providedIn: 'root'
})
export class TipoAmbitoGeograficoService extends SgiRestService<number, ITipoAmbitoGeografico> {
  private static readonly MAPPING = '/tipoambitogeograficos';

  constructor(protected http: HttpClient) {
    super(
      TipoAmbitoGeograficoService.name,
      `${environment.serviceServers.csp}${TipoAmbitoGeograficoService.MAPPING}`,
      http
    );
  }
}
