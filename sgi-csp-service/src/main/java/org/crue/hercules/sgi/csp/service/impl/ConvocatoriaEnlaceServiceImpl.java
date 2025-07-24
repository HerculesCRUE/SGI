package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEnlaceSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
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
 * Service Implementation para la gestión de {@link ConvocatoriaEnlace}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)

public class ConvocatoriaEnlaceServiceImpl implements ConvocatoriaEnlaceService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_KEY_URL = "convocatoriaEnlace.url";
  private static final String MSG_MODEL_MODELO_TIPO_ENLACE = "org.crue.hercules.sgi.csp.model.ModeloTipoEnlace.message";
  private static final String MSG_MODEL_TIPO_ENLACE = "org.crue.hercules.sgi.csp.model.TipoEnlace.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_INACTIVO = "org.springframework.util.Assert.entity.modeloEjecucion.inactivo.message";
  private static final String MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.exceptions.AssignModeloEjecucion.message";
  private static final String MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO = "org.crue.hercules.sgi.csp.model.Convocatoria.sinModeloEjecucion.message";
  private static final String MSG_NO_DISPONIBLE_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.noDisponible.message";

  private final ConvocatoriaEnlaceRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaEnlaceServiceImpl(ConvocatoriaEnlaceRepository convocatoriaEnlaceRepository,
      ConvocatoriaRepository convocatoriaRepository, ModeloTipoEnlaceRepository modeloTipoEnlaceRepository,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = convocatoriaEnlaceRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.modeloTipoEnlaceRepository = modeloTipoEnlaceRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guardar un nuevo {@link ConvocatoriaEnlace}.
   *
   * @param convocatoriaEnlace la entidad {@link ConvocatoriaEnlace} a guardar.
   * @return la entidad {@link ConvocatoriaEnlace} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEnlace create(ConvocatoriaEnlace convocatoriaEnlace) {
    log.debug("create(ConvocatoriaEnlace convocatoriaEnlace) - start");

    AssertHelper.idIsNull(convocatoriaEnlace.getId(), ConvocatoriaEnlace.class);
    AssertHelper.idNotNull(convocatoriaEnlace.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.fieldNotNull(convocatoriaEnlace.getUrl(), ConvocatoriaEnlace.class, MSG_KEY_URL);

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaEnlace.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaEnlace.getConvocatoriaId()));

    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
        && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

    if (convocatoriaEnlace.getTipoEnlace() != null) {
      if (convocatoriaEnlace.getTipoEnlace().getId() != null) {

        // TipoEnlace
        Optional<ModeloTipoEnlace> modeloTipoEnlace = modeloTipoEnlaceRepository
            .findByModeloEjecucionIdAndTipoEnlaceId(modeloEjecucionId, convocatoriaEnlace.getTipoEnlace().getId());

        // Está asignado al ModeloEjecucion
        Assert.isTrue(modeloTipoEnlace.isPresent(),
            () -> ProblemMessage.builder()
                .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ENLACE))
                .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
                .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                    : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
                .build());

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoEnlace.get().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_ENLACE))
                .parameter(MSG_KEY_FIELD, modeloTipoEnlace.get().getTipoEnlace().getNombre())
                .build());

        // El TipoEnlace está activo
        Assert.isTrue(modeloTipoEnlace.get().getTipoEnlace().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ENLACE))
                .parameter(MSG_KEY_FIELD, modeloTipoEnlace.get().getTipoEnlace().getNombre())
                .build());

        convocatoriaEnlace.setTipoEnlace(modeloTipoEnlace.get().getTipoEnlace());

      } else {
        convocatoriaEnlace.setTipoEnlace(null);
      }
    }

    ConvocatoriaEnlace returnValue = repository.save(convocatoriaEnlace);

    log.debug("create(ConvocatoriaEnlace convocatoriaEnlace) - end");
    return returnValue;

  }

  /**
   * Actualizar {@link ConvocatoriaEnlace}.
   *
   * @param convocatoriaEnlaceActualizar la entidad {@link ConvocatoriaEnlace} a
   *                                     actualizar.
   * @return la entidad {@link ConvocatoriaEnlace} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEnlace update(ConvocatoriaEnlace convocatoriaEnlaceActualizar) {
    log.debug("update(ConvocatoriaEnlace convocatoriaEnlaceActualizar) - start");

    AssertHelper.idNotNull(convocatoriaEnlaceActualizar.getId(), ConvocatoriaEnlace.class);
    AssertHelper.idNotNull(convocatoriaEnlaceActualizar.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.fieldNotNull(convocatoriaEnlaceActualizar.getUrl(), ConvocatoriaEnlace.class, MSG_KEY_URL);

    return repository.findById(convocatoriaEnlaceActualizar.getId()).map(convocatoriaEnlace -> {
      Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaEnlace.getConvocatoriaId())
          .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaEnlace.getConvocatoriaId()));
      // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
      Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
          && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

      if (convocatoriaEnlaceActualizar.getTipoEnlace() != null) {
        if (convocatoriaEnlaceActualizar.getTipoEnlace().getId() != null) {

          // TipoEnlace
          Optional<ModeloTipoEnlace> modeloTipoEnlace = modeloTipoEnlaceRepository
              .findByModeloEjecucionIdAndTipoEnlaceId(modeloEjecucionId,
                  convocatoriaEnlaceActualizar.getTipoEnlace().getId());

          // Está asignado al ModeloEjecucion
          Assert.isTrue(modeloTipoEnlace.isPresent(),
              () -> ProblemMessage.builder()
                  .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ENLACE))
                  .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
                  .parameter(MSG_KEY_MODELO,
                      ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                          : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
                  .build());

          // La asignación al ModeloEjecucion está activa
          Assert.isTrue(
              Objects.equals(modeloTipoEnlace.get().getTipoEnlace().getId(),
                  convocatoriaEnlaceActualizar.getTipoEnlace().getId())
                  && modeloTipoEnlace.get().getActivo(),
              () -> ProblemMessage.builder()
                  .key(MSG_MODELO_EJECUCION_INACTIVO)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_ENLACE))
                  .parameter(MSG_KEY_FIELD, modeloTipoEnlace.get().getTipoEnlace().getNombre())
                  .parameter(MSG_KEY_MODELO, modeloTipoEnlace.get().getModeloEjecucion().getNombre())
                  .build());

          // El TipoEnlace está activo
          Assert.isTrue(
              Objects.equals(modeloTipoEnlace.get().getTipoEnlace().getId(),
                  convocatoriaEnlaceActualizar.getTipoEnlace().getId())
                  && modeloTipoEnlace.get().getTipoEnlace().getActivo(),
              () -> ProblemMessage.builder()
                  .key(MSG_ENTITY_INACTIVO)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ENLACE))
                  .parameter(MSG_KEY_FIELD, modeloTipoEnlace.get().getTipoEnlace().getNombre())
                  .build());
        } else {
          convocatoriaEnlaceActualizar.setTipoEnlace(null);
        }
      }

      convocatoriaEnlace.setUrl(convocatoriaEnlaceActualizar.getUrl());
      convocatoriaEnlace.setDescripcion(convocatoriaEnlaceActualizar.getDescripcion());
      convocatoriaEnlace.setTipoEnlace(convocatoriaEnlaceActualizar.getTipoEnlace());

      ConvocatoriaEnlace returnValue = repository.save(convocatoriaEnlace);
      log.debug("update(ConvocatoriaEnlace convocatoriaEnlaceActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaEnlaceNotFoundException(convocatoriaEnlaceActualizar.getId()));
  }

  /**
   * Elimina el {@link ConvocatoriaEnlace}.
   *
   * @param id Id del {@link ConvocatoriaEnlace}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ConvocatoriaEnlace.class);
    if (!repository.existsById(id)) {
      throw new ConvocatoriaEnlaceNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaEnlace} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEnlace}.
   * @return la entidad {@link ConvocatoriaEnlace}.
   */
  @Override
  public ConvocatoriaEnlace findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaEnlace returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaEnlaceNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ConvocatoriaEnlace} para una
   * {@link Convocatoria} paginadas y/o filtradas.
   * 
   * @param convocatoriaId id de {@link Convocatoria}
   * @param query          la información del filtro.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEnlace} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<ConvocatoriaEnlace> findAllByConvocatoria(Long convocatoriaId, String query, Pageable paging) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaEnlace> specs = ConvocatoriaEnlaceSpecifications.byConvocatoriaId(
        convocatoriaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEnlace> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;

  }

  @Override
  public boolean existsByConvocatoriaId(Long convocatoriaId) {
    return repository.existsByConvocatoriaId(convocatoriaId);
  }

  @Override
  @Transactional
  public List<ConvocatoriaEnlace> update(Long convocatoriaId, List<ConvocatoriaEnlace> convocatoriaEnlaces) {
    log.debug("update(Long convocatoriaId, List<ConvocatoriaEnlace> convocatoriaEnlaces) - start");

    Set<String> nonRepeatedUrls = new HashSet<>();
    convocatoriaEnlaces.stream()
        .forEach(convocatoriaEnlace -> nonRepeatedUrls.add(convocatoriaEnlace.getUrl()));

    AssertHelper.fieldExists(
        convocatoriaEnlaces.size() == nonRepeatedUrls.size(), ConvocatoriaEnlace.class, MSG_KEY_URL);

    Specification<ConvocatoriaEnlace> specs = ConvocatoriaEnlaceSpecifications.byConvocatoriaId(
        convocatoriaId);
    List<ConvocatoriaEnlace> convocatoriaEnlacesBD = repository.findAll(specs);

    // Enlaces eliminados
    List<ConvocatoriaEnlace> convocatoriaEnlacesEliminar = convocatoriaEnlacesBD.stream()
        .filter(convocatoriaEnlace -> convocatoriaEnlaces.stream().map(ConvocatoriaEnlace::getId)
            .noneMatch(id -> Objects.equals(id, convocatoriaEnlace.getId())))
        .collect(Collectors.toList());

    if (!convocatoriaEnlacesEliminar.isEmpty()) {
      repository.deleteAll(convocatoriaEnlacesEliminar);
    }

    if (convocatoriaEnlaces.isEmpty()) {
      return new ArrayList<>();
    }

    List<ConvocatoriaEnlace> convocatoriaEnlacesUpdated = convocatoriaEnlaces.stream().map(convocatoriaEnlace -> {
      convocatoriaEnlace.setConvocatoriaId(convocatoriaId);
      if (convocatoriaEnlace.getId() == null) {
        return create(convocatoriaEnlace);
      } else {
        return update(convocatoriaEnlace);
      }
    }).collect(Collectors.toList());

    log.debug("update(Long convocatoriaId, List<ConvocatoriaEnlace> convocatoriaEnlaces) - end");
    return convocatoriaEnlacesUpdated;
  }

}
