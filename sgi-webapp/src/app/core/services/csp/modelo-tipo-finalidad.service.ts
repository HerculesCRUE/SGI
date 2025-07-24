import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { environment } from '@env';
import { CreateCtor, mixinCreate, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IModeloTipoFinalidadResponse } from './modelo-tipo-finalidad/modelo-tipo-finalidad-response';
import { MODELO_TIPO_FINALIDAD_RESPONSE_CONVERTER } from './modelo-tipo-finalidad/modelo-tipo-finalidad-response.converter';

// tslint:disable-next-line: variable-name
const _ModeloTipoFinalidadServiceMixinBase:
  CreateCtor<IModeloTipoFinalidad, IModeloTipoFinalidad, IModeloTipoFinalidadResponse, IModeloTipoFinalidadResponse> &
  typeof SgiRestBaseService = mixinCreate(
    SgiRestBaseService,
    MODELO_TIPO_FINALIDAD_RESPONSE_CONVERTER,
    MODELO_TIPO_FINALIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoFinalidadService extends _ModeloTipoFinalidadServiceMixinBase {
  private static readonly MAPPING = '/modelotipofinalidades';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ModeloTipoFinalidadService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
