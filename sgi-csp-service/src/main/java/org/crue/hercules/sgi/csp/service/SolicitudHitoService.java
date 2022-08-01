package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.converter.ComConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoInput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.SolicitudHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudHitoAviso;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.SolicitudHitoAvisoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudHitoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudHitoSpecifications;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestion {@link SolicitudHito}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudHitoService {

  private final SolicitudHitoRepository repository;

  private final SolicitudRepository solicitudRepository;

  private final TipoHitoRepository tipoHitoRepository;

  private final SolicitudService solicitudService;

  private final SolicitudHitoAvisoRepository solicitudHitoAvisoRepository;
  private final SgiApiComService emailService;
  private final SgiApiTpService sgiApiTaskService;
  private final SgiApiSgpService personaService;

  public SolicitudHitoService(SolicitudHitoRepository repository, SolicitudRepository solicitudRepository,
      TipoHitoRepository tipoHitoRepository, SolicitudService solicitudService,
      SolicitudHitoAvisoRepository solicitudHitoAvisoRepository,
      SgiApiComService emailService,
      SgiApiTpService sgiApiTaskService,
      SgiApiSgpService personaService) {
    this.repository = repository;
    this.solicitudRepository = solicitudRepository;
    this.tipoHitoRepository = tipoHitoRepository;
    this.solicitudService = solicitudService;
    this.solicitudHitoAvisoRepository = solicitudHitoAvisoRepository;
    this.emailService = emailService;
    this.sgiApiTaskService = sgiApiTaskService;
    this.personaService = personaService;
  }

  /**
   * Crea la entidad {@link SolicitudHito} y las relaciones necesarias.
   * 
   * @param solicitudHitoInput la entidad {@link SolicitudHitoInput} con la
   *                           información a guardar.
   * @return SolicitudHito la entidad {@link SolicitudHito} persistida.
   */
  @Transactional
  public SolicitudHito create(SolicitudHitoInput solicitudHitoInput) {
    log.debug("create(SolicitudHito solicitudHito) - start");

    Assert.notNull(solicitudHitoInput.getSolicitudId(), "La solicitud no puede ser null para crear la SolicitudHito");
    Assert.notNull(solicitudHitoInput.getFecha(), "La fecha no puede ser null para crear la SolicitudHito");
    Assert.notNull(solicitudHitoInput.getTipoHitoId(), "El tipo hito no puede ser null para crear la SolicitudHito");

    Assert.isTrue(!repository.findBySolicitudIdAndFechaAndTipoHitoId(solicitudHitoInput.getSolicitudId(),
        solicitudHitoInput.getFecha(), solicitudHitoInput.getTipoHitoId()).isPresent(),
        "Ya existe un Hito con el mismo tipo en esa fecha");

    if (!solicitudRepository.existsById(solicitudHitoInput.getSolicitudId())) {
      throw new SolicitudNotFoundException(solicitudHitoInput.getSolicitudId());
    }

    TipoHito tipoHito = tipoHitoRepository.findById(solicitudHitoInput.getTipoHitoId())
        .orElseThrow(() -> new TipoHitoNotFoundException(solicitudHitoInput.getTipoHitoId()));

    SolicitudHito solicitudHito = new SolicitudHito();
    solicitudHito.setSolicitudId(solicitudHitoInput.getSolicitudId());
    solicitudHito.setTipoHito(tipoHito);
    solicitudHito.setFecha(solicitudHitoInput.getFecha());
    solicitudHito.setComentario(solicitudHitoInput.getComentario());

    solicitudHito = repository.save(solicitudHito);

    if (solicitudHitoInput.getAviso() != null) {
      SolicitudHitoAviso aviso = this.createAviso(solicitudHito.getId(),
          solicitudHitoInput.getAviso());
      solicitudHito.setSolicitudHitoAviso(aviso);

      solicitudHito = repository.save(solicitudHito);

    }

    log.debug("create(SolicitudHito solicitudHito) - end");
    return solicitudHito;
  }

  /**
   * Actualiza los datos de {@link SolicitudHito}.
   * 
   * @param id                 Identificador de la {@link SolicitudHito}
   * @param solicitudHitoInput la entidad {@link SolicitudHitoInput}
   *                           con la información a actualizar.
   * @return {@link SolicitudHito} actualizada.
   */
  @Transactional
  public SolicitudHito update(Long id, SolicitudHitoInput solicitudHitoInput) {
    log.debug("update(SolicitudHito solicitudHito) - start");

    Assert.notNull(solicitudHitoInput.getSolicitudId(),
        "La solicitud no puede ser null para actualizar la SolicitudHito");
    Assert.notNull(solicitudHitoInput.getFecha(),
        "Nombre documento no puede ser null para actualizar la SolicitudHito");
    Assert.notNull(solicitudHitoInput.getTipoHitoId(),
        "La referencia del documento no puede ser null para actualizar la SolicitudHito");

    repository
        .findBySolicitudIdAndFechaAndTipoHitoId(solicitudHitoInput.getSolicitudId(), solicitudHitoInput.getFecha(),
            solicitudHitoInput.getTipoHitoId())
        .ifPresent((solicitudHitoExistente) -> {
          Assert.isTrue(id.equals(solicitudHitoExistente.getId()),
              "Ya existe un Hito con el mismo tipo en esa fecha");
        });

    if (!solicitudRepository.existsById(solicitudHitoInput.getSolicitudId())) {
      throw new SolicitudNotFoundException(solicitudHitoInput.getSolicitudId());
    }

    TipoHito tipoHito = tipoHitoRepository.findById(solicitudHitoInput.getTipoHitoId())
        .orElseThrow(() -> new TipoHitoNotFoundException(solicitudHitoInput.getTipoHitoId()));

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudHitoInput.getSolicitudId()),
        "No se puede modificar SolicitudHito");

    return repository.findById(id).map((solicitudHito) -> {

      solicitudHito.setComentario(solicitudHitoInput.getComentario());
      solicitudHito.setFecha(solicitudHitoInput.getFecha());
      solicitudHito.setTipoHito(tipoHito);

      // Creamos un nuevo aviso
      if (solicitudHitoInput.getAviso() != null && solicitudHito.getSolicitudHitoAviso() == null) {
        SolicitudHitoAviso aviso = this.createAviso(solicitudHito.getId(),
            solicitudHitoInput.getAviso());
        solicitudHito.setSolicitudHitoAviso(aviso);
      }
      // Borramos el aviso
      else if (solicitudHitoInput.getAviso() == null && solicitudHito.getSolicitudHitoAviso() != null) {
        // Comprobamos que se puede borrar el aviso.
        SgiApiInstantTaskOutput task = sgiApiTaskService
            .findInstantTaskById(Long.parseLong(solicitudHito.getSolicitudHitoAviso().getTareaProgramadaRef()));

        Assert.isTrue(task.getInstant().isAfter(Instant.now()), "El aviso ya se ha enviado.");

        sgiApiTaskService
            .deleteTask(Long.parseLong(solicitudHito.getSolicitudHitoAviso().getTareaProgramadaRef()));
        emailService.deleteEmail(Long.parseLong(solicitudHito.getSolicitudHitoAviso().getComunicadoRef()));
        solicitudHitoAvisoRepository.delete(solicitudHito.getSolicitudHitoAviso());
        solicitudHito.setSolicitudHitoAviso(null);
      }
      // Actualizamos el aviso
      else if (solicitudHitoInput.getAviso() != null && solicitudHito.getSolicitudHitoAviso() != null) {
        SgiApiInstantTaskOutput task = sgiApiTaskService
            .findInstantTaskById(Long.parseLong(solicitudHito.getSolicitudHitoAviso().getTareaProgramadaRef()));
        // Solo actualizamos los datos el aviso si este aún no se ha enviado.
        // TODO: Validar realmente el cambio de contenido, y si este ha cambiado,
        // generar error si no se puede editar
        if (task.getInstant().isAfter(Instant.now())) {
          this.emailService.updateSolicitudHitoEmail(
              Long.parseLong(solicitudHito.getSolicitudHitoAviso().getComunicadoRef()), solicitudHito.getId(),
              solicitudHitoInput.getAviso().getAsunto(), solicitudHitoInput.getAviso().getContenido(),
              solicitudHitoInput.getAviso().getDestinatarios().stream()
                  .map(destinatario -> new Recipient(destinatario.getNombre(), destinatario.getEmail()))
                  .collect(Collectors.toList()));

          this.sgiApiTaskService.updateSendEmailTask(
              Long.parseLong(solicitudHito.getSolicitudHitoAviso().getTareaProgramadaRef()),
              Long.parseLong(solicitudHito.getSolicitudHitoAviso().getComunicadoRef()),
              solicitudHitoInput.getAviso().getFechaEnvio());

          solicitudHito.getSolicitudHitoAviso()
              .setIncluirIpsSolicitud(solicitudHitoInput.getAviso().getIncluirIpsSolicitud());
          solicitudHitoAvisoRepository.save(solicitudHito.getSolicitudHitoAviso());
        }
      }
      log.debug("update(SolicitudHito solicitudHito) - end");
      return repository.save(solicitudHito);
    }).orElseThrow(() -> new SolicitudHitoNotFoundException(id));
  }

  private SolicitudHitoAviso createAviso(Long solicitudHitoId, SolicitudHitoAvisoInput avisoInput) {
    Instant now = Instant.now();
    Assert.isTrue(avisoInput.getFechaEnvio().isAfter(now),
        "La fecha de envio debe ser anterior a " + now.toString());

    Long emailId = this.emailService.createSolicitudHitoEmail(
        solicitudHitoId,
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

    SolicitudHitoAviso aviso = new SolicitudHitoAviso();
    aviso.setComunicadoRef(emailId.toString());
    aviso.setTareaProgramadaRef(taskId.toString());
    aviso.setIncluirIpsSolicitud(avisoInput.getIncluirIpsSolicitud());
    return solicitudHitoAvisoRepository.save(aviso);
  }

  /**
   * Obtiene una entidad {@link SolicitudHito} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudHito}.
   * @return SolicitudHito la entidad {@link SolicitudHito}.
   */
  public SolicitudHito findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudHito returnValue = repository.findById(id).orElseThrow(() -> new SolicitudHitoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudHito}.
   *
   * @param id Id del {@link SolicitudHito}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "SolicitudHito id no puede ser null para eliminar un SolicitudHito");
    if (!repository.existsById(id)) {
      throw new SolicitudHitoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudHito} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link SolicitudHito} de la {@link Solicitud}
   *         paginadas.
   */
  public Page<SolicitudHito> findAllBySolicitud(Long solicitudId, String query, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - start");

    Specification<SolicitudHito> specs = SolicitudHitoSpecifications.bySolicitudId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudHito> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene el listado de destinatarios adicionales a los que enviar el email
   * generado por un hito en base al {@link SolicitudHitoAviso} relacionadao
   * 
   * @param id identificador de {@link SolicitudHito}
   * @return listado de {@link Recipient}
   */
  public List<Recipient> getDeferredRecipients(Long id) {
    SolicitudHito hito = repository.findById(id).orElseThrow(() -> new SolicitudHitoNotFoundException(id));
    List<Recipient> recipients = new ArrayList<>();
    List<String> solicitantes = new ArrayList<>();
    if (hito.getSolicitudHitoAviso() != null) {
      if (Boolean.TRUE.equals(hito.getSolicitudHitoAviso().getIncluirIpsSolicitud())) {
        Optional<Solicitud> solicitud = this.solicitudRepository.findById(hito.getSolicitudId());
        if (solicitud.isPresent()) {
          solicitantes.add(solicitud.get().getSolicitanteRef());
        }
      }
      if (!CollectionUtils.isEmpty(solicitantes)) {
        recipients = ComConverter.toRecipients(personaService.findAllByIdIn(solicitantes));
      }
    }

    return recipients;
  }
}