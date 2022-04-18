package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.eti.ChecklistOutput;
import org.crue.hercules.sgi.csp.dto.eti.EquipoTrabajo;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacion;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacion.EstadoFinanciacion;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ColaborativoWithoutCoordinadorExternoException;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.EstadoSolicitudNotUpdatedException;
import org.crue.hercules.sgi.csp.exceptions.MissingInvestigadorPrincipalInSolicitudProyectoEquipoException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoWithoutSocioCoordinadorException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudWithoutRequeridedDocumentationException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToChangeEstadoSolicitudException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifySolicitudException;
import org.crue.hercules.sgi.csp.exceptions.eti.GetPeticionEvaluacionException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud.Estado;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPresupuestoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.predicate.SolicitudPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.DocumentoRequeridoSolicitudSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiEtiService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Solicitud}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SolicitudService {

  private final SgiConfigProperties sgiConfigProperties;
  private final SgiApiEtiService sgiApiEtiService;
  private final SolicitudRepository repository;
  private final EstadoSolicitudRepository estadoSolicitudRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final ProyectoRepository proyectoRepository;
  private final DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;
  private final SolicitudDocumentoRepository solicitudDocumentoRepository;
  private final SolicitudProyectoRepository solicitudProyectoRepository;
  private final SolicitudProyectoEquipoRepository solicitudProyectoEquipoRepository;
  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;
  private final SolicitudProyectoPresupuestoRepository solicitudProyectoPresupuestoRepository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;
  private final ConvocatoriaEnlaceRepository convocatoriaEnlaceRepository;
  private final ComunicadosService comunicadosService;
  private final SgiApiSgpService personasService;
  private final SolicitudAuthorityHelper solicitudAuthorityHelper;

  public SolicitudService(SgiConfigProperties sgiConfigProperties,
      SgiApiEtiService sgiApiEtiService, SolicitudRepository repository,
      EstadoSolicitudRepository estadoSolicitudRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository, ProyectoRepository proyectoRepository,
      SolicitudProyectoRepository solicitudProyectoRepository,
      DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository,
      SolicitudDocumentoRepository solicitudDocumentoRepository,
      SolicitudProyectoEquipoRepository solicitudProyectoEquipoRepository,
      SolicitudProyectoSocioRepository solicitudProyectoSocioRepository,
      SolicitudProyectoPresupuestoRepository solicitudProyectoPresupuestoRepository,
      ConvocatoriaRepository convocatoriaRepository,
      ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository,
      ConvocatoriaEnlaceRepository convocatoriaEnlaceRepository,
      ComunicadosService comunicadosService,
      SgiApiSgpService personasService,
      SolicitudAuthorityHelper solicitudAuthorityHelper) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.sgiApiEtiService = sgiApiEtiService;
    this.repository = repository;
    this.estadoSolicitudRepository = estadoSolicitudRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.proyectoRepository = proyectoRepository;
    this.documentoRequeridoSolicitudRepository = documentoRequeridoSolicitudRepository;
    this.solicitudDocumentoRepository = solicitudDocumentoRepository;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
    this.solicitudProyectoEquipoRepository = solicitudProyectoEquipoRepository;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
    this.solicitudProyectoPresupuestoRepository = solicitudProyectoPresupuestoRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.convocatoriaEntidadFinanciadoraRepository = convocatoriaEntidadFinanciadoraRepository;
    this.convocatoriaEnlaceRepository = convocatoriaEnlaceRepository;
    this.comunicadosService = comunicadosService;
    this.personasService = personasService;
    this.solicitudAuthorityHelper = solicitudAuthorityHelper;
  }

  /**
   * Guarda la entidad {@link Solicitud}.
   * 
   * @param solicitud la entidad {@link Solicitud} a guardar.
   * @return solicitud la entidad {@link Solicitud} persistida.
   */
  @Transactional
  public Solicitud create(Solicitud solicitud) {
    log.debug("create(Solicitud solicitud) - start");

    Assert.isNull(solicitud.getId(), "Solicitud id tiene que ser null para crear una Solicitud");
    Assert.notNull(solicitud.getCreadorRef(), "CreadorRef no puede ser null para crear una Solicitud");

    Assert.isTrue((solicitud.getConvocatoriaId() != null) || solicitud.getConvocatoriaExterna() != null,
        "Convocatoria o Convocatoria externa tienen que ser distinto de null para crear una Solicitud");

    String authority = "CSP-SOL-C";
    if (solicitud.getConvocatoriaId() != null) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(solicitud.getConvocatoriaId())
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(solicitud.getConvocatoriaId()));

      Convocatoria convocatoria = convocatoriaRepository.findById(configuracionSolicitud.getConvocatoriaId())
          .orElseThrow(() -> new ConvocatoriaNotFoundException(configuracionSolicitud.getConvocatoriaId()));

      Assert.isTrue(
          SgiSecurityContextHolder.hasAuthority("CSP-SOL-INV-C") || (SgiSecurityContextHolder.hasAuthority(authority)
              || SgiSecurityContextHolder.hasAuthorityForUO(authority, convocatoria.getUnidadGestionRef())),
          "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

      solicitud.setUnidadGestionRef(convocatoria.getUnidadGestionRef());
    } else {
      Assert.isTrue(
          SgiSecurityContextHolder.hasAuthority(authority)
              || SgiSecurityContextHolder.hasAuthorityForUO(authority, solicitud.getUnidadGestionRef()),
          "La Unidad de Gestión no es gestionable por el usuario");
    }

    solicitud.setActivo(Boolean.TRUE);

    // Crea la solicitud
    repository.save(solicitud);

    // Crea el estado inicial de la solicitud
    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.BORRADOR, null);

    // Actualiza la el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);
    solicitud.setCodigoRegistroInterno(generateCodigoRegistroInterno(solicitud.getId()));
    Solicitud returnValue = repository.save(solicitud);

    log.debug("create(Solicitud solicitud) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Solicitud}.
   * 
   * @param solicitud solicitudActualizar {@link Solicitud} con los datos
   *                  actualizados.
   * 
   * @return {@link Solicitud} actualizado.
   */
  @Transactional
  public Solicitud update(Solicitud solicitud) {
    log.debug("update(Solicitud solicitud) - start");

    Assert.notNull(solicitud.getId(), "Id no puede ser null para actualizar Solicitud");

    Assert.notNull(solicitud.getSolicitanteRef(), "El solicitante no puede ser null para actualizar Solicitud");

    Assert.isTrue(
        solicitud.getConvocatoriaId() != null
            || (solicitud.getConvocatoriaExterna() != null && !solicitud.getConvocatoriaExterna().isEmpty()),
        "Se debe seleccionar una convocatoria del SGI o convocatoria externa para actualizar Solicitud");

    // comprobar si la solicitud es modificable
    Assert.isTrue(modificable(solicitud.getId()), "No se puede modificar la Solicitud");

    return repository.findById(solicitud.getId()).map(data -> {

      Assert.isTrue(solicitud.getActivo(), "Solicitud tiene que estar activo para actualizarse");

      Assert.isTrue(solicitudAuthorityHelper.hasPermisosEdicion(solicitud),
          "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

      data.setSolicitanteRef(solicitud.getSolicitanteRef());
      data.setCodigoExterno(solicitud.getCodigoExterno());
      data.setObservaciones(solicitud.getObservaciones());
      data.setTitulo(solicitud.getTitulo());

      if (null == data.getConvocatoriaId()) {
        data.setConvocatoriaExterna(solicitud.getConvocatoriaExterna());
        if (data.getEstado().getEstado() == EstadoSolicitud.Estado.BORRADOR) {
          data.setCodigoExterno(solicitud.getCodigoExterno());
        }
      }

      Solicitud returnValue = repository.save(data);

      log.debug("update(Solicitud solicitud) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudNotFoundException(solicitud.getId()));
  }

  /**
   * Reactiva el {@link Solicitud}.
   *
   * @param id Id del {@link Solicitud}.
   * @return la entidad {@link Solicitud} persistida.
   */
  @Transactional
  public Solicitud enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "Solicitud id no puede ser null para reactivar un Solicitud");

    return repository.findById(id).map(solicitud -> {

      Assert.isTrue(SgiSecurityContextHolder.hasAuthorityForUO("CSP-SOL-R", solicitud.getUnidadGestionRef()),
          "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (Boolean.TRUE.equals(solicitud.getActivo())) {
        // Si esta activo no se hace nada
        return solicitud;
      }

      solicitud.setActivo(true);

      Solicitud returnValue = repository.save(solicitud);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudNotFoundException(id));
  }

  /**
   * Desactiva el {@link Solicitud}.
   *
   * @param id Id del {@link Solicitud}.
   * @return la entidad {@link Solicitud} persistida.
   */
  @Transactional
  public Solicitud disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Solicitud id no puede ser null para desactivar un Solicitud");

    return repository.findById(id).map(solicitud -> {
      String authorityInv = "CSP-SOL-INV-BR";
      boolean hasAuthorityInv = SgiSecurityContextHolder.hasAuthority(authorityInv);

      if (hasAuthorityInv) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.isTrue(solicitud.getCreadorRef().equals(authentication.getName()),
            "El usuario no es el creador de la Solicitud");
      } else {
        String authority = "CSP-SOL-B";
        Assert.isTrue(
            SgiSecurityContextHolder.hasAuthority(authority)
                || SgiSecurityContextHolder.hasAuthorityForUO(authority, solicitud.getUnidadGestionRef()),
            "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");
      }

      if (Boolean.FALSE.equals(solicitud.getActivo())) {
        // Si no esta activo no se hace nada
        return solicitud;
      }

      solicitud.setActivo(false);

      Solicitud returnValue = repository.save(solicitud);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link Solicitud} por id.
   * 
   * @param id Identificador de la entidad {@link Solicitud}.
   * @return Solicitud la entidad {@link Solicitud}.
   */
  public Solicitud findById(Long id) {
    log.debug("findById(Long id) - start");
    final Solicitud returnValue = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    solicitudAuthorityHelper.checkUserHasAuthorityViewSolicitud(returnValue);

    String authorityVisualizar = "CSP-SOL-V";

    Assert
        .isTrue(
            SgiSecurityContextHolder.hasAuthority("CSP-SOL-INV-C")
                || solicitudAuthorityHelper.hasPermisosEdicion(returnValue)
                || (SgiSecurityContextHolder.hasAuthority(authorityVisualizar) || SgiSecurityContextHolder
                    .hasAuthorityForUO(authorityVisualizar, returnValue.getUnidadGestionRef())),
            "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Solicitud} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Solicitud} activas paginadas y
   *         filtradas.
   */
  public Page<Solicitud> findAllRestringidos(String query, Pageable paging) {
    log.debug("findAllRestringidos(String query, Pageable paging) - start");

    Specification<Solicitud> specs = SolicitudSpecifications.distinct().and(SolicitudSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query, SolicitudPredicateResolver.getInstance())));

    Page<Solicitud> returnValue = repository.findAll(specs, paging);
    log.debug("findAllRestringidos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Solicitud} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Solicitud} paginadas y filtradas.
   */
  public Page<Solicitud> findAllTodosRestringidos(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Solicitud> specs = SolicitudSpecifications.distinct()
        .and(SgiRSQLJPASupport.toSpecification(query, SolicitudPredicateResolver.getInstance()));

    List<String> unidadesGestion = SgiSecurityContextHolder.getUOsForAnyAuthority(
        new String[] { "CSP-SOL-E", "CSP-SOL-V", "CSP-SOL-B", "CSP-SOL-C", "CSP-SOL-R", "CSP-PRO-C" });

    if (!CollectionUtils.isEmpty(unidadesGestion)) {
      Specification<Solicitud> specByUnidadGestionRefIn = SolicitudSpecifications.unidadGestionRefIn(unidadesGestion);
      specs = specs.and(specByUnidadGestionRefIn);
    }

    Page<Solicitud> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Solicitud} que puede visualizar un
   * investigador paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Solicitud} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  public Page<Solicitud> findAllInvestigador(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Specification<Solicitud> specs = SolicitudSpecifications.activos()
        .and(SolicitudSpecifications.bySolicitante(authentication.getName()))
        .and(SgiRSQLJPASupport.toSpecification(query, SolicitudPredicateResolver.getInstance()));

    Page<Solicitud> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Comprueba si la soliciutd está asociada a una convocatoria SGI.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return indicador de si se encuentra asociado o no la solicitud a una
   *         convocatoria SGI
   */
  public boolean hasConvocatoriaSgi(Long id) {
    log.debug("hasConvocatoriaSgi(Long id) - start");
    Assert.notNull(id, "Solicitud id no puede ser null para comprobar su convocatoria");

    return repository.findById(id).map(solicitud -> {

      log.debug("hasConvocatoriaSgi(Long id) - end");
      return solicitud.getConvocatoriaId() != null;
    }).orElseThrow(() -> new SolicitudNotFoundException(id));
  }

  /**
   * Se hace el cambio de estado de "Borrador" a "Presentada".
   * 
   * @param id              Identificador de {@link Solicitud}.
   * @param estadoSolicitud {@link EstadoSolicitud}
   * @return {@link Solicitud} actualizado.
   */
  @Transactional
  public Solicitud cambiarEstado(Long id, @Valid EstadoSolicitud estadoSolicitud) {
    log.debug("cambiarEstado(Long id, EstadoSolicitud estadoSolicitud) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    estadoSolicitud.setSolicitudId(solicitud.getId());

    // VALIDACIONES

    // Permisos
    if (!solicitudAuthorityHelper.hasPermisosEdicion(solicitud)) {
      throw new UserNotAuthorizedToModifySolicitudException();
    }

    // El nuevo estado es diferente al estado actual de la solicitud
    if (estadoSolicitud.getEstado().equals(solicitud.getEstado().getEstado())) {
      throw new EstadoSolicitudNotUpdatedException();
    }

    // En caso de pasar de cualquier estado a cualquier otro estado que no sea
    // Desistida ni Renunciada se deben realizar validaciones
    if ((!estadoSolicitud.getEstado().equals(EstadoSolicitud.Estado.DESISTIDA)
        && !estadoSolicitud.getEstado().equals(EstadoSolicitud.Estado.RENUNCIADA))) {
      validateCambioNoDesistidaRenunciada(solicitud);
    }

    if (solicitudAuthorityHelper.isUserInvestigador()) {
      validateCambioEstadoInvestigador(solicitud.getEstado().getEstado(), estadoSolicitud.getEstado());
    }

    // Se cambia el estado de la solicitud
    // Actualiza el estado actual de la solicitud con el nuevo estado
    estadoSolicitud = estadoSolicitudRepository.save(estadoSolicitud);
    solicitud.setEstado(estadoSolicitud);

    // Cuando la solicitud cambie de estado (de cualquier estado a otro que no sea
    // Renunciada o Desistida)
    if (!estadoSolicitud.getEstado().equals(EstadoSolicitud.Estado.DESISTIDA)
        && !estadoSolicitud.getEstado().equals(EstadoSolicitud.Estado.RENUNCIADA)) {
      SolicitudProyecto solicitudProyecto = solicitudProyectoRepository.findById(solicitud.getId()).orElse(null);
      if (solicitudProyecto != null) {
        String idChecklist = solicitudProyecto.getChecklistRef();
        String peticionEvaluacionRef = solicitudProyecto.getPeticionEvaluacionRef();
        // Si se ha rellenado el checklist de ética
        if (idChecklist != null) {
          // Y "peticionEvaluacionRef" tenga valor null (no se ha creado todavía la
          // petición de evaluación en ética)
          if (peticionEvaluacionRef == null) {
            ChecklistOutput checklistOutput = sgiApiEtiService.getCheckList(idChecklist);
            // En el caso que que en la Pestaña de Autoevaluación ética exista una respuesta
            // afirmativa a una sola de las preguntas del formulario
            if (checklistOutput != null && checklistOutput.getRespuesta() != null
                && checklistOutput.getRespuesta().contains("true")) {
              // Se creará un registro en la tabla "PeticionEvaluacion" del módulo de ética
              PeticionEvaluacion peticionEvaluacionRequest = PeticionEvaluacion.builder()
                  .solicitudConvocatoriaRef(solicitud.getId().toString()).checklistId(checklistOutput.getId())
                  .personaRef(solicitud.getSolicitanteRef()).titulo(solicitud.getTitulo())
                  // Si hay entidades financiadoras (registros en la tabla "Convocatoria
                  // Entidad Financiadora" de la convocatoria asociada a la solicitud) valor "Sí",
                  // en otro caso valor "No"
                  .existeFinanciacion(isEntidadFinanciadora(solicitud))
                  // Se concatenará el campo "nombre" de la entidad de los registros que
                  // existan en la tabla "Convocatoria Entidad Financiadora" de la convocatoria
                  // asociada a la solicitud (en caso de que la solicitud tenga asociada una
                  // convocatoria, sino se quedará vacío el campo). Los nombre de las entidades
                  // financiadoras se separarán por ","
                  .fuenteFinanciacion(getFuentesFinanciacion(solicitud))
                  .estadoFinanciacion(EstadoFinanciacion.SOLICITADO)
                  // La suma de los importes de los conceptos de gastos de todas las
                  // entidades financiadoras de la convocatoria (suma del campo "importeConcedido"
                  // de los registros de la tabla "SolicitudProyectoPresupuesto" cuyo campo
                  // finanicacionAjena = false)
                  .importeFinanciacion(getImporteAutoFinanciacion(solicitud))
                  .resumen(solicitudProyecto.getResultadosPrevistos()).objetivos(solicitudProyecto.getObjetivos())
                  .build();

              // Guardar el PeticionEvaluacion.id
              PeticionEvaluacion peticionEvaluacion = sgiApiEtiService.newPeticionEvaluacion(
                  peticionEvaluacionRequest);
              if (peticionEvaluacion != null) {
                solicitudProyecto.setPeticionEvaluacionRef(String.valueOf(peticionEvaluacion.getId()));
                solicitudProyecto = solicitudProyectoRepository.save(solicitudProyecto);

                // Copiamos el equipo de trabajo de una solicitud (personaRef
                // SolicitudProyectoEquipo)
                // a una petición de evalaución (personaRef EquipoTrabajo)
                copyMiembrosEquipoSolicitudToPeticionEvaluacion(peticionEvaluacion,
                    solicitudProyecto.getId());

                try {
                  // Enviamos el comunicado de alta de solicitud de petición evaluación de ética
                  this.comunicadosService.enviarComunicadoSolicitudAltaPeticionEvaluacionEti(
                      peticionEvaluacion.getCodigo(), solicitud.getCodigoRegistroInterno(),
                      solicitud.getSolicitanteRef());
                } catch (Exception e) {
                  log.debug(
                      "Error enviarComunicadoSolicitudAltaPeticionEvaluacionEti(String codigoPeticionEvaluacion, String codigoSolicitud, String solicitanteRef) -  codigoPeticionEvaluacion: "
                          + peticionEvaluacion
                              .getCodigo()
                          + ", codigoSolicitud: " + solicitud.getCodigoRegistroInterno() + ", solicitanteRef: "
                          + solicitud.getSolicitanteRef(),
                      e);
                }
              } else {
                // throw exception
                throw new GetPeticionEvaluacionException();
              }
            } else {
              // Do nothing
            }
          } else {
            // Si ya se había creado la petición de evaluación en ética
            switch (estadoSolicitud.getEstado()) {
              case DENEGADA:
                // Se debe recuperar la petición de ética y cambiar el valor del
                // campo "estadoFinanciacion" a "Denegado"
                PeticionEvaluacion peticionEvaluacionDenegada = sgiApiEtiService
                    .getPeticionEvaluacion(peticionEvaluacionRef);
                if (peticionEvaluacionDenegada != null) {
                  peticionEvaluacionDenegada.setEstadoFinanciacion(EstadoFinanciacion.DENEGADO);

                  sgiApiEtiService
                      .updatePeticionEvaluacion(peticionEvaluacionRef, peticionEvaluacionDenegada);
                } else {
                  // throw exception
                  throw new GetPeticionEvaluacionException();
                }
                break;
              case CONCEDIDA_PROVISIONAL:
              case CONCEDIDA_PROVISIONAL_ALEGADA:
              case CONCEDIDA_PROVISIONAL_NO_ALEGADA:
              case CONCEDIDA:
                // Se debe recuperar la petición de ética y cambiar el valor del
                // campo "estadoFinanciacion" a "Concedido"
                PeticionEvaluacion peticionEvaluacionConcedida = sgiApiEtiService
                    .getPeticionEvaluacion(peticionEvaluacionRef);
                if (peticionEvaluacionConcedida != null) {
                  peticionEvaluacionConcedida.setEstadoFinanciacion(EstadoFinanciacion.CONCEDIDO);

                  sgiApiEtiService
                      .updatePeticionEvaluacion(peticionEvaluacionRef, peticionEvaluacionConcedida);
                } else {
                  // throw exception
                  throw new GetPeticionEvaluacionException();
                }
                break;
              default:
                // Do nothing
                break;
            }
          }
        }
      }
    }
    Solicitud returnValue = repository.save(solicitud);
    enviarComunicadosCambioEstado(solicitud, estadoSolicitud);
    log.debug("cambiarEstado(Long id, EstadoSolicitud estadoSolicitud) - end");
    return returnValue;
  }

  /**
   * Obtiene el código de registro interno de la {@link Solicitud} por id.
   * 
   * @param id Identificador de la entidad {@link Solicitud}.
   * @return Código de registro interno de la entidad {@link Solicitud}.
   */
  public String getCodigoRegistroInterno(Long id) {
    log.debug("getCodigoRegistroInterno(Long id) - start");
    final String returnValue = repository.findById(id).map(Solicitud::getCodigoRegistroInterno)
        .orElseThrow(() -> new SolicitudNotFoundException(id));
    log.debug("getCodigoRegistroInterno(Long id) - end");
    return returnValue;
  }

  /**
   * Copia todos los miembros del equipo de una {@link Solicitud} a un Equipo d
   * Trabajo de una Petición de Evaluación Ética
   *
   * @param proyecto entidad {@link Proyecto}
   */
  private void copyMiembrosEquipoSolicitudToPeticionEvaluacion(PeticionEvaluacion peticionEvaluacion,
      Long solicitudProyectoId) {

    log.debug(
        "copyMiembrosEquipoSolicitudToPeticionEvaluacion(PeticionEvaluacion peticionEvaluacion, Long solicitudProyectoId) - start");

    solicitudProyectoEquipoRepository.findAllBySolicitudProyectoId(solicitudProyectoId).stream()
        .map(solicitudProyectoEquipo -> {
          log.debug("Copy SolicitudProyectoEquipo with id: {}", solicitudProyectoEquipo.getId());
          EquipoTrabajo.EquipoTrabajoBuilder equipoTrabajo = EquipoTrabajo.builder();
          equipoTrabajo.peticionEvaluacion(peticionEvaluacion);
          equipoTrabajo.personaRef(solicitudProyectoEquipo.getPersonaRef());
          return equipoTrabajo.build();
        })
        .distinct()
        .forEach(equipoTrabajo -> sgiApiEtiService.newEquipoTrabajo(peticionEvaluacion.getId(), equipoTrabajo));
    log.debug(
        "copyMiembrosEquipoSolicitudToPeticionEvaluacion(PeticionEvaluacion peticionEvaluacion, Long solicitudProyectoId) - end");
  }

  /**
   * Comprueba si la solicitud tiene la asociada la documentación requerida en la
   * configuración de la solicitud de la convocatoria.
   * 
   * @param idSolicitud    Identificador de {@link Solicitud}.
   * @param idConvocatoria Identificador de {@link Convocatoria}.
   * @return <code>true</code> En caso de tener asociada la documentación;
   *         <code>false</code>Documentación no asociada.
   */
  private boolean hasDocumentacionRequerida(Long idSolicitud, Long idConvocatoria) {
    log.debug("hasDocumentacionRequerida(Long idConvocatoria) - start");

    Specification<DocumentoRequeridoSolicitud> specByConvocatoria = DocumentoRequeridoSolicitudSpecifications
        .byConvocatoriaId(idConvocatoria);
    Specification<DocumentoRequeridoSolicitud> specs = Specification.where(specByConvocatoria);

    List<DocumentoRequeridoSolicitud> documentosRequeridosSolicitud = documentoRequeridoSolicitudRepository
        .findAll(specs);

    if (CollectionUtils.isEmpty(documentosRequeridosSolicitud)) {
      return true;
    }

    List<Long> tiposDocumentoRequeridosSolicitud = documentosRequeridosSolicitud.stream()
        .map(documentoRequerido -> documentoRequerido.getTipoDocumento().getId()).collect(Collectors.toList());

    List<SolicitudDocumento> solicitudDocumentos = solicitudDocumentoRepository
        .findAllByTipoDocumentoIdInAndSolicitudId(tiposDocumentoRequeridosSolicitud, idSolicitud);

    log.debug("hasDocumentacionRequerida(Long idConvocatoria) - end");

    return !CollectionUtils.isEmpty(solicitudDocumentos);
  }

  /**
   * Comprueba si el solicitante es miembro del equipo.
   * 
   * @param idSolicitudProyecto Identificador de {@link SolicitudProyecto}.
   * @param solicitanteRef      Solicitante ref.
   * @return <code>true</code> El solicitante es miembro del equipo;
   *         <code>false</code>No pertenece al equipo.
   */
  private boolean isSolicitanteMiembroEquipo(Long idSolicitudProyecto, String solicitanteRef) {
    // El solicitante debe pertenecer al equipo
    List<SolicitudProyectoEquipo> solicitudProyectoEquipos = solicitudProyectoEquipoRepository
        .findAllBySolicitudProyectoIdAndPersonaRef(idSolicitudProyecto, solicitanteRef);

    return !CollectionUtils.isEmpty(solicitudProyectoEquipos);
  }

  /**
   * Genera el codigo de registro interno de la solicitud
   * 
   * @param solicitudId
   * @return
   */
  private String generateCodigoRegistroInterno(Long solicitudId) {
    log.debug("generateCodigoRegistroInterno(Long solicitudId) - start");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    String codigoRegistroInterno = "SGI_SLC" + solicitudId
        + formatter.format(Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()));

    log.debug("generateCodigoRegistroInterno(Long solicitudId) - end");
    return codigoRegistroInterno;
  }

  /**
   * Añade el nuevo {@link EstadoSolicitud} y actualiza la {@link Solicitud} con
   * dicho estado.
   * 
   * @param solicitud la {@link Solicitud} para la que se añade el nuevo estado.
   * @param estado    El nuevo {@link EstadoSolicitud.Estado} de la
   *                  {@link Solicitud}.
   * @return la {@link Solicitud} con el estado actualizado.
   */
  private EstadoSolicitud addEstadoSolicitud(Solicitud solicitud, EstadoSolicitud.Estado estado, String comentario) {
    log.debug(
        "addEstadoSolicitud(Solicitud solicitud, TipoEstadoSolicitudEnum tipoEstadoSolicitud, String comentario) - start");

    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setEstado(estado);
    estadoSolicitud.setSolicitudId(solicitud.getId());
    estadoSolicitud.setComentario(comentario);
    estadoSolicitud.setFechaEstado(Instant.now());

    EstadoSolicitud returnValue = estadoSolicitudRepository.save(estadoSolicitud);

    log.debug(
        "addEstadoSolicitud(Solicitud solicitud, TipoEstadoSolicitudEnum tipoEstadoSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si se puede crear un
   * {@link Proyecto} a partir de la {@link Solicitud}
   * 
   * @param id Id de la {@link Solicitud}.
   * @return true si se permite la creación / false si no se permite creación
   */
  public boolean isPosibleCrearProyecto(Long id) {

    final Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));
    // Si la solicitud no está en estado CONCEDIDA no se puede crear el proyecto a
    // partir de la misma
    if (!solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL_ALEGADA)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL_NO_ALEGADA)) {
      return false;
    }

    // Si el formulario de la solicitud no es de tipo PROYECTO no se podrá crear el
    // proyecto a partir de ella
    if (solicitud.getFormularioSolicitud() != FormularioSolicitud.PROYECTO) {
      return false;
    }

    // Si no hay datos del proyecto en la solicitud, no se podrá crear el proyecto
    return solicitudProyectoRepository.existsById(solicitud.getId());
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con la propia
   * {@link Solicitud}.
   *
   * @param id Id del {@link Solicitud}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  public boolean modificable(Long id) {
    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    if (!solicitudAuthorityHelper.hasPermisosEdicion(solicitud)) {
      return false;
    }

    if (solicitudAuthorityHelper.isUserInvestigador()) {
      return modificableByInvestigador(solicitud);
    } else {
      return modificableByUnidadGestion(solicitud);
    }
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada para cambiar el estado y añadir
   * nuevos documentos.
   *
   * @param id Id del {@link Solicitud}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  public boolean modificableEstadoAndDocumentos(Long id) {
    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    if (!solicitudAuthorityHelper.hasPermisosEdicion(solicitud)) {
      return false;
    }

    if (solicitudAuthorityHelper.isUserInvestigador()) {
      return modificableEstadoAndDocumentosByInvestigador(solicitud);
    } else {
      return modificableByUnidadGestion(solicitud);
    }
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada para cambiar el estado y añadir
   * nuevos documentos.
   *
   * @param id Id del {@link Solicitud}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  public boolean modificableEstadoAndDocumentosByInvestigador(Long id) {
    return modificableEstadoAndDocumentosByInvestigador(
        repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id)));
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada para cambiar el estado y añadir
   * nuevos documentos.
   *
   * @param solicitud La {@link Solicitud}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  private boolean modificableEstadoAndDocumentosByInvestigador(Solicitud solicitud) {
    if (!solicitudAuthorityHelper.hasPermisosEdicion(solicitud)) {
      return false;
    }

    return Arrays.asList(
        Estado.BORRADOR,
        Estado.SUBSANACION,
        Estado.EXCLUIDA_PROVISIONAL,
        Estado.EXCLUIDA_DEFINITIVA,
        Estado.DENEGADA_PROVISIONAL,
        Estado.DENEGADA).contains(solicitud.getEstado().getEstado());
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada por un usuario investigador.
   * No es modificable cuando el estado de la {@link Solicitud} es distinto de
   * {@link EstadoSolicitud.Estado#BORRADOR}
   *
   * @param solicitud Id del {@link Solicitud}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  private boolean modificableByInvestigador(Solicitud solicitud) {
    return solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.BORRADOR);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada por un usuario de la unidad de gestion.
   * No es modificable cuando no esta activa ni cuando tiene
   * {@link Proyecto} asociados.
   *
   * @param solicitud Id del {@link Solicitud}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  private boolean modificableByUnidadGestion(Solicitud solicitud) {
    boolean hasProyectosAsociados = proyectoRepository.existsBySolicitudId(solicitud.getId());

    return Boolean.TRUE.equals(solicitud.getActivo()) && !hasProyectosAsociados;
  }

  /**
   * Comprueba si se cumplen todas las condiciones para que la solicitud pueda
   * pasar de cualquier estado a cualquier otro estado que no sea Desistida ni
   * Renunciada
   * 
   * @param solicitud Solicitud a comprobar.
   * @return <code>true</code> Cumple condiciones para el cambio de estado.
   *         <code>false</code>No cumple condiciones.
   */
  private void validateCambioNoDesistidaRenunciada(Solicitud solicitud) {
    log.debug("validateCambioNoDesistidaRenunciada(Solicitud solicitud) - start");

    if (solicitud.getConvocatoriaId() != null
        && !hasDocumentacionRequerida(solicitud.getId(), solicitud.getConvocatoriaId())) {
      throw new SolicitudWithoutRequeridedDocumentationException();
    }

    // Si el formulario es de tipo Estándar
    if (solicitud.getFormularioSolicitud() == FormularioSolicitud.PROYECTO) {

      SolicitudProyecto solicitudProyecto = solicitudProyectoRepository.findById(solicitud.getId()).orElse(null);
      if (solicitudProyecto == null) {
        throw new SolicitudProyectoNotFoundException(solicitud.getId());
      }

      if (!isSolicitanteMiembroEquipo(solicitudProyecto.getId(), solicitud.getSolicitanteRef())) {
        throw new MissingInvestigadorPrincipalInSolicitudProyectoEquipoException();
      }

      if (!solicitudAuthorityHelper.hasAuthorityEditInvestigador()) {
        // En caso de sea colaborativo y no tenga coordinador externo
        if (Boolean.TRUE.equals(solicitudProyecto.getColaborativo())
            && solicitudProyecto.getCoordinadorExterno() == null) {
          throw new ColaborativoWithoutCoordinadorExternoException();
        }

        if (solicitudProyecto.getColaborativo() && solicitudProyecto.getCoordinadorExterno()) {
          List<SolicitudProyectoSocio> solicitudProyectoSocios = solicitudProyectoSocioRepository
              .findAllBySolicitudProyectoIdAndRolSocioCoordinadorTrue(solicitudProyecto.getId());

          if (CollectionUtils.isEmpty(solicitudProyectoSocios)) {
            throw new SolicitudProyectoWithoutSocioCoordinadorException();
          }
        }
      }

    }
    log.debug("validateCambioNoDesistidaRenunciada(Solicitud solicitud) - end");
  }

  /**
   * Comprueba si el cambio de estado esta entre los permitidos para el
   * investigador
   * 
   * @param estadoActual Estado actual de la {@link Solicitud}
   * @param nuevoEstado  Nuevo estado al que se quiere cambiar la
   *                     {@link Solicitud}
   * 
   * @throws {@link UserNotAuthorizedToChangeEstadoSolicitudException} si el
   *                cambio de estado no esta permitido
   */
  private void validateCambioEstadoInvestigador(Estado estadoActual, Estado nuevoEstado) {
    boolean isCambioEstadoValido;
    switch (estadoActual) {
      case BORRADOR:
        isCambioEstadoValido = nuevoEstado.equals(Estado.SOLICITADA) || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case SUBSANACION:
        isCambioEstadoValido = nuevoEstado.equals(Estado.PRESENTADA_SUBSANACION)
            || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case EXCLUIDA_PROVISIONAL:
        isCambioEstadoValido = nuevoEstado.equals(Estado.ALEGACION_FASE_ADMISION)
            || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case EXCLUIDA_DEFINITIVA:
        isCambioEstadoValido = nuevoEstado.equals(Estado.RECURSO_FASE_ADMISION) || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case DENEGADA_PROVISIONAL:
        isCambioEstadoValido = nuevoEstado.equals(Estado.ALEGACION_FASE_PROVISIONAL)
            || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case DENEGADA:
        isCambioEstadoValido = nuevoEstado.equals(Estado.RECURSO_FASE_CONCESION)
            || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      default:
        isCambioEstadoValido = false;
    }

    if (!isCambioEstadoValido) {
      throw new UserNotAuthorizedToChangeEstadoSolicitudException(estadoActual, nuevoEstado);
    }
  }

  private boolean isEntidadFinanciadora(Solicitud solicitud) {
    // Si hay entidades financiadoras (registros en la tabla "Convocatoria
    // Entidad Financiadora" de la convocatoria asociada a la solicitud) valor "Sí",
    // en otro caso valor "No"
    Long convocatoriaId = solicitud.getConvocatoriaId();
    if (convocatoriaId == null) {
      // fast-return
      return false;
    }
    return convocatoriaEntidadFinanciadoraRepository.existsByConvocatoriaId(convocatoriaId);
  }

  private String getFuentesFinanciacion(Solicitud solicitud) {
    // Se concatenará el campo "nombre" de la entidad de los registros que
    // existan en la tabla "Convocatoria Entidad Financiadora" de la convocatoria
    // asociada a la solicitud (en caso de que la solicitud tenga asociada una
    // convocatoria, sino se quedará vacío el campo). Los nombre de las entidades
    // financiadoras se separarán por ","
    Long convocatoriaId = solicitud.getConvocatoriaId();
    if (convocatoriaId == null) {
      // fast-return
      return null;
    }
    List<ConvocatoriaEntidadFinanciadora> entidadesFinanciadoras = convocatoriaEntidadFinanciadoraRepository
        .findByConvocatoriaId(convocatoriaId);
    return entidadesFinanciadoras.stream()
        .map(entidadFinanciadora -> entidadFinanciadora.getFuenteFinanciacion().getNombre())
        .collect(Collectors.joining(", "));
  }

  private BigDecimal getImporteAutoFinanciacion(Solicitud solicitud) {
    // La suma de los importes de los conceptos de gastos de todas las
    // entidades financiadoras de la convocatoria (suma del campo "importeConcedido"
    // de los registros de la tabla "SolicitudProyectoPresupuesto" cuyo campo
    // finanicacionAjena = false)
    return solicitudProyectoPresupuestoRepository
        .sumImporteSolicitadoBySolicitudIdAndFinanciacionAjenaIsFalse(solicitud.getId());
  }

  private void enviarComunicadosCambioEstado(Solicitud solicitud, EstadoSolicitud estadoSolicitud) {
    log.debug("enviarComunicadosCambioEstado(Solicitud solicitud, EstadoSolicitud estadoSolicitud) - start");
    try {
      switch (estadoSolicitud.getEstado()) {
        case SOLICITADA:
          /*
           * Enviamos el comunicado de Cambio al estado SOLICITADA en solicitudes de
           * CONVOCATORIAS PROPIAS registradas por el propio por solicitante
           */
          if (checkConvocatoriaPropia(solicitud.getSolicitanteRef())
              && checkConvocatoriaTramitable(solicitud.getConvocatoriaId())) {
            Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));
            this.comunicadosService.enviarComunicadoSolicitudCambioEstadoSolicitada(
                getNombreApellidosByPersonaRef(solicitud.getSolicitanteRef()),
                solicitud.getUnidadGestionRef(), convocatoria.getTitulo(), convocatoria.getFechaPublicacion(),
                solicitud.getEstado().getFechaEstado());
          }
          break;
        case PRESENTADA_SUBSANACION:
        case ALEGACION_FASE_ADMISION:
        case RECURSO_FASE_ADMISION:
        case ALEGACION_FASE_PROVISIONAL:
        case RECURSO_FASE_CONCESION:
          /*
           * Enviamos el comunicado de Cambio de estado a PRESENTACIÓN DE ALEGACIONES en
           * solicitudes de CONVOCATORIAS PROPIAS registradas por el propio por
           * solicitante
           */
          if (checkConvocatoriaPropia(solicitud.getSolicitanteRef())
              && checkConvocatoriaTramitable(solicitud.getConvocatoriaId())) {
            Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));
            this.comunicadosService.enviarComunicadoSolicitudCambioEstadoAlegaciones(
                getNombreApellidosByPersonaRef(solicitud.getSolicitanteRef()),
                solicitud.getUnidadGestionRef(), convocatoria.getTitulo(), solicitud.getCodigoRegistroInterno(),
                solicitud.getEstado().getFechaEstado(), convocatoria.getFechaProvisional());
          }
          break;
        case EXCLUIDA_PROVISIONAL:
          /*
           * Enviamos el comunicado de Cambio al estado EXCLUIDA PROVISIONAL en
           * solicitudes de
           * CONVOCATORIAS PROPIAS registradas por el propio por solicitante
           */
          if (checkConvocatoriaTramitable(solicitud.getConvocatoriaId())) {
            Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));
            List<ConvocatoriaEnlace> enlaces = convocatoriaEnlaceRepository
                .findByConvocatoriaId(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaEnlaceNotFoundException(solicitud.getConvocatoriaId()));
            this.comunicadosService.enviarComunicadoSolicitudCambioEstadoExclProv(solicitud.getSolicitanteRef(),
                convocatoria.getTitulo(),
                convocatoria.getFechaProvisional(),
                enlaces);
          }
          break;
        case EXCLUIDA_DEFINITIVA:
          /*
           * Enviamos el comunicado de Cambio al estado EXCLUIDA DEFINITIVA en
           * solicitudes de
           * CONVOCATORIAS PROPIAS registradas por el propio por solicitante
           */
          if (checkConvocatoriaTramitable(solicitud.getConvocatoriaId())) {
            Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));
            List<ConvocatoriaEnlace> enlaces = convocatoriaEnlaceRepository
                .findByConvocatoriaId(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaEnlaceNotFoundException(solicitud.getConvocatoriaId()));
            this.comunicadosService.enviarComunicadoSolicitudCambioEstadoExclDef(solicitud.getSolicitanteRef(),
                convocatoria.getTitulo(),
                convocatoria.getFechaConcesion(),
                enlaces);
          }
          break;
        case CONCEDIDA_PROVISIONAL:
          /*
           * Enviamos el comunicado de Cambio al estado CONCEDIDA PROVISIONAL en
           * solicitudes de
           * CONVOCATORIAS PROPIAS registradas por el propio por solicitante
           */

          if (checkConvocatoriaTramitable(solicitud.getConvocatoriaId())) {
            Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));

            List<ConvocatoriaEnlace> enlaces = convocatoriaEnlaceRepository
                .findByConvocatoriaId(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaEnlaceNotFoundException(solicitud.getConvocatoriaId()));
            this.comunicadosService.enviarComunicadoSolicitudCambioEstadoConcProv(
                solicitud.getSolicitanteRef(),
                convocatoria.getTitulo(),
                convocatoria.getFechaProvisional(),
                enlaces);
          }
          break;
        case DENEGADA_PROVISIONAL:
          /*
           * Enviamos el comunicado de Cambio al estado DENEGADA PROVISIONAL en
           * solicitudes de CONVOCATORIAS PROPIAS registradas por el propio por
           * solicitante
           */
          if (checkConvocatoriaTramitable(solicitud.getConvocatoriaId())) {
            Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));

            List<ConvocatoriaEnlace> enlaces = convocatoriaEnlaceRepository
                .findByConvocatoriaId(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaEnlaceNotFoundException(solicitud.getConvocatoriaId()));
            this.comunicadosService.enviarComunicadoSolicitudCambioEstadoDenProv(
                solicitud.getSolicitanteRef(),
                convocatoria.getTitulo(),
                convocatoria.getFechaProvisional(),
                enlaces);
          }
          break;
        case CONCEDIDA:
          /*
           * Enviamos el comunicado de Cambio al estado CONCEDIDA en
           * solicitudes de
           * CONVOCATORIAS PROPIAS registradas por el propio por solicitante
           */
          if (checkConvocatoriaTramitable(solicitud.getConvocatoriaId())) {
            Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));
            List<ConvocatoriaEnlace> enlaces = convocatoriaEnlaceRepository
                .findByConvocatoriaId(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaEnlaceNotFoundException(solicitud.getConvocatoriaId()));
            this.comunicadosService.enviarComunicadoSolicitudCambioEstadoConc(solicitud.getSolicitanteRef(),
                convocatoria.getTitulo(),
                convocatoria.getFechaConcesion(),
                enlaces);
          }
          break;
        case DENEGADA:
          /*
           * Enviamos el comunicado de Cambio al estado DENEGADA en
           * solicitudes de
           * CONVOCATORIAS PROPIAS registradas por el propio por solicitante
           */
          if (checkConvocatoriaTramitable(solicitud.getConvocatoriaId())) {
            Convocatoria convocatoria = convocatoriaRepository.findById(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaNotFoundException(solicitud.getConvocatoriaId()));
            List<ConvocatoriaEnlace> enlaces = convocatoriaEnlaceRepository
                .findByConvocatoriaId(solicitud.getConvocatoriaId())
                .orElseThrow(() -> new ConvocatoriaEnlaceNotFoundException(solicitud.getConvocatoriaId()));
            this.comunicadosService.enviarComunicadoSolicitudCambioEstadoDen(solicitud.getSolicitanteRef(),
                convocatoria.getTitulo(),
                convocatoria.getFechaConcesion(),
                enlaces);
          }
          break;
        default:
          log.debug(
              "enviarComunicadosCambioEstado(Solicitud solicitud, EstadoSolicitud estadoSolicitud) - El estado {} no tiene comunicado",
              estadoSolicitud.getEstado());
          break;
      }
      log.debug("enviarComunicadosCambioEstado(Solicitud solicitud, EstadoSolicitud estadoSolicitud) - end");
    } catch (Exception e) {
      log.debug(
          "Error enviarComunicadoSolicitudCambioEstadoAlegaciones(Solicitud solicitud, EstadoSolicitud estadoSolicitud",
          e);
    }
  }

  private boolean checkConvocatoriaTramitable(Long convocatoriaId) {
    ConfiguracionSolicitud datosConfiguracionSolicitud = configuracionSolicitudRepository
        .findByConvocatoriaId(convocatoriaId)
        .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoriaId));
    return datosConfiguracionSolicitud.getTramitacionSGI();
  }

  private boolean checkConvocatoriaPropia(String solicitanteRef) {
    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();
    return personaRef.equals(solicitanteRef);
  }

  private String getNombreApellidosByPersonaRef(String personaRef) {
    PersonaOutput persona = personasService.findById(personaRef);
    return persona.getNombre() + " " + persona.getApellidos();
  }
}
