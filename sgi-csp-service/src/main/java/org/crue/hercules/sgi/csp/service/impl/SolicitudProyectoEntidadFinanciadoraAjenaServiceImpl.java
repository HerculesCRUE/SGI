package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;

import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadFinanciadoraAjenaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoEntidadFinanciadoraAjenaSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoEntidadSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
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
 * Service Implementation para la gestión de
 * {@link SolicitudProyectoEntidadFinanciadoraAjena}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoEntidadFinanciadoraAjenaServiceImpl
    implements SolicitudProyectoEntidadFinanciadoraAjenaService {

  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_MODEL_SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA = "org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena.message";
  private static final String MSG_ENTITY_MODIFICABLE = "org.springframework.util.Assert.entity.modificable.message";
  private static final String MSG_MODEL_FUENTE_FINANCIACION = "org.crue.hercules.sgi.csp.model.FuenteFinanciacion.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";

  private final SolicitudProyectoEntidadFinanciadoraAjenaRepository repository;
  private final FuenteFinanciacionRepository fuenteFinanciacionRepository;
  private final TipoFinanciacionRepository tipoFinanciacionRepository;
  private final SolicitudService solicitudService;
  private final SolicitudProyectoRepository solicitudProyectoRepository;
  private final SolicitudProyectoEntidadRepository solicitudProyectoEntidadRepository;
  private final SolicitudAuthorityHelper solicitudAuthorityHelper;

  public SolicitudProyectoEntidadFinanciadoraAjenaServiceImpl(
      SolicitudProyectoEntidadFinanciadoraAjenaRepository solicitudProyectoEntidadFinanciadoraAjenaRepository,
      FuenteFinanciacionRepository fuenteFinanciacionRepository, TipoFinanciacionRepository tipoFinanciacionRepository,
      SolicitudService solicitudService, SolicitudProyectoRepository solicitudProyectoRepository,
      SolicitudProyectoEntidadRepository solicitudProyectoEntidadRepository,
      SolicitudAuthorityHelper solicitudAuthorityHelper) {
    this.repository = solicitudProyectoEntidadFinanciadoraAjenaRepository;
    this.fuenteFinanciacionRepository = fuenteFinanciacionRepository;
    this.tipoFinanciacionRepository = tipoFinanciacionRepository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
    this.solicitudProyectoEntidadRepository = solicitudProyectoEntidadRepository;
    this.solicitudAuthorityHelper = solicitudAuthorityHelper;
  }

  /**
   * Guardar un nuevo {@link SolicitudProyectoEntidadFinanciadoraAjena} y el
   * {@link SolicitudProyectoEntidad} asociado.
   *
   * @param solicitudProyectoEntidadFinanciadoraAjena la entidad
   *                                                  {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                  a guardar.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *         persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoEntidadFinanciadoraAjena create(
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) {
    log.debug("create(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) - start");

    AssertHelper.idIsNull(solicitudProyectoEntidadFinanciadoraAjena.getId(),
        SolicitudProyectoEntidadFinanciadoraAjena.class);
    AssertHelper.idNotNull(solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId(), SolicitudProyecto.class);

    solicitudAuthorityHelper.checkUserHasAuthorityModifySolicitud(
        solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId());

    validateData(solicitudProyectoEntidadFinanciadoraAjena, null);

    SolicitudProyectoEntidadFinanciadoraAjena returnValue = repository.save(solicitudProyectoEntidadFinanciadoraAjena);

    SolicitudProyectoEntidad solicitudProyectoEntidad = new SolicitudProyectoEntidad();
    solicitudProyectoEntidad.setSolicitudProyectoId(solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId());
    solicitudProyectoEntidad.setSolicitudProyectoEntidadFinanciadoraAjena(returnValue);
    solicitudProyectoEntidadRepository.save(solicitudProyectoEntidad);

    log.debug("create(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *
   * @param solicitudProyectoEntidadFinanciadoraAjenaActualizar la entidad
   *                                                            {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                            a actualizar.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *         persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoEntidadFinanciadoraAjena update(
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizar) {
    log.debug(
        "update(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizar) - start");

    solicitudAuthorityHelper.checkUserHasAuthorityModifySolicitud(
        solicitudProyectoEntidadFinanciadoraAjenaActualizar.getSolicitudProyectoId());

    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository
        .findById(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getSolicitudProyectoId())
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(
            solicitudProyectoEntidadFinanciadoraAjenaActualizar.getSolicitudProyectoId()));
    AssertHelper.idNotNull(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getId(),
        SolicitudProyectoEntidadFinanciadoraAjena.class);

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudProyecto
        .getId()),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_MODIFICABLE)
            .parameter(MSG_KEY_ENTITY,
                ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA))
            .parameter(MSG_KEY_MSG, null)
            .build());

    return repository.findById(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getId())
        .map(solicitudProyectoEntidadFinanciadoraAjena -> {

          validateData(solicitudProyectoEntidadFinanciadoraAjenaActualizar, solicitudProyectoEntidadFinanciadoraAjena);

          solicitudProyectoEntidadFinanciadoraAjena
              .setFuenteFinanciacion(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getFuenteFinanciacion());
          solicitudProyectoEntidadFinanciadoraAjena
              .setTipoFinanciacion(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getTipoFinanciacion());
          solicitudProyectoEntidadFinanciadoraAjena.setPorcentajeFinanciacion(
              solicitudProyectoEntidadFinanciadoraAjenaActualizar.getPorcentajeFinanciacion());
          solicitudProyectoEntidadFinanciadoraAjena
              .setImporteFinanciacion(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getImporteFinanciacion());

          SolicitudProyectoEntidadFinanciadoraAjena returnValue = repository
              .save(solicitudProyectoEntidadFinanciadoraAjena);
          log.debug(
              "update(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizar) - end");
          return returnValue;
        }).orElseThrow(() -> new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(
            solicitudProyectoEntidadFinanciadoraAjenaActualizar.getId()));
  }

  /**
   * Elimina el {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *
   * @param id Id del {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, SolicitudProyectoEntidadFinanciadoraAjena.class);

    SolicitudProyectoEntidadFinanciadoraAjena entidadFinanciadoraAjena = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(id));

    solicitudAuthorityHelper.checkUserHasAuthorityModifySolicitud(entidadFinanciadoraAjena.getSolicitudProyectoId());

    solicitudProyectoEntidadRepository
        .findAll(SolicitudProyectoEntidadSpecifications.bySolicitudProyectoEntidadFinanciadoraAjenaId(id)).stream()
        .forEach(solicitudProyectoEntidad -> solicitudProyectoEntidadRepository
            .deleteById(solicitudProyectoEntidad.getId()));

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link SolicitudProyectoEntidadFinanciadoraAjena} por su id.
   *
   * @param id el id de la entidad
   *           {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  @Override
  public SolicitudProyectoEntidadFinanciadoraAjena findById(Long id) {
    log.debug("findById(Long id)  - start");
    final SolicitudProyectoEntidadFinanciadoraAjena returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudProyectoEntidadFinanciadoraAjena} para una
   * {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades
   *         {@link SolicitudProyectoEntidadFinanciadoraAjena} de la
   *         {@link Solicitud} paginadas.
   */
  public Page<SolicitudProyectoEntidadFinanciadoraAjena> findAllBySolicitud(Long solicitudId, String query,
      Pageable pageable) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable pageable) - start");

    solicitudAuthorityHelper.checkUserHasAuthorityViewSolicitud(solicitudId);

    Specification<SolicitudProyectoEntidadFinanciadoraAjena> specs = SolicitudProyectoEntidadFinanciadoraAjenaSpecifications
        .bySolicitudId(solicitudId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoEntidadFinanciadoraAjena> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Valida los nuevos datos de {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * 
   * @param updateData  nuevo valor del
   *                    {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @param currentData valor actual del
   *                    {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  private void validateData(SolicitudProyectoEntidadFinanciadoraAjena updateData,
      SolicitudProyectoEntidadFinanciadoraAjena currentData) {
    log.debug(
        "validateData(SolicitudProyectoEntidadFinanciadoraAjena updateData, SolicitudProyectoEntidadFinanciadoraAjena currentData) - start");

    if (updateData.getFuenteFinanciacion() != null) {
      if (updateData.getFuenteFinanciacion().getId() == null) {
        updateData.setFuenteFinanciacion(null);
      } else {
        updateData.setFuenteFinanciacion(
            fuenteFinanciacionRepository.findById(updateData.getFuenteFinanciacion().getId()).orElseThrow(
                () -> new FuenteFinanciacionNotFoundException(updateData.getFuenteFinanciacion().getId())));

        if (updateData.getFuenteFinanciacion() != null) {
          Assert.isTrue((currentData != null && currentData.getFuenteFinanciacion() != null
              && Objects.equals(currentData.getFuenteFinanciacion().getId(),
                  updateData.getFuenteFinanciacion().getId()))
              || updateData.getFuenteFinanciacion().getActivo(),
              () -> ProblemMessage.builder()
                  .key(MSG_ENTITY_INACTIVO)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_FUENTE_FINANCIACION))
                  .parameter(MSG_KEY_FIELD, updateData.getFuenteFinanciacion().getNombre())
                  .build());

        }
      }
    }

    if (updateData.getTipoFinanciacion() != null) {
      if (updateData.getTipoFinanciacion().getId() == null) {
        updateData.setTipoFinanciacion(null);
      } else {
        updateData.setTipoFinanciacion(tipoFinanciacionRepository.findById(updateData.getTipoFinanciacion().getId())
            .orElseThrow(() -> new TipoFinanciacionNotFoundException(updateData.getTipoFinanciacion().getId())));

        if (updateData.getTipoFinanciacion() != null) {
          Assert.isTrue((currentData != null && currentData.getTipoFinanciacion() != null
              && Objects.equals(currentData.getTipoFinanciacion().getId(), updateData.getTipoFinanciacion().getId()))
              || updateData.getTipoFinanciacion().getActivo(), "El TipoFinanciacion debe estar Activo");
        }
      }
    }

    log.debug(
        "validateData(SolicitudProyectoEntidadFinanciadoraAjena updateData, SolicitudProyectoEntidadFinanciadoraAjena currentData) - end");
  }
}
