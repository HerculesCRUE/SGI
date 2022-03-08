package org.crue.hercules.sgi.prc.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.AliasEnumeradoNotFoundException;
import org.crue.hercules.sgi.prc.model.AliasEnumerado;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link AliasEnumerado}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class AliasEnumeradoService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final AliasEnumeradoRepository repository;

  public AliasEnumeradoService(
      AliasEnumeradoRepository aliasEnumeradoRepository) {
    this.repository = aliasEnumeradoRepository;
  }

  /**
   * Guardar un nuevo {@link AliasEnumerado}.
   *
   * @param aliasEnumerado la entidad {@link AliasEnumerado}
   *                       a guardar.
   * @return la entidad {@link AliasEnumerado} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public AliasEnumerado create(@Valid AliasEnumerado aliasEnumerado) {

    log.debug("create(AliasEnumerado aliasEnumerado) - start");

    Assert.isNull(aliasEnumerado.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(AliasEnumerado.class))
            .build());

    AliasEnumerado returnValue = repository.save(aliasEnumerado);

    log.debug("create(AliasEnumerado aliasEnumerado) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link AliasEnumerado}.
   *
   * @param aliasEnumerado la entidad {@link AliasEnumerado}
   *                       a actualizar.
   * @return la entidad {@link AliasEnumerado} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public AliasEnumerado update(@Valid AliasEnumerado aliasEnumerado) {
    log.debug("update(AliasEnumerado aliasEnumerado) - start");

    Assert.notNull(aliasEnumerado.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(AliasEnumerado.class))
            .build());

    return repository.findById(aliasEnumerado.getId())
        .map(aliasEnumeradoExistente -> {

          // Establecemos los aliasEnumerados actualizables con los recibidos
          aliasEnumeradoExistente.setCodigoCVN(aliasEnumerado.getCodigoCVN());
          aliasEnumeradoExistente.setPrefijoEnumerado(aliasEnumerado.getPrefijoEnumerado());

          // Actualizamos la entidad
          AliasEnumerado returnValue = repository.save(aliasEnumeradoExistente);
          log.debug("update(AliasEnumerado aliasEnumerado) - end");
          return returnValue;
        }).orElseThrow(
            () -> new AliasEnumeradoNotFoundException(aliasEnumerado.getId()));
  }

  /**
   * Obtener todas las entidades {@link AliasEnumerado} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link AliasEnumerado} paginadas y/o
   *         filtradas.
   */
  public Page<AliasEnumerado> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<AliasEnumerado> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<AliasEnumerado> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link AliasEnumerado} por su id.
   *
   * @param id el id de la entidad {@link AliasEnumerado}.
   * @return la entidad {@link AliasEnumerado}.
   */
  public AliasEnumerado findById(Long id) {
    log.debug("findById(Long id)  - start");
    final AliasEnumerado returnValue = repository.findById(id)
        .orElseThrow(() -> new AliasEnumeradoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link AliasEnumerado}.
   *
   * @param id Id del {@link AliasEnumerado}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(AliasEnumerado.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }
}
