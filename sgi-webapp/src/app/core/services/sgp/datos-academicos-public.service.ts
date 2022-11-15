import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDatosAcademicos } from '@core/models/sgp/datos-academicos';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IDatosAcademicosResponse } from './datos-academicos/datos-academicos-response';
import { DATOS_ACADEMICOS_RESPONSE_CONVERTER } from './datos-academicos/datos-academicos-response.converter';

@Injectable({
  providedIn: 'root'
})
export class DatosAcademicosPublicService extends SgiRestBaseService {
  private static readonly MAPPING = '/datos-academicos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${DatosAcademicosPublicService.PUBLIC_PREFIX}${DatosAcademicosPublicService.MAPPING}`,
      http
    );
  }

  /**
   * Busca los datos academicos de la persona
   *
   * @param personaId identificador de la persona
   * @returns datos academicos de la persona
   */
  findByPersonaId(personaId: string): Observable<IDatosAcademicos> {
    return this.get<IDatosAcademicosResponse>(`${this.endpointUrl}/persona/${personaId}`).pipe(
      map(response => DATOS_ACADEMICOS_RESPONSE_CONVERTER.toTarget(response))
    );
  }


}
