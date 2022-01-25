package org.crue.hercules.sgi.pii.service;

import java.time.Instant;
import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.PeriodoTitularidadNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.repository.PeriodoTitularidadRepository;
import org.crue.hercules.sgi.pii.repository.PeriodoTitularidadTitularRepository;
import org.crue.hercules.sgi.pii.repository.specification.PeriodoTitularidadSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar los {@link PeriodoTitularidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class PeriodoTitularidadService {

  private final PeriodoTitularidadRepository repository;
  private final PeriodoTitularidadTitularRepository titularRepository;

  public PeriodoTitularidadService(PeriodoTitularidadRepository periodoTitularidadRepository,
      PeriodoTitularidadTitularRepository titularRepository) {
    this.repository = periodoTitularidadRepository;
    this.titularRepository = titularRepository;
  }

  /**
   * Guardar un nuevo {@link PeriodoTitularidad}.
   *
   * @param periodoTitularidad la entidad {@link PeriodoTitularidad} a guardar.
   * @param fechaFinPrevious   la fecha fin del anterior periodo.
   * @return la entidad {@link PeriodoTitularidad} persistida.
   */
  @Transactional
  public PeriodoTitularidad create(@Valid PeriodoTitularidad periodoTitularidad, Instant fechaFinPrevious) {
    log.debug("create(@Valid PeriodoTitularidad periodoTitularidad) - start");

    Assert.isNull(periodoTitularidad.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidad.class)).build());
    Assert.notNull(periodoTitularidad.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("invencionId"))
            .parameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidad.class)).build());
    Assert.isTrue(fechaFinPrevious == null || periodoTitularidad.getFechaInicio().compareTo(fechaFinPrevious) >= 0,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.pii.model.PeriodoTitularidad.fechaInicio.posterior")
            .parameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidad.class)).build());

    Optional<PeriodoTitularidad> previous = repository
        .findByInvencionIdAndFechaFinIsNull(periodoTitularidad.getInvencionId());
    previous.ifPresent((previousVigente) -> {
      previousVigente.setFechaFin(fechaFinPrevious);
      repository.save(previousVigente);
    });
    PeriodoTitularidad returnValue = repository.save(periodoTitularidad);

    log.debug("create(@Valid PeriodoTitularidad periodoTitularidad) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link PeriodoTitularidad}.
   *
   * @param periodoTitularidad la entidad {@link PeriodoTitularidad} a actualizar.
   * @return la entidad {@link PeriodoTitularidad} persistida.
   */
  @Transactional
  @Validated({ PeriodoTitularidad.OnActualizar.class })
  public PeriodoTitularidad update(@Valid PeriodoTitularidad periodoTitularidad) {
    log.debug("update(@Valid PeriodoTitularidad periodoTitularidad) - start");

    Assert.notNull(periodoTitularidad.getId(),
        // Defer message resolution until is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidad.class)).build());

    return repository.findById(periodoTitularidad.getId()).map(periodoTitularidadExistente -> {

      // Establecemos los campos actualizables con los recibidos
      periodoTitularidadExistente.setFechaInicio(periodoTitularidad.getFechaInicio());
      periodoTitularidadExistente.setFechaFin(periodoTitularidad.getFechaFin());

      // Actualizamos la entidad
      PeriodoTitularidad returnValue = repository.save(periodoTitularidadExistente);
      log.debug("update(@Valid PeriodoTitularidad periodoTitularidad) - end");
      return returnValue;
    }).orElseThrow(() -> new PeriodoTitularidadNotFoundException(periodoTitularidad.getId()));
  }

  /**
   * Obtiene un {@link PeriodoTitularidad} por su id. Lanza una excepcion
   * {@link PeriodoTitularidadNotFoundException} si no existe.
   *
   * @param id el id de la entidad {@link PeriodoTitularidad}.
   * @return la entidad {@link PeriodoTitularidad}.
   */
  public PeriodoTitularidad findById(Long id) throws PeriodoTitularidadNotFoundException {
    log.debug("findById(Long id) - start");

    final PeriodoTitularidad returnValue = repository.findById(id)
        .orElseThrow(() -> new PeriodoTitularidadNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link PeriodoTitularidad} asociados a la {@link Invencion}
   * pasada por parámetros, paginados o filtrados.
   *
   * @param invencionId el id de la {@link Invencion}.
   * @param pageable    Información de la paginación.
   * @param query       Información del/los filtros a aplicar.
   * @return Lista de entidades {@link PeriodoTitularidad} paginadas y/o
   *         filtradas.
   */
  public Page<PeriodoTitularidad> findByInvencion(Long invencionId, String query, Pageable pageable) {
    log.debug("findByInvencion(String query, Pageable pageable) - start");
    Specification<PeriodoTitularidad> specs = PeriodoTitularidadSpecifications
        .periodoTitularidadByInvencionId(invencionId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<PeriodoTitularidad> returnValue = repository.findAll(specs, pageable);
    log.debug("findByInvencion(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link PeriodoTitularidad} paginadas y/o
   * filtradas.
   *
   * @param pageable Información de la paginación.
   * @param query    Información del/los filtros a aplicar.
   * @return Lista de entidades {@link PeriodoTitularidad} paginadas y/o
   *         filtradas.
   */
  public Page<PeriodoTitularidad> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<PeriodoTitularidad> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<PeriodoTitularidad> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link PeriodoTitularidad} con el id indicado.
   * 
   * @param id el id de la entidad {@link PeriodoTitularidad}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidad.class)).build());

    if (!repository.existsById(id)) {
      throw new PeriodoTitularidadNotFoundException(id);
    }
    titularRepository.deleteByPeriodoTitularidadId(id);
    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }

}
