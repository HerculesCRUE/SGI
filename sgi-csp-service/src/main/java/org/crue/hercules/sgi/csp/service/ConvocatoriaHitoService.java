package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.converter.ComConverter;
import org.crue.hercules.sgi.csp.converter.ConvocatoriaHitoComentarioConverter;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoInput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoAviso;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaHitoAvisoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaHitoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaHitoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
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
 * Service para la gestión de {@link ConvocatoriaHito}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaHitoService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_KEY_ID = "id";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_KEY_DATE = "date";
  private static final String MSG_MODEL_TIPO_HITO = "org.crue.hercules.sgi.csp.model.TipoHito.message";
  private static final String MSG_MODEL_FECHA_ENVIO = "org.crue.hercules.sgi.csp.model.FechaEnvio.message";
  private static final String MSG_NO_DISPONIBLE_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.noDisponible.message";
  private static final String MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO = "org.crue.hercules.sgi.csp.model.Convocatoria.sinModeloEjecucion.message";
  private static final String MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.exceptions.AssignModeloEjecucion.message";
  private static final String MSG_MODELO_EJECUCION_INACTIVO = "org.springframework.util.Assert.entity.modeloEjecucion.inactivo.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_ENTITY_IN_FECHA_EXISTS = "org.springframework.util.Assert.fecha.exists.message";
  private static final String MSG_ENTITY_FECHA_ANTERIOR = "org.springframework.util.Assert.entity.fecha.anterior.message";
  private static final String MSG_AVISO_ENVIADO = "avisoEnviado.message";

  private final ConvocatoriaHitoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ModeloTipoHitoRepository modeloTipoHitoRepository;
  private final ConvocatoriaHitoAvisoRepository convocatoriaHitoAvisoRepository;
  private final SolicitudRepository solicitudRepository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final SgiApiComService emailService;
  private final SgiApiTpService sgiApiTaskService;
  private final SgiApiSgpService personaService;
  private final ConvocatoriaAuthorityHelper authorityHelper;
  private final ConvocatoriaHitoComentarioConverter convocatoriaHitoComentarioConverter;

  public ConvocatoriaHitoService(ConvocatoriaHitoRepository convocatoriaHitoRepository,
      ConvocatoriaRepository convocatoriaRepository, ModeloTipoHitoRepository modeloTipoHitoRepository,
      ConvocatoriaHitoAvisoRepository convocatoriaHitoAvisoRepository,
      SolicitudRepository solicitudRespository,
      ProyectoEquipoRepository proyectoEquipoRepository,
      SgiApiComService emailService,
      SgiApiTpService sgiApiTaskService,
      SgiApiSgpService personaService,
      ConvocatoriaAuthorityHelper authorityHelper,
      ConvocatoriaHitoComentarioConverter convocatoriaHitoComentarioConverter) {
    this.repository = convocatoriaHitoRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.modeloTipoHitoRepository = modeloTipoHitoRepository;
    this.convocatoriaHitoAvisoRepository = convocatoriaHitoAvisoRepository;
    this.solicitudRepository = solicitudRespository;
    this.proyectoEquipoRepository = proyectoEquipoRepository;
    this.emailService = emailService;
    this.sgiApiTaskService = sgiApiTaskService;
    this.personaService = personaService;
    this.authorityHelper = authorityHelper;
    this.convocatoriaHitoComentarioConverter = convocatoriaHitoComentarioConverter;
  }

  /**
   * Crea la entidad {@link ConvocatoriaHito} y las relaciones necesarias.
   * 
   * @param convocatoriaHitoInput la entidad {@link ConvocatoriaHitoInput} con la
   *                              información a guardar.
   * @return ConvocatoriaHito la entidad {@link ConvocatoriaHito} persistida.
   */
  @Transactional
  public ConvocatoriaHito create(ConvocatoriaHitoInput convocatoriaHitoInput) {
    log.debug("create(ConvocatoriaHito convocatoriaHito) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaHitoInput.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaHitoInput.getConvocatoriaId()));

    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
        && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

    // TipoHito
    Optional<ModeloTipoHito> modeloTipoHito = modeloTipoHitoRepository
        .findByModeloEjecucionIdAndTipoHitoId(modeloEjecucionId, convocatoriaHitoInput.getTipoHitoId());

    // Está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoHito.isPresent(),
        () -> ProblemMessage.builder()
            .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
            .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
            .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
            .build());

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoHito.get().getActivo(),
        () -> ProblemMessage.builder()
            .key(MSG_MODELO_EJECUCION_INACTIVO)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
            .parameter(MSG_KEY_FIELD, modeloTipoHito.get().getTipoHito().getNombre())
            .parameter(MSG_KEY_MODELO, modeloTipoHito.get().getModeloEjecucion().getNombre())
            .build());

    // El TipoHito está activo
    Assert.isTrue(modeloTipoHito.get().getTipoHito().getActivo(),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_INACTIVO)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
            .parameter(MSG_KEY_FIELD, modeloTipoHito.get().getTipoHito().getNombre())
            .build());

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndFechaAndTipoHitoId(convocatoriaHitoInput.getConvocatoriaId(),
            convocatoriaHitoInput.getFecha(), convocatoriaHitoInput.getTipoHitoId()).isPresent(),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_IN_FECHA_EXISTS)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
            .parameter(MSG_KEY_ID, convocatoriaHitoInput.getTipoHitoId())
            .build());

    ConvocatoriaHito convocatoriaHito = new ConvocatoriaHito();
    convocatoriaHito.setConvocatoriaId(convocatoriaHitoInput.getConvocatoriaId());
    convocatoriaHito.setTipoHito(modeloTipoHito.get().getTipoHito());
    convocatoriaHito.setFecha(convocatoriaHitoInput.getFecha());
    convocatoriaHito
        .setComentario(this.convocatoriaHitoComentarioConverter.convertAll(convocatoriaHitoInput.getComentario()));

    convocatoriaHito = repository.save(convocatoriaHito);

    if (convocatoriaHitoInput.getAviso() != null) {
      ConvocatoriaHitoAviso aviso = this.createAviso(convocatoriaHito.getId(),
          convocatoriaHitoInput.getAviso());
      convocatoriaHito.setConvocatoriaHitoAviso(aviso);

      convocatoriaHito = repository.save(convocatoriaHito);

    }

    log.debug("create(ConvocatoriaHito convocatoriaHito) - end");
    return convocatoriaHito;
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaHito} y sus relaciones.
   * 
   * @param id                         identificador de {@link ConvocatoriaHito}
   * @param convocatoriaHitoActualizar la entidad {@link ConvocatoriaHitoInput}
   *                                   con la información a actualizar.
   * @return ConvocatoriaHito la entidad {@link ConvocatoriaHito} persistida.
   */
  @Transactional
  public ConvocatoriaHito update(Long id, ConvocatoriaHitoInput convocatoriaHitoActualizar) {
    log.debug("update(ConvocatoriaHito convocatoriaHitoActualizar) - start");

    return repository.findById(id).map(convocatoriaHito -> {

      // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
      Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaHito.getConvocatoriaId())
          .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaHito.getConvocatoriaId()));
      Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
          && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

      // TipoHito
      Optional<ModeloTipoHito> modeloTipoHito = modeloTipoHitoRepository
          .findByModeloEjecucionIdAndTipoHitoId(modeloEjecucionId, convocatoriaHitoActualizar.getTipoHitoId());

      // Está asignado al ModeloEjecucion
      Assert.isTrue(modeloTipoHito.isPresent(),
          () -> ProblemMessage.builder()
              .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
              .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
              .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                  : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
              .build());

      // La asignación al ModeloEjecucion está activa
      Assert.isTrue(modeloTipoHito.get().getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_MODELO_EJECUCION_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
              .parameter(MSG_KEY_FIELD, modeloTipoHito.get().getTipoHito().getNombre())
              .parameter(MSG_KEY_MODELO, modeloTipoHito.get().getModeloEjecucion().getNombre())
              .build());

      // El TipoHito está activo
      Assert.isTrue(modeloTipoHito.get().getTipoHito().getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
              .parameter(MSG_KEY_FIELD, modeloTipoHito.get().getTipoHito().getNombre())
              .build());

      repository
          .findByConvocatoriaIdAndFechaAndTipoHitoId(convocatoriaHitoActualizar.getConvocatoriaId(),
              convocatoriaHitoActualizar.getFecha(), convocatoriaHitoActualizar.getTipoHitoId())
          .ifPresent(convocatoriaHitoExistente -> Assert.isTrue(id.equals(convocatoriaHitoExistente.getId()),
              () -> ProblemMessage.builder().key(MSG_ENTITY_IN_FECHA_EXISTS)
                  .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
                  .parameter(MSG_KEY_ID, convocatoriaHitoActualizar.getTipoHitoId())
                  .build()));

      convocatoriaHito.setFecha(convocatoriaHitoActualizar.getFecha());
      convocatoriaHito.setComentario(
          this.convocatoriaHitoComentarioConverter.convertAll(convocatoriaHitoActualizar.getComentario()));
      convocatoriaHito.setTipoHito(modeloTipoHito.get().getTipoHito());

      // Creamos un nuevo aviso
      if (convocatoriaHitoActualizar.getAviso() != null && convocatoriaHito.getConvocatoriaHitoAviso() == null) {
        ConvocatoriaHitoAviso aviso = this.createAviso(convocatoriaHito.getId(),
            convocatoriaHitoActualizar.getAviso());
        convocatoriaHito.setConvocatoriaHitoAviso(aviso);
      }
      // Borramos el aviso
      else if (convocatoriaHitoActualizar.getAviso() == null && convocatoriaHito.getConvocatoriaHitoAviso() != null) {
        // Comprobamos que se puede borrar el aviso.
        SgiApiInstantTaskOutput task = sgiApiTaskService
            .findInstantTaskById(Long.parseLong(convocatoriaHito.getConvocatoriaHitoAviso().getTareaProgramadaRef()));

        Assert.isTrue(task.getInstant().isAfter(Instant.now()),
            ApplicationContextSupport.getMessage(MSG_AVISO_ENVIADO));

        sgiApiTaskService
            .deleteTask(Long.parseLong(convocatoriaHito.getConvocatoriaHitoAviso().getTareaProgramadaRef()));
        emailService.deleteEmail(Long.parseLong(convocatoriaHito.getConvocatoriaHitoAviso().getComunicadoRef()));
        convocatoriaHitoAvisoRepository.delete(convocatoriaHito.getConvocatoriaHitoAviso());
        convocatoriaHito.setConvocatoriaHitoAviso(null);
      }
      // Actualizamos el aviso
      else if (convocatoriaHitoActualizar.getAviso() != null && convocatoriaHito.getConvocatoriaHitoAviso() != null) {
        SgiApiInstantTaskOutput task = sgiApiTaskService
            .findInstantTaskById(Long.parseLong(convocatoriaHito.getConvocatoriaHitoAviso().getTareaProgramadaRef()));
        // Solo actualizamos los datos el aviso si este aún no se ha enviado.
        // TODO: Validar realmente el cambio de contenido, y si este ha cambiado,
        // generar error si no se puede editar
        if (task.getInstant().isAfter(Instant.now())) {
          this.emailService.updateConvocatoriaHitoEmail(
              Long.parseLong(convocatoriaHito.getConvocatoriaHitoAviso().getComunicadoRef()), convocatoriaHito.getId(),
              convocatoriaHitoActualizar.getAviso().getAsunto(), convocatoriaHitoActualizar.getAviso().getContenido(),
              convocatoriaHitoActualizar.getAviso().getDestinatarios().stream()
                  .map(destinatario -> new Recipient(destinatario.getNombre(), destinatario.getEmail()))
                  .collect(Collectors.toList()));

          this.sgiApiTaskService.updateSendEmailTask(
              Long.parseLong(convocatoriaHito.getConvocatoriaHitoAviso().getTareaProgramadaRef()),
              Long.parseLong(convocatoriaHito.getConvocatoriaHitoAviso().getComunicadoRef()),
              convocatoriaHitoActualizar.getAviso().getFechaEnvio());

          convocatoriaHito.getConvocatoriaHitoAviso()
              .setIncluirIpsProyecto(convocatoriaHitoActualizar.getAviso().getIncluirIpsProyecto());
          convocatoriaHito.getConvocatoriaHitoAviso()
              .setIncluirIpsSolicitud(convocatoriaHitoActualizar.getAviso().getIncluirIpsSolicitud());
          convocatoriaHitoAvisoRepository.save(convocatoriaHito.getConvocatoriaHitoAviso());
        }
      }
      log.debug("update(ConvocatoriaHito convocatoriaHitoActualizar) - end");
      return repository.save(convocatoriaHito);
    }).orElseThrow(() -> new ConvocatoriaHitoNotFoundException(id));

  }

  private ConvocatoriaHitoAviso createAviso(Long convocatoriaHitoId, ConvocatoriaHitoAvisoInput avisoInput) {
    Instant now = Instant.now();

    Assert.isTrue(avisoInput.getFechaEnvio().isAfter(now),
        () -> ProblemMessage.builder().key(MSG_ENTITY_FECHA_ANTERIOR)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_FECHA_ENVIO))
            .parameter(MSG_KEY_DATE, now.toString())
            .build());

    Long emailId = this.emailService.createConvocatoriaHitoEmail(
        convocatoriaHitoId,
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

    ConvocatoriaHitoAviso aviso = new ConvocatoriaHitoAviso();
    aviso.setComunicadoRef(emailId.toString());
    aviso.setTareaProgramadaRef(taskId.toString());
    aviso.setIncluirIpsProyecto(avisoInput.getIncluirIpsProyecto());
    aviso.setIncluirIpsSolicitud(avisoInput.getIncluirIpsSolicitud());
    return convocatoriaHitoAvisoRepository.save(aviso);
  }

  /**
   * Elimina la {@link ConvocatoriaHito}.
   *
   * @param id Id del {@link ConvocatoriaHito}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    AssertHelper.idNotNull(id, ConvocatoriaHito.class);
    if (!repository.existsById(id)) {
      throw new ConvocatoriaHitoNotFoundException(id);
    }
    Optional<ConvocatoriaHitoAviso> aviso = convocatoriaHitoAvisoRepository.findByConvocatoriaHitoId(id);
    if (aviso.isPresent()) {
      this.sgiApiTaskService.deleteTask(Long.parseLong(aviso.get().getTareaProgramadaRef()));
      this.emailService.deleteEmail(Long.parseLong(aviso.get().getComunicadoRef()));
      this.convocatoriaHitoAvisoRepository.delete(aviso.get());
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene {@link ConvocatoriaHito} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaHito}.
   * @return la entidad {@link ConvocatoriaHito}.
   */
  public ConvocatoriaHito findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaHito returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaHitoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene las {@link ConvocatoriaHito} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaHito} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaHito> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaHito> specs = ConvocatoriaHitoSpecifications.byConvocatoriaId(convocatoriaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaHito> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - end");
    return returnValue;

  }

  /**
   * Obtiene el listado de destinatarios adicionales a los que enviar el email
   * generado por un hito en base al {@link ConvocatoriaHitoAviso} relacionadao
   * 
   * @param id identificador de {@link ConvocatoriaHito}
   * @return listado de {@link Recipient}
   */
  public List<Recipient> getDeferredRecipients(Long id) {
    ConvocatoriaHito hito = repository.findById(id).orElseThrow(() -> new ConvocatoriaHitoNotFoundException(id));
    List<Recipient> recipients = new ArrayList<>();
    List<String> solicitantes = new ArrayList<>();
    if (hito.getConvocatoriaHitoAviso() != null) {
      if (Boolean.TRUE.equals(hito.getConvocatoriaHitoAviso().getIncluirIpsSolicitud())) {
        solicitantes.addAll(solicitudRepository.findByConvocatoriaIdAndActivoIsTrue(hito.getConvocatoriaId()).stream()
            .map(Solicitud::getSolicitanteRef).collect(Collectors.toList()));
      }
      if (Boolean.TRUE.equals(hito.getConvocatoriaHitoAviso().getIncluirIpsProyecto())) {
        solicitantes.addAll(proyectoEquipoRepository.findAll(
            ProyectoEquipoSpecifications
                .byProyectoActivoAndProyectoConvocatoriaIdWithIpsActivos(hito.getConvocatoriaId()))
            .stream().map(ProyectoEquipo::getPersonaRef).collect(Collectors.toList()));
      }
      if (!CollectionUtils.isEmpty(solicitantes)) {
        recipients = ComConverter.toRecipients(personaService.findAllByIdIn(solicitantes));
      }
    }

    return recipients;
  }

  public boolean existsByConvocatoriaId(Long convocatoriaId) {
    return repository.existsByConvocatoriaId(convocatoriaId);
  }

}
