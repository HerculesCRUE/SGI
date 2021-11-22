
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONFLICTO_INTERESES_CONVERTER } from '@core/converters/eti/conflicto-intereses.converter';
import { EVALUACION_CONVERTER } from '@core/converters/eti/evaluacion.converter';
import { EVALUADOR_CONVERTER } from '@core/converters/eti/evaluador.converter';
import { IConflictoInteresBackend } from '@core/models/eti/backend/conflicto-intereses-backend';
import { IEvaluacionBackend } from '@core/models/eti/backend/evaluacion-backend';
import { IEvaluadorBackend } from '@core/models/eti/backend/evaluador-backend';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EvaluadorService extends SgiMutableRestService<number, IEvaluadorBackend, IEvaluador>{
  private static readonly MAPPING = '/evaluadores';

  constructor(protected http: HttpClient) {
    super(
      EvaluadorService.name,
      `${environment.serviceServers.eti}${EvaluadorService.MAPPING}`,
      http,
      EVALUADOR_CONVERTER
    );
  }

  /**
   * Devuelve las evaluaciones cuyo evaluador sea el usuario en sesión
   *
   * @param options Opciones de filtrado y ordenación
   */
  getEvaluaciones(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionBackend, IEvaluacion>(
      `${this.endpointUrl}/evaluaciones`,
      options,
      EVALUACION_CONVERTER
    );
  }

  /**
   * Devuelve los seguimientos cuyo evaluador sea el usuario en sesión
   *
   * @param options Opciones de filtrado y ordenación
   */
  getSeguimientos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionBackend, IEvaluacion>(
      `${this.endpointUrl}/evaluaciones-seguimiento`,
      options,
      EVALUACION_CONVERTER
    );
  }

  /**
   * Devuelve todos las memorias asignables a la convocatoria
   *
   * @param idComite id comite.
   * @param idMemoria id memoria.
   * @return las memorias asignables a la convocatoria.
   */
  findAllMemoriasAsignablesConvocatoria(idComite: number, idMemoria: number)
    : Observable<SgiRestListResult<IEvaluador>> {
    return this.find<IEvaluadorBackend, IEvaluador>(
      `${this.endpointUrl}/comite/${idComite}/sinconflictointereses/${idMemoria}`,
      null,
      EVALUADOR_CONVERTER
    );
  }

  /**
   * Devuelve todos los conflictos de interés por evaluador id.
   * @param idEvaluador id evaluador.
   */
  findConflictosInteres(idEvaluador: number) {
    return this.find<IConflictoInteresBackend, IConflictoInteres>(
      `${this.endpointUrl}/${idEvaluador}/conflictos`,
      null,
      CONFLICTO_INTERESES_CONVERTER
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

}
