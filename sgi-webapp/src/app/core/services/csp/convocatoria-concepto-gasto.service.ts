import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-response.converter';
import { IConvocatoriaConceptoGastoCodigoEcResponse } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-response';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConvocatoriaConceptoGastoResponse } from './convocatoria-concepto-gasto/convocatoria-concepto-gasto-response';
import { CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER } from './convocatoria-concepto-gasto/convocatoria-concepto-gasto-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaConceptoGastoServiceMixinBase:
  CreateCtor<IConvocatoriaConceptoGasto, IConvocatoriaConceptoGasto, IConvocatoriaConceptoGastoResponse, IConvocatoriaConceptoGastoResponse> &
  UpdateCtor<number, IConvocatoriaConceptoGasto, IConvocatoriaConceptoGasto, IConvocatoriaConceptoGastoResponse, IConvocatoriaConceptoGastoResponse> &
  FindAllCtor<IConvocatoriaConceptoGasto, IConvocatoriaConceptoGastoResponse> &
  FindByIdCtor<number, IConvocatoriaConceptoGasto, IConvocatoriaConceptoGastoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER,
          CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER
        ),
        CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER,
        CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER
      ),
      CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER),
    CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaConceptoGastoService extends _ConvocatoriaConceptoGastoServiceMixinBase {
  private static readonly MAPPING = '/convocatoriaconceptogastos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaConceptoGastoService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera listado de convocatoria concepto gastos códigos económicos permitidos.
   * @param id convocatoriaConceptoGasto
   * @param options opciones de búsqueda.
   */
  findAllConvocatoriaConceptoGastoCodigoEcs(id: number): Observable<SgiRestListResult<IConvocatoriaConceptoGastoCodigoEc>> {
    const endpointUrl = `${this.endpointUrl}/${id}/convocatoriagastocodigoec`;
    return this.find<IConvocatoriaConceptoGastoCodigoEcResponse, IConvocatoriaConceptoGastoCodigoEc>(
      endpointUrl, undefined, CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER);
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

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
