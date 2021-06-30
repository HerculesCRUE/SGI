import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_CONVERTER } from '@core/converters/csp/convocatoria-concepto-gasto-codigo-ec.converter';
import { CONVOCATORIA_CONCEPTO_GASTO_CONVERTER } from '@core/converters/csp/convocatoria-concepto-gasto.converter';
import { IConvocatoriaConceptoGastoBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-backend';
import { IConvocatoriaConceptoGastoCodigoEcBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-codigo-ec-backend';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaConceptoGastoService
  extends SgiMutableRestService<number, IConvocatoriaConceptoGastoBackend, IConvocatoriaConceptoGasto> {
  private static readonly MAPPING = '/convocatoriaconceptogastos';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaConceptoGastoService.name,
      `${environment.serviceServers.csp}${ConvocatoriaConceptoGastoService.MAPPING}`,
      http,
      CONVOCATORIA_CONCEPTO_GASTO_CONVERTER
    );
  }

  /**
   * Recupera listado de convocatoria concepto gastos códigos económicos permitidos.
   * @param id convocatoriaConceptoGasto
   * @param options opciones de búsqueda.
   */
  findAllConvocatoriaConceptoGastoCodigoEcs(id: number): Observable<SgiRestListResult<IConvocatoriaConceptoGastoCodigoEc>> {
    const endpointUrl = `${this.endpointUrl}/${id}/convocatoriagastocodigoec`;
    return this.find<IConvocatoriaConceptoGastoCodigoEcBackend, IConvocatoriaConceptoGastoCodigoEc>(
      endpointUrl, undefined, CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_CONVERTER);
  }

  /**
   * Comprueba si existe códigos económicos asociados a la convocatoria concepto de gasto
   *
   * @param id Id de la convocatoria concepto de gasto
   * @retrurn true/false
   */
  existsCodigosEconomicos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/convocatoriagastocodigoec`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si existe la entidad con el identificador facilitadao
   *
   * @param id Identificador de la entidad
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
