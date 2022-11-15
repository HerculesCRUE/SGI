package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.SolicitudDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.repository.SolicitudDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudDocumentoServiceImpl implements SolicitudDocumentoService {

  private final SolicitudDocumentoRepository repository;
  private final SolicitudService solicitudService;
  private final SolicitudAuthorityHelper authorityHelper;

  public SolicitudDocumentoServiceImpl(SolicitudDocumentoRepository repository, SolicitudService solicitudService,
      SolicitudAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.solicitudService = solicitudService;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guarda la entidad {@link SolicitudDocumento}.
   * 
   * @param solicitudDocumento la entidad {@link SolicitudDocumento} a guardar.
   * @return SolicitudDocumento la entidad {@link SolicitudDocumento} persistida.
   */
  @Override
  @Transactional
  public SolicitudDocumento create(SolicitudDocumento solicitudDocumento) {
    log.debug("create(SolicitudDocumento solicitudDocumento) - start");

    Assert.isNull(solicitudDocumento.getId(), "Id tiene que ser null para crear la SolicitudDocumento");

    AssertHelper.idNotNull(solicitudDocumento.getSolicitudId(), SolicitudDocumento.class);

    Assert.notNull(solicitudDocumento.getNombre(),
        "Nombre documento no puede ser null para crear la SolicitudDocumento");
    Assert.notNull(solicitudDocumento.getDocumentoRef(),
        "La referencia del documento no puede ser null para crear la SolicitudDocumento");

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificableEstadoAndDocumentos(solicitudDocumento.getSolicitudId()),
        "No se puede modificar SolicitudDocumento");

    SolicitudDocumento returnValue = repository.save(solicitudDocumento);

    log.debug("create(SolicitudDocumento solicitudDocumento) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudDocumento}.
   * 
   * @param solicitudDocumento rolSocioActualizar {@link SolicitudDocumento} con
   *                           los datos actualizados.
   * @return {@link SolicitudDocumento} actualizado.
   */
  @Override
  @Transactional
  public SolicitudDocumento update(SolicitudDocumento solicitudDocumento) {
    log.debug("update(SolicitudDocumento solicitudDocumento) - start");

    Assert.notNull(solicitudDocumento.getId(), "Id no puede ser null para actualizar SolicitudDocumento");

    Assert.notNull(solicitudDocumento.getSolicitudId(),
        "Solicitud id no puede ser null para crear una SolicitudModalidad");

    Assert.notNull(solicitudDocumento.getNombre(),
        "Nombre documento no puede ser null para actualizar la SolicitudDocumento");
    Assert.notNull(solicitudDocumento.getDocumentoRef(),
        "La referencia del documento no puede ser null para actualizar la SolicitudDocumento");

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudDocumento.getSolicitudId()),
        "No se puede modificar SolicitudDocumento");

    return repository.findById(solicitudDocumento.getId()).map(solicitudDocumentoExistente -> {

      solicitudDocumentoExistente.setComentario(solicitudDocumento.getComentario());
      solicitudDocumentoExistente.setDocumentoRef(solicitudDocumento.getDocumentoRef());
      solicitudDocumentoExistente.setTipoDocumento(solicitudDocumento.getTipoDocumento());
      solicitudDocumentoExistente.setNombre(solicitudDocumento.getNombre());

      SolicitudDocumento returnValue = repository.save(solicitudDocumentoExistente);

      log.debug("update(SolicitudDocumento solicitudDocumento) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudDocumentoNotFoundException(solicitudDocumento.getId()));
  }

  /**
   * Obtiene una entidad {@link SolicitudDocumento} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudDocumento}.
   * @return SolicitudDocumento la entidad {@link SolicitudDocumento}.
   */
  @Override
  public SolicitudDocumento findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudDocumentoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudDocumento}.
   *
   * @param id Id del {@link SolicitudDocumento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "SolicitudDocumento id no puede ser null para eliminar un SolicitudDocumento");
    if (!repository.existsById(id)) {
      throw new SolicitudDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudDocumento} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link SolicitudDocumento} de la
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudDocumento> findAllBySolicitud(Long solicitudId, String query, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - start");

    authorityHelper.checkUserHasAuthorityViewSolicitud(solicitudId);

    Specification<SolicitudDocumento> specs = SolicitudDocumentoSpecifications.bySolicitudId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudDocumento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Guarda la entidad {@link SolicitudDocumento}.
   * 
   * @param solicitudPublicId  el id de la {@link Solicitud}.
   * @param solicitudDocumento la entidad {@link SolicitudDocumento} a guardar.
   * @return la entidad {@link SolicitudDocumento} persistida.
   */
  @Override
  @Transactional
  public SolicitudDocumento createByExternalUser(String solicitudPublicId, SolicitudDocumento solicitudDocumento) {
    log.debug("createByExternalUser(String solicitudPublicId, SolicitudDocumento solicitudDocumento) - start");

    Solicitud solicitud = authorityHelper.getSolicitudByPublicId(solicitudPublicId);
    authorityHelper.checkExternalUserHasAuthorityModifySolicitud(solicitud);
    solicitudDocumento.setSolicitudId(solicitud.getId());

    Assert.isNull(solicitudDocumento.getId(), "Id tiene que ser null para crear la SolicitudDocumento");
    AssertHelper.idNotNull(solicitudDocumento.getSolicitudId(), SolicitudDocumento.class);
    Assert.notNull(solicitudDocumento.getNombre(),
        "Nombre documento no puede ser null para crear la SolicitudDocumento");
    Assert.notNull(solicitudDocumento.getDocumentoRef(),
        "La referencia del documento no puede ser null para crear la SolicitudDocumento");

    SolicitudDocumento returnValue = repository.save(solicitudDocumento);

    log.debug("createByExternalUser(String solicitudPublicId, SolicitudDocumento solicitudDocumento) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudDocumento}.
   * 
   * @param solicitudPublicId  el id de la {@link Solicitud}.
   * @param solicitudDocumento {@link SolicitudDocumento} con los datos
   *                           actualizados.
   * @return {@link SolicitudDocumento} actualizado.
   */
  @Override
  @Transactional
  public SolicitudDocumento updateByExternalUser(String solicitudPublicId, SolicitudDocumento solicitudDocumento) {
    log.debug("updateByExternalUser(String solicitudPublicId, SolicitudModalidad solicitudDocumento) - start");

    Assert.notNull(solicitudDocumento.getId(), "Id no puede ser null para actualizar SolicitudDocumento");

    Assert.notNull(solicitudDocumento.getNombre(),
        "Nombre documento no puede ser null para actualizar la SolicitudDocumento");
    Assert.notNull(solicitudDocumento.getDocumentoRef(),
        "La referencia del documento no puede ser null para actualizar la SolicitudDocumento");

    return repository.findById(solicitudDocumento.getId()).map(solicitudDocumentoExistente -> {

      Solicitud solicitud = authorityHelper.getSolicitudByPublicId(solicitudPublicId);
      authorityHelper.checkExternalUserHasAuthorityModifySolicitud(solicitud);
      Assert.isTrue(solicitud.getId().equals(solicitudDocumentoExistente.getSolicitudId()),
          "No coincide el id de la solicitud");

      solicitudDocumentoExistente.setComentario(solicitudDocumento.getComentario());
      solicitudDocumentoExistente.setDocumentoRef(solicitudDocumento.getDocumentoRef());
      solicitudDocumentoExistente.setTipoDocumento(solicitudDocumento.getTipoDocumento());
      solicitudDocumentoExistente.setNombre(solicitudDocumento.getNombre());

      SolicitudDocumento returnValue = repository.save(solicitudDocumentoExistente);

      log.debug("updateByExternalUser(String solicitudPublicId, SolicitudDocumento solicitudDocumento) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudDocumentoNotFoundException(solicitudDocumento.getId()));
  }

  /**
   * Elimina el {@link SolicitudDocumento}.
   *
   * @param solicitudPublicId el id de la {@link Solicitud}.
   * @param id                Id del {@link SolicitudDocumento}.
   */
  @Override
  @Transactional
  public void deleteByExternalUser(String solicitudPublicId, Long id) {
    log.debug("deleteByExternalUser(String solicitudPublicId, Long id) - start");

    Solicitud solicitud = authorityHelper.getSolicitudByPublicId(solicitudPublicId);
    authorityHelper.checkExternalUserHasAuthorityModifySolicitud(solicitud);

    AssertHelper.idNotNull(id, SolicitudDocumento.class);
    if (!repository.existsById(id)) {
      throw new SolicitudDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("deleteByExternalUser(String solicitudPublicId, Long id) - end");
  }

  /**
   * Obtiene las {@link SolicitudDocumento} para una {@link Solicitud}.
   *
   * @param solicitudPublicId el id de la {@link Solicitud}.
   * @param query             la información del filtro.
   * @param paging            la información de la paginación.
   * @return la lista de entidades {@link SolicitudDocumento} de la
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudDocumento> findAllBySolicitudPublicId(String solicitudPublicId, String query, Pageable paging) {
    log.debug("findAllBySolicitudPublicId(String solicitudPublicId, String query, Pageable paging) - start");
    Long solicitudId = authorityHelper.getSolicitudIdByPublicId(solicitudPublicId);
    Specification<SolicitudDocumento> specs = SolicitudDocumentoSpecifications.bySolicitudId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudDocumento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudPublicId(String solicitudPublicId, String query, Pageable paging) - end");
    return returnValue;
  }

}
