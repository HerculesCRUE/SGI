package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.ResultadoInformePatentabilidadNotFoundException;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad.OnActivar;
import org.crue.hercules.sgi.pii.repository.ResultadoInformePatentabilidadRepository;
import org.crue.hercules.sgi.pii.repository.specification.ResultadoInformePatentabilidadSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestionar {@link ResultadoInformePatentabilidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ResultadoInformePatentabilidadService {

  private final Validator validator;
  private final ResultadoInformePatentabilidadRepository repository;

  public ResultadoInformePatentabilidadService(Validator validator,
      ResultadoInformePatentabilidadRepository resultadoInformePatentabilidadRepository) {
    this.validator = validator;
    this.repository = resultadoInformePatentabilidadRepository;
  }

  /**
   * Guardar {@link ResultadoInformePatentabilidad}.
   *
   * @param resultadoInformePatentabilidad Entidad
   *                                       {@link ResultadoInformePatentabilidad}
   *                                       a guardar.
   * @return Entidad {@link ResultadoInformePatentabilidad} persistida.
   */
  @Transactional
  @Validated(ResultadoInformePatentabilidad.OnCrear.class)
  public ResultadoInformePatentabilidad create(@Valid ResultadoInformePatentabilidad resultadoInformePatentabilidad) {
    log.debug("create(ResultadoInformePatentabilidad resultadoInformePatentabilidad) - start");

    Assert.isNull(resultadoInformePatentabilidad.getId(),
        // Defer message resolution until is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidad.class))
            .build());
    this.validateCommonFields(resultadoInformePatentabilidad);

    resultadoInformePatentabilidad.setActivo(true);
    ResultadoInformePatentabilidad returnValue = repository.save(resultadoInformePatentabilidad);

    log.debug("create(ResultadoInformePatentabilidad resultadoInformePatentabilidad) - end");
    return returnValue;
  }

  private void validateCommonFields(ResultadoInformePatentabilidad resultadoInformePatentabilidad) {
    Assert.notNull(resultadoInformePatentabilidad.getNombre(),
        // Defer message resolution until is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("nombre"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidad.class))
            .build());
    Assert.notNull(resultadoInformePatentabilidad.getDescripcion(),
        // Defer message resolution until is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("descripcion"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidad.class))
            .build());
  }

  /**
   * Actualizar {@link ResultadoInformePatentabilidad}.
   *
   * @param resultadoInformePatentabilidad Entidad
   *                                       {@link ResultadoInformePatentabilidad}
   *                                       a actualizar.
   * @return Entidad {@link ResultadoInformePatentabilidad} persistida.
   */
  @Transactional
  @Validated(ResultadoInformePatentabilidad.OnActualizar.class)
  public ResultadoInformePatentabilidad update(@Valid ResultadoInformePatentabilidad resultadoInformePatentabilidad) {
    log.debug("update(ResultadoInformePatentabilidad resultadoInformePatentabilidad) - start");

    Assert.notNull(resultadoInformePatentabilidad.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidad.class))
            .build());
    this.validateCommonFields(resultadoInformePatentabilidad);

    return this.repository.findById(resultadoInformePatentabilidad.getId())
        .map(resultadoInformePatentabilidadInternal -> {
          // Establecemos los campos actualizables con los recibidos
          resultadoInformePatentabilidadInternal.setNombre(resultadoInformePatentabilidad.getNombre());
          resultadoInformePatentabilidadInternal.setDescripcion(resultadoInformePatentabilidad.getDescripcion());

          // Actualizamos la entidad
          ResultadoInformePatentabilidad returnValue = repository.save(resultadoInformePatentabilidadInternal);
          log.debug("update(ResultadoInformePatentabilidad resultadoInformePatentabilidad) - end");
          return returnValue;
        })
        .orElseThrow(() -> new ResultadoInformePatentabilidadNotFoundException(resultadoInformePatentabilidad.getId()));
  }

  /**
   * Obtener todas las entidades {@link ResultadoInformePatentabilidad} activos
   * paginadas y/o filtradas
   *
   * @param query    Información del filtro.
   * @param pageable Información de la paginación.
   * @return Lista de entidades {@link ResultadoInformePatentabilidad} paginadas
   *         y/o filtradas
   */
  public Page<ResultadoInformePatentabilidad> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");

    Specification<ResultadoInformePatentabilidad> specs = ResultadoInformePatentabilidadSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ResultadoInformePatentabilidad> returnValue = repository.findAll(specs, pageable);

    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ResultadoInformePatentabilidad} paginadas
   * y/o filtradas
   *
   * @param query    Información del filtro.
   * @param pageable Información de la paginación.
   * @return la lista de entidades {@link ResultadoInformePatentabilidad}
   *         paginadas y/o filtradas
   */
  public Page<ResultadoInformePatentabilidad> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");

    Specification<ResultadoInformePatentabilidad> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<ResultadoInformePatentabilidad> returnValue = repository.findAll(specs, pageable);

    log.debug("findAll(String query, Pageable pageable) - end");

    return returnValue;
  }

  /**
   * Obtener un {@link ResultadoInformePatentabilidad} por su id.
   *
   * @param id Id de la entidad {@link ResultadoInformePatentabilidad}.
   * @return La entidad {@link ResultadoInformePatentabilidad}.
   */
  public ResultadoInformePatentabilidad findById(Long id) {
    log.debug("findById(Long id)  - start");

    final ResultadoInformePatentabilidad returnValue = repository.findById(id)
        .orElseThrow(() -> new ResultadoInformePatentabilidadNotFoundException(id));

    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link ResultadoInformePatentabilidad}.
   *
   * @param id Id del {@link ResultadoInformePatentabilidad}.
   * @return la entidad {@link ResultadoInformePatentabilidad} persistida.
   */
  @Transactional
  @Validated({ ResultadoInformePatentabilidad.OnActivar.class })
  public ResultadoInformePatentabilidad enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidad.class))
            .build());

    return this.repository.findById(id).map(resultadoInformePatentabilidad -> {
      if (resultadoInformePatentabilidad.getActivo().booleanValue()) {
        // Si esta activo no se hace nada
        return resultadoInformePatentabilidad;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<ResultadoInformePatentabilidad>> result = validator
          .validate(resultadoInformePatentabilidad, OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      resultadoInformePatentabilidad.setActivo(true);

      ResultadoInformePatentabilidad returnValue = repository.save(resultadoInformePatentabilidad);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ResultadoInformePatentabilidadNotFoundException(id));
  }

  /**
   * Desactiva el {@link ResultadoInformePatentabilidad}.
   *
   * @param id Id de la entidad {@link ResultadoInformePatentabilidad}.
   * @return Entidad {@link ResultadoInformePatentabilidad} actualizadas.
   */
  @Transactional
  public ResultadoInformePatentabilidad disable(Long id) throws ResultadoInformePatentabilidadNotFoundException {
    log.debug("disable(Long id) - start");

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ResultadoInformePatentabilidad.class))
            .build());

    return this.repository.findById(id).map(resultadoInformePatentabilidad -> {
      if (!resultadoInformePatentabilidad.getActivo().booleanValue()) {
        // Si no esta activo no se hace nada
        return resultadoInformePatentabilidad;
      }

      resultadoInformePatentabilidad.setActivo(false);

      ResultadoInformePatentabilidad returnValue = repository.save(resultadoInformePatentabilidad);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ResultadoInformePatentabilidadNotFoundException(id));
  }

}