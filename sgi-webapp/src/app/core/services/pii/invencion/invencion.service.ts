import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInvencion } from '@core/models/pii/invencion';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IInvencionResponse } from './invencion-response';
import { INVENCION_RESPONSE_CONVERTER } from './invencion-response.converter';

// tslint:disable-next-line: variable-name
const _InvencionServiceMixinBase:
  FindAllCtor<IInvencion, IInvencionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    INVENCION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InvencionService extends _InvencionServiceMixinBase {
  private static readonly MAPPING = '/invenciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${InvencionService.MAPPING}`,
      http,
    );
  }

  /**
 * Muestra activos y no activos
 *
 * @param options opciones de búsqueda.
 */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IInvencion>> {
    return this.find<IInvencionResponse, IInvencion>(
      `${this.endpointUrl}/todos`,
      options,
      INVENCION_RESPONSE_CONVERTER
    );
  }
}
