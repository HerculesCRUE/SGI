package org.crue.hercules.sgi.csp.service.impl;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.predicate.ConceptoGastoPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.ConceptoGastoSpecifications;
import org.crue.hercules.sgi.csp.service.ConceptoGastoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ConceptoGasto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Validated
public class ConceptoGastoServiceImpl implements ConceptoGastoService {

  private static final String MSG_ENTITY_EXISTS = "org.springframework.util.Assert.entity.exists.message";
  private static final String MSG_MODEL_CONCEPTO_GASTO = "org.crue.hercules.sgi.csp.model.ConceptoGasto.message";
  private static final String PROBLEM_MESSAGE_KEY_NOT_NULL = "notNull";
  private static final String PROBLEM_MESSAGE_PARAMETER_KEY_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_PARAMETER_KEY_FIELD = "field";
  private final ConceptoGastoRepository repository;
  private final Validator validator;

  /**
   * Guardar un nuevo {@link ConceptoGasto}.
   *
   * @param conceptoGasto la entidad {@link ConceptoGasto} a guardar.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public ConceptoGasto create(@Valid ConceptoGasto conceptoGasto) {
    log.debug("create(ConceptoGasto conceptoGasto) - start");

    Assert.isNull(conceptoGasto.getId(),
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter(PROBLEM_MESSAGE_PARAMETER_KEY_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_KEY_ENTITY, ApplicationContextSupport.getMessage(ConceptoGasto.class))
            .build());

    Assert.notNull(conceptoGasto.getCostesIndirectos(),
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_KEY_NOT_NULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_KEY_FIELD, ApplicationContextSupport.getMessage("costesIndirectos"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_KEY_ENTITY, ApplicationContextSupport.getMessage(ConceptoGasto.class))
            .build());

    conceptoGasto.setActivo(true);
    ConceptoGasto returnValue = repository.save(conceptoGasto);

    log.debug("create(ConceptoGasto conceptoGasto) - end");
    return returnValue;

  }

  /**
   * Actualizar {@link ConceptoGasto}.
   *
   * @param conceptoGastoActualizar la entidad {@link ConceptoGasto} a actualizar.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public ConceptoGasto update(@Valid ConceptoGasto conceptoGastoActualizar) {
    log.debug("update(ConceptoGasto conceptoGastoActualizar) - start");

    Assert.notNull(conceptoGastoActualizar.getId(),
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_KEY_NOT_NULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_KEY_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_KEY_ENTITY, ApplicationContextSupport.getMessage(ConceptoGasto.class))
            .build());

    Assert.notNull(conceptoGastoActualizar.getCostesIndirectos(),
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_KEY_NOT_NULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_KEY_FIELD, ApplicationContextSupport.getMessage("costesIndirectos"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_KEY_ENTITY, ApplicationContextSupport.getMessage(ConceptoGasto.class))
            .build());

    return repository.findById(conceptoGastoActualizar.getId()).map(conceptoGasto -> {
      conceptoGasto.setNombre(conceptoGastoActualizar.getNombre());
      conceptoGasto.setDescripcion(conceptoGastoActualizar.getDescripcion());
      conceptoGasto.setCostesIndirectos(conceptoGastoActualizar.getCostesIndirectos());

      ConceptoGasto returnValue = repository.save(conceptoGasto);
      log.debug("update(ConceptoGasto conceptoGastoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConceptoGastoNotFoundException(conceptoGastoActualizar.getId()));
  }

  /**
   * Reactiva el {@link ConceptoGasto}.
   *
   * @param id Id del {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConceptoGasto enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, ConceptoGasto.class);

    return repository.findById(id).map(conceptoGasto -> {
      if (Boolean.TRUE.equals(conceptoGasto.getActivo())) {
        // Si esta activo no se hace nada
        return conceptoGasto;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<ConceptoGasto>> result = validator.validate(
          conceptoGasto,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      conceptoGasto.setActivo(true);

      ConceptoGasto returnValue = repository.save(conceptoGasto);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConceptoGastoNotFoundException(id));
  }

  /**
   * Desactiva el {@link ConceptoGasto}.
   *
   * @param id Id del {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConceptoGasto disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, ConceptoGasto.class);

    return repository.findById(id).map(conceptoGasto -> {
      if (Boolean.FALSE.equals(conceptoGasto.getActivo())) {
        // Si no esta activo no se hace nada
        return conceptoGasto;
      }

      conceptoGasto.setActivo(false);

      ConceptoGasto returnValue = repository.save(conceptoGasto);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConceptoGastoNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link ConceptoGasto} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConceptoGasto} paginadas y/o filtradas.
   */
  @Override
  public Page<ConceptoGasto> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ConceptoGasto> specs = ConceptoGastoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query, ConceptoGastoPredicateResolver.getInstance()));

    Page<ConceptoGasto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ConceptoGasto} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConceptoGasto} paginadas y/o filtradas.
   */
  @Override
  public Page<ConceptoGasto> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");
    Specification<ConceptoGasto> specs = SgiRSQLJPASupport.toSpecification(query,
        ConceptoGastoPredicateResolver.getInstance());

    Page<ConceptoGasto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto}.
   */
  @Override
  public ConceptoGasto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConceptoGasto returnValue = repository.findById(id).orElseThrow(() -> new ConceptoGastoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene una Page de {@link ConceptoGasto} por el id de la agrupacionde
   * gastos.
   *
   * @param id     el id de la agrupacion.
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * 
   * @return la page de entidades {@link ConceptoGasto}.
   */
  @Override
  public Page<ConceptoGasto> findAllNotInAgrupacion(Long id, String query, Pageable paging) {
    log.debug("findAllbyProyectoAgrupacionGastoId(Long agrupacionId, String query, Pageable pageable) - start");
    Specification<ConceptoGasto> specs = ConceptoGastoSpecifications.notInProyectoAgrupacionGasto(id)
        .and(ConceptoGastoSpecifications.activos()).and(SgiRSQLJPASupport.toSpecification(query));
    Page<ConceptoGasto> returnValue = repository.findAll(specs, paging);
    log.debug("findAllbyProyectoAgrupacionGastoId(Long agrupacionId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
