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
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
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

    Assert.isNull(modeloTipoFase.getId(), "ModeloTipoFase id no puede ser null para crear un nuevo modeloTipoFase");
    Assert.notNull(modeloTipoFase.getModeloEjecucion().getId(), "Id ModeloEjecución no puede ser null");
    Assert.notNull(modeloTipoFase.getTipoFase().getId(), "Id TipoFase no puede ser null");
    modeloTipoFase.setTipoFase(tipoFaseRepository.findById(modeloTipoFase.getTipoFase().getId())
        .orElseThrow(() -> new TipoFaseNotFoundException(modeloTipoFase.getTipoFase().getId())));
    modeloTipoFase.setModeloEjecucion(modeloEjecucionRepository.findById(modeloTipoFase.getModeloEjecucion().getId())
        .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloTipoFase.getModeloEjecucion().getId())));
    Assert.isTrue(modeloTipoFase.getTipoFase().getActivo(), "El tipo Fase debe estar activo");
    modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(modeloTipoFase.getModeloEjecucion().getId(),
        modeloTipoFase.getTipoFase().getId()).ifPresent(modeloTipoFaseExistente -> {
          Assert.isTrue(!modeloTipoFaseExistente.getActivo(),
              "Ya existe una asociación activa para ese ModeloEjecucion y ese TipoFase");
          modeloTipoFase.setId(modeloTipoFaseExistente.getId());
        });
    Assert.isTrue(modeloTipoFase.getConvocatoria() || modeloTipoFase.getProyecto(),
        "Debe seleccionarse si la fase está disponible para proyectos o convocatorias");

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
      Assert.isTrue(modeloTipoFase.getActivo(), "El ModeloTipoFase tiene que estar activo");
      Assert.isTrue(modeloTipoFase.getConvocatoria() || modeloTipoFase.getProyecto(),
          "Debe seleccionarse si la fase está disponible para proyectos o convocatorias");
      modeloTipoFase.setConvocatoria(modeloTipoFaseActualizar.getConvocatoria());
      modeloTipoFase.setProyecto(modeloTipoFaseActualizar.getProyecto());
      ModeloTipoFase returnValue = modeloTipoFaseRepository.save(modeloTipoFase);
      log.debug("update(ModeloTipoFase modeloTipoFaseActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloTipoFaseNotFoundException(modeloTipoFaseActualizar.getId()));

  }

  /**
   * Obtiene {@link ModeloTipoFase} por id.
   *
   * @param id el id de la entidad {@link ModeloTipoFase}.
   * @return la entidad {@link ModeloTipoFase}.
   */
  @Override
  public ModeloTipoFase findById(Long id) {
    log.debug("findById(Long id) - start");
    ModeloTipoFase modeloTipoFase = modeloTipoFaseRepository.findById(id)
        .orElseThrow(() -> new ModeloTipoFaseNotFoundException(id));
    log.debug("findById(Long id) - end");
    return modeloTipoFase;

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
    Assert.notNull(id, "El id no puede ser nulo");
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

  /**
   * Obtiene los {@link ModeloTipoFase} activos para convocatorias para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  @Override
  public Page<ModeloTipoFase> findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, String query,
      Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloTipoFase> specs = ModeloTipoFaseSpecifications.activos()
        .and(ModeloTipoFaseSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(ModeloTipoFaseSpecifications.activosConvocatoria()).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoFase> returnValue = modeloTipoFaseRepository.findAll(specs, pageable);
    log.debug(
        "findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link TipoFase} activos para proyectos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link TipoFase} del {@link ModeloEjecucion}
   *         paginadas.
   */
  @Override
  public Page<ModeloTipoFase> findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query,
      Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query, Pageable pageable) - start");
    Specification<ModeloTipoFase> specs = ModeloTipoFaseSpecifications.activos()
        .and(ModeloTipoFaseSpecifications.byModeloEjecucionId(idModeloEjecucion))
        .and(ModeloTipoFaseSpecifications.activosProyecto()).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ModeloTipoFase> returnValue = modeloTipoFaseRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query, Pageable pageable) - end");
    return returnValue;
  }

}
