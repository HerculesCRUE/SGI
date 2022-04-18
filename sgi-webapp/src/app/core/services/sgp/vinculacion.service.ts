import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { VINCULACION_CATEGORIA_PROFESIONAL_CONVERTER } from '@core/converters/sgp/vinculacion-categoria-profesional-converter';
import { IVinculacionBackend } from '@core/models/sgp/backend/vinculacion-backend';
import { IVinculacionCategoriaProfesionalBackend } from '@core/models/sgp/backend/vinculacion-categoria-profesional-backend';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { IVinculacionCategoriaProfesional } from '@core/models/sgp/vinculacion-categoria-profesional';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { get } from 'http';
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

  /**
   * Busca la vinculacion categoría profesional de la persona
   *
   * @param personaId identificador de la persona
   * @returns vinculacion de la persona
   */
  findVinculacionesCategoriasProfesionalesByPersonaId(personaId: string, options?: SgiRestFindOptions)
    : Observable<IVinculacionCategoriaProfesional> {
    const params = new HttpParams().set('q', options.filter.toString());
    return this.http.get<IVinculacionCategoriaProfesionalBackend>(
      `${this.endpointUrl}/persona/${personaId}/vinculaciones-categorias-profesionales`,
      { params }
    ).pipe(
      map(response => VINCULACION_CATEGORIA_PROFESIONAL_CONVERTER.toTarget(response))
    );
  }
}
