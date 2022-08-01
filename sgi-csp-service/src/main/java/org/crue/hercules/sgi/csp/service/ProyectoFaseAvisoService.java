package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.converter.ComConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseAvisoInput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseAviso;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseAvisoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProyectoFaseAvisoService {

  private final SgiApiComService emailService;
  private final SgiApiTpService sgiApiTaskService;
  private final SgiApiSgpService personaService;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final ProyectoFaseAvisoRepository proyectoFaseAvisoRepository;
  private final ProyectoFaseRepository proyectoFaseRepository;

  /**
   * Obtiene el listado de destinatarios adicionales a los que enviar el email
   * generado por una fase en base al {@link ConvocatoriaFaseAviso} relacionado
   * 
   * @param proyectoFaseId identificador de {@link ProyectoFase}
   * @return listado de {@link Recipient}
   */
  public List<Recipient> getDeferredRecipients(Long proyectoFaseId) {
    ProyectoFase fase = proyectoFaseRepository.findById(proyectoFaseId)
        .orElseThrow(() -> new ProyectoFaseNotFoundException(proyectoFaseId));
    List<String> solicitantes = new ArrayList<>();

    solicitantes.addAll(getAditionalSolicitantesIfNeeded(fase.getProyectoId(),
        fase.getProyectoFaseAviso1()));
    solicitantes.addAll(getAditionalSolicitantesIfNeeded(fase.getProyectoId(),
        fase.getProyectoFaseAviso2()));

    List<String> solicitantesDistinct = solicitantes.stream().distinct().collect(Collectors.toList());

    if (!CollectionUtils.isEmpty(solicitantesDistinct)) {
      return ComConverter.toRecipients(personaService.findAllByIdIn(solicitantesDistinct));
    }

    return new LinkedList<>();
  }

  private List<String> getAditionalSolicitantesIfNeeded(Long proyectoId, ProyectoFaseAviso aviso) {
    List<String> solicitantes = new LinkedList<>();
    if (aviso != null && Boolean.TRUE.equals(aviso.getIncluirIpsProyecto())) {
      solicitantes.addAll(proyectoEquipoRepository.findAll(
          ProyectoEquipoSpecifications
              .byProyectoActivoAndProyectoConvocatoriaIdWithIpsActivos(proyectoId))
          .stream().map(ProyectoEquipo::getPersonaRef).collect(Collectors.toList()));
    }
    return solicitantes;
  }

  @Transactional
  public ProyectoFaseAviso create(Long proyectoFaseId, ProyectoFaseAvisoInput avisoInput) {
    if (avisoInput == null) {
      return null;
    }

    Instant now = Instant.now();
    Assert.isTrue(avisoInput.getFechaEnvio().isAfter(now),
        "La fecha de envio debe ser anterior a " + now.toString());

    Long emailId = this.emailService.createProyectoFaseEmail(
        proyectoFaseId,
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

    ProyectoFaseAviso aviso = new ProyectoFaseAviso();
    aviso.setComunicadoRef(emailId.toString());
    aviso.setTareaProgramadaRef(taskId.toString());
    aviso.setIncluirIpsProyecto(avisoInput.getIncluirIpsProyecto());
    return proyectoFaseAvisoRepository.save(aviso);
  }

  /**
   * Comprueba si el aviso entrante es nulo y si existe en la base de datos, si
   * existe, lo intenta borrar
   * 
   * @param avisoInput        aviso entrante
   * @param proyectoFaseAviso aviso persistido
   * @return boolean true if was deleted, false if not
   */
  @Transactional
  public boolean deleteAvisoIfPossible(ProyectoFaseAvisoInput avisoInput,
      ProyectoFaseAviso proyectoFaseAviso) {
    if (avisoInput == null && proyectoFaseAviso != null) {
      // Comprobamos que se puede borrar el aviso.
      SgiApiInstantTaskOutput task = sgiApiTaskService
          .findInstantTaskById(Long.parseLong(proyectoFaseAviso.getTareaProgramadaRef()));

      Assert.isTrue(task.getInstant().isAfter(Instant.now()), "El aviso ya se ha enviado.");

      sgiApiTaskService
          .deleteTask(Long.parseLong(proyectoFaseAviso.getTareaProgramadaRef()));
      emailService.deleteEmail(Long.parseLong(proyectoFaseAviso.getComunicadoRef()));
      proyectoFaseAvisoRepository.delete(proyectoFaseAviso);
      log.debug("El aviso con id: {} fue eliminado correctamente", proyectoFaseAviso.getId());
      return true;
    }
    return false;
  }

  @Transactional
  public void updateAvisoIfNeeded(ProyectoFaseAvisoInput avisoInput, ProyectoFaseAviso proyectoFaseAviso,
      Long convocatoriaFaseId) {
    if (avisoInput != null && proyectoFaseAviso != null) {
      SgiApiInstantTaskOutput task = sgiApiTaskService
          .findInstantTaskById(Long.parseLong(proyectoFaseAviso.getTareaProgramadaRef()));
      // Solo actualizamos los datos el aviso si este aún no se ha enviado.
      // generar error si no se puede editar
      if (task.getInstant().isAfter(Instant.now())) {
        this.emailService.updateConvocatoriaHitoEmail(
            Long.parseLong(proyectoFaseAviso.getComunicadoRef()), convocatoriaFaseId,
            avisoInput.getAsunto(), avisoInput.getContenido(),
            avisoInput.getDestinatarios().stream()
                .map(destinatario -> new Recipient(destinatario.getNombre(), destinatario.getEmail()))
                .collect(Collectors.toList()));

        this.sgiApiTaskService.updateSendEmailTask(
            Long.parseLong(proyectoFaseAviso.getTareaProgramadaRef()),
            Long.parseLong(proyectoFaseAviso.getComunicadoRef()),
            avisoInput.getFechaEnvio());

        proyectoFaseAviso.setIncluirIpsProyecto(avisoInput.getIncluirIpsProyecto());
        proyectoFaseAvisoRepository.save(proyectoFaseAviso);
      }
    }
  }

}
