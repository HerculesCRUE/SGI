package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.eti.dto.com.EmailOutput;
import org.crue.hercules.sgi.eti.dto.com.Recipient;
import org.crue.hercules.sgi.eti.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComunicadosService {
  private final SgiApiComService emailService;
  private final SgiApiSgpService personasService;
  private final EvaluadorService evaluadorService;

  public ComunicadosService(SgiApiComService emailService, SgiApiSgpService personasService,
      EvaluadorService evaluadorService) {
    this.emailService = emailService;
    this.personasService = personasService;
    this.evaluadorService = evaluadorService;
  }

  public void enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion)
      throws JsonProcessingException {
    log.debug("enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion) - start");
    List<Evaluador> evaluadoresConvocatoriaReunion = evaluadorService
        .findAllByComite(convocatoriaReunion.getComite().getComite());

    List<String> idsPersonaRef = evaluadoresConvocatoriaReunion.stream().map(evaluador -> evaluador.getPersonaRef())
        .collect(Collectors.toList());

    if (idsPersonaRef != null) {
      List<PersonaOutput> personas = personasService.findAllByIdIn(idsPersonaRef);
      List<Recipient> recipients = new ArrayList<Recipient>();
      personas.stream().forEach(persona -> {
        recipients.addAll(persona.getEmails().stream()
            .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
            .collect(Collectors.toList()));
      });

      EmailOutput emailOutput = emailService
          .createComunicadoConvocatoriaReunionEti(convocatoriaReunion, recipients);

      emailService.sendEmail(emailOutput.getId());
    } else {
      log.debug(
          "enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion) - No se puede enviar el comunicado, no existe ninguna persona asociada");
    }
    log.debug("enviarComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion) - end");
  }
}
