package org.crue.hercules.sgi.prc.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.CampoProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.specification.CampoProduccionCientificaSpecifications;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaAuthorityHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link CampoProduccionCientifica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class CampoProduccionCientificaService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final CampoProduccionCientificaRepository repository;
  private final ProduccionCientificaAuthorityHelper authorityHelper;

  public CampoProduccionCientificaService(
      CampoProduccionCientificaRepository campoProduccionCientificaRepository,
      ProduccionCientificaAuthorityHelper authorityHelper) {
    this.repository = campoProduccionCientificaRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guardar un nuevo {@link CampoProduccionCientifica}.
   *
   * @param campoProduccionCientifica la entidad {@link CampoProduccionCientifica}
   *                                  a guardar.
   * @return la entidad {@link CampoProduccionCientifica} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public CampoProduccionCientifica create(@Valid CampoProduccionCientifica campoProduccionCientifica) {

    log.debug("create(CampoProduccionCientifica campoProduccionCientifica) - start");

    Assert.isNull(campoProduccionCientifica.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(CampoProduccionCientifica.class))
            .build());

    CampoProduccionCientifica returnValue = repository.save(campoProduccionCientifica);

    log.debug("create(CampoProduccionCientifica campoProduccionCientifica) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link CampoProduccionCientifica}.
   *
   * @param campoProduccionCientifica la entidad {@link CampoProduccionCientifica}
   *                                  a actualizar.
   * @return la entidad {@link CampoProduccionCientifica} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public CampoProduccionCientifica update(@Valid CampoProduccionCientifica campoProduccionCientifica) {
    log.debug("update(CampoProduccionCientifica campoProduccionCientifica) - start");

    Assert.notNull(campoProduccionCientifica.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(CampoProduccionCientifica.class))
            .build());

    return repository.findById(campoProduccionCientifica.getId())
        .map(campoProduccionCientificaExistente -> {

          // Establecemos los campos actualizables con los recibidos
          campoProduccionCientificaExistente.setCodigoCVN(campoProduccionCientifica.getCodigoCVN());

          // TODO completar campos a actualizar

          // Actualizamos la entidad
          CampoProduccionCientifica returnValue = repository.save(campoProduccionCientificaExistente);
          log.debug("update(CampoProduccionCientifica campoProduccionCientifica) - end");
          return returnValue;
        }).orElseThrow(
            () -> new CampoProduccionCientificaNotFoundException(campoProduccionCientifica.getId().toString()));
  }

  /**
   * Obtener todas las entidades {@link CampoProduccionCientifica} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link CampoProduccionCientifica} paginadas y/o
   *         filtradas.
   */
  public Page<CampoProduccionCientifica> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<CampoProduccionCientifica> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<CampoProduccionCientifica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link CampoProduccionCientifica} por su id.
   *
   * @param id el id de la entidad {@link CampoProduccionCientifica}.
   * @return la entidad {@link CampoProduccionCientifica}.
   */
  public CampoProduccionCientifica findById(Long id) {
    log.debug("findById(Long id)  - start");
    final CampoProduccionCientifica returnValue = repository.findById(id)
        .orElseThrow(() -> new CampoProduccionCientificaNotFoundException(id.toString()));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link CampoProduccionCientifica}.
   *
   * @param id Id del {@link CampoProduccionCientifica}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(CampoProduccionCientifica.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene todos los {@link CampoProduccionCientifica} por su
   * produccionCientificaId.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @return listado de {@link CampoProduccionCientifica}.
   */
  public List<CampoProduccionCientifica> findAllByProduccionCientificaId(Long produccionCientificaId) {
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId)  - start");
    final List<CampoProduccionCientifica> returnValue = repository
        .findAllByProduccionCientificaId(produccionCientificaId);
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId)  - end");
    return returnValue;
  }

  /**
   * Deletes the given entities.
   *
   * @param entities must not be {@literal null}. Must not contain {@literal null}
   *                 elements.
   * @throws IllegalArgumentException in case the given {@literal entities} or one
   *                                  of its entities is {@literal null}.
   */
  @Transactional
  public void deleteAll(Iterable<CampoProduccionCientifica> entities) {
    log.debug("deleteAll(entities) - start");
    repository.deleteAll(entities);
    log.debug("deleteAll(entities) - end");
  }

  /**
   * Elimina todos los {@link CampoProduccionCientifica} cuyo
   * produccionCientificaId coincide con el indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(Long produccionCientificaId)  - start");
    final int returnValue = repository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    log.debug("deleteInBulkByProduccionCientificaId(Long produccionCientificaId)  - end");
    return returnValue;
  }

  /**
   * Obtiene todos los {@link CampoProduccionCientifica} por su
   * produccionCientificaId paginadas y/o filtradas.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @param query                  la información del filtro.
   * @param pageable               la información de la paginación.
   * @return listado de {@link CampoProduccionCientifica} paginadas y/o filtradas.
   */
  public Page<CampoProduccionCientifica> findAllByProduccionCientificaId(Long produccionCientificaId, String query,
      Pageable pageable) {
    log.debug(
        "findAllByProduccionCientificaId(Long prodduccionCientificaId, String query, Pageable pageable) - start");
    authorityHelper.checkUserHasAuthorityViewProduccionCientifica(produccionCientificaId);
    Specification<CampoProduccionCientifica> specs = CampoProduccionCientificaSpecifications.byProduccionCientificaId(
        produccionCientificaId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    final Page<CampoProduccionCientifica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId, String query, Pageable pageable) - end");
    return returnValue;
  }
}
