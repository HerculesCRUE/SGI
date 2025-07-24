import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IPeticionEvaluacionWithIsEliminable } from '@core/models/eti/peticion-evaluacion-with-is-eliminable';
import { ITarea } from '@core/models/eti/tarea';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { IPeticionEvaluacionResponse } from '@core/services/eti/peticion-evaluacion/peticion-evaluacion-response';
import { PETICION_EVALUACION_RESPONSE_CONVERTER } from '@core/services/eti/peticion-evaluacion/peticion-evaluacion-response.converter';
import { IPeticionEvaluacionWithIsEliminableResponse } from '@core/services/eti/peticion-evaluacion/peticion-evaluacion-with-is-eliminable-response';
import { PETICION_EVALUACION_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER } from '@core/services/eti/peticion-evaluacion/peticion-evaluacion-with-is-eliminable-response.converter';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IEquipoTrabajoResponse } from './equipo-trabajo/equipo-trabajo-response';
import { EQUIPO_TRABAJO_RESPONSE_CONVERTER } from './equipo-trabajo/equipo-trabajo-response.converter';
import { IEquipoTrabajoWithIsEliminableResponse } from './equipo-trabajo/equipo-trabajo-with-is-eliminable-response';
import { EQUIPO_TRABAJO_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER } from './equipo-trabajo/equipo-trabajo-with-is-eliminable-response.converter';
import { IMemoriaPeticionEvaluacionResponse } from './memoria/memoria-peticion-evaluacion-response';
import { MEMORIA_PETICION_EVALUACION_RESPONSE_CONVERTER } from './memoria/memoria-peticion-evaluacion-response.converter';
import { ITareaResponse } from './tarea/tarea-response';
import { TAREA_RESPONSE_CONVERTER } from './tarea/tarea-response.converter';
import { ITareaWithIsEliminableResponse } from './tarea/tarea-with-is-eliminable-response';
import { TAREA_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER } from './tarea/tarea-with-is-eliminable-response.converter';

// tslint:disable-next-line: variable-name
const _PeticionEvaluacionServiceMixinBase:
  CreateCtor<IPeticionEvaluacion, IPeticionEvaluacion, IPeticionEvaluacionResponse, IPeticionEvaluacionResponse> &
  UpdateCtor<number, IPeticionEvaluacion, IPeticionEvaluacion, IPeticionEvaluacionResponse, IPeticionEvaluacionResponse> &
  FindByIdCtor<number, IPeticionEvaluacion, IPeticionEvaluacionResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        PETICION_EVALUACION_RESPONSE_CONVERTER,
        PETICION_EVALUACION_RESPONSE_CONVERTER
      ),
      PETICION_EVALUACION_RESPONSE_CONVERTER,
      PETICION_EVALUACION_RESPONSE_CONVERTER
    ),
    PETICION_EVALUACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class PeticionEvaluacionService extends _PeticionEvaluacionServiceMixinBase {
  private static readonly MAPPING = '/peticionevaluaciones';

  constructor(private readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${PeticionEvaluacionService.MAPPING}`,
      http
    );
  }

  /**
  * Elimina la petición de evaluación
  *
  * @param idPeticionEvaluacion Identifiacdor de la petición de evaluación.
  */
  deleteById(idPeticionEvaluacion: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idPeticionEvaluacion}`);
  }

  findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<IPeticionEvaluacionWithIsEliminable>> {
    return this.find<IPeticionEvaluacionResponse, IPeticionEvaluacionWithIsEliminable>(
      `${this.endpointUrl}`,
      options,
      PETICION_EVALUACION_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve los equipos de trabajo de la PeticionEvaluacion.
   *
   * @param idPeticionEvaluacion id de la peticion de evaluacion.
   * @return los equipos de trabajo de la PeticionEvaluacion.
   */
  findEquipoInvestigador(idPeticionEvaluacion: number)
    : Observable<SgiRestListResult<IEquipoTrabajoWithIsEliminable>> {
    return this.find<IEquipoTrabajoWithIsEliminableResponse, IEquipoTrabajoWithIsEliminable>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/equipo-investigador`,
      null,
      EQUIPO_TRABAJO_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve las tareas de la PeticionEvaluacion.
   *
   * @param idPeticionEvaluacion id de la peticion de evaluacion.
   * @return las tareas de la PeticionEvaluacion.
   */
  findTareas(idPeticionEvaluacion: number): Observable<SgiRestListResult<ITareaWithIsEliminable>> {
    return this.find<ITareaWithIsEliminableResponse, ITareaWithIsEliminable>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/tareas`,
      null,
      TAREA_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve las memorias de la PeticionEvaluacion.
   *
   * @param idPeticionEvaluacion id de la peticion de evaluacion.
   * @return las memorias de la PeticionEvaluacion.
   */
  findMemorias(idPeticionEvaluacion: number): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    return this.find<IMemoriaPeticionEvaluacionResponse, IMemoriaPeticionEvaluacion>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/memorias`,
      null,
      MEMORIA_PETICION_EVALUACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Create the element and return the persisted value
   *
   * @param idPeticionEvaluacion Identifiacdor de la peticion evaluacion.
   * @param equipoTrabajo El nuevo equipo de trabajo
   *
   * @returns observable para crear el equipo trabajo.
   */
  createEquipoTrabajo(idPeticionEvaluacion: number, equipoTrabajo: IEquipoTrabajo): Observable<IEquipoTrabajo> {
    return this.post<IEquipoTrabajoResponse, IEquipoTrabajoResponse>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/equipos-trabajo`,
      EQUIPO_TRABAJO_RESPONSE_CONVERTER.fromTarget(equipoTrabajo),
    ).pipe(
      map(response => EQUIPO_TRABAJO_RESPONSE_CONVERTER.toTarget(response)),
      catchError((error: HttpErrorResponse) => {
        this.logger.error(error);
        return throwError(error);
      })
    );
  }

  /**
   * Create the element and return the persisted value
   *
   * @param idPeticionEvaluacion Identifiacdor de la peticion evaluacion.
   * @param idEquipoTrabajo Identificador del equipo trabajo.
   * @param tarea La nueva tarea
   *
   * @returns observable para crear la tarea.
   */
  createTarea(idPeticionEvaluacion: number, idEquipoTrabajo: number, tarea: ITarea): Observable<ITarea> {
    return this.post<ITareaResponse, ITareaResponse>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/equipos-trabajo/${idEquipoTrabajo}/tareas`,
      TAREA_RESPONSE_CONVERTER.fromTarget(tarea)
    ).pipe(
      map(response => TAREA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Elimina el equipo de trabajo de la peticion de evaluación.
   *
   * @param idPeticionEvaluacion Identifiacdor de la peticion evaluacion.
   * @param idEquipoTrabajo Identificador del equipo trabajo.
   *
   * @returns observable para eliminar el equipo trabajo.
   */
  deleteEquipoTrabajoPeticionEvaluacion(idPeticionEvaluacion: number, idEquipoTrabajo: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idPeticionEvaluacion}/equipos-trabajo/${idEquipoTrabajo}`);
  }

  /**
   * Elimina la tarea de un equipo de trabajo de la peticion de evaluación.
   *
   * @param idPeticionEvaluacion Identifiacdor de la peticion evaluacion.
   * @param idEquipoTrabajo Identifiacdor del equipo de trabajo.
   * @param idTarea Identificador de la tarea.
   *
   * @returns observable para eliminar la tarea.
   */
  deleteTareaPeticionEvaluacion(idPeticionEvaluacion: number, idEquipoTrabajo: number, idTarea: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idPeticionEvaluacion}/equipos-trabajo/${idEquipoTrabajo}/tareas/${idTarea}`);
  }

  /**
   * Devuelve todas las peticiones de evaluación de una persona en la que es creador de la petición de evaluación
   *  o responsable de una memoria
   * @param options opciones de búsqueda
   * @return las peticiones de evaluación
   */
  findAllPeticionEvaluacionMemoria(options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IPeticionEvaluacionWithIsEliminable>> {
    return this.find<IPeticionEvaluacionWithIsEliminableResponse, IPeticionEvaluacionWithIsEliminable>(
      `${this.endpointUrl}/memorias`,
      options,
      PETICION_EVALUACION_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si el usuario es responsable o creador de la petición de evaluación
   *
   * @param id Id de la Petición de evaluación
   */
  isResponsableOrCreador(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/responsable-creador`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }
}
