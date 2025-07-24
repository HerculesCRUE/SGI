import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoDocumentoResponse } from './tipo-documento/tipo-documento-response';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from './tipo-documento/tipo-documento-response.converter';

// tslint:disable-next-line: variable-name
const _TipoDocumentoServiceMixinBase:
  CreateCtor<ITipoDocumento, ITipoDocumento, ITipoDocumentoResponse, ITipoDocumentoResponse> &
  UpdateCtor<number, ITipoDocumento, ITipoDocumento, ITipoDocumentoResponse, ITipoDocumentoResponse> &
  FindAllCtor<ITipoDocumento, ITipoDocumentoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TIPO_DOCUMENTO_RESPONSE_CONVERTER,
        TIPO_DOCUMENTO_RESPONSE_CONVERTER
      ),
      TIPO_DOCUMENTO_RESPONSE_CONVERTER,
      TIPO_DOCUMENTO_RESPONSE_CONVERTER
    ),
    TIPO_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoDocumentoService extends _TipoDocumentoServiceMixinBase {
  private static readonly MAPPING = '/tipodocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoDocumentoService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoDocumento>> {
    return this.find<ITipoDocumentoResponse, ITipoDocumento>(`${this.endpointUrl}/todos`, options, TIPO_DOCUMENTO_RESPONSE_CONVERTER);
  }

  /**
   * Desactivar tipo documento
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar tipo documento
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
