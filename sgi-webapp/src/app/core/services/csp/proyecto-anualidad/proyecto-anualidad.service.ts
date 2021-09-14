import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { IAnualidadResumen } from '@core/models/csp/anualidad-resumen';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
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
  SgiRestFindOptions, SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IAnualidadGastoResponse } from '../anualidad-gasto/anualidad-gasto-response';
import { ANUALIDAD_GASTO_RESPONSE_CONVERTER } from '../anualidad-gasto/anualidad-gasto-response.converter';
import { IAnualidadIngresoResponse } from '../anualidad-ingreso/anualidad-ingreso-response';
import { ANUALIDAD_INGRESO_RESPONSE_CONVERTER } from '../anualidad-ingreso/anualidad-ingreso-response.converter';
import { IAnualidadResumenResponse } from './anualidad-resumen-response';
import { ANUALIDAD_RESUMEN_RESPONSE_CONVERTER } from './anualidad-resumen-response.converter';
import { IProyectoAnualidadRequest } from './proyecto-anualidad-request';
import { PROYECTO_ANUALIDAD_REQUEST_CONVERTER } from './proyecto-anualidad-request.converter';
import { IProyectoAnualidadResponse } from './proyecto-anualidad-response';
import { PROYECTO_ANUALIDAD_RESPONSE_CONVERTER } from './proyecto-anualidad-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoAnualidadServiceMixinBase:
  CreateCtor<IProyectoAnualidad, IProyectoAnualidad, IProyectoAnualidadRequest, IProyectoAnualidadResponse> &
  UpdateCtor<number, IProyectoAnualidad, IProyectoAnualidad, IProyectoAnualidadRequest, IProyectoAnualidadResponse> &
  FindByIdCtor<number, IProyectoAnualidad, IProyectoAnualidadResponse> &
  FindAllCtor<IProyectoAnualidad, IProyectoAnualidadResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROYECTO_ANUALIDAD_REQUEST_CONVERTER,
          PROYECTO_ANUALIDAD_RESPONSE_CONVERTER
        ),
        PROYECTO_ANUALIDAD_REQUEST_CONVERTER,
        PROYECTO_ANUALIDAD_RESPONSE_CONVERTER
      ),
      PROYECTO_ANUALIDAD_RESPONSE_CONVERTER
    ),
    PROYECTO_ANUALIDAD_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class ProyectoAnualidadService extends _ProyectoAnualidadServiceMixinBase {

  private static readonly MAPPING = '/proyectosanualidad';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoAnualidadService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve el listado de IAnualidadGasto de un IProyectoAnualidad
   *
   * @param id Id del IProyectoAnualidad
   */
  findAllAnualidadGasto(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IAnualidadGasto>> {
    return this.find<IAnualidadGastoResponse, IAnualidadGasto>(
      `${this.endpointUrl}/${id}/anualidadgastos`,
      options,
      ANUALIDAD_GASTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve el listado de IAnualidadIngreso de un IProyectoAnualidad
   *
   * @param id Id del IProyectoAnualidad
   */
  findAllAnualidadIngreso(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IAnualidadIngreso>> {
    return this.find<IAnualidadIngresoResponse, IAnualidadIngreso>(
      `${this.endpointUrl}/${id}/anualidadingresos`,
      options,
      ANUALIDAD_INGRESO_RESPONSE_CONVERTER
    );
  }

  getAnualidadesResumen(id: number): Observable<SgiRestListResult<IAnualidadResumen>> {
    return this.find<IAnualidadResumenResponse, IAnualidadResumen>(
      `${this.endpointUrl}/${id}/partidas-resumen`,
      null,
      ANUALIDAD_RESUMEN_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si existe un proyecto anualidad
   *
   * @param id Id del proyecto anualidad
   * @retrurn true/false
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Elimina un proyecto anualidad por id.
   *
   * @param id Id del proyecto anualidad
   */
  deleteById(id: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      })
    );
  }

}
