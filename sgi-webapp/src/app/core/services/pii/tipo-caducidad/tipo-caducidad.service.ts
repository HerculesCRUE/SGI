import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { ITipoCaducidadResponse } from './tipo-caducidad-response';
import { TIPO_CADUCIDAD_RESPONSE_CONVERTER } from './tipo-caducidad-response.converter';

// tslint:disable-next-line: variable-name
const _TipoCaducidadServiceMixinBase:
  FindAllCtor<ITipoCaducidad, ITipoCaducidadResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    TIPO_CADUCIDAD_RESPONSE_CONVERTER
  );
@Injectable({
  providedIn: 'root'
})
export class TipoCaducidadService extends _TipoCaducidadServiceMixinBase {
  private static readonly MAPPING = '/tiposcaducidad';

  constructor(protected http: HttpClient) {
    super(`${environment.serviceServers.pii}${TipoCaducidadService.MAPPING}`,
      http);
  }
}
