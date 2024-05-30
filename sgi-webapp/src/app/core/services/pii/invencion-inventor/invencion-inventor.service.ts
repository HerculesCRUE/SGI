import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IInvencionInventorResponse } from './invencion-inventor-response';
import { INVENCION_INVENTOR_RESPONSE_CONVERTER } from './invencion-inventor-response.converter';

// tslint:disable-next-line: variable-name
const _InvencionInventorServiceMixinBase:
  FindByIdCtor<number, IInvencionInventor, IInvencionInventorResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    INVENCION_INVENTOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InvencionInventorService extends _InvencionInventorServiceMixinBase {

  private static readonly MAPPING = '/invencion-inventores';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${InvencionInventorService.MAPPING}`,
      http,
    );
  }

  /**
   * Indica si es eliminable
   *
   * @param id Id del iventor
   * @returns **true** si no tiene relaciones, **false** en cualquier otro caso
   */
  isEliminable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/eliminable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
