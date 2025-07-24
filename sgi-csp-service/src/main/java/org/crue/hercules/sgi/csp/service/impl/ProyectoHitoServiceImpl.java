package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.crue.hercules.sgi.csp.model.ProyectoHitoComentario;
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
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
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
  private static final String MSG_AVISO_ENVIADO = "avisoEnviado.message";
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_FIELD_FECHA_ENVIO = "fechaEnvio";
  private static final String MSG_FIELD_FECHA_ACTUAL = "fechaActual";
  private static final String MSG_MODEL_MODELO_TIPO_HITO = "org.crue.hercules.sgi.csp.model.ModeloTipoHito.message";
  private static final String MSG_MODEL_TIPO_HITO = "org.crue.hercules.sgi.csp.model.TipoHito.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_INACTIVO = "org.springframework.util.Assert.entity.modeloEjecucion.inactivo.message";
  private static final String MSG_PROYECTO_ASSIGN_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.exceptions.AssignModeloEjecucion.message";
  private static final String MSG_PROYECTO_SIN_MODELO_ASIGNADO = "org.crue.hercules.sgi.csp.model.Proyecto.sinModeloEjecucion.message";
  private static final String MSG_NO_DISPONIBLE_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.noDisponible.message";

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
        .comentario((proyectoHitoInput.getComentario().stream()
            .map(comentario -> new ProyectoHitoComentario(comentario.getLang(), comentario.getValue()))
            .collect(Collectors.toSet())))
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

    AssertHelper.idNotNull(proyectoHitoId, ProyectoHito.class);
    this.validarRequeridosProyectoHito(proyectoHitoActualizar);

    TipoHito tipoHito = tipoHitoRepository.findById(proyectoHitoActualizar.getTipoHitoId())
        .orElseThrow(() -> new TipoHitoNotFoundException(proyectoHitoActualizar.getTipoHitoId()));

    return repository.findById(proyectoHitoId).map(proyectoHito -> {

      validarProyectoHito(proyectoHitoActualizar, proyectoHito);

      List<I18nFieldValueDto> listaComentario = proyectoHitoActualizar.getComentario();

      Set<ProyectoHitoComentario> setComentario = listaComentario.stream()
          .map(dto -> {
            ProyectoHitoComentario comentario = new ProyectoHitoComentario(dto.getLang(), dto.getValue());
            return comentario;
          })
          .collect(Collectors.toSet());

      proyectoHito.setFecha(proyectoHitoActualizar.getFecha());
      proyectoHito.setComentario(setComentario);
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

    AssertHelper.idNotNull(id, ProyectoHito.class);
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
        () -> ProblemMessage.builder()
            .key(MSG_PROYECTO_ASSIGN_MODELO_EJECUCION)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO))
            .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
            .parameter(MSG_KEY_MODELO, ((modeloEjecucion.isPresent()) ? modeloEjecucion.get().getNombre()
                : ApplicationContextSupport.getMessage(MSG_PROYECTO_SIN_MODELO_ASIGNADO)))
            .build());

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoHito.get().getActivo(),
        () -> ProblemMessage.builder()
            .key(MSG_MODELO_EJECUCION_INACTIVO)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_HITO))
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

    AssertHelper.entityExists(!optProyectoHito.isPresent(), Proyecto.class, ProyectoHito.class);
  }

  /**
   * Comprueba la presencia de los datos requeridos al crear o modificar
   * {@link ProyectoHito}
   * 
   * @param datosProyectoHito
   */
  private void validarRequeridosProyectoHito(ProyectoHitoInput datosProyectoHito) {

    AssertHelper.idNotNull(datosProyectoHito.getProyectoId(), Proyecto.class);
    AssertHelper.idNotNull(datosProyectoHito.getTipoHitoId(), TipoHito.class);
    AssertHelper.fieldNotNull(datosProyectoHito.getFecha(), ProyectoHito.class, AssertHelper.MESSAGE_KEY_DATE);

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
    AssertHelper.fieldBefore(avisoInput.getFechaEnvio().isAfter(now), MSG_FIELD_FECHA_ENVIO, MSG_FIELD_FECHA_ACTUAL);

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

      Assert.isTrue(task.getInstant().isAfter(Instant.now()),
          ApplicationContextSupport.getMessage(MSG_AVISO_ENVIADO));

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
