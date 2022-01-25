package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.AutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.EstadoAutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessAutorizacionException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion.Estado;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.AutorizacionSpecifications;
import org.crue.hercules.sgi.csp.service.impl.AlreadyInEstadoAutorizacionException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Autorizacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class AutorizacionService {
  private final AutorizacionRepository repository;
  private final EstadoAutorizacionRepository estadoAutorizacionRepository;

  public AutorizacionService(AutorizacionRepository repository,
      EstadoAutorizacionRepository estadoAutorizacionRepository) {
    this.repository = repository;
    this.estadoAutorizacionRepository = estadoAutorizacionRepository;
  }

  /**
   * Guarda la entidad {@link Autorizacion}.
   * 
   * @param autorizacion la entidad {@link Autorizacion} a guardar.
   * @return Convocatoria la entidad {@link Autorizacion} persistida.
   */
  @Transactional
  public Autorizacion create(Autorizacion autorizacion) {
    log.debug("create(Autorizacion autorizacion) - start");

    Assert.isNull(autorizacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Autorizacion.class)).build());

    // Asigna al usuario actual como solicitante de la autorizacion
    autorizacion.setSolicitanteRef(getUserPersonaRef());

    // Crea la autorizacion
    repository.save(autorizacion);

    // Crea el estado inicial de la autorizacion
    EstadoAutorizacion estadoAutorizacion = addEstadoAutorizacion(autorizacion, EstadoAutorizacion.Estado.BORRADOR,
        null);

    autorizacion.setEstadoId(estadoAutorizacion.getId());

    Autorizacion returnValue = repository.save(autorizacion);

    log.debug("create(Autorizacion autorizacion) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Autorizacion}.
   *
   * @param autorizacionActualizar autorizacionActualizar {@link Autorizacion} con
   *                               los datos actualizados.
   * @return {@link Autorizacion} actualizado.
   */
  @Transactional
  public Autorizacion update(Autorizacion autorizacionActualizar) {
    log.debug("update(Autorizacion autorizacionActualizar- start");

    Assert.notNull(autorizacionActualizar.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Autorizacion.class)).build());

    return repository.findById(autorizacionActualizar.getId()).map(data -> {

      data.setId(autorizacionActualizar.getId());
      data.setObservaciones(autorizacionActualizar.getObservaciones());
      data.setResponsableRef(autorizacionActualizar.getResponsableRef());
      data.setTituloProyecto(autorizacionActualizar.getTituloProyecto());
      data.setEntidadRef(autorizacionActualizar.getEntidadRef());
      data.setHorasDedicacion(autorizacionActualizar.getHorasDedicacion());
      data.setDatosResponsable(autorizacionActualizar.getDatosResponsable());
      data.setDatosEntidad(autorizacionActualizar.getDatosEntidad());
      data.setDatosConvocatoria(autorizacionActualizar.getDatosConvocatoria());
      data.setConvocatoriaId(autorizacionActualizar.getConvocatoriaId());

      Autorizacion returnValue = repository.save(data);

      log.debug("update(Autorizacion autorizacionActualizar - end");
      return returnValue;
    }).orElseThrow(() -> new AutorizacionNotFoundException(autorizacionActualizar.getId()));
  }

  public Autorizacion findById(long id) {
    log.debug("findById(Long id) - start");
    final Autorizacion returnValue = repository.findById(id).orElseThrow(() -> new AutorizacionNotFoundException(id));
    checkUserHasAuthorityViewAutorizacion(returnValue);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  public Page<Autorizacion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<Autorizacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Autorizacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Autorizacion} que puede visualizar un
   * investigador paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Autorizacion} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  public Page<Autorizacion> findAllInvestigador(String query, Pageable paging) {
    log.debug("findAllInvestigador(String query, Pageable paging) - start");
    Specification<Autorizacion> specs = AutorizacionSpecifications.bySolicitante(getUserPersonaRef())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Autorizacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAllInvestigador(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link Autorizacion}.
   *
   * @param id Id del {@link Autorizacion}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Autorizacion.class)).build());

    final Autorizacion returnValue = repository.findById(id).orElseThrow(() -> new AutorizacionNotFoundException(id));
    checkUserHasAuthorityDeleteAutorizacion(returnValue);

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Se hace el cambio de estado de un Autorizacion.
   *
   * @param id                 Identificador de {@link Autorizacion}.
   * @param estadoAutorizacion el {@link EstadoAutorizacion} al que cambiar.
   * @return {@link Autorizacion} actualizado.
   */
  @Transactional
  public Autorizacion cambiarEstado(Long id, EstadoAutorizacion estadoAutorizacion) {

    log.debug("cambiarEstado(Long id, EstadoProyecto estadoProyecto) - start");

    Autorizacion autorizacion = repository.findById(id).orElseThrow(() -> new AutorizacionNotFoundException(id));
    checkUserHasAuthorityEditAutorizacion(autorizacion);

    estadoAutorizacion.setAutorizacionId(autorizacion.getId());

    Optional<EstadoAutorizacion> estadoActual = estadoAutorizacionRepository.findById(autorizacion.getEstadoId());

    // El nuevo estado es diferente al estado actual de del Autorizacion
    if (estadoAutorizacion.getEstado().equals(estadoActual.get().getEstado())) {
      throw new IllegalArgumentException("La Autorizacion ya se encuentra en el estado al que se quiere modificar.");
    }

    Instant fechaActual = Instant.now();

    // Se cambia el estado del proyecto
    estadoAutorizacion.setFecha(fechaActual);
    estadoAutorizacion = estadoAutorizacionRepository.save(estadoAutorizacion);
    autorizacion.setEstadoId(estadoAutorizacion.getId());

    Autorizacion returnValue = repository.save(autorizacion);

    log.debug("cambiarEstado(Long id, EstadoProyecto estadoProyecto) - end");
    return returnValue;
  }

  /**
   * Se hace el cambio de estado de un Autorizacion a presentada.
   *
   * @param id Identificador de {@link Autorizacion}.
   * @return {@link Autorizacion} actualizado.
   */
  @Transactional
  public Autorizacion presentar(Long id) {
    log.debug("presentar(Long id) - start");

    Autorizacion autorizacion = repository.findById(id).orElseThrow(() -> new AutorizacionNotFoundException(id));
    checkUserHasAuthorityEditAutorizacion(autorizacion);

    EstadoAutorizacion estadoAutorizacion = new EstadoAutorizacion();
    estadoAutorizacion.setAutorizacionId(autorizacion.getId());
    estadoAutorizacion.setEstado(Estado.PRESENTADA);

    EstadoAutorizacion estadoActual = estadoAutorizacionRepository.findById(autorizacion.getEstadoId())
        .orElseThrow(() -> new EstadoAutorizacionNotFoundException(autorizacion.getEstadoId()));

    // El nuevo estado es diferente al estado actual de del Autorizacion
    if (estadoAutorizacion.getEstado().equals(estadoActual.getEstado())) {
      throw new AlreadyInEstadoAutorizacionException();
    }
    Instant fechaActual = Instant.now();

    // Se cambia el estado del proyecto
    estadoAutorizacion.setFecha(fechaActual);
    estadoAutorizacion = estadoAutorizacionRepository.save(estadoAutorizacion);
    autorizacion.setEstadoId(estadoAutorizacion.getId());

    Autorizacion returnValue = repository.save(autorizacion);

    log.debug("presentar(Long id) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Autorizacion}
   * puede pasar a estado 'Presentada'.
   *
   * @param id Id del {@link Autorizacion}.
   * @return true si puede ser presentada / false si no puede ser presentada
   */
  public boolean presentable(Long id) {
    log.debug("presentable(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Autorizacion.class)).build());

    Autorizacion autorizacion = repository.findById(id)
        .orElseThrow(() -> new AutorizacionNotFoundException(id));
    checkUserHasAuthorityEditAutorizacion(autorizacion);

    EstadoAutorizacion estado = estadoAutorizacionRepository.findById(autorizacion.getEstadoId())
        .orElseThrow(() -> new EstadoAutorizacionNotFoundException(autorizacion.getEstadoId()));

    if (estado.getEstado() == Estado.BORRADOR) {
      return true;
    }
    log.debug("presentable(Long id) - end");
    return false;
  }

  private EstadoAutorizacion addEstadoAutorizacion(Autorizacion autorizacion,
      EstadoAutorizacion.Estado tipoEstadoAutorizacion,
      String comentario) {
    log.debug(
        "addEstadoAutorizacion(Autorizacion autorizacion, EstadoAutorizacion.Estado tipoEstadoAutorizacion,String comentario) - start");

    EstadoAutorizacion estadoAutorizacion = new EstadoAutorizacion();
    estadoAutorizacion.setEstado(tipoEstadoAutorizacion);
    estadoAutorizacion.setAutorizacionId(autorizacion.getId());
    estadoAutorizacion.setComentario(comentario);
    estadoAutorizacion.setFecha(Instant.now());

    EstadoAutorizacion returnValue = estadoAutorizacionRepository.save(estadoAutorizacion);

    log.debug(
        "addEstadoAutorizacion(Autorizacion autorizacion, EstadoAutorizacion.Estado tipoEstadoAutorizacion, String comentario) - end");
    return returnValue;
  }

  /**
   * Recupera el personaRef del usuario actual
   */
  private String getUserPersonaRef() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }

  private boolean hasUserAuthorityViewAndEditInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-AUT-INV-ER");
  }

  private boolean hasUserAuthorityDeleteInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-AUT-INV-BR");
  }

  private boolean hasUserAuthorityViewUnidadGestion() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-AUT-E") || SgiSecurityContextHolder
        .hasAuthorityForAnyUO("CSP-AUT-V");
  }

  private boolean hasUserAuthorityEditUnidadGestion() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-AUT-E");
  }

  private boolean hasUserAuthorityDeleteUnidadGestion() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-AUT-B");
  }

  /**
   * Comprueba si el usuario actual tiene permiso para ver la autorizacion
   * 
   * @param autorizacion la {@link Autorizacion}
   * 
   * @throws {@link UserNotAuthorizedToAccessAutorizacionException}
   */
  private void checkUserHasAuthorityViewAutorizacion(Autorizacion autorizacion) {
    if (!(hasUserAuthorityViewUnidadGestion()
        || (hasUserAuthorityViewAndEditInvestigador()
            && autorizacion.getSolicitanteRef().equals(getUserPersonaRef())))) {
      throw new UserNotAuthorizedToAccessAutorizacionException();
    }
  }

  /**
   * Comprueba si el usuario actual tiene permiso para editar la autorizacion
   * 
   * @param autorizacion la {@link Autorizacion}
   * 
   * @throws {@link UserNotAuthorizedToAccessAutorizacionException}
   */
  private void checkUserHasAuthorityEditAutorizacion(Autorizacion autorizacion) {
    if (!(hasUserAuthorityEditUnidadGestion()
        || (hasUserAuthorityViewAndEditInvestigador()
            && autorizacion.getSolicitanteRef().equals(getUserPersonaRef())))) {
      throw new UserNotAuthorizedToAccessAutorizacionException();
    }
  }

  /**
   * Comprueba si el usuario actual tiene permiso para eliminar la autorizacion
   * 
   * @param autorizacion la {@link Autorizacion}
   * 
   * @throws {@link UserNotAuthorizedToAccessAutorizacionException}
   */
  private void checkUserHasAuthorityDeleteAutorizacion(Autorizacion autorizacion) {
    if (!(hasUserAuthorityDeleteUnidadGestion()
        || (hasUserAuthorityDeleteInvestigador()
            && autorizacion.getSolicitanteRef().equals(getUserPersonaRef())))) {
      throw new UserNotAuthorizedToAccessAutorizacionException();
    }
  }

}