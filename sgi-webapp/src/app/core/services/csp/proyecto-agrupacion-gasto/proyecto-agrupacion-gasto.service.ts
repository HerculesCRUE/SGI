import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IAgrupacionGastoConceptoResponse } from '../agrupacio-gasto-concepto/agrupacion-gasto-concepto-response';
import { AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER } from '../agrupacio-gasto-concepto/agrupacion-gasto-concepto-response.converter';
import { IProyectoAgrupacionGastoRequest } from './proyecto-agrupacion-gasto-request';
import { PROYECTO_AGRUPACION_GASTO_REQUEST_CONVERTER } from './proyecto-agrupacion-gasto-request.converter';
import { IProyectoAgrupacionGastoResponse } from './proyecto-agrupacion-gasto-response';
import { PROYECTO_AGRUPACION_GASTO_RESPONSE_CONVERTER } from './proyecto-agrupacion-gasto-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoAgrupacionGastoServiceMixinBase:
  CreateCtor<IProyectoAgrupacionGasto, IProyectoAgrupacionGasto, IProyectoAgrupacionGastoRequest, IProyectoAgrupacionGastoResponse> &
  UpdateCtor<number, IProyectoAgrupacionGasto, IProyectoAgrupacionGasto, IProyectoAgrupacionGastoRequest, IProyectoAgrupacionGastoResponse> &
  FindByIdCtor<number, IProyectoAgrupacionGasto, IProyectoAgrupacionGastoResponse> &
  FindAllCtor<IProyectoAgrupacionGasto, IProyectoAgrupacionGastoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROYECTO_AGRUPACION_GASTO_REQUEST_CONVERTER,
          PROYECTO_AGRUPACION_GASTO_RESPONSE_CONVERTER
        ),
        PROYECTO_AGRUPACION_GASTO_REQUEST_CONVERTER,
        PROYECTO_AGRUPACION_GASTO_RESPONSE_CONVERTER
      ),
      PROYECTO_AGRUPACION_GASTO_RESPONSE_CONVERTER
    ),
    PROYECTO_AGRUPACION_GASTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoAgrupacionGastoService extends _ProyectoAgrupacionGastoServiceMixinBase {
  private static readonly MAPPING = '/agrupaciongastos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoAgrupacionGastoService.MAPPING}`,
      http,
    );
  }

  /** Comprueba si existe un agrupacion gasto
   *
   * @param id Id del proyecto agrupacion gasto
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  public deleteAgrupacionGastoById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /** Devuelve el listado de IAgrupacionGastoConcepto de un IProyectoAgrupacionGasto
   *
   * @param id Id del IProyectoAgrupacionGasto
   */
  findAllAgrupacionesGastoConceptoByIdAgrupacion(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IAgrupacionGastoConcepto>> {
    return this.find<IAgrupacionGastoConceptoResponse, IAgrupacionGastoConcepto>(
      `${this.endpointUrl}/${id}/agrupaciongastoconceptos`,
      options,
      AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER
    );
  }
}
