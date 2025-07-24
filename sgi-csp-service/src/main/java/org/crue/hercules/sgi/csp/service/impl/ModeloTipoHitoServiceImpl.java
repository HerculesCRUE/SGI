package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoHitoSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoHitoService;
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
 * Service Implementation para gestion {@link ModeloTipoHito}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoHitoServiceImpl implements ModeloTipoHitoService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_MODEL_TIPO_HITO = "org.crue.hercules.sgi.csp.model.TipoHito.message";
  private static final String MSG_MODEL_MODELO_TIPO_HITO = "org.crue.hercules.sgi.csp.model.ModeloTipoHito.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_EXISTS = "org.springframework.util.Assert.entity.modeloEjecucion.exists.message";
  private static final String MSG_MODELO_TIPO_HITO_ASSIGN = "modeloTipoHito.assign";

  private final ModeloTipoHitoRepository modeloTipoHitoRepository;
  private final ModeloEjecucionRepository modeloEjecucionRepository;
  private final TipoHitoRepository tipoHitoRepository;

  public ModeloTipoHitoServiceImpl(ModeloTipoHitoRepository modeloTipoHitoRepository,
      ModeloEjecucionRepository modeloEjecucionRepository, TipoHitoRepository tipoHitoRepository) {
    this.modeloTipoHitoRepository = modeloTipoHitoRepository;
    this.modeloEjecucionRepository = modeloEjecucionRepository;
    this.tipoHitoRepository = tipoHitoRepository;
  }

  /**
   * Guarda la entidad {@link ModeloTipoHito}.
   * 
   * @param modeloTipoHito la entidad {@link ModeloTipoHito} a guardar.
   * @return ModeloTipoHito la entidad {@link ModeloTipoHito} persistida.
   */
  @Override
  @Transactional
  public ModeloTipoHito create(ModeloTipoHito modeloTipoHito) {
    log.debug("create(ModeloTipoHito modeloTipoHito) - start");

    // Id vacío al crear
    AssertHelper.idIsNull(modeloTipoHito.getId(), ModeloTipoHito.class);

    // ModeloEjecucion existe
    AssertHelper.idNotNull(modeloTipoHito.getModeloEjecucion().getId(), ModeloEjecucion.class);
    modeloTipoHito.setModeloEjecucion(modeloEjecucionRepository.findById(modeloTipoHito.getModeloEjecucion().getId())
        .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloTipoHito.getModeloEjecucion().getId())));

    // TipoHito existe y activo
    AssertHelper.idNotNull(modeloTipoHito.getTipoHito().getId(), TipoHito.class);
    modeloTipoHito.setTipoHito(tipoHitoRepository.findById(modeloTipoHito.getTipoHito().getId())
        .orElseThrow(() -> new TipoHitoNotFoundException(modeloTipoHito.getTipoHito().getId())));
    Assert.isTrue(modeloTipoHito.getTipoHito().getActivo(),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_INACTIVO)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
            .parameter(MSG_KEY_FIELD, modeloTipoHito.getTipoHito().getNombre())
            .build());

    // Comprobar si ya existe una relación activa entre Modelo y Tipo
    modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(modeloTipoHito.getModeloEjecucion().getId(),
        modeloTipoHito.getTipoHito().getId()).ifPresent(
            modeloTipoHitoExistente -> {

              // Si ya está activa no se podrá insertar TipoHito
              Assert.isTrue(!modeloTipoHitoExistente.getActivo(),
                  () -> ProblemMessage.builder()
                      .key(MSG_MODELO_EJECUCION_EXISTS)
                      .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
                      .build());

              // Si está desactivado se activará la relación existente
              modeloTipoHito.setId(modeloTipoHitoExistente.getId());

            });

    // Simpre Activo en la creación
    modeloTipoHito.setActivo(Boolean.TRUE);

    // Al menos un tipo seleccionado
    Assert.isTrue((modeloTipoHito.getSolicitud() || modeloTipoHito.getProyecto() || modeloTipoHito.getConvocatoria()),
        ApplicationContextSupport.getMessage(MSG_MODELO_TIPO_HITO_ASSIGN));

    ModeloTipoHito returnValue = modeloTipoHitoRepository.save(modeloTipoHito);
    log.debug("create(ModeloTipoHito modeloTipoHito) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ModeloTipoHito}.
   *
   * @param modeloTipoHito la entidad {@link ModeloTipoHito} a actualizar.
   * @return la entidad {@link ModeloTipoHito} persistida.
   */
  @Override
  @Transactional
  public ModeloTipoHito update(ModeloTipoHito modeloTipoHito) {
    log.debug("update(ModeloTipoHito modeloTipoHito) - start");

    // Id no vacío
    AssertHelper.idNotNull(modeloTipoHito.getId(), ModeloTipoHito.class);

    return modeloTipoHitoRepository.findById(modeloTipoHito.getId()).map(modeloTipoHitoExistente -> {

      Assert.isTrue(modeloTipoHitoExistente.getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_HITO))
              .parameter(MSG_KEY_FIELD, modeloTipoHitoExistente.getTipoHito().getNombre())
              .build());

      Assert.isTrue((modeloTipoHito.getSolicitud() || modeloTipoHito.getProyecto() || modeloTipoHito.getConvocatoria()),
          ApplicationContextSupport.getMessage(MSG_MODELO_TIPO_HITO_ASSIGN));

      modeloTipoHitoExistente.setSolicitud(modeloTipoHito.getSolicitud());
      modeloTipoHitoExistente.setConvocatoria(modeloTipoHito.getConvocatoria());
      modeloTipoHitoExistente.setProyecto(modeloTipoHito.getProyecto());

      ModeloTipoHito returnValue = modeloTipoHitoRepository.save(modeloTipoHitoExistente);
      log.debug("update(ModeloTipoHito modeloTipoHito) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloTipoHitoNotFoundException(modeloTipoHito.getId()));

  }

  /**
   * Desactiva el {@link ModeloTipoHito}.
   *
   * @param id Id del {@link ModeloTipoHito}.
   * @return la entidad {@link ModeloTipoHito} persistida.
   */
  @Override
  @Transactional
  public ModeloTipoHito disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, ModeloTipoHito.class);

    return modeloTipoHitoRepository.findById(id).map(modeloTipoHito -> {
      modeloTipoHito.setActivo(false);

      ModeloTipoHito returnValue = modeloTipoHitoRepository.save(modeloTipoHito);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloTipoHitoNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link ModeloTipoHito} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoHito}.
   * @return ModeloTipoHito la entidad {@link ModeloTipoHito}.
   */
  @Override
  public ModeloTipoHito findById(Long id) {
    log.debug("findById(Long id) - start");
    final ModeloTipoHito returnValue = modeloTipoHitoRepository.findById(id)
        .orElseThrow(() -> new ModeloTipoHitoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ModeloTipoHito} para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link TipoHito} del {@link ModeloEjecucion}
   *         paginadas.
   */
  public Page<ModeloTipoHito> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloTipoHito> specs = ModeloTipoHitoSpecifications.activos()
        .and(ModeloTipoHitoSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoHito> returnValue = modeloTipoHitoRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

}
