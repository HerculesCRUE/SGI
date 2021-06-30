import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { environment } from '@env';
import { SgiReadOnlyMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class RolSocioService extends SgiReadOnlyMutableRestService<number, IRolSocio, IRolSocio>  {
  private static readonly MAPPING = '/rolsocios';

  constructor(protected http: HttpClient) {
    super(
      RolSocioService.name,
      `${environment.serviceServers.csp}${RolSocioService.MAPPING}`,
      http, null
    );
  }
}
