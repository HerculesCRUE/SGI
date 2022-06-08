import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfiguracionBaremo } from '@core/models/prc/configuracion-baremo';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { IConfiguracionBaremoResponse } from './configuracion-baremo-response';
import { CONFIGURACION_BAREMO_RESPONSE_CONVERTER } from './configuracion-baremo-response.converter';

// tslint:disable-next-line: variable-name
const _ConfiguracionBaremoMixinBase:
  FindAllCtor<IConfiguracionBaremo, IConfiguracionBaremoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    CONFIGURACION_BAREMO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionBaremoService extends _ConfiguracionBaremoMixinBase {

  private static readonly MAPPING = '/configuracionesbaremos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ConfiguracionBaremoService.MAPPING}`,
      http,
    );
  }
}
