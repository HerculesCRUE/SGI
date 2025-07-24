import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { CalendarioFacturacionSgeIntegration, CardinalidadRelacionSgiSge, FacturasJustificantesColumnasFijasConfigurables, IConfiguracion, ModoEjecucion, SgeEjecucionEconomicaFiltros, SgeIntegracionesEccMenus } from '@core/models/csp/configuracion';
import { environment } from '@env';
import { FindByIdCtor, SgiRestBaseService, mixinFindById } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConfigCsp } from 'src/app/module/adm/config-csp/config-csp.component';
import { IConfigValueResponse } from '../../cnf/config-value-response';
import { CONFIG_VALUE_RESPONSE_CONVERTER } from '../../cnf/config-value-response.converter';
import { TimeZoneConfigService } from '../../timezone.service';
import { IConfiguracionResponse } from './configuracion-response';
import { CONFIGURACION_RESPONSE_CONVERTER } from './configuracion-response.converter';

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
    return this.http.get<IConfiguracionResponse>(`${this.endpointUrl}`).pipe(
      map(response => CONFIGURACION_RESPONSE_CONVERTER.toTarget(response))
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

  isEjecucionEconomicaGruposEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_EJECUCION_ECONOMICA_GRUPOS_ENABLED);
  }

  getCardinalidadRelacionSgiSge(): Observable<CardinalidadRelacionSgiSge> {
    return this.getValueByConfigKey(ConfigCsp.CSP_CARDINALIDAD_RELACION_SGI_SGE);
  }

  isPartidasPresupuestariasSgeEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED);
  }

  isAmortizacionFondosSgeEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_AMORTIZACION_FONDOS_SGE_ENABLED);
  }

  isAltaBuscadorSgeEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_ALTA_BUSCADOR_SGE_ENABLED);
  }

  isGastosJustificadosSgeEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_GASTOS_JUSTIFICADOS_SGE_ENABLED);
  }

  isModificacionProyectoSgeEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_MODIFICACION_PROYECTO_SGE_ENABLED);
  }

  isSectorIvaSgeEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_SECTOR_IVA_SGE_ENABLED);
  }

  isProyectoSgeAltaModoEjecucionAsync(): Observable<boolean> {
    return this.getValueByConfigKey<ModoEjecucion>(ConfigCsp.CSP_PROYECTO_SGE_ALTA_MODO_EJECUCION).pipe(
      map(configValue => configValue === ModoEjecucion.ASINCRONA)
    );
  }

  isProyectoSgeModificacionModoEjecucionAsync(): Observable<boolean> {
    return this.getValueByConfigKey<ModoEjecucion>(ConfigCsp.CSP_PROYECTO_SGE_MODIFICACION_MODO_EJECUCION).pipe(
      map(configValue => configValue === ModoEjecucion.ASINCRONA)
    );
  }

  getCalendarioFacturacionSgeIntegration(): Observable<CalendarioFacturacionSgeIntegration> {
    return this.getValueByConfigKey(ConfigCsp.CSP_CALENDARIO_FACTURACION_SGE_INTEGRATION);
  }

  isSolicitudesSinConvocatoriaInvestigadorEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_SOLICITUDES_SIN_CONVOCATORIA_INVESTIGADOR_ENABLED);
  }

  getFacturasGastosColumnasFijasVisibles(): Observable<FacturasJustificantesColumnasFijasConfigurables[]> {
    return this.getValueByConfigKey(ConfigCsp.CSP_FACTURAS_GASTOS_COLUMNAS_FIJAS_VISIBLES);
  }

  getViajesDietasColumnasFijasVisibles(): Observable<FacturasJustificantesColumnasFijasConfigurables[]> {
    return this.getValueByConfigKey(ConfigCsp.CSP_VIAJES_DIETAS_COLUMNAS_FIJAS_VISIBLES);
  }

  getPersonalContratadoColumnasFijasVisibles(): Observable<FacturasJustificantesColumnasFijasConfigurables[]> {
    return this.getValueByConfigKey<string>(ConfigCsp.CSP_PERSONAL_CONTRATADO_COLUMNAS_FIJAS_VISIBLES).pipe(
      map((configValue) => configValue?.split(',').map(s => {
        return FacturasJustificantesColumnasFijasConfigurables[s];
      }))
    );
  }

  isProyectoSocioPaisFilterEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_PROYECTO_SOCIO_PAIS_FILTER_ENABLED);
  }

  isNotificacionPresupuestosSgeEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_NOTIFICACION_PRESUPUESTOS_SGE_ENABLED);
  }

  isFormatoAnualidadAnio(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_FORMATO_ANUALIDAD_ANIO);
  }

  getIntegracionesEccSgeEnabled(): Observable<SgeIntegracionesEccMenus[]> {
    return this.getValueByConfigKey(ConfigCsp.CSP_INTEGRACIONES_ECC_SGE_ENABLED);
  }

  getSgeEjecucionEconomicaFiltros(): Observable<SgeEjecucionEconomicaFiltros[]> {
    return this.getValueByConfigKey(ConfigCsp.CSP_SGE_EJECUCION_ECONOMICA_FILTROS);
  }

  isSgeEliminarRelacionProyectoEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigCsp.CSP_SGE_ELIMINAR_RELACION_PROYECTO_ENABLED);
  }

  private getValueByConfigKey<T>(configKey: ConfigCsp): Observable<T> {
    return this.findById(configKey).pipe(
      map(configValue => configValue?.value)
    );
  }

  private getValueAsBooleanByConfigKey(configKey: ConfigCsp): Observable<boolean> {
    return this.findById(configKey).pipe(
      map(configValue => configValue?.value && configValue.value === 'true')
    );
  }

}
