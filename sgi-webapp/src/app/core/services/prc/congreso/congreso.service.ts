import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICongreso } from '@core/models/prc/congreso';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ICongresoResponse } from './congreso-response';
import { CONGRESO_RESPONSE_CONVERTER } from './congreso-response.converter';

// tslint:disable-next-line: variable-name
const _CongresoMixinBase:
  FindAllCtor<ICongreso, ICongresoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    CONGRESO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class CongresoService extends _CongresoMixinBase {

  private static readonly MAPPING = '/producciones-cientificas/congresos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${CongresoService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra los congresos a los que pertenece el investigador actual
   *
   * @param options opciones de búsqueda.
   */
  findCongresosInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<ICongreso>> {
    return this.find<ICongresoResponse, ICongreso>(
      `${this.endpointUrl}/investigador`,
      options,
      CONGRESO_RESPONSE_CONVERTER
    );
  }
}
