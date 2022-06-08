package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.converter.ComConverter;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoInput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoAviso;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
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
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
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

  private static final String TIPO_HITO_TEMPLATE = "TipoHito '";
  private static final String TIPO_HITO_ID_TEMPLATE = "ID TipoHito '";
  private final ConvocatoriaHitoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ModeloTipoHitoRepository modeloTipoHitoRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final ConvocatoriaHitoAvisoRepository convocatoriaHitoAvisoRepository;
  private final SolicitudRepository solicitudRepository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final SgiApiComService emailService;
  private final SgiApiTpService sgiApiTaskService;
  private final SgiApiSgpService personaService;

  public ConvocatoriaHitoService(ConvocatoriaHitoRepository convocatoriaHitoRepository,
      ConvocatoriaRepository convocatoriaRepository, ModeloTipoHitoRepository modeloTipoHitoRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository,
      ConvocatoriaHitoAvisoRepository convocatoriaHitoAvisoRepository,
      SolicitudRepository solicitudRespository,
      ProyectoEquipoRepository proyectoEquipoRepository,
      SgiApiComService emailService,
      SgiApiTpService sgiApiTaskService,
      SgiApiSgpService personaService) {
    this.repository = convocatoriaHitoRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.modeloTipoHitoRepository = modeloTipoHitoRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.convocatoriaHitoAvisoRepository = convocatoriaHitoAvisoRepository;
    this.solicitudRepository = solicitudRespository;
    this.proyectoEquipoRepository = proyectoEquipoRepository;
    this.emailService = emailService;
    this.sgiApiTaskService = sgiApiTaskService;
    this.personaService = personaService;
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
        TIPO_HITO_ID_TEMPLATE + convocatoriaHitoInput.getTipoHitoId()
            + "' no disponible para el ModeloEjecucion '"
            + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                : "Convocatoria sin modelo asignado")
            + "'");

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoHito.get().getActivo(), "ModeloTipoHito '" + modeloTipoHito.get().getTipoHito().getNombre()
        + "' no está activo para el ModeloEjecucion '" + modeloTipoHito.get().getModeloEjecucion().getNombre() + "'");

    // El TipoHito está activo
    Assert.isTrue(modeloTipoHito.get().getTipoHito().getActivo(),
        TIPO_HITO_TEMPLATE + modeloTipoHito.get().getTipoHito().getNombre() + "' no está activo");

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndFechaAndTipoHitoId(convocatoriaHitoInput.getConvocatoriaId(),
            convocatoriaHitoInput.getFecha(), convocatoriaHitoInput.getTipoHitoId()).isPresent(),
        "Ya existe un Hito con el mismo tipo en esa fecha");

    Assert.isTrue(modeloTipoHito.get().getTipoHito().getActivo(), "El TipoHito debe estar activo");

    ConvocatoriaHito convocatoriaHito = new ConvocatoriaHito();
    convocatoriaHito.setConvocatoriaId(convocatoriaHitoInput.getConvocatoriaId());
    convocatoriaHito.setTipoHito(modeloTipoHito.get().getTipoHito());
    convocatoriaHito.setFecha(convocatoriaHitoInput.getFecha());
    convocatoriaHito.setComentario(convocatoriaHitoInput.getComentario());

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
          TIPO_HITO_ID_TEMPLATE + convocatoriaHitoActualizar.getTipoHitoId()
              + "' no disponible para el ModeloEjecucion '"
              + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                  : "Convocatoria sin modelo asignado")
              + "'");

      // La asignación al ModeloEjecucion está activa
      Assert.isTrue(
          modeloTipoHito.get().getTipoHito().getId().equals(convocatoriaHito.getTipoHito().getId())
              || modeloTipoHito.get().getActivo(),
          "ModeloTipoHito '" + modeloTipoHito.get().getTipoHito().getNombre()
              + "' no está activo para el ModeloEjecucion '" + modeloTipoHito.get().getModeloEjecucion().getNombre()
              + "'");

      // El TipoHito está activo
      Assert.isTrue(
          modeloTipoHito.get().getTipoHito().getId().equals(convocatoriaHito.getTipoHito().getId())
              || modeloTipoHito.get().getTipoHito().getActivo(),
          TIPO_HITO_TEMPLATE + modeloTipoHito.get().getTipoHito().getNombre() + "' no está activo");

      repository
          .findByConvocatoriaIdAndFechaAndTipoHitoId(convocatoriaHitoActualizar.getConvocatoriaId(),
              convocatoriaHitoActualizar.getFecha(), convocatoriaHitoActualizar.getTipoHitoId())
          .ifPresent(convocatoriaHitoExistente -> Assert.isTrue(
              id.equals(convocatoriaHitoExistente.getId()),
              "Ya existe un Hito con el mismo tipo en esa fecha"));

      convocatoriaHito.setFecha(convocatoriaHitoActualizar.getFecha());
      convocatoriaHito.setComentario(convocatoriaHitoActualizar.getComentario());
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

        Assert.isTrue(task.getInstant().isAfter(Instant.now()), "El aviso ya se ha enviado.");

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
        "La fecha de envio debe ser anterior a " + now.toString());

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

    Assert.notNull(id, "ConvocatoriaHito id no puede ser null para eliminar un ConvocatoriaHito");
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

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));
    if (hasAuthorityViewInvestigador()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(convocatoriaId)
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoriaId));
      if (!convocatoria.getEstado().equals(Estado.REGISTRADA)
          || Boolean.FALSE.equals(configuracionSolicitud.getTramitacionSGI())) {
        throw new UserNotAuthorizedToAccessConvocatoriaException();
      }
    }

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

  private boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V");
  }
}
