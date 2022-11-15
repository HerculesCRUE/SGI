package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.crue.hercules.sgi.csp.converter.ComConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoHitoInput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoAvisoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoHitoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoHito}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProyectoHitoServiceImpl implements ProyectoHitoService {

  private final ProyectoHitoRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ModeloTipoHitoRepository modeloTipoHitoRepository;
  private final SgiApiComService emailService;
  private final SgiApiTpService sgiApiTaskService;
  private final ProyectoHitoAvisoRepository proyectoHitoAvisoRepository;
  private final SgiApiSgpService personaService;
  private final ProyectoEquipoRepository proyectoEquipoReposiotry;
  private final TipoHitoRepository tipoHitoRepository;

  /**
   * Guarda la entidad {@link ProyectoHito}.
   * 
   * @param proyectoHitoInput la entidad {@link ProyectoHito} a guardar.
   * @return ProyectoHito la entidad {@link ProyectoHito} persistida.
   */
  @Override
  @Transactional
  public ProyectoHito create(ProyectoHitoInput proyectoHitoInput) {
    log.debug("create(ProyectoHito ProyectoHito) - start");

    this.validarRequeridosProyectoHito(proyectoHitoInput);
    this.validarProyectoHito(proyectoHitoInput, null);

    TipoHito tipoHito = tipoHitoRepository.findById(proyectoHitoInput.getTipoHitoId())
        .orElseThrow(() -> new TipoHitoNotFoundException(proyectoHitoInput.getTipoHitoId()));

    ProyectoHito proyectoHito = repository.save(ProyectoHito.builder()
        .comentario(proyectoHitoInput.getComentario())
        .fecha(proyectoHitoInput.getFecha())
        .proyectoId(proyectoHitoInput.getProyectoId())
        .tipoHito(tipoHito)
        .build());

    if (proyectoHitoInput.getAviso() != null) {
      ProyectoHitoAviso aviso = this.createAviso(proyectoHito.getId(),
          proyectoHitoInput.getAviso());
      proyectoHito.setProyectoHitoAviso(aviso);

      proyectoHito = repository.save(proyectoHito);
    }

    return proyectoHito;
  }

  /**
   * Actualiza la entidad {@link ProyectoHito}.
   * 
   * @param proyectoHitoActualizar la entidad {@link ProyectoHito} a guardar.
   * @return ProyectoHito la entidad {@link ProyectoHito} persistida.
   */
  @Override
  @Transactional
  public ProyectoHito update(Long proyectoHitoId, ProyectoHitoInput proyectoHitoActualizar) {
    log.debug("update(ProyectoHito ProyectoHitoActualizar) - start");

    Assert.notNull(proyectoHitoId, "ProyectoHito id no puede ser null para actualizar un ProyectoHito");
    this.validarRequeridosProyectoHito(proyectoHitoActualizar);

    TipoHito tipoHito = tipoHitoRepository.findById(proyectoHitoActualizar.getTipoHitoId())
        .orElseThrow(() -> new TipoHitoNotFoundException(proyectoHitoActualizar.getTipoHitoId()));

    return repository.findById(proyectoHitoId).map(proyectoHito -> {

      validarProyectoHito(proyectoHitoActualizar, proyectoHito);

      proyectoHito.setFecha(proyectoHitoActualizar.getFecha());
      proyectoHito.setComentario(proyectoHitoActualizar.getComentario());
      proyectoHito.setTipoHito(tipoHito);

      this.resolveProyectoHitoAviso(proyectoHitoActualizar, proyectoHito);

      return repository.save(proyectoHito);
    }).orElseThrow(() -> new ProyectoHitoNotFoundException(proyectoHitoId));

  }

  /**
   * Elimina la {@link ProyectoHito}.
   *
   * @param id Id del {@link ProyectoHito}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoHito id no puede ser null para eliminar un ProyectoHito");
    if (!repository.existsById(id)) {
      throw new ProyectoHitoNotFoundException(id);
    }

    Optional<ProyectoHitoAviso> aviso = proyectoHitoAvisoRepository.findByProyectoHitoId(id);
    if (aviso.isPresent()) {
      this.sgiApiTaskService.deleteTask(Long.parseLong(aviso.get().getTareaProgramadaRef()));
      this.emailService.deleteEmail(Long.parseLong(aviso.get().getComunicadoRef()));
      this.proyectoHitoAvisoRepository.delete(aviso.get());
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene {@link ProyectoHito} por su id.
   *
   * @param id el id de la entidad {@link ProyectoHito}.
   * @return la entidad {@link ProyectoHito}.
   */
  @Override
  public ProyectoHito findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoHito returnValue = repository.findById(id).orElseThrow(() -> new ProyectoHitoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene los {@link ProyectoHito} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoHito} del {@link Proyecto}
   *         paginadas.
   */
  @Override
  public Page<ProyectoHito> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoHito> specs = ProyectoHitoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoHito> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Realiza las validaciones necesarias para la creación y modificación de
   * {@link ProyectoHito}
   * 
   * @param datosProyectoHito
   * @param datosOriginales
   */
  private void validarProyectoHito(ProyectoHitoInput datosProyectoHito, ProyectoHito datosOriginales) {

    // Se comprueba la existencia del proyecto
    Long proyectoId = datosProyectoHito.getProyectoId();
    if (proyectoId == null || !proyectoRepository.existsById(proyectoId)) {
      throw new ProyectoNotFoundException(proyectoId);
    }
    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Optional<ModeloEjecucion> modeloEjecucion = proyectoRepository.getModeloEjecucion(proyectoId);
    Long modeloEjecucionId = modeloEjecucion.isPresent() ? modeloEjecucion.get().getId() : null;

    // TipoHito
    Optional<ModeloTipoHito> modeloTipoHito = modeloTipoHitoRepository
        .findByModeloEjecucionIdAndTipoHitoId(modeloEjecucionId, datosProyectoHito.getTipoHitoId());

    // Está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoHito.isPresent(),
        "TipoHito '" + datosProyectoHito.getTipoHitoId() + "' no disponible para el ModeloEjecucion '"
            + ((modeloEjecucion.isPresent()) ? modeloEjecucion.get().getNombre() : "Proyecto sin modelo asignado")
            + "'");

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoHito.get().getActivo(), "ModeloTipoHito '" + modeloTipoHito.get().getTipoHito().getNombre()
        + "' no está activo para el ModeloEjecucion '" + modeloTipoHito.get().getModeloEjecucion().getNombre() + "'");

    // El TipoHito está activo
    Assert.isTrue(modeloTipoHito.get().getTipoHito().getActivo(),
        "TipoHito '" + modeloTipoHito.get().getTipoHito().getNombre() + "' no está activo");

    datosProyectoHito.setTipoHitoId(modeloTipoHito.get().getTipoHito().getId());

    // Si en el campo Fecha se ha indicado una fecha ya pasada, el campo "generar
    // aviso" tomará el valor false, y no será editable.
    if (datosProyectoHito.getFecha().isBefore(Instant.now())) {
      datosProyectoHito.setAviso(null);
    }

    Optional<ProyectoHito> optProyectoHito = repository.findByProyectoIdAndFechaAndTipoHitoId(
        datosProyectoHito.getProyectoId(), datosProyectoHito.getFecha(),
        datosProyectoHito.getTipoHitoId());

    if (optProyectoHito.isPresent() && datosOriginales != null
        && datosOriginales.getId().longValue() == optProyectoHito.get().getId().longValue()) {
      return;
    }

    Assert.isTrue(!optProyectoHito.isPresent(),
        "Ya existe un Hito con el mismo tipo en esa fecha");
  }

  /**
   * Comprueba la presencia de los datos requeridos al crear o modificar
   * {@link ProyectoHito}
   * 
   * @param datosProyectoHito
   */
  private void validarRequeridosProyectoHito(ProyectoHitoInput datosProyectoHito) {

    Assert.isTrue(datosProyectoHito.getProyectoId() != null,
        "Id Proyecto no puede ser null para realizar la acción sobre ProyectoHito");

    Assert.isTrue(datosProyectoHito.getTipoHitoId() != null,
        "Id Tipo Hito no puede ser null para realizar la acción sobre ProyectoHito");

    Assert.notNull(datosProyectoHito.getFecha(), "Fecha no puede ser null para realizar la acción sobre ProyectoHito");
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoHito}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return si existe o no el proyecto
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoId(proyectoId);
  }

  private ProyectoHitoAviso createAviso(Long proyectoHitoId, ProyectoHitoAvisoInput avisoInput) {
    Instant now = Instant.now();
    Assert.isTrue(avisoInput.getFechaEnvio().isAfter(now),
        "La fecha de envio debe ser anterior a " + now.toString());

    Long emailId = this.emailService.createProyectoHitoEmail(
        proyectoHitoId,
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

    return proyectoHitoAvisoRepository.save(ProyectoHitoAviso.builder()
        .comunicadoRef(emailId.toString())
        .tareaProgramadaRef(taskId.toString())
        .incluirIpsProyecto(avisoInput.getIncluirIpsProyecto())
        .build());
  }

  /**
   * Obtiene el listado de destinatarios adicionales a los que enviar el email
   * generado por un hito en base al {@link ProyectoHitoAviso} relacionadao
   * 
   * @param proyectoHitoId identificador de {@link ProyectoHito}
   * @return listado de {@link Recipient}
   */
  @Override
  public List<Recipient> getDeferredRecipients(Long proyectoHitoId) {

    ProyectoHito hito = repository.findById(proyectoHitoId)
        .orElseThrow(() -> new ProyectoHitoNotFoundException(proyectoHitoId));
    List<Recipient> recipients = new ArrayList<>();

    List<String> investigadores = new LinkedList<>();

    if (hito.getProyectoHitoAviso() != null) {
      if (Boolean.TRUE.equals(hito.getProyectoHitoAviso().getIncluirIpsProyecto())) {

        investigadores = this.proyectoEquipoReposiotry
            .findByProyectoIdAndRolProyectoRolPrincipalTrue(hito.getProyectoId()).stream()
            .map(proyectoEquipo -> proyectoEquipo.getPersonaRef()).collect(Collectors.toList());

      }
      if (!CollectionUtils.isEmpty(investigadores)) {
        recipients = ComConverter.toRecipients(personaService.findAllByIdIn(investigadores));
      }
    }

    return recipients;
  }

  private void resolveProyectoHitoAviso(ProyectoHitoInput proyectoHitoInput, ProyectoHito proyectoHito) {
    // Creamos un nuevo aviso
    if (proyectoHitoInput.getAviso() != null && proyectoHito.getProyectoHitoAviso() == null) {
      ProyectoHitoAviso aviso = this.createAviso(proyectoHito.getId(),
          proyectoHitoInput.getAviso());
      proyectoHito.setProyectoHitoAviso(aviso);
    }
    // Borramos el aviso
    else if (proyectoHitoInput.getAviso() == null && proyectoHito.getProyectoHitoAviso() != null) {
      // Comprobamos que se puede borrar el aviso.
      SgiApiInstantTaskOutput task = sgiApiTaskService
          .findInstantTaskById(Long.parseLong(proyectoHito.getProyectoHitoAviso().getTareaProgramadaRef()));

      Assert.isTrue(task.getInstant().isAfter(Instant.now()), "El aviso ya se ha enviado.");

      sgiApiTaskService
          .deleteTask(Long.parseLong(proyectoHito.getProyectoHitoAviso().getTareaProgramadaRef()));
      emailService.deleteEmail(Long.parseLong(proyectoHito.getProyectoHitoAviso().getComunicadoRef()));
      proyectoHitoAvisoRepository.delete(proyectoHito.getProyectoHitoAviso());
      proyectoHito.setProyectoHitoAviso(null);
    }
    // Actualizamos el aviso
    else if (proyectoHitoInput.getAviso() != null && proyectoHito.getProyectoHitoAviso() != null) {
      SgiApiInstantTaskOutput task = sgiApiTaskService
          .findInstantTaskById(Long.parseLong(proyectoHito.getProyectoHitoAviso().getTareaProgramadaRef()));
      // Solo actualizamos los datos el aviso si este aún no se ha enviado.
      // TODO: Validar realmente el cambio de contenido, y si este ha cambiado,
      // generar error si no se puede editar
      if (task.getInstant().isAfter(Instant.now())) {
        this.emailService.updateSolicitudHitoEmail(
            Long.parseLong(proyectoHito.getProyectoHitoAviso().getComunicadoRef()), proyectoHito.getId(),
            proyectoHitoInput.getAviso().getAsunto(), proyectoHitoInput.getAviso().getContenido(),
            proyectoHitoInput.getAviso().getDestinatarios().stream()
                .map(destinatario -> new Recipient(destinatario.getNombre(), destinatario.getEmail()))
                .collect(Collectors.toList()));

        this.sgiApiTaskService.updateSendEmailTask(
            Long.parseLong(proyectoHito.getProyectoHitoAviso().getTareaProgramadaRef()),
            Long.parseLong(proyectoHito.getProyectoHitoAviso().getComunicadoRef()),
            proyectoHitoInput.getAviso().getFechaEnvio());

        proyectoHito.getProyectoHitoAviso()
            .setIncluirIpsProyecto(proyectoHitoInput.getAviso().getIncluirIpsProyecto());
        proyectoHitoAvisoRepository.save(proyectoHito.getProyectoHitoAviso());
      }
    }
  }
}
