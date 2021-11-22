import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IReparto, IRepartoCreate } from '@core/models/pii/reparto';
import { IRepartoEquipoInventor } from '@core/models/pii/reparto-equipo-inventor';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll,
  mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IRepartoCreateRequest } from './reparto-create-request';
import { REPARTO_CREATE_REQUEST_CONVERTER } from './reparto-create-request.converter';
import { IRepartoEquipoInventorResponse } from './reparto-equipo-inventor/reparto-equipo-inventor-response';
import { REPARTO_EQUIPO_INVENTOR_RESPONSE_CONVERTER } from './reparto-equipo-inventor/reparto-equipo-inventor-response.converter';
import { IRepartoGastoResponse } from './reparto-gasto/reparto-gasto-response';
import { REPARTO_GASTO_RESPONSE_CONVERTER } from './reparto-gasto/reparto-gasto-response.converter';
import { IRepartoIngresoResponse } from './reparto-ingreso/reparto-ingreso-response';
import { REPARTO_INGRESO_RESPONSE_CONVERTER } from './reparto-ingreso/reparto-ingreso-response.converter';
import { IRepartoRequest } from './reparto-request';
import { REPARTO_REQUEST_CONVERTER } from './reparto-request.converter';
import { IRepartoResponse } from './reparto-response';
import { REPARTO_RESPONSE_CONVERTER } from './reparto-response.converter';

// tslint:disable-next-line: variable-name
const _RepartoServiceMixinBase:
  FindAllCtor<IReparto, IRepartoResponse> &
  FindByIdCtor<number, IReparto, IRepartoResponse> &
  CreateCtor<IRepartoCreate, IReparto, IRepartoCreateRequest, IRepartoResponse> &
  UpdateCtor<number, IReparto, IReparto, IRepartoRequest, IRepartoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          REPARTO_REQUEST_CONVERTER,
          REPARTO_RESPONSE_CONVERTER
        ),
        REPARTO_CREATE_REQUEST_CONVERTER,
        REPARTO_RESPONSE_CONVERTER
      ),
      REPARTO_RESPONSE_CONVERTER
    ),
    REPARTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RepartoService extends _RepartoServiceMixinBase {

  private static readonly MAPPING = '/repartos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${RepartoService.MAPPING}`,
      http,
    );
  }

  /**
   * Recupera los RepartoGasto asociados a la entidad Reparto con el id indicado.
   *
   * @param id de la entidad Reparto
   * @param options opciones de búsqueda
   */
  findGastos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IRepartoGasto>> {
    return this.find<IRepartoGastoResponse, IRepartoGasto>(
      `${this.endpointUrl}/${id}/gastos`,
      options,
      REPARTO_GASTO_RESPONSE_CONVERTER);
  }

  /**
   * Recupera los IRepartoIngreso asociados a la entidad Reparto con el id indicado.
   *
   * @param id de la entidad Reparto
   * @param options opciones de búsqueda
   */
  findIngresos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IRepartoIngreso>> {
    return this.find<IRepartoIngresoResponse, IRepartoIngreso>(
      `${this.endpointUrl}/${id}/ingresos`,
      options,
      REPARTO_INGRESO_RESPONSE_CONVERTER);
  }

  /**
   * Recupera los RepartoEquipoInventor asociados a la entidad Reparto con el id indicado.
   *
   * @param id de la entidad Reparto
   * @param options opciones de búsqueda
   */
  findEquipoInventor(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IRepartoEquipoInventor>> {
    return this.find<IRepartoEquipoInventorResponse, IRepartoEquipoInventor>(
      `${this.endpointUrl}/${id}/equiposinventor`,
      options,
      REPARTO_EQUIPO_INVENTOR_RESPONSE_CONVERTER);
  }

  /**
   * Cambia el estado del Reparto a ejecutado
   * @param id del Reparto
   */
  ejecutar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/ejecutar`, { id });
  }
}
