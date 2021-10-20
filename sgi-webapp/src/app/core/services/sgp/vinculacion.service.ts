import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IVinculacionBackend } from '@core/models/sgp/backend/vinculacion-backend';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { VINCULACION_CONVERTER } from './vinculacion-converter';

@Injectable({
  providedIn: 'root'
})
export class VinculacionService extends SgiRestService<string, IVinculacion>{
  private static readonly MAPPING = '/vinculaciones';

  constructor(protected http: HttpClient) {
    super(
      VinculacionService.name,
      `${environment.serviceServers.sgp}${VinculacionService.MAPPING}`,
      http
    );
  }

  /**
   * Busca la vinculacion de la persona
   *
   * @param personaId identificador de la persona
   * @returns vinculacion de la persona
   */
  findByPersonaId(personaId: string): Observable<IVinculacion> {
    return this.http.get<IVinculacionBackend>(`${this.endpointUrl}/persona/${personaId}`).pipe(
      map(response => VINCULACION_CONVERTER.toTarget(response))
    );
  }

}
