package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.TipoFinalidadNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.repository.TipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoFinalidadSpecifications;
import org.crue.hercules.sgi.csp.service.TipoFinalidadService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link TipoFinalidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoFinalidadServiceImpl implements TipoFinalidadService {

  private final TipoFinalidadRepository repository;

  public TipoFinalidadServiceImpl(TipoFinalidadRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link TipoFinalidad}.
   * 
   * @param tipoFinalidad la entidad {@link TipoFinalidad} a guardar.
   * @return TipoFinalidad la entidad {@link TipoFinalidad} persistida.
   */
  @Override
  @Transactional
  public TipoFinalidad create(TipoFinalidad tipoFinalidad) {
    log.debug("create(TipoFinalidad tipoFinalidad) - start");

    Assert.isNull(tipoFinalidad.getId(), "Id tiene que ser null para crear TipoFinalidad");
    Assert.isTrue(!(repository.findByNombreAndActivoIsTrue(tipoFinalidad.getNombre()).isPresent()),
        "Ya existe un TipoFinalidad activo con el nombre '" + tipoFinalidad.getNombre() + "'");

    tipoFinalidad.setActivo(Boolean.TRUE);
    TipoFinalidad returnValue = repository.save(tipoFinalidad);

    log.debug("create(TipoFinalidad tipoFinalidad) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link TipoFinalidad}.
   * 
   * @param tipoFinalidad tipoFinalidadActualizar {@link TipoFinalidad} con los
   *                      datos actualizados.
   * @return {@link TipoFinalidad} actualizado.
   */
  @Override
  @Transactional
  public TipoFinalidad update(TipoFinalidad tipoFinalidad) {
    log.debug("update(TipoFinalidad tipoFinalidad) - start");

    Assert.notNull(tipoFinalidad.getId(), "Id no puede ser null para actualizar TipoFinalidad");
    repository.findByNombreAndActivoIsTrue(tipoFinalidad.getNombre()).ifPresent((tipoFinalidadExistente) -> {
      Assert.isTrue(tipoFinalidad.getId() == tipoFinalidadExistente.getId(),
          "Ya existe un TipoFinalidad activo con el nombre '" + tipoFinalidadExistente.getNombre() + "'");
    });

    return repository.findById(tipoFinalidad.getId()).map((data) -> {
      data.setNombre(tipoFinalidad.getNombre());
      data.setDescripcion(tipoFinalidad.getDescripcion());

      TipoFinalidad returnValue = repository.save(data);
      log.debug("update(TipoFinalidad tipoFinalidad) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFinalidadNotFoundException(tipoFinalidad.getId()));
  }

  /**
   * Reactiva el {@link TipoFinalidad}.
   *
   * @param id Id del {@link TipoFinalidad}.
   * @return la entidad {@link TipoFinalidad} persistida.
   */
  @Override
  @Transactional
  public TipoFinalidad enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "TipoFinalidad id no puede ser null para reactivar un TipoFinalidad");

    return repository.findById(id).map(tipoFinalidad -> {
      if (tipoFinalidad.getActivo()) {
        return tipoFinalidad;
      }

      Assert.isTrue(!(repository.findByNombreAndActivoIsTrue(tipoFinalidad.getNombre()).isPresent()),
          "Ya existe un TipoFinalidad activo con el nombre '" + tipoFinalidad.getNombre() + "'");

      tipoFinalidad.setActivo(true);
      TipoFinalidad returnValue = repository.save(tipoFinalidad);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFinalidadNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoFinalidad}.
   *
   * @param id Id del {@link TipoFinalidad}.
   * @return la entidad {@link TipoFinalidad} persistida.
   */
  @Override
  @Transactional
  public TipoFinalidad disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "TipoFinalidad id no puede ser null para desactivar un TipoFinalidad");

    return repository.findById(id).map(tipoFinalidad -> {
      if (!tipoFinalidad.getActivo()) {
        return tipoFinalidad;
      }

      tipoFinalidad.setActivo(false);
      TipoFinalidad returnValue = repository.save(tipoFinalidad);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFinalidadNotFoundException(id));
  }

  /**
   * Obtiene todas las entidades {@link TipoFinalidad} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoFinalidad} paginadas y filtradas.
   */
  @Override
  public Page<TipoFinalidad> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<TipoFinalidad> specs = TipoFinalidadSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoFinalidad> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link TipoFinalidad} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoFinalidad} paginadas y filtradas.
   */
  @Override
  public Page<TipoFinalidad> findAllTodos(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<TipoFinalidad> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<TipoFinalidad> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoFinalidad} por id.
   * 
   * @param id Identificador de la entidad {@link TipoFinalidad}.
   * @return TipoFinalidad la entidad {@link TipoFinalidad}.
   */
  @Override
  public TipoFinalidad findById(Long id) {
    log.debug("findById(Long id) - start");
    final TipoFinalidad returnValue = repository.findById(id).orElseThrow(() -> new TipoFinalidadNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
