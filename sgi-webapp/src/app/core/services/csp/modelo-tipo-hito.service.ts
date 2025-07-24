import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IModeloTipoHitoResponse } from './modelo-tipo-hito/modelo-tipo-hito-response';
import { MODELO_TIPO_HITO_RESPONSE_CONVERTER } from './modelo-tipo-hito/modelo-tipo-hito-response.converter';

// tslint:disable-next-line: variable-name
const _ModeloTipoHitoServiceMixinBase:
  CreateCtor<IModeloTipoHito, IModeloTipoHito, IModeloTipoHitoResponse, IModeloTipoHitoResponse> &
  UpdateCtor<number, IModeloTipoHito, IModeloTipoHito, IModeloTipoHitoResponse, IModeloTipoHitoResponse> &
  typeof SgiRestBaseService = mixinCreate(
    mixinUpdate(
      SgiRestBaseService,
      MODELO_TIPO_HITO_RESPONSE_CONVERTER,
      MODELO_TIPO_HITO_RESPONSE_CONVERTER
    ),
    MODELO_TIPO_HITO_RESPONSE_CONVERTER,
    MODELO_TIPO_HITO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoHitoService extends _ModeloTipoHitoServiceMixinBase {
  private static readonly MAPPING = '/modelotipohitos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ModeloTipoHitoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}