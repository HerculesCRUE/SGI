package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaEvaluada;
import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.DictamenNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.ActaDocumento;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comentario.TipoEstadoComentario;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.eti.repository.ActaDocumentoRepository;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.EstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.TipoEstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.specification.ActaSpecifications;
import org.crue.hercules.sgi.eti.service.ActaService;
import org.crue.hercules.sgi.eti.service.AsistentesService;
import org.crue.hercules.sgi.eti.service.ComentarioService;
import org.crue.hercules.sgi.eti.service.ComunicadosService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
import org.crue.hercules.sgi.eti.service.SgdocService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiBlockchainService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Acta}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ActaServiceImpl implements ActaService {
  private static final String MSG_KEY_PATH_SEPARATOR = ".";
  private static final String MSG_ACTA_NO_SE_PUEDE_ESTABLECER = "acta.tipoActa.invalido";

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

  /** CNF service */
  private final SgiApiCnfService configService;

  /** Blockchain service */
  private final SgiApiBlockchainService blockchainService;

  private final AsistentesService asistentesService;

  /** Comentario Repository. */
  private final ComentarioRepository comentarioRepository;

  /** Comentario Service. */
  private final ComentarioService comentarioService;

  /** Documento Repository. */
  private final ActaDocumentoRepository actaDocumentoRepository;

  private final MessageSource messageSource;

  private final I18nConfig i18nConfig;

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
   * @param configService            {@link SgiApiCnfService}
   * @param blockchainService        {@link SgiApiBlockchainService}
   * @param asistentesService        {@link AsistentesService}
   * @param comentarioRepository     {@link ComentarioRepository}
   * @param comentarioService        {@link ComentarioService}
   * @param actaDocumentoRepository  {@link ActaDocumentoRepository}
   * @param messageSource            {@link MessageSource}
   * @param i18nConfig               {@link I18nConfig}
   */
  public ActaServiceImpl(ActaRepository actaRepository, EstadoActaRepository estadoActaRepository,
      TipoEstadoActaRepository tipoEstadoActaRepository, EvaluacionRepository evaluacionRepository,
      RetrospectivaRepository retrospectivaRepository, MemoriaService memoriaService,
      RetrospectivaService retrospectivaService, SgiApiRepService reportService, SgdocService sgdocService,
      ComunicadosService comunicadosService, SgiApiCnfService configService,
      SgiApiBlockchainService blockchainService, AsistentesService asistentesService,
      ComentarioRepository comentarioRepository, ComentarioService comentarioService,
      ActaDocumentoRepository actaDocumentoRepository, MessageSource messageSource, I18nConfig i18nConfig) {
    this.actaRepository = actaRepository;
    this.estadoActaRepository = estadoActaRepository;
    this.tipoEstadoActaRepository = tipoEstadoActaRepository;
    this.evaluacionRepository = evaluacionRepository;
    this.memoriaService = memoriaService;
    this.retrospectivaService = retrospectivaService;
    this.reportService = reportService;
    this.sgdocService = sgdocService;
    this.comunicadosService = comunicadosService;
    this.configService = configService;
    this.blockchainService = blockchainService;
    this.asistentesService = asistentesService;
    this.comentarioRepository = comentarioRepository;
    this.comentarioService = comentarioService;
    this.actaDocumentoRepository = actaDocumentoRepository;
    this.messageSource = messageSource;
    this.i18nConfig = i18nConfig;
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

    AssertHelper.idIsNull(acta.getId(), Acta.class);

    Optional<TipoEstadoActa> tipoEstadoActa = tipoEstadoActaRepository.findById(1L);
    Assert.isTrue(tipoEstadoActa.isPresent(), ApplicationContextSupport.getMessage(MSG_ACTA_NO_SE_PUEDE_ESTABLECER));

    acta.setEstadoActual(tipoEstadoActa.get());
    Acta returnValue = actaRepository.save(acta);

    EstadoActa estadoActa = estadoActaRepository
        .save(new EstadoActa(null, returnValue, tipoEstadoActa.get(), Instant.now()));
    AssertHelper.entityNotNull(estadoActa, Acta.class, EstadoActa.class);

    try {
      this.comunicadosService.enviarComunicadoRevisionActa(acta,
          asistentesService.findAllByConvocatoriaReunionId(acta.getConvocatoriaReunion().getId()));
    } catch (Exception e) {
      log.error("enviarComunicadoRevisionActa(actaId: {}) - Error al enviar el comunicado", acta.getId(), e);
    }

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
    AssertHelper.idNotNull(id, Acta.class);
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

    AssertHelper.idNotNull(actaActualizar.getId(), Acta.class);

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

    AssertHelper.idNotNull(id, Acta.class);

    Acta acta = actaRepository.findById(id).orElseThrow(() -> new ActaNotFoundException(id));

    // Tipo evaluación memoria
    List<Evaluacion> listEvaluacionesMemoria = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(TipoEvaluacion.Tipo.MEMORIA.getId(),
            Boolean.FALSE, acta.getConvocatoriaReunion().getId());

    TipoEvaluacion tipoEvaluacion = null;

    if (ObjectUtils.isNotEmpty(listEvaluacionesMemoria)) {
      tipoEvaluacion = TipoEvaluacion.builder().id(TipoEvaluacion.Tipo.MEMORIA.getId()).build();

      listEvaluacionesMemoria.forEach(evaluacion -> {
        TipoEstadoMemoria.Tipo tipoEstadoMemoriaUpdate = null;
        switch (Dictamen.Tipo.fromId(evaluacion.getDictamen().getId())) {
          case DESFAVORABLE:
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.DESFAVORABLE;
            break;
          case FAVORABLE:
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.FIN_EVALUACION;
            break;
          case FAVORABLE_PENDIENTE_REVISION_MINIMA:
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS;
            break;
          case PENDIENTE_CORRECCIONES:
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.PENDIENTE_CORRECCIONES;
            break;
          case NO_PROCEDE_EVALUAR:
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.NO_PROCEDE_EVALUAR;
            break;
          case SOLICITUD_MODIFICACIONES:
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.SOLICITUD_MODIFICACION;
            break;
          default:
            throw new DictamenNotFoundException(id);
        }

        memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), tipoEstadoMemoriaUpdate.getId());

        // Enviar comunicado de cada evaluación al finalizar un acta
        if (!evaluacion.getEsRevMinima().booleanValue()) {
          sendComunicadoActaFinalizada(evaluacion);
        }
      });
    }

    // Tipo evaluación retrospectiva
    List<Evaluacion> listEvaluacionesRetrospectiva = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
            TipoEvaluacion.Tipo.RETROSPECTIVA.getId(), Boolean.FALSE, acta.getConvocatoriaReunion().getId());

    if (ObjectUtils.isNotEmpty(listEvaluacionesRetrospectiva)) {
      tipoEvaluacion = TipoEvaluacion.builder().id(TipoEvaluacion.Tipo.RETROSPECTIVA.getId()).build();

      listEvaluacionesRetrospectiva.forEach(evaluacion -> {
        switch (Dictamen.Tipo.fromId(evaluacion.getDictamen().getId())) {
          case DESFAVORABLE_RETROSPECTIVA:
          case FAVORABLE_RETROSPECTIVA:
          default:
            retrospectivaService.updateEstadoRetrospectiva(evaluacion.getMemoria().getRetrospectiva(),
                EstadoRetrospectiva.Tipo.FIN_EVALUACION.getId());
            break;
        }
      });
    }

    // Tipo evaluación seguimiento final
    List<Evaluacion> listEvaluacionesSegFinal = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
            TipoEvaluacion.Tipo.SEGUIMIENTO_FINAL.getId(), Boolean.FALSE, acta.getConvocatoriaReunion().getId());

    if (ObjectUtils.isNotEmpty(listEvaluacionesSegFinal)) {
      tipoEvaluacion = TipoEvaluacion.builder().id(TipoEvaluacion.Tipo.SEGUIMIENTO_FINAL.getId()).build();
      listEvaluacionesSegFinal.forEach(evaluacion -> {
        TipoEstadoMemoria.Tipo tipoEstadoMemoriaUpdate = null;
        switch (Dictamen.Tipo.fromId(evaluacion.getDictamen().getId())) {
          case FAVORABLE_SEGUIMIENTO_FINAL:
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.FIN_EVALUACION_SEGUIMIENTO_FINAL;
            break;
          case SOLICITUD_ACLARACIONES_SEGUIMIENTO_FINAL:
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.EN_ACLARACION_SEGUIMIENTO_FINAL;
            break;
          default:
            throw new DictamenNotFoundException(id);
        }

        memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), tipoEstadoMemoriaUpdate.getId());

        // Enviar comunicado de cada evaluación al finalizar un acta
        sendComunicadoActaFinalizada(evaluacion);
      });
    }

    // Tipo evaluación seguimiento anual
    List<Evaluacion> listEvaluacionesSegAnual = evaluacionRepository
        .findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
            TipoEvaluacion.Tipo.SEGUIMIENTO_ANUAL.getId(), Boolean.FALSE, acta.getConvocatoriaReunion().getId());

    if (ObjectUtils.isNotEmpty(listEvaluacionesSegAnual)) {
      tipoEvaluacion = TipoEvaluacion.builder().id(TipoEvaluacion.Tipo.SEGUIMIENTO_ANUAL.getId()).build();
      listEvaluacionesSegAnual.forEach(evaluacion -> {
        TipoEstadoMemoria.Tipo tipoEstadoMemoriaUpdate = null;
        switch (Dictamen.Tipo.fromId(evaluacion.getDictamen().getId())) {
          case FAVORABLE_SEGUIMIENTO_ANUAL: {
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.FIN_EVALUACION_SEGUIMIENTO_ANUAL;
            break;
          }
          case SOLICITUD_MODIFICACIONES: {
            tipoEstadoMemoriaUpdate = TipoEstadoMemoria.Tipo.SOLICITUD_MODIFICACION_SEGUIMIENTO_ANUAL;
            break;
          }
          default:
            throw new DictamenNotFoundException(id);
        }

        memoriaService.updateEstadoMemoria(evaluacion.getMemoria(), tipoEstadoMemoriaUpdate.getId());

        // Enviar comunicado de cada evaluación al finalizar un acta
        sendComunicadoActaFinalizada(evaluacion);
      });
    }

    this.generarDocumentosActa(acta.getId());

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

  /**
   * Envia el comunicado de {@code ETI_COM_ACTA_SIN_REV_MINIMA} correspondiente a
   * la {@code evaluacion}
   * 
   * @param evaluacion una {@link Evaluacion}
   */
  private void sendComunicadoActaFinalizada(Evaluacion evaluacion) {
    log.debug("sendComunicadoActaFinalizada(Evaluacion evaluacion) - Start");
    try {
      List<I18nFieldValue> i18nTipoActividad = new ArrayList<>();
      if (!evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre()
          .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
        String tipoActividadId = evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getId().toString();

        String messageKey = TipoActividad.class.getName() + MSG_KEY_PATH_SEPARATOR + tipoActividadId;
        for (Language language : i18nConfig.getEnabledLanguages()) {
          i18nTipoActividad.add(new I18nFieldValueDto(language,
              messageSource.getMessage(messageKey, null, Locale.forLanguageTag(language.getCode()))));
        }

      } else {
        String tipoInvestigacionTuteladaId = evaluacion.getMemoria().getPeticionEvaluacion()
            .getTipoInvestigacionTutelada().getId().toString();

        String messageKey = TipoInvestigacionTutelada.class.getName() + MSG_KEY_PATH_SEPARATOR
            + tipoInvestigacionTuteladaId;

        for (Language language : i18nConfig.getEnabledLanguages()) {
          i18nTipoActividad.add(new I18nFieldValueDto(language,
              messageSource.getMessage(messageKey, null, Locale.forLanguageTag(language.getCode()))));
        }

      }
      this.comunicadosService.enviarComunicadoActaEvaluacionFinalizada(
          evaluacion.getMemoria().getComite().getNombre(),
          evaluacion.getMemoria().getComite().getCodigo(),
          evaluacion.getMemoria().getNumReferencia(),
          i18nTipoActividad,
          evaluacion.getMemoria().getPeticionEvaluacion().getTitulo(),
          evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef());
      log.debug("sendComunicadoActaFinalizada(Evaluacion evaluacion) - End");
    } catch (Exception e) {
      log.error("sendComunicadoActaFinalizada(evaluacionId: {}) - Error al enviar el comunicado", evaluacion.getId(),
          e);
    }
  }

  @Override
  @Transactional
  public Boolean confirmarRegistroBlockchain(Long idActa, Language lang) {
    log.debug("confirmarRegistroBlockchain(Long idActa) - start");
    Acta acta = actaRepository.findById(idActa).orElseThrow(() -> new ActaNotFoundException(idActa));
    ActaDocumento actaDocumento = actaDocumentoRepository.findByActaIdAndLang(idActa, lang);

    DocumentoOutput documento = sgdocService.getDocumento(actaDocumento.getDocumentoRef());

    String hash = blockchainService.confirmarRegistro(actaDocumento.getTransaccionRef());
    log.debug("confirmarRegistroBlockchain(Long idActa) - end");
    return (documento.getHash().equals(hash));
  }

  /**
   * Identifica si los {@link Comentario} en el {@link Acta} han sido
   * enviados
   * 
   * @param idActa     identificador del {@link Acta}
   * @param personaRef El usuario de la petición
   * @return true/false
   */
  @Override
  public boolean isComentariosActaEnviados(Long idActa, String personaRef) {
    log.debug("isComentariosActaEnviados(Long idActa, String personaRef) - start");
    Acta acta = actaRepository.findById(idActa).orElseThrow(() -> new ActaNotFoundException(idActa));
    Page<Evaluacion> evaluaciones = evaluacionRepository
        .findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(acta.getConvocatoriaReunion().getId(), null);
    List<Long> enviados = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(evaluaciones.getContent())) {
      evaluaciones.getContent().forEach(evaluacion -> {
        boolean comentariosEnviados = comentarioRepository.existsByEvaluacionIdAndTipoComentarioIdAndEstadoAndCreatedBy(
            evaluacion.getId(),
            TipoComentario.Tipo.ACTA_EVALUADOR.getId(),
            TipoEstadoComentario.CERRADO, personaRef);
        if (comentariosEnviados) {
          enviados.add(evaluacion.getId());
          return;
        }
      });
    }
    log.debug("isComentariosActaEnviados(Long idActa, String personaRef) - end");
    return !enviados.isEmpty();
  }

  @Override
  public boolean isPosibleEnviarComentariosActa(Long idActa, String personaRef) {
    log.debug("isPosibleEnviarComentariosActa(Long idActa, String personaRef) - start");
    Acta acta = actaRepository.findById(idActa).orElseThrow(() -> new ActaNotFoundException(idActa));
    Page<Evaluacion> evaluaciones = evaluacionRepository
        .findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(acta.getConvocatoriaReunion().getId(), null);
    List<Long> idsEvsComentariosAbiertos = new ArrayList<Long>();
    List<Long> idsEvsComentariosCerrados = new ArrayList<Long>();
    if (CollectionUtils.isNotEmpty(evaluaciones.getContent())) {
      evaluaciones.getContent().forEach(evaluacion -> {

        if (comentarioRepository.countByEvaluacionIdAndTipoComentarioIdAndCreatedByAndEstado(evaluacion.getId(),
            TipoComentario.Tipo.ACTA_EVALUADOR.getId(), personaRef, TipoEstadoComentario.CERRADO) > 0) {
          idsEvsComentariosCerrados.add(evaluacion.getId());
        }
        if (comentarioRepository.countByEvaluacionIdAndTipoComentarioIdAndCreatedByAndEstado(evaluacion.getId(),
            TipoComentario.Tipo.ACTA_EVALUADOR.getId(), personaRef, TipoEstadoComentario.ABIERTO) > 0) {
          idsEvsComentariosAbiertos.add(evaluacion.getId());
        }
      });
    }

    log.debug("isPosibleEnviarComentariosActa(Long idActa, String personaRef) - end");
    return !idsEvsComentariosAbiertos.isEmpty() && idsEvsComentariosCerrados.isEmpty();
  }

  @Override
  @Transactional
  public boolean enviarComentariosEvaluacion(Long idActa, String personaRef) {
    log.debug("enviarComentariosEvaluacion(Long idActa, String personaRef) - start");
    try {
      Acta acta = actaRepository.findById(idActa).orElseThrow(() -> new ActaNotFoundException(idActa));
      Page<Evaluacion> evaluaciones = evaluacionRepository
          .findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(acta.getConvocatoriaReunion().getId(), null);
      if (CollectionUtils.isNotEmpty(evaluaciones.getContent())) {
        evaluaciones.getContent()
            .forEach(evaluacion -> this.comentarioService.enviarByEvaluacionActa(evaluacion.getId(), personaRef));
      } else {
        return false;
      }
    } catch (Exception e) {
      log.error("enviarComentariosEvaluacion(Long idActa, String personaRef)", e);
      return false;
    }
    log.debug("enviarComentariosEvaluacion(Long idActa, String personaRef) - end");
    return true;
  }

  private void generarDocumentosActa(Long idActa) {
    for (Language language : I18nConfig.get().getEnabledLanguages()) {
      ActaDocumento actaDocumento = null;
      Resource informePdf = reportService.getInformeActa(idActa, language);
      // Se sube el informe a sgdoc
      String fileName = TITULO_INFORME_ACTA + "_" + idActa + LocalDate.now() + ".pdf";
      DocumentoOutput documento = null;
      actaDocumento = actaDocumentoRepository.findByActaIdAndLang(idActa, language);
      if (ObjectUtils.isNotEmpty(actaDocumento) && ObjectUtils.isNotEmpty(actaDocumento.getDocumentoRef())) {
        try {
          documento = sgdocService.getDocumento(actaDocumento.getDocumentoRef());
          actaDocumentoRepository.delete(actaDocumento);
          if (ObjectUtils.isNotEmpty(documento)) {
            sgdocService.delete(documento.getDocumentoRef());
          }
        } catch (Exception e) {
          log.error("No se encuentra el documento en el sgdoc: " + actaDocumento.getDocumentoRef(), e);
        }
      }
      actaDocumento = new ActaDocumento();
      actaDocumento.setActaId(idActa);
      documento = sgdocService.uploadInforme(fileName, informePdf);

      actaDocumento.setDocumentoRef(documento.getDocumentoRef());
      actaDocumento.setLang(language);

      try {
        if (configService.isBlockchainEnable().booleanValue() && ObjectUtils.isNotEmpty(documento)) {
          String transaccion = blockchainService.sellarDocumento(documento.getHash());
          actaDocumento.setTransaccionRef(transaccion);
        }
      } catch (Exception e) {
        log.debug("generarDocumento(Acta acta) - Error blockchain", e);
      }

      actaDocumentoRepository.save(actaDocumento);
    }
  }

  @Override
  @Transactional
  public DocumentoOutput generarDocumentoActa(Long idActa, Language lang) {

    ActaDocumento actaDocumento = null;
    Acta acta = this.findById(idActa);
    boolean isActaFinalizada = acta.getEstadoActual().getId() == 2L;

    String fileName = TITULO_INFORME_ACTA + "_" + idActa + LocalDate.now() + ".pdf";
    DocumentoOutput documento = null;
    actaDocumento = actaDocumentoRepository.findByActaIdAndLang(idActa, lang);
    if (ObjectUtils.isNotEmpty(actaDocumento) && ObjectUtils.isNotEmpty(actaDocumento.getDocumentoRef())) {
      documento = sgdocService.getDocumento(actaDocumento.getDocumentoRef());
      if (!isActaFinalizada) {
        actaDocumentoRepository.delete(actaDocumento);
        if (ObjectUtils.isNotEmpty(documento)) {
          sgdocService.delete(documento.getDocumentoRef());
        }
      }
    }

    if (!isActaFinalizada) {
      actaDocumento = new ActaDocumento();
      actaDocumento.setActaId(idActa);
      Resource informePdf = reportService.getInformeActa(idActa, lang);
      documento = sgdocService.uploadInforme(fileName, informePdf);
      actaDocumento.setDocumentoRef(documento.getDocumentoRef());
      actaDocumento.setLang(lang);

      actaDocumentoRepository.save(actaDocumento);
    }

    return documento;
  }
}
