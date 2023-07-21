import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConfigValueResponse } from './config-value-response';
import { CONFIG_VALUE_RESPONSE_CONVERTER } from './config-value-response.converter';

// tslint:disable-next-line: variable-name
const _ConfigServiceMixinBase:
  FindByIdCtor<string, IConfigValue, IConfigValueResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONFIG_VALUE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConfigPublicService extends _ConfigServiceMixinBase {
  private static readonly MAPPING = '/config';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.cnf}${ConfigPublicService.PUBLIC_PREFIX}${ConfigPublicService.MAPPING}`,
      http
    );
  }

  getNumeroLogosCabecera(): Observable<string> {
    return this.findById('web-numero-logos-header').pipe(map(value => value?.value ?? null));
  }

}
