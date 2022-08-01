import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IProyectoHitoRequest } from './proyecto-hito-request';
import { PROYECTO_HITO_REQUEST_CONVERTER } from './proyecto-hito-request.converter';
import { IProyectoHitoResponse } from './proyecto-hito-response';
import { PROYECTO_HITO_RESPONSE_CONVERTER } from './proyecto-hito-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoHitoServiceMixinBase:
  CreateCtor<IProyectoHito, IProyectoHito, IProyectoHitoRequest, IProyectoHitoResponse> &
  UpdateCtor<number, IProyectoHito, IProyectoHito, IProyectoHitoRequest, IProyectoHitoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      PROYECTO_HITO_REQUEST_CONVERTER,
      PROYECTO_HITO_RESPONSE_CONVERTER
    ),
    PROYECTO_HITO_REQUEST_CONVERTER,
    PROYECTO_HITO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoHitoService extends _ProyectoHitoServiceMixinBase {
  private static readonly MAPPING = '/proyectohitos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoHitoService.MAPPING}`,
      http);
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
