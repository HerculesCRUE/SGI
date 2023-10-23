import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { FindByIdCtor, SgiRestBaseService, mixinFindById } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { TimeZoneConfigService } from '../timezone.service';
import { IConfigValue } from '@core/models/cnf/config-value';
import { IConfigValueResponse } from '../cnf/config-value-response';
import { map } from 'rxjs/operators';
import { CONFIG_VALUE_RESPONSE_CONVERTER } from '../cnf/config-value-response.converter';
import { IConfiguracion } from '@core/models/csp/configuracion';


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
export class ConfigService extends _ConfigServiceMixinBase implements TimeZoneConfigService {
  private static readonly MAPPING = '/config';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConfigService.MAPPING}`,
      http
    );
  }

  getTimeZone(): Observable<string> {
    return this.http.get(`${this.endpointUrl}/time-zone`, { responseType: 'text' });
  }

  /**
   * Devuelve la configuración completa
   */
  getConfiguracion(): Observable<IConfiguracion> {
    return this.http.get<IConfiguracion>(`${this.endpointUrl}`);
  }

  updateValue(key: string, value: string): Observable<IConfigValue> {
    return this.http.patch<IConfigValueResponse>(
      `${this.endpointUrl}/${key}`,
      value
    ).pipe(
      map((response => CONFIG_VALUE_RESPONSE_CONVERTER.toTarget(response)))
    );
  }

}
