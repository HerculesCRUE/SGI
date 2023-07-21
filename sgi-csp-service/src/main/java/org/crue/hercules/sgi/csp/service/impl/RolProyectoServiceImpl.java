package org.crue.hercules.sgi.csp.service.impl;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto.Orden;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.RolProyectoSpecifications;
import org.crue.hercules.sgi.csp.service.RolProyectoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RolProyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class RolProyectoServiceImpl implements RolProyectoService {

  private final RolProyectoRepository repository;
  private final Validator validator;

  public RolProyectoServiceImpl(RolProyectoRepository repository, Validator validator) {
    this.repository = repository;
    this.validator = validator;
  }

  /**
   * Guardar un nuevo {@link RolProyecto}.
   *
   * @param rolProyecto la entidad {@link RolProyecto} a guardar.
   * @return la entidad {@link RolProyecto} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public RolProyecto create(@Valid RolProyecto rolProyecto) {
    log.debug("create(RolProyecto rolProyecto) - start");

    AssertHelper.idIsNull(rolProyecto.getId(), RolProyecto.class);

    rolProyecto.setActivo(true);

    RolProyecto returnValue = repository.save(rolProyecto);

    log.debug("create(RolProyecto rolProyecto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link RolProyecto}.
   *
   * @param rolProyectoActualizar la entidad {@link RolProyecto} a actualizar.
   * @return la entidad {@link RolProyecto} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public RolProyecto update(@Valid RolProyecto rolProyectoActualizar) {
    log.debug("update(RolProyecto rolProyectoActualizar) - start");

    AssertHelper
        .idNotNull(rolProyectoActualizar.getId(), RolProyecto.class);

    return repository.findById(rolProyectoActualizar.getId()).map(rolProyecto -> {

      rolProyecto.setNombre(rolProyectoActualizar.getNombre());
      rolProyecto.setAbreviatura(rolProyectoActualizar.getAbreviatura());
      rolProyecto.setEquipo(rolProyectoActualizar.getEquipo());
      rolProyecto.setRolPrincipal(rolProyectoActualizar.getRolPrincipal());
      rolProyecto.setOrden(rolProyectoActualizar.getOrden());
      rolProyecto.setBaremablePRC(rolProyectoActualizar.getBaremablePRC());
      rolProyecto.setDescripcion(rolProyectoActualizar.getDescripcion());

      RolProyecto returnValue = repository.save(rolProyecto);
      log.debug("update(RolProyecto rolProyectoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new RolProyectoNotFoundException(rolProyectoActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link RolProyecto} por id.
   * 
   * @param id Identificador de la entidad {@link RolProyecto}.
   * @return RolProyecto la entidad {@link RolProyecto}.
   */
  @Override
  public RolProyecto findById(Long id) {
    log.debug("findById(Long id) - start");
    final RolProyecto returnValue = repository.findById(id).orElseThrow(() -> new RolProyectoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link RolProyecto} por orden Principal.
   * 
   * @return RolProyecto la entidad {@link RolProyecto}.
   */
  @Override
  public RolProyecto findPrincipal() {
    log.debug("findPrincipal() - start");
    final RolProyecto returnValue = repository.findByOrdenAndRolPrincipalIsTrueAndActivoIsTrue(Orden.PRIMARIO)
        .orElseThrow(() -> new RolProyectoNotFoundException(null));
    log.debug("findPrincipal() - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link RolProyecto} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolProyecto} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<RolProyecto> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<RolProyecto> specs = RolProyectoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<RolProyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link RolProyecto} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link RolProyecto} paginadas y/o filtradas.
   */
  @Override
  public Page<RolProyecto> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");
    Specification<RolProyecto> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<RolProyecto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link RolProyecto}.
   *
   * @param id Id del {@link RolProyecto}.
   * @return la entidad {@link RolProyecto} persistida.
   */
  @Override
  @Transactional
  public RolProyecto enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper
        .idNotNull(id, RolProyecto.class);

    return repository.findById(id).map(rolProyecto -> {
      if (Boolean.TRUE.equals(rolProyecto.getActivo())) {
        // Si esta activo no se hace nada
        return rolProyecto;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<RolProyecto>> result = validator.validate(
          rolProyecto,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      rolProyecto.setActivo(true);

      RolProyecto returnValue = repository.save(rolProyecto);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RolProyectoNotFoundException(id));
  }

  /**
   * Desactiva el {@link RolProyecto}
   *
   * @param id Id del {@link RolProyecto}.
   * @return la entidad {@link RolProyecto} persistida.
   */
  @Override
  @Transactional
  public RolProyecto disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper
        .idNotNull(id, RolProyecto.class);

    return repository.findById(id).map(rolProyecto -> {
      if (Boolean.FALSE.equals(rolProyecto.getActivo())) {
        // Si no esta activo no se hace nada
        return rolProyecto;
      }

      rolProyecto.setActivo(false);
      RolProyecto returnValue = repository.save(rolProyecto);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RolProyectoNotFoundException(id));
  }

  /**
   * Devuelve si existe una entidad {@link RolProyecto} por orden Principal.
   * 
   * @return true or false
   */
  @Override
  @Transactional
  public boolean existsPrincipal() {
    return repository.existsByOrdenAndRolPrincipalIsTrueAndActivoIsTrue(Orden.PRIMARIO);
  }

}
