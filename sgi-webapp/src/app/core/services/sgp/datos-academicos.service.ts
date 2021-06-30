import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDatosAcademicos } from '@core/models/sgp/datos-academicos';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DatosAcademicosService extends SgiRestService<string, IDatosAcademicos>{
  private static readonly MAPPING = '/datos-academicos';

  constructor(protected http: HttpClient) {
    super(
      DatosAcademicosService.name,
      `${environment.serviceServers.sgp}${DatosAcademicosService.MAPPING}`,
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
    return this.http.get<IDatosAcademicos>(`${this.endpointUrl}/persona/${personaId}`);
  }

}
