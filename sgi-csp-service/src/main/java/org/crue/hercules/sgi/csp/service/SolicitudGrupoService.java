package org.crue.hercules.sgi.csp.service;

import java.time.Instant;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudGrupoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifySolicitudException;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudGrupo;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudGrupoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link SolicitudGrupo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class SolicitudGrupoService {

  private final SgiConfigProperties sgiConfigProperties;

  private final SolicitudAuthorityHelper solicitudAuthorityHelper;

  private final GrupoRepository grupoRepository;
  private final SolicitudRepository solicitudRepository;
  private final SolicitudGrupoRepository repository;

  /**
   * Guarda la entidad {@link SolicitudGrupo}.
   * 
   * @param solicitudGrupo la entidad {@link SolicitudGrupo} a guardar.
   * @return la entidad {@link SolicitudGrupo} persistida.
   */
  @Transactional
  public SolicitudGrupo create(SolicitudGrupo solicitudGrupo) {
    log.debug("create(SolicitudGrupo solicitudGrupo) - start");

    AssertHelper.idIsNull(solicitudGrupo.getId(), SolicitudGrupo.class);
    checkRelatedGrupoAndSolicitud(solicitudGrupo.getGrupoId(), solicitudGrupo.getSolicitudId());

    if (!solicitudAuthorityHelper.hasPermisosEdicion(solicitudGrupo.getSolicitudId())) {
      throw new UserNotAuthorizedToModifySolicitudException();
    }

    SolicitudGrupo returnValue = repository.save(solicitudGrupo);

    log.debug("create(SolicitudGrupo solicitudGrupo) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudGrupo}.
   *
   * @param solicitudGrupo {@link SolicitudGrupo} con los datos
   *                       actualizados.
   * @return {@link SolicitudGrupo} actualizado.
   */
  @Transactional
  public SolicitudGrupo update(SolicitudGrupo solicitudGrupo) {
    log.debug("update(SolicitudGrupo solicitudGrupo) - start");

    AssertHelper.idNotNull(solicitudGrupo.getId(), SolicitudGrupo.class);
    checkRelatedGrupoAndSolicitud(solicitudGrupo.getGrupoId(), solicitudGrupo.getSolicitudId());

    return repository.findById(solicitudGrupo.getId()).map(data -> {
      if (!solicitudAuthorityHelper.hasPermisosEdicion(data.getSolicitudId())) {
        throw new UserNotAuthorizedToModifySolicitudException();
      }

      data.setGrupoId(solicitudGrupo.getGrupoId());

      SolicitudGrupo returnValue = repository.save(data);

      log.debug("update(SolicitudGrupo solicitudGrupo) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudGrupoNotFoundException(solicitudGrupo.getId()));
  }

  /**
   * Obtiene una entidad {@link SolicitudGrupo} por el id de la {@link Solicitud}.
   * 
   * @param solicitudId Identificador de la entidad {@link Solicitud}.
   * @return la entidad {@link SolicitudGrupo}.
   */
  public SolicitudGrupo findBySolicitudId(Long solicitudId) {
    log.debug("findBySolicitudId(Long solicitudId) - start");
    AssertHelper.idNotNull(solicitudId, Solicitud.class);
    SolicitudGrupo returnValue = repository.findBySolicitudId(solicitudId);
    log.debug("findBySolicitudId(Long solicitudId) - end");
    return returnValue;
  }

  /**
   * Comprueba si el grupo y la solicitud se pueden relacionar porque el
   * {@link Solicitud#solicitanteRef} es responsable o persona autorizada del
   * {@link Grupo} en el momento actual.
   * 
   * @param grupoId     Identificador de la entidad {@link Grupo}.
   * @param solicitudId Identificador de la entidad {@link Solicitud}.
   * @throws NoRelatedEntitiesException si no estan relacionados
   */
  private void checkRelatedGrupoAndSolicitud(Long grupoId, Long solicitudId) {
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    Solicitud solicitud = solicitudRepository.findById(solicitudId)
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));

    Specification<Grupo> specs = GrupoSpecifications.distinct()
        .and(GrupoSpecifications.activos())
        .and(GrupoSpecifications.byId(grupoId))
        .and(GrupoSpecifications.byResponsable(solicitud.getSolicitanteRef(), fechaActual)
            .or(GrupoSpecifications.byPersonaAutorizada(solicitud.getSolicitanteRef(), fechaActual)));

    if (grupoRepository.count(specs) == 0) {
      throw new NoRelatedEntitiesException(Grupo.class, Solicitud.class);
    }
  }

}
