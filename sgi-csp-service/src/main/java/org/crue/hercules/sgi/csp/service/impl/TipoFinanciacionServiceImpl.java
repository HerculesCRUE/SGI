package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoFinanciacionSpecifications;
import org.crue.hercules.sgi.csp.service.TipoFinanciacionService;
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
public class TipoFinanciacionServiceImpl implements TipoFinanciacionService {

  private final TipoFinanciacionRepository tipoFinanciacionRepository;

  public TipoFinanciacionServiceImpl(TipoFinanciacionRepository tipoFinanciacionRepository) {

    this.tipoFinanciacionRepository = tipoFinanciacionRepository;
  }

  /**
   * Guardar {@link TipoFinanciacion}.
   *
   * @param tipoFinanciacion la entidad {@link TipoFinanciacion} a guardar.
   * @return la entidad {@link TipoFinanciacion} persistida.
   */
  @Override
  @Transactional
  public TipoFinanciacion create(TipoFinanciacion tipoFinanciacion) {
    log.debug("create(TipoFinanciacion tipoFinanciacion) - start");

    Assert.isNull(tipoFinanciacion.getId(),
        "tipoFinanciacion id tiene que ser null para crear un nuevo tipoFinanciacion");
    Assert.isTrue(!(tipoFinanciacionRepository.findByNombreAndActivoIsTrue(tipoFinanciacion.getNombre()).isPresent()),
        "Ya existe TipoFinanciacion con el nombre " + tipoFinanciacion.getNombre());

    tipoFinanciacion.setActivo(Boolean.TRUE);
    TipoFinanciacion returnValue = tipoFinanciacionRepository.save(tipoFinanciacion);

    log.debug("create(TipoFinanciacion tipoFinanciacion) - end");
    return returnValue;

  }

  /**
   * Actualizar {@link TipoFinanciacion}.
   *
   * @param tipoFinanciacionActualizar la entidad {@link TipoFinanciacion} a
   *                                   actualizar.
   * @return la entidad {@link TipoFinanciacion} persistida.
   */
  @Override
  @Transactional
  public TipoFinanciacion update(TipoFinanciacion tipoFinanciacionActualizar) {
    log.debug("update(TipoFinanciacion tipoFinanciacionActualizar) - start");

    Assert.notNull(tipoFinanciacionActualizar.getId(), "TipoFinanciacion id no puede ser null para actualizar");
    tipoFinanciacionRepository.findByNombreAndActivoIsTrue(tipoFinanciacionActualizar.getNombre())
        .ifPresent((tipoFinanciacionExistente) -> {
          Assert.isTrue(tipoFinanciacionActualizar.getId() == tipoFinanciacionExistente.getId(),
              "Ya existe un TipoFinanciacion con el nombre " + tipoFinanciacionExistente.getNombre());
        });

    return tipoFinanciacionRepository.findById(tipoFinanciacionActualizar.getId()).map(tipoFinanciacion -> {
      tipoFinanciacion.setNombre(tipoFinanciacionActualizar.getNombre());
      tipoFinanciacion.setDescripcion(tipoFinanciacionActualizar.getDescripcion());

      TipoFinanciacion returnValue = tipoFinanciacionRepository.save(tipoFinanciacion);
      log.debug("update(TipoFinanciacion tipoFinanciacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFinanciacionNotFoundException(tipoFinanciacionActualizar.getId()));

  }

  /**
   * Obtener todas las entidades {@link TipoFinanciacion} activos paginadas y/o
   * filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFinanciacion} paginadas y/o
   *         filtradas
   */
  @Override
  public Page<TipoFinanciacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoFinanciacion> specs = TipoFinanciacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoFinanciacion> returnValue = tipoFinanciacionRepository.findAll(specs, pageable);

    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoFinanciacion} paginadas y/o filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFinanciacion} paginadas y/o
   *         filtradas
   */
  @Override
  public Page<TipoFinanciacion> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");
    Specification<TipoFinanciacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoFinanciacion> returnValue = tipoFinanciacionRepository.findAll(specs, pageable);

    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoFinanciacion} por id.
   *
   * @param id el id de la entidad {@link TipoFinanciacion}.
   * @return la entidad {@link TipoFinanciacion}.
   */
  @Override
  public TipoFinanciacion findById(Long id) {
    log.debug("findById(Long id)- start");
    TipoFinanciacion tipoFinanciacion = tipoFinanciacionRepository.findById(id)
        .orElseThrow(() -> new TipoFinanciacionNotFoundException(id));
    log.debug("findById(Long id) - end");
    return tipoFinanciacion;

  }

  /**
   * Reactiva el {@link TipoFinanciacion}.
   *
   * @param id Id del {@link TipoFinanciacion}.
   * @return la entidad {@link TipoFinanciacion} persistida.
   */
  @Override
  @Transactional
  public TipoFinanciacion enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "TipoFinanciacion id no puede ser null para reactivar un TipoFinanciacion");

    return tipoFinanciacionRepository.findById(id).map(tipoFinanciacion -> {
      if (tipoFinanciacion.getActivo()) {
        // Si esta activo no se hace nada
        return tipoFinanciacion;
      }

      tipoFinanciacionRepository.findByNombreAndActivoIsTrue(tipoFinanciacion.getNombre())
          .ifPresent((tipoFinanciacionExistente) -> {
            Assert.isTrue(tipoFinanciacion.getId() == tipoFinanciacionExistente.getId(),
                "Ya existe un TipoFinanciacion con el nombre " + tipoFinanciacion.getNombre());
          });

      tipoFinanciacion.setActivo(true);

      TipoFinanciacion returnValue = tipoFinanciacionRepository.save(tipoFinanciacion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFinanciacionNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoFinanciacion}.
   *
   * @param id el id de la entidad {@link TipoFinanciacion}.
   * @return la entidad {@link TipoFinanciacion} actualizadas.
   */
  @Override
  @Transactional
  public TipoFinanciacion disable(Long id) throws TipoFinanciacionNotFoundException {
    log.debug("disable(Long id) - start");
    Assert.notNull(id, "TipoFinanciacion id no puede ser null para reactivar un TipoFinanciacion");

    return tipoFinanciacionRepository.findById(id).map(tipoFinanciacion -> {
      if (!tipoFinanciacion.getActivo()) {
        // Si no esta activo no se hace nada
        return tipoFinanciacion;
      }

      tipoFinanciacion.setActivo(false);

      TipoFinanciacion returnValue = tipoFinanciacionRepository.save(tipoFinanciacion);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFinanciacionNotFoundException(id));

  }

}
