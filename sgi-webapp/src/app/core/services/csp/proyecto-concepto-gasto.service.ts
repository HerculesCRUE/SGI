import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  RSQLSgiRestFilter,
  SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoConceptoGastoCodigoEcResponse } from './proyecto-concepto-gasto-codigo-ec/proyecto-concepto-gasto-codigo-ec-response';
import { PROYECTO_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER } from './proyecto-concepto-gasto-codigo-ec/proyecto-concepto-gasto-codigo-ec-response.converter';
import { IProyectoConceptoGastoResponse } from './proyecto-concepto-gasto/proyecto-concepto-gasto-response';
import { PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER } from './proyecto-concepto-gasto/proyecto-concepto-gasto-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoConceptoGastoServiceMixinBase:
  CreateCtor<IProyectoConceptoGasto, IProyectoConceptoGasto, IProyectoConceptoGastoResponse, IProyectoConceptoGastoResponse> &
  UpdateCtor<number, IProyectoConceptoGasto, IProyectoConceptoGasto, IProyectoConceptoGastoResponse, IProyectoConceptoGastoResponse> &
  FindAllCtor<IProyectoConceptoGasto, IProyectoConceptoGastoResponse> &
  FindByIdCtor<number, IProyectoConceptoGasto, IProyectoConceptoGastoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER,
          PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER
        ),
        PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER,
        PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER
      ),
      PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER),
    PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoConceptoGastoService extends _ProyectoConceptoGastoServiceMixinBase {
  private static readonly MAPPING = '/proyectoconceptosgasto';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoConceptoGastoService.MAPPING}`,
      http
    );
  }

  findByConceptoGastoId(conceptoGastoId: number, proyectoId: number, permitido: boolean): Observable<SgiRestListResult<IProyectoConceptoGasto>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('conceptoGasto.id', SgiRestFilterOperator.EQUALS, conceptoGastoId.toString())
        .and('proyectoId', SgiRestFilterOperator.EQUALS, proyectoId.toString())
        .and('permitido', SgiRestFilterOperator.EQUALS, permitido.toString()),
    };

    return this.find<IProyectoConceptoGastoResponse, IProyectoConceptoGasto>(
      this.endpointUrl,
      options,
      PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER
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
    return this.find<IProyectoConceptoGastoCodigoEcResponse, IProyectoConceptoGastoCodigoEc>(
      endpointUrl, undefined, PROYECTO_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER);
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

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
