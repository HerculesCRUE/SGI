package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.LineaInvestigacionNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.crue.hercules.sgi.csp.repository.LineaInvestigacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.LineaInvestigacionSpecifications;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link LineaInvestigacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class LineaInvestigacionService {

  private final Validator validator;
  private final LineaInvestigacionRepository repository;

  public LineaInvestigacionService(Validator validator, LineaInvestigacionRepository repository) {
    this.repository = repository;
    this.validator = validator;
  }

  /**
   * Guarda la entidad {@link LineaInvestigacion}.
   * 
   * @param lineaInvestigacion la entidad {@link LineaInvestigacion} a guardar.
   * @return LineaInvestigacion la entidad {@link LineaInvestigacion} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public LineaInvestigacion create(@Valid LineaInvestigacion lineaInvestigacion) {
    log.debug("create(LineaInvestigacion lineaInvestigacion) - start");

    Assert.isNull(lineaInvestigacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(LineaInvestigacion.class)).build());

    lineaInvestigacion.setActivo(Boolean.TRUE);
    LineaInvestigacion returnValue = repository.save(lineaInvestigacion);

    log.debug("create(LineaInvestigacion lineaInvestigacion) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link LineaInvestigacion}.
   * 
   * @param lineaInvestigacion lineaInvestigacionActualizar
   *                           {@link LineaInvestigacion} con los datos
   *                           actualizados.
   * @return {@link LineaInvestigacion} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public LineaInvestigacion update(@Valid LineaInvestigacion lineaInvestigacion) {
    log.debug("update(LineaInvestigacion lineaInvestigacion) - start");

    Assert.notNull(lineaInvestigacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(LineaInvestigacion.class)).build());

    return repository.findById(lineaInvestigacion.getId()).map((data) -> {
      data.setNombre(lineaInvestigacion.getNombre());

      LineaInvestigacion returnValue = repository.save(data);
      log.debug("update(LineaInvestigacion lineaInvestigacion) - end");
      return returnValue;
    }).orElseThrow(() -> new LineaInvestigacionNotFoundException(lineaInvestigacion.getId()));
  }

  /**
   * Reactiva el {@link LineaInvestigacion}.
   *
   * @param id Id del {@link LineaInvestigacion}.
   * @return la entidad {@link LineaInvestigacion} persistida.
   */
  @Transactional
  public LineaInvestigacion enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(LineaInvestigacion.class)).build());

    return repository.findById(id).map(lineaInvestigacion -> {
      if (lineaInvestigacion.getActivo()) {
        return lineaInvestigacion;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<LineaInvestigacion>> result = validator.validate(lineaInvestigacion,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      lineaInvestigacion.setActivo(true);
      LineaInvestigacion returnValue = repository.save(lineaInvestigacion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new LineaInvestigacionNotFoundException(id));
  }

  /**
   * Desactiva el {@link LineaInvestigacion}.
   *
   * @param id Id del {@link LineaInvestigacion}.
   * @return la entidad {@link LineaInvestigacion} persistida.
   */
  @Transactional
  public LineaInvestigacion disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(LineaInvestigacion.class)).build());

    return repository.findById(id).map(lineaInvestigacion -> {
      if (!lineaInvestigacion.getActivo()) {
        return lineaInvestigacion;
      }

      lineaInvestigacion.setActivo(false);
      LineaInvestigacion returnValue = repository.save(lineaInvestigacion);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new LineaInvestigacionNotFoundException(id));
  }

  /**
   * Obtiene todas las entidades {@link LineaInvestigacion} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link LineaInvestigacion} paginadas y
   *         filtradas.
   */
  public Page<LineaInvestigacion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<LineaInvestigacion> specs = LineaInvestigacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<LineaInvestigacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link LineaInvestigacion} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link LineaInvestigacion} paginadas y
   *         filtradas.
   */
  public Page<LineaInvestigacion> findAllTodos(String query, Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Specification<LineaInvestigacion> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<LineaInvestigacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAllTodos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link LineaInvestigacion} por id.
   * 
   * @param id Identificador de la entidad {@link LineaInvestigacion}.
   * @return LineaInvestigacion la entidad {@link LineaInvestigacion}.
   */
  public LineaInvestigacion findById(Long id) {
    log.debug("findById(Long id) - start");
    final LineaInvestigacion returnValue = repository.findById(id)
        .orElseThrow(() -> new LineaInvestigacionNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
