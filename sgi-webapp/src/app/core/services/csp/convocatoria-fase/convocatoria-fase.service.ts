import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IConvocatoriaFaseRequest } from './convocatoria-fase-request';
import { CONVOCATORIA_FASE_REQUEST_CONVERTER } from './convocatoria-fase-request.converter';
import { IConvocatoriaFaseResponse } from './convocatoria-fase-response';
import { CONVOCATORIA_FASE_RESPONSE_CONVERTER } from './convocatoria-fase-response.converter';

const _ConvocatoriaFasesServiceMixinBase:
  CreateCtor<IConvocatoriaFase, IConvocatoriaFase, IConvocatoriaFaseRequest, IConvocatoriaFaseResponse> &
  UpdateCtor<number, IConvocatoriaFase, IConvocatoriaFase, IConvocatoriaFaseRequest, IConvocatoriaFaseResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      CONVOCATORIA_FASE_REQUEST_CONVERTER,
      CONVOCATORIA_FASE_RESPONSE_CONVERTER
    ),
    CONVOCATORIA_FASE_REQUEST_CONVERTER,
    CONVOCATORIA_FASE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaFaseService extends _ConvocatoriaFasesServiceMixinBase {
  private static readonly MAPPING = '/convocatoriafases';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaFaseService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
