import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresaDocumento } from '@core/models/eer/empresa-documento';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor,
  mixinCreate, mixinFindById,
  mixinUpdate, SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IEmpresaDocumentoRequest } from './empresa-documento-request';
import { EMPRESA_DOCUMENTO_REQUEST_CONVERTER } from './empresa-documento-request.converter';
import { IEmpresaDocumentoResponse } from './empresa-documento-response';
import { EMPRESA_DOCUMENTO_RESPONSE_CONVERTER } from './empresa-documento-response.converter';

// tslint:disable-next-line: variable-name
const _EmpresaDocumentoMixinBase:
  CreateCtor<IEmpresaDocumento, IEmpresaDocumento, IEmpresaDocumentoRequest,
    IEmpresaDocumentoResponse> &
  UpdateCtor<number, IEmpresaDocumento, IEmpresaDocumento, IEmpresaDocumentoRequest,
    IEmpresaDocumentoResponse> &
  FindByIdCtor<number, IEmpresaDocumento, IEmpresaDocumentoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        EMPRESA_DOCUMENTO_REQUEST_CONVERTER,
        EMPRESA_DOCUMENTO_RESPONSE_CONVERTER
      ),
      EMPRESA_DOCUMENTO_REQUEST_CONVERTER,
      EMPRESA_DOCUMENTO_RESPONSE_CONVERTER
    ),
    EMPRESA_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EmpresaDocumentoService extends _EmpresaDocumentoMixinBase {
  private static readonly MAPPING = '/empresasdocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${EmpresaDocumentoService.MAPPING}`,
      http,
    );
  }

  /**
   * Elimina el EmpresaDocumento con el id indicado.
   *
   * @param id Identificador del EmpresaDocumento.
   */
  delete(id: number): Observable<void> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.delete<void>(url);
  }
}
