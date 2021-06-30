package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;

import org.crue.hercules.sgi.csp.exceptions.SolicitudHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.repository.SolicitudHitoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudHitoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudHitoService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudHito}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudHitoServiceImpl implements SolicitudHitoService {

  private final SolicitudHitoRepository repository;

  private final SolicitudRepository solicitudRepository;

  private final TipoHitoRepository tipoHitoRepository;

  private final SolicitudService solicitudService;

  public SolicitudHitoServiceImpl(SolicitudHitoRepository repository, SolicitudRepository solicitudRepository,
      TipoHitoRepository tipoHitoRepository, SolicitudService solicitudService) {
    this.repository = repository;
    this.solicitudRepository = solicitudRepository;
    this.tipoHitoRepository = tipoHitoRepository;
    this.solicitudService = solicitudService;
  }

  /**
   * Guarda la entidad {@link SolicitudHito}.
   * 
   * @param solicitudHito la entidad {@link SolicitudHito} a guardar.
   * @return SolicitudHito la entidad {@link SolicitudHito} persistida.
   */
  @Override
  @Transactional
  public SolicitudHito create(SolicitudHito solicitudHito) {
    log.debug("create(SolicitudHito solicitudHito) - start");

    Assert.isNull(solicitudHito.getId(), "Id tiene que ser null para crear la SolicitudHito");
    Assert.notNull(solicitudHito.getSolicitudId(), "La solicitud no puede ser null para crear la SolicitudHito");
    Assert.notNull(solicitudHito.getFecha(), "La fecha no puede ser null para crear la SolicitudHito");
    Assert.notNull(solicitudHito.getTipoHito(), "El tipo hito no puede ser null para crear la SolicitudHito");
    Assert.notNull(solicitudHito.getGeneraAviso(), "Generar aviso no puede ser null para crear la SolicitudHito");

    if (solicitudHito.getFecha().isBefore(Instant.now())) {
      solicitudHito.setGeneraAviso(false);
    }

    Assert.isTrue(!repository.findBySolicitudIdAndFechaAndTipoHitoId(solicitudHito.getSolicitudId(),
        solicitudHito.getFecha(), solicitudHito.getTipoHito().getId()).isPresent(),
        "Ya existe un Hito con el mismo tipo en esa fecha");

    solicitudRepository.findById(solicitudHito.getSolicitudId())
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudHito.getSolicitudId()));

    solicitudHito.setTipoHito(tipoHitoRepository.findById(solicitudHito.getTipoHito().getId())
        .orElseThrow(() -> new TipoHitoNotFoundException(solicitudHito.getTipoHito().getId())));

    SolicitudHito returnValue = repository.save(solicitudHito);

    log.debug("create(SolicitudHito solicitudHito) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudHito}.
   * 
   * @param solicitudHito           rolSocioActualizar {@link SolicitudHito} con
   *                                los datos actualizados.
   * @param isAdministradorOrGestor Indiciador de si el usuario es administrador o
   *                                gestor.
   * @return {@link SolicitudHito} actualizado.
   */
  @Override
  @Transactional
  public SolicitudHito update(SolicitudHito solicitudHito, Boolean isAdministradorOrGestor) {
    log.debug("update(SolicitudHito solicitudHito) - start");

    Assert.notNull(solicitudHito.getId(), "Id no puede ser null para actualizar SolicitudHito");
    Assert.notNull(solicitudHito.getSolicitudId(), "La solicitud no puede ser null para actualizar la SolicitudHito");
    Assert.notNull(solicitudHito.getFecha(), "Nombre documento no puede ser null para actualizar la SolicitudHito");
    Assert.notNull(solicitudHito.getTipoHito(),
        "La referencia del documento no puede ser null para actualizar la SolicitudHito");
    Assert.notNull(solicitudHito.getGeneraAviso(), "Generar aviso no puede ser null para crear la SolicitudHito");

    if (solicitudHito.getFecha().isBefore(Instant.now())) {
      solicitudHito.setGeneraAviso(false);
    }

    repository.findBySolicitudIdAndFechaAndTipoHitoId(solicitudHito.getSolicitudId(), solicitudHito.getFecha(),
        solicitudHito.getTipoHito().getId()).ifPresent((solicitudHitoExistente) -> {
          Assert.isTrue(solicitudHito.getId() == solicitudHitoExistente.getId(),
              "Ya existe un Hito con el mismo tipo en esa fecha");
        });

    solicitudRepository.findById(solicitudHito.getSolicitudId())
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudHito.getSolicitudId()));

    solicitudHito.setTipoHito(tipoHitoRepository.findById(solicitudHito.getTipoHito().getId())
        .orElseThrow(() -> new TipoHitoNotFoundException(solicitudHito.getTipoHito().getId())));

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudHito.getSolicitudId()), "No se puede modificar SolicitudHito");

    return repository.findById(solicitudHito.getId()).map((solicitudHitoExistente) -> {

      solicitudHitoExistente.setComentario(solicitudHito.getComentario());
      solicitudHitoExistente.setFecha(solicitudHito.getFecha());
      solicitudHitoExistente.setTipoHito(solicitudHito.getTipoHito());
      solicitudHitoExistente.setGeneraAviso(solicitudHito.getGeneraAviso());
      SolicitudHito returnValue = repository.save(solicitudHitoExistente);

      log.debug("update(SolicitudHito solicitudHito) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudHitoNotFoundException(solicitudHito.getId()));
  }

  /**
   * Obtiene una entidad {@link SolicitudHito} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudHito}.
   * @return SolicitudHito la entidad {@link SolicitudHito}.
   */
  @Override
  public SolicitudHito findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudHito returnValue = repository.findById(id).orElseThrow(() -> new SolicitudHitoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudHito}.
   *
   * @param id Id del {@link SolicitudHito}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "SolicitudHito id no puede ser null para eliminar un SolicitudHito");
    if (!repository.existsById(id)) {
      throw new SolicitudHitoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudHito} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link SolicitudHito} de la {@link Solicitud}
   *         paginadas.
   */
  @Override
  public Page<SolicitudHito> findAllBySolicitud(Long solicitudId, String query, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - start");

    Specification<SolicitudHito> specs = SolicitudHitoSpecifications.bySolicitudId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudHito> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

}