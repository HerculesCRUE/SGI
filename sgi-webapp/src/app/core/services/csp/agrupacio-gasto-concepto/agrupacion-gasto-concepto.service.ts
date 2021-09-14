import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IAgrupacionGastoConceptoRequest } from './agrupacion-gasto-concepto-request';
import { AGRUPACION_GASTO_CONCEPTO_REQUEST_CONVERTER } from './agrupacion-gasto-concepto-request.converter';
import { IAgrupacionGastoConceptoResponse } from './agrupacion-gasto-concepto-response';
import { AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER } from './agrupacion-gasto-concepto-response.converter';

// tslint:disable-next-line: variable-name
const _AgrupacionGastoConceptoServiceMixinBase:
  CreateCtor<IAgrupacionGastoConcepto, IAgrupacionGastoConcepto, IAgrupacionGastoConceptoRequest, IAgrupacionGastoConceptoResponse> &
  UpdateCtor<number, IAgrupacionGastoConcepto, IAgrupacionGastoConcepto, IAgrupacionGastoConceptoRequest, IAgrupacionGastoConceptoResponse> &
  FindByIdCtor<number, IAgrupacionGastoConcepto, IAgrupacionGastoConceptoResponse> &
  FindAllCtor<IAgrupacionGastoConcepto, IAgrupacionGastoConceptoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          AGRUPACION_GASTO_CONCEPTO_REQUEST_CONVERTER,
          AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER
        ),
        AGRUPACION_GASTO_CONCEPTO_REQUEST_CONVERTER,
        AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER
      ),
      AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER
    ),
    AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class AgrupacionGastoConceptoService extends _AgrupacionGastoConceptoServiceMixinBase {
  private static readonly MAPPING = '/agrupaciongastoconceptos';
  private static readonly AGRUPACION_MAPPING = '/agrupaciongastos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${AgrupacionGastoConceptoService.MAPPING}`,
      http,
    );
  }

  /** Comprueba si existe un agrupacion gasto concepto
   *
   * @param id Id del agrupacion gasto al que pertenece el concepto
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
