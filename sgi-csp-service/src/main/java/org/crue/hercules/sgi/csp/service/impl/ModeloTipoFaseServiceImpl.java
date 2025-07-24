package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoFaseSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoFaseService;
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
 * Service Implementation para gestion {@link ModeloTipoFase}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoFaseServiceImpl implements ModeloTipoFaseService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_MODELO_TIPO_FASE_SELECCIONAR_CONVOCATORIA_SOLICITUD_PROYECTO = "modeloTipoFase.seleccionar.convocatoriaSolicitudProyecto";
  private static final String MSG_MODEL_TIPO_FASE = "org.crue.hercules.sgi.csp.model.TipoFase.message";
  private static final String MSG_MODEL_MODELO_TIPO_FASE = "org.crue.hercules.sgi.csp.model.ModeloTipoFase.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_INACTIVO = "org.springframework.util.Assert.entity.modeloEjecucion.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_EXISTS = "org.springframework.util.Assert.entity.modeloEjecucion.exists.message";

  private final ModeloTipoFaseRepository modeloTipoFaseRepository;

  private final TipoFaseRepository tipoFaseRepository;

  private final ModeloEjecucionRepository modeloEjecucionRepository;

  public ModeloTipoFaseServiceImpl(ModeloTipoFaseRepository modeloTipoFaseRepository,
      TipoFaseRepository tipoFaseRepository, ModeloEjecucionRepository modeloEjecucionRepository) {
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.tipoFaseRepository = tipoFaseRepository;
    this.modeloEjecucionRepository = modeloEjecucionRepository;
  }

  /**
   * Guardar {@link ModeloTipoFase}.
   *
   * @param modeloTipoFase la entidad {@link ModeloTipoFase} a guardar.
   * @return la entidad {@link ModeloTipoFase} persistida.
   */
  @Override
  @Transactional
  public ModeloTipoFase create(ModeloTipoFase modeloTipoFase) {
    log.debug("create(ModeloTipoFase modeloTipoFase) - start");

    AssertHelper.idIsNull(modeloTipoFase.getId(), ModeloTipoFase.class);
    AssertHelper.idNotNull(modeloTipoFase.getModeloEjecucion().getId(), ModeloEjecucion.class);
    AssertHelper.idNotNull(modeloTipoFase.getTipoFase().getId(), TipoFase.class);
    modeloTipoFase.setTipoFase(tipoFaseRepository.findById(modeloTipoFase.getTipoFase().getId())
        .orElseThrow(() -> new TipoFaseNotFoundException(modeloTipoFase.getTipoFase().getId())));
    modeloTipoFase.setModeloEjecucion(modeloEjecucionRepository.findById(modeloTipoFase.getModeloEjecucion().getId())
        .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloTipoFase.getModeloEjecucion().getId())));
    Assert.isTrue(modeloTipoFase.getTipoFase().getActivo(),
        () -> ProblemMessage.builder()
            .key(MSG_MODELO_EJECUCION_INACTIVO)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
            .parameter(MSG_KEY_FIELD, modeloTipoFase.getTipoFase().getNombre())
            .parameter(MSG_KEY_MODELO, modeloTipoFase.getModeloEjecucion().getNombre())
            .build());
    modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(modeloTipoFase.getModeloEjecucion().getId(),
        modeloTipoFase.getTipoFase().getId()).ifPresent(modeloTipoFaseExistente -> {
          Assert.isTrue(!modeloTipoFaseExistente.getActivo(),
              () -> ProblemMessage.builder()
                  .key(MSG_MODELO_EJECUCION_EXISTS)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
                  .build());
          modeloTipoFase.setId(modeloTipoFaseExistente.getId());
        });

    Assert.isTrue(modeloTipoFase.getConvocatoria() || modeloTipoFase.getSolicitud() || modeloTipoFase.getProyecto(),
        ApplicationContextSupport.getMessage(MSG_MODELO_TIPO_FASE_SELECCIONAR_CONVOCATORIA_SOLICITUD_PROYECTO));

    modeloTipoFase.setActivo(true);
    log.debug("create(ModeloTipoFase modeloTipoFase) - end");
    return modeloTipoFaseRepository.save(modeloTipoFase);
  }

  /**
   * Actualizar {@link ModeloTipoFase}.
   *
   * @param modeloTipoFaseActualizar la entidad {@link ModeloTipoFase} a
   *                                 actualizar.
   * @return la entidad {@link ModeloTipoFase} persistida.
   */
  @Override
  @Transactional
  public ModeloTipoFase update(ModeloTipoFase modeloTipoFaseActualizar) {
    log.debug("update(ModeloTipoFase modeloTipoFaseActualizar) - start");
    return modeloTipoFaseRepository.findById(modeloTipoFaseActualizar.getId()).map(modeloTipoFase -> {
      Assert.isTrue(modeloTipoFase.getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_FASE))
              .parameter(MSG_KEY_FIELD, modeloTipoFase.getTipoFase().getNombre())
              .build());

      Assert.isTrue(modeloTipoFase.getConvocatoria() || modeloTipoFase.getSolicitud() || modeloTipoFase.getProyecto(),
          ApplicationContextSupport.getMessage(MSG_MODELO_TIPO_FASE_SELECCIONAR_CONVOCATORIA_SOLICITUD_PROYECTO));

      modeloTipoFase.setConvocatoria(modeloTipoFaseActualizar.getConvocatoria());
      modeloTipoFase.setProyecto(modeloTipoFaseActualizar.getProyecto());
      modeloTipoFase.setSolicitud(modeloTipoFaseActualizar.getSolicitud());
      ModeloTipoFase returnValue = modeloTipoFaseRepository.save(modeloTipoFase);
      log.debug("update(ModeloTipoFase modeloTipoFaseActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloTipoFaseNotFoundException(modeloTipoFaseActualizar.getId()));

  }

  /**
   * Desactiva el {@link ModeloTipoFase} por id.
   *
   * @param id el id de la entidad {@link ModeloTipoFase}.
   * @return la entidad {@link ModeloTipoFase} persistida.
   */
  @Override
  @Transactional
  public ModeloTipoFase disable(Long id) throws ModeloTipoFaseNotFoundException {
    log.debug("disable(Long id) - start");
    AssertHelper.idNotNull(id, ModeloTipoFase.class);
    return modeloTipoFaseRepository.findById(id).map(modeloTipoFase -> {
      modeloTipoFase.setActivo(false);
      ModeloTipoFase returnValue = modeloTipoFaseRepository.save(modeloTipoFase);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloTipoFaseNotFoundException(id));

  }

  /**
   * Obtiene los {@link ModeloTipoFase} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  public Page<ModeloTipoFase> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion,  String query, Pageable pageable) - start");
    Specification<ModeloTipoFase> specs = ModeloTipoFaseSpecifications.activos()
        .and(ModeloTipoFaseSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoFase> returnValue = modeloTipoFaseRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion,  String query, Pageable pageable) - end");
    return returnValue;
  }

}
