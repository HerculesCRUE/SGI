package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoFinalidadNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinalidadNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoFinalidadSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoFinalidadService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloTipoFinalidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoFinalidadServiceImpl implements ModeloTipoFinalidadService {

  private final ModeloTipoFinalidadRepository modeloTipoFinalidadRepository;
  private final ModeloEjecucionRepository modeloEjecucionRepository;
  private final TipoFinalidadRepository tipoFinalidadRepository;

  public ModeloTipoFinalidadServiceImpl(ModeloTipoFinalidadRepository modeloTipoFinalidadRepository,
      ModeloEjecucionRepository modeloEjecucionRepository, TipoFinalidadRepository tipoFinalidadRepository) {
    this.modeloTipoFinalidadRepository = modeloTipoFinalidadRepository;
    this.modeloEjecucionRepository = modeloEjecucionRepository;
    this.tipoFinalidadRepository = tipoFinalidadRepository;
  }

  /**
   * Guarda la entidad {@link ModeloTipoFinalidad}.
   * 
   * @param modeloTipoFinalidad la entidad {@link ModeloTipoFinalidad} a guardar.
   * @return ModeloTipoFinalidad la entidad {@link ModeloTipoFinalidad}
   *         persistida.
   */
  @Override
  @Transactional
  public ModeloTipoFinalidad create(ModeloTipoFinalidad modeloTipoFinalidad) {
    log.debug("create(ModeloTipoFinalidad modeloTipoFinalidad) - start");

    // Id vacío al crear
    Assert.isNull(modeloTipoFinalidad.getId(), "Id tiene que ser null para crear ModeloTipoFinalidad");

    // ModeloEjecucion existe
    Assert.notNull(modeloTipoFinalidad.getModeloEjecucion().getId(),
        "Id ModeloEjecucion no puede ser null para actualizar ModeloTipoFinalidad");
    modeloTipoFinalidad
        .setModeloEjecucion(modeloEjecucionRepository.findById(modeloTipoFinalidad.getModeloEjecucion().getId())
            .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloTipoFinalidad.getModeloEjecucion().getId())));

    // TipoFinalidad existe y activo
    Assert.notNull(modeloTipoFinalidad.getTipoFinalidad().getId(),
        "Id TipoFinalidad no puede ser null para actualizar ModeloTipoFinalidad");
    modeloTipoFinalidad
        .setTipoFinalidad(tipoFinalidadRepository.findById(modeloTipoFinalidad.getTipoFinalidad().getId())
            .orElseThrow(() -> new TipoFinalidadNotFoundException(modeloTipoFinalidad.getTipoFinalidad().getId())));
    Assert.isTrue(modeloTipoFinalidad.getTipoFinalidad().getActivo(), "El TipoFinalidad debe estar Activo");

    // Comprobar si ya existe una relación activa entre Modelo y Tipo
    modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(
        modeloTipoFinalidad.getModeloEjecucion().getId(), modeloTipoFinalidad.getTipoFinalidad().getId())
        .ifPresent(modeloTipoFinalidadExistente -> {

          // Si ya está activa no se podrá insertar TipoFinalidad
          Assert.isTrue(!modeloTipoFinalidadExistente.getActivo(),
              "El TipoFinalidad ya se encuentra asociado al  ModeloEjecucion");

          // Se está desactivado se activará la relación existente
          modeloTipoFinalidad.setId(modeloTipoFinalidadExistente.getId());

        });

    // Simpre Activo en la creación
    modeloTipoFinalidad.setActivo(Boolean.TRUE);

    ModeloTipoFinalidad returnValue = modeloTipoFinalidadRepository.save(modeloTipoFinalidad);
    log.debug("create(ModeloTipoFinalidad modeloTipoFinalidad) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ModeloTipoFinalidad}.
   *
   * @param id Id del {@link ModeloTipoFinalidad}.
   * @return la entidad {@link ModeloTipoFinalidad} persistida.
   */
  @Override
  @Transactional
  public ModeloTipoFinalidad disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "ModeloTipoFinalidad id no puede ser null para desactivar un ModeloTipoFinalidad");

    return modeloTipoFinalidadRepository.findById(id).map(modeloTipoFinalidad -> {
      modeloTipoFinalidad.setActivo(false);

      ModeloTipoFinalidad returnValue = modeloTipoFinalidadRepository.save(modeloTipoFinalidad);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloTipoFinalidadNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link ModeloTipoFinalidad} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoFinalidad}.
   * @return ModeloTipoFinalidad la entidad {@link ModeloTipoFinalidad}.
   */
  @Override
  public ModeloTipoFinalidad findById(Long id) {
    log.debug("findById(Long id) - start");
    final ModeloTipoFinalidad returnValue = modeloTipoFinalidadRepository.findById(id)
        .orElseThrow(() -> new ModeloTipoFinalidadNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ModeloTipoEnlace} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoEnlace} del
   *         {@link ModeloEjecucion} paginadas.
   */
  public Page<ModeloTipoFinalidad> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloTipoFinalidad> specs = ModeloTipoFinalidadSpecifications.activos()
        .and(ModeloTipoFinalidadSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoFinalidad> returnValue = modeloTipoFinalidadRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

}
