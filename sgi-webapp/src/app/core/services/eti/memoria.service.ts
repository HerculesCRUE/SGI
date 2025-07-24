import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IEstadoMemoria } from '@core/models/eti/estado-memoria';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluacionWithNumComentario } from '@core/models/eti/evaluacion-with-num-comentario';
import { IInforme } from '@core/models/eti/informe';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { IRespuesta } from '@core/models/eti/respuesta';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { IEvaluacionResponse } from '@core/services/eti/evaluacion/evaluacion-response';
import { IEvaluacionWithNumComentarioResponse } from '@core/services/eti/evaluacion/evaluacion-with-num-comentario-response';
import { EVALUACION_WITH_NUM_COMENTARIO_RESPONSE_CONVERTER } from '@core/services/eti/evaluacion/evaluacion-with-num-comentario-response.converter';
import { MEMORIA_PETICION_EVALUACION_RESPONSE_CONVERTER } from '@core/services/eti/memoria/memoria-peticion-evaluacion-response.converter';
import { IMemoriaResponse } from '@core/services/eti/memoria/memoria-response';
import { MEMORIA_RESPONSE_CONVERTER } from '@core/services/eti/memoria/memoria-response.converter';
import { IRespuestaResponse } from '@core/services/eti/respuesta/respuesta-response';
import { RESPUESTA_RESPONSE_CONVERTER } from '@core/services/eti/respuesta/respuesta-response.converter';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConvocatoriaReunionResponse } from './convocatoria-reunion/convocatoria-reunion-response';
import { CONVOCATORIA_REUNION_RESPONSE_CONVERTER } from './convocatoria-reunion/convocatoria-reunion-response.converter';
import { IEstadoMemoriaResponse } from './estado-memoria/estado-memoria-response';
import { ESTADO_MEMORIA_RESPONSE_CONVERTER } from './estado-memoria/estado-memoria-response.converter';
import { EVALUACION_RESPONSE_CONVERTER } from './evaluacion/evaluacion-response.converter';
import { IDocumentacionMemoriaResponse } from './memoria/documentacion-memoria-response';
import { DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER } from './memoria/documentacion-memoria-response.converter';
import { IInformeResponse } from './memoria/informe-response';
import { INFORME_RESPONSE_CONVERTER } from './memoria/informe-response.converter';
import { IMemoriaPeticionEvaluacionResponse } from './memoria/memoria-peticion-evaluacion-response';
import { IMemoriaRequest } from './memoria/memoria-request';
import { MEMORIA_REQUEST_CONVERTER } from './memoria/memoria-request.converter';

const _MemoriaServiceMixinBase:
  CreateCtor<IMemoria, IMemoria, IMemoriaRequest, IMemoriaResponse> &
  UpdateCtor<number, IMemoria, IMemoria, IMemoriaResponse, IMemoriaResponse> &
  FindByIdCtor<number, IMemoria, IMemoriaResponse> &
  FindAllCtor<IMemoriaPeticionEvaluacion, IMemoriaPeticionEvaluacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          MEMORIA_REQUEST_CONVERTER,
          MEMORIA_RESPONSE_CONVERTER
        ),
        MEMORIA_RESPONSE_CONVERTER,
        MEMORIA_RESPONSE_CONVERTER
      ),
      MEMORIA_RESPONSE_CONVERTER
    ),
    MEMORIA_PETICION_EVALUACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class MemoriaService extends _MemoriaServiceMixinBase {
  private static readonly MAPPING = '/memorias';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${MemoriaService.MAPPING}`,
      http
    );
  }

  /**
  * Elimina la memoria
  *
  * @param idMemoria Identifiacdor de la memoria.
  */
  deleteById(idMemoria: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idMemoria}`);
  }

  /**
   * Devuelve toda la documentación asociada a una memoria según el tipo de la Evaluación
   *
   * @param id id de la memoria.
   * @param idTipoEvaluacion id del tipo de la evaluación.
   */
  getDocumentacionesTipoEvaluacion(idMemoria: number, idTipoEvaluacion: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaResponse, IDocumentacionMemoria>(
      `${this.endpointUrl}/${idMemoria}/documentaciones/${idTipoEvaluacion}`,
      options,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER
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
    return this.find<IEvaluacionWithNumComentarioResponse, IEvaluacionWithNumComentario>(
      `${this.endpointUrl}/${memoriaId}/evaluaciones-anteriores/${evaluacionId}/${tipoComentarioId}`,
      options,
      EVALUACION_WITH_NUM_COMENTARIO_RESPONSE_CONVERTER
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
    return this.find<IMemoriaResponse, IMemoria>(
      `${this.endpointUrl}/asignables/${idConvocatoria}`,
      options,
      MEMORIA_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve todos las memorias asignables para una convocatoria de tipo seguimiento
   *
   * @param options opciones de busqueda.
   * @return las memorias asignables a la convocatoria de ese tipo seguimiento.
   */
  findAllAsignablesTipoConvocatoriaSeguimiento(options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria>> {
    return this.find<IMemoriaResponse, IMemoria>(
      `${this.endpointUrl}/tipo-convocatoria-seg`,
      options,
      MEMORIA_RESPONSE_CONVERTER
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
    return this.find<IMemoriaResponse, IMemoria>(
      `${this.endpointUrl}/tipo-convocatoria-ord-ext`,
      options,
      MEMORIA_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve todas las memorias de una persona en la que es creador de la petición de evaluación
   *  o responsable de una memoria
   * @param options opciones de búsqueda
   * @return las memorias
   */
  findAllMemoriasEvaluacionByPersonaRef(options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    return this.find<IMemoriaPeticionEvaluacionResponse, IMemoriaPeticionEvaluacion>(
      `${this.endpointUrl}/persona`,
      options,
      MEMORIA_PETICION_EVALUACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación por tipo de formulario de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionFormulario(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaResponse, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-formulario`,
      options,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación por tipo de seguimiento anual de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionSeguimientoAnual(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaResponse, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-anual`,
      options,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación por tipo de seguimiento final de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionSeguimientoFinal(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaResponse, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-final`,
      options,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve toda la documentación por tipo de retrospectiva de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionRetrospectiva(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    return this.find<IDocumentacionMemoriaResponse, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-retrospectiva`,
      options,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER
    );
  }

  createDocumentacionInicial(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.post<IDocumentacionMemoriaResponse, IDocumentacionMemoriaResponse>(
      `${this.endpointUrl}/${id}/documentacion-inicial`,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  createDocumentacionInicialInvestigador(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.post<IDocumentacionMemoriaResponse, IDocumentacionMemoriaResponse>(
      `${this.endpointUrl}/${id}/documentacion-inicial/investigador`,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  createDocumentacionSeguimientoAnual(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.post<IDocumentacionMemoriaResponse, IDocumentacionMemoriaResponse>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-anual`,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  createDocumentacionSeguimientoFinal(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.post<IDocumentacionMemoriaResponse, IDocumentacionMemoriaResponse>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-final`,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  createDocumentacionRetrospectiva(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    return this.post<IDocumentacionMemoriaResponse, IDocumentacionMemoriaResponse>(
      `${this.endpointUrl}/${id}/documentacion-retrospectiva`,
      DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.fromTarget(documentacionMemoria)
    ).pipe(
      map(response => DOCUMENTACION_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
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
    return this.find<IEvaluacionResponse, IEvaluacion>(
      `${this.endpointUrl}/${memoriaId}/evaluaciones`,
      options,
      EVALUACION_RESPONSE_CONVERTER
    );
  }

  /**
   *  Devuelve los informes enviados a secretaría para su evaluación
   * @param id identificador de la memoria
   * @return listado de informes.
   */
  findInformesSecretaria(id: number): Observable<SgiRestListResult<IInforme>> {
    return this.find<IInformeResponse, IInforme>(
      `${this.endpointUrl}/${id}/informes`,
      null,
      INFORME_RESPONSE_CONVERTER
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
    return this.get<IMemoriaResponse>(
      `${this.endpointUrl}/${id}/recuperar-estado-anterior`
    ).pipe(
      map(response => MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Se cambia el estado de la memoria a Enviar Secretaría o Enviar Secretaría Revisión Mínima
   *
   * @param memoriaId id memoria.
   */
  enviarSecretaria(memoriaId: number): Observable<void> {
    return this.put<void, void>(`${this.endpointUrl}/${memoriaId}/enviar-secretaria`, null);
  }

  /**
   * Se cambia el estado de la Retrospectiva a 'En secretaría'
   *
   * @param memoriaId id memoria.
   */
  enviarSecretariaRetrospectiva(memoriaId: number): Observable<void> {
    return this.put<void, void>(`${this.endpointUrl}/${memoriaId}/enviar-secretaria-retrospectiva`, null);
  }

  /**
   * Se cambia el estado de la memoria a Subsanacion con el comnetario indicado
   *
   * @param memoriaId id memoria.
   * @param comentario un comentario
   */
  indicarSubsanacion(memoriaId: number, comentario: I18nFieldValue[]): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${memoriaId}/indicar-subsanacion`, comentario ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(comentario) : []);
  }

  /**
   * Crea una memoria del tipo modificada enviando el id de la memoria de la que se realizará la copia de datos.
   * @param memoria memoria a crear.
   * @param id identificador de la memoria de la que se parte para crear la nueva memoria.
   */
  createMemoriaModificada(memoria: IMemoria, id: number): Observable<IMemoria> {
    return this.post<IMemoriaRequest, IMemoriaResponse>(
      `${this.endpointUrl}/${id}/crear-memoria-modificada`,
      MEMORIA_REQUEST_CONVERTER.fromTarget(memoria)
    ).pipe(
      map(response => MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Obtiene la convocatoria de reunión próxima con comité indicado
   * @param idComite identificador del comite
   */
  findConvocatoriaReunionProxima(idComite: number): Observable<IConvocatoriaReunion> {
    return this.get<IConvocatoriaReunionResponse>(
      `${this.endpointUrl}/${idComite}/convocatoria-reunion/proxima`
    ).pipe(
      map(response => CONVOCATORIA_REUNION_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  getTiposDocumentoRespuestasFormulario(id: number): Observable<ITipoDocumento[]> {
    return this.find<IRespuestaResponse, IRespuesta>(
      `${this.endpointUrl}/${id}/respuestas-documento`,
      null,
      RESPUESTA_RESPONSE_CONVERTER
    ).pipe(
      map(response => response.items.map(item => item.tipoDocumento))
    );
  }

  /**
   * Obtiene la última versión del informe de la memoria
   * @param idMemoria identificador de la memoria
   */
  findInformeUltimaVersion(idMemoria: number): Observable<IInforme> {
    return this.get<IInformeResponse>(
      `${this.endpointUrl}/${idMemoria}/informe/ultima-version`
    ).pipe(
      map(response => INFORME_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Obtiene la última versión del informe de la memoria
   * @param idMemoria identificador de la memoria
   * * @param idTipoEvaluacion el identificador del tipo de evaluación
   */
  findInformeUltimaVersionTipoEvaluacion(idMemoria: number, idTipoEvaluacion: number): Observable<IInforme> {
    return this.get<IInformeResponse>(
      `${this.endpointUrl}/${idMemoria}/informe/ultima-version/tipo/${idTipoEvaluacion}`
    ).pipe(
      map(response => INFORME_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Comprobación de si están o no los documentos obligatorios aportados para
   * pasar la memoria al estado en secretaría
   *
   *  @param id Id de la memoria
   */
  checkDatosAdjuntosEnviarSecretariaExists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/enviar-secretaria`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si el usuario es responsable de la memoria  o creador de la petición de evaluación
   *
   * @param id Id de la Memoria
   */
  isResponsableOrCreador(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/responsable-creador`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Recupera el estado actual de la memoria
   * @param id identificador de la memoria
   */
  getEstadoActual(id: number): Observable<IEstadoMemoria> {
    return this.get<IEstadoMemoriaResponse>(
      `${this.endpointUrl}/${id}/estado-actual`
    ).pipe(
      map(response => ESTADO_MEMORIA_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Obtiene la ultima evaluacion de la memoria
   * 
   * @param id identificador de la memoria
   */
  getLastEvaluacionMemoria(id: number): Observable<IEvaluacion> {
    return this.get<IEvaluacionResponse>(
      `${this.endpointUrl}/${id}/last-evaluacion`
    ).pipe(
      map(response => EVALUACION_RESPONSE_CONVERTER.toTarget(response))
    );
  }


  /**
   * Comprueba si la ultima evaluacion de la memoria tiene dictamen pendiente de
   * correcciones
   *
   * @param id Id de la Memoria
   */
  isLastEvaluacionMemoriaPendienteCorrecciones(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/last-evaluacion-pendiente-correcciones`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Devuelve todas las memorias de una petición de evaluación asignables a la convocatoria
   *
   * @param idPeticionEvaluacion id petición de evaluación.
   * @return las memorias asignables a la convocatoria.
   */
  findAllMemoriasAsignablesPeticionEvaluacion(idPeticionEvaluacion: number): Observable<SgiRestListResult<IMemoria>> {
    return this.find<IMemoriaResponse, IMemoria>(
      `${this.endpointUrl}/asignables-peticion-evaluacion/${idPeticionEvaluacion}`,
      null,
      MEMORIA_RESPONSE_CONVERTER
    );
  }

  /**
   * Notifica el paso de la memoria de en secretaria revision minima a en evalucion revision minima
   * 
   * @param id Id de la Memoria
   */
  notificarRevisionMinima(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/notificar-revision-minima`, undefined);
  }


}
