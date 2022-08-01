package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.SolicitanteExternoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.SolicitanteExternoRepository;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
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

}
