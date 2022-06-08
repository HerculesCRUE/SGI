package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaEvaluada;
import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.TipoEstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.specification.ActaSpecifications;
import org.crue.hercules.sgi.eti.service.ActaService;
import org.crue.hercules.sgi.eti.service.ComunicadosService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
import org.crue.hercules.sgi.eti.service.SgdocService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Acta}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ActaServiceImpl implements ActaService {

  private static final String TITULO_INFORME_ACTA = "informeActaPdf";

  /** Acta Repository. */
  private final ActaRepository actaRepository;

  /** Estado Acta Repository. */
  private final EstadoActaRepository estadoActaRepository;

  /** Evaluacion Repository. */
  private final EvaluacionRepository evaluacionRepository;

  /** Tipo Estado Acta Repository. */
  private final TipoEstadoActaRepository tipoEstadoActaRepository;

  /** Memoria Service. */
  private final MemoriaService memoriaService;

  /** Retrospectiva Service. */
  private final RetrospectivaService retrospectivaService;

  /** Report service */
  private final SgiApiRepService reportService;

  /** SGDOC service */
  private final SgdocService sgdocService;

  /** Comunicado service */
  private final ComunicadosService comunicadosService;

  private static final String TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA = "Investigación tutelada";

  /**
   * Instancia un nuevo ActaServiceImpl.
   * 
   * @param actaRepository           {@link ActaRepository}
   * @param estadoActaRepository     {@link EstadoActaRepository}
   * @param tipoEstadoActaRepository {@link TipoEstadoActaRepository}
   * @param evaluacionRepository     {@link EvaluacionRepository}
   * @param retrospectivaRepository  {@link RetrospectivaRepository}
   * @param memoriaService           {@link MemoriaService}
   * @param retrospectivaService     {@link RetrospectivaService}
   * @param reportService            {@link SgiApiRepService}
   * @param sgdocService             {@link SgdocService}
   * @param comunicadosService       {@link ComunicadosService}
   */
  @Autowired
  public ActaServiceImpl(ActaRepository actaRepository, EstadoActaRepository estadoActaRepository,
      TipoEstadoActaRepository tipoEstadoActaRepository, EvaluacionRepository evaluacionRepository,
      RetrospectivaRepository retrospectivaRepository, MemoriaService memoriaService,
      RetrospectivaService retrospectivaService, SgiApiRepService reportService, SgdocService sgdocService,
      ComunicadosService comunicadosService) {
    this.actaRepository = actaRepository;
    this.estadoActaRepository = estadoActaRepository;
    this.tipoEstadoActaRepository = tipoEstadoActaRepository;
    this.evaluacionRepository = evaluacionRepository;
    this.memoriaService = memoriaService;
    this.retrospectivaService = retrospectivaService;
    this.reportService = reportService;
    this.sgdocService = sgdocService;
    this.comunicadosService = comunicadosService;
  }

  /**
   * Guarda la entidad {@link Acta}.
   * 
   * Se insertará de forma automática un registro para el estado inicial del tipo
   * "En elaboración" para el acta
   *
   * @param acta la entidad {@link Acta} a guardar.
   * @return la entidad {@link Acta} persistida.
   */
  @Transactional
  public Acta create(Acta acta) {
    log.debug("Acta create (Acta acta) - start");

    Assert.isNull(acta.getId(), "Acta id tiene que ser null para crear un nuevo acta");

    Optional<TipoEstadoActa> tipoEstadoActa = tipoEstadoActaRepository.findById(1L);
    Assert.isTrue(tipoEstadoActa.isPresent(), "No se puede establecer el TipoEstadoActa inicial (1: 'En elaboración')");

    acta.setEstadoActual(tipoEstadoActa.get());
    Acta returnValue = actaRepository.save(acta);

    EstadoActa estadoActa = estadoActaRepository
        .save(new EstadoActa(null, returnValue, tipoEstadoActa.get(), Instant.now()));
    Assert.notNull(estadoActa, "No se ha podido crear el EstadoActa inicial");

    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ActaWithNumEvaluaciones}.
   *
   * @param paging     la información de paginación.
   * @param query      información del filtro.
   * @param personaRef referencia de la persona.
   * @return el listado de {@link ActaWithNumEvaluaciones} paginadas y filtradas.
   */
  public Page<ActaWithNumEvaluaciones> findAllActaWithNumEvaluaciones(String query, Pageable paging,
      String personaRef) {
    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - start");
    Specification<Acta> specActa = null;
    if (StringUtils.isNotBlank(query)) {
      specActa = SgiRSQLJPASupport.toSpecification(query);
    }
    Page<ActaWithNumEvaluaciones> returnValue = actaRepository.findAllActaWithNumEvaluaciones(specActa, paging,
        SgiSecurityContextHolder.hasAuthority("ETI-ACT-V") ? null : personaRef);
    log.debug("findAllActaWithNumEvaluaciones(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Devuelve si el usuario es miembro activo del comité del {@link Acta}
   * 
   * @param personaRef usuario
   * @param idActa     identificador del {@link Acta}
   * @return las entidades {@link Acta}
   */
  @Override
  public Boolean isMiembroComiteActa(String personaRef, Long idActa) {
    log.debug("isMiembroComiteActa(String personaRef, Long idActa) - start");
    Specification<Acta> specActa = ActaSpecifications.byId(idActa);

    Page<ActaWithNumEvaluaciones> returnValue = actaRepository.findAllActaWithNumEvaluaciones(specActa,
        PageRequest.of(0, 1), personaRef);
    log.debug("isMiembroComiteActa(String personaRef, Long idActa) - end");
    return returnValue.hasContent();
  }

  /**
   * Obtiene una entidad {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @return la entidad {@link Acta}.
   * @throws ActaNotFoundException Si no existe ningún {@link Acta} con ese id.
   */
  public Acta findById(final Long id) throws TareaNotFoundException {
    log.debug("Acta findById (Acta acta)  - start", id);
    final Acta acta = actaRepository.findById(id).orElseThrow(() -> new ActaNotFoundException(id));
    log.debug("Acta findById (Acta acta)  - end", id);
    return acta;
  }

  /**
   * Comprueba la existencia del {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @return true si existe y false en caso contrario.
   */
  public boolean existsById(final Long id) throws TareaNotFoundException {
    log.debug("Acta existsById (Acta acta)  - start", id);
    final boolean existe = actaRepository.existsById(id);
    log.debug("Acta existsById (Acta acta)  - end", id);
    return existe;
  }

  /**
   * Elimina una entidad {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @throws ActaNotFoundException Si no existe ningún {@link Acta} con ese id.
   */
  @Transactional
  public void delete(Long id) throws TareaNotFoundException {
    log.debug("Petición a delete Acta : {}  - start", id);
    Assert.notNull(id, "El id de Acta no puede ser null.");
    if (!actaRepository.existsById(id)) {
      throw new ActaNotFoundException(id);
    }
    actaRepository.deleteById(id);
    log.debug("Petición a delete Acta : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link Acta}.
   * 
   * @param actaActualizar {@link Acta} con los datos actualizados.
   * @return El {@link Acta} actualizado.
   * @throws ActaNotFoundException    Si no existe ningún {@link Acta} con ese id.
   * @throws IllegalArgumentException Si el {@link Acta} no tiene id.
   */
  @Transactional
  public Acta update(final Acta actaActualizar) {
    log.debug("update(Acta actaActualizar) - start");

    Assert.notNull(actaActualizar.getId(), "Acta id no puede ser null para actualizar un acta");

    return actaRepository.findById(actaActualizar.getId()).map(acta -> {
      acta.setConvocatoriaReunion(actaActualizar.getConvocatoriaReunion());
      acta.setHoraInicio(actaActualizar.getHoraInicio());
      acta.setMinutoInicio(actaActualizar.getMinutoInicio());
      acta.setHoraFin(actaActualizar.getHoraFin());
      acta.setMinutoFin(actaActualizar.getMinutoFin());
      acta.setResumen(actaActualizar.getResumen());
      acta.setNumero(actaActualizar.getNumero());
      acta.setEstadoActual(actaActualizar.getEstadoActual());
      acta.setInactiva(actaActualizar.getInactiva());
      acta.setActivo(actaActualizar.getActivo());

      Acta returnValue = actaRepository.save(acta);
      log.debug("update(Acta actaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ActaNotFoundException(actaActualizar.getId()));
  }

  /**
   * Finaliza el {@link Acta} con el id recibido como parámetro (actualización de
   * su estado a finalizado).
   * 
   * Se actualiza el estado de sus evaluaciones:
   * 
   * - En caso de ser de tipo memoria se actualiza el estado de la memoria
   * dependiendo del dictamen asociado a la evaluación.
   * 
   * - En caso de ser una retrospectiva se actualiza siempre a estado "Fin
   * evaluación".
   * 
   * @param id identificador del {@link Acta} a finalizar.
   */
  @Override
  @Transactional
  public void finishActa(Long id) {

    log.debug("finishActa(Long id) - start");

    Assert.notNull(id, "El id de acta recibido no puede ser null.");

    Acta acta = actaRepository.findById(id).orElseThrow(() -> new ActaNotFoundException(id));

    // Tipo evaluación memoria
    List<Evaluacion> listEvaluacionesMemoria = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(Constantes.TIPO_EVALUACION_MEMORIA,
            Boolean.FALSE, acta.getConvocatoriaReunion().getId());

    for (Evaluacion evaluacion : listEvaluacionesMemoria) {
      boolean evaluacionMinima = false;
      switch (evaluacion.getDictamen().getId().intValue()) {
        case Constantes.DICTAMEN_FAVORABLE: {
          // Dictamen "Favorable"-
          // Se actualiza memoria a estado 9: "Fin evaluación"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), Constantes.ESTADO_MEMORIA_FIN_EVALUACION);
          break;
        }
        case Constantes.DICTAMEN_FAVORABLE_PENDIENTE_REVISION_MINIMA: {
          // Dictamen "Favorable pendiente de revisión mínima"-
          // Se actualiza memoria a estado 6: "Favorable Pendiente de Modificaciones
          // Mínimas"
          evaluacionMinima = true;
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(),
              Constantes.ESTADO_MEMORIA_FAVORABLE_PENDIENTE_MOD_MINIMAS);
          break;
        }
        case Constantes.DICTAMEN_PENDIENTE_CORRECCIONES: {
          // Dictamen "Pendiente de correcciones"
          // Se actualiza memoria a estado 7: "Pendiente de correcciones"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), Constantes.ESTADO_MEMORIA_PENDIENTE_CORRECCIONES);
          break;
        }
        case Constantes.DICTAMEN_NO_PROCEDE_EVALUAR: {
          // Dictamen "No procede evaluar"
          // Se actualiza memoria a estado 8: "No procede evaluar"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), Constantes.ESTADO_MEMORIA_NO_PROCEDE_EVALUAR);
          break;
        }
        case Constantes.DICTAMEN_SOLICITUD_MODIFICACIONES: {
          // Dictamen "Solicitud modificaciones"
          // Se actualiza memoria a estado 15: "Solicitud modificacion"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), Constantes.ESTADO_MEMORIA_SOLICITUD_MODIFICACION);
          break;
        }
        default:
          break;
      }

      // Enviar comunicado de cada evaluación al finalizar un acta
      if (!evaluacionMinima) {
        sendComunicadoActaFinalizada(evaluacion);
      }

    }

    // Tipo evaluación retrospectiva
    List<Evaluacion> listEvaluacionesRetrospectiva = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
            Constantes.TIPO_EVALUACION_RETROSPECTIVA, Boolean.FALSE, acta.getConvocatoriaReunion().getId());

    listEvaluacionesRetrospectiva.forEach(evaluacion -> {

      switch (evaluacion.getDictamen().getId().intValue()) {
        case Constantes.DICTAMEN_DESFAVORABLE_RETROSPECTIVA:
        case Constantes.DICTAMEN_FAVORABLE_RETROSPECTIVA:
        default: {
          // Dictamen "Favorable y desfavorable retrospectiva"
          // Se actualiza memoria a estado 5: "Fin evaluación retrospectiva"
          retrospectivaService.updateEstadoRetrospectiva(evaluacion.getMemoria().getRetrospectiva(),
              Constantes.ESTADO_RETROSPECTIVA_FIN_EVALUACION);
          break;
        }
      }

    });

    // Tipo evaluación seguimiento final
    List<Evaluacion> listEvaluacionesSegFinal = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
            Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL, Boolean.FALSE, acta.getConvocatoriaReunion().getId());

    listEvaluacionesSegFinal.forEach(evaluacion -> {

      switch (evaluacion.getDictamen().getId().intValue()) {
        case Constantes.DICTAMEN_FAVORABLE_SEGUIMIENTO_FINAL: {
          // Dictamen "Favorable - seguimiento anual"-
          // Se actualiza memoria a estado 9: "Fin evaluación"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(),
              Constantes.ESTADO_MEMORIA_FIN_EVALUACION_SEGUIMIENTO_FINAL);
          break;
        }
        case Constantes.DICTAMEN_SOLICITUD_ACLARACIONES_SEGUIMIENTO_FINAL: {
          // Dictamen "Solicitud aclaraciones seguimiento final"
          // Se actualiza memoria a estado 21: "En aclaración seguimiento final"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(),
              Constantes.TIPO_ESTADO_MEMORIA_EN_ACLARACION_SEGUIMIENTO_FINAL);
          break;
        }
        default:
          break;
      }
      // Enviar comunicado de cada evaluación al finalizar un acta
      sendComunicadoActaFinalizada(evaluacion);

    });

    // Tipo evaluación seguimiento anual
    List<Evaluacion> listEvaluacionesSegAnual = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
            Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL, Boolean.FALSE, acta.getConvocatoriaReunion().getId());

    listEvaluacionesSegAnual.forEach(evaluacion -> {

      switch (evaluacion.getDictamen().getId().intValue()) {
        case Constantes.DICTAMEN_FAVORABLE_SEGUIMIENTO_ANUAL: {
          // Dictamen "Favorable - seguimiento anual"-
          // Se actualiza memoria a estado 9: "Fin evaluación"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(),
              Constantes.ESTADO_MEMORIA_FIN_EVALUACION_SEGUIMIENTO_ANUAL);
          break;
        }
        case Constantes.DICTAMEN_SOLICITUD_MODIFICACIONES: {
          // Dictamen "Solicitud modificaciones"
          // Se actualiza memoria a estado 15: "Solicitud modificacion"
          memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), Constantes.ESTADO_MEMORIA_SOLICITUD_MODIFICACION);
          break;
        }
        default:
          break;
      }

      // Enviar comunicado de cada evaluación al finalizar un acta
      sendComunicadoActaFinalizada(evaluacion);
    });

    // Se crea el nuevo estado acta 2:"Finalizado"
    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(2L);

    EstadoActa estadoActa = new EstadoActa(null, acta, tipoEstadoActa, Instant.now());
    estadoActaRepository.save(estadoActa);

    // Actualización del estado actual de acta
    acta.setEstadoActual(tipoEstadoActa);
    actaRepository.save(acta);

    log.debug("finishActa(Long id) - end");
  }

  /**
   * Devuelve el {@link Acta} asociada a una {@link ConvocatoriaReunion}
   *
   * @param convocatoriaReunionId Id de {@link ConvocatoriaReunion}.
   * @return si hay acta asociado a la convocatoria de reunión
   */
  @Override
  public Acta findByConvocatoriaReunionId(Long convocatoriaReunionId) {
    Optional<Acta> acta = actaRepository.findByConvocatoriaReunionId(convocatoriaReunionId);
    return acta.isPresent() ? acta.get() : null;
  }

  /**
   * Devuelve el número de evaluaciones nuevas asociadas a un {@link Acta}
   *
   * @param idActa Id de {@link Acta}.
   * @return número de evaluaciones nuevas
   */
  @Override
  public Long countEvaluacionesNuevas(Long idActa) {
    return actaRepository.countEvaluacionesNuevas(idActa);
  }

  /**
   * Devuelve el número de evaluaciones de revisión sin las de revisión mínima
   *
   * @param idActa Id de {@link Acta}.
   * @return número de evaluaciones
   */
  @Override
  public Long countEvaluacionesRevisionSinMinima(Long idActa) {
    return actaRepository.countEvaluacionesRevisionSinMinima(idActa);
  }

  /**
   * Devuelve una lista de {@link MemoriaEvaluada} sin las de revisión mínima para
   * una determinada {@link Acta}
   * 
   * @param idActa Id de {@link Acta}.
   * @return lista de memorias evaluadas
   */
  @Override
  public List<MemoriaEvaluada> findAllMemoriasEvaluadasSinRevMinimaByActaId(Long idActa) {
    return actaRepository.findAllMemoriasEvaluadasSinRevMinimaByActaId(idActa);
  }

  /**
   * Obtiene el informe de un {@link Acta}
   * 
   * @param idActa id {@link Acta}
   * @return El documento del informe del acta
   */
  @Override
  public DocumentoOutput generarDocumentoActa(Long idActa) {
    Resource informePdf = reportService.getInformeActa(idActa);
    // Se sube el informe a sgdoc
    String fileName = TITULO_INFORME_ACTA + "_" + idActa + LocalDate.now() + ".pdf";
    return sgdocService.uploadInforme(fileName, informePdf);
  }

  /**
   * Identifica si el usuario es {@link Evaluador} en algun {@link Acta}
   * 
   * @param personaRef El usuario de la petición
   * @return true/false
   */
  @Override
  public Boolean hasAssignedActasByEvaluador(String personaRef) {
    log.debug("hasAssignedActasByEvaluador(String personaRef) - end");
    return actaRepository.hasAssignedActasByEvaluador(personaRef);
  }

  private void sendComunicadoActaFinalizada(Evaluacion evaluacion) {
    log.debug("sendComunicadoActaFinalizada(Evaluacion evaluacion) - Start");
    try {
      String tipoActividad;
      if (!evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre()
          .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
        tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre();
      } else {
        tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
      }
      this.comunicadosService.enviarComunicadoActaEvaluacionFinalizada(
          evaluacion.getMemoria().getComite().getNombreInvestigacion(),
          evaluacion.getMemoria().getComite().getGenero().toString(), evaluacion.getMemoria().getNumReferencia(),
          tipoActividad,
          evaluacion.getMemoria().getPeticionEvaluacion().getTitulo(), evaluacion.getMemoria().getPersonaRef());
      log.debug("sendComunicadoActaFinalizada(Evaluacion evaluacion) - End");
    } catch (Exception e) {
      log.debug("sendComunicadoActaFinalizada(Evaluacion evaluacion) - Error al enviar el comunicado", e);
    }
  }
}
