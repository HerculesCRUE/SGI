import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { COMENTARIO_CONVERTER } from '@core/converters/eti/comentario.converter';
import { EVALUACION_WITH_IS_ELIMINABLE_CONVERTER } from '@core/converters/eti/evaluacion-with-is-eliminable.converter';
import { EVALUACION_CONVERTER } from '@core/converters/eti/evaluacion.converter';
import { DOCUMENTO_CONVERTER } from '@core/converters/sgdoc/documento.converter';
import { IComentarioBackend } from '@core/models/eti/backend/comentario-backend';
import { IEvaluacionBackend } from '@core/models/eti/backend/evaluacion-backend';
import { IEvaluacionWithIsEliminableBackend } from '@core/models/eti/backend/evaluacion-with-is-eliminable-backend';
import { IComentario } from '@core/models/eti/comentario';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { IDocumentoBackend } from '@core/models/sgdoc/backend/documento-backend';
import { IDocumento } from '@core/models/sgdoc/documento';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EvaluacionService extends SgiMutableRestService<number, IEvaluacionBackend, IEvaluacion>{
  private static readonly MAPPING = '/evaluaciones';

  constructor(protected http: HttpClient) {
    super(
      EvaluacionService.name,
      `${environment.serviceServers.eti}${EvaluacionService.MAPPING}`,
      http,
      EVALUACION_CONVERTER
    );
  }

  /**
   * Devuelve todos las evaluaciones por convocatoria id.
   *
   * @param convocatoriaId id convocatoria.
   */
  findAllByConvocatoriaReunionId(convocatoriaId: number): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionBackend, IEvaluacion>(
      `${this.endpointUrl}/convocatoriareuniones/${convocatoriaId}`,
      null,
      EVALUACION_CONVERTER
    );
  }

  /**
   * Devuelve los comentarios de tipo GESTOR de una evalución
   *
   * @param id Id de la evaluación
   * @param options Opciones de paginación
   */
  getComentariosGestor(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IComentario>> {
    return this.find<IComentarioBackend, IComentario>(
      `${this.endpointUrl}/${id}/comentarios-gestor`,
      options,
      COMENTARIO_CONVERTER
    );
  }

  /**
   * Devuelve los comentarios de tipo EVALUADOR de una evalución
   *
   * @param id Id de la evaluación
   * @param options Opciones de paginación
   */
  getComentariosEvaluador(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IComentario>> {
    return this.find<IComentarioBackend, IComentario>(
      `${this.endpointUrl}/${id}/comentarios-evaluador`,
      options,
      COMENTARIO_CONVERTER
    );
  }

  /**
   * Devuelve los comentarios de tipo ACTA de una evalución
   *
   * @param id Id de la evaluación
   * @param options Opciones de paginación
   */
  getComentariosActa(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IComentario>> {
    return this.find<IComentarioBackend, IComentario>(
      `${this.endpointUrl}/${id}/comentarios-acta`,
      options,
      COMENTARIO_CONVERTER
    );
  }

  /**
   * Añade un comentario de tipo GESTOR a una evaluación
   *
   * @param id Id de la evaluación
   * @param comentario Comentario a crear
   */
  createComentarioGestor(id: number, comentario: IComentario): Observable<IComentario> {
    return this.http.post<IComentarioBackend>(
      `${this.endpointUrl}/${id}/comentario-gestor`,
      COMENTARIO_CONVERTER.fromTarget(comentario)
    ).pipe(
      map(response => COMENTARIO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Añade un comentario de tipo EVALUADOR a una evaluación
   *
   * @param id Id de la evaluación
   * @param comentario Comentario a crear
   */
  createComentarioEvaluador(id: number, comentario: IComentario): Observable<IComentario> {
    return this.http.post<IComentarioBackend>(
      `${this.endpointUrl}/${id}/comentario-evaluador`,
      COMENTARIO_CONVERTER.fromTarget(comentario)
    ).pipe(
      map(response => COMENTARIO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Añade un comentario de tipo ACTA a una evaluación
   *
   * @param id Id de la evaluación
   * @param comentario Comentario a crear
   */
  createComentarioActa(id: number, comentario: IComentario): Observable<IComentario> {
    return this.http.post<IComentarioBackend>(
      `${this.endpointUrl}/${id}/comentario-acta`,
      COMENTARIO_CONVERTER.fromTarget(comentario)
    ).pipe(
      map(response => COMENTARIO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Actualiza un comentario de tipo GESTOR de una evaluación
   *
   * @param id Id de la evaluación
   * @param comentario Comentario a actualizar
   * @param idComentario Id del Comentario
   */
  updateComentarioGestor(id: number, comentario: IComentario, idComentario: number): Observable<IComentario> {
    return this.http.put<IComentarioBackend>(
      `${this.endpointUrl}/${id}/comentario-gestor/${idComentario}`,
      COMENTARIO_CONVERTER.fromTarget(comentario)
    ).pipe(
      map(response => COMENTARIO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Actualiza un comentario de tipo EVALUADOR de una evaluación
   *
   * @param id Id de la evaluación
   * @param comentario Comentario a actualizar
   * @param idComentario Id del Comentario
   */
  updateComentarioEvaluador(id: number, comentario: IComentario, idComentario: number): Observable<IComentario> {
    return this.http.put<IComentarioBackend>(
      `${this.endpointUrl}/${id}/comentario-evaluador/${idComentario}`,
      COMENTARIO_CONVERTER.fromTarget(comentario)
    ).pipe(
      map(response => COMENTARIO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Elimina un comentario de tipo GESTOR de una evaluación
   *
   * @param id Id de la evaluación
   * @param idComentario Id del comentario
   */
  deleteComentarioGestor(id: number, idComentario: number): Observable<void> {
    const params = new HttpParams().set('idComentario', idComentario.toString());
    return this.http.delete<void>(`${this.endpointUrl}/${id}/comentario-gestor/${idComentario}`, { params });
  }

  /**
   * Elimina un comentario de tipo EVALUADOR de una evaluación
   *
   * @param id Id de la evaluación
   * @param idComentario Id del comentario
   */
  deleteComentarioEvaluador(id: number, idComentario: number): Observable<void> {
    const params = new HttpParams().set('idComentario', idComentario.toString());
    return this.http.delete<void>(`${this.endpointUrl}/${id}/comentario-evaluador/${idComentario}`, { params });
  }

  /**
   * Elimina un comentario de tipo ACTA de una evaluación
   *
   * @param id Id de la evaluación
   * @param idComentario Id del comentario
   */
  deleteComentarioActa(id: number, idComentario: number): Observable<void> {
    const params = new HttpParams().set('idComentario', idComentario.toString());
    return this.http.delete<void>(`${this.endpointUrl}/${id}/comentario-acta/${idComentario}`, { params });
  }

  /**
   * Por un lado devuelve las evaluaciones de tipo Memoria, con las memorias en estado "En Evaluacion" o "En secretaría revisión mínima",
   * y por otro, las evaluaciones de tipo Retrospectiva cuya memoria tenga en estado de la retrospectiva "En Evaluacion",
   * ambas en su última versión (que serán las que no estén evaluadas).
   * @param options SgiRestFindOptions.
   */
  findAllByMemoriaAndRetrospectivaEnEvaluacion(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionBackend, IEvaluacion>(
      `${environment.serviceServers.eti}${EvaluacionService.MAPPING}/evaluables`,
      options,
      EVALUACION_CONVERTER
    );
  }

  /**
   * Devuelve todos las evaluaciones de la convocatoria que no son revisión mínima.
   *
   * @param idConvocatoria id convocatoria.
   * @param options opcione de busqueda.
   * @return las evaluaciones de la convocatoria.
   */
  findAllByConvocatoriaReunionIdAndNoEsRevMinima(idConvocatoria: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IEvaluacionWithIsEliminable>> {
    return this.find<IEvaluacionWithIsEliminableBackend, IEvaluacionWithIsEliminable>(
      `${this.endpointUrl}/convocatoriareunionnorevminima/${idConvocatoria}`,
      options,
      EVALUACION_WITH_IS_ELIMINABLE_CONVERTER
    );
  }

  /**
   * Devuelve todos las memorias de evaluación que tengan determinados estados.
   *
   * @param options opciones de búsqueda.
   * @return las evaluaciones
   */

  findSeguimientoMemoria(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionBackend, IEvaluacion>(
      `${environment.serviceServers.eti}${EvaluacionService.MAPPING}/memorias-seguimiento-final`,
      options,
      EVALUACION_CONVERTER
    );
  }

  /**
   * Obtiene el documento del evaluador
   * @param idEvaluacion identificador de la evaluación
   */
  getDocumentoEvaluador(idEvaluacion: number): Observable<IDocumento> {
    return this.http.get<IDocumentoBackend>(
      `${this.endpointUrl}/${idEvaluacion}/documento-evaluador`
    ).pipe(
      map(response => DOCUMENTO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Obtiene el documento de evaluación o favorable
   * @param idEvaluacion identificador de la evaluación
   */
  getDocumentoEvaluacion(idEvaluacion: number): Observable<IDocumento> {
    return this.http.get<IDocumentoBackend>(
      `${this.endpointUrl}/${idEvaluacion}/documento-evaluacion`
    ).pipe(
      map(response => DOCUMENTO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Comprueba si el usuario es evaluador de la evaluación
   *
   */
  isEvaluacionEvaluable(idEvaluacion: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idEvaluacion}/evaluacion`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si el usuario es evaluador de la evaluación en seguimiento
   *
   */
  isSeguimientoEvaluable(idEvaluacion: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idEvaluacion}/evaluacion-seguimiento`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Permite enviar el comunicado de la evaluación modificada
   * 
   * * @param idEvaluacion id convocatoria
   */
  enviarComunicado(idEvaluacion: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idEvaluacion}/comunicado`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
