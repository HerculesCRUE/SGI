package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.repository.predicate.ConvocatoriaPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.AutorizacionSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaClonerService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Convocatoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaServiceImpl implements ConvocatoriaService {
  private static final String MSG_MODEL_CONVOCATORIA = "org.crue.hercules.sgi.csp.model.Convocatoria.message";
  private static final String MSG_KEY_UNIDAD_GESTION_REF = "unidadGestionRef";
  private static final String MSG_KEY_TITULO = "titulo";
  private static final String MSG_KEY_TRAMITACION_SGI = "configuracionSolicitud.tramitacionSgi";
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_KEY_UNIDAD_GESTION = "unidadGestion";
  private static final String MSG_FIELD_ACTION_ELIMINAR = "action.eliminar";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_FIELD_PRESENTACION_SGI = "presentacionSgi";
  private static final String MSG_PROBLEM_UNIDAD_GESTION_NO_GESTIONABLE = "org.springframework.util.Assert.entity.unidadGestion.noGestionable.message";
  private static final String MSG_MODEL_UNIDAD_GESTION = "org.crue.hercules.sgi.csp.model.UnidadGestion.message";
  private static final String MSG_MODEL_MODELO_UNIDAD = "org.crue.hercules.sgi.csp.model.ModeloUnidad.message";
  private static final String MSG_MODEL_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.message";
  private static final String MSG_MODEL_MODELO_TIPO_FINALIDAD = "org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad.message";
  private static final String MSG_MODEL_TIPO_FINALIDAD = "org.crue.hercules.sgi.csp.model.TipoFinalidad.message";
  private static final String MSG_MODEL_TIPO_REGIMEN_CONCURRENCIA = "org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia.message";
  private static final String MSG_MODEL_TIPO_AMBITO_GEOGRAFICO = "org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_INACTIVO = "org.springframework.util.Assert.entity.modeloEjecucion.inactivo.message";
  private static final String MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.exceptions.AssignModeloEjecucion.message";
  private static final String MSG_NO_DISPONIBLE_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.noDisponible.message";
  private static final String MSG_NO_DISPONIBLE_UNIDAD_GESTION = "org.crue.hercules.sgi.csp.exceptions.ModeloEjecucion.noDisponible.unidadGestion.message";
  private static final String MSG_ENTITY_EXISTS = "org.springframework.util.Assert.entity.exists.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA_PERMISOS = "org.springframework.util.Assert.accion.denegada.permisos.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA = "org.springframework.util.Assert.accion.denegada.message";

  private final ConvocatoriaRepository repository;
  private final ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  private final ModeloUnidadRepository modeloUnidadRepository;
  private final ModeloTipoFinalidadRepository modeloTipoFinalidadRepository;
  private final TipoRegimenConcurrenciaRepository tipoRegimenConcurrenciaRepository;
  private final TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository;
  private final ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final SolicitudRepository solicitudRepository;
  private final ProyectoRepository proyectoRepository;
  private final ConvocatoriaClonerService convocatoriaClonerService;
  private final AutorizacionRepository autorizacionRepository;
  private final ProgramaRepository programaRepository;
  private final ProyectoHelper proyectoHelper;
  private final SgiConfigProperties sgiConfigProperties;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaServiceImpl(ConvocatoriaRepository repository,
      ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository,
      ModeloUnidadRepository modeloUnidadRepository, ModeloTipoFinalidadRepository modeloTipoFinalidadRepository,
      TipoRegimenConcurrenciaRepository tipoRegimenConcurrenciaRepository,
      TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository,
      ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository, final SolicitudRepository solicitudRepository,
      final ProyectoRepository proyectoRepository, final ConvocatoriaClonerService convocatoriaClonerService,
      AutorizacionRepository autorizacionRepository,
      ProgramaRepository programaRepository,
      ProyectoHelper proyectoHelper,
      SgiConfigProperties sgiConfigProperties,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.convocatoriaPeriodoJustificacionRepository = convocatoriaPeriodoJustificacionRepository;
    this.modeloUnidadRepository = modeloUnidadRepository;
    this.modeloTipoFinalidadRepository = modeloTipoFinalidadRepository;
    this.tipoRegimenConcurrenciaRepository = tipoRegimenConcurrenciaRepository;
    this.tipoAmbitoGeograficoRepository = tipoAmbitoGeograficoRepository;
    this.convocatoriaPeriodoSeguimientoCientificoRepository = convocatoriaPeriodoSeguimientoCientificoRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.solicitudRepository = solicitudRepository;
    this.proyectoRepository = proyectoRepository;
    this.convocatoriaClonerService = convocatoriaClonerService;
    this.autorizacionRepository = autorizacionRepository;
    this.programaRepository = programaRepository;
    this.proyectoHelper = proyectoHelper;
    this.sgiConfigProperties = sgiConfigProperties;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guarda la entidad {@link Convocatoria}.
   * 
   * @param convocatoria la entidad {@link Convocatoria} a guardar.
   * @return Convocatoria la entidad {@link Convocatoria} persistida.
   */
  @Override
  @Transactional
  public Convocatoria create(Convocatoria convocatoria) {
    log.debug("create(Convocatoria convocatoria) - start");

    AssertHelper.idIsNull(convocatoria.getId(), Convocatoria.class);

    authorityHelper.checkUserHasAuthorityCreateConvocatoria(convocatoria);

    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setActivo(Boolean.TRUE);

    Convocatoria validConvocatoria = validarDatosConvocatoria(convocatoria, null);
    Convocatoria returnValue = repository.save(validConvocatoria);

    log.debug("create(Convocatoria convocatoria) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Convocatoria}.
   * 
   * @param convocatoria convocatoriaActualizar {@link Convocatoria} con los datos
   *                     actualizados.
   * @return {@link Convocatoria} actualizado.
   */
  @Override
  @Transactional
  public Convocatoria update(Convocatoria convocatoria) {
    log.debug("update(Convocatoria convocatoria) - start");

    AssertHelper.idNotNull(convocatoria.getId(), Convocatoria.class);
    AssertHelper.entityNotNull(convocatoria.getFormularioSolicitud(), Convocatoria.class, FormularioSolicitud.class);

    return repository.findById(convocatoria.getId()).map(data -> {
      authorityHelper.checkUserHasAuthorityModifyConvocatoria(data);
      Convocatoria validConvocatoria = validarDatosConvocatoria(convocatoria, data);

      data.setUnidadGestionRef(validConvocatoria.getUnidadGestionRef());
      data.setModeloEjecucion(validConvocatoria.getModeloEjecucion());
      data.setCodigo(validConvocatoria.getCodigo());
      data.setFechaPublicacion(validConvocatoria.getFechaPublicacion());
      data.setFechaProvisional(validConvocatoria.getFechaProvisional());
      data.setFechaConcesion(validConvocatoria.getFechaConcesion());
      data.setTitulo(validConvocatoria.getTitulo());
      data.setObjeto(validConvocatoria.getObjeto());
      data.setObservaciones(validConvocatoria.getObservaciones());
      data.setFinalidad(validConvocatoria.getFinalidad());
      data.setRegimenConcurrencia(validConvocatoria.getRegimenConcurrencia());
      data.setFormularioSolicitud(validConvocatoria.getFormularioSolicitud());
      data.setDuracion(validConvocatoria.getDuracion());
      data.setAmbitoGeografico(validConvocatoria.getAmbitoGeografico());
      data.setClasificacionCVN(validConvocatoria.getClasificacionCVN());
      data.setActivo(validConvocatoria.getActivo());
      data.setExcelencia(validConvocatoria.getExcelencia());
      data.setCodigoInterno(validConvocatoria.getCodigoInterno());
      data.setAnio(validConvocatoria.getAnio());

      Convocatoria returnValue = repository.save(validConvocatoria);

      log.debug("update(Convocatoria convocatoria) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoria.getId()));
  }

  /**
   * Registra una {@link Convocatoria} actualizando su estado de 'Borrador' a
   * 'Registrada'
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return Convocatoria {@link Convocatoria} actualizada.
   */
  @Override
  @Transactional
  public Convocatoria registrar(final Long id) {
    log.debug("registrar(Long id) - start");

    AssertHelper.idNotNull(id, Convocatoria.class);

    return repository.findById(id).map(data -> {

      Assert.isTrue(data.getEstado() == Convocatoria.Estado.BORRADOR,
          ApplicationContextSupport.getMessage("convocatoria.estado.borrador"));

      // Campos obligatorios en estado Registrada
      validarRequeridosConvocatoriaRegistrada(data);

      data.setEstado(Convocatoria.Estado.REGISTRADA);
      Convocatoria returnValue = repository.save(data);

      log.debug("registrar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaNotFoundException(id));
  }

  /**
   * Reactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  @Override
  @Transactional
  public Convocatoria enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, Convocatoria.class);

    return repository.findById(id).map(convocatoria -> {
      if (Boolean.TRUE.equals(convocatoria.getActivo())) {
        return convocatoria;
      }
      convocatoria.setActivo(Boolean.TRUE);
      Convocatoria returnValue = repository.save(convocatoria);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaNotFoundException(id));
  }

  /**
   * Desactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  @Override
  @Transactional
  public Convocatoria disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, Convocatoria.class);

    return repository.findById(id).map(convocatoria -> {
      if (Boolean.FALSE.equals(convocatoria.getActivo())) {
        return convocatoria;
      }

      // Comprobación de permisos para borrado lógico de convocatorias
      String authority = ConvocatoriaAuthorityHelper.CSP_CON_B;
      Assert.isTrue(!repository.isRegistradaConSolicitudesOProyectos(id),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_ELIMINAR))
              .build());

      Assert.isTrue(
          (SgiSecurityContextHolder.hasAuthorityForUO(authority, convocatoria.getUnidadGestionRef())),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_UNIDAD_GESTION_NO_GESTIONABLE)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_MODEL_CONVOCATORIA))
              .build());

      convocatoria.setActivo(Boolean.FALSE);
      Convocatoria returnValue = repository.save(convocatoria);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaNotFoundException(id));
  }

  /**
   * Comprueba si existen datos vinculados a la {@link Convocatoria} de
   * {@link TipoFase}, {@link TipoHito}, {@link TipoEnlace} y
   * {@link TipoDocumento}
   *
   * @param id Id del {@link Convocatoria}.
   * @return true existen datos vinculados/false no existen datos vinculados.
   */
  private boolean tieneVinculaciones(Long id) {
    log.debug("vinculaciones(Long id) - start");
    boolean returnValue = repository.tieneVinculaciones(id);
    log.debug("vinculaciones(Long id) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede ser modificada. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con la propia
   * {@link Convocatoria}.
   *
   * @param id                 Id del {@link Convocatoria}.
   * @param unidadConvocatoria unidadGestionRef {@link Convocatoria}.
   * @param authorities        Authorities a validar
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  @Override
  public boolean isRegistradaConSolicitudesOProyectos(Long id, String unidadConvocatoria, String[] authorities) {
    log.debug("isRegistradaConSolicitudesOProyectos(Long id, String unidadConvocatoria, String[] authorities) - start");

    if (StringUtils.isEmpty(unidadConvocatoria)) {
      unidadConvocatoria = repository.findById(id).map(Convocatoria::getUnidadGestionRef)
          .orElseThrow(() -> new ConvocatoriaNotFoundException(id));
    }

    if (SgiSecurityContextHolder.hasAnyAuthorityForUO(authorities, unidadConvocatoria)) {
      // Será modificable si no tiene solicitudes o proyectos asociados
      return !(repository.isRegistradaConSolicitudesOProyectos(id));
    }

    log.debug("isRegistradaConSolicitudesOProyectos(Long id, String unidadConvocatoria, String[] authorities) - end");
    return false;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede pasar a estado 'Registrada'.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true si puede ser registrada / false si no puede ser registrada
   */
  @Override
  public boolean registrable(Long id) {
    log.debug("registrable(Long id) - start");

    // si no tiene id no es registrable
    if (id == null) {
      log.debug("registrable(Long id) - end");
      return false;
    }

    Optional<Convocatoria> convocatoria = repository.findById(id);

    // convocatoria existe y su estado actual es 'Borrador'
    if (convocatoria.isPresent() && convocatoria.get().getEstado() == Convocatoria.Estado.BORRADOR
        && convocatoria.get().getUnidadGestionRef() != null && convocatoria.get().getModeloEjecucion() != null
        && convocatoria.get().getFinalidad() != null && convocatoria.get().getAmbitoGeografico() != null
        && convocatoria.get().getFormularioSolicitud() != null) {

      Optional<ConfiguracionSolicitud> configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(convocatoria.get().getId());

      // tiene configuración solicitud
      if (configuracionSolicitud.isPresent() && configuracionSolicitud.get().getTramitacionSGI() != null
          && !(configuracionSolicitud.get().getFasePresentacionSolicitudes() == null
              && configuracionSolicitud.get().getTramitacionSGI() == Boolean.TRUE)) {
        return true;
      }
    }
    log.debug("registrable(Long id) - end");
    return false;
  }

  /**
   * Comprueba la existencia del {@link Convocatoria} por id.
   *
   * @param id el id de la entidad {@link Convocatoria}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene una entidad {@link Convocatoria} por id.
   * 
   * @param id Identificador de la entidad {@link Convocatoria}.
   * @return Convocatoria la entidad {@link Convocatoria}.
   */
  @Override
  public Convocatoria findById(Long id) {
    log.debug("findById(Long id) - start");
    final Convocatoria returnValue = repository.findById(id).orElseThrow(() -> new ConvocatoriaNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewConvocatoria(id);

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Convocatoria} que puede visualizar un
   * investigador paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  @Override
  public Page<Convocatoria> findAllInvestigador(String query, Pageable paging) {
    log.debug("findAllInvestigador(String query, Pageable paging) - start");
    Specification<Convocatoria> specs = ConvocatoriaSpecifications.activos()
        .and(ConvocatoriaSpecifications.registradas())
        .and(ConvocatoriaSpecifications.configuracionSolicitudTramitacionSGI())
        .and(SgiRSQLJPASupport.toSpecification(query,
            ConvocatoriaPredicateResolver.getInstance(programaRepository, sgiConfigProperties)));

    Page<Convocatoria> returnValue = repository.findAll(specs, paging);
    log.debug("findAllInvestigador(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Convocatoria} que puede visualizar un
   * investigador paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  @Override
  public Page<Convocatoria> findAllPublicas(String query, Pageable paging) {
    log.debug("findAllPublicas(String query, Pageable paging) - start");
    Specification<Convocatoria> specs = ConvocatoriaSpecifications.publicas()
        .and(SgiRSQLJPASupport.toSpecification(query,
            ConvocatoriaPredicateResolver.getInstance(programaRepository, sgiConfigProperties)));

    Page<Convocatoria> returnValue = repository.findAll(specs, paging);
    log.debug("findAllPublicas(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Devuelve todas las convocatorias activas registradas que se encuentren dentro
   * de la unidad de gestión del usuario logueado.
   * 
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  @Override
  public Page<Convocatoria> findAllRestringidos(String query, Pageable paging) {
    log.debug("findAllRestringidos(String query, Pageable paging) - start");

    Specification<Convocatoria> specs = ConvocatoriaSpecifications.activos()
        .and(ConvocatoriaSpecifications.registradas())
        .and(SgiRSQLJPASupport.toSpecification(query,
            ConvocatoriaPredicateResolver.getInstance(programaRepository, sgiConfigProperties)));

    Page<Convocatoria> returnValue = repository.findAll(specs, paging);

    log.debug("findAllRestringidos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Devuelve todas las convocatorias activas que se encuentren dentro de la
   * unidad de gestión del usuario logueado.
   * 
   * @param query  información del filtro.
   * @param paging información de paginación.
   * 
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  @Override
  public Page<Convocatoria> findAllTodosRestringidos(String query, Pageable paging) {
    log.debug("findAllTodosRestringidos(String query, Pageable paging) - start");

    Specification<Convocatoria> specs = SgiRSQLJPASupport.toSpecification(query,
        ConvocatoriaPredicateResolver.getInstance(programaRepository, sgiConfigProperties));

    List<String> unidadesGestion = authorityHelper.getUserUOsConvocatoria();

    if (!CollectionUtils.isEmpty(unidadesGestion)) {
      Specification<Convocatoria> specByUnidadGestionRefIn = ConvocatoriaSpecifications.acronimosIn(unidadesGestion);
      specs = specs.and(specByUnidadGestionRefIn);
    }

    Page<Convocatoria> returnValue = repository.findAll(specs, paging);

    log.debug("findAllTodosRestringidos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede tramitarse.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true si puede ser tramitada / false si no puede ser tramitada
   */
  @Override
  public boolean tramitable(Long id) {
    Convocatoria convocatoria = repository.findById(id).orElseThrow(() -> new ConvocatoriaNotFoundException(id));

    ConfiguracionSolicitud datosConfiguracionSolicitud = configuracionSolicitudRepository.findByConvocatoriaId(id)
        .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(id));

    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    return convocatoria.getActivo() && datosConfiguracionSolicitud.getTramitacionSGI()
        && datosConfiguracionSolicitud.getFasePresentacionSolicitudes().getFechaInicio().isBefore(fechaActual)
        && datosConfiguracionSolicitud.getFasePresentacionSolicitudes().getFechaFin().isAfter(fechaActual);
  }

  /**
   * Devuelve la {@link Convocatoria} asociada a la {@link Solicitud} con el id
   * indicado si el usuario que realiza la peticion es el solicitante o el tutor
   * de la {@link Solicitud}.
   * 
   * @param solicitudId Identificador de {@link Solicitud}.
   * @return {@link Convocatoria} correspondiente a la {@link Solicitud}.
   */
  @Override
  public Convocatoria findBySolicitudIdAndUserIsSolicitanteOrTutor(Long solicitudId) {
    log.debug("findBySolicitudIdAndUserIsSolicitante(Long solicitudId) - start");

    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();

    if (solicitudRepository
        .count(SolicitudSpecifications.bySolicitanteOrTutor(personaRef)
            .and(SolicitudSpecifications.byId(solicitudId))) < 1) {
      throw new UserNotAuthorizedToAccessConvocatoriaException();
    }

    final Convocatoria returnValue = repository.findOne(ConvocatoriaSpecifications.bySolicitudId(solicitudId))
        .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitudId));

    log.debug("findBySolicitudIdAndUserIsSolicitante(Long solicitudId) - end");
    return returnValue;
  }

  /**
   * Devuelve la {@link Convocatoria} asociada a la {@link Autorizacion} con el id
   * indicado si el usuario que realiza la peticion es el solicitante de la
   * {@link Autorizacion}.
   * 
   * @param autorizacionId Identificador de {@link Autorizacion}.
   * @return {@link Convocatoria} correspondiente a la {@link Autorizacion}.
   */
  @Override
  public Convocatoria findByAutorizacionIdAndUserIsSolicitante(Long autorizacionId) {
    log.debug("findByAutorizacionIdAndUserIsSolicitante(Long autorizacionId) - start");

    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();

    if (autorizacionRepository
        .count(AutorizacionSpecifications.bySolicitante(personaRef).and(AutorizacionSpecifications.byId(
            autorizacionId))) < 1) {
      throw new UserNotAuthorizedToAccessConvocatoriaException();
    }

    final Convocatoria returnValue = repository.findOne(ConvocatoriaSpecifications.byAutorizacionId(autorizacionId))
        .orElseThrow(() -> new ConvocatoriaNotFoundException(autorizacionId));

    log.debug("findByAutorizacionIdAndUserIsSolicitante(Long autorizacionId) - end");
    return returnValue;
  }

  /**
   * Devuelve la {@link Convocatoria} asociada al {@link Proyecto} con el id
   * indicado si el usuario está logado con perfil investigador.
   * 
   * @param proyectoId Identificador de {@link Convocatoria}.
   * @return {@link Convocatoria}
   */
  @Override
  public Convocatoria findConvocatoriaByProyectoIdAndUserIsInvestigador(Long proyectoId) {
    log.debug("findByConvocatoriaIdAndUserIsInvestigador(Long convocatoriaId) - start");

    proyectoHelper.checkCanAccessProyecto(proyectoId);

    final Convocatoria returnValue = repository.findOne(ConvocatoriaSpecifications.byProyectoId(proyectoId))
        .orElseThrow(() -> new ConvocatoriaNotFoundException(proyectoId));

    log.debug("findByConvocatoriaIdAndUserIsInvestigador(Long proyectoId) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link FormularioSolicitud} de la {@link Convocatoria}
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return {@link FormularioSolicitud} correspondiente a la
   *         {@link Convocatoria}.
   */
  @Override
  public FormularioSolicitud findFormularioSolicitudById(Long id) {
    log.debug("findFormularioSolicitudById(Long id) - start");
    final Convocatoria returnValue = repository.findById(id).orElseThrow(() -> new ConvocatoriaNotFoundException(id));
    log.debug("findFormularioSolicitudById(Long id) - end");
    return returnValue.getFormularioSolicitud();
  }

  /**
   * Comprueba y valida los datos de una convocatoria.
   * 
   * @param datosConvocatoria
   * @param datosOriginales
   * @return convocatoria con los datos validados
   */
  private Convocatoria validarDatosConvocatoria(Convocatoria datosConvocatoria, Convocatoria datosOriginales) {
    log.debug("validarDatosConvocatoria(Convocatoria datosConvocatoria, Convocatoria datosOriginales) - start");

    // Campos obligatorios en estado Registrada
    if (datosConvocatoria.getEstado() == Convocatoria.Estado.REGISTRADA) {
      validarRequeridosConvocatoriaRegistrada(datosConvocatoria);
    } else {
      validarRequeridosConvocatoriaBorrador(datosConvocatoria);
    }

    // Permitir actualizar UnidadGestionRef
    if (datosOriginales != null
        && !datosConvocatoria.getUnidadGestionRef().equals(datosOriginales.getUnidadGestionRef())) {

      Assert.isTrue(!this.tieneVinculaciones(datosConvocatoria.getId()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA)
              .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                  MSG_MODEL_UNIDAD_GESTION))
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_MODEL_CONVOCATORIA))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
              .build());
    }

    // Permitir actualizar ModeloEjecucion
    // En caso de que se haya modificado el modelo de gestión y no la unidad se
    // comprobará si tiene vinculaciones, ya que si se ha cambiado la gestión se
    // comprueba anteriormente.
    if (datosOriginales != null
        && ((datosOriginales.getModeloEjecucion() == null && datosOriginales.getModeloEjecucion() != null)
            || (datosOriginales.getModeloEjecucion() != null && datosOriginales.getModeloEjecucion() == null)
            || (datosOriginales.getModeloEjecucion() != null && datosConvocatoria.getModeloEjecucion() != null
                && !datosConvocatoria.getModeloEjecucion().getId()
                    .equals(datosOriginales.getModeloEjecucion().getId())))
        && datosConvocatoria.getUnidadGestionRef().equals(datosOriginales.getUnidadGestionRef())) {

      Assert.isTrue(!this.tieneVinculaciones(datosConvocatoria.getId()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA)
              .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                  MSG_MODEL_UNIDAD_GESTION))
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_MODEL_CONVOCATORIA))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
              .build());
    }

    // ModeloEjecucion
    if (datosConvocatoria.getModeloEjecucion() != null) {

      AssertHelper.fieldNotNull(datosConvocatoria.getUnidadGestionRef(), Convocatoria.class,
          MSG_KEY_UNIDAD_GESTION_REF);

      Optional<ModeloUnidad> modeloUnidad = modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(
          datosConvocatoria.getModeloEjecucion().getId(), datosConvocatoria.getUnidadGestionRef());

      Assert.isTrue(modeloUnidad.isPresent(),
          () -> ProblemMessage.builder()
              .key(MSG_NO_DISPONIBLE_UNIDAD_GESTION)
              .parameter(MSG_KEY_MODELO, datosConvocatoria.getModeloEjecucion().getNombre())
              .parameter(MSG_KEY_UNIDAD_GESTION, datosConvocatoria.getUnidadGestionRef())
              .build());

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getModeloEjecucion() != null
          && (!Objects.equals(modeloUnidad.get().getModeloEjecucion().getId(),
              datosOriginales.getModeloEjecucion().getId())))) {
        Assert.isTrue(modeloUnidad.get().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_UNIDAD))
                .parameter(MSG_KEY_FIELD, modeloUnidad.get().getModeloEjecucion().getNombre())
                .build());

      }
      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getModeloEjecucion() != null
          && (!Objects.equals(modeloUnidad.get().getModeloEjecucion().getId(),
              datosOriginales.getModeloEjecucion().getId())))) {
        Assert.isTrue(modeloUnidad.get().getModeloEjecucion().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_EJECUCION))
                .parameter(MSG_KEY_FIELD, modeloUnidad.get().getModeloEjecucion().getNombre())
                .build());
      }
      datosConvocatoria.setModeloEjecucion(modeloUnidad.get().getModeloEjecucion());
    }

    // TipoFinalidad
    if (datosConvocatoria.getFinalidad() != null) {

      AssertHelper.entityNotNull(datosConvocatoria.getModeloEjecucion(), Convocatoria.class, ModeloEjecucion.class);

      Optional<ModeloTipoFinalidad> modeloTipoFinalidad = modeloTipoFinalidadRepository
          .findByModeloEjecucionIdAndTipoFinalidadId(datosConvocatoria.getModeloEjecucion().getId(),
              datosConvocatoria.getFinalidad().getId());

      Assert.isTrue(modeloTipoFinalidad.isPresent(),
          () -> ProblemMessage.builder()
              .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FINALIDAD))
              .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
              .parameter(MSG_KEY_MODELO, datosConvocatoria.getModeloEjecucion().getNombre())
              .build());

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getFinalidad() != null
          && (!Objects.equals(modeloTipoFinalidad.get().getTipoFinalidad().getId(),
              datosOriginales.getFinalidad().getId())))) {
        Assert.isTrue(modeloTipoFinalidad.get().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_MODELO_EJECUCION_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_FINALIDAD))
                .parameter(MSG_KEY_FIELD, modeloTipoFinalidad.get().getTipoFinalidad().getNombre())
                .parameter(MSG_KEY_MODELO, modeloTipoFinalidad.get().getModeloEjecucion().getNombre())
                .build());
      }

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getFinalidad() != null
          && (!Objects.equals(modeloTipoFinalidad.get().getTipoFinalidad().getId(),
              datosOriginales.getFinalidad().getId())))) {
        Assert.isTrue(modeloTipoFinalidad.get().getTipoFinalidad().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FINALIDAD))
                .parameter(MSG_KEY_FIELD, modeloTipoFinalidad.get().getTipoFinalidad().getNombre())
                .build());
      }
      datosConvocatoria.setFinalidad(modeloTipoFinalidad.get().getTipoFinalidad());
    }

    // TipoRegimenConcurrencia
    if (datosConvocatoria.getRegimenConcurrencia() != null) {
      if (datosConvocatoria.getRegimenConcurrencia().getId() == null) {
        datosConvocatoria.setRegimenConcurrencia(null);
      } else {

        Optional<TipoRegimenConcurrencia> tipoRegimenConcurrencia = tipoRegimenConcurrenciaRepository
            .findById(datosConvocatoria.getRegimenConcurrencia().getId());

        Assert.isTrue(tipoRegimenConcurrencia.isPresent(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_EXISTS)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_REGIMEN_CONCURRENCIA))
                .parameter(MSG_KEY_FIELD, datosConvocatoria.getRegimenConcurrencia().getNombre())
                .build());

        // Permitir no activos solo si estamos modificando y es el mismo
        if (datosOriginales == null || (datosOriginales.getRegimenConcurrencia() != null
            && (!tipoRegimenConcurrencia.get().getId().equals(datosOriginales.getRegimenConcurrencia().getId())))) {
          Assert.isTrue(tipoRegimenConcurrencia.get().getActivo(),
              () -> ProblemMessage.builder()
                  .key(MSG_ENTITY_INACTIVO)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_REGIMEN_CONCURRENCIA))
                  .parameter(MSG_KEY_FIELD, tipoRegimenConcurrencia.get().getNombre())
                  .build());
        }
        datosConvocatoria.setRegimenConcurrencia(tipoRegimenConcurrencia.get());
      }
    }

    // TipoAmbitoGeografico
    if (datosConvocatoria.getAmbitoGeografico() != null) {

      Optional<TipoAmbitoGeografico> tipoAmbitoGeografico = tipoAmbitoGeograficoRepository
          .findById(datosConvocatoria.getAmbitoGeografico().getId());

      AssertHelper.fieldNotNull(tipoAmbitoGeografico.isPresent(), Convocatoria.class, MSG_MODEL_TIPO_AMBITO_GEOGRAFICO);

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getAmbitoGeografico() != null
          && (!Objects.equals(tipoAmbitoGeografico.get().getId(), datosOriginales.getAmbitoGeografico().getId())))) {
        Assert.isTrue(tipoAmbitoGeografico.get().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_AMBITO_GEOGRAFICO))
                .parameter(MSG_KEY_FIELD, tipoAmbitoGeografico.get().getNombre())
                .build());
      }
      datosConvocatoria.setAmbitoGeografico(tipoAmbitoGeografico.get());
    }

    // Comprueba que la duracion no sea menor que el ultimo mes del ultimo
    // ConvocatoriaPeriodoJustificacion de la convocatoria
    if (datosConvocatoria.getId() != null && datosConvocatoria.getDuracion() != null
        && !Objects.equals(datosConvocatoria.getDuracion(), datosOriginales.getDuracion())) {
      convocatoriaPeriodoJustificacionRepository
          .findFirstByConvocatoriaIdOrderByNumPeriodoDesc(datosConvocatoria.getId())
          .ifPresent(convocatoriaPeriodoJustificacion -> Assert.isTrue(
              convocatoriaPeriodoJustificacion.getMesFinal() <= datosConvocatoria.getDuracion(),
              "Hay ConvocatoriaPeriodoJustificacion con mesFinal inferior a la nueva duracion"));
    }

    // Duración mayor que el mayor mes del Periodo Seguimiento Cientifico
    if (datosOriginales != null && datosConvocatoria.getDuracion() != null
        && (!datosConvocatoria.getDuracion().equals(datosOriginales.getDuracion()))) {
      List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientificos = convocatoriaPeriodoSeguimientoCientificoRepository
          .findAllByConvocatoriaIdOrderByMesInicial(datosConvocatoria.getId());
      if (!listaConvocatoriaPeriodoSeguimientoCientificos.isEmpty()) {
        Assert.isTrue(
            listaConvocatoriaPeriodoSeguimientoCientificos
                .get(listaConvocatoriaPeriodoSeguimientoCientificos.size() - 1)
                .getMesFinal() < datosConvocatoria.getDuracion(),
            "Existen periodos de seguimiento científico con una duración en meses superior a la indicada");
      }
    }

    log.debug("validarDatosConvocatoria(Convocatoria datosConvocatoria, Convocatoria datosOriginales) - end");

    return datosConvocatoria;
  }

  /**
   * Valida los campos generales requeridos para una convocatoria en estado
   * 'Registrada'
   * 
   * @param datosConvocatoria
   */
  private void validarRequeridosConvocatoriaRegistrada(Convocatoria datosConvocatoria) {
    log.debug("validarRequeridosConvocatoriaRegistrada(Convocatoria datosConvocatoria) - start");

    // ModeloUnidadGestion
    AssertHelper.fieldNotNull(datosConvocatoria.getUnidadGestionRef(), Convocatoria.class, MSG_KEY_UNIDAD_GESTION_REF);
    // ModeloEjecucion
    AssertHelper.entityNotNull(datosConvocatoria.getModeloEjecucion(), Convocatoria.class, ModeloEjecucion.class);
    // Titulo
    AssertHelper.fieldNotNull(datosConvocatoria.getTitulo(), Convocatoria.class, MSG_KEY_TITULO);
    // TipoFinalidad
    AssertHelper.entityNotNull(datosConvocatoria.getFinalidad(), Convocatoria.class, TipoFinalidad.class);
    // TipoAmbitoGeografico
    AssertHelper.entityNotNull(datosConvocatoria.getAmbitoGeografico(), Convocatoria.class, TipoAmbitoGeografico.class);
    // FormularioSolicitud
    AssertHelper.entityNotNull(datosConvocatoria.getFormularioSolicitud(), Convocatoria.class,
        FormularioSolicitud.class);

    // ConfiguracionSolicitud
    validarRequeridosConfiguracionSolicitudConvocatoriaRegistrada(datosConvocatoria);

    log.debug("validarRequeridosConvocatoriaRegistrada(Convocatoria datosConvocatoria) - end");
  }

  /**
   * Valida los campos generales requeridos para una convocatoria en estado
   * 'Borrador'
   * 
   * @param datosConvocatoria
   */
  private void validarRequeridosConvocatoriaBorrador(Convocatoria datosConvocatoria) {
    log.debug("validarRequeridosConvocatoriaBorrador(Convocatoria datosConvocatoria) - start");

    // ModeloUnidadGestion
    AssertHelper.fieldNotNull(datosConvocatoria.getUnidadGestionRef(), Convocatoria.class, MSG_KEY_UNIDAD_GESTION_REF);
    // Titulo
    AssertHelper.fieldNotNull(datosConvocatoria.getTitulo(), Convocatoria.class, MSG_KEY_TITULO);

    log.debug("validarRequeridosConvocatoriaBorrador(Convocatoria datosConvocatoria) - end");
  }

  /**
   * Valida los campos requeridos de la configuración solicitud para una
   * convocatoria en estado 'Registrada'
   * 
   * @param datosConvocatoria
   */
  private void validarRequeridosConfiguracionSolicitudConvocatoriaRegistrada(Convocatoria datosConvocatoria) {
    log.debug("validarRequeridosConfiguracionSolicitudConvocatoriaRegistrada(Convocatoria datosConvocatoria) - start");

    ConfiguracionSolicitud datosConfiguracionSolicitud = configuracionSolicitudRepository
        .findByConvocatoriaId(datosConvocatoria.getId())
        .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(datosConvocatoria.getId()));

    // Tramitacion SGI
    AssertHelper.fieldNotNull(datosConfiguracionSolicitud.getTramitacionSGI(), ConfiguracionSolicitud.class,
        MSG_KEY_TRAMITACION_SGI);
    // Convocatoria Fase
    AssertHelper.fieldNotNull(
        !(datosConfiguracionSolicitud.getFasePresentacionSolicitudes() == null
            && datosConfiguracionSolicitud.getTramitacionSGI() == Boolean.TRUE),
        ConvocatoriaFase.class, MSG_FIELD_PRESENTACION_SGI);

    log.debug("validarRequeridosConfiguracionSolicitudConvocatoriaRegistrada(Convocatoria datosConvocatoria) - end");
  }

  /**
   * Devuelve si tiene alguna {@link Solicitud} asociada
   * 
   * @param convocatoriaId id de la {@link Convocatoria}
   * @return true o false
   */
  @Override
  public boolean hasAnySolicitudReferenced(Long convocatoriaId) {
    return this.solicitudRepository.existsByConvocatoriaIdAndActivoTrue(convocatoriaId);
  }

  /**
   * Devuelve si tiene algún {@link Proyecto} asociado
   * 
   * @param convocatoriaId id de la {@link Convocatoria}
   * @return true o false
   */
  @Override
  public boolean hasAnyProyectoReferenced(Long convocatoriaId) {
    return this.proyectoRepository.existsByConvocatoriaIdAndActivoTrue(convocatoriaId);
  }

  /**
   * Clona una {@link Convocatoria} cuya fuente es la que corresponde con el id
   * pasado por parámetro
   * 
   * @param convocatoriaId Id de la convocatoria a clonar
   * @return un objeto de tipo {@link Convocatoria}
   */
  @Transactional
  @Override
  public Convocatoria clone(Long convocatoriaId) {

    Convocatoria toClone = repository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));

    Convocatoria cloned = this.repository.save(this.convocatoriaClonerService.cloneBasicConvocatoriaData(toClone));

    this.convocatoriaClonerService.cloneEntidadesGestoras(convocatoriaId, cloned.getId());

    this.convocatoriaClonerService.cloneConvocatoriaAreasTematicas(convocatoriaId, cloned);

    this.convocatoriaClonerService.cloneConvocatoriasEntidadesConvocantes(convocatoriaId, cloned.getId());

    this.convocatoriaClonerService.cloneConvocatoriasEntidadesFinanciadoras(convocatoriaId, cloned.getId());

    this.convocatoriaClonerService.clonePeriodosJustificacion(convocatoriaId, cloned.getId());

    this.convocatoriaClonerService.cloneConvocatoriaPeriodosSeguimientoCientifico(convocatoriaId, cloned.getId());

    this.convocatoriaClonerService.cloneRequisitoIP(convocatoriaId, cloned.getId());

    this.convocatoriaClonerService.cloneRequisitosEquipo(convocatoriaId, cloned.getId());

    this.convocatoriaClonerService.cloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc(convocatoriaId,
        cloned.getId());

    this.convocatoriaClonerService.clonePartidasPresupuestarias(convocatoriaId, cloned.getId());

    return cloned;
  }

  /**
   * Obtiene los ids de {@link Convocatoria} modificadas que no esten activas y
   * que
   * cumplan las condiciones indicadas en el filtro de búsqueda
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Convocatoria}.
   */
  @Override
  public List<Long> findIdsConvocatoriasEliminadas(String query) {
    log.debug("findIdsConvocatoriasEliminadas(String query) - start");

    Specification<Convocatoria> specs = ConvocatoriaSpecifications.notActivos()
        .and(SgiRSQLJPASupport.toSpecification(query,
            ConvocatoriaPredicateResolver.getInstance(programaRepository, sgiConfigProperties)));

    List<Long> returnValue = repository.findIds(specs);

    log.debug("findIdsConvocatoriasEliminadas(String query) - end");

    return returnValue;
  }

  /**
   * Obtiene los ids de {@link Convocatoria} modificadas que esten activos y que
   * cumplan las condiciones indicadas en el filtro de búsqueda
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Convocatoria}.
   */
  public List<Long> findIdsConvocatoriasModificadas(String query) {
    log.debug("findIdsConvocatoriasModificadas(String query) - start");

    Specification<Convocatoria> specs = ConvocatoriaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query,
            ConvocatoriaPredicateResolver.getInstance(programaRepository, sgiConfigProperties)));

    List<Long> returnValue = repository.findIds(specs);

    log.debug("findIdsConvocatoriasModificadas(String query) - end");

    return returnValue;
  }

}
