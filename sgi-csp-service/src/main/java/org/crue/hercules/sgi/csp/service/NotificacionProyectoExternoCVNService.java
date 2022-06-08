package org.crue.hercules.sgi.csp.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.NotificacionProyectoExternoCVNNotFoundException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.BaseEntity.Create;
import org.crue.hercules.sgi.csp.model.NotificacionCVNEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN.AsociarAutorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN.AsociarProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.NotificacionCVNEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.NotificacionProyectoExternoCVNRepository;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link NotificacionProyectoExternoCVN}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Validated
public class NotificacionProyectoExternoCVNService {

  private final NotificacionProyectoExternoCVNRepository repository;
  private final NotificacionCVNEntidadFinanciadoraRepository notificacionCVNEntidadFinanciadoraRepository;

  /**
   * Obtiene una entidad {@link NotificacionProyectoExternoCVN} por id.
   * 
   * @param id Identificador de la entidad {@link NotificacionProyectoExternoCVN}.
   * @return la entidad {@link NotificacionProyectoExternoCVN}.
   */
  public NotificacionProyectoExternoCVN findById(Long id) {
    log.debug("findById(Long id) - start");
    final NotificacionProyectoExternoCVN returnValue = repository.findById(id)
        .orElseThrow(() -> new NotificacionProyectoExternoCVNNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link NotificacionProyectoExternoCVN} paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link NotificacionProyectoExternoCVN}
   *         paginadas y/o filtradas.
   */
  public Page<NotificacionProyectoExternoCVN> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<NotificacionProyectoExternoCVN> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<NotificacionProyectoExternoCVN> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;

  }

  /**
   * Guarda la entidad {@link NotificacionProyectoExternoCVN}.
   *
   * @param notificacionProyectoExternoCVN     la entidad
   *                                           {@link NotificacionProyectoExternoCVN}
   *                                           a guardar.
   * @param notificacionesEntidadFinanciadoras lista de
   *                                           {@link NotificacionCVNEntidadFinanciadora}
   *                                           asociadas a la
   *                                           {@link NotificacionProyectoExternoCVN}.
   * @return proyecto la entidad {@link NotificacionProyectoExternoCVN}
   *         persistida.
   */
  @Transactional
  @Validated({ Create.class })
  public NotificacionProyectoExternoCVN create(@Valid NotificacionProyectoExternoCVN notificacionProyectoExternoCVN,
      List<NotificacionCVNEntidadFinanciadora> notificacionesEntidadFinanciadoras) {
    log.debug("create(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) - start");

    AssertHelper.idIsNull(notificacionProyectoExternoCVN.getId(), NotificacionProyectoExternoCVN.class);

    if (notificacionProyectoExternoCVN.getResponsableRef() != null) {
      notificacionProyectoExternoCVN.setDatosResponsable(null);
    }
    if (notificacionProyectoExternoCVN.getEntidadParticipacionRef() != null) {
      notificacionProyectoExternoCVN.setDatosEntidadParticipacion(null);
    }

    // Crea la notificacion proyecto externo CVN
    NotificacionProyectoExternoCVN returnValue = repository.save(notificacionProyectoExternoCVN);

    // Crea las NotificacionEntidadFinanciadora
    this.createEntidadesFianciadoras(returnValue.getId(), notificacionesEntidadFinanciadoras);

    log.debug("create(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) - end");
    return returnValue;
  }

  private void createEntidadesFianciadoras(Long notificacionProyectoExternoCvnId,
      List<NotificacionCVNEntidadFinanciadora> notificacionesEntidadFinanciadoras) {
    notificacionesEntidadFinanciadoras.stream().forEach(notificacionEntidadFinanciadora -> {
      if (notificacionEntidadFinanciadora.getEntidadFinanciadoraRef() != null) {
        notificacionEntidadFinanciadora.setDatosEntidadFinanciadora(null);
      }
      notificacionEntidadFinanciadora.setNotificacionProyectoExternoCvnId(notificacionProyectoExternoCvnId);
      notificacionCVNEntidadFinanciadoraRepository.save(notificacionEntidadFinanciadora);
    });
  }

  /**
   * Devuelve la {@link NotificacionProyectoExternoCVN} asociada a la
   * {@link Autorizacion}.
   * 
   * @param id id del {@link Autorizacion}.
   * @return el {@link NotificacionProyectoExternoCVN}.
   */
  public NotificacionProyectoExternoCVN findByAutorizacionId(Long id) {
    log.debug("findByAutorizacionId(Long id) - start");
    NotificacionProyectoExternoCVN returnValue = repository.findByAutorizacionId(id).orElse(null);
    log.debug("findByAutorizacionId(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba si existen datos vinculados a {@link Autorizacion} de
   * {@link NotificacionProyectoExternoCVN}
   *
   * @param autorizacionId Id del {@link Autorizacion}.
   * @return si existe o no el Autorizacion
   */
  public boolean existsByAutorizacionId(Long autorizacionId) {
    return repository.existsByAutorizacionId(autorizacionId);
  }

  /**
   * 
   * Recupera una lista de objetos {@link NotificacionProyectoExternoCVN} de un
   * {@link Proyecto}
   * 
   * @param proyectoId Identificador del {@link Proyecto}
   * @return lista de {@link NotificacionProyectoExternoCVN}
   */
  public List<NotificacionProyectoExternoCVN> findByProyectoId(Long proyectoId) {
    return this.repository.findByProyectoId(proyectoId);
  }

  /**
   * Actualiza la entidad {@link NotificacionProyectoExternoCVN} actualizando el
   * {@link Proyecto} asociado.
   *
   * @param notificacionProyectoExternoCVNActualizar la entidad
   *                                                 {@link NotificacionProyectoExternoCVN}
   *                                                 a guardar.
   * @return la entidad {@link NotificacionProyectoExternoCVN} persistida.
   */
  @Transactional
  @Validated({ AsociarAutorizacion.class })
  public NotificacionProyectoExternoCVN asociarAutorizacion(
      @Valid NotificacionProyectoExternoCVN notificacionProyectoExternoCVNActualizar) {
    log.debug("asociarAutorizacion(NotificacionProyectoExternoCVN notificacionProyectoExternoCVNActualizar - start");

    AssertHelper.idNotNull(notificacionProyectoExternoCVNActualizar.getId(), NotificacionProyectoExternoCVN.class);

    return repository.findById(notificacionProyectoExternoCVNActualizar.getId()).map(data -> {

      data.setAutorizacionId(notificacionProyectoExternoCVNActualizar.getAutorizacionId());

      NotificacionProyectoExternoCVN returnValue = repository.save(data);

      log.debug("asociarAutorizacion(NotificacionProyectoExternoCVN notificacionProyectoExternoCVNActualizar - end");
      return returnValue;
    }).orElseThrow(() -> new NotificacionProyectoExternoCVNNotFoundException(
        notificacionProyectoExternoCVNActualizar.getId()));
  }

  /**
   * Actualiza la entidad {@link NotificacionProyectoExternoCVN} actualizando el
   * {@link Proyecto} asociado.
   *
   * @param id Id de la {@link NotificacionProyectoExternoCVN}.
   * @return la entidad {@link NotificacionProyectoExternoCVN} persistida.
   */
  @Transactional
  public NotificacionProyectoExternoCVN desasociarAutorizacion(Long id) {
    log.debug("desasociarAutorizacion(Long id - start");

    AssertHelper.idNotNull(id, NotificacionProyectoExternoCVN.class);

    return repository.findById(id).map(data -> {

      data.setAutorizacionId(null);

      NotificacionProyectoExternoCVN returnValue = repository.save(data);

      log.debug("desasociarAutorizacion(Long id - end");
      return returnValue;
    }).orElseThrow(() -> new NotificacionProyectoExternoCVNNotFoundException(id));
  }

  /**
   * Actualiza la entidad {@link NotificacionProyectoExternoCVN} actualizando el
   * {@link Autorizacion} asociado.
   *
   * @param notificacionProyectoExternoCVNActualizar la entidad
   *                                                 {@link NotificacionProyectoExternoCVN}
   *                                                 a guardar.
   * @return la entidad {@link NotificacionProyectoExternoCVN} persistida.
   */
  @Transactional
  @Validated({ AsociarProyecto.class })
  public NotificacionProyectoExternoCVN asociarProyecto(
      @Valid NotificacionProyectoExternoCVN notificacionProyectoExternoCVNActualizar) {
    log.debug("asociarProyecto(NotificacionProyectoExternoCVN notificacionProyectoExternoCVNActualizar - start");

    AssertHelper.idNotNull(notificacionProyectoExternoCVNActualizar.getId(), NotificacionProyectoExternoCVN.class);

    return repository.findById(notificacionProyectoExternoCVNActualizar.getId()).map(data -> {

      data.setProyectoId(notificacionProyectoExternoCVNActualizar.getProyectoId());

      NotificacionProyectoExternoCVN returnValue = repository.save(data);

      log.debug("asociarProyecto(NotificacionProyectoExternoCVN notificacionProyectoExternoCVNActualizar - end");
      return returnValue;
    }).orElseThrow(() -> new NotificacionProyectoExternoCVNNotFoundException(
        notificacionProyectoExternoCVNActualizar.getId()));
  }

  /**
   * Actualiza la entidad {@link NotificacionProyectoExternoCVN} actualizando el
   * {@link Autorizacion} asociado.
   *
   * @param id Id de la {@link NotificacionProyectoExternoCVN}.
   * @return la entidad {@link NotificacionProyectoExternoCVN} persistida.
   */
  @Transactional
  public NotificacionProyectoExternoCVN desasociarProyecto(Long id) {
    log.debug("desasociarProyecto(Long id - start");

    AssertHelper.idNotNull(id, NotificacionProyectoExternoCVN.class);
    return repository.findById(id).map(data -> {

      data.setProyectoId(null);

      NotificacionProyectoExternoCVN returnValue = repository.save(data);

      log.debug("desasociarProyecto(Long id - end");
      return returnValue;
    }).orElseThrow(() -> new NotificacionProyectoExternoCVNNotFoundException(id));
  }

}
