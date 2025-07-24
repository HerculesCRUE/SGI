package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoSocioSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
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

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudProyectoSocio}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoSocioServiceImpl implements SolicitudProyectoSocioService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_FIELD_EMPRESA_REF = "empresaRef";
  private static final String MSG_MODEL_SOLICITUD_PROYECTO_SOCIO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio.message";
  private static final String MSG_ENTITY_MODIFICABLE = "org.springframework.util.Assert.entity.modificable.message";

  private final SolicitudProyectoSocioRepository repository;

  private final SolicitudProyectoSocioEquipoRepository solicitudProyectoEquipoSocioRepository;

  private final SolicitudProyectoSocioPeriodoPagoRepository solicitudProyectoSocioPeriodoPagoRepository;

  private final SolicitudProyectoSocioPeriodoJustificacionRepository solicitudProyectoSocioPeriodoJustificacionRepository;

  private final SolicitudRepository solicitudRepository;

  private final SolicitudService solicitudService;

  private final SolicitudAuthorityHelper solicitudAuthorityHelper;

  public SolicitudProyectoSocioServiceImpl(SolicitudProyectoSocioRepository repository,
      SolicitudRepository solicitudRepository,
      SolicitudProyectoSocioEquipoRepository solicitudProyectoEquipoSocioRepository,
      SolicitudProyectoSocioPeriodoPagoRepository solicitudProyectoSocioPeriodoPagoRepository,
      SolicitudProyectoSocioPeriodoJustificacionRepository solicitudProyectoSocioPeriodoJustificacionRepository,
      SolicitudService solicitudService,
      SolicitudAuthorityHelper solicitudAuthorityHelper) {
    this.repository = repository;
    this.solicitudRepository = solicitudRepository;
    this.solicitudProyectoEquipoSocioRepository = solicitudProyectoEquipoSocioRepository;
    this.solicitudProyectoSocioPeriodoPagoRepository = solicitudProyectoSocioPeriodoPagoRepository;
    this.solicitudProyectoSocioPeriodoJustificacionRepository = solicitudProyectoSocioPeriodoJustificacionRepository;
    this.solicitudService = solicitudService;
    this.solicitudAuthorityHelper = solicitudAuthorityHelper;

  }

  /**
   * Guarda la entidad {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio la entidad {@link SolicitudProyectoSocio} a
   *                               guardar.
   * @return SolicitudProyectoSocio la entidad {@link SolicitudProyectoSocio}
   *         persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoSocio create(SolicitudProyectoSocio solicitudProyectoSocio) {
    log.debug("create(SolicitudProyectoSocio solicitudProyectoSocio) - start");

    AssertHelper.idIsNull(solicitudProyectoSocio.getId(), SolicitudProyectoSocio.class);

    solicitudAuthorityHelper
        .checkUserHasAuthorityModifySolicitud(solicitudProyectoSocio.getSolicitudProyectoId());

    validateSolicitudProyectoSocio(solicitudProyectoSocio);

    SolicitudProyectoSocio returnValue = repository.save(solicitudProyectoSocio);

    log.debug("create(SolicitudProyectoSocio solicitudProyectoSocio) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio rolSocioActualizar
   *                               {@link SolicitudProyectoSocio} con los datos
   *                               actualizados.
   * @return {@link SolicitudProyectoSocio} actualizado.
   */
  @Override
  @Transactional
  public SolicitudProyectoSocio update(SolicitudProyectoSocio solicitudProyectoSocio) {
    log.debug("update(SolicitudProyectoSocio solicitudProyectoSocio) - start");

    AssertHelper.idNotNull(solicitudProyectoSocio.getId(), SolicitudProyectoSocio.class);

    solicitudAuthorityHelper
        .checkUserHasAuthorityModifySolicitud(solicitudProyectoSocio.getSolicitudProyectoId());

    validateSolicitudProyectoSocio(solicitudProyectoSocio);

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudProyectoSocio.getSolicitudProyectoId()),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_MODIFICABLE)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_SOCIO))
            .parameter(MSG_KEY_MSG, null)
            .build());

    return repository.findById(solicitudProyectoSocio.getId()).map((solicitudProyectoSocioExistente) -> {

      solicitudProyectoSocioExistente.setMesInicio(solicitudProyectoSocio.getMesInicio());
      solicitudProyectoSocioExistente.setMesFin(solicitudProyectoSocio.getMesFin());
      solicitudProyectoSocioExistente.setRolSocio(solicitudProyectoSocio.getRolSocio());
      solicitudProyectoSocioExistente.setNumInvestigadores(solicitudProyectoSocio.getNumInvestigadores());
      solicitudProyectoSocioExistente.setImporteSolicitado(solicitudProyectoSocio.getImporteSolicitado());
      solicitudProyectoSocioExistente.setImportePresupuestado(solicitudProyectoSocio.getImportePresupuestado());
      SolicitudProyectoSocio returnValue = repository.save(solicitudProyectoSocioExistente);

      log.debug("update(SolicitudProyectoSocio solicitudProyectoSocio) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocio.getId()));
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoSocio} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoSocio}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocio} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoSocio}.
   * @return SolicitudProyectoSocio la entidad {@link SolicitudProyectoSocio}.
   */
  @Override
  public SolicitudProyectoSocio findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoSocio returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoSocio} y sus entidades relacionadas:
   * {@link SolicitudProyectoSocioEquipo},
   * {@link SolicitudProyectoSocioPeriodoPago}
   *
   * @param id Id del {@link SolicitudProyectoSocio}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, SolicitudProyectoSocio.class);

    SolicitudProyectoSocio solicitudProyectoSocio = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(id));
    solicitudAuthorityHelper
        .checkUserHasAuthorityModifySolicitud(solicitudProyectoSocio.getSolicitudProyectoId());

    solicitudProyectoSocioPeriodoPagoRepository.deleteBySolicitudProyectoSocioId(id);
    solicitudProyectoEquipoSocioRepository.deleteBySolicitudProyectoSocioId(id);
    solicitudProyectoSocioPeriodoJustificacionRepository.deleteBySolicitudProyectoSocioId(id);
    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudProyectoSocio} para un {@link Solicitud}.
   *
   * @param solicitudId el id del {@link Solicitud}.
   * @param query       la información del filtro.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoSocio} del
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudProyectoSocio> findAllBySolicitud(Long solicitudId, String query, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - start");

    solicitudAuthorityHelper.checkUserHasAuthorityViewSolicitud(solicitudId);

    Specification<SolicitudProyectoSocio> specs = SolicitudProyectoSocioSpecifications.bySolicitudId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoSocio> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Realiza las validaciones comunes para las operaciones de creación y
   * acutalización de solicitud proyecto socio.
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio}
   */
  private void validateSolicitudProyectoSocio(SolicitudProyectoSocio solicitudProyectoSocio) {
    log.debug("validateSolicitudProyectoSocio(SolicitudProyectoSocio solicitudProyectoSocio) - start");

    AssertHelper.idNotNull(solicitudProyectoSocio.getSolicitudProyectoId(), SolicitudProyecto.class);
    AssertHelper.entityNotNull(solicitudProyectoSocio.getRolSocio(), SolicitudProyectoSocio.class, RolSocio.class);
    AssertHelper.fieldNotNull(solicitudProyectoSocio.getEmpresaRef(), SolicitudProyectoSocio.class,
        MSG_FIELD_EMPRESA_REF);

    if (!solicitudRepository.existsById(solicitudProyectoSocio.getSolicitudProyectoId())) {
      throw new SolicitudNotFoundException(solicitudProyectoSocio.getSolicitudProyectoId());
    }

    log.debug("validateSolicitudProyectoSocio(SolicitudProyectoSocio solicitudProyectoSocio) - end");
  }

  /**
   * Indica si {@link SolicitudProyectoSocio} tiene
   * {@link SolicitudProyectoSocioPeriodoJustificacion},
   * {@link SolicitudProyectoSocioPeriodoPago} y/o
   * {@link SolicitudProyectoSocioEquipo} relacionadas.
   *
   * @param id Id de la {@link SolicitudProyectoSocio}.
   * @return True si tiene {@link SolicitudProyectoSocioPeriodoJustificacion},
   *         {@link SolicitudProyectoSocioPeriodoPago} y/o
   *         {@link SolicitudProyectoSocioEquipo} relacionadas. En caso contrario
   *         false
   */
  public Boolean vinculaciones(Long id) {
    log.debug("vinculaciones(Long id) - start");
    final Boolean returnValue = repository.vinculaciones(id);
    log.debug("vinculaciones(Long id) - start");
    return returnValue;
  }

  /**
   * Obtiene el {@link SolicitudProyectoSocio} de la {@link SolicitudProyecto}.
   * 
   * @param id {@link SolicitudProyecto}.
   * @return {@link SolicitudProyectoSocio}.
   */
  @Override
  public Boolean hasSolicitudSocio(Long id) {
    log.debug("hasSolicitudSocio(Long id) - start");
    final List<SolicitudProyectoSocio> solicitudProyectoSocios = repository.findAllBySolicitudProyectoId(id);
    Boolean returnValue = CollectionUtils.isNotEmpty(solicitudProyectoSocios);
    log.debug("hasSolicitudSocio(Long id) - end");
    return returnValue;
  }

  @Override
  public boolean existsSolicitudProyectoSocioPeriodoPagoBySolicitudProyectoSocioId(Long solicitudProyectoId) {

    return !this.repository.findAllBySolicitudProyectoId(solicitudProyectoId).stream()
        .filter(soliciturProyectoSocio -> this.solicitudProyectoSocioPeriodoPagoRepository
            .existsBySolicitudProyectoSocioId(soliciturProyectoSocio.getId()))
        .collect(Collectors.toList()).isEmpty();
  }

  @Override
  public boolean existsSolicitudProyectoSocioPeriodoJustificacionBySolicitudProyectoSocioId(Long solicitudProyectoId) {

    return !this.repository.findAllBySolicitudProyectoId(solicitudProyectoId).stream()
        .filter(soliciturProyectoSocio -> this.solicitudProyectoSocioPeriodoJustificacionRepository
            .existsBySolicitudProyectoSocioId(soliciturProyectoSocio.getId()))
        .collect(Collectors.toList()).isEmpty();
  }

  @Override
  public boolean hasAnySolicitudProyectoSocioWithRolCoordinador(Long idSolicitudProyecto) {

    return this.repository.existsBySolicitudProyectoIdAndRolSocioCoordinador(idSolicitudProyecto, Boolean.TRUE);
  }

}