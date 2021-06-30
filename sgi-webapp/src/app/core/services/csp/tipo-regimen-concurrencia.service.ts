import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoRegimenConcurrencia } from '@core/models/csp/tipo-regimen-concurrencia';
import { environment } from '@env';
import { SgiReadOnlyMutableRestService } from '@sgi/framework/http/';

@Injectable({
  providedIn: 'root'
})
export class TipoRegimenConcurrenciaService
  extends SgiReadOnlyMutableRestService<number, ITipoRegimenConcurrencia, ITipoRegimenConcurrencia> {
  private static readonly MAPPING = '/tiporegimenconcurrencias';

  constructor(protected http: HttpClient) {
    super(
      TipoRegimenConcurrenciaService.name,
      `${environment.serviceServers.csp}${TipoRegimenConcurrenciaService.MAPPING}`,
      http, null
    );
  }
}
