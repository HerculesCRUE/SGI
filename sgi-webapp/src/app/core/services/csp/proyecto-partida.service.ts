import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPartidaService extends SgiRestService<number, IProyectoPartida> {
  private static readonly MAPPING = '/proyecto-partidas';

  constructor(protected http: HttpClient) {
    super(
      ProyectoPartidaService.name,
      `${environment.serviceServers.csp}${ProyectoPartidaService.MAPPING}`,
      http
    );
  }

  /**
   * Comprueba si tiene permisos de edición de la convocatoria partida presupuestaria
   *
   * @param id Id de la convocatoria partida presupuestaria
   */
  modificable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  public hasAnyAnualidadAssociated(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/anualidades`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
