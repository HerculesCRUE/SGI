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
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
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
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_MODEL_TIPO_ENLACE = "org.crue.hercules.sgi.csp.model.TipoEnlace.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_EXISTS = "org.springframework.util.Assert.entity.modeloEjecucion.exists.message";

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

    AssertHelper.idIsNull(modeloTipoEnlace.getId(), ModeloTipoEnlace.class);
    AssertHelper.idNotNull(modeloTipoEnlace.getModeloEjecucion().getId(), ModeloEjecucion.class);
    AssertHelper.idNotNull(modeloTipoEnlace.getTipoEnlace().getId(), TipoEnlace.class);

    modeloTipoEnlace
        .setModeloEjecucion(modeloEjecucionRepository.findById(modeloTipoEnlace.getModeloEjecucion().getId())
            .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloTipoEnlace.getModeloEjecucion().getId())));

    modeloTipoEnlace.setTipoEnlace(tipoEnlaceRepository.findById(modeloTipoEnlace.getTipoEnlace().getId())
        .orElseThrow(() -> new TipoEnlaceNotFoundException(modeloTipoEnlace.getTipoEnlace().getId())));
    Assert.isTrue(modeloTipoEnlace.getTipoEnlace().getActivo(),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_INACTIVO)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ENLACE))
            .parameter(MSG_KEY_FIELD, modeloTipoEnlace.getTipoEnlace().getNombre())
            .build());

    modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(modeloTipoEnlace.getModeloEjecucion().getId(),
        modeloTipoEnlace.getTipoEnlace().getId()).ifPresent(modeloTipoEnlaceExistente -> {

          Assert.isTrue(!modeloTipoEnlaceExistente.getActivo(),
              () -> ProblemMessage.builder()
                  .key(MSG_MODELO_EJECUCION_EXISTS)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ENLACE))
                  .build());

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

    AssertHelper.idNotNull(id, ModeloTipoEnlace.class);

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
