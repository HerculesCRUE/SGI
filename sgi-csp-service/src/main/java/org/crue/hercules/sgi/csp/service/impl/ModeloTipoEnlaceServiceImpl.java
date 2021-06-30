package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.TipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoEnlaceSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoEnlaceService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloTipoEnlace}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoEnlaceServiceImpl implements ModeloTipoEnlaceService {

  private final ModeloEjecucionRepository modeloEjecucionRepository;
  private final ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;
  private final TipoEnlaceRepository tipoEnlaceRepository;

  public ModeloTipoEnlaceServiceImpl(ModeloEjecucionRepository modeloEjecucionRepository,
      ModeloTipoEnlaceRepository modeloTipoEnlaceRepository, TipoEnlaceRepository tipoEnlaceRepository) {
    this.modeloEjecucionRepository = modeloEjecucionRepository;
    this.modeloTipoEnlaceRepository = modeloTipoEnlaceRepository;
    this.tipoEnlaceRepository = tipoEnlaceRepository;
  }

  /**
   * Guarda la entidad {@link ModeloTipoEnlace}.
   * 
   * @param modeloTipoEnlace la entidad {@link ModeloTipoEnlace} a guardar.
   * @return la entidad {@link ModeloTipoEnlace} persistida.
   */
  @Transactional
  @Override
  public ModeloTipoEnlace create(ModeloTipoEnlace modeloTipoEnlace) {
    log.debug("create(ModeloTipoEnlace modeloTipoEnlace) - start");

    Assert.isNull(modeloTipoEnlace.getId(), "Id tiene que ser null para crear ModeloTipoEnlace");
    Assert.notNull(modeloTipoEnlace.getModeloEjecucion().getId(),
        "Id ModeloEjecucion no puede ser null para crear un ModeloTipoEnlace");
    Assert.notNull(modeloTipoEnlace.getTipoEnlace().getId(),
        "Id TipoEnlace no puede ser null para crear un ModeloTipoEnlace");

    modeloTipoEnlace
        .setModeloEjecucion(modeloEjecucionRepository.findById(modeloTipoEnlace.getModeloEjecucion().getId())
            .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloTipoEnlace.getModeloEjecucion().getId())));

    modeloTipoEnlace.setTipoEnlace(tipoEnlaceRepository.findById(modeloTipoEnlace.getTipoEnlace().getId())
        .orElseThrow(() -> new TipoEnlaceNotFoundException(modeloTipoEnlace.getTipoEnlace().getId())));
    Assert.isTrue(modeloTipoEnlace.getTipoEnlace().getActivo(), "El TipoEnlace debe estar Activo");

    modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(modeloTipoEnlace.getModeloEjecucion().getId(),
        modeloTipoEnlace.getTipoEnlace().getId()).ifPresent(modeloTipoEnlaceExistente -> {

          Assert.isTrue(!modeloTipoEnlaceExistente.getActivo(),
              "Ya existe una asociación activa para ese ModeloEjecucion y ese TipoEnlace");

          modeloTipoEnlace.setId(modeloTipoEnlaceExistente.getId());
        });

    modeloTipoEnlace.setActivo(true);
    ModeloTipoEnlace returnValue = modeloTipoEnlaceRepository.save(modeloTipoEnlace);
    log.debug("create(ModeloTipoEnlace modeloTipoEnlace) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ModeloTipoEnlace}.
   *
   * @param id Id del {@link ModeloTipoEnlace}.
   * @return la entidad {@link ModeloTipoEnlace} persistida.
   */
  @Transactional
  @Override
  public ModeloTipoEnlace disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "ModeloTipoEnlace id no puede ser null para desactivar un ModeloTipoEnlace");

    return modeloTipoEnlaceRepository.findById(id).map(modeloTipoEnlace -> {
      modeloTipoEnlace.setActivo(false);

      ModeloTipoEnlace returnValue = modeloTipoEnlaceRepository.save(modeloTipoEnlace);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloTipoEnlaceNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link ModeloTipoEnlace} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoEnlace}.
   * @return la entidad {@link ModeloTipoEnlace}.
   */
  @Override
  public ModeloTipoEnlace findById(final Long id) {
    log.debug("findById(Long id) - start");
    final ModeloTipoEnlace returnValue = modeloTipoEnlaceRepository.findById(id)
        .orElseThrow(() -> new ModeloTipoEnlaceNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ModeloTipoEnlace} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link TipoEnlace} del {@link ModeloEjecucion}
   *         paginadas.
   */
  @Override
  public Page<ModeloTipoEnlace> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloTipoEnlace> specs = ModeloTipoEnlaceSpecifications.activos()
        .and(ModeloTipoEnlaceSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoEnlace> returnValue = modeloTipoEnlaceRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

}
