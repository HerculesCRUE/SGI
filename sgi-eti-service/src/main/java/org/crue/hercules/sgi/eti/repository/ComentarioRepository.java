package org.crue.hercules.sgi.eti.repository;

import java.util.List;

import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comentario.TipoEstadoComentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Comentario}.
 */

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long>, JpaSpecificationExecutor<Comentario> {

  /**
   * Obtener todas las entidades paginadas {@link Comentario} para un determinado
   * {@link Evaluacion} y {@link TipoComentario}.
   *
   * @param idEvaluacion     Id de {@link Evaluacion}.
   * @param idTipoComentario Id de {@link TipoComentario}.
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findByEvaluacionIdAndTipoComentarioId(Long idEvaluacion, Long idTipoComentario);

  /**
   * Obtener todas las entidades paginadas {@link Comentario} para un determinado
   * {@link Evaluacion} , {@link TipoComentario} y personaRef
   *
   * @param idEvaluacion     Id de {@link Evaluacion}.
   * @param idTipoComentario Id de {@link TipoComentario}.
   * @param personaRef       referencia de la persona
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findByEvaluacionIdAndTipoComentarioIdAndCreatedBy(Long idEvaluacion, Long idTipoComentario,
      String personaRef);

  /**
   * Obtener todas las entidades paginadas {@link Comentario} para un determinado
   * {@link Evaluacion}, {@link TipoComentario}, {@link TipoEstadoComentario} y
   * otras personaRef
   *
   * @param idEvaluacion     Id de {@link Evaluacion}.
   * @param idTipoComentario Id de {@link TipoComentario}.
   * @param personaRef       referencia de la persona
   * @param estado           estado {@link TipoEstadoComentario}
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findByEvaluacionIdAndTipoComentarioIdAndCreatedByNotAndEstado(Long idEvaluacion,
      Long idTipoComentario, String personaRef, TipoEstadoComentario estado);

  /**
   * Obtiene el número total de {@link Comentario} para un determinado
   * {@link Evaluacion}.
   * 
   * @param id Id de {@link Evaluacion}.
   * @return número de entidades {@link Comentario}
   */
  int countByEvaluacionId(Long id);

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion} y un tipo de comentario {@link TipoComentario}.
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @return número de {@link Comentario}
   */
  int countByEvaluacionIdAndTipoComentarioId(Long id, Long idTipoComentario);

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion}, un tipo de comentario {@link TipoComentario} y un
   * identificador de persona
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @param personaRef       identificador persona
   * @return número de {@link Comentario}
   */
  int countByEvaluacionIdAndTipoComentarioIdAndCreatedBy(Long id, Long idTipoComentario, String personaRef);

  /**
   * Identifica si los {@link Comentario} en la {@link Evaluacion} han sido
   * enviados
   * 
   * @param idEvaluacion         identificador de la {@link Evaluacion}
   * @param idTipoComentario     el identificador dle {@link TipoComentario}
   * @param tipoEstadoComentario el estado del {@link Comentario}
   * @param personaRef           El usuario de la petición
   * @return true/false
   */
  boolean existsByEvaluacionIdAndTipoComentarioIdAndEstadoAndCreatedBy(Long idEvaluacion, Long idTipoComentario,
      TipoEstadoComentario tipoEstadoComentario, String personaRef);

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion}, un tipo de comentario {@link TipoComentario}
   * y un {@link TipoEstadoComentario}
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @param estado           estado de {@link TipoEstadoComentario}.
   * @return número de {@link Comentario}
   */
  int countByEvaluacionIdAndTipoComentarioIdAndEstado(Long id, Long idTipoComentario,
      TipoEstadoComentario estado);

  /**
   * Obtiene el número total de {@link Comentario} de otros evaluadores distintos
   * al que realiza la petición para una determinada {@link Evaluacion} y un
   * {@link TipoEstadoComentario} Abierto
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @param personaRef       Persona creadora del comentario
   * @param estado           estado de {@link TipoEstadoComentario}.
   * @return número de {@link Comentario}
   */
  int countByEvaluacionIdAndTipoComentarioIdAndCreatedByAndEstado(Long id, Long idTipoComentario,
      String personaRef, TipoEstadoComentario estado);

  /**
   * Obtener todas las entidades paginadas {@link Comentario} para un determinado
   * {@link Evaluacion} y {@link TipoComentario}.
   *
   * @param idEvaluacion         Id de {@link Evaluacion}.
   * @param idTipoComentario     Id de {@link TipoComentario}.
   * @param tipoEstadoComentario estado {@link TipoEstadoComentario}.
   * @param pageable             la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  Page<Comentario> findByEvaluacionIdAndTipoComentarioIdAndEstado(Long idEvaluacion, Long idTipoComentario,
      TipoEstadoComentario tipoEstadoComentario, Pageable pageable);

  /**
   * Obtener todas las entidades {@link Comentario} para un determinado
   * {@link Evaluacion} y {@link TipoComentario}.
   *
   * @param idEvaluacion         Id de {@link Evaluacion}.
   * @param idTipoComentario     Id de {@link TipoComentario}.
   * @param tipoEstadoComentario estado {@link TipoEstadoComentario}.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  List<Comentario> findByEvaluacionIdAndTipoComentarioIdAndEstado(Long idEvaluacion, Long idTipoComentario,
      TipoEstadoComentario tipoEstadoComentario);
}