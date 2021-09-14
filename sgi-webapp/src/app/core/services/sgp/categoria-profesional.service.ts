import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { environment } from '@env';
import { SgiRestBaseService, SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class CategoriaProfesionalService extends SgiRestService<string, ICategoriaProfesional>{
  private static readonly MAPPING = '/categorias-profesionales';

  constructor(protected http: HttpClient) {
    super(
      CategoriaProfesionalService.name,
      `${environment.serviceServers.sgp}${CategoriaProfesionalService.MAPPING}`,
      http
    );
  }

}
