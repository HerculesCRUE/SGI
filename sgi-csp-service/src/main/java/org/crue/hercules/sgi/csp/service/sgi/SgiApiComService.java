package org.crue.hercules.sgi.csp.service.sgi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComInicioPresentacionGastoData;
import org.crue.hercules.sgi.csp.dto.com.EmailInput;
import org.crue.hercules.sgi.csp.dto.com.EmailInput.Deferrable;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.EmailParam;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.com.Status;
import org.crue.hercules.sgi.csp.enums.ServiceType;
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

  private static final String TEMPLATE_CSP_COM_INICIO_PRESENTACION_GASTO = "CSP_COM_INICIO_PRESENTACION_GASTO";
  private static final String TEMPLATE_CSP_COM_INICIO_PRESENTACION_GASTO_PARAM = TEMPLATE_CSP_COM_INICIO_PRESENTACION_GASTO
      + "_DATA";

  private static final String CONVOCATORIA_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT = "/convocatoriahitos/%s/deferrable-recipients";

  private final ObjectMapper mapper;

  public SgiApiComService(RestApiProperties restApiProperties, RestTemplate restTemplate, ObjectMapper mapper) {
    super(restApiProperties, restTemplate);
    this.mapper = mapper;
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
   * Crea un email en el modulo COM para el aviso de un hito de convocatoria
   * 
   * @param convocatoriaHitoId Identificador del hito de convocatoria
   * @param subject            Asunto del email
   * @param content            Contenido del email
   * @param recipients         Destinatarios del email
   * @return Identificador del email creado
   */
  public Long createConvocatoriaHitoEmail(Long convocatoriaHitoId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("createConvocatoriaHitoEmail({}, {}, {}, {}) - start", convocatoriaHitoId, subject, content, recipients);

    Assert.notNull(convocatoriaHitoId, "ConvocatoriaHito ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    Long id = this.createGenericEmailText(subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(CONVOCATORIA_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT, convocatoriaHitoId), HttpMethod.GET))
        .getId();
    log.debug("createConvocatoriaHitoEmail({}, {}, {}, {}) - end", convocatoriaHitoId, subject, content, recipients);
    return id;
  }

  /**
   * Actualiza un email en el modulo COM para el aviso de un hito de convocatoria
   * 
   * @param id                 Identificador el email
   * @param convocatoriaHitoId Identificador del hito de convocatoria
   * @param subject            Asunto del email
   * @param content            Contenido del email
   * @param recipients         Destinatarios del email
   */
  public void updateConvocatoriaHitoEmail(Long id, Long convocatoriaHitoId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("updateConvocatoriaHitoEmail({}, {}, {}, {}) - start", id, convocatoriaHitoId, subject, content,
        recipients);

    Assert.notNull(id, "ID is required");
    Assert.notNull(convocatoriaHitoId, "ConvocatoriaHito ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    this.updateGenericEmailText(id, subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(CONVOCATORIA_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT, convocatoriaHitoId), HttpMethod.GET));
    log.debug("updateConvocatoriaHitoEmail({}, {}, {}, {}) - end", id, convocatoriaHitoId, subject, content,
        recipients);
  }

  /**
   * Crea un email en el modulo COM para el aviso de inicio del per&iacute;odo de
   * presentaci&oacute;n de justificaci&oacute;n de gastos
   * 
   * @param data       informaci&oacute;n a incluir en el email
   * @param recipients lista de destinatarios
   * @return EmailOutput informaci&oacute;n del email creado
   * @throws JsonProcessingException en caso de que se produzca un error
   *                                 convirtiendo data a JSON
   */
  public EmailOutput createComunicadoInicioPresentacionJustificacionGastosEmail(CspComInicioPresentacionGastoData data,
      List<Recipient> recipients) throws JsonProcessingException {
    log.debug(
        "createComunicadoInicioPresentacionJustificacionGastosEmail(CspComInicioPresentacionGastoData data, List<Recipient> recipients) - start");

    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = "/emails";
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    EmailInput request = EmailInput.builder().template(
        TEMPLATE_CSP_COM_INICIO_PRESENTACION_GASTO).recipients(recipients)
        .build();
    request.setParams(Arrays.asList(
        new EmailParam(TEMPLATE_CSP_COM_INICIO_PRESENTACION_GASTO_PARAM, mapper.writeValueAsString(data))));

    final EmailOutput response = super.<EmailInput, EmailOutput>callEndpoint(mergedURL, httpMethod, request,
        new ParameterizedTypeReference<EmailOutput>() {
        }).getBody();

    log.debug(
        "createComunicadoInicioPresentacionJustificacionGastosEmail(CspComInicioPresentacionGastoData data, List<Recipient> recipients) - end");
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

    final Status response = super.<Status>callEndpoint(mergedURL, httpMethod, new ParameterizedTypeReference<Status>() {
    }, id).getBody();

    log.debug("sendEmail(Long id) - end");
    return response;
  }

}
