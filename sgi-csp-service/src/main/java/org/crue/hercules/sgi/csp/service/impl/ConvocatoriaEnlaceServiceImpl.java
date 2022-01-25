package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEnlaceSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
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

  private static final String TIPO_ENLACE_TEMPLATE = "TipoEnlace '";
  private final ConvocatoriaEnlaceRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  public ConvocatoriaEnlaceServiceImpl(ConvocatoriaEnlaceRepository convocatoriaEnlaceRepository,
      ConvocatoriaRepository convocatoriaRepository, ModeloTipoEnlaceRepository modeloTipoEnlaceRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository) {
    this.repository = convocatoriaEnlaceRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.modeloTipoEnlaceRepository = modeloTipoEnlaceRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
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

    Assert.isNull(convocatoriaEnlace.getId(),
        "ConvocatoriaEnlace id tiene que ser null para crear un nuevo ConvocatoriaEnlace");

    Assert.isTrue(convocatoriaEnlace.getConvocatoriaId() != null,
        "Id Convocatoria no puede ser null para crear ConvocatoriaEnlace");

    Assert.notNull(convocatoriaEnlace.getUrl(),
        "ConvocatoriaEnlace url no puede ser null para crear una nueva ConvocatoriaEnlace");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaEnlace.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaEnlace.getConvocatoriaId()));

    Assert.isTrue(!repository
        .findByConvocatoriaIdAndUrl(convocatoriaEnlace.getConvocatoriaId(), convocatoriaEnlace.getUrl()).isPresent(),
        "Ya existe esa url para esta Convocatoria");

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
            TIPO_ENLACE_TEMPLATE + convocatoriaEnlace.getTipoEnlace().getNombre()
                + "' no disponible para el ModeloEjecucion '"
                + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                    : "Convocatoria sin modelo asignado")
                + "'");

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoEnlace.get().getActivo(),
            "ModeloTipoEnlace '" + modeloTipoEnlace.get().getTipoEnlace().getNombre()
                + "' no está activo para el ModeloEjecucion '" + modeloTipoEnlace.get().getModeloEjecucion().getNombre()
                + "'");

        // El TipoEnlace está activo
        Assert.isTrue(modeloTipoEnlace.get().getTipoEnlace().getActivo(),
            TIPO_ENLACE_TEMPLATE + modeloTipoEnlace.get().getTipoEnlace().getNombre() + "' no está activo");

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

    Assert.notNull(convocatoriaEnlaceActualizar.getId(),
        "ConvocatoriaEnlace id no puede ser null para actualizar un ConvocatoriaEnlace");

    Assert.isTrue(convocatoriaEnlaceActualizar.getConvocatoriaId() != null,
        "Id Convocatoria no puede ser null para actualizar ConvocatoriaEnlace");

    Assert.notNull(convocatoriaEnlaceActualizar.getUrl(),
        "ConvocatoriaEnlace url no puede ser null para actualizar un nuevo ConvocatoriaEnlace");

    return repository.findById(convocatoriaEnlaceActualizar.getId()).map(convocatoriaEnlace -> {
      Long convocatoriaId = convocatoriaEnlace.getConvocatoriaId();
      String url = convocatoriaEnlaceActualizar.getUrl();
      Optional<ConvocatoriaEnlace> optional = repository.findByConvocatoriaIdAndUrl(convocatoriaId, url);
      if (optional.isPresent()) {
        Assert.isTrue(optional.get().getId() == convocatoriaEnlaceActualizar.getId(),
            "Ya existe esa url para esta Convocatoria");
      }

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
              TIPO_ENLACE_TEMPLATE + convocatoriaEnlaceActualizar.getTipoEnlace().getNombre()
                  + "' no disponible para el ModeloEjecucion '"
                  + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                      : "Convocatoria sin modelo asignado")
                  + "'");

          // La asignación al ModeloEjecucion está activa
          Assert.isTrue(
              modeloTipoEnlace.get().getTipoEnlace().getId() == convocatoriaEnlaceActualizar.getTipoEnlace().getId()
                  && modeloTipoEnlace.get().getActivo(),
              "ModeloTipoEnlace '" + modeloTipoEnlace.get().getTipoEnlace().getNombre()
                  + "' no está activo para el ModeloEjecucion '"
                  + modeloTipoEnlace.get().getModeloEjecucion().getNombre() + "'");

          // El TipoEnlace está activo
          Assert.isTrue(
              modeloTipoEnlace.get().getTipoEnlace().getId() == convocatoriaEnlaceActualizar.getTipoEnlace().getId()
                  && modeloTipoEnlace.get().getTipoEnlace().getActivo(),
              TIPO_ENLACE_TEMPLATE + modeloTipoEnlace.get().getTipoEnlace().getNombre() + "' no está activo");
          convocatoriaEnlaceActualizar.setTipoEnlace(modeloTipoEnlace.get().getTipoEnlace());
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

    Assert.notNull(id, "ConvocatoriaEnlace id no puede ser null para eliminar un ConvocatoriaEnlace");
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
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(
        convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));
    if (hasAuthorityViewInvestigador()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(convocatoriaId)
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoriaId));
      if (!convocatoria.getEstado().equals(Estado.REGISTRADA)
          || Boolean.FALSE.equals(configuracionSolicitud.getTramitacionSGI())) {
        throw new UserNotAuthorizedToAccessConvocatoriaException();
      }
    }

    Specification<ConvocatoriaEnlace> specs = ConvocatoriaEnlaceSpecifications.byConvocatoriaId(
        convocatoriaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEnlace> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - end");
    return returnValue;

  }

  @Override
  public boolean existsByConvocatoriaId(Long convocatoriaId) {
    return repository.existsByConvocatoriaId(convocatoriaId);
  }

  private boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V");
  }
}
