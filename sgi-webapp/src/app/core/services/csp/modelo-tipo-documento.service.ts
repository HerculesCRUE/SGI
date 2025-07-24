import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { environment } from '@env';
import { CreateCtor, mixinCreate, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IModeloTipoDocumentoResponse } from './modelo-tipo-documento/modelo-tipo-documento-response';
import { MODELO_TIPO_DOCUMENTO_RESPONSE_CONVERTER } from './modelo-tipo-documento/modelo-tipo-documento-response.converter';


// tslint:disable-next-line: variable-name
const _ModeloTipoDocumentoServiceMixinBase:
  CreateCtor<IModeloTipoDocumento, IModeloTipoDocumento, IModeloTipoDocumentoResponse, IModeloTipoDocumentoResponse> &
  typeof SgiRestBaseService = mixinCreate(
    SgiRestBaseService,
    MODELO_TIPO_DOCUMENTO_RESPONSE_CONVERTER,
    MODELO_TIPO_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoDocumentoService extends _ModeloTipoDocumentoServiceMixinBase {
  private static readonly MAPPING = '/modelotipodocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ModeloTipoDocumentoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
