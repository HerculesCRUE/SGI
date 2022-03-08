package org.crue.hercules.sgi.prc.service;

import java.time.Instant;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.EstadoProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.EstadoProduccionCientificaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link EstadoProduccionCientifica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class EstadoProduccionCientificaService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final EstadoProduccionCientificaRepository repository;

  public EstadoProduccionCientificaService(
      EstadoProduccionCientificaRepository estadoProduccionCientificaRepository) {
    this.repository = estadoProduccionCientificaRepository;
  }

  /**
   * Guardar un nuevo {@link EstadoProduccionCientifica}.
   *
   * @param estadoProduccionCientifica la entidad
   *                                   {@link EstadoProduccionCientifica}
   *                                   a guardar.
   * @return la entidad {@link EstadoProduccionCientifica} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public EstadoProduccionCientifica create(@Valid EstadoProduccionCientifica estadoProduccionCientifica) {

    log.debug("create(EstadoProduccionCientifica estadoProduccionCientifica) - start");

    Assert.isNull(estadoProduccionCientifica.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(EstadoProduccionCientifica.class))
            .build());

    estadoProduccionCientifica.setFecha(Instant.now());
    EstadoProduccionCientifica returnValue = repository.save(estadoProduccionCientifica);

    log.debug("create(EstadoProduccionCientifica estadoProduccionCientifica) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link EstadoProduccionCientifica}.
   *
   * @param estadoProduccionCientifica la entidad
   *                                   {@link EstadoProduccionCientifica}
   *                                   a actualizar.
   * @return la entidad {@link EstadoProduccionCientifica} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public EstadoProduccionCientifica update(@Valid EstadoProduccionCientifica estadoProduccionCientifica) {
    log.debug("update(EstadoProduccionCientifica estadoProduccionCientifica) - start");

    Assert.notNull(estadoProduccionCientifica.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(EstadoProduccionCientifica.class))
            .build());

    return repository.findById(estadoProduccionCientifica.getId())
        .map(estadoProduccionCientificaExistente -> {

          // Establecemos los estados actualizables con los recibidos
          estadoProduccionCientificaExistente.setEstado(estadoProduccionCientifica.getEstado());
          estadoProduccionCientificaExistente.setFecha(Instant.now());

          // TODO completar estados a actualizar

          // Actualizamos la entidad
          EstadoProduccionCientifica returnValue = repository.save(estadoProduccionCientificaExistente);
          log.debug("update(EstadoProduccionCientifica estadoProduccionCientifica) - end");
          return returnValue;
        }).orElseThrow(
            () -> new EstadoProduccionCientificaNotFoundException(estadoProduccionCientifica.getId().toString()));
  }

  /**
   * Obtener todas las entidades {@link EstadoProduccionCientifica} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link EstadoProduccionCientifica} paginadas
   *         y/o
   *         filtradas.
   */
  public Page<EstadoProduccionCientifica> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<EstadoProduccionCientifica> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<EstadoProduccionCientifica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link EstadoProduccionCientifica} por su id.
   *
   * @param id el id de la entidad {@link EstadoProduccionCientifica}.
   * @return la entidad {@link EstadoProduccionCientifica}.
   */
  public EstadoProduccionCientifica findById(Long id) {
    log.debug("findById(Long id)  - start");
    final EstadoProduccionCientifica returnValue = repository.findById(id)
        .orElseThrow(() -> new EstadoProduccionCientificaNotFoundException(id.toString()));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link EstadoProduccionCientifica}.
   *
   * @param id Id del {@link EstadoProduccionCientifica}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(EstadoProduccionCientifica.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }
}
