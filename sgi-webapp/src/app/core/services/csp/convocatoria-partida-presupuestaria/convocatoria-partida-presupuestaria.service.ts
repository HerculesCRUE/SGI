import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, SgiRestBaseService, UpdateCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConvocatoriaPartidaPresupuestariaRequest } from './convocatoria-partida-presupuestaria-request';
import { CONVOCATORIA_PARTIDA_PRESUPUESTARIA_REQUEST_CONVERTER } from './convocatoria-partida-presupuestaria-request.converter';
import { IConvocatoriaPartidaPresupuestariaResponse } from './convocatoria-partida-presupuestaria-response';
import { CONVOCATORIA_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER } from './convocatoria-partida-presupuestaria-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaPartidaPresupuestariaMixinBase:
  CreateCtor<IConvocatoriaPartidaPresupuestaria, IConvocatoriaPartidaPresupuestaria, IConvocatoriaPartidaPresupuestariaRequest, IConvocatoriaPartidaPresupuestariaResponse> &
  UpdateCtor<number, IConvocatoriaPartidaPresupuestaria, IConvocatoriaPartidaPresupuestaria, IConvocatoriaPartidaPresupuestariaRequest, IConvocatoriaPartidaPresupuestariaResponse> &
  FindByIdCtor<number, IConvocatoriaPartidaPresupuestaria, IConvocatoriaPartidaPresupuestariaResponse> &
  FindAllCtor<IConvocatoriaPartidaPresupuestaria, IConvocatoriaPartidaPresupuestariaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          CONVOCATORIA_PARTIDA_PRESUPUESTARIA_REQUEST_CONVERTER,
          CONVOCATORIA_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER
        ),
        CONVOCATORIA_PARTIDA_PRESUPUESTARIA_REQUEST_CONVERTER,
        CONVOCATORIA_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER
      ),
      CONVOCATORIA_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER
    ),
    CONVOCATORIA_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaPartidaPresupuestariaService extends _ConvocatoriaPartidaPresupuestariaMixinBase {
  private static readonly MAPPING = '/convocatoria-partidas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaPartidaPresupuestariaService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
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
