package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.converter.EvaluacionConverter;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionEvaluacionException;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion.Tipo;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.specification.EvaluacionSpecifications;
import org.crue.hercules.sgi.eti.service.ComunicadosService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.EvaluadorService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.SgdocService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Evaluacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EvaluacionServiceImpl implements EvaluacionService {

  private static final String TITULO_INFORME_EVALUACION = "informeEvaluacionPdf";
  private static final String TITULO_INFORME_EVALUACION_RETROSPECTIVA = "informeEvaluacionRetrospectivaPdf";
  private static final String TITULO_INFORME_FAVORABLE = "informeFavorablePdf";
  private static final String TITULO_INFORME_FICHA_EVALUADOR = "informeFichaEvaluadorPdf";

  /** Propiedades de configuración de la aplicación */
  private final SgiConfigProperties sgiConfigProperties;

  /** Estado Memoria repository */
  private final EstadoMemoriaRepository estadoMemoriaRepository;

  /** Evaluación repository */
  private final EvaluacionRepository evaluacionRepository;

  /** Retrospectiva repository */
  private final RetrospectivaRepository retrospectivaRepository;

  /** Memoria service */
  private final MemoriaService memoriaService;

  /** Convocatoria reunión repository */
  private final ConvocatoriaReunionRepository convocatoriaReunionRepository;

  /** Comentario repository */
  private final ComentarioRepository comentarioRepository;

  /** Memoria repository */
  private final MemoriaRepository memoriaRepository;

  /** Evaluacion converter */
  private final EvaluacionConverter evaluacionConverter;

  /** Report service */
  private final SgiApiRepService reportService;

  /** SGDOC service */
  private final SgdocService sgdocService;

  /** Comunicado service */
  private final ComunicadosService comunicadosService;

  /** Evaluador service */
  private final EvaluadorService evaluadorService;

  private static final String TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA = "Investigación tutelada";

  public EvaluacionServiceImpl(EvaluacionRepository evaluacionRepository,
      EstadoMemoriaRepository estadoMemoriaRepository, RetrospectivaRepository retrospectivaRepository,
      MemoriaService memoriaService, ComentarioRepository comentarioRepository,
      ConvocatoriaReunionRepository convocatoriaReunionRepository, MemoriaRepository memoriaRepository,
      EvaluacionConverter evaluacionConverter, SgiApiRepService reportService, SgdocService sgdocService,
      ComunicadosService comunicadosService, SgiConfigProperties sgiConfigProperties,
      EvaluadorService evaluadorService) {

    this.evaluacionRepository = evaluacionRepository;
    this.estadoMemoriaRepository = estadoMemoriaRepository;
    this.retrospectivaRepository = retrospectivaRepository;
    this.memoriaService = memoriaService;
    this.convocatoriaReunionRepository = convocatoriaReunionRepository;
    this.comentarioRepository = comentarioRepository;
    this.memoriaRepository = memoriaRepository;
    this.evaluacionConverter = evaluacionConverter;
    this.reportService = reportService;
    this.sgdocService = sgdocService;
    this.comunicadosService = comunicadosService;
    this.sgiConfigProperties = sgiConfigProperties;
    this.evaluadorService = evaluadorService;
  }

  /**
   * Guarda la entidad {@link Evaluacion}.
   * 
   * Cuando se generan las evaluaciones al asignar memorias a una
   * {@link ConvocatoriaReunion} el tipo de la evaluación vendrá dado por el tipo
   * de la {@link ConvocatoriaReunion} y el estado de la {@link Memoria} o de la
   * {@link Retrospectiva}. Además será necesario actualizar el estado de la
   * {@link Memoria} o de la {@link Retrospectiva} al estado 'En evaluacion'
   * dependiendo del tipo de evaluación.
   *
   * @param evaluacion la entidad {@link Evaluacion} a guardar.
   * @return la entidad {@link Evaluacion} persistida.
   */
  @Transactional
  public Evaluacion create(Evaluacion evaluacion) {
    log.debug("Petición a create Evaluacion : {} - start", evaluacion);
    Assert.isNull(evaluacion.getId(), "Evaluacion id tiene que ser null para crear una nueva evaluacion");
    Assert.notNull(evaluacion.getConvocatoriaReunion().getId(), "La convocatoria de reunión no puede ser nula");

    if (!convocatoriaReunionRepository.existsById(evaluacion.getConvocatoriaReunion().getId())) {
      throw new ConvocatoriaReunionNotFoundException(evaluacion.getConvocatoriaReunion().getId());
    }

    if (!memoriaRepository.existsById(evaluacion.getMemoria().getId())) {
      throw new MemoriaNotFoundException(evaluacion.getMemoria().getId());
    }

    List<Memoria> memoriasAsignables = memoriaRepository
        .findAllMemoriasAsignablesConvocatoria(evaluacion.getConvocatoriaReunion().getId());
    boolean asignable = memoriasAsignables.stream()
        .anyMatch(mem -> mem.getId().equals(evaluacion.getMemoria().getId()));
    if (!asignable) {
      throw new ConvocatoriaReunionEvaluacionException();
    }

    // Si la evaluación es creada mediante la asignación de memorias en
    // ConvocatoriaReunión
    ConvocatoriaReunion crEvaluacion = convocatoriaReunionRepository
        .findById(evaluacion.getConvocatoriaReunion().getId())
        .orElseThrow(() -> new ConvocatoriaReunionNotFoundException(evaluacion.getConvocatoriaReunion().getId()));
    evaluacion.setConvocatoriaReunion(crEvaluacion);

    rellenarEvaluacionConEstadosMemoria(evaluacion);

    if (evaluacion.getTipoEvaluacion().getTipo().equals(TipoEvaluacion.Tipo.RETROSPECTIVA)) {
      retrospectivaRepository.save(evaluacion.getMemoria().getRetrospectiva());
    } else {
      estadoMemoriaRepository.save(new EstadoMemoria(null, evaluacion.getMemoria(),
          evaluacion.getMemoria().getEstadoActual(), Instant.now(), null));
    }

    memoriaService.update(evaluacion.getMemoria());

    try {
      Instant fechaEvaluacionAnterior = null;
      if (evaluacion.getVersion() > 1) {
        Optional<Evaluacion> evaluacionAnterior = evaluacionRepository
            .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(evaluacion.getMemoria().getId(),
                evaluacion.getTipoEvaluacion().getId());

        if (evaluacionAnterior.isPresent()) {
          fechaEvaluacionAnterior = evaluacionAnterior.get().getConvocatoriaReunion().getFechaEvaluacion();
        }
      }
      List<Evaluador> evaluadoresComite = this.evaluadorService.findAllByComiteSinconflictoInteresesMemoria(
          evaluacion.getConvocatoriaReunion().getComite().getId(), evaluacion.getMemoria().getId(),
          Instant.now());
      this.comunicadosService.enviarComunicadoAsignacionEvaluacion(evaluacion, evaluadoresComite,
          fechaEvaluacionAnterior);
    } catch (Exception e) {
      log.debug("enviarComunicadoAsignacionEvaluacion(evaluacionId: {}) - Error al enviar el comunicado",
          evaluacion.getId(), e);
    }

    return evaluacionRepository.save(evaluacion);
  }

  /**
   * La {@link Evaluacion#version} se incremente si la memoria tiene evaluaciones
   * anteriores (del mismo tipo) y si no se empieza con la version 1.<br/>
   * <br/>
   * 
   * Actualiza el estado de la memoria o de la retrospectiva con el estado en
   * evaluacion correspondiente al tipo de evaluacion
   * 
   * @param evaluacion una {@link Evaluacion}
   */
  private void rellenarEvaluacionConEstadosMemoria(Evaluacion evaluacion) {
    /** Se setean campos de evaluación */

    evaluacion.setActivo(true);
    evaluacion.setEsRevMinima(false);
    evaluacion.setFechaDictamen(evaluacion.getConvocatoriaReunion().getFechaEvaluacion());

    TipoEvaluacion.Tipo tipoEvaluacion = null;
    TipoEstadoMemoria.Tipo newEstadoMemoria = null;

    switch (evaluacion.getConvocatoriaReunion().getTipoConvocatoriaReunion().getTipo()) {
      case SEGUIMIENTO:
        if (evaluacion.getMemoria().getEstadoActual().getTipo()
            .equals(TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_ANUAL)) {
          tipoEvaluacion = TipoEvaluacion.Tipo.SEGUIMIENTO_ANUAL;
          newEstadoMemoria = TipoEstadoMemoria.Tipo.EN_EVALUACION_SEGUIMIENTO_ANUAL;
        } else {
          tipoEvaluacion = TipoEvaluacion.Tipo.SEGUIMIENTO_FINAL;
          newEstadoMemoria = TipoEstadoMemoria.Tipo.EN_EVALUACION_SEGUIMIENTO_FINAL;
        }

        evaluacion.getMemoria().getEstadoActual().setId(newEstadoMemoria.getId());
        evaluacion.setTipoEvaluacion(TipoEvaluacion.builder().id(tipoEvaluacion.getId()).build());
        break;
      case ORDINARIA:
      case EXTRAORDINARIA:
        if (evaluacion.getMemoria().getEstadoActual().getId() > TipoEstadoMemoria.Tipo.EN_SECRETARIA.getId()
            && evaluacion.getMemoria().getRequiereRetrospectiva().booleanValue()
            && evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getTipo()
                .equals(EstadoRetrospectiva.Tipo.EN_SECRETARIA)) {
          tipoEvaluacion = TipoEvaluacion.Tipo.RETROSPECTIVA;
          evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva()
              .setId(EstadoRetrospectiva.Tipo.EN_EVALUACION.getId());
        } else {
          tipoEvaluacion = TipoEvaluacion.Tipo.MEMORIA;
          evaluacion.getMemoria().getEstadoActual().setId(TipoEstadoMemoria.Tipo.EN_EVALUACION.getId());
        }

        evaluacion.setTipoEvaluacion(TipoEvaluacion.builder().id(tipoEvaluacion.getId()).build());
        break;
    }

    Optional<Evaluacion> evaluacionAnterior = evaluacionRepository
        .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(evaluacion.getMemoria().getId(),
            evaluacion.getTipoEvaluacion().getId());

    if (evaluacionAnterior.isPresent()) {
      evaluacion.setVersion(evaluacionAnterior.get().getVersion() + 1);
    } else {
      evaluacion.setVersion(1);
    }
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Evaluacion} paginadas y filtradas.
   */
  public Page<Evaluacion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Evaluacion> specs = EvaluacionSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Evaluacion> returnValue = evaluacionRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene la lista de evaluaciones activas de una convocatoria reunion que no
   * estan en revisión mínima.
   * 
   * @param idConvocatoriaReunion Id de {@link ConvocatoriaReunion}.
   * @param query                 información del filtro.
   * @param paging                la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<EvaluacionWithIsEliminable> findAllByConvocatoriaReunionIdAndNoEsRevMinima(Long idConvocatoriaReunion,
      String query, Pageable paging) {
    log.debug(
        "findAllByConvocatoriaReunionIdAndNoEsRevMinima(Long idConvocatoriaReunion, String query, Pageable pageable) - start");
    Specification<Evaluacion> specs = EvaluacionSpecifications.byConvocatoriaReunionId(idConvocatoriaReunion)
        .and(EvaluacionSpecifications.byEsRevMinima(false)).and(EvaluacionSpecifications.activos())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Evaluacion> returnValue = evaluacionRepository.findAll(specs, paging);

    return new PageImpl<>(evaluacionConverter.evaluacionesToEvaluacionesWithIsEliminable(returnValue.getContent()),
        paging, returnValue.getTotalElements());
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  public Page<Evaluacion> findAllByConvocatoriaReunionId(Long id, Pageable pageable) {
    log.debug("findAllByConvocatoriaReunionId(Long id, Pageable pageable) - start");
    Page<Evaluacion> returnValue = evaluacionRepository
        .findAllByConvocatoriaReunionIdAndEsRevMinimaFalse(id, pageable);
    log.debug("findAllByConvocatoriaReunionId(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  public Page<Evaluacion> findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) {
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - start");
    Page<Evaluacion> returnValue = evaluacionRepository
        .findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(id, pageable);
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} para una determinada
   * {@link Memoria}.
   *
   * @param idMemoria        Id de {@link Memoria}.
   * @param idEvaluacion     Id de {@link Evaluacion}
   * @param idTipoComentario Id de {@link TipoComentario}
   * @param pageable         la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Long idTipoComentario, Pageable pageable) {
    log.debug("findEvaluacionesAnterioresByMemoria(Long id, Pageable pageable) - start");
    Assert.notNull(idMemoria, "El id de la memoria no puede ser nulo para mostrar sus evaluaciones");
    Assert.notNull(idEvaluacion, "El id de la evaluación no puede ser nulo para recuperar las evaluaciones anteriores");
    Optional<Evaluacion> evaluacion = evaluacionRepository.findById(idEvaluacion);
    Long idTipoEvaluacion = null;
    if (evaluacion.isPresent()) {
      idTipoEvaluacion = evaluacion.get().getTipoEvaluacion().getId();
    }
    Page<EvaluacionWithNumComentario> returnValue = evaluacionRepository.findEvaluacionesAnterioresByMemoria(idMemoria,
        idEvaluacion, idTipoComentario, idTipoEvaluacion, pageable);
    log.debug("findEvaluacionesAnterioresByMemoria(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion} según su
   * {@link Evaluador}.
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<Evaluacion> findByEvaluador(String personaRef, String query, Pageable pageable) {
    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - start");
    Assert.notNull(personaRef, "El personaRef de la evaluación no puede ser nulo para mostrar sus evaluaciones");
    Page<Evaluacion> returnValue = evaluacionRepository.findByEvaluador(personaRef, query, pageable);
    log.debug("findByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene la última versión de las memorias en estado "En evaluación" o "En
   * secretaria revisión mínima", y evaluaciones de tipo retrospectiva asociadas a
   * memoria con el campo estado de retrospectiva en "En evaluación".
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Evaluacion} paginadas y filtradas.
   */
  @Override
  public Page<Evaluacion> findAllByMemoriaAndRetrospectivaEnEvaluacion(String query, Pageable paging) {
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(String query,Pageable paging) - start");

    Page<Evaluacion> returnValue = evaluacionRepository.findAllByMemoriaAndRetrospectivaEnEvaluacion(query, paging);
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluación
   * seguimiento anual" (id = 11), "En evaluación seguimiento final" (id = 12) o
   * "En secretaría seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador
   * 
   * @param personaRef Persona ref del {@link Evaluador}
   * @param query      filtro de búsqueda.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query,
      Pageable pageable) {
    log.debug("findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable) - start");
    Assert.notNull(personaRef,
        "El personaRef de la evaluación no puede ser nulo para mostrar sus evaluaciones en seguimiento");
    Page<Evaluacion> evaluaciones = evaluacionRepository.findEvaluacionesEnSeguimientosByEvaluador(personaRef, query,
        pageable);
    log.debug("findEvaluacionesEnSeguimientosByEvaluador(String personaRef, String query, Pageable pageable) - end");
    return evaluaciones;
  }

  /**
   * Obtiene una entidad {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la entidad {@link Evaluacion}.
   * @throws EvaluacionNotFoundException Si no existe ningún {@link Evaluacion} *
   *                                     con ese id.
   */
  public Evaluacion findById(final Long id) throws EvaluacionNotFoundException {
    log.debug("Petición a get Evaluacion : {}  - start -end", id);
    return evaluacionRepository.findById(id).orElseThrow(() -> new EvaluacionNotFoundException(id));
  }

  /**
   * Elimina una entidad {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   */
  @Transactional
  public void delete(Long id) throws EvaluacionNotFoundException {
    log.debug("Petición a delete Evaluacion : {}  - start", id);
    Assert.notNull(id, "El id de Evaluacion no puede ser null.");
    if (!evaluacionRepository.existsById(id)) {
      throw new EvaluacionNotFoundException(id);
    }
    evaluacionRepository.deleteById(id);
    log.debug("Petición a delete Evaluacion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Evaluacion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Evaluacion: {} - start");
    evaluacionRepository.deleteAll();
    log.debug("Petición a deleteAll de Evaluacion: {} - end");

  }

  /**
   * Actualiza los datos del {@link Evaluacion}.
   * 
   * @param evaluacionActualizar {@link Evaluacion} con los datos actualizados.
   * @return El {@link Evaluacion} actualizado.
   * @throws EvaluacionNotFoundException Si no existe ningún {@link Evaluacion}
   *                                     con ese id.
   * @throws IllegalArgumentException    Si el {@link Evaluacion} no tiene id.
   */

  @Transactional
  public Evaluacion update(final Evaluacion evaluacionActualizar) {
    log.debug("update(Evaluacion evaluacionActualizar) - start");

    Assert.notNull(evaluacionActualizar.getId(), "Evaluacion id no puede ser null para actualizar una evaluacion");

    // Si la Evaluación es de Revisión Mínima
    // se actualiza la fechaDictamen con la fecha actual
    if (evaluacionActualizar.getEsRevMinima().booleanValue()) {
      evaluacionActualizar.setFechaDictamen(Instant.now());
    }

    // Si es una evaluacion de revision minima con dictamen se actualiza el estado
    // de la memoria
    if (evaluacionActualizar.getDictamen() != null && evaluacionActualizar.getEsRevMinima().booleanValue()) {
      switch (evaluacionActualizar.getDictamen().getTipo()) {
        // Si el dictamen es "Favorable" se cambia al fin de evaluacion correspondiente
        case FAVORABLE:
        case FAVORABLE_RETROSPECTIVA:
        case FAVORABLE_SEGUIMIENTO_ANUAL:
        case FAVORABLE_SEGUIMIENTO_FINAL:
          switch (evaluacionActualizar.getMemoria().getEstadoActual().getTipo()) {
            // memoria
            case EN_EVALUACION:
            case EN_SECRETARIA_REVISION_MINIMA:
              memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(),
                  TipoEstadoMemoria.Tipo.FIN_EVALUACION.getId());
              break;
            // seguimiento anual
            case EN_EVALUACION_SEGUIMIENTO_ANUAL:
            case EN_SECRETARIA_SEGUIMIENTO_ANUAL_MODIFICACION:
              memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(),
                  TipoEstadoMemoria.Tipo.FIN_EVALUACION_SEGUIMIENTO_ANUAL.getId());
              break;
            // seguimiento final
            case EN_EVALUACION_SEGUIMIENTO_FINAL:
            case EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES:
              memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(),
                  TipoEstadoMemoria.Tipo.FIN_EVALUACION_SEGUIMIENTO_FINAL.getId());
              break;
            default:
              break;
          }
          break;
        case SOLICITUD_MODIFICACIONES:
          if (evaluacionActualizar.getMemoria().getEstadoActual().getTipo()
              .equals(TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_ANUAL_MODIFICACION)) {
            memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(),
                TipoEstadoMemoria.Tipo.SOLICITUD_MODIFICACION_SEGUIMIENTO_ANUAL.getId());
          }
          break;
        case FAVORABLE_PENDIENTE_REVISION_MINIMA:
          memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(),
              TipoEstadoMemoria.Tipo.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS.getId());
          break;
        case SOLICITUD_ACLARACIONES_SEGUIMIENTO_FINAL:
          memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(),
              Constantes.ESTADO_MEMORIA_EN_ACLARACION_SEGUIMIENTO_FINAL);
          break;
        default:
          log.info(
              "update(evaluacionActualizarId: {}) - No se hace ningun cambio para el dictamen: {}",
              evaluacionActualizar.getId(), evaluacionActualizar.getDictamen().getId());
          break;
      }
    }

    return evaluacionRepository.findById(evaluacionActualizar.getId()).map(evaluacion -> {
      evaluacion.setDictamen(evaluacionActualizar.getDictamen());
      evaluacion.setEsRevMinima(evaluacionActualizar.getEsRevMinima());
      evaluacion.setFechaDictamen(evaluacionActualizar.getFechaDictamen());
      evaluacion.setMemoria(evaluacionActualizar.getMemoria());
      evaluacion.setConvocatoriaReunion(evaluacionActualizar.getConvocatoriaReunion());
      evaluacion.setActivo(evaluacionActualizar.getActivo());
      evaluacion.setTipoEvaluacion(evaluacionActualizar.getTipoEvaluacion());
      evaluacion.setEvaluador1(evaluacionActualizar.getEvaluador1());
      evaluacion.setEvaluador2(evaluacionActualizar.getEvaluador2());
      evaluacion.setComentario(evaluacionActualizar.getComentario());

      Evaluacion returnValue = evaluacionRepository.save(evaluacion);

      if (returnValue.getEsRevMinima().booleanValue()) {
        // Se envía comunicado para evaluación con Dictamen de evaluación de seguimiento
        // de memoria de revisión mínima disponible
        if (returnValue.getDictamen().getTipoEvaluacion() != null
            && (returnValue.getDictamen().getTipoEvaluacion().getTipo() == Tipo.SEGUIMIENTO_ANUAL
                || returnValue.getDictamen().getTipoEvaluacion().getTipo() == Tipo.SEGUIMIENTO_FINAL)) {
          sendComunicadoDictamenEvaluacionSeguimientoRevMin(evaluacion);
          // Se envía comunicado para evaluación con dictamen de evaluación de revisión
          // mínima disponible
        } else {
          sendComunicadoDictamenEvaluacionRevMin(evaluacion);
        }
      }

      log.debug("update(Evaluacion evaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionActualizar.getId()));
  }

  @Transactional
  @Override
  public DocumentoOutput generarDocumentoEvaluacion(Long idEvaluacion) {
    Evaluacion evaluacion = this.findById(idEvaluacion);
    DocumentoOutput documento = null;
    switch (evaluacion.getTipoEvaluacion().getTipo()) {
      case MEMORIA:
        documento = this.getDocumentoByTipoEvaluacionMemoria(evaluacion);
        break;
      case SEGUIMIENTO_ANUAL:
        if (evaluacion.getDictamen() != null
            && evaluacion.getDictamen().getId().equals(Dictamen.Tipo.SOLICITUD_MODIFICACIONES.getId())) {
          documento = this.generarDocumento(evaluacion, false);
        }
        break;
      case SEGUIMIENTO_FINAL:
        if (evaluacion.getDictamen() != null && (evaluacion.getDictamen().getId()
            .equals(Dictamen.Tipo.SOLICITUD_ACLARACIONES_SEGUIMIENTO_FINAL.getId()))) {
          documento = this.generarDocumento(evaluacion, false);
        }
        break;
      case RETROSPECTIVA:
      default:
        if (evaluacion.getDictamen() != null
            && (evaluacion.getDictamen().getId().equals(Dictamen.Tipo.FAVORABLE.getId())
                || evaluacion.getDictamen().getId().equals(Dictamen.Tipo.FAVORABLE_RETROSPECTIVA.getId()))) {
          documento = this.generarDocumento(evaluacion, true);
        }
        break;
    }
    return documento;
  }

  private DocumentoOutput getDocumentoByTipoEvaluacionMemoria(Evaluacion evaluacion) {
    boolean isFavorable = (evaluacion.getDictamen() != null
        && (evaluacion.getDictamen().getId().equals(Dictamen.Tipo.FAVORABLE.getId())));

    return this.generarDocumento(evaluacion, isFavorable);
  }

  private DocumentoOutput generarDocumento(Evaluacion evaluacion, Boolean favorable) {
    log.debug("generarDocumento(Evaluacion evaluacion, Boolean favorable)- start");

    Resource informePdf = null;
    String tituloInforme = "";
    if (favorable.booleanValue()) {
      // Se obtiene el informe favorable en formato pdf creado mediante el
      // servicio de reporting
      if (Objects.equals(evaluacion.getTipoEvaluacion().getTipo(), TipoEvaluacion.Tipo.RETROSPECTIVA)) {
        informePdf = reportService.getInformeEvaluacionRetrospectiva(evaluacion.getId(), Instant.now());
        tituloInforme = TITULO_INFORME_EVALUACION_RETROSPECTIVA;
      } else {
        switch (evaluacion.getMemoria().getTipoMemoria().getTipo()) {
          case NUEVA:
            informePdf = reportService.getInformeFavorableMemoria(evaluacion.getId());
            break;
          case MODIFICACION:
            informePdf = reportService.getInformeFavorableModificacion(evaluacion.getId());
            break;
          case RATIFICACION:
            informePdf = reportService.getInformeFavorableRatificacion(evaluacion.getId());
            break;
          default:
            break;
        }
        tituloInforme = TITULO_INFORME_FAVORABLE;
      }
    } else {
      // Se obtiene el informe de evaluación en formato pdf creado mediante el
      // servicio de reporting
      informePdf = reportService.getInformeEvaluacion(evaluacion.getId());
      tituloInforme = TITULO_INFORME_EVALUACION;
    }

    // Se sube el informe a sgdoc
    String fileName = tituloInforme + "_" + evaluacion.getId() + LocalDate.now() + ".pdf";
    DocumentoOutput documento = sgdocService.uploadInforme(fileName, informePdf);
    log.debug("generarDocumento(Evaluacion evaluacion, Boolean favorable)- end");
    return documento;
  }

  /**
   * Obtiene el documento de la ficha del Evaluador
   * 
   * @param idEvaluacion id {@link Evaluacion}
   * @return El documento del informe de la ficha del Evaluador
   */
  @Override
  public DocumentoOutput generarDocumentoEvaluador(Long idEvaluacion) {
    Resource informePdf = reportService.getInformeEvaluador(idEvaluacion);
    // Se sube el informe a sgdoc
    String fileName = TITULO_INFORME_FICHA_EVALUADOR + "_" + idEvaluacion + LocalDate.now() + ".pdf";
    return sgdocService.uploadInforme(fileName, informePdf);
  }

  /**
   * Obtiene la última versión de las memorias en estado "En evaluación
   * seguimiento anual" o "En evaluación seguimiento final" o "En secretaría
   * seguimiento final aclaraciones" .
   * 
   * @param pageable la información de paginación.
   * @param query    información del filtro.
   * @return el listado de entidades {@link Evaluacion} paginadas y filtradas.
   */

  @Override
  public Page<Evaluacion> findByEvaluacionesEnSeguimientoAnualOrFinal(String query, Pageable pageable) {
    log.debug("findByEvaluacionesEnSeguimientoAnualOrFinal(String query,Pageable paging) - start");

    Page<Evaluacion> returnValue = evaluacionRepository.findByEvaluacionesEnSeguimientoAnualOrFinal(query, pageable);
    log.debug("findByEvaluacionesEnSeguimientoAnualOrFinal(String query,Pageable paging) - end");

    return returnValue;
  }

  /**
   * Elimina las memorias asignadas a una convocatoria de reunión
   * 
   * @param idConvocatoriaReunion id de la {@link ConvocatoriaReunion}
   * @param idEvaluacion          id de la {@link Evaluacion}
   */
  @Override
  @Transactional
  public void deleteEvaluacion(Long idConvocatoriaReunion, Long idEvaluacion) {
    log.debug("deleteEvaluacion(Long idConvocatoriaReunion, Long idEvaluacion) - start");

    Evaluacion evaluacion = evaluacionRepository.findById(idEvaluacion)
        .orElseThrow(() -> new EvaluacionNotFoundException(idEvaluacion));

    Assert.isTrue(evaluacion.getConvocatoriaReunion().getId().compareTo(idConvocatoriaReunion) == 0,
        "La evaluación no pertenece a esta convocatoria de reunión");

    Assert.isTrue(evaluacion.getConvocatoriaReunion().getFechaEvaluacion().isAfter(Instant.now()),
        "La fecha de la convocatoria es anterior a la actual");

    Assert.isNull(evaluacion.getDictamen(), "No se pueden eliminar memorias que ya contengan un dictamen");

    Assert.isTrue(comentarioRepository.countByEvaluacionId(evaluacion.getId()) == 0L,
        "No se puede eliminar una memoria que tenga comentarios asociados");

    // Volvemos la memoria a su estado anterior
    Memoria memoria = memoriaService.getMemoriaWithEstadoAnterior(evaluacion.getMemoria());

    memoriaRepository.save(memoria);
    evaluacion.setActivo(Boolean.FALSE);
    evaluacionRepository.save(evaluacion);

    log.debug("deleteEvaluacion(Long idConvocatoriaReunion, Long idEvaluacion) - end");
  }

  @Override
  public Page<Evaluacion> findAllByMemoriaId(Long id, Pageable pageable) {
    log.debug("findAllByMemoriaId(Long id,Pageable paging) - start");

    Assert.notNull(id, "El id de la memoria no puede ser nulo para mostrar sus evaluaciones");

    return memoriaRepository.findByIdAndActivoTrue(id).map(memoria -> {

      Specification<Evaluacion> specMemoriaId = EvaluacionSpecifications.memoriaId(id);

      Specification<Evaluacion> specEvaluacionActiva = EvaluacionSpecifications.activos();

      Specification<Evaluacion> specs = Specification.where(specMemoriaId).and(specEvaluacionActiva);

      Page<Evaluacion> returnValue = evaluacionRepository.findAll(specs, pageable);

      log.debug("findAllByMemoriaId(Long id,Pageable paging) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(id));

  }

  /**
   * Identifica si el usuario es {@link Evaluador} en alguna {@link Evaluacion}
   * 
   * @param personaRef El usuario de la petición
   * @return true/false
   */
  @Override
  public Boolean hasAssignedEvaluacionesByEvaluador(String personaRef) {
    log.debug("hasAssignedEvaluacionesByEvaluador(String personaRef) - end");
    return evaluacionRepository.hasAssignedEvaluacionesByEvaluador(personaRef);
  }

  /**
   * Identifica si el usuario es {@link Evaluador} en alguna {@link Evaluacion} en
   * Seguimiento
   * 
   * @param personaRef El usuario de la petición
   * @return true/false
   */
  @Override
  public Boolean hasAssignedEvaluacionesSeguimientoByEvaluador(String personaRef) {
    log.debug("hasAssignedEvaluacionesSeguimientoByEvaluador(String personaRef) - end");
    return evaluacionRepository.hasAssignedEvaluacionesSeguimientoByEvaluador(personaRef);
  }

  /**
   * Identifica si el usuario es {@link Evaluador} en la {@link Evaluacion}
   * 
   * @param idEvaluacion identificador de la {@link Evaluacion}
   * @param personaRef   El usuario de la petición
   * @return true/false
   */
  @Override
  public Boolean isEvaluacionEvaluableByEvaluador(Long idEvaluacion, String personaRef) {
    log.debug("isEvaluacionEvaluableByEvaluador(Long idEvaluacion, String personaRef) - end");
    return evaluacionRepository.isEvaluacionEvaluableByEvaluador(idEvaluacion, personaRef);
  }

  /**
   * Identifica si el usuario es {@link Evaluador} en alguna {@link Evaluacion} en
   * Seguimiento
   * 
   * @param idEvaluacion identificador de la {@link Evaluacion} en Seguimiento
   * @param personaRef   El usuario de la petición
   * @return true/false
   */
  @Override
  public Boolean isEvaluacionSeguimientoEvaluableByEvaluador(Long idEvaluacion, String personaRef) {
    log.debug("isEvaluacionSeguimientoEvaluableByEvaluador(Long idEvaluacion, String personaRef) - end");
    return evaluacionRepository.isEvaluacionSeguimientoEvaluableByEvaluador(idEvaluacion, personaRef);
  }

  /**
   * Retorna el identificador de la usuarioRef del presidente
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return id del presidente
   */
  @Override
  public String findIdPresidenteByIdEvaluacion(Long idEvaluacion) {
    log.debug("findIdPresidenteByIdEvaluacion(String idEvaluacion) - end");
    return evaluacionRepository.findIdPresidenteByIdEvaluacion(idEvaluacion);
  }

  /**
   * Retorna la primera fecha de envío a secretaría (histórico estado)
   * 
   * @param idEvaluacion Id de {@link Evaluacion}.
   * @return fecha de envío a secretaría
   */
  @Override
  public Instant findFirstFechaEnvioSecretariaByIdEvaluacion(Long idEvaluacion) {
    log.debug("findFirstFechaEnvioSecretariaByIdEvaluacion(String idEvaluacion) - end");
    return evaluacionRepository.findFirstFechaEnvioSecretariaByIdEvaluacion(idEvaluacion);
  }

  private void sendComunicadoDictamenEvaluacionRevMin(Evaluacion evaluacion) {
    log.debug("sendComunicadoDictamenEvaluacionRevMin(Evaluacion evaluacion) - Start");
    try {
      String tipoActividad;
      if (!evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre()
          .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
        tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre();
      } else {
        tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
      }
      this.comunicadosService.enviarComunicadoDictamenEvaluacionRevMinima(
          evaluacion.getMemoria().getComite().getNombreInvestigacion(),
          evaluacion.getMemoria().getComite().getGenero().toString(), evaluacion.getMemoria().getNumReferencia(),
          tipoActividad,
          evaluacion.getMemoria().getPeticionEvaluacion().getTitulo(),
          evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef());
      log.debug("sendComunicadoDictamenEvaluacionRevMin(Evaluacion evaluacion) - End");
    } catch (Exception e) {
      log.error("sendComunicadoDictamenEvaluacionRevMin(evaluacionId: {}) - Error al enviar el comunicado",
          evaluacion.getId(), e);
    }
  }

  private void sendComunicadoDictamenEvaluacionSeguimientoRevMin(Evaluacion evaluacion) {
    log.debug("sendComunicadoDictamenEvaluacionSeguimientoRevMin(Evaluacion evaluacion) - Start");
    try {
      String tipoActividad;
      if (!evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre()
          .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
        tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre();
      } else {
        tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
      }
      this.comunicadosService.enviarComunicadoDictamenEvaluacionSeguimientoRevMinima(
          evaluacion.getMemoria().getComite().getNombreInvestigacion(),
          evaluacion.getMemoria().getComite().getGenero().toString(), evaluacion.getMemoria().getNumReferencia(),
          tipoActividad,
          evaluacion.getMemoria().getPeticionEvaluacion().getTitulo(),
          evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef());
      log.debug("sendComunicadoDictamenEvaluacionSeguimientoRevMin(Evaluacion evaluacion) - End");
    } catch (Exception e) {
      log.error(
          "sendComunicadoDictamenEvaluacionSeguimientoRevMin(evaluacionId: {}) - Error al enviar el comunicado",
          evaluacion.getId(), e);
    }
  }

  /**
   * Permite enviar el comunicado de {@link Evaluacion}
   *
   * @param idEvaluacion Id del {@link Evaluacion}.
   * @return true si puede ser enviado / false si no puede ser enviado
   */
  @Override
  @Transactional
  public Boolean enviarComunicado(Long idEvaluacion) {
    log.debug("enviarComunicado(Long idEvaluacion) - start");
    Evaluacion evaluacion = this.findById(idEvaluacion);
    try {
      this.comunicadosService.enviarComunicadoCambiosEvaluacionEti(evaluacion.getMemoria().getComite().getComite(),
          evaluacion.getMemoria().getComite().getNombreInvestigacion(), evaluacion.getMemoria().getNumReferencia(),
          evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());
      log.debug("enviarComunicado(Long idEvaluacion) - end");
      return true;
    } catch (JsonProcessingException e) {
      log.debug("Error - enviarComunicado(Long idEvaluacion)", e);
      return false;
    }
  }

  public void sendComunicadoInformeSeguimientoAnualPendiente() {
    List<Evaluacion> evaluaciones = recuperaInformesAvisoSeguimientoAnualPendiente();
    if (CollectionUtils.isEmpty(evaluaciones)) {
      log.info("No existen evaluaciones que requieran generar aviso de informe de evaluación anual pendiente.");
    } else {
      evaluaciones.stream().forEach(evaluacion -> {
        String tipoActividad;
        if (!evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre()
            .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
          tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre();
        } else {
          tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
        }
        try {
          this.comunicadosService.enviarComunicadoInformeSeguimientoAnual(
              evaluacion.getMemoria().getComite().getNombreInvestigacion(),
              evaluacion.getMemoria().getNumReferencia(),
              tipoActividad,
              evaluacion.getMemoria().getPeticionEvaluacion().getTitulo(),
              evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef());
        } catch (Exception e) {
          log.error("sendComunicadoInformeSeguimientoAnualPendiente(evaluacionId: {}) - Error al enviar el comunicado",
              evaluacion.getId(), e);
        }
      });
    }

  }

  /**
   * Recuperar aquellas evaluaciones que tienen informe de seguimiento anual
   * pendiente
   * 
   * @return lista de {@link Evaluacion}
   */
  public List<Evaluacion> recuperaInformesAvisoSeguimientoAnualPendiente() {
    log.debug("recuperaInformesAvisoSeguimientoAnualPendiente() - start");

    Instant fechaInicio = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MIN).withNano(0).minusYears(1L).toInstant();

    Instant fechaFin = this.getLastInstantOfDay().minusYears(1L)
        .toInstant();

    // Se buscan evaluaciones activas con Memoria con estado Fin Evaluacion (9) y
    // Dictamen con estado Favorable(1) cuya fecha de dictamen cumpla un año durante
    // el día de hoy
    Specification<Evaluacion> specsEvaluacionByYearAvisoInformeAnualAnd = EvaluacionSpecifications.activos()
        .and(EvaluacionSpecifications.byMemoriaEstado(9L))
        .and(EvaluacionSpecifications.byDictamenEstado(1L))
        .and(EvaluacionSpecifications.byFechaDictamenBetween(fechaInicio, fechaFin));

    List<Evaluacion> evaluacionesPendientesAviso = evaluacionRepository
        .findAll(specsEvaluacionByYearAvisoInformeAnualAnd);

    log.debug("recuperaInformesAvisoSeguimientoAnualPendiente() - end");
    return evaluacionesPendientesAviso;
  }

  /**
   * Obtiene el secretario activo de la fecha de evaluación
   * 
   * @param idEvaluacion id de la {@link Evaluacion}
   * @return secretario de la evaluación
   */
  public Evaluador findSecretarioEvaluacion(Long idEvaluacion) {
    log.debug("findSecretarioEvaluacion(Long idEvaluacion) - start");
    Evaluacion evaluacion = this.findById(idEvaluacion);
    Evaluador secretario = evaluadorService.findSecretarioInFechaAndComite(
        evaluacion.getConvocatoriaReunion().getFechaEvaluacion(),
        evaluacion.getConvocatoriaReunion().getComite().getComite());
    log.debug("findSecretarioEvaluacion(Long idEvaluacion) - end");
    return secretario;
  }

  private ZonedDateTime getLastInstantOfDay() {
    return Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).withNano(0);
  }

  @Override
  public Evaluacion getLastEvaluacionMemoria(Long memoriaId) {
    log.debug("getLastEvaluacionMemoria(Long memoriaId) - start");
    Evaluacion evaluacion = evaluacionRepository
        .findFirstByMemoriaIdAndActivoTrueOrderByVersionDescCreationDateDesc(memoriaId)
        .orElse(null);
    log.debug("getLastEvaluacionMemoria(Long memoriaId) - end");
    return evaluacion;
  }

  @Override
  public boolean isLastEvaluacionMemoriaPendienteCorrecciones(Long memoriaId) {
    log.debug("isLastEvaluacionMemoriaPendienteCorrecciones(Long memoriaId) - start");
    boolean isPendienteCorrecciones = evaluacionRepository.isLastEvaluacionMemoriaPendienteCorrecciones(memoriaId);
    log.debug("isLastEvaluacionMemoriaPendienteCorrecciones(Long memoriaId) - end");
    return isPendienteCorrecciones;
  }

}
