import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IEvaluadorResponse } from '@core/services/eti/evaluador/evaluador-response';
import { EVALUADOR_RESPONSE_CONVERTER } from '@core/services/eti/evaluador/evaluador-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConflictoInteresResponse } from './conflicto-intereses/conflicto-intereses-response';
import { CONFLICTO_INTERESES_RESPONSE_CONVERTER } from './conflicto-intereses/conflicto-intereses-response.converter';
import { IEvaluacionResponse } from './evaluacion/evaluacion-response';
import { EVALUACION_RESPONSE_CONVERTER } from './evaluacion/evaluacion-response.converter';

const _EvaluadorServiceMixinBase:
  CreateCtor<IEvaluador, IEvaluador, IEvaluadorResponse, IEvaluadorResponse> &
  UpdateCtor<number, IEvaluador, IEvaluador, IEvaluadorResponse, IEvaluadorResponse> &
  FindByIdCtor<number, IEvaluador, IEvaluadorResponse> &
  FindAllCtor<IEvaluador, IEvaluadorResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          EVALUADOR_RESPONSE_CONVERTER,
          EVALUADOR_RESPONSE_CONVERTER
        ),
        EVALUADOR_RESPONSE_CONVERTER,
        EVALUADOR_RESPONSE_CONVERTER
      ),
      EVALUADOR_RESPONSE_CONVERTER
    ),
    EVALUADOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EvaluadorService extends _EvaluadorServiceMixinBase {
  private static readonly MAPPING = '/evaluadores';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${EvaluadorService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Devuelve las evaluaciones cuyo evaluador sea el usuario en sesión
   *
   * @param options Opciones de filtrado y ordenación
   */
  getEvaluaciones(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionResponse, IEvaluacion>(
      `${this.endpointUrl}/evaluaciones`,
      options,
      EVALUACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve los seguimientos cuyo evaluador sea el usuario en sesión
   *
   * @param options Opciones de filtrado y ordenación
   */
  getSeguimientos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionResponse, IEvaluacion>(
      `${this.endpointUrl}/evaluaciones-seguimiento`,
      options,
      EVALUACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve todos las memorias asignables a la convocatoria
   *
   * @param idComite id comite.
   * @param idMemoria id memoria.
   * @return las memorias asignables a la convocatoria.
   */
  findAllMemoriasAsignablesConvocatoria(idComite: number, idMemoria: number, fechaEvaluacion: DateTime)
    : Observable<SgiRestListResult<IEvaluador>> {
    return this.find<IEvaluadorResponse, IEvaluador>(
      `${this.endpointUrl}/comite/${idComite}/sinconflictointereses/${idMemoria}/fecha/${LuxonUtils.toBackend(fechaEvaluacion)}`,
      null,
      EVALUADOR_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve todos los conflictos de interés por evaluador id.
   * @param idEvaluador id evaluador.
   */
  findConflictosInteres(idEvaluador: number) {
    return this.find<IConflictoInteresResponse, IConflictoInteres>(
      `${this.endpointUrl}/${idEvaluador}/conflictos`,
      null,
      CONFLICTO_INTERESES_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si el usuario es evaluador de alguna evaluación
   *
   */
  hasAssignedEvaluaciones(): Observable<boolean> {
    const url = `${this.endpointUrl}/evaluaciones`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si el usuario es evaluador de alguna evaluación en seguimiento
   *
   */
  hasAssignedEvaluacionesSeguimiento(): Observable<boolean> {
    const url = `${this.endpointUrl}/evaluaciones-seguimiento`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si el usuario es evaluador de algun acta
   *
   */
  hasAssignedActas(): Observable<boolean> {
    const url = `${this.endpointUrl}/actas`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si el usuario es evaluador activo en algun comite
   *
   */
  isEvaluador(): Observable<boolean> {
    const url = `${this.endpointUrl}/is-evaluador`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si el evaluador esta activo en el comite
   */
  isActivo(evaluadorId: number, comiteId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${evaluadorId}/activo-comite/${comiteId}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
