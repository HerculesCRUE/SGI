package org.crue.hercules.sgi.eti.service.sgi;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.com.EmailInput;
import org.crue.hercules.sgi.eti.dto.com.EmailInput.Deferrable;
import org.crue.hercules.sgi.eti.dto.com.EmailOutput;
import org.crue.hercules.sgi.eti.dto.com.EmailParam;
import org.crue.hercules.sgi.eti.dto.com.Recipient;
import org.crue.hercules.sgi.eti.dto.com.Status;
import org.crue.hercules.sgi.eti.enums.ServiceType;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiComService extends SgiApiBaseService {
  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_NAME = "GENERIC_EMAIL_TEXT";
  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_CONTENT = "GENERIC_CONTENT_TEXT";
  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_SUBJECT = "GENERIC_SUBJECT";

  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION = "ETI_COM_CONVOCATORIA_REUNION";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_NOMBRE_INVESTIGACION = "ETI_COMITE_NOMBRE_INVESTIGACION";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_COMITE = "ETI_COMITE";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_FECHA_EVALUACION = "ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_HORA_INICIO = "ETI_CONVOCATORIA_REUNION_HORA_INICIO";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_MINUTO_INICIO = "ETI_CONVOCATORIA_REUNION_MINUTO_INICIO";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_HORA_INICIO_SEGUNDA = "ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_MINUTO_INICIO_SEGUNDA = "ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_ORDEN_DEL_DIA = "ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA";

  private final SgiConfigProperties sgiConfigProperties;

  public SgiApiComService(RestApiProperties restApiProperties, RestTemplate restTemplate,
      SgiConfigProperties sgiConfigProperties) {
    super(restApiProperties, restTemplate);
    this.sgiConfigProperties = sgiConfigProperties;
  }

  /**
   * Crea un email genérico en el modulo COM
   * 
   * @param subject              Asunto del email
   * @param content              Contenido del email
   * @param recipients           Destinatarios del email
   * @param deferrableRecipients Resolutor de los destinatarios adicionales a
   *                             resolver en el momento del envio
   * @return Email creado
   */
  public EmailOutput createGenericEmailText(String subject, String content, List<Recipient> recipients,
      Deferrable deferrableRecipients) {
    log.debug("createGenericEmailText({}, {}, {}, {}) - start", subject, content, recipients, deferrableRecipients);

    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = "/emails";
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    EmailInput emailRequest = EmailInput.builder().template(TEMPLATE_GENERIC_EMAIL_TEXT_NAME).recipients(recipients)
        .deferrableRecipients(deferrableRecipients).build();
    emailRequest.setParams(new ArrayList<>());
    emailRequest.getParams()
        .add(new EmailParam(TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_CONTENT, content));
    emailRequest.getParams()
        .add(new EmailParam(TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_SUBJECT, subject));

    final EmailOutput response = super.<EmailInput, EmailOutput>callEndpoint(mergedURL, httpMethod, emailRequest,
        new ParameterizedTypeReference<EmailOutput>() {
        }).getBody();

    log.debug("createGenericEmailText({}, {}, {}, {}) - end", subject, content, recipients, deferrableRecipients);
    return response;
  }

  /**
   * Actualiza un email genérico en el modulo COM
   * 
   * @param id                   Identificador del email
   * @param subject              Asunto del email
   * @param content              Contenido del email
   * @param recipients           Destinatarios del email
   * @param deferrableRecipients Resolutor de los destinatarios adicionales a
   *                             resolver en el momento del envio
   * @return Email creado
   */
  public EmailOutput updateGenericEmailText(Long id, String subject, String content, List<Recipient> recipients,
      Deferrable deferrableRecipients) {
    log.debug("updateGenericEmailText({}, {}, {}, {}, {}) - start", id, subject, content, recipients,
        deferrableRecipients);

    Assert.notNull(id, "ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = "/emails/{id}";
    HttpMethod httpMethod = HttpMethod.PUT;
    String mergedURL = buildUri(serviceType, relativeUrl);

    EmailInput emailRequest = EmailInput.builder().template(TEMPLATE_GENERIC_EMAIL_TEXT_NAME).recipients(recipients)
        .deferrableRecipients(deferrableRecipients).build();
    emailRequest.setParams(new ArrayList<>());
    emailRequest.getParams()
        .add(new EmailParam(TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_CONTENT, content));
    emailRequest.getParams()
        .add(new EmailParam(TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_SUBJECT, subject));

    final EmailOutput response = super.<EmailInput, EmailOutput>callEndpoint(mergedURL, httpMethod, emailRequest,
        new ParameterizedTypeReference<EmailOutput>() {
        }, id).getBody();
    log.debug("updateGenericEmailText({}, {}, {}, {}, {}) - end", id, subject, content, recipients,
        deferrableRecipients);
    return response;
  }

  /**
   * Elimina un email del modulo COM
   * 
   * @param id Identificador del email
   */
  public void deleteEmail(Long id) {
    log.debug("deleteEmail({}) - start", id);

    Assert.notNull(id, "ID is required");

    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = "/emails/{id}";
    HttpMethod httpMethod = HttpMethod.DELETE;
    String mergedURL = buildUri(serviceType, relativeUrl);

    super.<Void>callEndpoint(mergedURL, httpMethod, new ParameterizedTypeReference<Void>() {
    }, id);

    log.debug("deleteEmail({}) - end", id);
  }

  /**
   * Crea un email en el modulo COM para el aviso del envío de una convocatoria de
   * reunión de ética
   * 
   * @param convocatoriaReunion datos de ConvocatoriaReunion
   * @param recipients          lista de destinatarios
   * @return EmailOutput información del email creado
   * @throws JsonProcessingException en caso de que se produzca un error
   *                                 convirtiendo data a JSON
   */
  public EmailOutput createComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion,
      List<Recipient> recipients) throws JsonProcessingException {
    log.debug(
        "createComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion, List<Recipient> recipients) - start");

    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = "/emails";
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    String hora = String.format("%02d", convocatoriaReunion.getHoraInicio());
    String minuto = String.format("%02d", convocatoriaReunion.getMinutoInicio());
    String horaSegunda = String.format("%02d", convocatoriaReunion.getHoraInicioSegunda());
    String minutoSegunda = String.format("%02d", convocatoriaReunion.getMinutoInicioSegunda());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy")
        .withZone(sgiConfigProperties.getTimeZone().toZoneId()).withLocale(LocaleContextHolder.getLocale());
    String fechaEvaluacion = formatter.format(convocatoriaReunion.getFechaEvaluacion());

    EmailInput request = EmailInput.builder().template(
        TEMPLATE_ETI_COM_CONVOCATORIA_REUNION).recipients(recipients)
        .build();
    request.setParams(Arrays.asList(
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_NOMBRE_INVESTIGACION,
            convocatoriaReunion.getComite().getNombreInvestigacion()),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_COMITE,
            convocatoriaReunion.getComite().getComite()),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_FECHA_EVALUACION,
            fechaEvaluacion),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_HORA_INICIO,
            hora),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_MINUTO_INICIO,
            minuto),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_HORA_INICIO_SEGUNDA,
            horaSegunda),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_MINUTO_INICIO_SEGUNDA,
            minutoSegunda),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_ORDEN_DEL_DIA,
            convocatoriaReunion.getOrdenDia())));

    final EmailOutput response = super.<EmailInput, EmailOutput>callEndpoint(mergedURL,
        httpMethod, request,
        new ParameterizedTypeReference<EmailOutput>() {
        }).getBody();

    log.debug(
        "createComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion, List<Recipient> recipients) - end");
    return response;
  }

  /**
   * Invoca el env&iacute;o de un email en el modulo COM
   * 
   * @param id identificador de email a enviar
   * @return Status el estado del env&iacute;o
   */
  public Status sendEmail(Long id) {
    log.debug("sendEmail(Long id) - start");
    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = "/emails/{id}/send";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final Status response = super.<Status>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<Status>() {
        }, id).getBody();

    log.debug("sendEmail(Long id) - end");
    return response;
  }

}
