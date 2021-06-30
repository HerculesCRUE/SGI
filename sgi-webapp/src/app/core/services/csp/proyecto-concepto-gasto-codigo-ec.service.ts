import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_CONCEPTO_GASTO_CODIGO_EC_CONVERTER } from '@core/converters/csp/proyecto-concepto-gasto-codigo-ec.converter';
import { IProyectoConceptoGastoCodigoEcBackend } from '@core/models/csp/backend/proyecto-concepto-gasto-codigo-ec-backend';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { environment } from '@env';
import { SgiReadOnlyMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoConceptoGastoCodigoEcService extends
  SgiReadOnlyMutableRestService<number, IProyectoConceptoGastoCodigoEcBackend, IProyectoConceptoGastoCodigoEc> {
  private static readonly MAPPING = '/proyectoconceptogastocodigosec';

  constructor(protected http: HttpClient) {
    super(
      ProyectoConceptoGastoCodigoEcService.name,
      `${environment.serviceServers.csp}${ProyectoConceptoGastoCodigoEcService.MAPPING}`,
      http,
      PROYECTO_CONCEPTO_GASTO_CODIGO_EC_CONVERTER
    );
  }

  /**
   * Actualiza el listado de IProyectoConceptoGastoCodigoEc asociados a un IProyectoConceptoGasto
   *
   * @param convocatoriaConceptoGastoId Id del IConvocatoriaConceptoGasto
   * @param entities Listado de IConvocatoriaConceptoGastoCodigoEc
   */
  updateList(convocatoriaConceptoGastoId: number, entities: IProyectoConceptoGastoCodigoEc[]):
    Observable<IProyectoConceptoGastoCodigoEc[]> {
    return this.http.patch<IProyectoConceptoGastoCodigoEcBackend[]>(
      `${this.endpointUrl}/${convocatoriaConceptoGastoId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map((response => this.converter.toTargetArray(response)))
    );
  }

}
