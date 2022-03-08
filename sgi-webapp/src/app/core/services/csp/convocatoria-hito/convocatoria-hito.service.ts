import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IConvocatoriaHitoRequest } from './convocatoria-hito-request';
import { CONVOCATORIA_HITO_REQUEST_CONVERTER } from './convocatoria-hito-request.converter';
import { IConvocatoriaHitoResponse } from './convocatoria-hito-response';
import { CONVOCATORIA_HITO_RESPONSE_CONVERTER } from './convocatoria-hito-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaHitoServiceMixinBase:
  CreateCtor<IConvocatoriaHito, IConvocatoriaHito, IConvocatoriaHitoRequest, IConvocatoriaHitoResponse> &
  UpdateCtor<number, IConvocatoriaHito, IConvocatoriaHito, IConvocatoriaHitoRequest, IConvocatoriaHitoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      CONVOCATORIA_HITO_REQUEST_CONVERTER,
      CONVOCATORIA_HITO_RESPONSE_CONVERTER
    ),
    CONVOCATORIA_HITO_REQUEST_CONVERTER,
    CONVOCATORIA_HITO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaHitoService extends _ConvocatoriaHitoServiceMixinBase {
  private static readonly MAPPING = '/convocatoriahitos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaHitoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
