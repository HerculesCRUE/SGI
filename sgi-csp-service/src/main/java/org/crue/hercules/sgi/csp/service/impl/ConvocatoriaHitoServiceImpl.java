package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaHitoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaHitoSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaHitoService;
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
 * Service Implementation para la gestión de {@link ConvocatoriaHito}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaHitoServiceImpl implements ConvocatoriaHitoService {

  private static final String TIPO_HITO_TEMPLATE = "TipoHito '";
  private final ConvocatoriaHitoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ModeloTipoHitoRepository modeloTipoHitoRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  public ConvocatoriaHitoServiceImpl(ConvocatoriaHitoRepository convocatoriaHitoRepository,
      ConvocatoriaRepository convocatoriaRepository, ModeloTipoHitoRepository modeloTipoHitoRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository) {
    this.repository = convocatoriaHitoRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.modeloTipoHitoRepository = modeloTipoHitoRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaHito}.
   * 
   * @param convocatoriaHito la entidad {@link ConvocatoriaHito} a guardar.
   * @return ConvocatoriaHito la entidad {@link ConvocatoriaHito} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaHito create(ConvocatoriaHito convocatoriaHito) {
    log.debug("create(ConvocatoriaHito convocatoriaHito) - start");

    Assert.isNull(convocatoriaHito.getId(),
        "ConvocatoriaHito id tiene que ser null para crear un nuevo ConvocatoriaHito");

    Assert.isTrue(convocatoriaHito.getConvocatoriaId() != null,
        "Id Convocatoria no puede ser null para crear ConvocatoriaHito");

    Assert.isTrue(convocatoriaHito.getTipoHito() != null && convocatoriaHito.getTipoHito().getId() != null,
        "Id Hito no puede ser null para crear ConvocatoriaHito");

    if (convocatoriaHito.getFecha().isBefore(Instant.now())) {
      convocatoriaHito.setGeneraAviso(false);
    }

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaHito.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaHito.getConvocatoriaId()));

    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
        && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

    // TipoHito
    Optional<ModeloTipoHito> modeloTipoHito = modeloTipoHitoRepository
        .findByModeloEjecucionIdAndTipoHitoId(modeloEjecucionId, convocatoriaHito.getTipoHito().getId());

    // Está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoHito.isPresent(),
        TIPO_HITO_TEMPLATE + convocatoriaHito.getTipoHito().getNombre() + "' no disponible para el ModeloEjecucion '"
            + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                : "Convocatoria sin modelo asignado")
            + "'");

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoHito.get().getActivo(), "ModeloTipoHito '" + modeloTipoHito.get().getTipoHito().getNombre()
        + "' no está activo para el ModeloEjecucion '" + modeloTipoHito.get().getModeloEjecucion().getNombre() + "'");

    // El TipoHito está activo
    Assert.isTrue(modeloTipoHito.get().getTipoHito().getActivo(),
        TIPO_HITO_TEMPLATE + modeloTipoHito.get().getTipoHito().getNombre() + "' no está activo");

    convocatoriaHito.setTipoHito(modeloTipoHito.get().getTipoHito());

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndFechaAndTipoHitoId(convocatoriaHito.getConvocatoriaId(),
            convocatoriaHito.getFecha(), convocatoriaHito.getTipoHito().getId()).isPresent(),
        "Ya existe un Hito con el mismo tipo en esa fecha");

    Assert.isTrue(convocatoriaHito.getTipoHito().getActivo(), "El TipoHito debe estar activo");

    ConvocatoriaHito returnValue = repository.save(convocatoriaHito);

    log.debug("create(ConvocatoriaHito convocatoriaHito) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaHito}.
   * 
   * @param convocatoriaHitoActualizar la entidad {@link ConvocatoriaHito} a
   *                                   guardar.
   * @return ConvocatoriaHito la entidad {@link ConvocatoriaHito} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaHito update(ConvocatoriaHito convocatoriaHitoActualizar) {
    log.debug("update(ConvocatoriaHito convocatoriaHitoActualizar) - start");

    Assert.notNull(convocatoriaHitoActualizar.getId(),
        "ConvocatoriaHito id no puede ser null para actualizar un ConvocatoriaHito");

    Assert.isTrue(convocatoriaHitoActualizar.getConvocatoriaId() != null,
        "Id Convocatoria no puede ser null para actualizar ConvocatoriaHito");

    Assert.isTrue(
        convocatoriaHitoActualizar.getTipoHito() != null && convocatoriaHitoActualizar.getTipoHito().getId() != null,
        "Id Hito no puede ser null para actualizar ConvocatoriaHito");

    return repository.findById(convocatoriaHitoActualizar.getId()).map(convocatoriaHito -> {

      // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
      Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaHito.getConvocatoriaId())
          .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaHito.getConvocatoriaId()));
      Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
          && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

      // TipoHito
      Optional<ModeloTipoHito> modeloTipoHito = modeloTipoHitoRepository
          .findByModeloEjecucionIdAndTipoHitoId(modeloEjecucionId, convocatoriaHitoActualizar.getTipoHito().getId());

      // Está asignado al ModeloEjecucion
      Assert.isTrue(modeloTipoHito.isPresent(),
          TIPO_HITO_TEMPLATE + convocatoriaHitoActualizar.getTipoHito().getNombre()
              + "' no disponible para el ModeloEjecucion '"
              + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                  : "Convocatoria sin modelo asignado")
              + "'");

      // La asignación al ModeloEjecucion está activa
      Assert.isTrue(
          modeloTipoHito.get().getTipoHito().getId() == convocatoriaHito.getTipoHito().getId()
              || modeloTipoHito.get().getActivo(),
          "ModeloTipoHito '" + modeloTipoHito.get().getTipoHito().getNombre()
              + "' no está activo para el ModeloEjecucion '" + modeloTipoHito.get().getModeloEjecucion().getNombre()
              + "'");

      // El TipoHito está activo
      Assert.isTrue(
          modeloTipoHito.get().getTipoHito().getId() == convocatoriaHito.getTipoHito().getId()
              || modeloTipoHito.get().getTipoHito().getActivo(),
          TIPO_HITO_TEMPLATE + modeloTipoHito.get().getTipoHito().getNombre() + "' no está activo");

      convocatoriaHitoActualizar.setTipoHito(modeloTipoHito.get().getTipoHito());

      if (convocatoriaHitoActualizar.getFecha().isBefore(Instant.now())) {
        convocatoriaHitoActualizar.setGeneraAviso(false);
      }
      repository
          .findByConvocatoriaIdAndFechaAndTipoHitoId(convocatoriaHitoActualizar.getConvocatoriaId(),
              convocatoriaHitoActualizar.getFecha(), convocatoriaHitoActualizar.getTipoHito().getId())
          .ifPresent(convocatoriaHitoExistente -> Assert.isTrue(
              convocatoriaHitoActualizar.getId() == convocatoriaHitoExistente.getId(),
              "Ya existe un Hito con el mismo tipo en esa fecha"));

      convocatoriaHito.setFecha(convocatoriaHitoActualizar.getFecha());
      convocatoriaHito.setComentario(convocatoriaHitoActualizar.getComentario());
      convocatoriaHito.setTipoHito(convocatoriaHitoActualizar.getTipoHito());
      convocatoriaHito.setGeneraAviso(convocatoriaHitoActualizar.getGeneraAviso());

      ConvocatoriaHito returnValue = repository.save(convocatoriaHito);
      log.debug("update(ConvocatoriaHito convocatoriaHitoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaHitoNotFoundException(convocatoriaHitoActualizar.getId()));

  }

  /**
   * Elimina la {@link ConvocatoriaHito}.
   *
   * @param id Id del {@link ConvocatoriaHito}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ConvocatoriaHito id no puede ser null para eliminar un ConvocatoriaHito");
    if (!repository.existsById(id)) {
      throw new ConvocatoriaHitoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene {@link ConvocatoriaHito} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaHito}.
   * @return la entidad {@link ConvocatoriaHito}.
   */
  @Override
  public ConvocatoriaHito findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaHito returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaHitoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene las {@link ConvocatoriaHito} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaHito} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaHito> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
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

    Specification<ConvocatoriaHito> specs = ConvocatoriaHitoSpecifications.byConvocatoriaId(convocatoriaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaHito> returnValue = repository.findAll(specs, pageable);
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
