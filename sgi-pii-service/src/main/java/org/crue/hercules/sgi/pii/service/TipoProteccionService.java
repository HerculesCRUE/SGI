package org.crue.hercules.sgi.pii.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.TipoProteccionNotFoundException;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion.OnActivar;
import org.crue.hercules.sgi.pii.repository.TipoProteccionRepository;
import org.crue.hercules.sgi.pii.repository.specification.TipoProteccionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link TipoProteccion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class TipoProteccionService {

  private final Validator validator;

  private final TipoProteccionRepository repository;

  public TipoProteccionService(Validator validator, TipoProteccionRepository tipoProteccionRepository) {
    this.validator = validator;
    this.repository = tipoProteccionRepository;
  }

  /**
   * Guardar un nuevo {@link TipoProteccion}.
   *
   * @param tipoProteccion la entidad {@link TipoProteccion} a guardar.
   * @return la entidad {@link TipoProteccion} persistida.
   */
  @Transactional
  @Validated({ TipoProteccion.OnCrear.class })
  public TipoProteccion create(@Valid TipoProteccion tipoProteccion) {
    log.debug("create(@Valid TipoProteccion tipoProteccion) - start");

    Assert.isNull(tipoProteccion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProteccion.class)).build());

    tipoProteccion.setActivo(true);
    TipoProteccion returnValue = repository.save(tipoProteccion);

    log.debug("create(@Valid TipoProteccion tipoProteccion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoProteccion}.
   *
   * @param tipoProteccion la entidad {@link TipoProteccion} a actualizar.
   * @return la entidad {@link TipoProteccion} persistida.
   */
  @Transactional
  @Validated({ TipoProteccion.OnActualizar.class })
  public TipoProteccion update(@Valid TipoProteccion tipoProteccion) {
    log.debug("update(TipoProteccion tipoProteccion) - start");

    Assert.notNull(tipoProteccion.getId(),
        // Defer message resolution until is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProteccion.class)).build());

    return repository.findById(tipoProteccion.getId()).map(tipoProteccionExistente -> {

      // Establecemos los campos actualizables con los recibidos
      tipoProteccionExistente.setNombre(tipoProteccion.getNombre());
      tipoProteccionExistente.setDescripcion(tipoProteccion.getDescripcion());
      tipoProteccionExistente.setTipoPropiedad(tipoProteccion.getTipoPropiedad());

      // Actualizamos la entidad
      TipoProteccion returnValue = repository.save(tipoProteccionExistente);
      log.debug("update(TipoProteccion tipoProteccion) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoProteccionNotFoundException(tipoProteccion.getId()));
  }

  /**
   * Activar {@link TipoProteccion}.
   *
   * @param id Id del {@link TipoProteccion}.
   * @return Entidad {@link TipoProteccion} persistida activada.
   */
  @Transactional
  public TipoProteccion activar(Long id) {
    log.debug("activar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProteccion.class)).build());

    return repository.findById(id).map(tipoProteccion -> {
      if (tipoProteccion.getActivo()) {
        // Si esta activo no se hace nada
        return tipoProteccion;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoProteccion>> result = validator.validate(tipoProteccion, OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoProteccion.setActivo(true);

      TipoProteccion returnValue = repository.save(tipoProteccion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoProteccionNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoProteccion}.
   *
   * @param id Id del {@link TipoProteccion}.
   * @return Entidad {@link TipoProteccion} persistida desactivada.
   */
  @Transactional
  public TipoProteccion desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProteccion.class)).build());

    return repository.findById(id).map(tipoProteccion -> {
      if (!tipoProteccion.getActivo()) {
        // Si no esta activo no se hace nada
        return tipoProteccion;
      }

      tipoProteccion.setActivo(false);

      TipoProteccion returnValue = repository.save(tipoProteccion);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoProteccionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link TipoProteccion} activas paginadas y/o
   * filtradas.
   *
   * @param pageable Información de la paginación.
   * @param query    Información del/los filtros a aplicar.
   * @return Lista de entidades {@link TipoProteccion} paginadas y/o filtradas.
   */
  public Page<TipoProteccion> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<TipoProteccion> specs = TipoProteccionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoProteccion> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoProteccion} paginadas y/o filtradas.
   *
   * @param pageable Información de la paginación.
   * @param query    Información del/los filtros a aplicar.
   * @return Lista de entidades {@link TipoProteccion} paginadas y/o filtradas.
   */
  public Page<TipoProteccion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoProteccion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoProteccion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene un {@link TipoProteccion} por su id.
   *
   * @param id el id de la entidad {@link TipoProteccion}.
   * @return la entidad {@link TipoProteccion}.
   */
  public TipoProteccion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoProteccion returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoProteccionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
