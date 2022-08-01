package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.prc.dto.com.EmailOutput;
import org.crue.hercules.sgi.prc.dto.com.PrcComProcesoBaremacionErrorData;
import org.crue.hercules.sgi.prc.dto.com.PrcComProcesoBaremacionFinData;
import org.crue.hercules.sgi.prc.dto.com.PrcComValidarItemData;
import org.crue.hercules.sgi.prc.dto.com.Recipient;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComunicadosService {

  private static final String DESTINATARIOS_PROCESO_BAREMACION = "prc-proceso-baremacion-destinatarios";

  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiApiCspService cspService;
  private final SgiApiSgpService sgpService;

  /**
   * Envia el comunicado de error en el proceso de baremacion en la
   * {@link ConvocatoriaBaremacion}
   * 
   * @param convocatoriaBaremacion la {@link ConvocatoriaBaremacion}
   * @param error                  texto error
   */
  public void enviarComunicadoErrorProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion, String error) {
    log.debug(
        "enviarComunicadoErrorProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion, String error) - start");
    try {
      this.buildComunicadoErrorProcesoBaremacion(convocatoriaBaremacion, error)
          .ifPresent(email -> this.emailService.sendEmail(email.getId()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.debug(
        "enviarComunicadoErrorProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion, String error) - end");
  }

  /**
   * Envia el comunicado de fin del proceso de baremacion de la
   * {@link ConvocatoriaBaremacion}
   * 
   * @param convocatoriaBaremacion la {@link ConvocatoriaBaremacion}
   */
  public void enviarComunicadoFinProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) {
    log.debug("enviarComunicadoFinProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) - start");
    try {
      this.buildComunicadoFinProcesoBaremacion(convocatoriaBaremacion)
          .ifPresent(email -> this.emailService.sendEmail(email.getId()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.debug("enviarComunicadoFinProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) - end");
  }

  /**
   * Envia el comunicado de valicacion de items
   * {@link ConvocatoriaBaremacion}
   * 
   * @param epigrafeCVN nombre del epigrafe
   * @param tituloItem  titulo del item
   * @param fechaItem   fecha del item
   * @param personaRefs personasRef de los autores del item
   */
  public void enviarComunicadoValidarItem(String epigrafeCVN, String tituloItem, Instant fechaItem,
      List<String> personaRefs) {

    log.debug("enviarComunicadoValidarItem(String epigrafeCVN, String tituloItem, String fechaItem) - start");
    try {
      this.buildComunicadoValidarItem(epigrafeCVN, tituloItem, fechaItem, personaRefs)
          .ifPresent(email -> this.emailService.sendEmail(email.getId()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.debug("enviarComunicadoValidarItem(String epigrafeCVN, String tituloItem, String fechaItem) - end");
  }

  private Optional<EmailOutput> buildComunicadoErrorProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion,
      String error) {
    PrcComProcesoBaremacionErrorData data = PrcComProcesoBaremacionErrorData
        .builder()
        .anio(convocatoriaBaremacion.getAnio().toString())
        .error(error)
        .build();

    EmailOutput comunicado = null;
    try {
      List<Recipient> recipients = getRecipientsProcesoBaremacion();
      comunicado = this.emailService.createComunicadoErrorProcesoBaremacion(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado != null ? Optional.of(comunicado) : Optional.empty();
  }

  private Optional<EmailOutput> buildComunicadoFinProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) {
    PrcComProcesoBaremacionFinData data = PrcComProcesoBaremacionFinData
        .builder()
        .anio(convocatoriaBaremacion.getAnio().toString())
        .build();

    EmailOutput comunicado = null;
    try {
      List<Recipient> recipients = getRecipientsProcesoBaremacion();
      comunicado = this.emailService.createComunicadoFinProcesoBaremacion(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado != null ? Optional.of(comunicado) : Optional.empty();
  }

  private Optional<EmailOutput> buildComunicadoValidarItem(String epigrafeCVN, String tituloItem, Instant fechaItem,
      List<String> personaRefs) {
    if (personaRefs.isEmpty()) {
      log.debug(
          "buildComunicadoValidarItem(String epigrafeCVN, String tituloItem, String fechaItem, List<String> personaRefs) - No se envia el comunicado porque no hay destinatarios");
      return Optional.empty();
    }

    PrcComValidarItemData data = PrcComValidarItemData
        .builder()
        .nombreEpigrafe(epigrafeCVN)
        .tituloItem(tituloItem)
        .fechaItem(fechaItem)
        .build();

    EmailOutput comunicado = null;
    try {
      List<Recipient> recipients = getEmailsInvestigadoresPrincipalesAndAutorizados(personaRefs);
      if (recipients.isEmpty()) {
        log.debug(
            "buildComunicadoValidarItem(String epigrafeCVN, String tituloItem, String fechaItem, List<String> personaRefs) - No se envia el comunicado porque no hay destinatarios asociados a los personaRefs");
        return Optional.empty();
      }
      comunicado = this.emailService.createComunicadoValidarItem(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado != null ? Optional.of(comunicado) : Optional.empty();
  }

  private List<Recipient> getRecipientsProcesoBaremacion() throws JsonProcessingException {
    List<String> destinatarios = configService
        .findStringListByName(DESTINATARIOS_PROCESO_BAREMACION);

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }

  private List<Recipient> getEmailsInvestigadoresPrincipalesAndAutorizados(List<String> personaRefs) {
    return cspService.findPersonaRefInvestigadoresPrincipalesAndAutorizadasByPersonaRefs(personaRefs)
        .stream().map(this::getRecipientsFromPersonaRef).flatMap(List::stream)
        .collect(Collectors.toList());
  }

  /**
   * Obtiene los emails de la personaRef recibida
   * 
   * @param personaRef id del proyecto
   * @return lista @link{Recipient}
   */
  private List<Recipient> getRecipientsFromPersonaRef(String personaRef) {
    List<Recipient> recipients = new ArrayList<>();
    Optional<PersonaDto> persona = sgpService.findPersonaById(personaRef);
    if (persona.isPresent()) {
      recipients = persona.get().getEmails().stream()
          .map(email -> Recipient.builder().name(email.getEmail()).address(email.getEmail()).build())
          .collect(Collectors.toList());
    }
    return recipients;
  }

}
