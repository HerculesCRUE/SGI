import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_REUNION_CONVERTER } from '@core/converters/eti/convocatoria-reunion.converter';
import { DOCUMENTACION_MEMORIA_CONVERTER } from '@core/converters/eti/documentacion-memoria.converter';
import { EVALUACION_WITH_NUM_COMENTARIO_CONVERTER } from '@core/converters/eti/evaluacion-with-num-comentario.converter';
import { EVALUACION_CONVERTER } from '@core/converters/eti/evaluacion.converter';
import { INFORME_CONVERTER } from '@core/converters/eti/informe.converter';
import { MEMORIA_PETICION_EVALUACION_CONVERTER } from '@core/converters/eti/memoria-peticion-evaluacion.converter';
import { MEMORIA_CONVERTER } from '@core/converters/eti/memoria.converter';
import { RESPUESTA_CONVERTER } from '@core/converters/eti/respuesta.converter';
import { IConvocatoriaReunionBackend } from '@core/models/eti/backend/convocatoria-reunion-backend';
import { IDocumentacionMemoriaBackend } from '@core/models/eti/backend/documentacion-memoria-backend';
import { IEvaluacionBackend } from '@core/models/eti/backend/evaluacion-backend';
import { IEvaluacionWithNumComentarioBackend } from '@core/models/eti/backend/evaluacion-with-num-comentario-backend';
import { IInformeBackend } from '@core/models/eti/backend/informe-backend';
import { IMemoriaBackend } from '@core/models/eti/backend/memoria-backend';
import { IMemoriaPeticionEvaluacionBackend } from '@core/models/eti/backend/memoria-peticion-evaluacion-backend';
import { IRespuestaBackend } from '@core/models/eti/backend/respuesta-backend';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluacionWithNumComentario } from '@core/models/eti/evaluacion-with-num-comentario';
import { IInforme } from '@core/models/eti/informe';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { IRespuesta } from '@core/models/eti/respuesta';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MemoriaService extends SgiMutableRestService<number, IMemoriaBackend, IMemoria>{
  private static readonly MAPPING = '/memorias';

  constructor(private readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      MemoriaService.name,
      `${environment.serviceServers.eti}${MemoriaService.MAPPING}`,
      http,
      MEMORIA_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación asociada a una memoria según el tipo de la Evaluación
   *
   * @param id id de la memoria.
   * @param idTipoEvaluacion id del tipo de la evaluación.
   */
  getDocumentacionesTipoEvaluacion(idMemoria: number, idTipoEvaluacion: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaBackend, IDocumentacionMemoria>(
      `${this.endpointUrl}/${idMemoria}/documentaciones/${idTipoEvaluacion}`,
      options,
      DOCUMENTACION_MEMORIA_CONVERTER
    );
  }

  /**
   * Devuelve todas las evaluaciones de una memoria.
   *
   * @param memoriaId id de la memoria.
   * @param evaluacionId id de la evaluacion.
   */
  getEvaluacionesAnteriores(memoriaId: number, evaluacionId: number, tipoComentarioId: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IEvaluacionWithNumComentario>> {
    return this.find<IEvaluacionWithNumComentarioBackend, IEvaluacionWithNumComentario>(
      `${this.endpointUrl}/${memoriaId}/evaluaciones-anteriores/${evaluacionId}/${tipoComentarioId}`,
      options,
      EVALUACION_WITH_NUM_COMENTARIO_CONVERTER
    );
  }

  /**
   * Devuelve todos las memorias asignables a la convocatoria
   *
   * @param idConvocatoria id convocatoria.
   * @param options opciones de busqueda.
   * @return las memorias asignables a la convocatoria.
   */
  findAllMemoriasAsignablesConvocatoria(idConvocatoria: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria>> {
    return this.find<IMemoriaBackend, IMemoria>(
      `${this.endpointUrl}/asignables/${idConvocatoria}`,
      options,
      MEMORIA_CONVERTER
    );
  }

  /**
   * Devuelve todos las memorias asignables para una convocatoria de tipo seguimiento
   *
   * @param options opciones de busqueda.
   * @return las memorias asignables a la convocatoria de ese tipo seguimiento.
   */
  findAllAsignablesTipoConvocatoriaSeguimiento(options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria>> {
    return this.find<IMemoriaBackend, IMemoria>(
      `${this.endpointUrl}/tipo-convocatoria-seg`,
      options,
      MEMORIA_CONVERTER
    );
  }

  /**
   * Devuelve todos las memorias asignables para una convocatoria de tipo ordinaria / extraordinaria
   *
   * @param options opciones de busqueda.
   * @return las memorias asignables a la convocatoria de tipo ordinaria / extraordinaria.
   *
   */
  findAllAsignablesTipoConvocatoriaOrdExt(options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria>> {
    return this.find<IMemoriaBackend, IMemoria>(
      `${this.endpointUrl}/tipo-convocatoria-ord-ext`,
      options,
      MEMORIA_CONVERTER
    );
  }

  /**
   * Devuelve todas las memorias de una persona en la que es creador de la petición de evaluación
   *  o responsable de una memoria
   * @param options opciones de búsqueda
   * @return las memorias
   */
  findAllMemoriasEvaluacionByPersonaRef(options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    return this.find<IMemoriaPeticionEvaluacionBackend, IMemoriaPeticionEvaluacion>(
      `${this.endpointUrl}/persona`,
      options,
      MEMORIA_PETICION_EVALUACION_CONVERTER
    );
  }

  /**
   * Se sobrecarga el metodo para permirtir la sobreescritura del findAll y retornar otro tipo. 
   * Siempre se retornará {@link IMemoriaPeticionEvaluacion}
   */
  findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria>>;
  findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria | IMemoriaPeticionEvaluacion>> {
    return this.find<IMemoriaPeticionEvaluacionBackend, IMemoriaPeticionEvaluacion>(
      `${this.endpointUrl}`,
      options,
      MEMORIA_PETICION_EVALUACION_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación por tipo de formulario de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionFormulario(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaBackend, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-formulario`,
      options,
      DOCUMENTACION_MEMORIA_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación por tipo de seguimiento anual de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionSeguimientoAnual(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaBackend, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-anual`,
      options,
      DOCUMENTACION_MEMORIA_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación por tipo de seguimiento final de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionSeguimientoFinal(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaBackend, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-final`,
      options,
      DOCUMENTACION_MEMORIA_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación por tipo de retrospectiva de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionRetrospectiva(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaBackend, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-retrospectiva`,
      options,
      DOCUMENTACION_MEMORIA_CONVERTER
    );
  }

  createDocumentacionInicial(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.http.post<IDocumentacionMemoriaBackend>(
      `${this.endpointUrl}/${id}/documentacion-inicial`,
      DOCUMENTACION_MEMORIA_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_CONVERTER.toTarget(response))
    );
  }

  createDocumentacionSeguimientoAnual(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.http.post<IDocumentacionMemoriaBackend>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-anual`,
      DOCUMENTACION_MEMORIA_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_CONVERTER.toTarget(response))
    );
  }

  createDocumentacionSeguimientoFinal(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.http.post<IDocumentacionMemoriaBackend>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-final`,
      DOCUMENTACION_MEMORIA_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_CONVERTER.toTarget(response))
    );
  }

  createDocumentacionRetrospectiva(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.http.post<IDocumentacionMemoriaBackend>(
      `${this.endpointUrl}/${id}/documentacion-retrospectiva`,
      DOCUMENTACION_MEMORIA_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_CONVERTER.toTarget(response))
    );
  }

  /**
   * Elimina el documento inicial de una memoria.
   *
   * @param idMemoria Identifiacdor de la memoria.
   * @param idDocumentacionMemoria Identificador del documento memoria.
   *
   * @returns observable para eliminar documentación de seguimiento anual.
   */
  deleteDocumentacionInicial(idMemoria: number, idDocumentacionMemoria: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idMemoria}/documentacion-inicial/${idDocumentacionMemoria}`);
  }

  /**
   * Devuelve todos las evaluaciones de una memoria id.
   *
   * @param memoriaId id memoria.
   */
  getEvaluacionesMemoria(memoriaId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionBackend, IEvaluacion>(
      `${this.endpointUrl}/${memoriaId}/evaluaciones`,
      options,
      EVALUACION_CONVERTER
    );
  }

  /**
   *  Devuelve los informes enviados a secretaría para su evaluación
   * @param id identificador de la memoria
   * @return listado de informes.
   */
  findInformesSecretaria(id: number): Observable<SgiRestListResult<IInforme>> {
    return this.find<IInformeBackend, IInforme>(
      `${this.endpointUrl}/${id}/informes`,
      null,
      INFORME_CONVERTER
    );
  }

  /**
   * Elimina el documento de tipo seguimiento anual de una memoria.
   *
   * @param idMemoria Identifiacdor de la memoria.
   * @param idDocumentacionMemoria Identificador del documento memoria.
   *
   * @returns observable para eliminar documentación de seguimiento anual.
   */
  deleteDocumentacionSeguimientoAnual(idMemoria: number, idDocumentacionMemoria: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idMemoria}/documentacion-seguimiento-anual/${idDocumentacionMemoria}`);
  }

  /**
   * Elimina el documento de tipo seguimiento final de una memoria.
   *
   * @param idMemoria Identifiacdor de la memoria.
   * @param idDocumentacionMemoria Identificador del documento memoria.
   *
   * @returns observable para eliminar documentación de seguimiento final.
   */
  deleteDocumentacionSeguimientoFinal(idMemoria: number, idDocumentacionMemoria: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idMemoria}/documentacion-seguimiento-final/${idDocumentacionMemoria}`);
  }

  /**
   * Elimina el documento de tipo retrospectiva de una memoria.
   *
   * @param idMemoria Identifiacdor de la memoria.
   * @param idDocumentacionMemoria Identificador del documento memoria.
   *
   * @returns observable para eliminar documentación de retrospectiva.
   */
  deleteDocumentacionRetrospectiva(idMemoria: number, idDocumentacionMemoria: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idMemoria}/documentacion-retrospectiva/${idDocumentacionMemoria}`);
  }

  /**
   * Recupera el estado anterior de la memoria
   * @param id identificador de la memoria
   */
  recuperarEstadoAnterior(id: number): Observable<IMemoria> {
    return this.http.get<IMemoriaBackend>(
      `${this.endpointUrl}/${id}/recuperar-estado-anterior`
    ).pipe(
      map(response => this.converter.toTarget(response))
    );
  }

  /**
   * Se cambia el estado de la memoria a Enviar Secretaría o Enviar Secretaría Revisión Mínima
   *
   * @param memoriaId id memoria.
   */
  enviarSecretaria(memoriaId: number): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/${memoriaId}/enviar-secretaria`, null);
  }

  /**
   * Se cambia el estado de la Retrospectiva a 'En secretaría'
   *
   * @param memoriaId id memoria.
   */
  enviarSecretariaRetrospectiva(memoriaId: number): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/${memoriaId}/enviar-secretaria-retrospectiva`, null);
  }

  /**
   * Crea una memoria del tipo modificada enviando el id de la memoria de la que se realizará la copia de datos.
   * @param memoria memoria a crear.
   * @param id identificador de la memoria de la que se parte para crear la nueva memoria.
   */
  createMemoriaModificada(memoria: IMemoria, id: number): Observable<IMemoria> {
    return this.http.post<IMemoriaBackend>(
      `${this.endpointUrl}/${id}/crear-memoria-modificada`,
      this.converter.fromTarget(memoria)
    ).pipe(
      map(response => this.converter.toTarget(response))
    );
  }

  /**
   * Obtiene la convocatoria de reunión próxima con comité indicado
   * @param idComite identificador del comite
   */
  findConvocatoriaReunionProxima(idComite: number): Observable<IConvocatoriaReunion> {
    return this.http.get<IConvocatoriaReunionBackend>(
      `${this.endpointUrl}/${idComite}/convocatoria-reunion/proxima`
    ).pipe(
      map(response => CONVOCATORIA_REUNION_CONVERTER.toTarget(response))
    );
  }

  getTiposDocumentoRespuestasFormulario(id: number): Observable<ITipoDocumento[]> {
    return this.find<IRespuestaBackend, IRespuesta>(
      `${this.endpointUrl}/${id}/respuestas-documento`,
      null,
      RESPUESTA_CONVERTER
    ).pipe(
      map(response => response.items.map(item => item.tipoDocumento))
    );
  }

  /**
   * Obtiene la última versión del informe de la memoria
   * @param idMemoria identificador de la memoria
   */
  findInformeUltimaVersion(idMemoria: number): Observable<IInforme> {
    return this.http.get<IInformeBackend>(
      `${this.endpointUrl}/${idMemoria}/informe/ultima-version`
    ).pipe(
      map(response => INFORME_CONVERTER.toTarget(response))
    );
  }

}
