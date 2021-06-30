package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Comentario;
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
   * @param pageable         la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  Page<Comentario> findByEvaluacionIdAndTipoComentarioId(Long idEvaluacion, Long idTipoComentario, Pageable pageable);

  /**
   * Obtiene el número total de {@link Comentario} para un determinado
   * {@link Evaluacion}.
   * 
   * @param id Id de {@link Evaluacion}.
   * @return número de entidades {@link Comentario}
   */
  int countByEvaluacionId(Long id);

}