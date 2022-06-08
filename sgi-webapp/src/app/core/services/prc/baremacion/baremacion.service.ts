import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDetalleProduccionInvestigador } from '@core/models/prc/detalle-produccion-investigador';
import { IResumenPuntuacionGrupoAnio } from '@core/models/prc/resumen-puntuacion-grupo-anio';
import { environment } from '@env';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BaremacionService {
  private static readonly MAPPING = '/baremacion';

  private readonly endpointUrl: string;

  constructor(protected http: HttpClient) {
    this.endpointUrl = `${environment.serviceServers.prc}${BaremacionService.MAPPING}`;
  }

  resumenPuntuacionGrupos(anio: number): Observable<IResumenPuntuacionGrupoAnio> {
    return this.http.get<IResumenPuntuacionGrupoAnio>(`${this.endpointUrl}/resumenpuntuaciongrupos/${anio}`);
  }

  detalleProduccionInvestigador(anio: number, personaRef: string): Observable<IDetalleProduccionInvestigador> {
    return this.http.get<IDetalleProduccionInvestigador>(`${this.endpointUrl}/detalleproduccioninvestigador/${anio}/${personaRef}`);
  }

  /**
   * Lanza el proceso de baremación
   * @param id id de la ConvocatoriaBaremacion.
   */
  createTaskBaremacion(id: number): Observable<number> {
    return this.http.post<number>(`${this.endpointUrl}/createTask/${id}`, { id });
  }
}
