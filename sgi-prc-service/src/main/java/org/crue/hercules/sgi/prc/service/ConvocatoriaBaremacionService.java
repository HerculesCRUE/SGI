package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion.OnActivar;
import org.crue.hercules.sgi.prc.model.Modulador;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.ModuladorRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.RangoRepository;
import org.crue.hercules.sgi.prc.repository.specification.ConvocatoriaBaremacionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para ConvocatoriaBaremacion
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class ConvocatoriaBaremacionService {

  private static final String ENTITY = "entity";
  private static final String FIELD = "field";
  private final ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;
  private final ModuladorRepository moduladorRepository;
  private final RangoRepository rangoRepository;
  private final BaremoRepository baremoRepository;
  private final ConfiguracionRepository configuracionRepository;
  private final PuntuacionGrupoRepository puntuacionGrupoRepository;
  private final PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;
  private final ProduccionCientificaRepository produccionCientificaRepository;
  private final ProduccionCientificaBuilderService produccionCientificaBuilderService;

  private final SgiConfigProperties sgiConfigProperties;
  private final Validator validator;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ConvocatoriaBaremacion updateFechaInicioEjecucion(Long convocatoriaBaremacionId,
      Instant fechaInicioEjecucion) {
    log.debug("updateFechaInicioEjecucion(convocatoriaBaremacionId, fechaInicioEjecucion) - start");

    return convocatoriaBaremacionRepository
        .findById(convocatoriaBaremacionId).map(convocatoria -> {
          convocatoria.setFechaInicioEjecucion(fechaInicioEjecucion);
          return convocatoriaBaremacionRepository.save(convocatoria);
        }).orElse(null);
  }

  /**
   * Clona una convocatoria estableciendo un prefijo al nombre y sumándole el
   * número de años pasados como parámetro
   * 
   * @param convocatoriaBaremacionId Id de {@link ConvocatoriaBaremacion}
   * @param prefixNombre             Prefijo a añadir al nombre de la nueva
   *                                 {@link ConvocatoriaBaremacion}
   * @param aniosAdd                 Años a añadir al año de la nueva
   *                                 {@link ConvocatoriaBaremacion}
   * @return nueva {@link ConvocatoriaBaremacion}
   */
  @Transactional
  public ConvocatoriaBaremacion clone(Long convocatoriaBaremacionId, String prefixNombre, Integer aniosAdd) {
    log.debug("clone(convocatoriaBaremacionId, prefixNombre, anioAdded) - start");

    return convocatoriaBaremacionRepository
        .findById(convocatoriaBaremacionId).map(convocatoria -> {
          ConvocatoriaBaremacion convocatoriaBaremacionClone = ConvocatoriaBaremacion.builder()
              .nombre(prefixNombre + convocatoria.getNombre())
              .ultimoAnio(convocatoria.getUltimoAnio())
              .anio(convocatoria.getAnio() + aniosAdd)
              .aniosBaremables(convocatoria.getAniosBaremables())
              .importeTotal(convocatoria.getImporteTotal())
              .partidaPresupuestaria(convocatoria.getPartidaPresupuestaria())
              .build();

          ConvocatoriaBaremacion convocatoriaBaremacionNew = convocatoriaBaremacionRepository
              .save(convocatoriaBaremacionClone);
          Long convocatoriaBaremacionIdNew = convocatoriaBaremacionNew.getId();

          baremoRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
              .forEach(baremo -> cloneBaremo(convocatoriaBaremacionIdNew, baremo));

          moduladorRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
              .forEach(modulador -> cloneModulador(convocatoriaBaremacionIdNew, modulador));

          rangoRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
              .forEach(rango -> cloneRango(convocatoriaBaremacionIdNew, rango));

          return convocatoriaBaremacionClone;
        }).orElseThrow(() -> new ConvocatoriaBaremacionNotFoundException(convocatoriaBaremacionId));
  }

  private void cloneRango(Long convocatoriaBaremacionIdNew, Rango rango) {
    Rango rangoClone = Rango.builder()
        .convocatoriaBaremacionId(convocatoriaBaremacionIdNew)
        .tipoRango(rango.getTipoRango())
        .tipoTemporalidad(rango.getTipoTemporalidad())
        .desde(rango.getDesde())
        .hasta(rango.getHasta())
        .puntos(rango.getPuntos())
        .build();
    rangoRepository.save(rangoClone);
  }

  private void cloneModulador(Long convocatoriaBaremacionIdNew, Modulador modulador) {
    Modulador moduladorClone = Modulador.builder()
        .convocatoriaBaremacionId(convocatoriaBaremacionIdNew)
        .areaRef(modulador.getAreaRef())
        .tipo(modulador.getTipo())
        .valor1(modulador.getValor1())
        .valor2(modulador.getValor2())
        .valor3(modulador.getValor3())
        .valor4(modulador.getValor4())
        .valor5(modulador.getValor5())
        .build();
    moduladorRepository.save(moduladorClone);
  }

  private void cloneBaremo(Long convocatoriaBaremacionIdNew, Baremo baremo) {
    Baremo baremoClone = Baremo.builder()
        .configuracionBaremoId(baremo.getConfiguracionBaremoId())
        .convocatoriaBaremacionId(convocatoriaBaremacionIdNew)
        .peso(baremo.getPeso())
        .cuantia(baremo.getCuantia())
        .tipoCuantia(baremo.getTipoCuantia())
        .puntos(baremo.getPuntos())
        .build();
    baremoRepository.save(baremoClone);
  }

  /**
   * Obtener todas las entidades {@link ConvocatoriaBaremacion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConvocatoriaBaremacion} paginadas y/o
   *         filtradas.
   */
  public Page<ConvocatoriaBaremacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ConvocatoriaBaremacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ConvocatoriaBaremacion> returnValue = convocatoriaBaremacionRepository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ConvocatoriaBaremacion} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaBaremacion}.
   * @return la entidad {@link ConvocatoriaBaremacion}.
   */
  public ConvocatoriaBaremacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaBaremacion returnValue = convocatoriaBaremacionRepository.findById(id)
        .orElseThrow(() -> new ConvocatoriaBaremacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Activa la {@link ConvocatoriaBaremacion}.
   *
   * @param id Id de la {@link ConvocatoriaBaremacion}.
   * @return la entidad {@link ConvocatoriaBaremacion} persistida.
   */
  @Transactional
  public ConvocatoriaBaremacion activar(Long id) {
    log.debug("activar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter(FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(ConvocatoriaBaremacion.class)).build());

    return convocatoriaBaremacionRepository.findById(id).map(convocatoriaBaremacion -> {
      if (convocatoriaBaremacion.getActivo()) {
        log.debug("enable(Long id) - end");
        // Si esta activo no se hace nada
        return convocatoriaBaremacion;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<ConvocatoriaBaremacion>> result = validator.validate(convocatoriaBaremacion,
          OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      convocatoriaBaremacion.setActivo(true);

      ConvocatoriaBaremacion returnValue = convocatoriaBaremacionRepository.save(convocatoriaBaremacion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaBaremacionNotFoundException(id));
  }

  /**
   * Desactiva la {@link ConvocatoriaBaremacion}.
   *
   * @param id Id de la {@link ConvocatoriaBaremacion}.
   * @return la entidad {@link ConvocatoriaBaremacion} persistida.
   */
  @Transactional
  public ConvocatoriaBaremacion desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter(FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(ConvocatoriaBaremacion.class)).build());

    return convocatoriaBaremacionRepository.findById(id).map(convocatoriaBaremacion -> {
      // Una ConvocatoriaBaremacion con baremación ya realizada no se puede desactivar
      Assert.isNull(convocatoriaBaremacion.getFechaInicioEjecucion(),
          // Defer message resolution untill is needed
          () -> ProblemMessage.builder().key(Assert.class, "isNull")
              .parameter(FIELD, ApplicationContextSupport.getMessage("fechaInicioEjecucion"))
              .parameter(ENTITY, ApplicationContextSupport.getMessage(ConvocatoriaBaremacion.class)).build());

      if (!convocatoriaBaremacion.getActivo()) {
        log.debug("desactivar(Long id) - end");
        // Si no esta activo no se hace nada
        return convocatoriaBaremacion;
      }

      convocatoriaBaremacion.setActivo(false);

      ConvocatoriaBaremacion returnValue = convocatoriaBaremacionRepository.save(convocatoriaBaremacion);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaBaremacionNotFoundException(id));
  }

  /**
   * Resetea las {@link ConvocatoriaBaremacion} que han iniciado la baremación
   * pero han superado el tiempo de finalización
   */
  @Transactional
  public void reset() {
    log.debug("reset() - start");

    Integer numHoras = configuracionRepository.findAll().get(0).getHorasProcesoBaremacion();

    Instant fechaLimiteBaremacion = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant()
        .minus(numHoras, ChronoUnit.HOURS);

    Specification<ConvocatoriaBaremacion> specs = ConvocatoriaBaremacionSpecifications
        .isResettable(fechaLimiteBaremacion);

    convocatoriaBaremacionRepository.findAll(specs).stream().forEach(this::resetDatesConvocatoria);

    log.debug("reset() - end");
  }

  @Transactional
  public void resetDatesConvocatoria(ConvocatoriaBaremacion convocatoriaBaremacion) {
    convocatoriaBaremacion.setFechaInicioEjecucion(null);
    convocatoriaBaremacion.setFechaFinEjecucion(null);
    convocatoriaBaremacionRepository.save(convocatoriaBaremacion);

    this.deleteItemsConvocatoriaBaremacion(convocatoriaBaremacion);
  }

  @Transactional
  public void deleteItemsConvocatoriaBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) {
    Long convocatoriaBaremacionId = convocatoriaBaremacion.getId();
    puntuacionGrupoRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
        .forEach(this::deletePuntuacionGrupo);

    produccionCientificaRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId)
        .forEach(produccionCientificaBuilderService::deleteProduccionCientifica);
  }

  /*
   * Guardar un nuevo {@link ConvocatoriaBaremacion}.
   *
   * @param convocatoriaBaremacion la entidad {@link ConvocatoriaBaremacion} a
   * guardar.
   * 
   * @return la entidad {@link ConvocatoriaBaremacion} persistida.
   */
  @Transactional
  @Validated({ ConvocatoriaBaremacion.OnCrear.class })
  public ConvocatoriaBaremacion create(@Valid ConvocatoriaBaremacion convocatoriaBaremacion) {
    log.debug("create(ConvocatoriaBaremacion convocatoriaBaremacion) - start");

    Assert.isNull(convocatoriaBaremacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ConvocatoriaBaremacion.class)).build());

    convocatoriaBaremacion.setActivo(true);
    ConvocatoriaBaremacion returnValue = convocatoriaBaremacionRepository.save(convocatoriaBaremacion);

    log.debug("create(ConvocatoriaBaremacion convocatoriaBaremacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaBaremacion}.
   *
   * @param convocatoriaBaremacion la entidad {@link ConvocatoriaBaremacion} a
   *                               actualizar.
   * @return la entidad {@link ConvocatoriaBaremacion} persistida.
   */
  @Transactional
  @Validated({ ConvocatoriaBaremacion.OnActualizar.class })
  public ConvocatoriaBaremacion update(@Valid ConvocatoriaBaremacion convocatoriaBaremacion) {
    log.debug("update(ConvocatoriaBaremacion convocatoriaBaremacion) - start");

    return convocatoriaBaremacionRepository.findById(convocatoriaBaremacion.getId())
        .map(convocatoriaBaremacionExistente -> {
          // Establecemos los campos actualizables con los recibidos
          convocatoriaBaremacionExistente.setAnio(convocatoriaBaremacion.getAnio());
          convocatoriaBaremacionExistente.setAniosBaremables(convocatoriaBaremacion.getAniosBaremables());
          convocatoriaBaremacionExistente.setImporteTotal(convocatoriaBaremacion.getImporteTotal());
          convocatoriaBaremacionExistente.setNombre(convocatoriaBaremacion.getNombre());
          convocatoriaBaremacionExistente.setPartidaPresupuestaria(convocatoriaBaremacion.getPartidaPresupuestaria());
          convocatoriaBaremacionExistente.setUltimoAnio(convocatoriaBaremacion.getUltimoAnio());

          // Actualizamos la entidad
          ConvocatoriaBaremacion returnValue = convocatoriaBaremacionRepository.save(convocatoriaBaremacionExistente);
          log.debug("update(ConvocatoriaBaremacion convocatoriaBaremacion) - end");
          return returnValue;
        }).orElseThrow(() -> new ConvocatoriaBaremacionNotFoundException(convocatoriaBaremacion.getId()));
  }

  private void deletePuntuacionGrupo(PuntuacionGrupo puntuacionGrupo) {
    Long puntuacionGrupoId = puntuacionGrupo.getId();
    puntuacionGrupoInvestigadorRepository.deleteInBulkByPuntuacionGrupoId(puntuacionGrupoId);
    puntuacionGrupoRepository.deleteById(puntuacionGrupoId);
  }

}
