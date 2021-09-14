import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class NivelAcademicosService extends SgiRestService<string, INivelAcademico>{
  private static readonly MAPPING = '/niveles-academicos';

  constructor(protected http: HttpClient) {
    super(
      NivelAcademicosService.name,
      `${environment.serviceServers.sgp}${NivelAcademicosService.MAPPING}`,
      http
    );
  }
}
