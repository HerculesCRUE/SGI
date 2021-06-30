package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloUnidadNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloUnidadSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloUnidadService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloUnidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloUnidadServiceImpl implements ModeloUnidadService {

  private final ModeloEjecucionRepository modeloEjecucionRepository;
  private final ModeloUnidadRepository modeloUnidadRepository;

  public ModeloUnidadServiceImpl(ModeloEjecucionRepository modeloEjecucionRepository,
      ModeloUnidadRepository modeloUnidadRepository) {
    this.modeloEjecucionRepository = modeloEjecucionRepository;
    this.modeloUnidadRepository = modeloUnidadRepository;
  }

  /**
   * Guarda la entidad {@link ModeloUnidad}.
   * 
   * @param modeloUnidad la entidad {@link ModeloUnidad} a guardar.
   * @return la entidad {@link ModeloUnidad} persistida.
   */
  @Transactional
  @Override
  public ModeloUnidad create(ModeloUnidad modeloUnidad) {
    log.debug("create(ModeloUnidad modeloUnidad) - start");

    Assert.isNull(modeloUnidad.getId(), "Id tiene que ser null para crear ModeloUnidad");
    Assert.notNull(modeloUnidad.getModeloEjecucion().getId(),
        "Id ModeloEjecucion no puede ser null para crear un ModeloUnidad");

    modeloUnidad.setModeloEjecucion(modeloEjecucionRepository.findById(modeloUnidad.getModeloEjecucion().getId())
        .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloUnidad.getModeloEjecucion().getId())));

    modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(modeloUnidad.getModeloEjecucion().getId(),
        modeloUnidad.getUnidadGestionRef()).ifPresent(modeloUnidadExistente -> {

          Assert.isTrue(!modeloUnidadExistente.getActivo(),
              "Ya existe una asociación activa para ese ModeloEjecucion y esa Unidad");

          modeloUnidad.setId(modeloUnidadExistente.getId());
        });

    modeloUnidad.setActivo(true);
    ModeloUnidad returnValue = modeloUnidadRepository.save(modeloUnidad);
    log.debug("create(ModeloUnidad modeloUnidad) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ModeloUnidad}.
   *
   * @param id Id del {@link ModeloUnidad}.
   * @return la entidad {@link ModeloUnidad} persistida.
   */
  @Transactional
  @Override
  public ModeloUnidad disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "ModeloUnidad id no puede ser null para desactivar un ModeloTipoEnlace");

    return modeloUnidadRepository.findById(id).map(modeloUnidad -> {
      modeloUnidad.setActivo(false);

      ModeloUnidad returnValue = modeloUnidadRepository.save(modeloUnidad);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloUnidadNotFoundException(id));
  }

  /**
   * Obtiene los {@link ModeloUnidad} activos.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link ModeloUnidad} del
   *         {@link ModeloEjecucion} paginadas.
   */
  public Page<ModeloUnidad> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ModeloUnidad> specs = ModeloUnidadSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloUnidad> returnValue = modeloUnidadRepository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ModeloTipoEnlace} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoEnlace}.
   * @return la entidad {@link ModeloTipoEnlace}.
   */
  @Override
  public ModeloUnidad findById(final Long id) {
    log.debug("findById(Long id) - start");
    final ModeloUnidad returnValue = modeloUnidadRepository.findById(id)
        .orElseThrow(() -> new ModeloUnidadNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ModeloUnidad} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloUnidad} del
   *         {@link ModeloEjecucion} paginadas.
   */
  public Page<ModeloUnidad> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloUnidad> specs = ModeloUnidadSpecifications.activos()
        .and(ModeloUnidadSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloUnidad> returnValue = modeloUnidadRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

}
