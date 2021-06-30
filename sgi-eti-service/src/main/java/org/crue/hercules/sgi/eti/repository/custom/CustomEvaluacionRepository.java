package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * CustomEvaluacionRepository
 */
@Component
public interface CustomEvaluacionRepository {

  /**
   * Obtener todas las entidades {@link EvaluacionWithNumComentario} paginadas
   * asociadas a una memoria y anteriores a la evaluación recibida.
   *
   * @param idMemoria        id de la memoria.
   * @param idEvaluacion     id de la evaluación.
   * @param idTipoComentario id del tipo de comentario.
   * @param idTipoEvaluacion id del tipo de evaluación.
   * @param pageable         la información de la paginación.
   * 
   * @return la lista de entidades {@link EvaluacionWithNumComentario} paginadas
   *         y/o filtradas.
   */
  Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Long idTipoComentario, Long idTipoEvaluacion, Pageable pageable);

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas.
   * 
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */

  Page<Evaluacion> findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas asociadas a un
   * evaluador
   *
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  Page<Evaluacion> findByEvaluador(String personaRef, String query, Pageable pageable);

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
   * Obtener todas las entidades {@link Evaluacion} paginadas asociadas a
   * determinados tipos de seguimiento final
   *
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */

  public Page<Evaluacion> findByEvaluacionesEnSeguimientoFinal(String query, Pageable pageable);

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
}
