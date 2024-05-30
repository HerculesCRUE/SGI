import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { CardinalidadRelacionSgiSge, IConfiguracion, ModoEjecucion } from '@core/models/csp/configuracion';
import { environment } from '@env';
import { FindByIdCtor, SgiRestBaseService, mixinFindById } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConfigCsp } from 'src/app/module/adm/config-csp/config-csp.component';
import { IConfigValueResponse } from '../cnf/config-value-response';
import { CONFIG_VALUE_RESPONSE_CONVERTER } from '../cnf/config-value-response.converter';
import { TimeZoneConfigService } from '../timezone.service';

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

  isEjecucionEconomicaGruposEnabled(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_EJECUCION_ECONOMICA_GRUPOS_ENABLED).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  getCardinalidadRelacionSgiSge(): Observable<CardinalidadRelacionSgiSge> {
    return this.findById(ConfigCsp.CSP_CARDINALIDAD_RELACION_SGI_SGE).pipe(
      map(configValue => configValue?.value)
    );
  }

  isPartidasPresupuestariasSgeEnabled(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isAmortizacionFondosSgeEnabled(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_AMORTIZACION_FONDOS_SGE_ENABLED).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isAltaBuscadorSgeEnabled(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_ALTA_BUSCADOR_SGE_ENABLED).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isGastosJustificadosSgeEnabled(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_GASTOS_JUSTIFICADOS_SGE_ENABLED).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isModificacionProyectoSgeEnabled(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_MODIFICACION_PROYECTO_SGE_ENABLED).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isSectorIvaSgeEnabled(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_SECTOR_IVA_SGE_ENABLED).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

  isProyectoSgeAltaModoEjecucionAsync(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_PROYECTO_SGE_ALTA_MODO_EJECUCION).pipe(
      map(configValue => configValue?.value && configValue.value === ModoEjecucion.ASINCRONA)
    );
  }

  isProyectoSgeModificacionModoEjecucionAsync(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_PROYECTO_SGE_MODIFICACION_MODO_EJECUCION).pipe(
      map(configValue => configValue?.value && configValue.value === ModoEjecucion.ASINCRONA)
    );
  }

  isCalendarioFacturacionSgeEnabled(): Observable<boolean> {
    return this.findById(ConfigCsp.CSP_CALENDARIO_FACTURACION_SGE_ENABLED).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

}
