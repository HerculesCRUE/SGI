package org.crue.hercules.sgi.eti.service;

import java.time.Instant;

import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Evaluacion}.
 */
public interface EvaluacionService {
  /**
   * Guardar {@link Evaluacion}.
   *
   * @param evaluacion la entidad {@link Evaluacion} a guardar.
   * @return la entidad {@link Evaluacion} persistida.
   */
  Evaluacion create(Evaluacion evaluacion);

  /**
   * Actualizar {@link Evaluacion}.
   *
   * @param evaluacion la entidad {@link Evaluacion} a actualizar.
   * @return la entidad {@link Evaluacion} persistida.
   */
  Evaluacion update(Evaluacion evaluacion);

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  Page<Evaluacion> findAll(String query, Pageable pageable);

  /**
   * Obtiene la lista de evaluaciones activas de una convocatoria reunion que no
   * estan en revisión mínima.
   * 
   * @param idConvocatoriaReunion Id de {@link ConvocatoriaReunion}.
   * @param query                 información del filtro.
   * @param paging                la información de la paginación.
   * @return la lista de entidades {@link EvaluacionWithIsEliminable} paginadas.
   */
  Page<EvaluacionWithIsEliminable> findAllByConvocatoriaReunionIdAndNoEsRevMinima(Long idConvocatoriaReunion,
      String query, Pageable paging);

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  Page<Evaluacion> findAllByConvocatoriaReunionId(Long id, Pageable pageable);

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  Page<Evaluacion> findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable);

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} para una determinada
   * {@link Memoria} anteriores al id de evaluación recibido.
   *
   * @param idMemoria        Id de {@link Memoria}.
   * @param idEvaluacion     Id de {@link Evaluacion}.
   * @param idTipoComentario Id de {@link TipoComentario}.
   * @param pageable         la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Long idTipoComentario, Pageable pageable);

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluación
   * seguimiento anual" (id = 11), "En evaluación seguimiento final" (id = 12) o
   * "En secretaría seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador
   * 
   * @param personaRef Persona Ref del {@link Evaluador}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  Page<Evaluacion> findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable);

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion} según su
   * {@link Evaluador}.
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  Page<Evaluacion> findByEvaluador(String personaRef, String query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  Page<Evaluacion> findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable pageable);

  /**
   * Obtiene {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la entidad {@link Evaluacion}.
   */
  Evaluacion findById(Long id);

  /**
   * Elimina el {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   */
  void delete(Long id) throws EvaluacionNotFoundException;

  /**
   * Elimina todos los {@link Evaluacion}.
   */
  void deleteAll();

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} con memorias en
   * determinados estados de seguimiento
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */

  Page<Evaluacion> findByEvaluacionesEnSeguimientoAnualOrFinal(String query, Pageable pageable);

  /**
   * Elimina las memorias asignadas a una convocatoria de reunión
   * 
   * @param idConvocatoriaReunion id de la {@link ConvocatoriaReunion}
   * @param idEvaluacion          id de la {@link Evaluacion}
   */
  void deleteEvaluacion(Long idConvocatoriaReunion, Long idEvaluacion);

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} de una
   * {@link Memoria}
   * 
   * @param id       Id de la {@link Memoria}
   * @param pageable pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  Page<Evaluacion> findAllByMemoriaId(Long id, Pageable pageable);

  /**
   * Identifica si el usuario es {@link Evaluador} en alguna {@link Evaluacion}
   * 
   * @param personaRef El usuario de la petición
   * @return true/false
   */
  Boolean hasAssignedEvaluacionesByEvaluador(String personaRef);

  /**
   * Identifica si el usuario es {@link Evaluador} en alguna {@link Evaluacion} en
   * Seguimiento
   * 
   * @param personaRef El usuario de la petición
   * @return true/false
   */
  Boolean hasAssignedEvaluacionesSeguimientoByEvaluador(String personaRef);

  /**
   * Identifica si el usuario es {@link Evaluador} en la {@link Evaluacion}
   * 
   * @param idEvaluacion identificador de la {@link Evaluacion}
   * @param personaRef   El usuario de la petición
   * @return true/false
   */
  Boolean isEvaluacionEvaluableByEvaluador(Long idEvaluacion, String personaRef);

  /**
   * Identifica si el usuario es {@link Evaluador} en la {@link Evaluacion} en
   * Seguimiento
   * 
   * @param idEvaluacion identificador de la {@link Evaluacion} en Seguimiento
   * @param personaRef   El usuario de la petición
   * @return true/false
   */
  Boolean isEvaluacionSeguimientoEvaluableByEvaluador(Long idEvaluacion, String personaRef);

  /**
   * Retorna el identificador de la usuarioRef del presidente
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return id del presidente
   */
  String findIdPresidenteByIdEvaluacion(Long idEvaluacion);

  /**
   * Retorna la primera fecha de envío a secretaría (histórico estado)
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return fecha de envío a secretaría
   */
  Instant findFirstFechaEnvioSecretariaByIdEvaluacion(Long idEvaluacion);

  DocumentoOutput generarDocumentoEvaluacion(Long idEvaluacion);

  /**
   * Obtiene el documento de la ficha del Evaluador
   * 
   * @param idEvaluacion id {@link Evaluacion}
   * @return El documento del informe de la ficha del Evaluador
   */
  public DocumentoOutput generarDocumentoEvaluador(Long idEvaluacion);

  /**
   * 
   * Envia comunicados de aviso para informes con seguimiento anual
   * pendiente
   * 
   */
  void sendComunicadoInformeSeguimientoAnualPendiente();

  /**
   * Obtiene el secretario activo de la fecha de evaluación
   * 
   * @param idEvaluacion id de la {@link Evaluacion}
   * @return secretario de la evaluación
   */
  Evaluador findSecretarioEvaluacion(Long idEvaluacion);

  /**
   * Obtiene la ultima evaluacion de la memoria
   * 
   * @param memoriaId identificador de la {@link Memoria}
   * @return la evaluacion
   */
  Evaluacion getLastEvaluacionMemoria(Long memoriaId);

  /**
   * Comprueba si la ultima evaluacion de la memoria tiene dictamen pendiente de
   * correcciones
   * 
   * @param memoriaId identificador de la {@link Memoria}
   * @return true si la ultima evaluacion tiene dictamen pendiente de correcciones
   *         / false si no lo tiene
   */
  boolean isLastEvaluacionMemoriaPendienteCorrecciones(Long memoriaId);

}
