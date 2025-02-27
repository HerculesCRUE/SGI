package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.dto.AnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadGastosTotales;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadNotificacionSge;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadResumen;
import org.crue.hercules.sgi.csp.exceptions.ProyectoAnualidadAnioUniqueException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoAnualidadNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad.OnCrear;
import org.crue.hercules.sgi.csp.repository.AnualidadGastoRepository;
import org.crue.hercules.sgi.csp.repository.AnualidadIngresoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoAnualidadRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.AnualidadGastoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.AnualidadIngresoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoAnualidadSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoAnualidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProyectoAnualidadService {
  private final Validator validator;

  private final ProyectoAnualidadRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final AnualidadGastoRepository anualidadGastoRepository;
  private final AnualidadIngresoRepository anualidadIngresoRepository;

  public ProyectoAnualidadService(Validator validator, ProyectoAnualidadRepository proyectoAnualidadepository,
      ProyectoRepository proyectoRepository, AnualidadGastoRepository anualidadGastoRepository,
      AnualidadIngresoRepository anualidadIngresoRepository) {

    this.validator = validator;
    this.repository = proyectoAnualidadepository;
    this.proyectoRepository = proyectoRepository;
    this.anualidadGastoRepository = anualidadGastoRepository;
    this.anualidadIngresoRepository = anualidadIngresoRepository;
  }

  /**
   * Crea un {@link ProyectoAnualidad} para un {@link Proyecto}.
   * 
   * @param proyectoAnualidad {@link ProyectoAnualidad}
   * @return {@link ProyectoAnualidad} creado.
   */
  @Transactional
  @Validated({ ProyectoAnualidad.OnCrear.class })
  public ProyectoAnualidad create(ProyectoAnualidad proyectoAnualidad) {
    log.debug("create(ProyectoAnualidad proyectoAnualidad) - start");

    AssertHelper.idIsNull(proyectoAnualidad.getId(), ProyectoAnualidad.class);
    AssertHelper.idNotNull(proyectoAnualidad.getProyectoId(), Proyecto.class);

    // Invocar validaciones asociadas a OnCrear
    Set<ConstraintViolation<ProyectoAnualidad>> result = validator.validate(proyectoAnualidad, OnCrear.class);
    if (!result.isEmpty()) {
      throw new ConstraintViolationException(result);
    }

    // En caso de que el proyecto tenga anualidad genérica no se guardarán la fecha
    // de inicio y fecha fin ya que se tendrán en cuenta las del proyecto.
    Proyecto proyecto = proyectoRepository.findById(proyectoAnualidad.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoAnualidad.getId()));

    if (proyecto.getAnualidades() == null || !proyecto.getAnualidades()) {
      proyectoAnualidad.setFechaInicio(null);
      proyectoAnualidad.setFechaFin(null);
    } else {
      Assert.notNull(proyectoAnualidad.getAnio(),
          ProblemMessage.builder().key(Assert.class, "notNull")
              .parameter("field", ApplicationContextSupport.getMessage("anio"))
              .parameter("entity", ApplicationContextSupport.getMessage(ProyectoAnualidad.class)).build());

      if (repository.existsByAnioAndProyectoId(proyectoAnualidad.getAnio(), proyecto.getId())) {
        throw new ProyectoAnualidadAnioUniqueException(proyectoAnualidad.getAnio());
      }
    }

    proyectoAnualidad.setEnviadoSge(false);

    ProyectoAnualidad returnValue = repository.save(proyectoAnualidad);

    log.debug("create(ProyectoAnualidad proyectoAnualidad) - end");
    return returnValue;
  }

  /**
   * Actualiza un {@link ProyectoAnualidad} para un {@link Proyecto}.
   * 
   * @param proyectoAnualidad {@link ProyectoAnualidad}
   * @return {@link ProyectoAnualidad} creado.
   */
  @Transactional
  @Validated({ ProyectoAnualidad.OnActualizar.class })
  public ProyectoAnualidad update(ProyectoAnualidad proyectoAnualidad) {
    log.debug("update(ProyectoAnualidad proyectoAnualidad) - start");

    AssertHelper.idNotNull(proyectoAnualidad.getId(), ProyectoAnualidad.class);
    AssertHelper.idNotNull(proyectoAnualidad.getProyectoId(), Proyecto.class);

    return repository.findById(proyectoAnualidad.getId()).map(data -> {

      // En caso de que el proyecto tenga anualidad genérica no se guardarán la fecha
      // de inicio y fecha fin ya que se tendrán en cuenta las del proyecto.
      Proyecto proyecto = proyectoRepository.findById(proyectoAnualidad.getProyectoId())
          .orElseThrow(() -> new ProyectoNotFoundException(proyectoAnualidad.getId()));

      if (Boolean.TRUE.equals(proyecto.getAnualidades())) {
        data.setFechaInicio(proyectoAnualidad.getFechaInicio());
        data.setFechaFin(proyectoAnualidad.getFechaFin());

        AssertHelper.fieldNotNull(proyectoAnualidad, ProyectoAnualidad.class,
            ApplicationContextSupport.getMessage("anio"));

        if (repository.existsByAnioAndProyectoIdAndIdNot(proyectoAnualidad.getAnio(), proyecto.getId(),
            proyectoAnualidad.getId())) {
          throw new ProyectoAnualidadAnioUniqueException(proyectoAnualidad.getAnio());
        }
      }

      data.setAnio(proyectoAnualidad.getAnio());
      data.setPresupuestar(proyectoAnualidad.getPresupuestar());

      ProyectoAnualidad returnValue = repository.save(data);

      log.debug("update(ProyectoAnualidad proyectoAnualidad) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoAnualidadNotFoundException(proyectoAnualidad.getId()));
  }

  /**
   * Elimina la {@link ProyectoAnualidad}.
   *
   * @param id Id del {@link ProyectoAnualidad}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoAnualidad id no puede ser null para eliminar un ProyectoAnualidad");

    if (!repository.findById(id).isPresent()) {
      throw new ProyectoAnualidadNotFoundException(id);
    }

    repository.deleteByIdCascade(id);
    log.debug("delete(Long id) - end");
  }

  public Page<ProyectoAnualidadResumen> findAllResumenByProyecto(Long proyectoId, Pageable pageable) {
    log.debug("create(ProyectoAnualidad proyectoAnualidad) - start");
    Page<ProyectoAnualidadResumen> returnValue = repository.findAllResumenByProyectoId(proyectoId, pageable);

    log.debug("create(ProyectoAnualidad proyectoAnualidad) - end");
    return returnValue;
  }

  public Page<AnualidadGasto> findAllProyectoAnualidadGasto(Long proyectoId, Pageable pageable) {
    log.debug(" findAllProyectoAnualidadGasto(Long proyectoId, Pageable pageable) - start");

    Page<AnualidadGasto> returnValue = anualidadGastoRepository
        .findAllAnualidadGastoByProyectoAnualidadProyectoId(proyectoId, pageable);

    log.debug(" findAllProyectoAnualidadGasto(Long proyectoId, Pageable pageable) - end");
    return returnValue;
  }

  public ProyectoAnualidad findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoAnualidad returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoAnualidadNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Recupera el resumen de {@link AnualidadGasto} y {@link AnualidadIngreso}
   * ({@link AnualidadResumen}) de una {@link ProyectoAnualidad}.
   * 
   * @param proyectoAnualidadId Identificador de {@link ProyectoAnualidad}.
   * @return Listado del resumen de {@link AnualidadResumen}.
   */
  public List<AnualidadResumen> getPartidasResumen(Long proyectoAnualidadId) {
    log.debug("getPartidasResumen(Long proyectoAnualidadId) - start");
    List<AnualidadResumen> anulidadResumen = repository.getPartidasResumen(proyectoAnualidadId);
    log.debug("getPartidasResumen(Long proyectoAnualidadId) - end");
    return anulidadResumen;
  }

  /**
   * Recupera todos los {@link ProyectoAnualidad} .
   * 
   * @param query    filtro de búsqueda.
   * @param pageable datos paginación.
   * @return Listado paginado de {@link ProyectoAnualidad}.
   */
  public Page<ProyectoAnualidad> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    // TODO: Pendiente evaluar si es necesario retringir por unidad de gestión
    Specification<ProyectoAnualidad> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ProyectoAnualidad> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  public List<ProyectoAnualidad> findByProyectoId(Long proyectoId) {
    return this.repository.findByProyectoId(proyectoId);
  }

  /**
   * Recupera los {@link ProyectoAnualidadNotificacionSge} que cumplan las
   * condiciones de búsqueda y tengan a true el indicador presupuestar.
   * 
   * @param query filtro de búsqueda.
   * @return Listado de {@link ProyectoAnualidadNotificacionSge}.
   */
  public List<ProyectoAnualidadNotificacionSge> findAllNotificacionesSge(String query) {
    log.debug("findAllNotificacionesSge(String query) - start");

    List<String> unidadesGestion = SgiSecurityContextHolder.getUOsForAnyAuthority(new String[] { "CSP-EJEC-E" });

    List<ProyectoAnualidadNotificacionSge> returnValue = repository.findAllNotificacionSge(query, unidadesGestion);

    log.debug("findAllNotificacionesSge(String query) - end");
    return returnValue;
  }

  /**
   * Comprueba si hay {@link ProyectoAnualidad} del proyecto asociadas al
   * proyectoSgeRef que esten enviadas al SGE.
   * 
   * @param proyectoId     Identificador del {@link Proyecto}.
   * @param proyectoSgeRef Identificador del proyecto en el SGE.
   * @return <code>true</code> si existen o <code>false</code> si no.
   */
  public boolean hasGastosOrIngresosWithProyectoSgeRefEnviados(Long proyectoId, String proyectoSgeRef) {
    log.debug("hasGastosOrIngresosWithProyectoSgeRefEnviados(Long proyectoId, String proyectoSgeRef) - start");

    Specification<ProyectoAnualidad> specs = ProyectoAnualidadSpecifications.byProyectoId(proyectoId)
        .and(ProyectoAnualidadSpecifications.isEnviadoSge())
        .and(ProyectoAnualidadSpecifications.byAnualidadGastoProyectoSgeRef(proyectoSgeRef)
            .or(ProyectoAnualidadSpecifications.byAnualidadIngresoProyectoSgeRef(proyectoSgeRef)));

    boolean returnValue = repository.count(specs) > 0;

    log.debug("hasGastosOrIngresosWithProyectoSgeRefEnviados(Long proyectoId, String proyectoSgeRef) - end");
    return returnValue;
  }

  /**
   * Actualiza el proyectoSgeRef de los {@link AnualidadGasto} y
   * {@link AnualidadIngreso} del {@link Proyecto} que tengan el
   * proyectoSgeRefOld.
   * 
   * @param proyectoId        Identificador del {@link Proyecto}.
   * @param proyectoSgeRefOld Identificador del proyecto en el SGE actual.
   * @param proyectoSgeRefNew Identificador del proyecto en el SGE nuevo.
   */
  @Transactional
  public void updatePresupuestoWithProyectoSgeRef(Long proyectoId, String proyectoSgeRefOld, String proyectoSgeRefNew) {
    log.debug("updatePresupuestoWithProyectoSgeRef(String query) - start");

    List<AnualidadGasto> gastosAnualidades = anualidadGastoRepository.findAll(AnualidadGastoSpecifications
        .byProyectoId(proyectoId).and(AnualidadGastoSpecifications.byProyectoSgeRef(proyectoSgeRefOld)));
    gastosAnualidades.forEach(gastoAnualidad -> {
      gastoAnualidad.setProyectoSgeRef(proyectoSgeRefNew);
    });
    anualidadGastoRepository.saveAll(gastosAnualidades);

    List<AnualidadIngreso> ingresosAnualidades = anualidadIngresoRepository.findAll(AnualidadIngresoSpecifications
        .byProyectoId(proyectoId).and(AnualidadIngresoSpecifications.byProyectoSgeRef(proyectoSgeRefOld)));
    ingresosAnualidades.forEach(ingresoAnualidad -> {
      ingresoAnualidad.setProyectoSgeRef(proyectoSgeRefNew);
    });
    anualidadIngresoRepository.saveAll(ingresosAnualidades);

    log.debug("updatePresupuestoWithProyectoSgeRef(String query) - end");
  }

  /**
   * Actualiza el flag notificadoSge del {@link ProyectoAnualidad} con id
   * indicado.
   *
   * @param id Identificador de {@link ProyectoAnualidad}.
   * @return {@link ProyectoAnualidad} actualizado.
   */
  @Transactional
  public ProyectoAnualidad notificarSge(final Long id) {
    log.debug("notificarSge(Long id) - start");

    return repository.findById(id).map(data -> {
      data.setEnviadoSge(true);
      ProyectoAnualidad returnValue = repository.save(data);

      log.debug("notificarSge(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoAnualidadNotFoundException(id));
  }

  /**
   * Obtiene la suma de importe concedido de cada {@link AnualidadGasto}
   * asociados a un {@link Proyecto} cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link Proyecto}
   * @return suma de puntos del campo importeConcedido
   */
  public BigDecimal getTotalImporteConcedidoAnualidadGasto(Long proyectoId) {
    log.debug("getTotalImporteConcedidoAnualidadGasto(Long proyectoId) - start");

    return repository.getTotalImporteConcedidoAnualidadGasto(proyectoId);
  }

  /**
   * Obtiene la suma de importe concedido de cada {@link AnualidadGasto} de costes
   * indirectos
   * asociados a un {@link Proyecto} cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link Proyecto}
   * @return suma de puntos del campo importeConcedido
   */
  public BigDecimal getTotalImporteConcedidoAnualidadGastoCostesIndirectos(Long proyectoId) {
    log.debug("getTotalImporteConcedidoAnualidadGastoCostesIndirectos(Long proyectoId) - start");

    return repository.getTotalImporteConcedidoAnualidadGastoCostesIndirectos(proyectoId);
  }

  /**
   * Obtiene las sumas de importe concedido costes indirectos y de costes directos
   * (por separado) de una entidad {@link ProyectoAnualidad}.
   * 
   * @param id el identificador de la entidad {@link ProyectoAnualidad}
   * @return suma de importes concedidos de costes directos y suma de importes
   *         concedidos de costes indirectos
   *         {@link ProyectoAnualidadGastosTotales}
   */
  public ProyectoAnualidadGastosTotales getTotalImportesProyectoAnualidad(Long id) {
    log.debug("getTotalImportesProyectoAnualidad(Long id) - start");
    AssertHelper.idNotNull(id, ProyectoAnualidad.class);

    ProyectoAnualidadGastosTotales returnValue = repository.getTotalImportesProyectoAnualidad(id);
    log.debug("getTotalImportesProyectoAnualidad(Long id) - end");

    return returnValue;
  }

}
