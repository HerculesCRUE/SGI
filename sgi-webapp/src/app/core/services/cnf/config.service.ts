import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { IRecipient } from '@core/models/com/recipient';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TimeZoneConfigService } from '../timezone.service';
import { IConfigValueRequest } from './config-value-request';
import { CONFIG_VALUE_REQUEST_CONVERTER } from './config-value-request.converter';
import { IConfigValueResponse } from './config-value-response';
import { CONFIG_VALUE_RESPONSE_CONVERTER } from './config-value-response.converter';
import { ConfigGlobal } from 'src/app/module/adm/config-global/config-global.component';

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

  updateValue(key: string, value: string): Observable<IConfigValue> {
    return this.http.patch<IConfigValueResponse>(
      `${this.endpointUrl}/${key}`,
      value
    ).pipe(
      map((response => CONFIG_VALUE_RESPONSE_CONVERTER.toTarget(response)))
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

  isBlockchainEnable(): Observable<boolean> {
    return this.findById('eti-blockchain-enable').pipe(
      map(value => {
        if (value.value === 'true') {
          return true;
        } else {
          return false;
        }
      })
    );
  }

  getNombreSistemaGestionExterno(): Observable<string> {
    return this.findById('nombre-sistema-gestion-externo').pipe(map(value => value?.value ?? null));
  }

  getUrlSistemaGestionExterno(): Observable<string> {
    return this.findById('url-sistema-gestion-externo').pipe(map(value => value?.value ?? null));
  }

  getLimiteRegistrosExportacionExcel(key?: string): Observable<string> {
    if (key) {
      return this.findById(key).pipe(
        map(value => {
          return value?.value ?? null;
        }
        ),
        switchMap(limit => {
          if (!limit) {
            return this.findById('exp-max-num-registros-excel').pipe(
              map(value => {
                return value?.value ?? null;
              })
            );
          } else {
            return of(limit);
          }
        }),
        catchError(error => {
          return this.findById('exp-max-num-registros-excel').pipe(map(value => value?.value ?? null));
        })
      );
    } else {
      return this.findById('exp-max-num-registros-excel').pipe(map(value => value?.value ?? null));
    }
  }

  isAltaSgpEnabled(): Observable<boolean> {
    return this.findById(ConfigGlobal.SGP_ALTA).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isModificacionSgpEnabled(): Observable<boolean> {
    return this.findById(ConfigGlobal.SGP_MODIFICACION).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isAltaSgempEnabled(): Observable<boolean> {
    return this.findById(ConfigGlobal.SGEMP_ALTA).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isModificacionSgempEnabled(): Observable<boolean> {
    return this.findById(ConfigGlobal.SGEMP_MODIFICACION).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

}
