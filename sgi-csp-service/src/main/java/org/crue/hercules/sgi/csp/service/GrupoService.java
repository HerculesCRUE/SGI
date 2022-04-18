package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.FormularioSolicitudTypeNotCorrect;
import org.crue.hercules.sgi.csp.exceptions.GrupoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotInCorrectState;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo.Dedicacion;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.GrupoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.predicate.GrupoPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.PeriodDateUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link Grupo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoService {

  private final GrupoRepository repository;
  private final GrupoTipoService grupoTipoService;
  private final GrupoEspecialInvestigacionService grupoEspecialInvestigacionService;
  private final Validator validator;
  private final SgiConfigProperties sgiConfigProperties;
  private final SolicitudRepository solicitudRepository;
  private final RolProyectoService rolProyectoService;
  private final GrupoEquipoRepository grupoEquipoRepository;

  /**
   * Guarda la entidad {@link Grupo}.
   * 
   * @param grupo la entidad {@link Grupo} a guardar.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Grupo create(@Valid Grupo grupo) {
    log.debug("create(Grupo grupo) - start");

    AssertHelper.idIsNull(grupo.getId(), Grupo.class);

    GrupoTipo grupoTipo = null;
    if (grupo.getTipo() != null) {
      grupoTipo = GrupoTipo.builder()
          .tipo(grupo.getTipo().getTipo())
          .fechaInicio(grupo.getFechaInicio())
          .build();
    }
    GrupoEspecialInvestigacion grupoEspecialInvestigacion = null;
    if (grupo.getEspecialInvestigacion() != null) {
      grupoEspecialInvestigacion = GrupoEspecialInvestigacion.builder()
          .especialInvestigacion(grupo.getEspecialInvestigacion().getEspecialInvestigacion())
          .fechaInicio(grupo.getFechaInicio())
          .build();
    }

    // Elimina el GrupoTipo y GrupoEspecialInvestigacion para crear el grupo
    grupo.setTipo(null);
    grupo.setEspecialInvestigacion(null);

    Grupo newGrupo = repository.save(grupo);

    // Crea el GrupoTipo y GrupoEspecialInvestigacion
    if (grupoTipo != null) {
      grupoTipo.setGrupoId(newGrupo.getId());
      newGrupo.setTipo(grupoTipoService.create(grupoTipo));
    }

    if (grupoEspecialInvestigacion != null) {
      grupoEspecialInvestigacion.setGrupoId(newGrupo.getId());
      newGrupo.setEspecialInvestigacion(grupoEspecialInvestigacionService.create(grupoEspecialInvestigacion));
    }

    // Actualiza el grupo con los GrupoTipo y GrupoEspecialInvestigacion creados
    Grupo returnValue = repository.save(newGrupo);

    log.debug("create(Grupo grupo) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Grupo}.
   *
   * @param grupoActualizar {@link Grupo} con los datos actualizados.
   * @return {@link Grupo} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public Grupo update(@Valid Grupo grupoActualizar) {
    log.debug("update(Grupo grupoActualizar) - start");

    AssertHelper.idNotNull(grupoActualizar.getId(), Grupo.class);

    return repository.findById(grupoActualizar.getId()).map(data -> {
      data.setNombre(grupoActualizar.getNombre());
      data.setCodigo(grupoActualizar.getCodigo());
      data.setProyectoSgeRef(grupoActualizar.getProyectoSgeRef());
      data.setFechaInicio(grupoActualizar.getFechaInicio());
      data.setFechaFin(grupoActualizar.getFechaFin());

      data = this.updateEspecialInvestigacion(data, grupoActualizar);
      data = this.updateTipo(data, grupoActualizar);

      Grupo returnValue = repository.save(data);

      log.debug("update(Grupo grupoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(grupoActualizar.getId()));
  }

  private Grupo updateEspecialInvestigacion(Grupo data, Grupo grupoActualizar) {
    ZonedDateTime fechaInicio = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MIN);
    ZonedDateTime fechaFin = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).withNano(0).minusDays(1);
    // Ambos informados y distintos (actualiza la fecha de fin del anterior y crea
    // el nuevo)
    if (data.getEspecialInvestigacion() != null && grupoActualizar.getEspecialInvestigacion() != null
        && !data.getEspecialInvestigacion().getEspecialInvestigacion()
            .equals(grupoActualizar.getEspecialInvestigacion().getEspecialInvestigacion())) {

      // Especial investigación anterior se actualiza tambien en el dia actual
      if (this.isSameDay(fechaInicio,
          data.getEspecialInvestigacion().getFechaInicio().atZone(this.sgiConfigProperties.getTimeZone().toZoneId()))) {
        GrupoEspecialInvestigacion grupoEspecialInvestigacion = grupoEspecialInvestigacionService
            .findById(data.getEspecialInvestigacion().getId());
        grupoEspecialInvestigacion
            .setEspecialInvestigacion(grupoActualizar.getEspecialInvestigacion().getEspecialInvestigacion());
        data.setEspecialInvestigacion(grupoEspecialInvestigacionService.update(grupoEspecialInvestigacion));
      } else {
        GrupoEspecialInvestigacion grupoEspecialInvestigacion = GrupoEspecialInvestigacion.builder()
            .especialInvestigacion(grupoActualizar.getEspecialInvestigacion().getEspecialInvestigacion())
            .fechaInicio(fechaInicio.toInstant())
            .grupoId(data.getId()).build();
        data.getEspecialInvestigacion().setFechaFin(fechaFin.toInstant());
        grupoEspecialInvestigacionService.update(data.getEspecialInvestigacion());

        data.setEspecialInvestigacion(grupoEspecialInvestigacionService.create(grupoEspecialInvestigacion));
      }
    }
    // Tipo añadido y sin tipo previo (crea el nuevo tipo)
    else if (data.getEspecialInvestigacion() == null && grupoActualizar.getEspecialInvestigacion() != null) {
      GrupoEspecialInvestigacion grupoEspecialInvestigacion = GrupoEspecialInvestigacion.builder()
          .especialInvestigacion(grupoActualizar.getEspecialInvestigacion().getEspecialInvestigacion())
          .fechaInicio(fechaInicio.toInstant())
          .grupoId(data.getId()).build();

      data.setEspecialInvestigacion(grupoEspecialInvestigacionService.create(grupoEspecialInvestigacion));
    }
    // Con tipo previo y eliminado (actualiza la fecha de fin del anterior)
    else if (data.getEspecialInvestigacion() != null && grupoActualizar.getEspecialInvestigacion() == null) {
      data.getEspecialInvestigacion().setFechaFin(fechaFin.toInstant());
      grupoEspecialInvestigacionService.update(data.getEspecialInvestigacion());
      data.setEspecialInvestigacion(null);
    }
    return data;
  }

  private Grupo updateTipo(Grupo data, Grupo grupoActualizar) {
    ZonedDateTime fechaInicio = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MIN);
    ZonedDateTime fechaFin = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).withNano(0).minusDays(1);

    // Ambos informados y distintos (actualiza la fecha de fin del anterior y crea
    // el nuevo)
    if (data.getTipo() != null && grupoActualizar.getTipo() != null
        && !data.getTipo().getTipo().equals(grupoActualizar.getTipo().getTipo())) {

      // El tipo anterior se actualiza tambien en el dia actual
      if (this.isSameDay(fechaInicio,
          data.getTipo().getFechaInicio().atZone(this.sgiConfigProperties.getTimeZone().toZoneId()))) {
        GrupoTipo grupoTipo = grupoTipoService.findById(data.getTipo().getId());
        grupoTipo.setTipo(grupoActualizar.getTipo().getTipo());
        data.setTipo(grupoTipoService.update(grupoTipo));
      } else {
        GrupoTipo grupoTipo = GrupoTipo.builder().tipo(grupoActualizar.getTipo().getTipo())
            .fechaInicio(fechaInicio.toInstant())
            .grupoId(data.getId()).build();
        data.getTipo().setFechaFin(fechaFin.toInstant());
        grupoTipoService.update(data.getTipo());

        data.setTipo(grupoTipoService.create(grupoTipo));
      }
    }
    // Tipo añadido y sin tipo previo (crea el nuevo tipo)
    else if (data.getTipo() == null && grupoActualizar.getTipo() != null) {
      GrupoTipo grupoTipo = GrupoTipo.builder().tipo(grupoActualizar.getTipo().getTipo())
          .fechaInicio(fechaInicio.toInstant())
          .grupoId(data.getId()).build();

      data.setTipo(grupoTipoService.create(grupoTipo));
    }
    // Con tipo previo y eliminado (actualiza la fecha de fin del anterior)
    else if (data.getTipo() != null && grupoActualizar.getTipo() == null) {
      data.getTipo().setFechaFin(fechaFin.toInstant());
      grupoTipoService.update(data.getTipo());
      data.setTipo(null);
    }
    return data;
  }

  /**
   * Obtiene una entidad {@link Grupo} por id.
   * 
   * @param id Identificador de la entidad {@link Grupo}.
   * @return la entidad {@link Grupo}.
   */
  public Grupo findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, Grupo.class);
    final Grupo returnValue = repository.findById(id).orElseThrow(() -> new GrupoNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Grupo} por id.
   *
   * @param id el id de la entidad {@link Grupo}.
   * @return <code>true</code> si existe y <code>false</code> en caso contrario.
   */
  public boolean existsById(Long id) {
    log.debug("existsById(Long id)  - start");

    AssertHelper.idNotNull(id, Grupo.class);
    final boolean exists = repository.existsById(id);

    log.debug("existsById(Long id)  - end");
    return exists;
  }

  /**
   * Obtener todas las entidades {@link Grupo} paginadas y/o filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link Grupo} paginadas y/o
   *         filtradas.
   */
  public Page<Grupo> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Grupo> specs = GrupoSpecifications.distinct()
        .and(SgiRSQLJPASupport.toSpecification(query, GrupoPredicateResolver.getInstance(sgiConfigProperties)));
    Page<Grupo> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Grupo} activas paginadas y/o filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link Grupo} activas paginadas y/o
   *         filtradas.
   */
  public Page<Grupo> findActivos(String query, Pageable paging) {
    log.debug("findActivos(String query, Pageable paging) - start");

    Specification<Grupo> specs = GrupoSpecifications.distinct()
        .and(GrupoSpecifications.activos())
        .and(SgiRSQLJPASupport.toSpecification(query, GrupoPredicateResolver.getInstance(sgiConfigProperties)));
    Page<Grupo> returnValue = repository.findAll(specs, paging);

    log.debug("findActivos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link Grupo}.
   *
   * @param id Id del {@link Grupo}.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  public Grupo desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    AssertHelper.idNotNull(id, Grupo.class);

    return repository.findById(id).map(grupo -> {
      if (Boolean.FALSE.equals(grupo.getActivo())) {
        // Si no esta activo no se hace nada
        return grupo;
      }

      grupo.setActivo(false);

      Grupo returnValue = repository.save(grupo);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(id));
  }

  /**
   * Activa el {@link Grupo}.
   *
   * @param id Id del {@link Grupo}.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  public Grupo activar(Long id) {
    log.debug("activar(Long id) - start");

    AssertHelper.idNotNull(id, Grupo.class);

    return repository.findById(id).map(grupo -> {
      if (Boolean.TRUE.equals(grupo.getActivo())) {
        // Si esta activo no se hace nada
        return grupo;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<Grupo>> result = validator.validate(grupo, BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      grupo.setActivo(true);

      Grupo returnValue = repository.save(grupo);
      log.debug("activar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(id));
  }

  /**
   * Devuelve el siguiente codigo para el {@link Grupo} del departamento indicado.
   *
   * @param departamentoRef departamento para el que se quiere obtener el codigo.
   * @return el siguiente codigo para el {@link Grupo}.
   */
  public String getNextCodigo(String departamentoRef) {
    log.debug("getNextCodigo(String departamentoRef) - start");

    Specification<Grupo> specs = GrupoSpecifications.byDepartamentoOrigenRef(departamentoRef);
    long numGruposDepartamento = repository.count(specs);

    String codigo;
    do {
      codigo = generateCodigo(departamentoRef, ++numGruposDepartamento);
    } while (isDuplicatedCodigo(codigo));

    log.debug("getNextCodigo(String departamentoRef) - end");
    return codigo;
  }

  /**
   * Comprueba si ya existe un grupo activo (o otro grupo si se indica un grupoId)
   * con el codigo indicado
   * 
   * @param grupoId Identificador del {@link Grupo}
   * @param codigo  codigo que se quiere validar
   * @return <code>true</code> si ya existe otro grupo con el mismo codigo,
   *         <code>false</code> en caso contrario
   */
  public boolean isDuplicatedCodigo(Long grupoId, String codigo) {
    log.debug("isDuplicatedCodigo(Long grupoId, String codigo) - start");
    Specification<Grupo> specs = GrupoSpecifications.byCodigo(codigo).and(GrupoSpecifications.activos());

    if (grupoId != null) {
      specs = specs.and(GrupoSpecifications.byIdNotEqual(grupoId));
    }

    log.debug("isDuplicatedCodigo(Long grupoId, String codigo) - end");
    return repository.count(specs) > 0;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica}
   * 
   * @param query    filtro de búsqueda.
   * @param pageable {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} activas
   *         paginadas y filtradas.
   */
  public Page<RelacionEjecucionEconomica> findRelacionesEjecucionEconomicaGrupos(String query, Pageable pageable) {
    log.debug("findRelacionesEjecucionEconomicaGrupos(String query, Pageable pageable) - start");

    Specification<Grupo> specs = GrupoSpecifications.activos();
    if (query != null) {
      specs = specs
          .and(SgiRSQLJPASupport.toSpecification(query, GrupoPredicateResolver.getInstance(sgiConfigProperties)));
    }

    Page<RelacionEjecucionEconomica> returnValue = repository.findRelacionesEjecucionEconomica(specs, pageable);
    log.debug("findRelacionesEjecucionEconomicaGrupos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba si ya existe un grupo con el
   * codigo indicado
   * 
   * @param codigo codigo que se quiere validar
   * @return <code>true</code> si ya existe otro grupo con el mismo codigo,
   *         <code>false</code> en caso contrario
   */
  private boolean isDuplicatedCodigo(String codigo) {
    return isDuplicatedCodigo(null, codigo);
  }

  /**
   * Genera el codigo para el grupo concatenando el departamentoRef y el
   * numGrupoDepartamento
   * 
   * @param departamentoRef
   * @param numGrupoDepartamento
   * @return el codigo generado
   */
  private String generateCodigo(String departamentoRef, long numGrupoDepartamento) {
    return departamentoRef + "-" + numGrupoDepartamento;
  }

  /**
   * Devuelve si grupoRef pertenece a un grupo de investigación con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param grupoRef grupoRef
   * @param anio     año de baremación
   * @return true/false
   */
  public boolean isGrupoBaremable(Long grupoRef, Integer anio) {

    Instant fechaBaremacion = PeriodDateUtil.calculateFechaFinBaremacionByAnio(anio, sgiConfigProperties.getTimeZone());
    return repository.isGrupoBaremable(grupoRef, fechaBaremacion);
  }

  /**
   * Devuelve una lista de {@link GrupoDto} con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param anio año de baremación
   * @return Lista de {@link GrupoDto}
   */
  public List<GrupoDto> findAllByAnio(Integer anio) {

    Instant fechaBaremacion = PeriodDateUtil.calculateFechaFinBaremacionByAnio(anio, sgiConfigProperties.getTimeZone());
    return repository.findAllByAnio(fechaBaremacion);
  }

  /*
   * Guarda la entidad {@link Grupo} a partir de los datos de la entidad
   * {@link Solicitud}.
   *
   * @param solicitudId identificador de la entidad {@link Solicitud} a copiar
   * datos.
   * 
   * @param grupo datos necesarios para crear el {@link Grupo}
   * 
   * @return proyecto la entidad {@link Grupo} persistida.
   */
  @Transactional
  public Grupo createGrupoBySolicitud(Long solicitudId, Grupo grupo) {
    log.debug("createGrupoBySolicitud(Long solicitudId, GrupoInput grupo) - start");

    Solicitud solicitud = solicitudRepository.findById(solicitudId)
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));

    this.validarDatosSolicitud(solicitud);
    grupo.setSolicitudId(solicitudId);
    // Crea el grupo
    Grupo newGrupo = repository.save(grupo);

    // Grupo Especial investigacion
    GrupoEspecialInvestigacion grupoEspecialInvestigacion = GrupoEspecialInvestigacion.builder()
        .especialInvestigacion(false)
        .fechaInicio(newGrupo.getFechaInicio())
        .build();

    grupoEspecialInvestigacion.setGrupoId(newGrupo.getId());
    newGrupo.setEspecialInvestigacion(grupoEspecialInvestigacionService.create(grupoEspecialInvestigacion));

    // Grupo Equipo
    RolProyecto rolProyecto = rolProyectoService.findPrincipal();

    GrupoEquipo grupoEquipo = GrupoEquipo.builder()
        .personaRef(solicitud.getSolicitanteRef())
        .fechaInicio(newGrupo.getFechaInicio())
        .fechaFin(newGrupo.getFechaFin())
        .rol(rolProyecto)
        .dedicacion(Dedicacion.COMPLETA)
        .participacion(new BigDecimal(100))
        .grupoId(newGrupo.getId())
        .build();

    grupoEquipoRepository.save(grupoEquipo);
    Grupo returnValue = repository.save(newGrupo);

    log.debug("createGrupoBySolicitud(Long solicitudId, GrupoInput grupo) - end");
    return returnValue;
  }

  /**
   * Se comprueba que los datos de la {@link Solicitud} a copiar para crear el
   * {@link Grupo} cumplan las validaciones oportunas
   *
   * @param solicitud datos de la {@link Solicitud}
   */
  private void validarDatosSolicitud(Solicitud solicitud) {

    if (!solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL_ALEGADA)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA_PROVISIONAL_NO_ALEGADA)
        && !solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCEDIDA)) {
      throw new SolicitudNotInCorrectState();
    }

    if (solicitud.getFormularioSolicitud() != FormularioSolicitud.GRUPO) {
      throw new FormularioSolicitudTypeNotCorrect();
    }
  }

  private boolean isSameDay(ZonedDateTime date1, ZonedDateTime date2) {
    return date1.truncatedTo(ChronoUnit.DAYS).equals(date2.truncatedTo(ChronoUnit.DAYS));
  }

  /**
   * Hace las comprobaciones necesarias para determinar si el {@link Grupo}
   * puede ser modificado. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con el
   * {@link Grupo}.
   * 
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  public boolean modificable() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-GIN-E");
  }

}
