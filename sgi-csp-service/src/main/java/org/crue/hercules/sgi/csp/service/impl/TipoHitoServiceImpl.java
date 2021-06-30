package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoHitoSpecifications;
import org.crue.hercules.sgi.csp.service.TipoHitoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoHitoServiceImpl implements TipoHitoService {

  private final TipoHitoRepository tipoHitoRepository;

  public TipoHitoServiceImpl(TipoHitoRepository tipoHitoRepository) {
    this.tipoHitoRepository = tipoHitoRepository;
  }

  /**
   * Guardar {@link TipoHito}.
   *
   * @param tipoHito la entidad {@link TipoHito} a guardar.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  public TipoHito create(TipoHito tipoHito) {
    log.debug("create(TipoHito tipoHito) - start");

    Assert.isNull(tipoHito.getId(), "TipoHito id tiene que ser null para crear un nuevo tipoHito");
    Assert.isTrue(!(tipoHitoRepository.findByNombreAndActivoIsTrue(tipoHito.getNombre()).isPresent()),
        "Ya existe un TipoHito activo con el nombre '" + tipoHito.getNombre() + "'");

    tipoHito.setActivo(Boolean.TRUE);
    TipoHito returnValue = tipoHitoRepository.save(tipoHito);

    log.debug("create(TipoHito tipoHito) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoHito}.
   *
   * @param tipoHitoActualizar la entidad {@link TipoHito} a actualizar.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  public TipoHito update(TipoHito tipoHitoActualizar) {
    log.debug("update(TipoHito tipoHitoActualizar) - start");

    Assert.notNull(tipoHitoActualizar.getId(), "TipoHito id no puede ser null para actualizar");
    tipoHitoRepository.findByNombreAndActivoIsTrue(tipoHitoActualizar.getNombre()).ifPresent((tipoHitoExistente) -> {
      Assert.isTrue(tipoHitoActualizar.getId() == tipoHitoExistente.getId(),
          "Ya existe un TipoHito activo con el nombre '" + tipoHitoExistente.getNombre() + "'");
    });

    return tipoHitoRepository.findById(tipoHitoActualizar.getId()).map(tipoHito -> {
      tipoHito.setNombre(tipoHitoActualizar.getNombre());
      tipoHito.setDescripcion(tipoHitoActualizar.getDescripcion());

      TipoHito returnValue = tipoHitoRepository.save(tipoHito);
      log.debug("update(TipoHito tipoHitoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoHitoNotFoundException(tipoHitoActualizar.getId()));

  }

  /**
   * Obtener todas las entidades {@link TipoHito} activas paginadas
   *
   * @param pageable la información de la paginación.
   * @param query    información del filtro.
   * @return la lista de entidades {@link TipoHito} paginadas
   */
  @Override
  public Page<TipoHito> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoHito> specs = TipoHitoSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoHito> returnValue = tipoHitoRepository.findAll(specs, pageable);

    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoHito} paginadas
   *
   * @param pageable la información de la paginación.
   * @param query    información del filtro.
   * @return la lista de entidades {@link TipoHito} paginadas
   */
  @Override
  public Page<TipoHito> findAllTodos(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoHito> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoHito> returnValue = tipoHitoRepository.findAll(specs, pageable);

    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoHito} por id.
   *
   * @param id el id de la entidad {@link TipoHito}.
   * @return la entidad {@link TipoHito}.
   */
  @Override
  public TipoHito findById(Long id) throws TipoHitoNotFoundException {
    log.debug("findById(Long id) id:{}- start", id);
    TipoHito tipoHito = tipoHitoRepository.findById(id).orElseThrow(() -> new TipoHitoNotFoundException(id));
    log.debug("findById(Long id) id:{}- end", id);
    return tipoHito;
  }

  /**
   * Reactiva el {@link TipoHito}.
   *
   * @param id Id del {@link TipoHito}.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  public TipoHito enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "TipoHito id no puede ser null para reactivar un TipoHito");

    return tipoHitoRepository.findById(id).map(tipoHito -> {
      if (tipoHito.getActivo()) {
        return tipoHito;
      }

      Assert.isTrue(!(tipoHitoRepository.findByNombreAndActivoIsTrue(tipoHito.getNombre()).isPresent()),
          "Ya existe un TipoHito activo con el nombre '" + tipoHito.getNombre() + "'");

      tipoHito.setActivo(true);
      TipoHito returnValue = tipoHitoRepository.save(tipoHito);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoHitoNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoHito}.
   *
   * @param id Id del {@link TipoHito}.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  public TipoHito disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "TipoHito id no puede ser null para desactivar un TipoHito");

    return tipoHitoRepository.findById(id).map(tipoHito -> {
      if (!tipoHito.getActivo()) {
        return tipoHito;
      }

      tipoHito.setActivo(false);
      TipoHito returnValue = tipoHitoRepository.save(tipoHito);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoHitoNotFoundException(id));
  }

}