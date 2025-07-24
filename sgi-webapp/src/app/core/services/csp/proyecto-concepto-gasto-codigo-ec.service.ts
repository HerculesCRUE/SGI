import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoConceptoGastoCodigoEcResponse } from './proyecto-concepto-gasto-codigo-ec/proyecto-concepto-gasto-codigo-ec-response';
import { PROYECTO_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER } from './proyecto-concepto-gasto-codigo-ec/proyecto-concepto-gasto-codigo-ec-response.converter';

const _ProyectoConceptoGastoCodigoEcServiceMixinBase:
  FindAllCtor<IProyectoConceptoGastoCodigoEc, IProyectoConceptoGastoCodigoEcResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    PROYECTO_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoConceptoGastoCodigoEcService extends _ProyectoConceptoGastoCodigoEcServiceMixinBase {
  private static readonly MAPPING = '/proyectoconceptogastocodigosec';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoConceptoGastoCodigoEcService.MAPPING}`,
      http
    );
  }

  /**
   * Actualiza el listado de IProyectoConceptoGastoCodigoEc asociados a un IProyectoConceptoGasto
   *
   * @param proyectoConceptoGastoId Id del IProyectoConceptoGasto
   * @param entities Listado de IConvocatoriaConceptoGastoCodigoEc
   */
  updateList(proyectoConceptoGastoId: number, entities: IProyectoConceptoGastoCodigoEc[]):
    Observable<IProyectoConceptoGastoCodigoEc[]> {
    return this.http.patch<IProyectoConceptoGastoCodigoEcResponse[]>(
      `${this.endpointUrl}/${proyectoConceptoGastoId}`, PROYECTO_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(map((response => PROYECTO_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

}
