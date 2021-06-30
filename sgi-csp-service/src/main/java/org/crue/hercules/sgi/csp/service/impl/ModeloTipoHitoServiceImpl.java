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
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
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
    Assert.isNull(modeloTipoHito.getId(), "Id tiene que ser null para crear ModeloTipoHito");

    // ModeloEjecucion existe
    Assert.notNull(modeloTipoHito.getModeloEjecucion().getId(),
        "Id ModeloEjecucion no puede ser null para actualizar ModeloTipoHito");
    modeloTipoHito.setModeloEjecucion(modeloEjecucionRepository.findById(modeloTipoHito.getModeloEjecucion().getId())
        .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloTipoHito.getModeloEjecucion().getId())));

    // TipoHito existe y activo
    Assert.notNull(modeloTipoHito.getTipoHito().getId(),
        "Id TipoHito no puede ser null para actualizar ModeloTipoHito");
    modeloTipoHito.setTipoHito(tipoHitoRepository.findById(modeloTipoHito.getTipoHito().getId())
        .orElseThrow(() -> new TipoHitoNotFoundException(modeloTipoHito.getTipoHito().getId())));
    Assert.isTrue(modeloTipoHito.getTipoHito().getActivo(), "El TipoHito debe estar Activo");

    // Comprobar si ya existe una relación activa entre Modelo y Tipo
    modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(modeloTipoHito.getModeloEjecucion().getId(),
        modeloTipoHito.getTipoHito().getId()).ifPresent(
            modeloTipoHitoExistente -> {

              // Si ya está activa no se podrá insertar TipoHito
              Assert.isTrue(!modeloTipoHitoExistente.getActivo(),
                  "El TipoHito ya se encuentra asociado al  ModeloEjecucion");

              // Si está desactivado se activará la relación existente
              modeloTipoHito.setId(modeloTipoHitoExistente.getId());

            });

    // Simpre Activo en la creación
    modeloTipoHito.setActivo(Boolean.TRUE);

    // Al menos un tipo seleccionado
    Assert.isTrue((modeloTipoHito.getSolicitud() || modeloTipoHito.getProyecto() || modeloTipoHito.getConvocatoria()),
        "ModeloTipoHito debe estar asignado al menos a uno de los tipos Solicitud, Convocatoria o Proyecto");

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
    Assert.notNull(modeloTipoHito.getId(), "Id no puede que ser null para actualizar ModeloTipoHito");

    return modeloTipoHitoRepository.findById(modeloTipoHito.getId()).map(modeloTipoHitoExistente -> {

      Assert.isTrue(modeloTipoHitoExistente.getActivo(), "El ModeloTipoHito debe estar activo");

      Assert.isTrue((modeloTipoHito.getSolicitud() || modeloTipoHito.getProyecto() || modeloTipoHito.getConvocatoria()),
          "ModeloTipoHito debe estar asignado al menos a uno de los tipos Solicitud, Convocatoria o Proyecto");

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

    Assert.notNull(id, "ModeloTipoHito id no puede ser null para desactivar un ModeloTipoHito");

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

  /**
   * Obtiene los {@link ModeloTipoHito} activos para convocatorias para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoHito} del
   *         {@link ModeloEjecucion} paginadas.
   */
  @Override
  public Page<ModeloTipoHito> findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, String query,
      Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloTipoHito> specs = ModeloTipoHitoSpecifications.activos()
        .and(ModeloTipoHitoSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(ModeloTipoHitoSpecifications.activosConvocatoria()).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoHito> returnValue = modeloTipoHitoRepository.findAll(specs, pageable);
    log.debug(
        "findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ModeloTipoHito} activos para proyectos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoHito} del
   *         {@link ModeloEjecucion} paginadas.
   */
  @Override
  public Page<ModeloTipoHito> findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query,
      Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloTipoHito> specs = ModeloTipoHitoSpecifications.activos()
        .and(ModeloTipoHitoSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(ModeloTipoHitoSpecifications.activosProyecto()).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoHito> returnValue = modeloTipoHitoRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ModeloTipoHito} activos para solicitudes para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoHito} del
   *         {@link ModeloEjecucion} paginadas.
   */
  @Override
  public Page<ModeloTipoHito> findAllByModeloEjecucionActivosSolicitud(Long idModeloEjecucion, String query,
      Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloTipoHito> specs = ModeloTipoHitoSpecifications.activos()
        .and(ModeloTipoHitoSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(ModeloTipoHitoSpecifications.activosSolcitud()).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoHito> returnValue = modeloTipoHitoRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

}
