package org.crue.hercules.sgi.csp.service;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.exceptions.SolicitanteExternoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifySolicitudException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudExterna;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitanteExternoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudExternaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link SolicitanteExterno}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class SolicitanteExternoService {

  private final SolicitanteExternoRepository repository;
  private final SolicitudExternaRepository solicitudExternaRepository;
  private final SolicitudRepository solicitudRepository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final SolicitudComService solicitudComService;
  private final SolicitudAuthorityHelper authorityHelper;

  /**
   * Guarda la entidad {@link SolicitanteExterno}.
   * 
   * @param solicitanteExterno la entidad {@link SolicitanteExterno} a guardar.
   * @return la entidad {@link SolicitanteExterno} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public SolicitanteExterno create(@Valid SolicitanteExterno solicitanteExterno) {
    log.debug("create(SolicitanteExterno solicitanteExterno) - start");

    AssertHelper.idIsNull(solicitanteExterno.getId(), SolicitanteExterno.class);
    authorityHelper.checkUserHasAuthorityViewSolicitud(solicitanteExterno.getSolicitudId());

    SolicitanteExterno returnValue = repository.save(solicitanteExterno);

    SolicitudExterna solicitudExterna = SolicitudExterna.builder().solicitudId(solicitanteExterno.getSolicitudId())
        .build();

    solicitudExternaRepository.save(solicitudExterna);

    enviarComunicadoSolicitudExterna(solicitanteExterno.getSolicitudId(), solicitudExterna.getId());

    log.debug("create(SolicitanteExterno solicitanteExterno) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos de la {@link SolicitanteExterno}.
   *
   * @param solicitanteExternoActualizar {@link SolicitanteExterno} con los datos
   *                                     actualizados.
   * @return {@link SolicitanteExterno} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public SolicitanteExterno update(@Valid SolicitanteExterno solicitanteExternoActualizar) {
    log.debug("update(SolicitanteExterno solicitanteExternoActualizar) - start");

    AssertHelper.idNotNull(solicitanteExternoActualizar.getId(), SolicitanteExterno.class);
    authorityHelper.checkUserHasAuthorityViewSolicitud(solicitanteExternoActualizar.getSolicitudId());

    return repository.findById(solicitanteExternoActualizar.getId()).map(data -> {
      data.setNombre(solicitanteExternoActualizar.getNombre());
      data.setApellidos(solicitanteExternoActualizar.getApellidos());
      data.setTipoDocumentoRef(solicitanteExternoActualizar.getTipoDocumentoRef());
      data.setNumeroDocumento(solicitanteExternoActualizar.getNumeroDocumento());
      data.setSexoRef(solicitanteExternoActualizar.getSexoRef());
      data.setFechaNacimiento(solicitanteExternoActualizar.getFechaNacimiento());
      data.setPaisContactoRef(solicitanteExternoActualizar.getPaisNacimientoRef());
      data.setTelefono(solicitanteExternoActualizar.getTelefono());
      data.setEmail(solicitanteExternoActualizar.getEmail());
      data.setDireccion(solicitanteExternoActualizar.getDireccion());
      data.setPaisContactoRef(solicitanteExternoActualizar.getPaisContactoRef());
      data.setComunidadRef(solicitanteExternoActualizar.getComunidadRef());
      data.setProvinciaRef(solicitanteExternoActualizar.getProvinciaRef());
      data.setCiudad(solicitanteExternoActualizar.getCiudad());
      data.setCodigoPostal(solicitanteExternoActualizar.getCodigoPostal());

      SolicitanteExterno returnValue = repository.save(data);

      log.debug("update(SolicitanteExterno solicitanteExternoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitanteExternoNotFoundException(solicitanteExternoActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link SolicitanteExterno} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitanteExterno}.
   * @return la entidad {@link SolicitanteExterno}.
   */
  public SolicitanteExterno findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, SolicitanteExterno.class);

    final SolicitanteExterno returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitanteExternoNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewSolicitud(returnValue.getSolicitudId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link SolicitanteExterno} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @return el {@link SolicitanteExterno} para una {@link Solicitud}.
   */
  public SolicitanteExterno findBySolicitud(Long solicitudId) {
    log.debug("findBySolicitud(Long solicitudId) - start");
    Optional<SolicitanteExterno> returnValue = repository.findBySolicitudId(solicitudId);
    log.debug("findBySolicitud(Long solicitudId) - end");
    return (returnValue.isPresent()) ? returnValue.get() : null;
  }

  /**
   * Guarda la entidad {@link SolicitanteExterno}.
   * 
   * @param solicitanteExterno la entidad {@link SolicitanteExterno} a guardar.
   * @return la entidad {@link SolicitanteExterno} persistida y el uuid de la
   *         solicitud.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Pair<SolicitanteExterno, UUID> createByExternalUser(@Valid SolicitanteExterno solicitanteExterno) {
    log.debug("createByExternalUser(SolicitanteExterno solicitanteExterno) - start");

    AssertHelper.idIsNull(solicitanteExterno.getId(), SolicitanteExterno.class);
    Solicitud solicitud = solicitudRepository.findById(solicitanteExterno.getSolicitudId())
        .orElseThrow(() -> new SolicitudNotFoundException(solicitanteExterno.getSolicitudId()));
    SolicitanteExterno solicitante = findBySolicitud(solicitanteExterno.getSolicitudId());
    if (solicitud.getCreadorRef() != null || solicitante != null) {
      throw new UserNotAuthorizedToModifySolicitudException();
    }

    SolicitanteExterno returnValue = repository.save(solicitanteExterno);

    SolicitudExterna solicitudExterna = SolicitudExterna.builder().solicitudId(solicitanteExterno.getSolicitudId())
        .build();

    solicitudExternaRepository.save(solicitudExterna);

    enviarComunicadoSolicitudExterna(solicitanteExterno.getSolicitudId(), solicitudExterna.getId());

    log.debug("createByExternalUser(SolicitanteExterno solicitanteExterno) - end");
    return Pair.of(returnValue, solicitudExterna.getId());
  }

  /**
   * Actualiza los datos de la {@link SolicitanteExterno}.
   *
   * @param solicitudPublicId            el id de la {@link Solicitud}.
   * @param solicitanteExternoActualizar {@link SolicitanteExterno} con los datos
   *                                     actualizados.
   * @return {@link SolicitanteExterno} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public SolicitanteExterno updateByExternalUser(String solicitudPublicId,
      @Valid SolicitanteExterno solicitanteExternoActualizar) {
    log.debug(
        "updateByExternalUser(String solicitudPublicId, SolicitanteExterno solicitanteExternoActualizar) - start");

    AssertHelper.idNotNull(solicitanteExternoActualizar.getId(), SolicitanteExterno.class);
    Solicitud solicitud = authorityHelper.getSolicitudByPublicId(solicitudPublicId);
    authorityHelper.checkExternalUserHasAuthorityModifySolicitud(solicitud);

    return repository.findById(solicitanteExternoActualizar.getId()).map(data -> {
      data.setNombre(solicitanteExternoActualizar.getNombre());
      data.setApellidos(solicitanteExternoActualizar.getApellidos());
      data.setTipoDocumentoRef(solicitanteExternoActualizar.getTipoDocumentoRef());
      data.setNumeroDocumento(solicitanteExternoActualizar.getNumeroDocumento());
      data.setSexoRef(solicitanteExternoActualizar.getSexoRef());
      data.setFechaNacimiento(solicitanteExternoActualizar.getFechaNacimiento());
      data.setPaisContactoRef(solicitanteExternoActualizar.getPaisNacimientoRef());
      data.setTelefono(solicitanteExternoActualizar.getTelefono());
      data.setEmail(solicitanteExternoActualizar.getEmail());
      data.setDireccion(solicitanteExternoActualizar.getDireccion());
      data.setPaisContactoRef(solicitanteExternoActualizar.getPaisContactoRef());
      data.setComunidadRef(solicitanteExternoActualizar.getComunidadRef());
      data.setProvinciaRef(solicitanteExternoActualizar.getProvinciaRef());
      data.setCiudad(solicitanteExternoActualizar.getCiudad());
      data.setCodigoPostal(solicitanteExternoActualizar.getCodigoPostal());

      SolicitanteExterno returnValue = repository.save(data);

      log.debug(
          "updateByExternalUser(String solicitudPublicId, SolicitanteExterno solicitanteExternoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitanteExternoNotFoundException(solicitanteExternoActualizar.getId()));
  }

  /**
   * Obtiene el {@link SolicitanteExterno} para una {@link Solicitud}.
   *
   * @param solicitudPublicId el id de la {@link Solicitud}.
   * @return el {@link SolicitanteExterno} para una {@link Solicitud}.
   */
  public SolicitanteExterno findBySolicitudPublicId(String solicitudPublicId) {
    log.debug("findBySolicitudPublicId(String solicitudPublicId) - start");
    Long solicitudId = authorityHelper.getSolicitudIdByPublicId(solicitudPublicId);
    Optional<SolicitanteExterno> returnValue = repository.findBySolicitudId(solicitudId);
    log.debug("findBySolicitudPublicId(String solicitudPublicId) - end");
    return (returnValue.isPresent()) ? returnValue.get() : null;
  }

  private void enviarComunicadoSolicitudExterna(Long solicitudId, UUID uuid) {
    log.debug("enviarComunicadoSolicitudExterna(Solicitud solicitud) - start");
    try {

      String tituloConvocatoria = this.convocatoriaRepository
          .findOne(ConvocatoriaSpecifications.bySolicitudId(solicitudId)).map(Convocatoria::getTitulo)
          .orElseGet(() -> this.solicitudRepository.findById(solicitudId).map(Solicitud::getConvocatoriaExterna)
              .orElse(StringUtils.EMPTY));

      this.solicitudComService.enviarComunicadoSolicitudUsuarioExterno(solicitudId, tituloConvocatoria,
          uuid.toString());

      log.debug("enviarComunicadoSolicitudExterna(Solicitud solicitud) - end");
    } catch (Exception e) {
      log.error("enviarComunicadoSolicitudExterna(Solicitud solicitud)", e);
    }
  }

}
