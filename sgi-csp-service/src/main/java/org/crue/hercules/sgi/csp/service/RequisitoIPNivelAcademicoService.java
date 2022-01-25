package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessSolicitudException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoIPNivelAcademicoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad RequisitoIPNivelAcademico.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class RequisitoIPNivelAcademicoService {

  private final RequisitoIPNivelAcademicoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final SolicitudRepository solicitudRepository;

  public RequisitoIPNivelAcademicoService(RequisitoIPNivelAcademicoRepository repository,
      ConvocatoriaRepository convocatoriaRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository, SolicitudRepository solicitudRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.solicitudRepository = solicitudRepository;
  }

  /**
   * Obtiene los {@link RequisitoIPNivelAcademico} para un {@link RequisitoIP}.
   *
   * @param requisitoIPId el id del {@link RequisitoIP}.
   * @return la lista de {@link RequisitoIPNivelAcademico} del {@link RequisitoIP}
   *         paginadas.
   */
  public List<RequisitoIPNivelAcademico> findByRequisitoIP(Long requisitoIPId) {
    log.debug("findByRequisitoIP(Long requisitoIPId, String query, Pageable paging) - start");

    Specification<RequisitoIPNivelAcademico> specs = RequisitoIPNivelAcademicoSpecifications
        .byRequisitoIPId(requisitoIPId);

    List<RequisitoIPNivelAcademico> returnValue = repository.findAll(specs);
    log.debug("findByRequisitoIP(Long requisitoIPId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link RequisitoIPNivelAcademico} del
   * {@link RequisitoIP}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param requisitoIPId     el id del {@link RequisitoIP}.
   * @param nivelesAcademicos la lista con los nuevos niveles.
   * @return La lista actualizada de {@link RequisitoIPNivelAcademico}.
   */
  @Transactional
  public List<RequisitoIPNivelAcademico> updateNivelesAcademicos(Long requisitoIPId,
      List<RequisitoIPNivelAcademico> nivelesAcademicos) {
    log.debug("updateNivelesAcademicos(Long requisitoIPId, List<RequisitoIPNivelAcademico> nivelesAcademicos) - start");

    // Los nievelesAcademicos tienen el requisitoIPId especificado
    Assert.isTrue(
        nivelesAcademicos.stream()
            .allMatch(nivelAcademico -> nivelAcademico.getRequisitoIPId() == null
                || nivelAcademico.getRequisitoIPId().equals(requisitoIPId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(RequisitoIPNivelAcademico.class))
            .parameter("related", ApplicationContextSupport.getMessage(RequisitoIP.class)).build());

    // Eliminamos los RequisitoIPNivelAcademico existentes para el requisitoIPId
    // dado
    repository.deleteInBulkByRequisitoIPId(requisitoIPId);

    List<RequisitoIPNivelAcademico> returnValue = new ArrayList<>();
    if (nivelesAcademicos != null && !nivelesAcademicos.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<RequisitoIPNivelAcademico> uniqueNivelesAcademicos = nivelesAcademicos.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniqueNivelesAcademicos);
    }

    log.debug("updateNivelesAcademicos(Long requisitoIPId, List<RequisitoIPNivelAcademico> nivelesAcademicos) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link RequisitoIPNivelAcademico} para un {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @return la lista de {@link RequisitoIPNivelAcademico} de la
   *         {@link Convocatoria}.
   */
  public List<RequisitoIPNivelAcademico> findByConvocatoria(Long convocatoriaId) {
    log.debug("findByConvocatoria(Long requisitoIPId) - start");

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

    Specification<RequisitoIPNivelAcademico> specs = RequisitoIPNivelAcademicoSpecifications
        .byConvocatoriaId(convocatoriaId);

    List<RequisitoIPNivelAcademico> returnValue = repository.findAll(specs);
    log.debug("findByConvocatoria(Long requisitoIPId) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link RequisitoIPNivelAcademico} de la
   * {@link Convocatoria} de la {@link Solicitud} si el usuario que realiza la
   * peticion es el solicitante de la {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @return la lista de {@link RequisitoIPNivelAcademico} de la
   *         {@link Convocatoria} de la {@link Solicitud}.
   */
  public List<RequisitoIPNivelAcademico> findBySolicitudAndUserIsSolicitante(Long solicitudId) {
    log.debug("findBySolicitudAndUserIsSolicitante(Long solicitudId) - start");

    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();

    Solicitud solicitud = solicitudRepository.findOne(SolicitudSpecifications.bySolicitante(personaRef).and(
        SolicitudSpecifications.byId(solicitudId)))
        .orElseThrow(UserNotAuthorizedToAccessSolicitudException::new);

    if (Objects.isNull(solicitud.getConvocatoriaId())) {
      return new ArrayList<>();
    }

    Specification<RequisitoIPNivelAcademico> specs = RequisitoIPNivelAcademicoSpecifications
        .byConvocatoriaId(solicitud.getConvocatoriaId());

    List<RequisitoIPNivelAcademico> returnValue = repository.findAll(specs);
    log.debug("findBySolicitudAndUserIsSolicitante(Long solicitudId) - end");
    return returnValue;
  }

  private boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V");
  }
}