package org.crue.hercules.sgi.pii.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.TramoRepartoNotFoundException;
import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.crue.hercules.sgi.pii.model.TramoReparto.OnActivar;
import org.crue.hercules.sgi.pii.repository.TramoRepartoRepository;
import org.crue.hercules.sgi.pii.repository.specification.TramoRepartoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link TramoReparto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class TramoRepartoService {

  private final Validator validator;
  private final TramoRepartoRepository repository;

  public TramoRepartoService(Validator validator, TramoRepartoRepository tramoRepartoRepository) {
    this.validator = validator;
    this.repository = tramoRepartoRepository;
  }

  /**
   * Obtener todas las entidades {@link TramoReparto} activas paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TramoReparto} paginadas y/o filtradas.
   */
  public Page<TramoReparto> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<TramoReparto> specs = TramoRepartoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TramoReparto> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TramoReparto} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TramoReparto} paginadas y/o filtradas.
   */
  public Page<TramoReparto> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TramoReparto> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TramoReparto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TramoReparto} por su id.
   *
   * @param id el id de la entidad {@link TramoReparto}.
   * @return la entidad {@link TramoReparto}.
   */
  public TramoReparto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TramoReparto returnValue = repository.findById(id).orElseThrow(() -> new TramoRepartoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link TramoReparto}.
   *
   * @param tramoReparto la entidad {@link TramoReparto} a guardar.
   * @return la entidad {@link TramoReparto} persistida.
   */
  @Transactional
  @Validated({ TramoReparto.OnCrear.class })
  public TramoReparto create(@Valid TramoReparto tramoReparto) {
    log.debug("create(TramoReparto tramoReparto) - start");

    Assert.isNull(tramoReparto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TramoReparto.class)).build());

    tramoReparto.setActivo(true);
    TramoReparto returnValue = repository.save(tramoReparto);

    log.debug("create(TramoReparto tramoReparto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TramoReparto}.
   *
   * @param tramoReparto la entidad {@link TramoReparto} a actualizar.
   * @return la entidad {@link TramoReparto} persistida.
   */
  @Transactional
  @Validated({ TramoReparto.OnActualizar.class })
  public TramoReparto update(@Valid TramoReparto tramoReparto) {
    log.debug("update(TramoReparto tramoReparto) - start");

    Assert.notNull(tramoReparto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TramoReparto.class)).build());

    return repository.findById(tramoReparto.getId()).map(tramorepartoExistente -> {

      // Establecemos los campos actualizables con los recibidos
      tramorepartoExistente.setDesde(tramoReparto.getDesde());
      tramorepartoExistente.setHasta(tramoReparto.getHasta());
      tramorepartoExistente.setPorcentajeUniversidad(tramoReparto.getPorcentajeUniversidad());
      tramorepartoExistente.setPorcentajeInventores(tramoReparto.getPorcentajeInventores());

      // Actualizamos la entidad
      TramoReparto returnValue = repository.save(tramorepartoExistente);
      log.debug("update(TramoReparto tramoReparto) - end");
      return returnValue;
    }).orElseThrow(() -> new TramoRepartoNotFoundException(tramoReparto.getId()));
  }

  /**
   * Activa el {@link TramoReparto}.
   *
   * @param id Id del {@link TramoReparto}.
   * @return la entidad {@link TramoReparto} persistida.
   */
  @Transactional
  public TramoReparto activar(Long id) {
    log.debug("activar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TramoReparto.class)).build());

    return repository.findById(id).map(tramoReparto -> {
      if (tramoReparto.getActivo()) {
        // Si esta activo no se hace nada
        return tramoReparto;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TramoReparto>> result = validator.validate(tramoReparto, OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tramoReparto.setActivo(true);

      TramoReparto returnValue = repository.save(tramoReparto);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TramoRepartoNotFoundException(id));
  }

  /**
   * Desactiva el {@link TramoReparto}.
   *
   * @param id Id del {@link TramoReparto}.
   * @return la entidad {@link TramoReparto} persistida.
   */
  @Transactional
  public TramoReparto desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TramoReparto.class)).build());

    return repository.findById(id).map(tramoReparto -> {
      if (!tramoReparto.getActivo()) {
        // Si no esta activo no se hace nada
        return tramoReparto;
      }

      tramoReparto.setActivo(false);

      TramoReparto returnValue = repository.save(tramoReparto);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TramoRepartoNotFoundException(id));
  }
}
