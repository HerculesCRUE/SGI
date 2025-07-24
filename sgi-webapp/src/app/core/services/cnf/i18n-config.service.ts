import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { II18nConfigValue } from '@core/models/cnf/i18n-config-value';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TimeZoneConfigService } from '../timezone.service';
import { IConfigValueResponse } from './config-value-response';
import { I18N_CONFIG_VALUE_REQUEST_CONVERTER } from './i18n-config-value-request.converter';
import { I18N_CONFIG_VALUE_RESPONSE_CONVERTER } from './i18n-config-value-response.converter';

// tslint:disable-next-line: variable-name
const _ConfigServiceMixinBase:
  FindByIdCtor<string, II18nConfigValue, IConfigValueResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    SgiRestBaseService,
    I18N_CONFIG_VALUE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class I18nConfigService extends _ConfigServiceMixinBase implements TimeZoneConfigService {
  private static readonly MAPPING = '/config';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.cnf}${I18nConfigService.MAPPING}`,
      http
    );
  }

  updateValue(key: string, value: II18nConfigValue): Observable<II18nConfigValue> {
    return this.http.patch<IConfigValueResponse>(
      `${this.endpointUrl}/${key}`,
      I18N_CONFIG_VALUE_REQUEST_CONVERTER.fromTarget(value).value
    ).pipe(
      map((response => I18N_CONFIG_VALUE_RESPONSE_CONVERTER.toTarget(response)))
    );
  }

  getTimeZone(): Observable<string> {
    return this.http.get(`${this.endpointUrl}/time-zone`, { responseType: 'text' });
  }
}
