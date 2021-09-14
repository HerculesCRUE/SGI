import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IProyectoPeriodoJustificacionRequest } from './proyecto-periodo-justificacion-request';
import { PROYECTO_PERIODO_JUSTIFICACION_REQUEST_CONVERTER } from './proyecto-periodo-justificacion-request.converter';
import { IProyectoPeriodoJustificacionResponse } from './proyecto-periodo-justificacion-response';
import { PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER } from './proyecto-periodo-justificacion-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoPeriodoJstificacionMixinBase:
  CreateCtor<IProyectoPeriodoJustificacion, IProyectoPeriodoJustificacion,
    IProyectoPeriodoJustificacionRequest, IProyectoPeriodoJustificacionResponse> &
  UpdateCtor<number, IProyectoPeriodoJustificacion, IProyectoPeriodoJustificacion,
    IProyectoPeriodoJustificacionRequest, IProyectoPeriodoJustificacionResponse> &
  FindByIdCtor<number, IProyectoPeriodoJustificacion, IProyectoPeriodoJustificacionResponse> &
  FindAllCtor<IProyectoPeriodoJustificacion, IProyectoPeriodoJustificacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROYECTO_PERIODO_JUSTIFICACION_REQUEST_CONVERTER,
          PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER
        ),
        PROYECTO_PERIODO_JUSTIFICACION_REQUEST_CONVERTER,
        PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER
      ),
      PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER
    ),
    PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoJustificacionService extends _ProyectoPeriodoJstificacionMixinBase {

  private static readonly MAPPING = '/proyectoperiodosjustificacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoPeriodoJustificacionService.MAPPING}`,
      http
    );
  }

  /**
   * Comprueba si existe un Peridojustificacion
   *
   * @param id Id del Peridojustificacion
   * @retrurn true/false
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Elimina un Perido Justificacion por id.
   *
   * @param id Id del Perido Justificacion
   */
  deleteById(id: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      })
    );
  }
  /**
   * Actualiza el listado de IProyectoConceptoGastoCodigoEc asociados a un IProyectoConceptoGasto
   *
   * @param periodoJustificacionId Id del IConvocatoriaConceptoGasto
   * @param entities Listado de IConvocatoriaConceptoGastoCodigoEc
   */
  updateList(periodoJustificacionId: number, entities: IProyectoPeriodoJustificacion[]):
    Observable<IProyectoPeriodoJustificacion[]> {
    return this.http.patch<IProyectoPeriodoJustificacionResponse[]>(
      `${this.endpointUrl}/${periodoJustificacionId}`,
      PROYECTO_PERIODO_JUSTIFICACION_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map((response => PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

}
