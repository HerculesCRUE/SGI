package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotUpdatableException;
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
import org.crue.hercules.sgi.prc.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  @Transactional
  public ConvocatoriaBaremacion initFechasBaremacion(Long convocatoriaBaremacionId) {
    log.debug("initFechasBaremacion({},{}) - start", convocatoriaBaremacionId);

    return convocatoriaBaremacionRepository
        .findById(convocatoriaBaremacionId).map(convocatoria -> {
          convocatoria.setFechaInicioEjecucion(Instant.now());
          convocatoria.setFechaFinEjecucion(null);
          return convocatoriaBaremacionRepository.save(convocatoria);
        }).orElse(null);
  }

  @Transactional
  public ConvocatoriaBaremacion closeFechaBaremacion(Long convocatoriaBaremacionId) {
    log.debug("closeFechaBaremacion({}) - start", convocatoriaBaremacionId);

    return convocatoriaBaremacionRepository
        .findById(convocatoriaBaremacionId).map(convocatoria -> {
          convocatoria.setFechaFinEjecucion(Instant.now());
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

    AssertHelper.idNotNull(id, ConvocatoriaBaremacion.class);

    return convocatoriaBaremacionRepository.findById(id).map(convocatoriaBaremacion -> {
      if (Boolean.TRUE.equals(convocatoriaBaremacion.getActivo())) {
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

    AssertHelper.idNotNull(id, ConvocatoriaBaremacion.class);

    return convocatoriaBaremacionRepository.findById(id).map(convocatoriaBaremacion -> {
      // Una ConvocatoriaBaremacion con baremación ya realizada no se puede desactivar
      checkConvocatoriaBaremacionUpdatable(convocatoriaBaremacion);

      if (Boolean.FALSE.equals(convocatoriaBaremacion.getActivo())) {
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

    AssertHelper.idIsNull(convocatoriaBaremacion.getId(), ConvocatoriaBaremacion.class);

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
          // Una ConvocatoriaBaremacion con baremación ya realizada no se puede actualizar
          checkConvocatoriaBaremacionUpdatable(convocatoriaBaremacionExistente);

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

  /**
   * Obtiene los años en los que hay alguna {@link ConvocatoriaBaremacion}
   * 
   * @return lista de años en los hay alguna {@link ConvocatoriaBaremacion}
   */
  public List<Integer> findAniosWithConvocatoriasBaremacion() {
    log.debug("findAniosWithConvocatoriasBaremacion() - start");
    List<Integer> anios = convocatoriaBaremacionRepository.findAniosWithConvocatoriasBaremacion();
    log.debug("findAniosWithConvocatoriasBaremacion() - end");
    return anios;
  }

  private void deletePuntuacionGrupo(PuntuacionGrupo puntuacionGrupo) {
    Long puntuacionGrupoId = puntuacionGrupo.getId();
    puntuacionGrupoInvestigadorRepository.deleteInBulkByPuntuacionGrupoId(puntuacionGrupoId);
    puntuacionGrupoRepository.deleteById(puntuacionGrupoId);
  }

  /**
   * Comprueba si la {@link ConvocatoriaBaremacion} es actualizable.
   * 
   * @param id de la {@link ConvocatoriaBaremacion}
   */
  public void checkConvocatoriaBaremacionUpdatable(Long id) {
    final ConvocatoriaBaremacion convocatoriaBaremacionToCheck = findById(id);
    checkConvocatoriaBaremacionUpdatable(convocatoriaBaremacionToCheck);
  }

  private void checkConvocatoriaBaremacionUpdatable(ConvocatoriaBaremacion convocatoriaBaremacionToCheck) {
    if (!isConvocatoriaBaremacionUpdatable(convocatoriaBaremacionToCheck)) {
      throw new ConvocatoriaBaremacionNotUpdatableException();
    }
  }

  private boolean isConvocatoriaBaremacionUpdatable(ConvocatoriaBaremacion convocatoriaBaremacion) {
    return convocatoriaBaremacion.getFechaInicioEjecucion() == null;
  }
}
