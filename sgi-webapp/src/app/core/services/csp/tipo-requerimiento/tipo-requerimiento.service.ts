import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoRequerimiento } from '@core/models/csp/tipo-requerimiento';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { ITipoRequerimientoResponse } from './tipo-requerimiento-response';
import { TIPO_REQUERIMIENTO_RESPONSE_CONVERTER } from './tipo-requerimiento-response.converter';

// tslint:disable-next-line: variable-name
const _TipoRequerimientoMixinBase:
  FindAllCtor<ITipoRequerimiento, ITipoRequerimientoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    TIPO_REQUERIMIENTO_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class TipoRequerimientoService extends _TipoRequerimientoMixinBase {

  private static readonly MAPPING = '/tiposrequerimientos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoRequerimientoService.MAPPING}`,
      http
    );
  }
}
