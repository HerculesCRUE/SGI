import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_CONVERTER } from '@core/converters/csp/convocatoria-concepto-gasto-codigo-ec.converter';
import { CONVOCATORIA_CONCEPTO_GASTO_CONVERTER } from '@core/converters/csp/convocatoria-concepto-gasto.converter';
import { IConvocatoriaConceptoGastoBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-backend';
import { IConvocatoriaConceptoGastoCodigoEcBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-codigo-ec-backend';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

// tslint:disable-next-line: variable-name
const _ConvocatoriaConceptoGastoMixinBase:
  FindByIdCtor<number, IConvocatoriaConceptoGasto, IConvocatoriaConceptoGastoBackend> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONVOCATORIA_CONCEPTO_GASTO_CONVERTER
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
    return this.find<IConvocatoriaConceptoGastoCodigoEcBackend, IConvocatoriaConceptoGastoCodigoEc>(
      endpointUrl, undefined, CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_CONVERTER);
  }

}
