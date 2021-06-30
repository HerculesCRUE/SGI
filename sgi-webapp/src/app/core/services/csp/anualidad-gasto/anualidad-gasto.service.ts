import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { environment } from '@env';
import {
  CreateCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindById,
  mixinUpdate, SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ANUALIDAD_GASTO_REQUEST_CONVERTER } from './anualidad-gasto-request.converter';
import { ANUALIDAD_GASTO_RESPONSE_CONVERTER } from './anualidad-gasto-response.converter';
import { IAnualidadGastoRequest } from './anualidad-gasto-request';
import { IAnualidadGastoResponse } from './anualidad-gasto-response';


// tslint:disable-next-line: variable-name
const _AnualidadGastoServiceMixinBase:
  CreateCtor<IAnualidadGasto, IAnualidadGasto, IAnualidadGastoRequest, IAnualidadGastoResponse> &
  UpdateCtor<number, IAnualidadGasto, IAnualidadGasto, IAnualidadGastoRequest, IAnualidadGastoResponse> &
  FindByIdCtor<number, IAnualidadGasto, IAnualidadGastoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        ANUALIDAD_GASTO_REQUEST_CONVERTER,
        ANUALIDAD_GASTO_RESPONSE_CONVERTER
      ),
      ANUALIDAD_GASTO_REQUEST_CONVERTER,
      ANUALIDAD_GASTO_RESPONSE_CONVERTER
    ),
    ANUALIDAD_GASTO_REQUEST_CONVERTER
  );

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
