package org.crue.hercules.sgi.pii.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import org.crue.hercules.sgi.pii.config.SgiConfigProperties;
import org.crue.hercules.sgi.pii.dto.com.EmailOutput;
import org.crue.hercules.sgi.pii.dto.com.PiiComFechaLimiteProcedimientoData;
import org.crue.hercules.sgi.pii.dto.com.Recipient;
import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.crue.hercules.sgi.pii.repository.ProcedimientoRepository;
import org.crue.hercules.sgi.pii.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.pii.service.sgi.SgiApiComService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcedimientoComService {

  private static final String CONFIG_PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DESTINATARIOS = "pii-fecha-limite-procedimiento-destinatarios";

  private final ProcedimientoRepository procedimientoRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Envia los comunicados {@code PII_COM_FECHA_LIMITE_PROCEDIMIENTO} para los
   * {@link Procedimiento} cuya fecha limite acción este tres dias habiles después
   * de la fecha actual y que tengan configurado el envio de avisos
   */
  public void enviarComunicadoFechaLimiteProcedimiento() {
    log.debug("enviarComunicadoFechaLimiteProcedimiento() - start");

    this.getDateThreeWorkableDaysAfter().forEach(date -> {
      Instant fechaLimiteFrom = date.atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
          .with(LocalTime.MIN).toInstant();
      Instant fechaLimiteTo = date;

      log.info(
          "enviarComunicadoFechaLimiteProcedimiento() - Recuperando procedimientos cuya fecha limite accion este entre {} y {}",
          fechaLimiteFrom, fechaLimiteTo);

      this.procedimientoRepository.findByFechaLimiteAccionBetweenAndGenerarAvisoTrue(fechaLimiteFrom, fechaLimiteTo)
          .forEach(procedimiento -> {
            log.info("enviarComunicadoFechaLimiteProcedimiento() - procedimientoId: {} - enviando comunicado",
                procedimiento.getId());

            EmailOutput comunicado = this.buildComunicadoFechaLimiteProcedimiento(procedimiento);
            this.emailService.sendEmail(comunicado.getId());
          });
    });

    log.debug("enviarComunicadoFechaLimiteProcedimiento() - end");
  }

  private EmailOutput buildComunicadoFechaLimiteProcedimiento(Procedimiento procedimiento) {

    PiiComFechaLimiteProcedimientoData data = PiiComFechaLimiteProcedimientoData.builder()
        .tipoProcedimiento(procedimiento.getTipoProcedimiento().getDescripcion())
        .fechaLimite(procedimiento.getFechaLimiteAccion())
        .accionATomar(procedimiento.getAccionATomar())
        .build();

    EmailOutput comunicado = null;

    try {
      comunicado = this.emailService.createComunicadoFechaLimiteProcedimiento(data, getRecipientsPreconfigurados());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return comunicado;
  }

  /**
   * Obtiene fecha tres dias después de la fecha actual, siendo hoy un día hábil
   * 
   * @return returns List of @link{Instant}
   */
  private List<Instant> getDateThreeWorkableDaysAfter() {
    ZonedDateTime now = getLastInstantOfDay();
    DayOfWeek day = now.getDayOfWeek();
    List<Instant> dates = new LinkedList<>();

    switch (day) {
      case MONDAY:
        dates.add(now.plusDays(3).toInstant());
        break;
      case TUESDAY:
        ZonedDateTime friday = now.plusDays(3);
        ZonedDateTime saturday = getLastInstantOfDay().plusDays(4);
        ZonedDateTime sunday = getLastInstantOfDay().plusDays(5);

        dates.add(friday.toInstant());
        dates.add(saturday.toInstant());
        dates.add(sunday.toInstant());
        break;
      case WEDNESDAY, THURSDAY, FRIDAY:
        dates.add(now.plusDays(5).toInstant());
        break;
      default:
        break;
    }

    return dates;
  }

  private ZonedDateTime getLastInstantOfDay() {
    return Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).withNano(0);
  }

  private List<Recipient> getRecipientsPreconfigurados() throws JsonProcessingException {
    List<String> destinatarios = configService
        .findStringListByName(CONFIG_PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DESTINATARIOS);

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder()
            .name(destinatario)
            .address(destinatario)
            .build())
        .toList();
  }

}
