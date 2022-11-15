package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.converter.EvaluacionConverter;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionEvaluacionException;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoComentario;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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

  @Autowired
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

    Evaluacion evaluacionCompleta = rellenarEvaluacionConEstadosMemoria(evaluacion);

    if (evaluacionCompleta.getTipoEvaluacion().getId().compareTo(Constantes.TIPO_EVALUACION_RETROSPECTIVA) == 0) {
      retrospectivaRepository.save(evaluacionCompleta.getMemoria().getRetrospectiva());
    } else {
      estadoMemoriaRepository.save(new EstadoMemoria(null, evaluacionCompleta.getMemoria(),
          evaluacionCompleta.getMemoria().getEstadoActual(), Instant.now()));
    }

    memoriaService.update(evaluacionCompleta.getMemoria());

    return evaluacionRepository.save(evaluacionCompleta);
  }

  public Evaluacion rellenarEvaluacionConEstadosMemoria(Evaluacion evaluacion) {
    /** Se setean campos de evaluación */

    evaluacion.setActivo(true);
    evaluacion.setEsRevMinima(false);
    evaluacion.setFechaDictamen(evaluacion.getConvocatoriaReunion().getFechaEvaluacion());
    evaluacion.setTipoEvaluacion(new TipoEvaluacion());

    // Convocatoria Seguimiento
    if (evaluacion.getConvocatoriaReunion().getTipoConvocatoriaReunion().getId()
        .compareTo(Constantes.TIPO_CONVOCATORIA_REUNION_SEGUIMIENTO) == 0) {
      // mismo tipo seguimiento que la memoria Anual(3) Final(4)
      evaluacion.getTipoEvaluacion()
          .setId((evaluacion.getMemoria().getEstadoActual().getId()
              .compareTo(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL) == 0)
                  ? Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL
                  : Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL);
      // se actualiza estado de la memoria a 'En evaluación'
      evaluacion.getMemoria().getEstadoActual()
          .setId((evaluacion.getMemoria().getEstadoActual().getId()
              .compareTo(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL) == 0)
                  ? Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL
                  : Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL);

      // Convocatoria Ordinaria o Extraordinaria
    } else {
      // memoria 'en secretaría' y retrospectiva 'en secretaría'
      if (evaluacion.getMemoria().getEstadoActual().getId() > Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
          && evaluacion.getMemoria().getRequiereRetrospectiva().booleanValue()
          && evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getId()
              .compareTo(Constantes.ESTADO_RETROSPECTIVA_EN_SECRETARIA) == 0) {
        // tipo retrospectiva
        evaluacion.getTipoEvaluacion().setId(Constantes.TIPO_EVALUACION_RETROSPECTIVA);
        // se actualiza el estado retrospectiva a 'En evaluación'
        evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva()
            .setId(Constantes.ESTADO_RETROSPECTIVA_EN_EVALUACION);

      } else {
        // tipo 'memoria'
        evaluacion.getTipoEvaluacion().setId(Constantes.TIPO_EVALUACION_MEMORIA);
        // se actualiza estado de la memoria a 'En evaluación'
        evaluacion.getMemoria().getEstadoActual().setId(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION);
      }
    }

    Optional<Evaluacion> evaluacionAnterior = evaluacionRepository
        .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(evaluacion.getMemoria().getId(),
            evaluacion.getTipoEvaluacion().getId());

    if (evaluacionAnterior.isPresent()) {
      evaluacion.setVersion(evaluacionAnterior.get().getVersion() + 1);
    } else {
      evaluacion.setVersion(1);
    }

    evaluacion.getMemoria().setVersion(evaluacion.getVersion());

    return evaluacion;
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

    // Si el dictamen es "Favorable" y la Evaluación es de Revisión Mínima
    if (evaluacionActualizar.getDictamen() != null
        && evaluacionActualizar.getDictamen().getNombre().equalsIgnoreCase("FAVORABLE")
        && evaluacionActualizar.getEsRevMinima().booleanValue()) {

      // Si el estado de la memoria es "En evaluación" o
      // "En secretaria revisión mínima"
      // Se cambia el estado de la memoria a "Fin evaluación"
      if (evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(4L)
          || evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(5L)) {
        memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(), 9L);
      }
      // Si el estado de la memoria es "En evaluación seguimiento anual" o "En
      // evaluación seguimiento final" o "En secretaría seguimiento final
      // aclaraciones"
      // Se cambia el estado de la memoria a "Fin evaluación seguimiento final"
      if (evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(13L)
          || evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(19L)
          || evaluacionActualizar.getMemoria().getEstadoActual().getId().equals(18L)) {
        memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(), 20L);
      }
    }

    // Si el dictamen es "Favorable pendiente de revisión mínima" y
    // la Evaluación es de Revisión Mínima, se cambia el estado de la
    // memoria a "Favorable Pendiente de Modificaciones Mínimas".
    if (evaluacionActualizar.getDictamen() != null && evaluacionActualizar.getDictamen().getId().equals(2L)
        && evaluacionActualizar.getEsRevMinima().booleanValue()) {
      memoriaService.updateEstadoMemoria(evaluacionActualizar.getMemoria(), 6L);
    }

    return evaluacionRepository.findById(evaluacionActualizar.getId()).map(evaluacion -> {
      evaluacion.setDictamen(evaluacionActualizar.getDictamen());
      evaluacion.setEsRevMinima(evaluacionActualizar.getEsRevMinima());
      evaluacion.setFechaDictamen(evaluacionActualizar.getFechaDictamen());
      evaluacion.setMemoria(evaluacionActualizar.getMemoria());
      evaluacion.setVersion(evaluacionActualizar.getVersion());
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
    switch (evaluacion.getTipoEvaluacion().getId().intValue()) {
      case Constantes.TIPO_EVALUACION_MEMORIA_INT:
        documento = getDocumentoByTipoEvaluacionMemoria(evaluacion, documento);
        break;
      case Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL_INT:
        if (evaluacion.getDictamen() != null
            && (evaluacion.getDictamen().getId().intValue() == Constantes.DICTAMEN_SOLICITUD_MODIFICACIONES)) {
          documento = this.generarDocumento(evaluacion, false);
        }
        break;
      case Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL_INT:
        if (evaluacion.getDictamen() != null && (evaluacion.getDictamen().getId()
            .intValue() == Constantes.DICTAMEN_SOLICITUD_ACLARACIONES_SEGUIMIENTO_FINAL)) {
          documento = this.generarDocumento(evaluacion, false);
        }
        break;
      case Constantes.TIPO_EVALUACION_RETROSPECTIVA_INT:
      default:
        if (evaluacion.getDictamen() != null
            && (evaluacion.getDictamen().getId().intValue() == Constantes.DICTAMEN_FAVORABLE
                || evaluacion.getDictamen().getId().intValue() == Constantes.DICTAMEN_FAVORABLE_RETROSPECTIVA)) {
          documento = this.generarDocumento(evaluacion, true);
        }
        break;
    }
    return documento;
  }

  private DocumentoOutput getDocumentoByTipoEvaluacionMemoria(Evaluacion evaluacion, DocumentoOutput documento) {
    if (evaluacion.getDictamen() != null
        && (evaluacion.getDictamen().getId().intValue() == Constantes.DICTAMEN_FAVORABLE_PENDIENTE_REVISION_MINIMA
            || evaluacion.getDictamen().getId().intValue() == Constantes.DICTAMEN_PENDIENTE_CORRECCIONES
            || evaluacion.getDictamen().getId().intValue() == Constantes.DICTAMEN_NO_PROCEDE_EVALUAR)) {
      documento = this.generarDocumento(evaluacion, false);
    } else if (evaluacion.getDictamen() != null
        && (evaluacion.getDictamen().getId().intValue() == Constantes.DICTAMEN_FAVORABLE)) {
      documento = this.generarDocumento(evaluacion, true);
    }

    return documento;
  }

  private DocumentoOutput generarDocumento(Evaluacion evaluacion, Boolean favorable) {
    log.debug("generarDocumento(Evaluacion evaluacion, Boolean favorable)- start");

    Resource informePdf = null;
    String tituloInforme = "";
    if (favorable.booleanValue()) {
      // Se obtiene el informe favorable en formato pdf creado mediante el
      // servicio de reporting
      if (Objects.equals(evaluacion.getTipoEvaluacion().getId(), Constantes.TIPO_EVALUACION_RETROSPECTIVA)) {
        informePdf = reportService.getInformeEvaluacionRetrospectiva(evaluacion.getId(), Instant.now());
        tituloInforme = TITULO_INFORME_EVALUACION_RETROSPECTIVA;
      } else {
        switch (evaluacion.getMemoria().getTipoMemoria().getId().intValue()) {
          case Constantes.TIPO_MEMORIA_NUEVA:
            informePdf = reportService.getInformeFavorableMemoria(evaluacion.getId());
            break;
          case Constantes.TIPO_MEMORIA_MODIFICACION:
            informePdf = reportService.getInformeFavorableModificacion(evaluacion.getId());
            break;
          case Constantes.TIPO_MEMORIA_RATIFICACION:
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
  public Page<Evaluacion> findByEvaluacionesEnSeguimientoFinal(String query, Pageable pageable) {
    log.debug("findByEvaluacionesEnSeguimientoFinal(String query,Pageable paging) - start");

    Page<Evaluacion> returnValue = evaluacionRepository.findByEvaluacionesEnSeguimientoFinal(query, pageable);
    log.debug("findByEvaluacionesEnSeguimientoFinal(String query,Pageable paging) - end");

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
    Memoria memoria = null;

    Optional<Evaluacion> evaluacion = evaluacionRepository.findById(idEvaluacion);
    if (evaluacion.isPresent()) {
      Assert.isTrue(evaluacion.get().getConvocatoriaReunion().getId().compareTo(idConvocatoriaReunion) == 0,
          "La evaluación no pertenece a esta convocatoria de reunión");
      Optional<Memoria> memoriaOpt = memoriaRepository.findById(evaluacion.get().getMemoria().getId());
      if (!memoriaOpt.isPresent()) {
        throw new MemoriaNotFoundException(evaluacion.get().getMemoria().getId());
      } else {
        memoria = memoriaOpt.get();
      }

    } else {
      throw new EvaluacionNotFoundException(idEvaluacion);
    }

    // Volvemos al estado anterior de la memoria
    memoria = memoriaService.getEstadoAnteriorMemoria(memoria);

    memoria.setVersion(memoria.getVersion() - 1);

    Assert.isTrue(evaluacion.get().getConvocatoriaReunion().getFechaEvaluacion().isAfter(Instant.now()),
        "La fecha de la convocatoria es anterior a la actual");

    Assert.isNull(evaluacion.get().getDictamen(), "No se pueden eliminar memorias que ya contengan un dictamen");

    Assert.isTrue(comentarioRepository.countByEvaluacionId(evaluacion.get().getId()) == 0L,
        "No se puede eliminar una memoria que tenga comentarios asociados");

    memoriaRepository.save(memoria);
    evaluacion.get().setActivo(Boolean.FALSE);
    evaluacionRepository.save(evaluacion.get());
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
      log.debug("sendComunicadoDictamenEvaluacionRevMin(Evaluacion evaluacion) - Error al enviar el comunicado", e);
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
      log.debug(
          "sendComunicadoDictamenEvaluacionSeguimientoRevMin(Evaluacion evaluacion) - Error al enviar el comunicado",
          e);
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
          log.debug("sendComunicadoInformeSeguimientoAnualPendiente() - Error al enviar el comunicado", e);

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
}
