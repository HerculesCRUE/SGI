package org.crue.hercules.sgi.prc.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.repository.ProyectoRepository;
import org.crue.hercules.sgi.prc.repository.specification.ProyectoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link Proyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProyectoService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final ProyectoRepository repository;

  public ProyectoService(
      ProyectoRepository proyectoRepository) {
    this.repository = proyectoRepository;
  }

  /**
   * Guardar un nuevo {@link Proyecto}.
   *
   * @param proyecto la entidad {@link Proyecto}
   *                 a guardar.
   * @return la entidad {@link Proyecto} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Proyecto create(@Valid Proyecto proyecto) {

    log.debug("create(Proyecto proyecto) - start");

    Assert.isNull(proyecto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(Proyecto.class))
            .build());

    Proyecto returnValue = repository.save(proyecto);

    log.debug("create(Proyecto proyecto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Proyecto}.
   *
   * @param proyecto la entidad {@link Proyecto}
   *                 a actualizar.
   * @return la entidad {@link Proyecto} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public Proyecto update(@Valid Proyecto proyecto) {
    log.debug("update(Proyecto proyecto) - start");

    Assert.notNull(proyecto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(Proyecto.class))
            .build());

    return repository.findById(proyecto.getId())
        .map(proyectoExistente -> {

          // Establecemos los proyectoes actualizables con los recibidos
          proyectoExistente.setProyectoRef(proyecto.getProyectoRef());

          // Actualizamos la entidad
          Proyecto returnValue = repository.save(proyectoExistente);
          log.debug("update(Proyecto proyecto) - end");
          return returnValue;
        }).orElseThrow(
            () -> new ProyectoNotFoundException(proyecto.getId()));
  }

  /**
   * Obtener todas las entidades {@link Proyecto} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Proyecto} paginadas y/o
   *         filtradas.
   */
  public Page<Proyecto> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Proyecto> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Proyecto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Proyecto} por su id.
   *
   * @param id el id de la entidad {@link Proyecto}.
   * @return la entidad {@link Proyecto}.
   */
  public Proyecto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Proyecto returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link Proyecto}.
   *
   * @param id Id del {@link Proyecto}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(Proyecto.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene todos los {@link Proyecto} por su produccionCientificaId.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @return listado de {@link Proyecto}.
   */
  public List<Proyecto> findAllByProduccionCientificaId(Long produccionCientificaId) {
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId)  - start");
    final List<Proyecto> returnValue = repository.findAllByProduccionCientificaId(produccionCientificaId);
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId)  - end");
    return returnValue;
  }

  /**
   * Obtiene todos los {@link Proyecto} por su produccionCientificaId
   * paginadas y/o filtradas.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @param query                  la información del filtro.
   * @param pageable               la información de la paginación.
   * @return listado de {@link Proyecto} paginadas y/o filtradas.
   */
  public Page<Proyecto> findAllByProduccionCientificaId(Long produccionCientificaId, String query,
      Pageable pageable) {
    log.debug(
        "findAllByProduccionCientificaId(Long produccionCientificaId, String query, Pageable pageable) - start");
    Specification<Proyecto> specs = ProyectoSpecifications.byProduccionCientificaId(
        produccionCientificaId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    final Page<Proyecto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProduccionCientificaId(Long produccionCientificaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Elimina todos los {@link Proyecto} cuyo produccionCientificaId
   * coincide con el indicado.
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
}