package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.EstadoRetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.repository.EstadoRetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.specification.EstadoRetrospectivaSpecifications;
import org.crue.hercules.sgi.eti.service.EstadoRetrospectivaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link EstadoRetrospectiva}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EstadoRetrospectivaServiceImpl implements EstadoRetrospectivaService {

  private final EstadoRetrospectivaRepository repository;

  public EstadoRetrospectivaServiceImpl(EstadoRetrospectivaRepository repository) {
    this.repository = repository;
  }

  /**
   * Crea {@link EstadoRetrospectiva}.
   *
   * @param estadoRetrospectiva La entidad {@link EstadoRetrospectiva} a crear.
   * @return La entidad {@link EstadoRetrospectiva} creada.
   */
  @Override
  @Transactional
  public EstadoRetrospectiva create(EstadoRetrospectiva estadoRetrospectiva) {
    log.debug("create(EstadoRetrospectiva estadoRetrospectiva) - start");
    Assert.notNull(estadoRetrospectiva.getId(),
        "EstadoRetrospectiva id no puede ser null para crear un nuevo estadoRetrospectiva");
    EstadoRetrospectiva returnValue = repository.save(estadoRetrospectiva);
    log.debug("create(EstadoRetrospectiva estadoRetrospectiva) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link EstadoRetrospectiva}.
   *
   * @param estadoRetrospectivaActualizar La entidad {@link EstadoRetrospectiva} a
   *                                      actualizar.
   * @return La entidad {@link EstadoRetrospectiva} actualizada.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si la entidad
   *                                              {@link EstadoRetrospectiva}
   *                                              entidad no tiene id.
   */
  @Override
  @Transactional
  public EstadoRetrospectiva update(final EstadoRetrospectiva estadoRetrospectivaActualizar) {
    log.debug("update(EstadoRetrospectiva estadoRetrospectivaActualizar) - start");

    Assert.notNull(estadoRetrospectivaActualizar.getId(),
        "EstadoRetrospectiva id no puede ser null para actualizar un estadoRetrospectiva");

    return repository.findById(estadoRetrospectivaActualizar.getId()).map(estadoRetrospectiva -> {
      estadoRetrospectiva.setNombre(estadoRetrospectivaActualizar.getNombre());
      estadoRetrospectiva.setNombre(estadoRetrospectivaActualizar.getNombre());
      estadoRetrospectiva.setActivo(estadoRetrospectivaActualizar.getActivo());

      EstadoRetrospectiva returnValue = repository.save(estadoRetrospectiva);
      log.debug("update(EstadoRetrospectiva estadoRetrospectivaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EstadoRetrospectivaNotFoundException(estadoRetrospectivaActualizar.getId()));
  }

  /**
   * Elimina todas las entidades {@link EstadoRetrospectiva}.
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
   * Elimina {@link EstadoRetrospectiva} por id.
   *
   * @param id El id de la entidad {@link EstadoRetrospectiva}.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id, "EstadoRetrospectiva id no puede ser null para eliminar una estadoRetrospectiva");
    if (!repository.existsById(id)) {
      throw new EstadoRetrospectivaNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link EstadoRetrospectiva} filtradas y paginadas según
   * los criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link EstadoRetrospectiva} paginadas y
   *         filtradas.
   */
  @Override
  public Page<EstadoRetrospectiva> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<EstadoRetrospectiva> specs = EstadoRetrospectivaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<EstadoRetrospectiva> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link EstadoRetrospectiva} por id.
   *
   * @param id El id de la entidad {@link EstadoRetrospectiva}.
   * @return La entidad {@link EstadoRetrospectiva}.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  @Override
  public EstadoRetrospectiva findById(final Long id) {
    log.debug("findById(final Long id) - start");
    Assert.notNull(id, "EstadoRetrospectiva id no puede ser null para buscar una estadoRetrospectiva por Id");
    final EstadoRetrospectiva estadoRetrospectiva = repository.findById(id)
        .orElseThrow(() -> new EstadoRetrospectivaNotFoundException(id));
    log.debug("findById(final Long id) - end");
    return estadoRetrospectiva;
  }

}
