import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_PARTIDA_PRESUPUESTARIA_CONVERTER } from '@core/converters/csp/convocatoria-partida.converter';
import { IConvocatoriaPartidaPresupuestariaBackend } from '@core/models/csp/backend/convocatoria-partida-backend';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaPartidaPresupuestariaService extends SgiMutableRestService<number, IConvocatoriaPartidaPresupuestariaBackend, IConvocatoriaPartidaPresupuestaria> {
  private static readonly MAPPING = '/convocatoria-partidas';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaPartidaPresupuestariaService.name,
      `${environment.serviceServers.csp}${ConvocatoriaPartidaPresupuestariaService.MAPPING}`,
      http,
      CONVOCATORIA_PARTIDA_PRESUPUESTARIA_CONVERTER
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

}
