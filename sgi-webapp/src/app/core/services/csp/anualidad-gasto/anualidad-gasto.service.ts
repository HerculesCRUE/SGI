import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IAnualidadGastoResponse } from './anualidad-gasto-response';
import { ANUALIDAD_GASTO_RESPONSE_CONVERTER } from './anualidad-gasto-response.converter';

// tslint:disable-next-line: variable-name
const _AnualidadGastoServiceMixinBase:
  FindAllCtor<IAnualidadGasto, IAnualidadGastoResponse> &
  typeof SgiRestBaseService = mixinFindAll(SgiRestBaseService, ANUALIDAD_GASTO_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class AnualidadGastoService extends _AnualidadGastoServiceMixinBase {
  private static readonly MAPPING = '/anualidadgasto';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${AnualidadGastoService.MAPPING}`,
      http,
    );
  }

  /**
   * Actualiza el listado de IDConvocatoriaConceptoGasto asociados a un IConvocatoriaConceptoGastoCodigoEc
   *
   * @param id Id del IConvocatoriaConceptoGasto
   * @param entities Listado de IConvocatoriaConceptoGastoCodigoEc
   */
  updateList(id: number, entities: IAnualidadGasto[]): Observable<IAnualidadGasto[]> {
    return this.http.patch<IAnualidadGastoResponse[]>(
      `${this.endpointUrl}/${id}`,
      ANUALIDAD_GASTO_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map((response => ANUALIDAD_GASTO_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

}
