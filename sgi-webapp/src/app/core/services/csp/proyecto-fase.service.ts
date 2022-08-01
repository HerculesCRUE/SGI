import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoFase } from '@core/models/csp/proyecto-fase';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IProyectoFaseRequest } from './proyecto-fase/proyecto-fase-request';
import { PROYECTO_FASE_REQUEST_CONVERTER } from './proyecto-fase/proyecto-fase-request.converter';
import { IProyectoFaseResponse } from './proyecto-fase/proyecto-fase-response';
import { PROYECTO_FASE_RESPONSE_CONVERTER } from './proyecto-fase/proyecto-fase-response.converter';

const _ProyectoFasesServiceMixinBase:
  CreateCtor<IProyectoFase, IProyectoFase, IProyectoFaseRequest, IProyectoFaseResponse> &
  UpdateCtor<number, IProyectoFase, IProyectoFase, IProyectoFaseRequest, IProyectoFaseResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      PROYECTO_FASE_REQUEST_CONVERTER,
      PROYECTO_FASE_RESPONSE_CONVERTER
    ),
    PROYECTO_FASE_REQUEST_CONVERTER,
    PROYECTO_FASE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoFaseService extends _ProyectoFasesServiceMixinBase {
  private static readonly MAPPING = '/proyectofases';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoFaseService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
