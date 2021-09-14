import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISexo } from '@core/models/sgp/sexo';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SexoService extends SgiRestService<string, ISexo>{
  private static readonly MAPPING = '/sexos';

  constructor(protected http: HttpClient) {
    super(
      SexoService.name,
      `${environment.serviceServers.sgp}${SexoService.MAPPING}`,
      http
    );
  }

}
