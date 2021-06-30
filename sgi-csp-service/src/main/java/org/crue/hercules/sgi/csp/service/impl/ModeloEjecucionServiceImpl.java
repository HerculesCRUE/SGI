package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloEjecucionSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloEjecucionService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ModeloEjecucion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloEjecucionServiceImpl implements ModeloEjecucionService {

  private final ModeloEjecucionRepository modeloEjecucionRepository;

  public ModeloEjecucionServiceImpl(ModeloEjecucionRepository modeloEjecucionRepository) {
    this.modeloEjecucionRepository = modeloEjecucionRepository;
  }

  /**
   * Guardar un nuevo {@link ModeloEjecucion}.
   *
   * @param modeloEjecucion la entidad {@link ModeloEjecucion} a guardar.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Override
  @Transactional
  public ModeloEjecucion create(ModeloEjecucion modeloEjecucion) {
    log.debug("create(ModeloEjecucion modeloEjecucion) - start");

    Assert.isNull(modeloEjecucion.getId(), "ModeloEjecucion id tiene que ser null para crear un nuevo ModeloEjecucion");
    Assert.isTrue(!(modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucion.getNombre()).isPresent()),
        "Ya existe un ModeloEjecucion activo con el nombre '" + modeloEjecucion.getNombre() + "'");

    modeloEjecucion.setActivo(true);

    ModeloEjecucion returnValue = modeloEjecucionRepository.save(modeloEjecucion);

    log.debug("create(ModeloEjecucion modeloEjecucion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ModeloEjecucion}.
   *
   * @param modeloEjecucionActualizar la entidad {@link ModeloEjecucion} a
   *                                  actualizar.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Override
  @Transactional
  public ModeloEjecucion update(ModeloEjecucion modeloEjecucionActualizar) {
    log.debug("update(ModeloEjecucion modeloEjecucionActualizar) - start");

    Assert.notNull(modeloEjecucionActualizar.getId(),
        "ModeloEjecucion id no puede ser null para actualizar un ModeloEjecucion");
    modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucionActualizar.getNombre())
        .ifPresent((modeloEjecucionExistente) -> {
          Assert.isTrue(modeloEjecucionActualizar.getId() == modeloEjecucionExistente.getId(),
              "Ya existe un ModeloEjecucion activo con el nombre '" + modeloEjecucionExistente.getNombre() + "'");
        });

    return modeloEjecucionRepository.findById(modeloEjecucionActualizar.getId()).map(modeloEjecucion -> {
      modeloEjecucion.setNombre(modeloEjecucionActualizar.getNombre());
      modeloEjecucion.setDescripcion(modeloEjecucionActualizar.getDescripcion());

      ModeloEjecucion returnValue = modeloEjecucionRepository.save(modeloEjecucion);
      log.debug("update(ModeloEjecucion modeloEjecucionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloEjecucionActualizar.getId()));
  }

  /**
   * Reactiva el {@link ModeloEjecucion}.
   *
   * @param id Id del {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Override
  @Transactional
  public ModeloEjecucion enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "ModeloEjecucion id no puede ser null para reactivar un ModeloEjecucion");

    return modeloEjecucionRepository.findById(id).map(modeloEjecucion -> {
      if (modeloEjecucion.getActivo()) {
        return modeloEjecucion;
      }

      Assert.isTrue(!(modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucion.getNombre()).isPresent()),
          "Ya existe un ModeloEjecucion activo con el nombre '" + modeloEjecucion.getNombre() + "'");

      modeloEjecucion.setActivo(true);
      ModeloEjecucion returnValue = modeloEjecucionRepository.save(modeloEjecucion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloEjecucionNotFoundException(id));
  }

  /**
   * Desactiva el {@link ModeloEjecucion}.
   *
   * @param id Id del {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Override
  @Transactional
  public ModeloEjecucion disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "ModeloEjecucion id no puede ser null para desactivar un ModeloEjecucion");

    return modeloEjecucionRepository.findById(id).map(modeloEjecucion -> {
      if (!modeloEjecucion.getActivo()) {
        return modeloEjecucion;
      }

      modeloEjecucion.setActivo(false);
      ModeloEjecucion returnValue = modeloEjecucionRepository.save(modeloEjecucion);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloEjecucionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link ModeloEjecucion} activas paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ModeloEjecucion} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<ModeloEjecucion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ModeloEjecucion> specs = ModeloEjecucionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloEjecucion> returnValue = modeloEjecucionRepository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ModeloEjecucion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ModeloEjecucion} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<ModeloEjecucion> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");
    Specification<ModeloEjecucion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ModeloEjecucion> returnValue = modeloEjecucionRepository.findAll(specs, pageable);
    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ModeloEjecucion} por su id.
   *
   * @param id el id de la entidad {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion}.
   */
  @Override
  public ModeloEjecucion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ModeloEjecucion returnValue = modeloEjecucionRepository.findById(id)
        .orElseThrow(() -> new ModeloEjecucionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
