package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.converter.ComConverter;
import org.crue.hercules.sgi.csp.converter.ConvocatoriaFaseObservacionesConverter;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseAvisoInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseInput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseAviso;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseAvisoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaFaseSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConvocatoriaFase}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaFaseServiceImpl implements ConvocatoriaFaseService {

  private static final String MSG_AVISO_ENVIADO = "avisoEnviado.message";
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_CREAR = "action.crear";
  private static final String MSG_FIELD_ACTION_ELIMINAR = "action.eliminar";
  private static final String MSG_FIELD_FECHA_ENVIO = "fechaEnvio";
  private static final String MSG_FIELD_FECHA_ACTUAL = "fechaActual";
  private static final String MSG_MODEL_MODELO_TIPO_FASE = "org.crue.hercules.sgi.csp.model.ModeloTipoFase.message";
  private static final String MSG_MODEL_CONVOCATORIA = "org.crue.hercules.sgi.csp.model.Convocatoria.message";
  private static final String MSG_MODEL_CONVOCATORIA_FASE = "org.crue.hercules.sgi.csp.model.ConvocatoriaFase.message";
  private static final String MSG_MODEL_TIPO_FASE = "org.crue.hercules.sgi.csp.model.TipoFase.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_INACTIVO = "org.springframework.util.Assert.entity.modeloEjecucion.inactivo.message";
  private static final String MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.exceptions.AssignModeloEjecucion.message";
  private static final String MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO = "org.crue.hercules.sgi.csp.model.Convocatoria.sinModeloEjecucion.message";
  private static final String MSG_NO_DISPONIBLE_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.noDisponible.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA_PERMISOS = "org.springframework.util.Assert.accion.denegada.permisos.message";
  private static final String MSG_PROBLEM_DATE_OVERLOAP = "org.springframework.util.Assert.date.overloap.message";

  private final ConvocatoriaFaseRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;
  private final ConvocatoriaService convocatoriaService;
  private final ConvocatoriaFaseAvisoRepository convocatoriaFaseAvisoRepository;
  private final SgiApiComService emailService;
  private final SgiApiTpService sgiApiTaskService;
  private final SgiApiSgpService personaService;
  private final SolicitudRepository solicitudRepository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;
  private final ConvocatoriaFaseObservacionesConverter convocatoriaFaseObservacionesConverter;

  public ConvocatoriaFaseServiceImpl(
      ConvocatoriaFaseRepository repository,
      ConvocatoriaRepository convocatoriaRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository,
      ModeloTipoFaseRepository modeloTipoFaseRepository,
      ConvocatoriaService convocatoriaService,
      ConvocatoriaFaseAvisoRepository convocatoriaFaseAvisoRepository,
      SolicitudRepository solicitudRepository,
      ProyectoEquipoRepository proyectoEquipoRepository,
      SgiApiComService emailService,
      SgiApiTpService sgiApiTaskService,
      SgiApiSgpService personaService,
      ConvocatoriaAuthorityHelper authorityHelper,
      ConvocatoriaFaseObservacionesConverter convocatoriaFaseObservacionesConverter) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.convocatoriaService = convocatoriaService;
    this.emailService = emailService;
    this.sgiApiTaskService = sgiApiTaskService;
    this.personaService = personaService;
    this.convocatoriaFaseAvisoRepository = convocatoriaFaseAvisoRepository;
    this.solicitudRepository = solicitudRepository;
    this.proyectoEquipoRepository = proyectoEquipoRepository;
    this.authorityHelper = authorityHelper;
    this.convocatoriaFaseObservacionesConverter = convocatoriaFaseObservacionesConverter;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaFase}.
   * 
   * @param convocatoriaFaseInput la entidad {@link ConvocatoriaFase} a guardar.
   * @return ConvocatoriaFase la entidad {@link ConvocatoriaFase} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaFase create(ConvocatoriaFaseInput convocatoriaFaseInput) {
    log.debug("create(ConvocatoriaFase convocatoriaFase) - start");

    AssertHelper.idNotNull(convocatoriaFaseInput.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.idNotNull(convocatoriaFaseInput.getTipoFaseId(), TipoFase.class);
    AssertHelper.fieldNotNull(convocatoriaFaseInput.getFechaInicio(), ConvocatoriaFase.class,
        AssertHelper.MESSAGE_KEY_DATE_START);
    AssertHelper.fieldNotNull(convocatoriaFaseInput.getFechaFin(), ConvocatoriaFase.class,
        AssertHelper.MESSAGE_KEY_DATE_END);

    AssertHelper.isBefore(convocatoriaFaseInput.getFechaFin().compareTo(convocatoriaFaseInput.getFechaInicio()) >= 0);

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaFaseInput.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaFaseInput.getConvocatoriaId()));

    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
        && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

    // TipoFase
    Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository
        .findByModeloEjecucionIdAndTipoFaseId(modeloEjecucionId, convocatoriaFaseInput.getTipoFaseId());

    // Está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoFase.isPresent(),
        () -> ProblemMessage.builder()
            .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
            .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
            .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
            .build());

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoFase.get().getActivo(),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_INACTIVO)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_FASE))
            .parameter(MSG_KEY_FIELD, modeloTipoFase.get().getTipoFase().getNombre())
            .build());

    // El TipoFase está activo
    Assert.isTrue(modeloTipoFase.get().getTipoFase().getActivo(),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_INACTIVO)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
            .parameter(MSG_KEY_FIELD, modeloTipoFase.get().getTipoFase().getNombre())
            .build());

    Assert.isTrue(!existsConvocatoriaFaseConFechasSolapadas(convocatoriaFaseInput, null),
        () -> ProblemMessage.builder()
            .key(MSG_PROBLEM_DATE_OVERLOAP)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                MSG_MODEL_CONVOCATORIA))
            .parameter(MSG_KEY_FIELD,
                convocatoriaFaseInput.getConvocatoriaId())
            .build());

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .convocatoriaId(convocatoriaFaseInput.getConvocatoriaId())
        .fechaInicio(convocatoriaFaseInput.getFechaInicio())
        .fechaFin(convocatoriaFaseInput.getFechaFin())
        .observaciones(
            this.convocatoriaFaseObservacionesConverter.convertAll(convocatoriaFaseInput.getObservaciones()))
        .tipoFase(modeloTipoFase.get().getTipoFase())
        .build();

    convocatoriaFase = repository.save(convocatoriaFase);

    ConvocatoriaFaseAviso aviso1 = createAviso(convocatoriaFase.getId(), convocatoriaFaseInput.getAviso1());
    ConvocatoriaFaseAviso aviso2 = createAviso(convocatoriaFase.getId(), convocatoriaFaseInput.getAviso2());

    convocatoriaFase.setConvocatoriaFaseAviso1(aviso1);
    convocatoriaFase.setConvocatoriaFaseAviso2(aviso2);

    if (aviso1 != null || aviso2 != null) {
      convocatoriaFase = repository.save(convocatoriaFase);
    }
    log.debug("create(ConvocatoriaFase convocatoriaFase) - end");

    return convocatoriaFase;
  }

  private ConvocatoriaFaseAviso createAviso(Long convocatoriaFaseId, ConvocatoriaFaseAvisoInput avisoInput) {
    if (avisoInput == null) {
      return null;
    }

    Instant now = Instant.now();
    AssertHelper.fieldBefore(avisoInput.getFechaEnvio().isAfter(now), MSG_FIELD_FECHA_ENVIO, MSG_FIELD_FECHA_ACTUAL);

    Long emailId = this.emailService.createConvocatoriaFaseEmail(
        convocatoriaFaseId,
        avisoInput.getAsunto(), avisoInput.getContenido(),
        avisoInput.getDestinatarios().stream()
            .map(destinatario -> new Recipient(destinatario.getNombre(), destinatario.getEmail()))
            .collect(Collectors.toList()));
    Long taskId = null;
    try {
      taskId = this.sgiApiTaskService.createSendEmailTask(
          emailId,
          avisoInput.getFechaEnvio());
    } catch (Exception ex) {
      log.warn("Error creando tarea programada. Se elimina el email");
      this.emailService.deleteEmail(emailId);
      throw ex;
    }

    ConvocatoriaFaseAviso aviso = new ConvocatoriaFaseAviso();
    aviso.setComunicadoRef(emailId.toString());
    aviso.setTareaProgramadaRef(taskId.toString());
    aviso.setIncluirIpsProyecto(avisoInput.getIncluirIpsProyecto());
    aviso.setIncluirIpsSolicitud(avisoInput.getIncluirIpsSolicitud());
    return convocatoriaFaseAvisoRepository.save(aviso);
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaFase}.
   * 
   * @param convocatoriaFaseActualizar la entidad {@link ConvocatoriaFase} a
   *                                   guardar.
   * @return ConvocatoriaFase la entidad {@link ConvocatoriaFase} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaFase update(Long convocatoriaFaseId, ConvocatoriaFaseInput convocatoriaFaseActualizar) {
    log.debug("update(ConvocatoriaFase convocatoriaFaseActualizar) - start");

    AssertHelper.idNotNull(convocatoriaFaseId, ConvocatoriaFase.class);
    AssertHelper.idNotNull(convocatoriaFaseActualizar.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.idNotNull(convocatoriaFaseActualizar.getTipoFaseId(), TipoFase.class);
    AssertHelper.fieldNotNull(convocatoriaFaseActualizar.getFechaInicio(), ConvocatoriaFase.class,
        AssertHelper.MESSAGE_KEY_DATE_START);
    AssertHelper.fieldNotNull(convocatoriaFaseActualizar.getFechaFin(), ConvocatoriaFase.class,
        AssertHelper.MESSAGE_KEY_DATE_END);

    AssertHelper
        .isBefore(convocatoriaFaseActualizar.getFechaFin().compareTo(convocatoriaFaseActualizar.getFechaInicio()) >= 0);

    return repository.findById(convocatoriaFaseId).map(convocatoriaFase -> {

      // Si la fase es la asignada a la ConfiguracionSolicitud comprobar si
      // convocatoria es modificable
      Assert.isTrue(isModificable(convocatoriaFase.getConvocatoriaId(), convocatoriaFase.getId()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_FASE))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_CREAR))
              .build());

      // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
      Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaFase.getConvocatoriaId())
          .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaFase.getConvocatoriaId()));

      Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
          && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

      Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository
          .findByModeloEjecucionIdAndTipoFaseId(modeloEjecucionId, convocatoriaFaseActualizar.getTipoFaseId());

      // Está asignado al ModeloEjecucion
      Assert.isTrue(modeloTipoFase.isPresent(),
          () -> ProblemMessage.builder()
              .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
              .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
              .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                  : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
              .build());

      // La asignación al ModeloEjecucion está activa
      Assert.isTrue(
          Objects.equals(modeloTipoFase.get().getTipoFase().getId(), convocatoriaFase.getTipoFase().getId())
              || modeloTipoFase.get().getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_MODELO_EJECUCION_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_FASE))
              .parameter(MSG_KEY_FIELD, modeloTipoFase.get().getTipoFase().getNombre())
              .parameter(MSG_KEY_MODELO, modeloTipoFase.get().getModeloEjecucion().getNombre())
              .build());

      // El TipoFase está activo
      Assert.isTrue(
          Objects.equals(modeloTipoFase.get().getTipoFase().getId(), convocatoriaFase.getTipoFase().getId())
              || modeloTipoFase.get().getTipoFase().getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
              .parameter(MSG_KEY_FIELD, modeloTipoFase.get().getTipoFase().getNombre())
              .build());

      Assert.isTrue(!existsConvocatoriaFaseConFechasSolapadas(convocatoriaFaseActualizar, convocatoriaFaseId),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_DATE_OVERLOAP)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_MODEL_CONVOCATORIA))
              .parameter(MSG_KEY_FIELD, convocatoriaFaseActualizar.getConvocatoriaId())
              .build());

      convocatoriaFase.setFechaInicio(convocatoriaFaseActualizar.getFechaInicio());
      convocatoriaFase.setFechaFin(convocatoriaFaseActualizar.getFechaFin());
      convocatoriaFase.setTipoFase(modeloTipoFase.get().getTipoFase());
      convocatoriaFase.setObservaciones(
          this.convocatoriaFaseObservacionesConverter.convertAll(convocatoriaFaseActualizar.getObservaciones()));

      if (convocatoriaFaseActualizar.getAviso1() != null && convocatoriaFase.getConvocatoriaFaseAviso1() == null) {
        ConvocatoriaFaseAviso aviso1 = this.createAviso(convocatoriaFase.getId(),
            convocatoriaFaseActualizar.getAviso1());
        convocatoriaFase.setConvocatoriaFaseAviso1(aviso1);
      } else if (this.deleteAvisoIfPossible(convocatoriaFaseActualizar.getAviso1(),
          convocatoriaFase.getConvocatoriaFaseAviso1())) {
        convocatoriaFase.setConvocatoriaFaseAviso1(null);
      } else {
        this.updateAvisoIfNeeded(convocatoriaFaseActualizar.getAviso1(), convocatoriaFase.getConvocatoriaFaseAviso1(),
            convocatoriaFase.getId());
      }

      if (convocatoriaFaseActualizar.getAviso2() != null && convocatoriaFase.getConvocatoriaFaseAviso2() == null) {
        ConvocatoriaFaseAviso aviso2 = this.createAviso(convocatoriaFase.getId(),
            convocatoriaFaseActualizar.getAviso2());
        convocatoriaFase.setConvocatoriaFaseAviso2(aviso2);
      } else if (this.deleteAvisoIfPossible(convocatoriaFaseActualizar.getAviso2(),
          convocatoriaFase.getConvocatoriaFaseAviso2())) {
        convocatoriaFase.setConvocatoriaFaseAviso2(null);
      } else {
        this.updateAvisoIfNeeded(convocatoriaFaseActualizar.getAviso2(), convocatoriaFase.getConvocatoriaFaseAviso2(),
            convocatoriaFase.getId());
      }

      return repository.save(convocatoriaFase);

    }).orElseThrow(() -> new ConvocatoriaFaseNotFoundException(convocatoriaFaseId));
  }

  /**
   * Elimina la {@link ConvocatoriaFase}.
   *
   * @param id Id del {@link ConvocatoriaFase}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ConvocatoriaFase.class);

    Optional<ConvocatoriaFase> fase = repository.findById(id);
    if (fase.isPresent()) {
      // Si la fase es la asignada a la ConfiguracionSolicitud comprobar si
      // convocatoria es modificable
      Assert.isTrue(isModificable(fase.get().getConvocatoriaId(), fase.get().getId()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_FASE))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_ELIMINAR))
              .build());
    } else {
      throw new ConvocatoriaFaseNotFoundException(id);
    }

    Page<ConfiguracionSolicitud> configuracionesSolicitud = configuracionSolicitudRepository
        .findByFasePresentacionSolicitudesId(id, null);
    List<ConfiguracionSolicitud> configuracionesSolicitudModificadas = configuracionesSolicitud.getContent().stream()
        .map(configuracionSolicitud -> {
          configuracionSolicitud.setFasePresentacionSolicitudes(null);
          return configuracionSolicitud;
        }).collect(Collectors.toList());

    configuracionSolicitudRepository.saveAll(configuracionesSolicitudModificadas);

    this.deleteAvisoIfPossible(null, fase.get().getConvocatoriaFaseAviso1());
    this.deleteAvisoIfPossible(null, fase.get().getConvocatoriaFaseAviso2());

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaFase} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaFase}.
   * @return la entidad {@link ConvocatoriaFase}.
   */
  @Override
  public ConvocatoriaFase findById(Long id) {

    return repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaFaseNotFoundException(id));

  }

  /**
   * Obtiene las {@link ConvocatoriaFase} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaFase} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaFase> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaFase> specs = ConvocatoriaFaseSpecifications.byConvocatoriaId(convocatoriaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaFase> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba que existen {@link ConvocatoriaFase} para una {@link Convocatoria}
   * con el mismo {@link TipoFase} y con las fechas solapadas
   *
   * @param convocatoriaFase {@link Convocatoria} a comprobar.
   * 
   * @return true si exite la coincidencia
   */

  private Boolean existsConvocatoriaFaseConFechasSolapadas(ConvocatoriaFaseInput convocatoriaFase,
      Long convocatoriaFaseId) {

    log.debug("existsConvocatoriaFaseConFechasSolapadas(ConvocatoriaFase convocatoriaFase) - start");
    Specification<ConvocatoriaFase> specByRangoFechaSolapados = ConvocatoriaFaseSpecifications
        .byRangoFechaSolapados(convocatoriaFase.getFechaInicio(), convocatoriaFase.getFechaFin());
    Specification<ConvocatoriaFase> specByConvocatoria = ConvocatoriaFaseSpecifications
        .byConvocatoriaId(convocatoriaFase.getConvocatoriaId());
    Specification<ConvocatoriaFase> specByTipoFase = ConvocatoriaFaseSpecifications
        .byTipoFaseId(convocatoriaFase.getTipoFaseId());
    Specification<ConvocatoriaFase> specByIdNotEqual = ConvocatoriaFaseSpecifications
        .byIdNotEqual(convocatoriaFaseId);

    Specification<ConvocatoriaFase> specs = Specification.where(specByConvocatoria).and(specByRangoFechaSolapados)
        .and(specByTipoFase).and(specByIdNotEqual);

    Page<ConvocatoriaFase> convocatoriaFases = repository.findAll(specs, Pageable.unpaged());

    Boolean returnValue = !convocatoriaFases.isEmpty();
    log.debug("existsConvocatoriaFaseConFechasSolapadas(ConvocatoriaFase convocatoriaFase) - end");

    return returnValue;

  }

  /**
   * Comprueba si la {@link ConvocatoriaFase} está la asignada a la
   * {@link ConfiguracionSolicitud} y si la {@link Convocatoria} correspondiente
   * es modificable para permitir la modificación o eliminación de la
   * {@link ConvocatoriaFase}
   * 
   * @param convocatoriaId     id de la {@link Convocatoria}
   * @param convocatoriaFaseId id de la {@link ConvocatoriaFase}
   * @return
   */
  private Boolean isModificable(Long convocatoriaId, Long convocatoriaFaseId) {
    log.debug("Boolean isModificable(Long convocatoriaId, Long convocatoriaFaseId) - start");

    Boolean returnValue = Boolean.TRUE;

    Optional<ConfiguracionSolicitud> configuraciSolicitud = configuracionSolicitudRepository
        .findByConvocatoriaId(convocatoriaId);

    if (configuraciSolicitud.isPresent() && configuraciSolicitud.get().getFasePresentacionSolicitudes() != null
        && Objects.equals(configuraciSolicitud.get().getFasePresentacionSolicitudes().getId(), convocatoriaFaseId)) {

      returnValue = convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaId, null,
          new String[] { ConvocatoriaAuthorityHelper.CSP_CON_E });
    }
    log.debug("Boolean isModificable(Long convocatoriaId, Long convocatoriaFaseId) - end");
    return returnValue;
  }

  @Override
  public boolean existsByConvocatoriaId(Long convocatoriaId) {
    return repository.existsByConvocatoriaId(convocatoriaId);
  }

  /**
   * Obtiene el listado de destinatarios adicionales a los que enviar el email
   * generado por una fase en base al {@link ConvocatoriaFaseAviso} relacionado
   * 
   * @param convocatoriaFaseId identificador de {@link ConvocatoriaFase}
   * @return listado de {@link Recipient}
   */
  public List<Recipient> getDeferredRecipients(Long convocatoriaFaseId) {
    ConvocatoriaFase fase = repository.findById(convocatoriaFaseId)
        .orElseThrow(() -> new ConvocatoriaFaseNotFoundException(convocatoriaFaseId));
    List<String> solicitantes = new ArrayList<>();

    solicitantes.addAll(getAditionalSolicitantesIfNeeded(fase.getConvocatoriaId(),
        fase.getConvocatoriaFaseAviso1()));
    solicitantes.addAll(getAditionalSolicitantesIfNeeded(fase.getConvocatoriaId(),
        fase.getConvocatoriaFaseAviso2()));

    List<String> solicitantesDistinct = solicitantes.stream().distinct().collect(Collectors.toList());

    if (!CollectionUtils.isEmpty(solicitantesDistinct)) {
      return ComConverter.toRecipients(personaService.findAllByIdIn(solicitantesDistinct));
    }

    return new LinkedList<>();
  }

  private List<String> getAditionalSolicitantesIfNeeded(Long convocatoriaId, ConvocatoriaFaseAviso aviso) {
    List<String> solicitantes = new LinkedList<>();
    if (aviso != null) {
      if (Boolean.TRUE.equals(aviso.getIncluirIpsSolicitud())) {
        solicitantes.addAll(solicitudRepository.findByConvocatoriaIdAndActivoIsTrue(convocatoriaId).stream()
            .map(Solicitud::getSolicitanteRef).collect(Collectors.toList()));
      }
      if (Boolean.TRUE.equals(aviso.getIncluirIpsProyecto())) {
        solicitantes.addAll(proyectoEquipoRepository.findAll(
            ProyectoEquipoSpecifications
                .byProyectoActivoAndProyectoConvocatoriaIdWithIpsActivos(convocatoriaId))
            .stream().map(ProyectoEquipo::getPersonaRef).collect(Collectors.toList()));
      }
    }
    return solicitantes;
  }

  /**
   * Comprueba si el aviso entrante es nulo y si existe en la base de datos, si
   * existe, lo intenta borrar
   * 
   * @param aviso                 aviso entrante
   * @param convocatoriaFaseAviso aviso persistido
   * @return boolean true if was deleted, false if not
   */
  private boolean deleteAvisoIfPossible(ConvocatoriaFaseAvisoInput avisoInput,
      ConvocatoriaFaseAviso convocatoriaFaseAviso) {
    if (avisoInput == null && convocatoriaFaseAviso != null) {
      // Comprobamos que se puede borrar el aviso.
      SgiApiInstantTaskOutput task = sgiApiTaskService
          .findInstantTaskById(Long.parseLong(convocatoriaFaseAviso.getTareaProgramadaRef()));

      Assert.isTrue(task.getInstant().isAfter(Instant.now()),
          ApplicationContextSupport.getMessage(MSG_AVISO_ENVIADO));

      sgiApiTaskService
          .deleteTask(Long.parseLong(convocatoriaFaseAviso.getTareaProgramadaRef()));
      emailService.deleteEmail(Long.parseLong(convocatoriaFaseAviso.getComunicadoRef()));
      convocatoriaFaseAvisoRepository.delete(convocatoriaFaseAviso);
      return true;
    }
    return false;
  }

  private void updateAvisoIfNeeded(ConvocatoriaFaseAvisoInput avisoInput, ConvocatoriaFaseAviso convocatoriaFaseAviso,
      Long convocatoriaFaseId) {
    if (avisoInput != null && convocatoriaFaseAviso != null) {
      SgiApiInstantTaskOutput task = sgiApiTaskService
          .findInstantTaskById(Long.parseLong(convocatoriaFaseAviso.getTareaProgramadaRef()));
      // Solo actualizamos los datos el aviso si este aún no se ha enviado.
      // generar error si no se puede editar
      if (task.getInstant().isAfter(Instant.now())) {
        this.emailService.updateConvocatoriaHitoEmail(
            Long.parseLong(convocatoriaFaseAviso.getComunicadoRef()), convocatoriaFaseId,
            avisoInput.getAsunto(), avisoInput.getContenido(),
            avisoInput.getDestinatarios().stream()
                .map(destinatario -> new Recipient(destinatario.getNombre(), destinatario.getEmail()))
                .collect(Collectors.toList()));

        this.sgiApiTaskService.updateSendEmailTask(
            Long.parseLong(convocatoriaFaseAviso.getTareaProgramadaRef()),
            Long.parseLong(convocatoriaFaseAviso.getComunicadoRef()),
            avisoInput.getFechaEnvio());

        convocatoriaFaseAviso.setIncluirIpsProyecto(avisoInput.getIncluirIpsProyecto());
        convocatoriaFaseAviso.setIncluirIpsSolicitud(avisoInput.getIncluirIpsSolicitud());
        convocatoriaFaseAvisoRepository.save(convocatoriaFaseAviso);
      }
    }
  }
}
