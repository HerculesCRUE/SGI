package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.AutorizacionWithFirstEstado;
import org.crue.hercules.sgi.csp.dto.DocumentoOutput;
import org.crue.hercules.sgi.csp.dto.EstadoAutorizacionOutput;
import org.crue.hercules.sgi.csp.exceptions.AlreadyInEstadoAutorizacionException;
import org.crue.hercules.sgi.csp.exceptions.AutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessAutorizacionException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion.Estado;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.predicate.AutorizacionPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.AutorizacionSpecifications;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
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
  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_ID = "id";

  /** Autorizacion repository */
  private final AutorizacionRepository repository;
  /** EstadoAutorizacion repository */
  private final EstadoAutorizacionRepository estadoAutorizacionRepository;
  /** Report service */
  private final SgiApiRepService reportService;
  /** SGDOC service */
  private final SgdocService sgdocService;
  /** SGI properties */
  SgiConfigProperties sgiConfigProperties;

  private static final String TITULO_INFORME_AUTORIZACION = "SGI_certificadoAutorizacionProyectoExterno_";

  public AutorizacionService(
      AutorizacionRepository repository,
      EstadoAutorizacionRepository estadoAutorizacionRepository,
      SgiApiRepService reportService,
      SgdocService sgdocService, SgiConfigProperties sgiConfigProperties) {
    this.repository = repository;
    this.estadoAutorizacionRepository = estadoAutorizacionRepository;
    this.reportService = reportService;
    this.sgdocService = sgdocService;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  /**
   * Guarda la entidad {@link Autorizacion}.
   * 
   * @param autorizacion la entidad {@link Autorizacion} a guardar.
   * @return la entidad {@link Autorizacion} persistida.
   */
  @Transactional
  public Autorizacion create(Autorizacion autorizacion) {
    log.debug("create(Autorizacion autorizacion) - start");

    Assert.isNull(autorizacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Autorizacion.class))
            .build());

    // Asigna al usuario actual como solicitante de la autorizacion
    autorizacion.setSolicitanteRef(getUserPersonaRef());

    // Crea la autorizacion
    repository.save(autorizacion);

    // Crea el estado inicial de la autorizacion
    EstadoAutorizacion estadoAutorizacion = addEstadoAutorizacion(autorizacion, EstadoAutorizacion.Estado.BORRADOR,
        null);

    autorizacion.setEstado(estadoAutorizacion);

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
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Autorizacion.class))
            .build());

    return repository.findById(autorizacionActualizar.getId()).map(data -> {

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

  /**
   * Obtiene una entidad {@link Autorizacion} por id.
   * 
   * @param id Identificador de la entidad {@link Autorizacion}.
   * @return la entidad {@link Autorizacion}.
   */
  public Autorizacion findById(Long id) {
    log.debug("findById(Long id) - start");
    final Autorizacion returnValue = repository.findById(id).orElseThrow(() -> new AutorizacionNotFoundException(id));
    checkUserHasAuthorityViewAutorizacion(returnValue);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Autorizacion} paginadas y/o filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link Autorizacion} paginadas y/o
   *         filtradas.
   */
  public Page<AutorizacionWithFirstEstado> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<Autorizacion> specs = null;
    if (query != null) {
      specs = SgiRSQLJPASupport.toSpecification(query);
    }
    Page<AutorizacionWithFirstEstado> returnValue = repository.findAllAutorizacionWithFirstEstado(specs, paging);
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
  public Page<AutorizacionWithFirstEstado> findAllInvestigador(String query, Pageable paging) {
    log.debug("findAllInvestigador(String query, Pageable paging) - start");
    Specification<Autorizacion> specs = AutorizacionSpecifications.bySolicitante(getUserPersonaRef())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AutorizacionWithFirstEstado> returnValue = repository.findAllAutorizacionWithFirstEstado(specs, paging);
    log.debug("findAllInvestigador(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Autorizacion} del solicitante indicado
   * que esten en estado autorizadas y sin ninguna notificacion asociada
   * 
   *
   * @param solicitanteRef referencia del solicitante
   * @param query          información del filtro.
   * @param paging         información de paginación.
   * @return el listado de entidades {@link Autorizacion} paginadas y filtradas.
   */
  public Page<Autorizacion> findAllAutorizadasWithoutNotificacionBySolicitanteRef(String solicitanteRef, String query,
      Pageable paging) {
    log.debug(
        "findAllAutorizadasWithoutNotificacionBySolicitanteRef(String solicitanteRef, String query, Pageable paging) - start");
    Specification<Autorizacion> specs = AutorizacionSpecifications.bySolicitante(solicitanteRef)
        .and(AutorizacionSpecifications.byEstado(Estado.AUTORIZADA))
        .and(AutorizacionSpecifications.withoutNotificacionProyectoExternoCVN())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Autorizacion> returnValue = repository.findAll(specs, paging);
    log.debug(
        "findAllAutorizadasWithoutNotificacionBySolicitanteRef(String solicitanteRef, String query, Pageable paging) - end");
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
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Autorizacion.class))
            .build());

    final Autorizacion returnValue = repository.findById(id).orElseThrow(() -> new AutorizacionNotFoundException(id));
    returnValue.setEstado(null);
    repository.save(returnValue);
    this.estadoAutorizacionRepository.deleteByAutorizacionId(id);
    checkUserHasAuthorityDeleteAutorizacion(returnValue);

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Se hace el cambio de estado de un Autorizacion.
   *
   * @param id                 Identificador de {@link Autorizacion}.
   * @param estadoAutorizacion el {@link EstadoAutorizacionOutput} al que cambiar.
   * @return {@link Autorizacion} actualizado.
   */
  @Transactional
  public Autorizacion cambiarEstado(Long id, EstadoAutorizacion estadoAutorizacion) {

    log.debug("cambiarEstado(Long id, EstadoProyecto estadoProyecto) - start");

    Autorizacion autorizacion = repository.findById(id).orElseThrow(() -> new AutorizacionNotFoundException(id));
    checkUserHasAuthorityEditAutorizacion(autorizacion);

    estadoAutorizacion.setAutorizacionId(autorizacion.getId());

    // El nuevo estado es diferente al estado actual de del Autorizacion
    if (estadoAutorizacion.getEstado().equals(autorizacion.getEstado().getEstado())) {
      throw new AlreadyInEstadoAutorizacionException();
    }

    if (estadoAutorizacion.getFecha() == null) {
      Instant fechaActual = Instant.now();
      estadoAutorizacion.setFecha(fechaActual);
    }

    // Se cambia el estado del proyecto
    estadoAutorizacion = estadoAutorizacionRepository.save(estadoAutorizacion);
    autorizacion.setEstado(estadoAutorizacion);

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

    // El nuevo estado es diferente al estado actual de del Autorizacion
    if (estadoAutorizacion.getEstado().equals(autorizacion.getEstado().getEstado())) {
      throw new AlreadyInEstadoAutorizacionException();
    }
    Instant fechaActual = Instant.now();

    // Se cambia el estado del proyecto
    estadoAutorizacion.setFecha(fechaActual);
    estadoAutorizacion = estadoAutorizacionRepository.save(estadoAutorizacion);
    autorizacion.setEstado(estadoAutorizacion);

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
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Autorizacion.class))
            .build());

    Autorizacion autorizacion = repository.findById(id)
        .orElseThrow(() -> new AutorizacionNotFoundException(id));

    checkUserHasAuthorityViewAutorizacion(autorizacion);

    if (autorizacion.getEstado().getEstado() == Estado.BORRADOR) {
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

  /**
   * Obtiene todos los ids de {@link Autorizacion} que cumplan las condiciones
   * indicadas en la query.
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Autorizacion}.
   */
  public List<Long> findIds(String query) {
    log.debug("findIds(String query) - start");

    List<Long> returnValue = repository.findIds(SgiRSQLJPASupport.toSpecification(query,
        AutorizacionPredicateResolver.getInstance()));

    log.debug("findIds(String query) - end");

    return returnValue;
  }

  /**
   * Obtiene el informe de una {@link Autorizacion}
   * 
   * @param idAutorizacion identificador {@link Autorizacion}
   * @return El documento del informe de la {@link Autorizacion}
   */
  public DocumentoOutput generarDocumentoAutorizacion(Long idAutorizacion) {
    Resource informePdf = reportService.getInformeAutorizacion(idAutorizacion);
    // Se sube el informe a sgdoc
    String pattern = "yyyyMMddHH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
        .withZone(sgiConfigProperties.getTimeZone().toZoneId()).withLocale(LocaleContextHolder.getLocale());
    String fileName = TITULO_INFORME_AUTORIZACION + idAutorizacion + formatter.format(Instant.now()) + ".pdf";
    return sgdocService.uploadInforme(fileName, informePdf);
  }
}
