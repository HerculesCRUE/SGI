package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.ApartadoNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.service.ApartadoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Apartado}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ApartadoServiceImpl implements ApartadoService {

  private final ApartadoRepository repository;

  public ApartadoServiceImpl(ApartadoRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según los
   * criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Page<Apartado> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Apartado> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Apartado> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link Apartado} por id.
   *
   * @param id El id de la entidad {@link Apartado}.
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa Id.
   */
  @Override
  public Apartado findById(final Long id) {
    log.debug("findById(final Long id) - start");
    Assert.notNull(id, "Apartado id no puede ser null para buscar un apartado por Id");
    final Apartado apartado = repository.findById(id).orElseThrow(() -> new ApartadoNotFoundException(id));
    log.debug("findById(final Long id) - end");
    return apartado;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su {@link Bloque}. Solamente se devuelven los Apartados de primer nivel
   * (sin padre).
   *
   * @param id       id del {@link Bloque}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Page<Apartado> findByBloqueId(Long id, Pageable pageable) {
    log.debug("findByBloqueId(Long id, Pageable pageable) - start");
    Assert.notNull(id, "Id no puede ser null para buscar un apartado por el Id de su Bloque");
    final Page<Apartado> apartado = repository.findByBloqueIdAndPadreIsNull(id, pageable);
    log.debug("findByBloqueId(Long id, Pageable pageable) - end");
    return apartado;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su padre {@link Apartado}.
   *
   * @param id       id del {@link Apartado}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Page<Apartado> findByPadreId(Long id, Pageable pageable) {
    log.debug("findByPadreId(Long id, Pageable pageable) - start");
    Assert.notNull(id, "Id no puede ser null para buscar un apartado por el Id de su padre");
    final Page<Apartado> apartado = repository.findByPadreId(id, pageable);
    log.debug("findByPadreId(Long id, Pageable pageable) - end");
    return apartado;
  }

}
