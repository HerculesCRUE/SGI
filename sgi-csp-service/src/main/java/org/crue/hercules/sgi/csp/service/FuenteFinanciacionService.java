package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion.OnActivar;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.FuenteFinanciacionSpecifications;
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
 * Service para gestionar {@link FuenteFinanciacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class FuenteFinanciacionService {

  private final Validator validator;

  private final FuenteFinanciacionRepository repository;

  public FuenteFinanciacionService(Validator validator, FuenteFinanciacionRepository fuenteFinanciacionRepository) {
    this.validator = validator;
    this.repository = fuenteFinanciacionRepository;
  }

  /**
   * Guardar un nuevo {@link FuenteFinanciacion}.
   *
   * @param fuenteFinanciacion la entidad {@link FuenteFinanciacion} a guardar.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  @Transactional
  @Validated({ FuenteFinanciacion.OnCrear.class })
  public FuenteFinanciacion create(@Valid FuenteFinanciacion fuenteFinanciacion) {
    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - start");

    Assert.isNull(fuenteFinanciacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class)).build());
    Assert.notNull(fuenteFinanciacion.getTipoAmbitoGeografico(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage(TipoAmbitoGeografico.class))
            .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class)).build());
    Assert.notNull(fuenteFinanciacion.getTipoAmbitoGeografico().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoAmbitoGeografico.class)).build());
    Assert.notNull(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage(TipoOrigenFuenteFinanciacion.class))
            .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class)).build());
    Assert.notNull(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoOrigenFuenteFinanciacion.class)).build());

    fuenteFinanciacion.setActivo(true);
    FuenteFinanciacion returnValue = repository.save(fuenteFinanciacion);

    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link FuenteFinanciacion}.
   *
   * @param fuenteFinanciacion la entidad {@link FuenteFinanciacion} a actualizar.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  @Transactional
  @Validated({ FuenteFinanciacion.OnActualizar.class })
  public FuenteFinanciacion update(@Valid FuenteFinanciacion fuenteFinanciacion) {
    log.debug("update(FuenteFinanciacion fuenteFinanciacion) - start");

    Assert.notNull(fuenteFinanciacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class)).build());
    Assert.notNull(fuenteFinanciacion.getTipoAmbitoGeografico(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage(TipoAmbitoGeografico.class))
            .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class)).build());
    Assert.notNull(fuenteFinanciacion.getTipoAmbitoGeografico().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoAmbitoGeografico.class)).build());
    Assert.notNull(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage(TipoOrigenFuenteFinanciacion.class))
            .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class)).build());
    Assert.notNull(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoOrigenFuenteFinanciacion.class)).build());

    return repository.findById(fuenteFinanciacion.getId()).map(fuenteFinanciacionExistente -> {

      if (!fuenteFinanciacionExistente.getTipoAmbitoGeografico().getId()
          .equals(fuenteFinanciacion.getTipoAmbitoGeografico().getId())) {
        // Si estamos mofificando el TipoAmbitoGeografico invocar validaciones asociadas
        // a OnActualizarTipoAmbitoGeografico
        Set<ConstraintViolation<FuenteFinanciacion>> result = validator.validate(fuenteFinanciacion,
            FuenteFinanciacion.OnActualizarTipoAmbitoGeografico.class);
        if (!result.isEmpty()) {
          throw new ConstraintViolationException(result);
        }
      }

      if (!fuenteFinanciacionExistente.getTipoOrigenFuenteFinanciacion().getId()
          .equals(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId())) {
        // Si estamos modificando el TipoOrigenFuenteFinanciacion invocar validaciones
        // asociadas a OnActualizarTipoOrigenFuenteFinanciacion
        Set<ConstraintViolation<FuenteFinanciacion>> result = validator.validate(fuenteFinanciacion,
            FuenteFinanciacion.OnActualizarTipoOrigenFuenteFinanciacion.class);
        if (!result.isEmpty()) {
          throw new ConstraintViolationException(result);
        }
      }

      // Establecemos los campos actualizables con los recibidos
      fuenteFinanciacionExistente.setNombre(fuenteFinanciacion.getNombre());
      fuenteFinanciacionExistente.setDescripcion(fuenteFinanciacion.getDescripcion());
      fuenteFinanciacionExistente.setFondoEstructural(fuenteFinanciacion.getFondoEstructural());
      fuenteFinanciacionExistente.setTipoAmbitoGeografico(fuenteFinanciacion.getTipoAmbitoGeografico());
      fuenteFinanciacionExistente.setTipoOrigenFuenteFinanciacion(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion());

      // Actualizamos la entidad
      FuenteFinanciacion returnValue = repository.save(fuenteFinanciacionExistente);
      log.debug("update(FuenteFinanciacion fuenteFinanciacion) - end");
      return returnValue;
    }).orElseThrow(() -> new FuenteFinanciacionNotFoundException(fuenteFinanciacion.getId()));
  }

  /**
   * Activa el {@link FuenteFinanciacion}.
   *
   * @param id Id del {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  @Transactional
  public FuenteFinanciacion activar(Long id) {
    log.debug("activar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class)).build());

    return repository.findById(id).map(fuenteFinanciacion -> {
      if (fuenteFinanciacion.getActivo()) {
        // Si esta activo no se hace nada
        return fuenteFinanciacion;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<FuenteFinanciacion>> result = validator.validate(fuenteFinanciacion, OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      fuenteFinanciacion.setActivo(true);

      FuenteFinanciacion returnValue = repository.save(fuenteFinanciacion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new FuenteFinanciacionNotFoundException(id));
  }

  /**
   * Desactiva el {@link FuenteFinanciacion}.
   *
   * @param id Id del {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  @Transactional
  public FuenteFinanciacion desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class)).build());

    return repository.findById(id).map(fuenteFinanciacion -> {
      if (!fuenteFinanciacion.getActivo()) {
        // Si no esta activo no se hace nada
        return fuenteFinanciacion;
      }

      fuenteFinanciacion.setActivo(false);

      FuenteFinanciacion returnValue = repository.save(fuenteFinanciacion);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new FuenteFinanciacionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link FuenteFinanciacion} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link FuenteFinanciacion} paginadas y/o
   *         filtradas.
   */
  public Page<FuenteFinanciacion> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<FuenteFinanciacion> specs = FuenteFinanciacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<FuenteFinanciacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link FuenteFinanciacion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link FuenteFinanciacion} paginadas y/o
   *         filtradas.
   */
  public Page<FuenteFinanciacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<FuenteFinanciacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<FuenteFinanciacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link FuenteFinanciacion} por su id.
   *
   * @param id el id de la entidad {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion}.
   */
  public FuenteFinanciacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final FuenteFinanciacion returnValue = repository.findById(id)
        .orElseThrow(() -> new FuenteFinanciacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}