import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IInvencionDocumentoRequest } from './invencion-documento-request';
import { INVENCION_DOCUMENTO_REQUEST_CONVERTER } from './invencion-documento-request.converter';
import { IInvencionDocumentoResponse } from './invencion-documento-response';
import { INVENCION_DOCUMENTO_RESPONSE_CONVERTER } from './invencion-documento-response.converter';

// tslint:disable-next-line: variable-name
const _InvencionDocumentoServiceMixinBase:
  CreateCtor<IInvencionDocumento, IInvencionDocumento, IInvencionDocumentoRequest, IInvencionDocumentoResponse> &
  UpdateCtor<number, IInvencionDocumento, IInvencionDocumento, IInvencionDocumentoRequest, IInvencionDocumentoResponse> &
  FindByIdCtor<number, IInvencionDocumento, IInvencionDocumentoResponse> &
  FindAllCtor<IInvencionDocumento, IInvencionDocumentoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          INVENCION_DOCUMENTO_REQUEST_CONVERTER,
          INVENCION_DOCUMENTO_RESPONSE_CONVERTER
        ),
        INVENCION_DOCUMENTO_REQUEST_CONVERTER,
        INVENCION_DOCUMENTO_RESPONSE_CONVERTER
      ),
      INVENCION_DOCUMENTO_RESPONSE_CONVERTER
    ),
    INVENCION_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InvencionDocumentoService extends _InvencionDocumentoServiceMixinBase {

  private static readonly MAPPING = '/invenciondocumentos';

  constructor(protected http: HttpClient) {
    super(`${environment.serviceServers.pii}${InvencionDocumentoService.MAPPING}`,
      http);
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
