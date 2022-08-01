import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoDocumento } from '@core/models/eer/tipo-documento';
import { environment } from '@env';
import {
  FindAllCtor, mixinFindAll, SgiRestBaseService,
  SgiRestFindOptions, SgiRestListResult
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoDocumentoResponse } from './tipo-documento-response';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from './tipo-documento-response.converter';

// tslint:disable-next-line: variable-name
const _TipoDocumentoMixinBase:
  FindAllCtor<ITipoDocumento, ITipoDocumentoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    TIPO_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoDocumentoService extends _TipoDocumentoMixinBase {
  private static readonly MAPPING = '/tiposdocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${TipoDocumentoService.MAPPING}`,
      http,
    );
  }

  /**
   * Busca los Subtipos pertenecientes al TipoDocumento pasado por parámetro.
   *
   * @param id del TipoDocumento padre.
   * @returns lista de TipoDocumento.
   */
  findSubtipos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoDocumento>> {
    return this.find<ITipoDocumentoResponse, ITipoDocumento>(
      `${this.endpointUrl}/${id}/subtipos`,
      options,
      TIPO_DOCUMENTO_RESPONSE_CONVERTER
    );
  }

}
