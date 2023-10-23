package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comentario.TipoEstadoComentario;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.EvaluadorRepository;
import org.crue.hercules.sgi.eti.repository.specification.EvaluadorSpecifications;
import org.crue.hercules.sgi.eti.service.ComentarioService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Comentario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ComentarioServiceImpl implements ComentarioService {
  private static final String MSG_EL_ID_DE_LA_EVALUACION_NO_PUEDE_SER_NULO_PARA_LISTAR_SUS_COMENTARIOS = "El id de la evaluación no puede ser nulo para listar sus comentarios";
  private static final String MSG_COMENTARIO_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO = "Comentario id no puede ser null para eliminar un comentario.";
  private static final String MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO = "Evaluación id no puede ser null para eliminar un comentario.";
  private static final String MSG_EL_USUARIO_NO_COINCIDE_CON_NINGUNO_DE_LOS_EVALUADORES_DE_LA_EVALUACION = "El usuario no coincide con ninguno de los Evaluadores de la Evaluación.";
  private static final String MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_CREAR_UN_NUEVO_COMENTARIO = "Evaluación id no puede ser null para crear un nuevo comentario.";

  private final ComentarioRepository comentarioRepository;
  private final EvaluacionRepository evaluacionRepository;
  private final EvaluadorRepository evaluadorRepository;
  private final ActaRepository actaRepository;

  public ComentarioServiceImpl(ComentarioRepository comentarioRepository, EvaluacionRepository evaluacionRepository,
      EvaluadorRepository evaluadorRepository, ActaRepository actaRepository) {
    this.comentarioRepository = comentarioRepository;
    this.evaluacionRepository = evaluacionRepository;
    this.evaluadorRepository = evaluadorRepository;
    this.actaRepository = actaRepository;
  }

  /**
   * Guardar un {@link Comentario} de {@link TipoComentario} "GESTOR" de una
   * {@link Evaluacion}.
   * 
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a guardar.
   * @return lista de entidades {@link Comentario} persistida.
   */
  @Override
  @Transactional
  public Comentario createComentarioGestor(Long evaluacionId, Comentario comentario) {
    log.debug("createComentarioGestor(Long evaluacionId, Comentario comentario) - start");

    Assert.notNull(evaluacionId, MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_CREAR_UN_NUEVO_COMENTARIO);

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getId(),
          comentario.getApartado().getBloque());

      validateEstadoEvaluacion(evaluacion);

      log.debug("createComentarioGestor(Long evaluacionId, Comentario comentario) - end");

      return createComentarioEvaluacion(evaluacionId, comentario, TipoComentario.Tipo.GESTOR.getId());

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

  }

  /**
   * Guardar un {@link Comentario} de {@link TipoComentario} "EVALUADOR" de una
   * {@link Evaluacion}.
   * 
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a guardar.
   * @return lista de entidades {@link Comentario} persistida.
   */
  @Override
  @Transactional
  public Comentario createComentarioEvaluador(Long evaluacionId, Comentario comentario, String personaRef) {
    log.debug("createComentarioEvaluador(Long evaluacionId, Comentario comentario) - start");

    Assert.notNull(evaluacionId, MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_CREAR_UN_NUEVO_COMENTARIO);

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getId(),
          comentario.getApartado().getBloque());

      Assert.isTrue(
          (evaluacion.getEvaluador1().getPersonaRef()).equals(personaRef)
              || evaluacion.getEvaluador2().getPersonaRef().equals(personaRef) || isMiembroComite(personaRef,
                  evaluacion.getMemoria().getComite()
                      .getComite()),
          MSG_EL_USUARIO_NO_COINCIDE_CON_NINGUNO_DE_LOS_EVALUADORES_DE_LA_EVALUACION);

      comentario.setEstado(TipoEstadoComentario.ABIERTO);
      comentario.setFechaEstado(Instant.now());
      log.debug("createComentarioEvaluador(Long evaluacionId, Comentario comentario) - end");
      return createComentarioEvaluacion(evaluacionId, comentario, TipoComentario.Tipo.EVALUADOR.getId());

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

  }

  /**
   * Comprueba si la persona es miembro activo del comité
   * 
   * @param personaRef identificador de la persona
   * @param comite     nombre del comité
   * @return true si es miembro activo del comité / false si no lo es
   */
  private Boolean isMiembroComite(String personaRef, String comite) {
    Specification<Evaluador> specActivos = EvaluadorSpecifications.activos();
    Specification<Evaluador> specPersonaRef = EvaluadorSpecifications.byPersonaRef(personaRef);
    Specification<Evaluador> specComite = EvaluadorSpecifications.byComite(comite);

    Specification<Evaluador> specs = Specification.where(specActivos).and(specPersonaRef).and(specComite);

    List<Evaluador> returnValue = evaluadorRepository.findAll(specs);
    return !returnValue.isEmpty();

  }

  /**
   * Guardar un {@link Comentario} de {@link TipoComentario} "ACTA_GESTOR" de una
   * {@link Evaluacion}.
   * 
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a guardar.
   * @return lista de entidades {@link Comentario} persistida.
   */
  @Override
  @Transactional
  public Comentario createComentarioActaGestor(Long evaluacionId, Comentario comentario) {
    log.debug("createComentarioActaGestor(Long evaluacionId, Comentario comentario) - start");

    Assert.notNull(evaluacionId, MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_CREAR_UN_NUEVO_COMENTARIO);

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getId(),
          comentario.getApartado().getBloque());

      validateEstadoEvaluacion(evaluacion);

      log.debug("createComentarioActaGestor(Long evaluacionId, Comentario comentario) - end");

      return createComentarioEvaluacion(evaluacionId, comentario, TipoComentario.Tipo.ACTA_GESTOR.getId());

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

  }

  /**
   * Guardar un {@link Comentario} de {@link TipoComentario} "ACTA_EVALUADOR" de
   * una
   * {@link Evaluacion}.
   * 
   * @param evaluacionId Id de la evaluación
   * @param comentario   {@link Comentario} a guardar.
   * @return lista de entidades {@link Comentario} persistida.
   */
  @Override
  @Transactional
  public Comentario createComentarioActaEvaluador(Long evaluacionId, Comentario comentario, String personaRef) {
    log.debug("createComentarioActa(Long evaluacionId, Comentario comentario) - start");

    Assert.notNull(evaluacionId, MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_CREAR_UN_NUEVO_COMENTARIO);

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getId(),
          comentario.getApartado().getBloque());

      validateEstadoEvaluacion(evaluacion);

      comentario.setEstado(TipoEstadoComentario.ABIERTO);
      comentario.setFechaEstado(Instant.now());

      log.debug("createComentarioActa(Long evaluacionId, Comentario comentario) - end");

      return createComentarioEvaluacion(evaluacionId, comentario, TipoComentario.Tipo.ACTA_EVALUADOR.getId());

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

  }

  /**
   * Obtiene una entidad {@link Comentario} por id.
   *
   * @param id el id de la entidad {@link Comentario}.
   * @return la entidad {@link Comentario}.
   * @throws ComentarioNotFoundException Si no existe ningún {@link Comentario}
   *                                     con ese id.
   */
  public Comentario findById(final Long id) throws ComentarioNotFoundException {
    log.debug("Petición a get Comentario : {}  - start", id);
    final Comentario comentario = comentarioRepository.findById(id)
        .orElseThrow(() -> new ComentarioNotFoundException(id));
    log.debug("Petición a get Comentario : {}  - end", id);
    return comentario;

  }

  /**
   * Elimina un {@link Comentario} de tipo "GESTOR" de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de {@link Evaluacion}
   * @param comentarioId Id de {@link Comentario}
   */
  @Override
  @Transactional
  public void deleteComentarioGestor(Long evaluacionId, Long comentarioId) throws ComentarioNotFoundException {
    log.debug("deleteComentarioGestor(Long evaluacionId, Long comentarioId) - start");

    Assert.notNull(evaluacionId, MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO);
    Assert.notNull(comentarioId, MSG_COMENTARIO_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO);

    Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
        .orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

    validateEstadoEvaluacion(evaluacion);

    deleteComentarioEvaluacion(evaluacionId, comentarioId, TipoComentario.Tipo.GESTOR.getId());

    log.debug("deleteComentarioGestor(Long evaluacionId, Long comentarioId) - end");

  }

  /**
   * Elimina un {@link Comentario} de tipo "EVALUADOR" de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de {@link Evaluacion}
   * @param comentarioId Id de {@link Comentario}
   */
  @Override
  @Transactional
  public void deleteComentarioEvaluador(Long evaluacionId, Long comentarioId, String personaRef)
      throws ComentarioNotFoundException {
    log.debug("deleteComentarioEvaluador(Long evaluacionId, Long comentarioId) - start");

    Assert.notNull(evaluacionId, MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO);
    Assert.notNull(comentarioId, MSG_COMENTARIO_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO);

    Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
        .orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

    Assert.isTrue(
        (evaluacion.getEvaluador1().getPersonaRef()).equals(personaRef)
            || evaluacion.getEvaluador2().getPersonaRef().equals(personaRef) || isMiembroComite(personaRef,
                evaluacion.getMemoria().getComite()
                    .getComite()),
        MSG_EL_USUARIO_NO_COINCIDE_CON_NINGUNO_DE_LOS_EVALUADORES_DE_LA_EVALUACION);

    deleteComentarioEvaluacion(evaluacionId, comentarioId, TipoComentario.Tipo.EVALUADOR.getId());

    log.debug("deleteComentarioEvaluador(Long evaluacionId, Long comentarioId) - end");
  }

  /**
   * Elimina un {@link Comentario} de tipo "ACTA_GESTOR" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId Id de {@link Evaluacion}
   * @param comentarioId Id de {@link Comentario}
   */
  @Override
  @Transactional
  public void deleteComentarioActaGestor(Long evaluacionId, Long comentarioId) throws ComentarioNotFoundException {
    log.debug("deleteComentarioActaGestor(Long evaluacionId, Long comentarioId) - start");

    Assert.notNull(evaluacionId, MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO);
    Assert.notNull(comentarioId, MSG_COMENTARIO_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO);

    if (!evaluacionRepository.existsById(evaluacionId)) {
      throw new EvaluacionNotFoundException(evaluacionId);
    }

    deleteComentarioEvaluacion(evaluacionId, comentarioId, TipoComentario.Tipo.ACTA_GESTOR.getId());

    log.debug("deleteComentarioActaGestor(Long evaluacionId, Long comentarioId) - end");
  }

  /**
   * Elimina un {@link Comentario} de tipo "ACTA_EVALUADOR" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId Id de {@link Evaluacion}
   * @param comentarioId Id de {@link Comentario}
   */
  @Override
  @Transactional
  public void deleteComentarioActaEvaluador(Long evaluacionId, Long comentarioId, String personaRef)
      throws ComentarioNotFoundException {
    log.debug("deleteComentarioActaEvaluador(Long evaluacionId, Long comentarioId) - start");

    Assert.notNull(evaluacionId, MSG_EVALUACION_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO);
    Assert.notNull(comentarioId, MSG_COMENTARIO_ID_NO_PUEDE_SER_NULL_PARA_ELIMINAR_UN_COMENTARIO);

    if (!evaluacionRepository.existsById(evaluacionId)) {
      throw new EvaluacionNotFoundException(evaluacionId);
    }

    deleteComentarioEvaluacion(evaluacionId, comentarioId, TipoComentario.Tipo.ACTA_EVALUADOR.getId());

    log.debug("deleteComentarioActaEvaluador(Long evaluacionId, Long comentarioId) - end");
  }

  /**
   * Actualizar un {@link Comentario} del tipo "GESTOR" de una {@link Evaluacion}.
   *
   * @param evaluacionId         Id de la evaluación
   * @param comentarioActualizar {@link Comentario} a actualizar.
   * @return {@link Comentario} actualizado.
   */
  @Override
  @Transactional
  public Comentario updateComentarioGestor(Long evaluacionId, Comentario comentarioActualizar) {
    log.debug("updateComentarioGestor(Long evaluacionId, Comentario comentarioActualizar) - start");

    Assert.notNull(evaluacionId, "Evaluación id no puede ser null  para actualizar un comentario.");
    Assert.isTrue(comentarioActualizar.getTipoComentario().getTipo().equals(TipoComentario.Tipo.GESTOR),
        "No se puede actualizar un tipo de comentario que no sea del tipo Gestor.");

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getId(),
          comentarioActualizar.getApartado().getBloque());

      validateEstadoEvaluacion(evaluacion);

      log.debug("updateComentarioGestor(Long evaluacionId, Comentario comentarioActualizar) - end");
      return updateComentarioEvaluacion(evaluacionId, comentarioActualizar);

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

  }

  /**
   * Actualizar un {@link Comentario} del tipo "EVALUADOR" de una
   * {@link Evaluacion}.
   *
   * @param evaluacionId         Id de la evaluación
   * @param comentarioActualizar {@link Comentario} a actualizar.
   * @return {@link Comentario} actualizado.
   */
  @Override
  @Transactional
  public Comentario updateComentarioEvaluador(Long evaluacionId, Comentario comentarioActualizar, String personaRef) {
    log.debug("updateComentarioEvaluador(Long evaluacionId, Comentario comentarioActualizar) - start");

    Assert.notNull(evaluacionId, "Evaluación id no puede ser null  para actualizar un comentario.");
    Assert.isTrue(comentarioActualizar.getTipoComentario().getTipo().equals(TipoComentario.Tipo.EVALUADOR),
        "No se puede actualizar un tipo de comentario que no sea del tipo Evaluador.");

    log.debug("updateComentarioEvaluador(Long evaluacionId, Comentario comentarioActualizar) - end");

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getId(),
          comentarioActualizar.getApartado().getBloque());

      Assert.isTrue(
          (evaluacion.getEvaluador1().getPersonaRef()).equals(personaRef)
              || evaluacion.getEvaluador2().getPersonaRef().equals(personaRef) || isMiembroComite(personaRef,
                  evaluacion.getMemoria().getComite()
                      .getComite()),
          MSG_EL_USUARIO_NO_COINCIDE_CON_NINGUNO_DE_LOS_EVALUADORES_DE_LA_EVALUACION);

      return updateComentarioEvaluacion(evaluacionId, comentarioActualizar);

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));
  }

  /**
   * Obtiene todos los {@link Comentario} del tipo "GESTOR" por el id de su
   * evaluación.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la lista de entidades {@link Comentario}.
   */
  @Override
  public List<Comentario> findByEvaluacionIdGestor(Long id) {
    log.debug("findByEvaluacionIdGestor(Long id) - start");
    Assert.notNull(id, MSG_EL_ID_DE_LA_EVALUACION_NO_PUEDE_SER_NULO_PARA_LISTAR_SUS_COMENTARIOS);

    return evaluacionRepository.findById(id).map(evaluacion -> {
      List<Comentario> returnValue = comentarioRepository.findByEvaluacionIdAndTipoComentarioId(id,
          TipoComentario.Tipo.GESTOR.getId());
      log.debug("findByEvaluacionIdGestor(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluacionNotFoundException(id));

  }

  /**
   * Obtiene todos los {@link Comentario} del tipo "EVALUADOR" por el id de su
   * evaluación.
   *
   * @param id         el id de la entidad {@link Evaluacion}.
   * @param personaRef referencia de la persona
   * @return la lista de entidades {@link Comentario}.
   */
  @Override
  public List<Comentario> findByEvaluacionIdEvaluador(Long id, String personaRef) {
    log.debug("findByEvaluacionIdEvaluador(Long id, String personaRef) - start");
    return evaluacionRepository.findById(id).map(evaluacion -> {
      List<Comentario> comentarios = new ArrayList<Comentario>();
      List<Comentario> comentariosUsuarioEvaluador = comentarioRepository
          .findByEvaluacionIdAndTipoComentarioIdAndCreatedBy(id,
              TipoComentario.Tipo.EVALUADOR.getId(), personaRef);
      if (!comentariosUsuarioEvaluador.isEmpty()) {
        comentarios.addAll(comentariosUsuarioEvaluador);
      }
      List<Comentario> comentariosCerradosOtrosUsuarios = comentarioRepository
          .findByEvaluacionIdAndTipoComentarioIdAndCreatedByNotAndEstado(id, TipoComentario.Tipo.EVALUADOR.getId(),
              personaRef, TipoEstadoComentario.CERRADO);
      if (!comentariosCerradosOtrosUsuarios.isEmpty()) {
        comentarios.addAll(comentariosCerradosOtrosUsuarios);
      }
      log.debug("findByEvaluacionIdEvaluador(Long id, String personaRef) - end");
      return comentarios;
    }).orElseThrow(() -> new EvaluacionNotFoundException(id));

  }

  /**
   * Obtiene todos los {@link Comentario} del tipo "ACTA_GESTOR" por el id de su
   * evaluación.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la lista de entidades {@link Comentario}.
   */
  @Override
  public List<Comentario> findByEvaluacionIdActaGestor(Long id) {
    log.debug("findByEvaluacionIdActaGestor(Long id) - start");
    Assert.notNull(id, MSG_EL_ID_DE_LA_EVALUACION_NO_PUEDE_SER_NULO_PARA_LISTAR_SUS_COMENTARIOS);

    return evaluacionRepository.findById(id).map(evaluacion -> {
      List<Comentario> comentarios = new ArrayList<Comentario>();
      List<Comentario> comentariosGestores = comentarioRepository.findByEvaluacionIdAndTipoComentarioId(id,
          TipoComentario.Tipo.ACTA_GESTOR.getId());
      if (!comentariosGestores.isEmpty()) {
        comentarios.addAll(comentariosGestores);
      }
      List<Comentario> comentariosEvaluadoresCerrados = comentarioRepository
          .findByEvaluacionIdAndTipoComentarioIdAndEstado(id,
              TipoComentario.Tipo.ACTA_EVALUADOR.getId(), TipoEstadoComentario.CERRADO);
      if (!comentariosEvaluadoresCerrados.isEmpty()) {
        comentarios.addAll(comentariosEvaluadoresCerrados);
      }
      log.debug("findByEvaluacionIdActaGestor(Long id) - end");
      return comentarios;
    }).orElseThrow(() -> new EvaluacionNotFoundException(id));

  }

  /**
   * Obtiene todos los {@link Comentario} del tipo "ACTA_EVALUADOR" por el id de
   * su
   * evaluación.
   *
   * @param id         el id de la entidad {@link Evaluacion}.
   * @param personaRef identificador del usuario
   * @return la lista de entidades {@link Comentario}.
   */
  @Override
  public List<Comentario> findByEvaluacionIdActaEvaluador(Long id, String personaRef) {
    log.debug("findByEvaluacionIdActaEvaluador(Long id, String personaRef) - start");
    Assert.notNull(id, MSG_EL_ID_DE_LA_EVALUACION_NO_PUEDE_SER_NULO_PARA_LISTAR_SUS_COMENTARIOS);

    return evaluacionRepository.findById(id).map(evaluacion -> {
      List<Comentario> comentarios = new ArrayList<Comentario>();
      List<Comentario> comentariosUsuarioEvaluador = comentarioRepository
          .findByEvaluacionIdAndTipoComentarioIdAndCreatedBy(id,
              TipoComentario.Tipo.ACTA_EVALUADOR.getId(), personaRef);
      if (!comentariosUsuarioEvaluador.isEmpty()) {
        comentarios.addAll(comentariosUsuarioEvaluador);
      }
      List<Comentario> comentariosCerradosOtrosUsuarios = comentarioRepository
          .findByEvaluacionIdAndTipoComentarioIdAndCreatedByNotAndEstado(id, TipoComentario.Tipo.ACTA_EVALUADOR.getId(),
              personaRef, TipoEstadoComentario.CERRADO);
      if (!comentariosCerradosOtrosUsuarios.isEmpty()) {
        comentarios.addAll(comentariosCerradosOtrosUsuarios);
      }
      log.debug("findByEvaluacionIdActaEvaluador(Long id, String personaRef) - end");
      return comentarios;
    }).orElseThrow(() -> new EvaluacionNotFoundException(id));

  }

  @Override
  public int countByEvaluacionId(Long id) {
    return comentarioRepository.countByEvaluacionId(id);
  }

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion} y un tipo de comentario {@link TipoComentario}.
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @return número de {@link Comentario}
   */
  @Override
  public int countByEvaluacionIdAndTipoComentarioId(Long id, Long idTipoComentario) {
    return comentarioRepository.countByEvaluacionIdAndTipoComentarioId(id, idTipoComentario);
  }

  /**
   * Crea un comentario seteandole la evaluación y el tipo correspondiente.
   * 
   * @param evaluacionId     Identificador de la {@link Evaluacion}
   * @param comentario       {@link Comentario}
   * @param tipoComentarioId Identificador del {@link TipoComentario}
   * @return {@link Comentario}
   */
  private Comentario createComentarioEvaluacion(Long evaluacionId, Comentario comentario, Long tipoComentarioId) {
    log.debug("createComentarioEvaluacion(Long evaluacionId, Comentario comentario, Long tipoComentarioId) - start");

    Assert.notNull(evaluacionId, "Evaluación id no puede ser null para crear un nuevo comentario");
    Assert.isNull(comentario.getId(), "Comentario id  tiene que ser null para crear un nuevo comentario");
    Assert.isNull(comentario.getEvaluacion(), "La evaluación no debe estar rellena para crear un nuevo comentario");

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {
      comentario.setEvaluacion(evaluacion);
      TipoComentario tipoComentario = new TipoComentario();
      tipoComentario.setId(tipoComentarioId);
      comentario.setTipoComentario(tipoComentario);

      log.debug("createComentarioEvaluacion(Long evaluacionId, Comentario comentario, Long tipoComentarioId) - end");
      return comentarioRepository.save(comentario);

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));
  }

  /**
   * Actualiza un comentario de una evaluación en concreto.
   * 
   * @param evaluacionId         Identificador de la {@link Evaluacion}
   * @param comentarioActualizar {@link Comentario} a actualizar
   */
  private Comentario updateComentarioEvaluacion(Long evaluacionId, Comentario comentarioActualizar) {
    log.debug("updateComentarioEvaluacion(Long evaluacionId, Comentario comentario) - start");

    return comentarioRepository.findById(comentarioActualizar.getId()).map(comentario -> {

      Assert.isTrue(comentario.getEvaluacion().getId().equals(evaluacionId),
          "El comentario no pertenece a la evaluación recibida.");

      comentario.setApartado(comentarioActualizar.getApartado());
      comentario.setEvaluacion(comentarioActualizar.getEvaluacion());
      comentario.setTipoComentario(comentarioActualizar.getTipoComentario());
      comentario.setTexto(comentarioActualizar.getTexto());
      comentario.setEstado(comentarioActualizar.getEstado());
      comentario.setFechaEstado(comentarioActualizar.getFechaEstado());

      Comentario returnValue = comentarioRepository.save(comentario);
      log.debug("updateComentarioEvaluacion(Long evaluacionId, Comentario comentario) - end");
      return returnValue;
    }).orElseThrow(() -> new ComentarioNotFoundException(comentarioActualizar.getId()));
  }

  private void deleteComentarioEvaluacion(Long evaluacionId, Long comentarioId, Long tipoComentarioId) {

    Assert.notNull(evaluacionId, "Evaluación id no puede ser null para eliminar un comentario");
    Assert.notNull(comentarioId, "Comentario id no puede ser null para eliminar un comentario");

    Optional<Comentario> comentario = comentarioRepository.findById(comentarioId);

    if (!comentario.isPresent()) {
      throw new ComentarioNotFoundException(comentarioId);
    }

    Assert.isTrue(comentario.get().getEvaluacion().getId().equals(evaluacionId),
        "El comentario no pertenece a la evaluación recibida");

    // TODO Refactorizar texto cuando se tengan enumerados.
    Assert.isTrue(comentario.get().getTipoComentario().getId().equals(tipoComentarioId),
        "No se puede eliminar el comentario debido a su tipo");

    comentarioRepository.deleteById(comentarioId);

  }

  /**
   * Comprueba si el formulario se corresponde con el adecuado para comité y tipo
   * de evaluación.
   * 
   * @param idTipoEvaluacion Identificador {@link TipoEvaluacion}.
   * @param idComite         Identificador del {@link Comite}.
   * @param idFormulario     Identificador de {@link Formulario}
   */
  private void validarTipoEvaluacionAndFormulario(Long idTipoEvaluacion, Long idComite, Bloque bloque) {
    Long idFormulario = null;

    if (bloque.getFormulario() != null) {
      idFormulario = bloque.getFormulario().getId();
    }

    boolean isValid = false;

    switch (TipoEvaluacion.Tipo.fromId(idTipoEvaluacion)) {
      case RETROSPECTIVA: {
        isValid = idFormulario == null || Constantes.FORMULARIO_RETROSPECTIVA.equals(idFormulario);
        break;
      }
      case MEMORIA: {
        isValid = idFormulario == null
            || (Constantes.FORMULARIO_M10.equals(idFormulario) && Constantes.COMITE_CEI.equals(idComite))
            || (Constantes.FORMULARIO_M20.equals(idFormulario) && Constantes.COMITE_CEEA.equals(idComite))
            || (Constantes.FORMULARIO_M30.equals(idFormulario) && Constantes.COMITE_CBE.equals(idComite));
        break;
      }
      case SEGUIMIENTO_ANUAL: {
        isValid = idFormulario == null || Constantes.FORMULARIO_ANUAL.equals(idFormulario);
        break;
      }
      case SEGUIMIENTO_FINAL: {
        isValid = idFormulario == null || Constantes.FORMULARIO_FINAL.equals(idFormulario);
        break;
      }
      default:
        log.warn("Tipo de Evaluación con el id: {} no encontrado.", idTipoEvaluacion.intValue());
        break;
    }

    if (!isValid) {
      throw new NoRelatedEntitiesException(Comentario.class, TipoEvaluacion.class);
    }

  }

  private void validateEstadoEvaluacion(Evaluacion evaluacion) {
    Assert.isTrue((evaluacion.getMemoria().getRetrospectiva() == null &&
        evaluacion.getMemoria().getEstadoActual().getId()
            .equals(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA)
        || evaluacion.getMemoria().getEstadoActual().getId().equals(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION)
        || evaluacion.getMemoria().getEstadoActual().getId()
            .equals(Constantes.TIPO_ESTADO_MEMORIA_FAVORABLE_PENDIENTE_MOD_MINIMAS)
        || evaluacion.getMemoria().getEstadoActual().getId()
            .equals(TipoEstadoMemoria.Tipo.EN_EVALUACION_SEGUIMIENTO_ANUAL.getId())
        || evaluacion.getMemoria().getEstadoActual().getId()
            .equals(TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_ANUAL_MODIFICACION.getId())
        || evaluacion.getMemoria().getEstadoActual().getId()
            .equals(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES)
        || evaluacion.getMemoria().getEstadoActual().getId()
            .equals(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL))
        || (evaluacion.getMemoria()
            .getRetrospectiva() != null
            && evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getId()
                .equals(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA)),
        "La Evaluación no está en un estado adecuado para añadir comentarios.");
  }

  /**
   * Identifica si los {@link Comentario} en la {@link Evaluacion} han sido
   * enviados
   * 
   * @param idEvaluacion identificador de la {@link Evaluacion}
   * @param personaRef   El usuario de la petición
   * @return true/false
   */
  @Override
  public boolean isComentariosEvaluadorEnviados(Long idEvaluacion, String personaRef) {
    log.debug("isComentariosEvaluadorEnviados(Long idEvaluacion, String personaRef) - end");
    return comentarioRepository.existsByEvaluacionIdAndTipoComentarioIdAndEstadoAndCreatedBy(idEvaluacion,
        TipoComentario.Tipo.EVALUADOR.getId(),
        TipoEstadoComentario.CERRADO, personaRef);
  }

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion}, un tipo de comentario {@link TipoComentario}
   * y un {@link TipoEstadoComentario} Abierto
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @return número de {@link Comentario}
   */
  @Override
  public int countByEvaluacionIdAndTipoComentarioIdAndEstadoAbierto(Long id, Long idTipoComentario) {
    return comentarioRepository.countByEvaluacionIdAndTipoComentarioIdAndEstado(id, idTipoComentario,
        TipoEstadoComentario.ABIERTO);
  }

  /**
   * Obtiene el número total de {@link Comentario} para una determinada
   * {@link Evaluacion}, un tipo de comentario {@link TipoComentario}
   * y un {@link TipoEstadoComentario} Cerrado
   * 
   * @param id               Id de {@link Evaluacion}.
   * @param idTipoComentario idTipoComentario de {@link TipoComentario}.
   * @return número de {@link Comentario}
   */
  @Override
  public int countByEvaluacionIdAndTipoComentarioIdAndEstadoCerrado(Long id, Long idTipoComentario) {
    return comentarioRepository.countByEvaluacionIdAndTipoComentarioIdAndEstado(id, idTipoComentario,
        TipoEstadoComentario.CERRADO);
  }

  /**
   * Obtiene el número total de {@link Comentario} de otros evaluadores distintos
   * al que realiza la petición para una determinada {@link Evaluacion} y un
   * {@link TipoEstadoComentario} Abierto
   * 
   * @param id         Id de {@link Evaluacion}.
   * @param personaRef Persona creadora del comentario
   * @return número de {@link Comentario}
   */
  @Override
  public boolean isPosibleEnviarComentarios(Long id, String personaRef) {
    if (comentarioRepository.countByEvaluacionIdAndTipoComentarioIdAndCreatedBy(id,
        TipoComentario.Tipo.EVALUADOR.getId(), personaRef) == 0) {
      return false;
    }

    return comentarioRepository.countByEvaluacionIdAndTipoComentarioIdAndCreatedByAndEstado(id,
        TipoComentario.Tipo.EVALUADOR.getId(),
        personaRef, TipoEstadoComentario.ABIERTO) > 0;
  }

  /**
   * Permite enviar los comentarios de {@link Evaluacion} y persona
   *
   * @param id         Id del {@link Evaluacion}.
   * @param personaRef referencia de la persona de los {@link Comentario}
   * @return true si puede ser enviado / false si no puede ser enviado
   */
  @Override
  @Transactional
  public boolean enviarByEvaluacion(Long id, String personaRef) {
    try {
      List<Comentario> comentarios = comentarioRepository.findByEvaluacionIdAndTipoComentarioIdAndCreatedBy(id,
          TipoComentario.Tipo.EVALUADOR.getId(), personaRef);
      if (CollectionUtils.isNotEmpty(comentarios)) {
        comentarios.forEach(comentario -> {
          comentario.setEstado(TipoEstadoComentario.CERRADO);
          this.updateComentarioEvaluador(id, comentario, personaRef);
        });
      }
      return true;
    } catch (Exception e) {
      log.error("enviarByEvaluacion(Long id, String personaRef)", e);
      return false;
    }
  }

  /**
   * Obtiene todos los {@link Comentario} del tipo "EVALUADOR" por el id de su
   * evaluación y la persona creadora del comentario
   *
   * @param id         el id de la entidad {@link Evaluacion}.
   * @param personaRef Usuario logueado
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  @Override
  public List<Comentario> findComentariosEvaluadorByPersonaRef(Long id, String personaRef) {
    log.debug("findComentariosEvaluadorByPersonaRef(Long id, String personaRef) - start");
    Assert.notNull(id, MSG_EL_ID_DE_LA_EVALUACION_NO_PUEDE_SER_NULO_PARA_LISTAR_SUS_COMENTARIOS);
    return evaluacionRepository.findById(id).map(evaluacion -> {
      List<Comentario> returnValue = comentarioRepository.findByEvaluacionIdAndTipoComentarioIdAndCreatedBy(id,
          TipoComentario.Tipo.EVALUADOR.getId(), personaRef);
      log.debug("findComentariosEvaluadorByPersonaRef(Long id, Pageable pageable, String personaRef) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluacionNotFoundException(id));
  }

  @Override
  public Page<Comentario> findByEvaluacionEvaluadorAndEstadoCerrado(Long id, Pageable pageable) {
    log.debug("findByEvaluacionEvaluadorAndEstadoCerrado(Long id, Pageable pageable) - start");
    Assert.notNull(id, MSG_EL_ID_DE_LA_EVALUACION_NO_PUEDE_SER_NULO_PARA_LISTAR_SUS_COMENTARIOS);
    return evaluacionRepository.findById(id).map(evaluacion -> {
      Page<Comentario> returnValue = comentarioRepository.findByEvaluacionIdAndTipoComentarioIdAndEstado(id,
          TipoComentario.Tipo.EVALUADOR.getId(), TipoEstadoComentario.CERRADO, pageable);
      log.debug("findByEvaluacionEvaluadorAndEstadoCerrado(Long id, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluacionNotFoundException(id));
  }

  @Override
  @Transactional
  public boolean enviarByEvaluacionActa(Long id, String personaRef) {
    log.error("enviarByEvaluacionActa(Long id, String personaRef) - start");
    try {
      List<Comentario> comentarios = comentarioRepository.findByEvaluacionIdAndTipoComentarioIdAndCreatedBy(id,
          TipoComentario.Tipo.ACTA_EVALUADOR.getId(), personaRef);
      if (CollectionUtils.isNotEmpty(comentarios)) {
        comentarios.forEach(comentario -> {
          comentario.setEstado(TipoEstadoComentario.CERRADO);
          this.updateComentarioEvaluacion(id, comentario);
        });
      }
      log.error("enviarByEvaluacionActa(Long id, String personaRef) - end");
      return true;
    } catch (Exception e) {
      log.error("enviarByEvaluacionActa(Long id, String personaRef)", e);
      return false;
    }
  }

  /**
   * Obtiene todos los {@link Comentario} del tipo "ACTA" por el id de su
   * evaluación y la persona creadora del comentario
   *
   * @param id         el id de la entidad {@link Acta}.
   * @param personaRef Usuario logueado
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  @Override
  public List<Comentario> findComentariosActaByPersonaRef(Long id, String personaRef) {
    log.debug("findComentariosEvaluadorByPersonaRef(Long id, Pageable pageable, String personaRef) - start");
    Assert.notNull(id, MSG_EL_ID_DE_LA_EVALUACION_NO_PUEDE_SER_NULO_PARA_LISTAR_SUS_COMENTARIOS);
    return actaRepository.findById(id).map(acta -> {
      List<Comentario> returnValue = new ArrayList();
      Page<Evaluacion> evaluaciones = evaluacionRepository
          .findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(
              acta.getConvocatoriaReunion().getId(), null);
      if (evaluaciones.hasContent()) {
        evaluaciones.getContent().forEach(evaluacion -> {
          returnValue.addAll(comentarioRepository.findByEvaluacionIdAndTipoComentarioIdAndCreatedBy(evaluacion.getId(),
              TipoComentario.Tipo.ACTA_EVALUADOR.getId(), personaRef));
        });
      }
      log.debug("findComentariosEvaluadorByPersonaRef(Long id, Pageable pageable, String personaRef) - end");
      return returnValue;
    }).orElseThrow(() -> new ActaNotFoundException(id));
  }

}
