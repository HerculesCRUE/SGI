package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.repository.predicate.ConvocatoriaPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

  private final ConvocatoriaRepository repository;
  private final ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  private final ModeloUnidadRepository modeloUnidadRepository;
  private final ModeloTipoFinalidadRepository modeloTipoFinalidadRepository;
  private final TipoRegimenConcurrenciaRepository tipoRegimenConcurrenciaRepository;
  private final TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository;
  private final ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  public ConvocatoriaServiceImpl(ConvocatoriaRepository repository,
      ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository,
      ModeloUnidadRepository modeloUnidadRepository, ModeloTipoFinalidadRepository modeloTipoFinalidadRepository,
      TipoRegimenConcurrenciaRepository tipoRegimenConcurrenciaRepository,
      TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository,
      ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository) {
    this.repository = repository;
    this.convocatoriaPeriodoJustificacionRepository = convocatoriaPeriodoJustificacionRepository;
    this.modeloUnidadRepository = modeloUnidadRepository;
    this.modeloTipoFinalidadRepository = modeloTipoFinalidadRepository;
    this.tipoRegimenConcurrenciaRepository = tipoRegimenConcurrenciaRepository;
    this.tipoAmbitoGeograficoRepository = tipoAmbitoGeograficoRepository;
    this.convocatoriaPeriodoSeguimientoCientificoRepository = convocatoriaPeriodoSeguimientoCientificoRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
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

    Assert.isNull(convocatoria.getId(), "Id tiene que ser null para crear la Convocatoria");

    // Validación permisos
    String authority = "CSP-CON-C";

    Assert.isTrue(
        SgiSecurityContextHolder.hasAuthority(authority)
            || SgiSecurityContextHolder.hasAuthorityForUO(authority, convocatoria.getUnidadGestionRef()),
        "El usuario no tiene permisos para crear una convocatoria asociada a la unidad de gestión recibida.");

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

    Assert.notNull(convocatoria.getId(), "Id no puede ser null para actualizar Convocatoria");

    return repository.findById(convocatoria.getId()).map((data) -> {

      // comprobar si es modificable
      Assert.isTrue(this.modificable(data.getId(), data.getUnidadGestionRef(), new String[] { "CSP-CON-E" }),
          "No se puede modificar Convocatoria. No tiene los permisos necesarios o está registrada y cuenta con solicitudes o proyectos asociados");

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
      data.setColaborativos(validConvocatoria.getColaborativos());
      data.setDuracion(validConvocatoria.getDuracion());
      data.setAmbitoGeografico(validConvocatoria.getAmbitoGeografico());
      data.setClasificacionCVN(validConvocatoria.getClasificacionCVN());
      data.setActivo(validConvocatoria.getActivo());

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

    Assert.notNull(id, "Id no puede ser null para registrar Convocatoria");

    return repository.findById(id).map((data) -> {

      Assert.isTrue(data.getEstado() == Convocatoria.Estado.BORRADOR,
          "Convocatoria deber estar en estado 'Borrador' para pasar a 'Registrada'");

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

    Assert.notNull(id, "Convocatoria id no puede ser null para reactivar un Convocatoria");

    return repository.findById(id).map(convocatoria -> {
      if (convocatoria.getActivo()) {
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

    Assert.notNull(id, "Convocatoria id no puede ser null para desactivar un Convocatoria");

    return repository.findById(id).map(convocatoria -> {
      if (!convocatoria.getActivo()) {
        return convocatoria;
      }

      // Comprobación de permisos para borrado lógico de convocatorias
      String authority = "CSP-CON-B";
      Assert.isTrue(
          (SgiSecurityContextHolder.hasAuthority(authority)
              || SgiSecurityContextHolder.hasAuthorityForUO(authority, convocatoria.getUnidadGestionRef()))
              && !repository.esRegistradaConSolicitudesOProyectos(id),
          "No se puede eliminar Convocatoria. No tiene los permisos necesarios o está registrada y cuenta con solicitudes o proyectos asociados");

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
   * @param atuhorities        Authorities a validar
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  @Override
  public boolean modificable(Long id, String unidadConvocatoria, String[] atuhorities) {
    log.debug("modificable(Long id, String unidadConvocatoria) - start");

    if (StringUtils.isEmpty(unidadConvocatoria)) {
      unidadConvocatoria = repository.findById(id).map(convocatoria -> convocatoria.getUnidadGestionRef())
          .orElseThrow(() -> new ConvocatoriaNotFoundException(id));
    }

    if (SgiSecurityContextHolder.hasAnyAuthorityForUO(atuhorities, unidadConvocatoria)) {
      // Será modificable si no tiene solicitudes o proyectos asociados
      return !(repository.esRegistradaConSolicitudesOProyectos(id));
    }

    log.debug("modificable(Long id, String unidadConvocatoria) - end");
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

    // id convocatoria presente
    if (id != null) {

      Optional<Convocatoria> convocatoria = repository.findById(id);

      // convocatoria existe y su estado actual es 'Borrador'
      if (convocatoria.isPresent() && convocatoria.get().getEstado() == Convocatoria.Estado.BORRADOR) {

        // Campos requeridos a nivel de convocatoria
        if (convocatoria.get().getUnidadGestionRef() != null && convocatoria.get().getModeloEjecucion() != null
            && convocatoria.get().getFechaPublicacion() != null && convocatoria.get().getTitulo() != null
            && convocatoria.get().getFinalidad() != null && convocatoria.get().getAmbitoGeografico() != null) {

          Optional<ConfiguracionSolicitud> configuracionSolicitud = configuracionSolicitudRepository
              .findByConvocatoriaId(convocatoria.get().getId());

          // tiene configuración solicitud
          if (configuracionSolicitud.isPresent()) {

            // campos requeridos a nivel de configuración solicitud
            if (configuracionSolicitud.get().getTramitacionSGI() != null
                && configuracionSolicitud.get().getFormularioSolicitud() != null) {

              // con tramitación SGI debe tener una fase asignada
              if (!(configuracionSolicitud.get().getFasePresentacionSolicitudes() == null
                  && configuracionSolicitud.get().getTramitacionSGI() == Boolean.TRUE)) {
                return true;
              }
            }
          }
        }
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
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Convocatoria} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<Convocatoria> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<Convocatoria> specs = ConvocatoriaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Convocatoria> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
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
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<Convocatoria> specs = ConvocatoriaSpecifications.activos()
        .and(ConvocatoriaSpecifications.registradas())
        .and(ConvocatoriaSpecifications.configuracionSolicitudTramitacionSGI())
        .and(SgiRSQLJPASupport.toSpecification(query, ConvocatoriaPredicateResolver.getInstance()));

    Page<Convocatoria> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
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
        .and(SgiRSQLJPASupport.toSpecification(query, ConvocatoriaPredicateResolver.getInstance()));

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
        ConvocatoriaPredicateResolver.getInstance());

    List<String> unidadesGestion = SgiSecurityContextHolder.getUOsForAnyAuthority(
        new String[] { "CSP-CON-C", "CSP-CON-V", "CSP-CON-E", "CSP-CON-INV-V", "CSP-CON-B", "CSP-CON-R" });

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

    Instant fechaActual = Instant.now();

    return convocatoria.getActivo() && datosConfiguracionSolicitud.getTramitacionSGI()
        && datosConfiguracionSolicitud.getFasePresentacionSolicitudes().getFechaInicio().isBefore(fechaActual)
        && datosConfiguracionSolicitud.getFasePresentacionSolicitudes().getFechaFin().isAfter(fechaActual);
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
          "No se puede modificar la unidad de gestión al existir registros dependientes en las pantallas Enlaces, Plazos y fases, Hitos o Documentos");
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
          "No se puede modificar el modelo de ejecución al existir registros dependientes en las pantallas Enlaces, Plazos y fases, Hitos o Documentos");
    }

    // ModeloEjecucion
    if (datosConvocatoria.getModeloEjecucion() != null) {

      Assert.notNull(datosConvocatoria.getUnidadGestionRef(),
          "UnidadGestionRef requerido para obtener ModeloEjecucion");

      Optional<ModeloUnidad> modeloUnidad = modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(
          datosConvocatoria.getModeloEjecucion().getId(), datosConvocatoria.getUnidadGestionRef());

      Assert.isTrue(modeloUnidad.isPresent(), "ModeloEjecucion '" + datosConvocatoria.getModeloEjecucion().getNombre()
          + "' no disponible para la UnidadGestion " + datosConvocatoria.getUnidadGestionRef());

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getModeloEjecucion() != null
          && (modeloUnidad.get().getModeloEjecucion().getId() != datosOriginales.getModeloEjecucion().getId()))) {
        Assert.isTrue(modeloUnidad.get().getActivo(),
            "ModeloEjecucion '" + modeloUnidad.get().getModeloEjecucion().getNombre()
                + "' no está activo para la UnidadGestion " + modeloUnidad.get().getUnidadGestionRef());
      }
      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getModeloEjecucion() != null
          && (modeloUnidad.get().getModeloEjecucion().getId() != datosOriginales.getModeloEjecucion().getId()))) {
        Assert.isTrue(modeloUnidad.get().getModeloEjecucion().getActivo(),
            "ModeloEjecucion '" + modeloUnidad.get().getModeloEjecucion().getNombre() + "' no está activo");
      }
      datosConvocatoria.setModeloEjecucion(modeloUnidad.get().getModeloEjecucion());
    }

    // TipoFinalidad
    if (datosConvocatoria.getFinalidad() != null) {

      Assert.notNull(datosConvocatoria.getModeloEjecucion(), "ModeloEjecucion requerido para obtener TipoFinalidad");

      Optional<ModeloTipoFinalidad> modeloTipoFinalidad = modeloTipoFinalidadRepository
          .findByModeloEjecucionIdAndTipoFinalidadId(datosConvocatoria.getModeloEjecucion().getId(),
              datosConvocatoria.getFinalidad().getId());

      Assert.isTrue(modeloTipoFinalidad.isPresent(), "TipoFinalidad '" + datosConvocatoria.getFinalidad().getNombre()
          + "' no disponible para el ModeloEjecucion " + datosConvocatoria.getModeloEjecucion().getNombre());

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getFinalidad() != null
          && (modeloTipoFinalidad.get().getTipoFinalidad().getId() != datosOriginales.getFinalidad().getId()))) {
        Assert.isTrue(modeloTipoFinalidad.get().getActivo(),
            "ModeloTipoFinalidad '" + modeloTipoFinalidad.get().getTipoFinalidad().getNombre()
                + "' no está activo para el ModeloEjecucion "
                + modeloTipoFinalidad.get().getModeloEjecucion().getNombre());
      }

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getFinalidad() != null
          && (modeloTipoFinalidad.get().getTipoFinalidad().getId() != datosOriginales.getFinalidad().getId()))) {
        Assert.isTrue(modeloTipoFinalidad.get().getTipoFinalidad().getActivo(),
            "TipoFinalidad '" + modeloTipoFinalidad.get().getTipoFinalidad().getNombre() + "' no está activo");
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
            "RegimenConcurrencia '" + datosConvocatoria.getRegimenConcurrencia().getNombre() + "' no disponible");

        // Permitir no activos solo si estamos modificando y es el mismo
        if (datosOriginales == null || (datosOriginales.getRegimenConcurrencia() != null
            && (tipoRegimenConcurrencia.get().getId() != datosOriginales.getRegimenConcurrencia().getId()))) {
          Assert.isTrue(tipoRegimenConcurrencia.get().getActivo(),
              "RegimenConcurrencia '" + tipoRegimenConcurrencia.get().getNombre() + "' no está activo");
        }
        datosConvocatoria.setRegimenConcurrencia(tipoRegimenConcurrencia.get());
      }
    }

    // TipoAmbitoGeografico
    if (datosConvocatoria.getAmbitoGeografico() != null) {

      Optional<TipoAmbitoGeografico> tipoAmbitoGeografico = tipoAmbitoGeograficoRepository
          .findById(datosConvocatoria.getAmbitoGeografico().getId());

      Assert.isTrue(tipoAmbitoGeografico.isPresent(),
          "AmbitoGeografico '" + datosConvocatoria.getAmbitoGeografico().getNombre() + "' no disponible");

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getAmbitoGeografico() != null
          && (tipoAmbitoGeografico.get().getId() != datosOriginales.getAmbitoGeografico().getId()))) {
        Assert.isTrue(tipoAmbitoGeografico.get().getActivo(),
            "AmbitoGeografico '" + tipoAmbitoGeografico.get().getNombre() + "' no está activo");
      }
      datosConvocatoria.setAmbitoGeografico(tipoAmbitoGeografico.get());
    }

    if (datosConvocatoria.getId() != null) {
      // Comprueba que la duracion no sea menor que el ultimo mes del ultimo
      // ConvocatoriaPeriodoJustificacion de la convocatoria
      if (datosConvocatoria.getDuracion() != null && datosConvocatoria.getDuracion() != datosOriginales.getDuracion()) {
        convocatoriaPeriodoJustificacionRepository
            .findFirstByConvocatoriaIdOrderByNumPeriodoDesc(datosConvocatoria.getId())
            .ifPresent(convocatoriaPeriodoJustificacion -> {
              Assert.isTrue(convocatoriaPeriodoJustificacion.getMesFinal() <= datosConvocatoria.getDuracion(),
                  "Hay ConvocatoriaPeriodoJustificacion con mesFinal inferior a la nueva duracion");
            });
      }
    }

    // Duración mayor que el mayor mes del Periodo Seguimiento Cientifico
    if (datosOriginales != null && datosConvocatoria.getDuracion() != null
        && (datosConvocatoria.getDuracion() != datosOriginales.getDuracion())) {
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
    Assert.notNull(datosConvocatoria.getUnidadGestionRef(), "UnidadGestionRef no puede ser null en la Convocatoria");
    // ModeloEjecucion
    Assert.notNull(datosConvocatoria.getModeloEjecucion(), "ModeloEjecucion no puede ser null en la Convocatoria");
    // Codigo
    Assert.notNull(datosConvocatoria.getCodigo(), "Codigo no puede ser null en la Convocatoria");
    // FechaPublicacion
    Assert.notNull(datosConvocatoria.getFechaPublicacion(), "Fecha publicación no puede ser null en la Convocatoria");
    // Titulo
    Assert.notNull(datosConvocatoria.getTitulo(), "Titulo no puede ser null en la Convocatoria");
    // TipoFinalidad
    Assert.notNull(datosConvocatoria.getFinalidad(), "Finalidad no puede ser null en la Convocatoria");
    // TipoAmbitoGeografico
    Assert.notNull(datosConvocatoria.getAmbitoGeografico(), "AmbitoGeografico no puede ser null en la Convocatoria");

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
    Assert.notNull(datosConvocatoria.getUnidadGestionRef(), "UnidadGestionRef no puede ser null en la Convocatoria");
    // Codigo
    Assert.notNull(datosConvocatoria.getCodigo(), "Codigo no puede ser null en la Convocatoria");
    // FechaPublicacion
    Assert.notNull(datosConvocatoria.getFechaPublicacion(), "Fecha publicación no puede ser null en la Convocatoria");
    // Titulo
    Assert.notNull(datosConvocatoria.getTitulo(), "Titulo no puede ser null en la Convocatoria");

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
    Assert.notNull(datosConfiguracionSolicitud.getTramitacionSGI(),
        "Habilitar presentacion SGI no puede ser null para crear ConfiguracionSolicitud cuando la convocatoria está registrada");
    // Convocatoria Fase
    Assert.isTrue(
        !(datosConfiguracionSolicitud.getFasePresentacionSolicitudes() == null
            && datosConfiguracionSolicitud.getTramitacionSGI() == Boolean.TRUE),
        "Plazo presentación solicitudes no puede ser null cuando se establece presentacion SGI");
    // Tipo Formulario Solicitud
    Assert.notNull(datosConfiguracionSolicitud.getFormularioSolicitud(),
        "Tipo formulario no puede ser null para crear ConfiguracionSolicitud cuando la convocatoria está registrada");

    log.debug("validarRequeridosConfiguracionSolicitudConvocatoriaRegistrada(Convocatoria datosConvocatoria) - end");
  }
}
