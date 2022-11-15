import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_CONCEPTO_GASTO_CODIGO_EC_CONVERTER } from '@core/converters/csp/proyecto-concepto-gasto-codigo-ec.converter';
import { PROYECTO_CONCEPTO_GASTO_CONVERTER } from '@core/converters/csp/proyecto-concepto-gasto.converter';
import { IProyectoConceptoGastoBackend } from '@core/models/csp/backend/proyecto-concepto-gasto-backend';
import { IProyectoConceptoGastoCodigoEcBackend } from '@core/models/csp/backend/proyecto-concepto-gasto-codigo-ec-backend';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { environment } from '@env';
import {
  RSQLSgiRestFilter, SgiMutableRestService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoConceptoGastoService
  extends SgiMutableRestService<number, IProyectoConceptoGastoBackend, IProyectoConceptoGasto> {
  private static readonly MAPPING = '/proyectoconceptosgasto';

  constructor(protected http: HttpClient) {
    super(
      ProyectoConceptoGastoService.name,
      `${environment.serviceServers.csp}${ProyectoConceptoGastoService.MAPPING}`,
      http,
      PROYECTO_CONCEPTO_GASTO_CONVERTER
    );
  }

  findByConceptoGastoId(conceptoGastoId: number, permitido: boolean): Observable<SgiRestListResult<IProyectoConceptoGasto>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('conceptoGasto.id', SgiRestFilterOperator.EQUALS, conceptoGastoId.toString())
        .and('permitido', SgiRestFilterOperator.EQUALS, permitido.toString()),
    };

    return this.find<IProyectoConceptoGastoBackend, IProyectoConceptoGasto>(
      this.endpointUrl,
      options,
      PROYECTO_CONCEPTO_GASTO_CONVERTER
    );
  }

  /**
   * Recupera listado de códigos económicos permitidos.
   *
   * @param id Id del IProyectoConceptoGasto
   * @param options opciones de búsqueda.
   */
  findAllProyectoConceptoGastoCodigosEc(id: number): Observable<SgiRestListResult<IProyectoConceptoGastoCodigoEc>> {
    const endpointUrl = `${this.endpointUrl}/${id}/proyectoconceptogastocodigosec`;
    return this.find<IProyectoConceptoGastoCodigoEcBackend, IProyectoConceptoGastoCodigoEc>(
      endpointUrl, undefined, PROYECTO_CONCEPTO_GASTO_CODIGO_EC_CONVERTER);
  }

  /**
   * Comprueba si existe códigos económicos asociados al concepto de gasto
   *
   * @param id Id del IProyectoConceptoGasto
   * @return true/false
   */
  hasCodigosEconomicos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/proyectoconceptogastocodigosec`;
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

  /**
   * Comprueba si existen diferencias entre los codigos economicos del ProyectoConceptoGasto y el ConvocatoriaConceptoGasto relacionado.
   *
   * @param id Identificador de la entidad
   */
  hasDifferencesCodigosEcConvocatoria(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/proyectoconceptogastocodigosec/differences`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
