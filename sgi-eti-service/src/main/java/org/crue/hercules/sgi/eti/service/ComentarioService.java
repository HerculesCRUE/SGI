package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Comentario}.
 */
public interface ComentarioService {

  /**
   * Guardar un {@link Comentario} de {@link TipoComentario} "GESTOR" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a guardar.
   * @return lista de entidades {@link Comentario} persistida.
   */
  Comentario createComentarioGestor(Long evaluacionId, Comentario comentario);

  /**
   * Guardar un {@link Comentario} de {@link TipoComentario} "EVALUADOR" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a guardar.
   * @param personaRef   Usuario logueado
   * @return lista de entidades {@link Comentario} persistida.
   */
  Comentario createComentarioEvaluador(Long evaluacionId, Comentario comentario, String personaRef);

  /**
   * Actualizar un {@link Comentario} de tipo "GESTOR" de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a actualizar.
   * @return {@link Comentario} actualizado.
   */
  Comentario updateComentarioGestor(Long evaluacionId, Comentario comentario);

  /**
   * Actualizar un {@link Comentario} de tipo "EVALUADOR" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a actualizar.
   * @param personaRef   Usuario logueado
   * @return {@link Comentario} actualizado.
   */
  Comentario updateComentarioEvaluador(Long evaluacionId, Comentario comentario, String personaRef);

  /**
   * Obtiene {@link Comentario} por id.
   *
   * @param id el id de la entidad {@link Comentario}.
   * @return la entidad {@link Comentario}.
   */
  Comentario findById(Long id);

  /**
   * Obtiene todos los {@link Comentario} del tipo "GESTOR" por el id de su
   * evaluación.
   *
   * @param id       el id de la entidad {@link Evaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  Page<Comentario> findByEvaluacionIdGestor(Long id, Pageable pageable);

  /**
   * Obtiene todos los {@link Comentario} del tipo "EVALUADOR" por el id de su
   * evaluación.
   *
   * @param id         el id de la entidad {@link Evaluacion}.
   * @param pageable   la información de la paginación.
   * @param personaRef Usuario logueado
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  Page<Comentario> findByEvaluacionIdEvaluador(Long id, Pageable pageable, String personaRef);

  /**
   * Elimina un {@link Comentario} de tipo "GESTOR" de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de {@link Evaluacion}
   * @param idComentario Id de {@link Comentario}
   */
  void deleteComentarioGestor(Long evaluacionId, Long idComentario) throws ComentarioNotFoundException;

  /**
   * Elimina un {@link Comentario} de tipo "EVALUADOR" de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de {@link Evaluacion}
   * @param idComentario Id de {@link Comentario}
   * @param personaRef   Usuario logeado
   */
  void deleteComentarioEvaluador(Long evaluacionId, Long idComentario, String personaRef)
      throws ComentarioNotFoundException;

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion}.
   * 
   * @param id Id de {@link Evaluacion}.
   * @return número de {@link Comentario}
   */
  int countByEvaluacionId(Long id);

}