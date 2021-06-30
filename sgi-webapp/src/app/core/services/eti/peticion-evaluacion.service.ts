import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EQUIPO_TRABAJO_WITH_IS_ELIMINABLE_CONVERTER } from '@core/converters/eti/equipo-trabajo-with-is-eliminable.converter';
import { EQUIPO_TRABAJO_CONVERTER } from '@core/converters/eti/equipo-trabajo.converter';
import { MEMORIA_PETICION_EVALUACION_CONVERTER } from '@core/converters/eti/memoria-peticion-evaluacion.converter';
import { PETICION_EVALUACION_WITH_IS_ELIMINABLE_CONVERTER } from '@core/converters/eti/peticion-evaluacion-with-is-eliminable.converter';
import { PETICION_EVALUACION_CONVERTER } from '@core/converters/eti/peticion-evaluacion.converter';
import { TAREA_WITH_IS_ELIMINABLE_CONVERTER } from '@core/converters/eti/tarea-with-is-eliminable.converter';
import { TAREA_CONVERTER } from '@core/converters/eti/tarea.converter';
import { IEquipoTrabajoBackend } from '@core/models/eti/backend/equipo-trabajo-backend';
import { IEquipoTrabajoWithIsEliminableBackend } from '@core/models/eti/backend/equipo-trabajo-with-is-eliminable-backend';
import { IMemoriaPeticionEvaluacionBackend } from '@core/models/eti/backend/memoria-peticion-evaluacion-backend';
import { IPeticionEvaluacionBackend } from '@core/models/eti/backend/peticion-evaluacion-backend';
import { IPeticionEvaluacionWithIsEliminableBackend } from '@core/models/eti/backend/peticion-evaluacion-with-is-eliminable-backend';
import { ITareaBackend } from '@core/models/eti/backend/tarea-backend';
import { ITareaWithIsEliminableBackend } from '@core/models/eti/backend/tarea-with-is-eliminable-backend';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IPeticionEvaluacionWithIsEliminable } from '@core/models/eti/peticion-evaluacion-with-is-eliminable';
import { ITarea } from '@core/models/eti/tarea';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PeticionEvaluacionService extends SgiMutableRestService<number, IPeticionEvaluacionBackend, IPeticionEvaluacion> {
  private static readonly MAPPING = '/peticionevaluaciones';

  constructor(private readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      PeticionEvaluacionService.name,
      `${environment.serviceServers.eti}${PeticionEvaluacionService.MAPPING}`,
      http,
      PETICION_EVALUACION_CONVERTER
    );
  }

  findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<IPeticionEvaluacionWithIsEliminable>> {
    // TODO: Revisar. La sobrecarga funciona porque tienen base común, pero no debería ser así.
    return this.find<IPeticionEvaluacionWithIsEliminableBackend, IPeticionEvaluacionWithIsEliminable>(
      `${this.endpointUrl}`,
      options,
      PETICION_EVALUACION_WITH_IS_ELIMINABLE_CONVERTER
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
    return this.find<IEquipoTrabajoWithIsEliminableBackend, IEquipoTrabajoWithIsEliminable>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/equipo-investigador`,
      null,
      EQUIPO_TRABAJO_WITH_IS_ELIMINABLE_CONVERTER
    );
  }

  /**
   * Devuelve las tareas de la PeticionEvaluacion.
   *
   * @param idPeticionEvaluacion id de la peticion de evaluacion.
   * @return las tareas de la PeticionEvaluacion.
   */
  findTareas(idPeticionEvaluacion: number): Observable<SgiRestListResult<ITareaWithIsEliminable>> {
    return this.find<ITareaWithIsEliminableBackend, ITareaWithIsEliminable>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/tareas`,
      null,
      TAREA_WITH_IS_ELIMINABLE_CONVERTER
    );
  }

  /**
   * Devuelve las memorias de la PeticionEvaluacion.
   *
   * @param idPeticionEvaluacion id de la peticion de evaluacion.
   * @return las memorias de la PeticionEvaluacion.
   */
  findMemorias(idPeticionEvaluacion: number): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    return this.find<IMemoriaPeticionEvaluacionBackend, IMemoriaPeticionEvaluacion>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/memorias`,
      null,
      MEMORIA_PETICION_EVALUACION_CONVERTER
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
    return this.http.post<IEquipoTrabajoBackend>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/equipos-trabajo`,
      EQUIPO_TRABAJO_CONVERTER.fromTarget(equipoTrabajo),
    ).pipe(
      map(response => EQUIPO_TRABAJO_CONVERTER.toTarget(response)),
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
    return this.http.post<ITareaBackend>(
      `${this.endpointUrl}/${idPeticionEvaluacion}/equipos-trabajo/${idEquipoTrabajo}/tareas`,
      TAREA_CONVERTER.fromTarget(tarea)
    ).pipe(
      map(response => TAREA_CONVERTER.toTarget(response))
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
    return this.find<IPeticionEvaluacionWithIsEliminableBackend, IPeticionEvaluacionWithIsEliminable>(
      `${this.endpointUrl}/memorias`,
      options,
      PETICION_EVALUACION_WITH_IS_ELIMINABLE_CONVERTER
    );
  }
}
