import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, SgiRestBaseService, UpdateCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoPartidaPresupuestariaRequest } from './proyecto-partida-presupuestaria-request';
import { PROYECTO_PARTIDA_PRESUPUESTARIA_REQUEST_CONVERTER } from './proyecto-partida-presupuestaria-request.converter';
import { IProyectoPartidaPresupuestariaResponse } from './proyecto-partida-presupuestaria-response';
import { PROYECTO_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER } from './proyecto-partida-presupuestaria-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoPartidaPresupuestariaMixinBase:
  CreateCtor<IProyectoPartida, IProyectoPartida, IProyectoPartidaPresupuestariaRequest, IProyectoPartidaPresupuestariaResponse> &
  UpdateCtor<number, IProyectoPartida, IProyectoPartida, IProyectoPartidaPresupuestariaRequest, IProyectoPartidaPresupuestariaResponse> &
  FindByIdCtor<number, IProyectoPartida, IProyectoPartidaPresupuestariaResponse> &
  FindAllCtor<IProyectoPartida, IProyectoPartidaPresupuestariaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROYECTO_PARTIDA_PRESUPUESTARIA_REQUEST_CONVERTER,
          PROYECTO_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER
        ),
        PROYECTO_PARTIDA_PRESUPUESTARIA_REQUEST_CONVERTER,
        PROYECTO_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER
      ),
      PROYECTO_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER
    ),
    PROYECTO_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoPartidaPresupuestariaService extends _ProyectoPartidaPresupuestariaMixinBase {
  private static readonly MAPPING = '/proyecto-partidas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoPartidaPresupuestariaService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Comprueba si tiene permisos de edición del proyecto partida presupuestaria
   *
   * @param id Id del proyecto partida presupuestaria
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
