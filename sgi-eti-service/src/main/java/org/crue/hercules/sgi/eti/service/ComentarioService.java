package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comentario.TipoEstadoComentario;
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
   * Guardar un {@link Comentario} de {@link TipoComentario} "ACTA" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a guardar.
   * @return lista de entidades {@link Comentario} persistida.
   */
  Comentario createComentarioActaGestor(Long evaluacionId, Comentario comentario);

  /**
   * Guardar un {@link Comentario} de {@link TipoComentario} "ACTA" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a guardar.
   * @param personaRef   Usuario logueado
   * @return lista de entidades {@link Comentario} persistida.
   */
  Comentario createComentarioActaEvaluador(Long evaluacionId, Comentario comentario, String personaRef);

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
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findByEvaluacionIdGestor(Long id);

  /**
   * Obtiene todos los {@link Comentario} del tipo "EVALUADOR" por el id de su
   * evaluación.
   *
   * @param id         el id de la entidad {@link Evaluacion}.
   * @param personaRef Usuario logueado
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findByEvaluacionIdEvaluador(Long id, String personaRef);

  /**
   * Obtiene todos los {@link Comentario} del tipo "ACTA_GESTOR" por el id de su
   * evaluación.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findByEvaluacionIdActaGestor(Long id);

  /**
   * Obtiene todos los {@link Comentario} del tipo "ACTA_EVALUADOR" por el id de
   * su
   * evaluación.
   *
   * @param id         el id de la entidad {@link Evaluacion}.
   * @param personaRef Id de la persona
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findByEvaluacionIdActaEvaluador(Long id, String personaRef);

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
   * Elimina un {@link Comentario} de tipo "ACTA_GESTOR" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId Id de {@link Evaluacion}
   * @param idComentario Id de {@link Comentario}
   */
  void deleteComentarioActaGestor(Long evaluacionId, Long idComentario) throws ComentarioNotFoundException;

  /**
   * Elimina un {@link Comentario} de tipo "ACTA" de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de {@link Evaluacion}
   * @param idComentario Id de {@link Comentario}
   * @param personaRef   identificador usuario
   */
  void deleteComentarioActaEvaluador(Long evaluacionId, Long idComentario, String personaRef)
      throws ComentarioNotFoundException;

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion}.
   * 
   * @param id Id de {@link Evaluacion}.
   * @return número de {@link Comentario}
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
   * Identifica si los {@link Comentario} en la {@link Evaluacion} han sido
   * enviados
   * 
   * @param idEvaluacion identificador de la {@link Evaluacion}
   * @param personaRef   El usuario de la petición
   * @return true/false
   */
  boolean isComentariosEvaluadorEnviados(Long idEvaluacion, String personaRef);

  /**
   * Obtiene el número total de {@link Comentario} de otros evaluadores distintos
   * al que realiza la petición para una determinada {@link Evaluacion} y un
   * {@link TipoEstadoComentario} Abierto
   * 
   * @param id         Id de {@link Evaluacion}.
   * @param personaRef Persona creadora del comentario
   * @return número de {@link Comentario}
   */
  boolean isPosibleEnviarComentarios(Long id, String personaRef);

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion}, un tipo de comentario {@link TipoComentario}
   * y un {@link TipoEstadoComentario} Abierto
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @return número de {@link Comentario}
   */
  int countByEvaluacionIdAndTipoComentarioIdAndEstadoAbierto(Long id, Long idTipoComentario);

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion}, un tipo de comentario {@link TipoComentario}
   * y un {@link TipoEstadoComentario} Cerrado
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @return número de {@link Comentario}
   */
  int countByEvaluacionIdAndTipoComentarioIdAndEstadoCerrado(Long id, Long idTipoComentario);

  /**
   * Permite enviar los comentarios de {@link Evaluacion} y persona
   *
   * @param id         Id del {@link Evaluacion}.
   * @param personaRef referencia de la persona de los {@link Comentario}
   * @return true si puede ser enviado / false si no puede ser enviado
   */
  boolean enviarByEvaluacion(Long id, String personaRef);

  /**
   * Obtiene todos los {@link Comentario} del tipo "EVALUADOR" por el id de su
   * evaluación y la persona creadora del comentario
   *
   * @param id         el id de la entidad {@link Evaluacion}.
   * @param personaRef Usuario logueado
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findComentariosEvaluadorByPersonaRef(Long id, String personaRef);

  /**
   * Obtiene todos los {@link Comentario} del tipo "EVALUADOR" por el id de su
   * evaluación y estado cerrado
   *
   * @param id       el id de la entidad {@link Evaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  Page<Comentario> findByEvaluacionEvaluadorAndEstadoCerrado(Long id, Pageable pageable);

  /**
   * Permite enviar los comentarios de las {@link Evaluacion} del {@link Acta} y
   * persona
   *
   * @param id         Id del {@link Evaluacion}.
   * @param personaRef referencia de la persona de los {@link Comentario}
   * @return true si puede ser enviado / false si no puede ser enviado
   */
  boolean enviarByEvaluacionActa(Long id, String personaRef);

  /**
   * Obtiene todos los {@link Comentario} del tipo "ACTA" por el id de su
   * evaluación y la persona creadora del comentario
   *
   * @param id         el id de la entidad {@link Acta}.
   * @param personaRef Usuario logueado
   * @return la lista de entidades {@link Comentario}.
   */
  List<Comentario> findComentariosActaByPersonaRef(Long id, String personaRef);

}