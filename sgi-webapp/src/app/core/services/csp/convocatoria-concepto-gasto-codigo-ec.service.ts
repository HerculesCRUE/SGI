import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { IConvocatoriaConceptoGastoCodigoEcResponse } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-response';
import { CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-response.converter';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaConceptoGastoCodigoEcService extends SgiRestBaseService {
  private static readonly MAPPING = '/convocatoriaconceptogastocodigoecs';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaConceptoGastoCodigoEcService.MAPPING}`,
      http
    );
  }

  /**
   * Actualiza el listado de IConvocatoriaConceptoGastoCodigoEc asociados a un IConvocatoriaConceptoGasto
   *
   * @param id Id del IConvocatoriaConceptoGasto
   * @param entities Listado de IConvocatoriaConceptoGastoCodigoEc
   */
  updateList(id: number, entities: IConvocatoriaConceptoGastoCodigoEc[]): Observable<IConvocatoriaConceptoGastoCodigoEc[]> {
    return this.http.patch<IConvocatoriaConceptoGastoCodigoEcResponse[]>(
      `${this.endpointUrl}/${id}`,
      CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map((response => CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

}
