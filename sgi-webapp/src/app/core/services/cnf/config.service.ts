import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { IRecipient } from '@core/models/com/recipient';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TimeZoneConfigService } from '../timezone.service';
import { IConfigValueRequest } from './config-value-request';
import { CONFIG_VALUE_REQUEST_CONVERTER } from './config-value-request.converter';
import { IConfigValueResponse } from './config-value-response';
import { CONFIG_VALUE_RESPONSE_CONVERTER } from './config-value-response.converter';

// tslint:disable-next-line: variable-name
const _ConfigServiceMixinBase:
  CreateCtor<IConfigValue, IConfigValue, IConfigValueRequest, IConfigValueResponse> &
  UpdateCtor<string, IConfigValue, IConfigValue, IConfigValueRequest, IConfigValueResponse> &
  FindByIdCtor<string, IConfigValue, IConfigValueResponse> &
  FindAllCtor<IConfigValue, IConfigValueResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          CONFIG_VALUE_REQUEST_CONVERTER,
          CONFIG_VALUE_RESPONSE_CONVERTER
        ),
        CONFIG_VALUE_REQUEST_CONVERTER,
        CONFIG_VALUE_RESPONSE_CONVERTER
      ),
      CONFIG_VALUE_RESPONSE_CONVERTER
    ),
    CONFIG_VALUE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConfigService extends _ConfigServiceMixinBase implements TimeZoneConfigService {
  private static readonly MAPPING = '/config';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.cnf}${ConfigService.MAPPING}`,
      http
    );
  }

  getTimeZone(): Observable<string> {
    return this.http.get(`${this.endpointUrl}/time-zone`, { responseType: 'text' });
  }

  getEmailRecipients(key: string): Observable<IRecipient[]> {
    return this.findById(key).pipe(
      map(value => {
        if (value && !!value.value) {
          const mails: string[] = JSON.parse(value.value);
          return mails.map(email => {
            return { name: email, address: email } as IRecipient;
          });
        }
        else {
          return [];
        }
      })
    );
  }
}
