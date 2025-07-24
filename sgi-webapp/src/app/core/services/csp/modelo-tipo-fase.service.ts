import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IModeloTipoFaseResponse } from './modelo-tipo-fase/modelo-tipo-fase-response';
import { MODELO_TIPO_FASE_RESPONSE_CONVERTER } from './modelo-tipo-fase/modelo-tipo-fase-response.converter';

// tslint:disable-next-line: variable-name
const _ModeloTipoFaseServiceMixinBase:
  CreateCtor<IModeloTipoFase, IModeloTipoFase, IModeloTipoFaseResponse, IModeloTipoFaseResponse> &
  UpdateCtor<number, IModeloTipoFase, IModeloTipoFase, IModeloTipoFaseResponse, IModeloTipoFaseResponse> &
  typeof SgiRestBaseService = mixinCreate(
    mixinUpdate(
      SgiRestBaseService,
      MODELO_TIPO_FASE_RESPONSE_CONVERTER,
      MODELO_TIPO_FASE_RESPONSE_CONVERTER
    ),
    MODELO_TIPO_FASE_RESPONSE_CONVERTER,
    MODELO_TIPO_FASE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoFaseService extends _ModeloTipoFaseServiceMixinBase {
  private static readonly MAPPING = '/modelotipofases';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ModeloTipoFaseService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
