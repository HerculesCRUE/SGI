package org.crue.hercules.sgi.eti.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.service.ComentarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Comentario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ComentarioServiceImpl implements ComentarioService {
  private final ComentarioRepository comentarioRepository;
  private final EvaluacionRepository evaluacionRepository;

  public ComentarioServiceImpl(ComentarioRepository comentarioRepository, EvaluacionRepository evaluacionRepository) {
    this.comentarioRepository = comentarioRepository;
    this.evaluacionRepository = evaluacionRepository;
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

    Assert.notNull(evaluacionId, "Evaluación id no puede ser null para crear un nuevo comentario.");

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getComite(),
          comentario.getApartado().getBloque().getFormulario().getId());

      Assert.isTrue(
          evaluacion.getMemoria().getEstadoActual().getId().equals(4L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(5L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(13L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(18L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(19L)
              || evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getId().equals(4L),
          "La Evaluación no está en un estado adecuado para añadir comentarios.");

      log.debug("createComentarioGestor(Long evaluacionId, Comentario comentario) - end");

      return createComentarioEvaluacion(evaluacionId, comentario, 1L);

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

    Assert.notNull(evaluacionId, "Evaluación id no puede ser null para crear un nuevo comentario.");

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getComite(),
          comentario.getApartado().getBloque().getFormulario().getId());

      Assert.isTrue(
          (evaluacion.getEvaluador1().getPersonaRef()).equals(personaRef)
              || evaluacion.getEvaluador2().getPersonaRef().equals(personaRef),
          "El usuario no coincide con ninguno de los Evaluadores de la Evaluación.");

      log.debug("createComentarioEvaluador(Long evaluacionId, Comentario comentario) - end");
      return createComentarioEvaluacion(evaluacionId, comentario, 2L);

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

    Assert.notNull(evaluacionId, "Evaluación id no puede ser null para eliminar un comentario.");
    Assert.notNull(comentarioId, "Comentario id no puede ser null para eliminar un comentario.");

    evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      Assert.isTrue(
          evaluacion.getMemoria().getEstadoActual().getId().equals(4L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(5L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(13L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(18L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(19L)
              || evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getId().equals(4L),
          "La Evaluación no está en un estado adecuado para eliminar comentarios.");

      deleteComentarioEvaluacion(evaluacionId, comentarioId, 1L);

      log.debug("deleteComentarioGestor(Long evaluacionId, Long comentarioId) - end");
      return evaluacion;

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

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

    Assert.notNull(evaluacionId, "Evaluación id no puede ser null para eliminar un comentario.");
    Assert.notNull(comentarioId, "Comentario id no puede ser null para eliminar un comentario.");

    evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      Assert.isTrue(
          (evaluacion.getEvaluador1().getPersonaRef()).equals(personaRef)
              || evaluacion.getEvaluador2().getPersonaRef().equals(personaRef),
          "El usuario no coincide con ninguno de los Evaluadores de la Evaluación.");

      deleteComentarioEvaluacion(evaluacionId, comentarioId, 2L);

      log.debug("deleteComentarioEvaluador(Long evaluacionId, Long comentarioId) - end");
      return evaluacion;
    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));

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
    Assert.isTrue(comentarioActualizar.getTipoComentario().getId().equals(1L),
        "No se puede actualizar un tipo de comentario que no sea del tipo Gestor.");

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getComite(),
          comentarioActualizar.getApartado().getBloque().getFormulario().getId());

      Assert.isTrue(
          evaluacion.getMemoria().getEstadoActual().getId().equals(4L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(5L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(13L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(18L)
              || evaluacion.getMemoria().getEstadoActual().getId().equals(19L)
              || evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getId().equals(4L),
          "La Evaluación no está en un estado adecuado para actualizar comentarios.");

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
    Assert.isTrue(comentarioActualizar.getTipoComentario().getId().equals(2L),
        "No se puede actualizar un tipo de comentario que no sea del tipo Evaluador.");

    log.debug("updateComentarioEvaluador(Long evaluacionId, Comentario comentarioActualizar) - end");

    return evaluacionRepository.findById(evaluacionId).map(evaluacion -> {

      validarTipoEvaluacionAndFormulario(evaluacion.getTipoEvaluacion().getId(),
          evaluacion.getMemoria().getComite().getComite(),
          comentarioActualizar.getApartado().getBloque().getFormulario().getId());

      Assert.isTrue(
          (evaluacion.getEvaluador1().getPersonaRef()).equals(personaRef)
              || evaluacion.getEvaluador2().getPersonaRef().equals(personaRef),
          "El usuario no coincide con ninguno de los Evaluadores de la Evaluación.");

      return updateComentarioEvaluacion(evaluacionId, comentarioActualizar);

    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionId));
  }

  /**
   * Obtiene todos los {@link Comentario} del tipo "GESTOR" por el id de su
   * evaluación.
   *
   * @param id       el id de la entidad {@link Evaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  @Override
  public Page<Comentario> findByEvaluacionIdGestor(Long id, Pageable pageable) {
    log.debug("findByEvaluacionIdGestor(Long id, Pageable pageable) - start");
    Assert.notNull(id, "El id de la evaluación no puede ser nulo para listar sus comentarios");

    return evaluacionRepository.findById(id).map(evaluacion -> {
      Page<Comentario> returnValue = comentarioRepository.findByEvaluacionIdAndTipoComentarioId(id, 1L, pageable);
      log.debug("findByEvaluacionIdGestor(Long id, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluacionNotFoundException(id));

  }

  /**
   * Obtiene todos los {@link Comentario} del tipo "EVALUADOR" por el id de su
   * evaluación.
   *
   * @param id       el id de la entidad {@link Evaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  @Override
  public Page<Comentario> findByEvaluacionIdEvaluador(Long id, Pageable pageable, String personaRef) {
    log.debug("findByEvaluacionIdEvaluador(Long id, Pageable pageable) - start");
    Assert.notNull(id, "El id de la evaluación no puede ser nulo para listar sus comentarios");

    return evaluacionRepository.findById(id).map(evaluacion -> {

      Assert.isTrue(
          (evaluacion.getEvaluador1().getPersonaRef()).equals(personaRef)
              || evaluacion.getEvaluador2().getPersonaRef().equals(personaRef),
          "El usuario no coincide con ninguno de los Evaluadores de la Evaluación.");

      Page<Comentario> returnValue = comentarioRepository.findByEvaluacionIdAndTipoComentarioId(id, 2L, pageable);
      log.debug("findByEvaluacionIdEvaluador(Long id, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluacionNotFoundException(id));

  }

  @Override
  public int countByEvaluacionId(Long id) {
    return comentarioRepository.countByEvaluacionId(id);
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
   * @param comite           nombre de {@link Comite}.
   * @param idFormulario     Identificador de {@link Formulario}
   */
  private void validarTipoEvaluacionAndFormulario(Long idTipoEvaluacion, String comite, Long idFormulario) {

    switch (idTipoEvaluacion.intValue()) {
      case 1: {
        // Tipo Evaluación Retrospectiva

        // El id formulario debe ser del tipo 6 - > Retrospectiva
        Assert.isTrue(idFormulario.equals(6L), "El bloque seleccionado no es correcto para el tipo de evaluación.");
        break;
      }
      case 2: {
        // Tipo Evaluación Memoria

        // El id formulario debe ser del tipo 1 - > M10 si el comité es CEISH
        Assert.isTrue(
            (idFormulario.equals(1L) && comite.equals("CEISH")) || (idFormulario.equals(2L) && comite.equals("CEEA"))
                || (idFormulario.equals(3L) && comite.equals("CEIAB")),
            "El bloque seleccionado no es correcto para el tipo de evaluación.");

        break;
      }
      case 3: {
        // Tipo Evaluación Seguimiento Anual

        // El id formulario debe ser del tipo 4 - > Seguimiento Anual
        Assert.isTrue(idFormulario.equals(4L), "El bloque seleccionado no es correcto para el tipo de evaluación.");
        break;
      }
      case 4: {
        // Tipo Evaluación Seguimiento Final
        // El id formulario debe ser del tipo 5 - > Seguimiento Final
        Assert.isTrue(idFormulario.equals(5L), "El bloque seleccionado no es correcto para el tipo de evaluación.");
      }
    }
  }

}
