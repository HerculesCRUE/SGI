import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEstadoValidacionIP } from '@core/models/csp/estado-validacion-ip';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { ESTADO_VALIDACION_IP_RESPONSE_CONVERTER } from './estado-validacion-ip-response.converter';
import { IEstadoValidacionIPResponse } from './estado-validacion-ip.response';

// tslint:disable-next-line: variable-name
const _EstadoValidacionIPServiceMixinBase:
  FindByIdCtor<number, IEstadoValidacionIP, IEstadoValidacionIPResponse> &
  FindAllCtor<IEstadoValidacionIP, IEstadoValidacionIPResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      ESTADO_VALIDACION_IP_RESPONSE_CONVERTER
    ),
    ESTADO_VALIDACION_IP_RESPONSE_CONVERTER);

@Injectable({ providedIn: 'root' })
export class EstadoValidacionIPService extends _EstadoValidacionIPServiceMixinBase {

  private static readonly MAPPING = '/estadosvalidacionip';

  constructor(http: HttpClient) {
    super(`${environment.serviceServers.csp}${EstadoValidacionIPService.MAPPING}`, http);
  }

}
