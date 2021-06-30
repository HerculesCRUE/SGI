package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.RetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Retrospectiva}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RetrospectivaServiceImpl implements RetrospectivaService {

  private final RetrospectivaRepository repository;

  public RetrospectivaServiceImpl(RetrospectivaRepository repository) {
    this.repository = repository;
  }

  /**
   * Crea {@link Retrospectiva}.
   *
   * @param retrospectiva La entidad {@link Retrospectiva} a crear.
   * @return La entidad {@link Retrospectiva} creada.
   * @throws IllegalArgumentException Si la entidad {@link Retrospectiva} tiene
   *                                  id.
   */
  @Override
  @Transactional
  public Retrospectiva create(Retrospectiva retrospectiva) {
    log.debug("create(Retrospectiva retrospectiva) - start");
    Assert.isNull(retrospectiva.getId(), "Retrospectiva id debe ser null para crear un nuevo Retrospectiva");
    Retrospectiva returnValue = repository.save(retrospectiva);
    log.debug("create(Retrospectiva retrospectiva) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link Retrospectiva}.
   *
   * @param retrospectivaActualizar La entidad {@link Retrospectiva} a actualizar.
   * @return La entidad {@link Retrospectiva} actualizada.
   * @throws RetrospectivaNotFoundException Si no existe ninguna entidad
   *                                        {@link Retrospectiva} con ese id.
   * @throws IllegalArgumentException       Si la entidad {@link Retrospectiva}
   *                                        entidad no tiene id.
   */
  @Override
  @Transactional
  public Retrospectiva update(final Retrospectiva retrospectivaActualizar) {
    log.debug("update(Retrospectiva retrospectivaActualizar) - start");

    Assert.notNull(retrospectivaActualizar.getId(),
        "Retrospectiva id no puede ser null para actualizar un retrospectiva");

    return repository.findById(retrospectivaActualizar.getId()).map(retrospectiva -> {
      retrospectiva.setEstadoRetrospectiva(retrospectivaActualizar.getEstadoRetrospectiva());
      retrospectiva.setFechaRetrospectiva(retrospectivaActualizar.getFechaRetrospectiva());

      Retrospectiva returnValue = repository.save(retrospectiva);
      log.debug("update(Retrospectiva retrospectivaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new RetrospectivaNotFoundException(retrospectivaActualizar.getId()));
  }

  /**
   * Elimina todas las entidades {@link Retrospectiva}.
   *
   */
  @Override
  @Transactional
  public void deleteAll() {
    log.debug("deleteAll() - start");
    repository.deleteAll();
    log.debug("deleteAll() - end");
  }

  /**
   * Elimina {@link Retrospectiva} por id.
   *
   * @param id El id de la entidad {@link Retrospectiva}.
   * @throws RetrospectivaNotFoundException Si no existe ninguna entidad
   *                                        {@link Retrospectiva} con ese id.
   * @throws IllegalArgumentException       Si no se informa Id.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id, "Retrospectiva id no puede ser null para eliminar una retrospectiva");
    if (!repository.existsById(id)) {
      throw new RetrospectivaNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link Retrospectiva} filtradas y paginadas según los
   * criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Retrospectiva} paginadas y filtradas.
   */
  @Override
  public Page<Retrospectiva> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Retrospectiva> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<Retrospectiva> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link Retrospectiva} por id.
   *
   * @param id El id de la entidad {@link Retrospectiva}.
   * @return La entidad {@link Retrospectiva}.
   * @throws RetrospectivaNotFoundException Si no existe ninguna entidad
   *                                        {@link Retrospectiva} con ese id.
   * @throws IllegalArgumentException       Si no se informa Id.
   */
  @Override
  public Retrospectiva findById(final Long id) {
    log.debug("findById(final Long id) - start");
    Assert.notNull(id, "Retrospectiva id no puede ser null para buscar una retrospectiva por Id");
    final Retrospectiva retrospectiva = repository.findById(id)
        .orElseThrow(() -> new RetrospectivaNotFoundException(id));
    log.debug("findById(final Long id) - end");
    return retrospectiva;
  }

  /**
   * Se actualiza el estado actual de la {@link Retrospectiva} recibida
   * 
   * @param retrospectiva         {@link Retrospectiva} a actualizar estado.
   * @param idEstadoRetrospectiva identificador del estado nuevo de la
   *                              retrospectiva.
   */
  @Override
  public void updateEstadoRetrospectiva(Retrospectiva retrospectiva, Long idEstadoRetrospectiva) {
    log.debug("updateEstadoRetrospectiva(Retrospectiva retrospectiva, Long idEstadoRetrospectiva) - start");

    // se crea el nuevo estado para la retrospectiva
    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(idEstadoRetrospectiva);

    // Se actualiza la memoria con el nuevo tipo estado memoria
    retrospectiva.setEstadoRetrospectiva(estadoRetrospectiva);
    repository.save(retrospectiva);

    log.debug("updateEstadoRetrospectiva(Retrospectiva retrospectiva, Long idEstadoRetrospectiva) - end");
  }

}
