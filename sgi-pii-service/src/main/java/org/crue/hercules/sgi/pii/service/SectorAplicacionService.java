package org.crue.hercules.sgi.pii.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.SectorAplicacionNotFoundException;
import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.crue.hercules.sgi.pii.model.SectorAplicacion.OnActivar;
import org.crue.hercules.sgi.pii.repository.SectorAplicacionRepository;
import org.crue.hercules.sgi.pii.repository.specification.SectorAplicacionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link SectorAplicacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SectorAplicacionService {

  private final Validator validator;

  private final SectorAplicacionRepository repository;

  public SectorAplicacionService(Validator validator, SectorAplicacionRepository sectorAplicacionRepository) {
    this.validator = validator;
    this.repository = sectorAplicacionRepository;
  }

  /**
   * Guardar un nuevo {@link SectorAplicacion}.
   *
   * @param sectorAplicacion la entidad {@link SectorAplicacion} a guardar.
   * @return la entidad {@link SectorAplicacion} persistida.
   */
  @Transactional
  @Validated({ SectorAplicacion.OnCrear.class })
  public SectorAplicacion create(@Valid SectorAplicacion sectorAplicacion) {
    log.debug("create(SectorAplicacion sectorAplicacion) - start");

    Assert.isNull(sectorAplicacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SectorAplicacion.class)).build());

    sectorAplicacion.setActivo(true);
    SectorAplicacion returnValue = repository.save(sectorAplicacion);

    log.debug("create(SectorAplicacion sectorAplicacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link SectorAplicacion}.
   *
   * @param sectorAplicacion la entidad {@link SectorAplicacion} a actualizar.
   * @return la entidad {@link SectorAplicacion} persistida.
   */
  @Transactional
  @Validated({ SectorAplicacion.OnActualizar.class })
  public SectorAplicacion update(SectorAplicacion sectorAplicacion) {
    log.debug("update(SectorAplicacion sectorAplicacion) - start");

    Assert.notNull(sectorAplicacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SectorAplicacion.class)).build());

    return repository.findById(sectorAplicacion.getId()).map(sectorAplicacionExistente -> {

      // Establecemos los campos actualizables con los recibidos
      sectorAplicacionExistente.setNombre(sectorAplicacion.getNombre());
      sectorAplicacionExistente.setDescripcion(sectorAplicacion.getDescripcion());

      // Actualizamos la entidad
      SectorAplicacion returnValue = repository.save(sectorAplicacionExistente);
      log.debug("update(FuenteFinanciacion fuenteFinanciacion) - end");
      return returnValue;
    }).orElseThrow(() -> new SectorAplicacionNotFoundException(sectorAplicacion.getId()));
  }

  /**
   * Activa el {@link SectorAplicacion}.
   *
   * @param id Id del {@link SectorAplicacion}.
   * @return la entidad {@link SectorAplicacion} persistida.
   */
  @Transactional
  public SectorAplicacion activar(Long id) {
    log.debug("activar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SectorAplicacion.class)).build());

    return repository.findById(id).map(sectorAplicacion -> {
      if (sectorAplicacion.getActivo()) {
        // Si esta activo no se hace nada
        return sectorAplicacion;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<SectorAplicacion>> result = validator.validate(sectorAplicacion, OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      sectorAplicacion.setActivo(true);

      SectorAplicacion returnValue = repository.save(sectorAplicacion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new SectorAplicacionNotFoundException(id));
  }

  /**
   * Desactiva el {@link SectorAplicacion}.
   *
   * @param id Id del {@link SectorAplicacion}.
   * @return la entidad {@link SectorAplicacion} persistida.
   */
  @Transactional
  public SectorAplicacion desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SectorAplicacion.class)).build());

    return repository.findById(id).map(sectorAplicacion -> {
      if (!sectorAplicacion.getActivo()) {
        // Si no esta activo no se hace nada
        return sectorAplicacion;
      }

      sectorAplicacion.setActivo(false);

      SectorAplicacion returnValue = repository.save(sectorAplicacion);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new SectorAplicacionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link SectorAplicacion} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link SectorAplicacion} paginadas y/o
   *         filtradas.
   */
  public Page<SectorAplicacion> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<SectorAplicacion> specs = SectorAplicacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SectorAplicacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link SectorAplicacion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link SectorAplicacion} paginadas y/o
   *         filtradas.
   */
  public Page<SectorAplicacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<SectorAplicacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<SectorAplicacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link SectorAplicacion} por su id.
   *
   * @param id el id de la entidad {@link SectorAplicacion}.
   * @return la entidad {@link SectorAplicacion}.
   */
  public SectorAplicacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final SectorAplicacion returnValue = repository.findById(id)
        .orElseThrow(() -> new SectorAplicacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }
}
