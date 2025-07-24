import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { IConvocatoriaConceptoGastoCodigoEcResponse } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-response';
import { CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-response.converter';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IConvocatoriaConceptoGastoResponse } from './convocatoria-concepto-gasto/convocatoria-concepto-gasto-response';
import { CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER } from './convocatoria-concepto-gasto/convocatoria-concepto-gasto-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaConceptoGastoMixinBase:
  FindByIdCtor<number, IConvocatoriaConceptoGasto, IConvocatoriaConceptoGastoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaConceptoGastoPublicService extends _ConvocatoriaConceptoGastoMixinBase {
  private static readonly MAPPING = '/convocatoriaconceptogastos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaConceptoGastoPublicService.PUBLIC_PREFIX}${ConvocatoriaConceptoGastoPublicService.MAPPING}`,
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

}
