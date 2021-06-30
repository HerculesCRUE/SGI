import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

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

}
