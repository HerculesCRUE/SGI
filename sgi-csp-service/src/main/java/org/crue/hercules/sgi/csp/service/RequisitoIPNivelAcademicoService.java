package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.RequisitoIPNivelAcademicoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessSolicitudException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.RequisitoIPNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoIPNivelAcademicoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
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
  private final SolicitudRepository solicitudRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public RequisitoIPNivelAcademicoService(
      RequisitoIPNivelAcademicoRepository repository,
      SolicitudRepository solicitudRepository,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.solicitudRepository = solicitudRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Obtiene los {@link RequisitoIPNivelAcademico} para un {@link RequisitoIP}.
   *
   * @param requisitoIPId el id del {@link RequisitoIP}.
   * @return la lista de {@link RequisitoIPNivelAcademico} del {@link RequisitoIP}
   *         paginadas.
   */
  public List<RequisitoIPNivelAcademico> findByRequisitoIP(Long requisitoIPId) {
    log.debug("findByRequisitoIP(Long requisitoIPId) - start");

    Specification<RequisitoIPNivelAcademico> specs = RequisitoIPNivelAcademicoSpecifications
        .byRequisitoIPId(requisitoIPId);

    List<RequisitoIPNivelAcademico> returnValue = repository.findAll(specs);
    log.debug("findByRequisitoIP(Long requisitoIPId) - end");
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
            .parameter(AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(RequisitoIPNivelAcademico.class))
            .parameter("related", ApplicationContextSupport.getMessage(RequisitoIP.class)).build());

    // Recuperamos los Niveles academicos existentes en base de datos
    List<RequisitoIPNivelAcademico> nivelesAcademicosBD = repository.findByRequisitoIPId(requisitoIPId);

    List<Long> idsNivelesAcademicosActualizados = nivelesAcademicos.stream().map(RequisitoIPNivelAcademico::getId)
        .collect(Collectors.toList());

    // Niveles academicos a eliminar (existen en base de datos pero no están entre
    // los que se van a modificar)
    List<RequisitoIPNivelAcademico> nivelesAcademicosEliminar = nivelesAcademicosBD.stream()
        .filter(nivelAcademico -> !idsNivelesAcademicosActualizados.contains(nivelAcademico.getId()))
        .collect(Collectors.toList());

    // Eliminamos los niveles academicos no existentes en la nueva lista
    if (!nivelesAcademicosEliminar.isEmpty()) {
      repository.deleteAll(nivelesAcademicosEliminar);
    }

    // Id's de niveles academicos a crear (no tienen id)
    List<RequisitoIPNivelAcademico> nivelesAcademicosCrear = nivelesAcademicos.stream()
        .filter(nivelAcademico -> nivelAcademico.getId() == null).collect(Collectors.toList());

    if (!nivelesAcademicosCrear.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<RequisitoIPNivelAcademico> uniqueNivelesAcademicos = nivelesAcademicosCrear.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      repository.saveAll(uniqueNivelesAcademicos);
    }

    List<RequisitoIPNivelAcademico> returnValue = repository.findByRequisitoIPId(requisitoIPId);

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

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

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

  /**
   * Hace las comprobaciones necesarias para determinar si el
   * {@link RequisitoIPNivelAcademico} puede ser eliminado.
   *
   * @param requisitoIpId Id del {@link RequisitoIP}.
   * @param id            Id del {@link RequisitoIPNivelAcademico}.
   * @return <code>true</code> Si se permite / <code>false</code>
   *         Si no se permite
   */
  public boolean eliminable(Long requisitoIpId, Long id) {
    Specification<RequisitoIPNivelAcademico> specs = RequisitoIPNivelAcademicoSpecifications.byId(id)
        .and(RequisitoIPNivelAcademicoSpecifications.byRequisitoIPId(requisitoIpId));

    Optional<RequisitoIPNivelAcademico> requisitoIpNivelAcademico = repository.findOne(specs);
    if (!requisitoIpNivelAcademico.isPresent()) {
      throw new RequisitoIPNivelAcademicoNotFoundException(id);
    }

    specs = specs.and(RequisitoIPNivelAcademicoSpecifications.withRelatedEntities());
    return repository.count(specs) == 0;
  }

}