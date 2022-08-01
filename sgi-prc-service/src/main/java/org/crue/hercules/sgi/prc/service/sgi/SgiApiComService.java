package org.crue.hercules.sgi.prc.service.sgi;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.com.EmailInput;
import org.crue.hercules.sgi.prc.dto.com.EmailOutput;
import org.crue.hercules.sgi.prc.dto.com.EmailParam;
import org.crue.hercules.sgi.prc.dto.com.PrcComProcesoBaremacionErrorData;
import org.crue.hercules.sgi.prc.dto.com.PrcComProcesoBaremacionFinData;
import org.crue.hercules.sgi.prc.dto.com.PrcComValidarItemData;
import org.crue.hercules.sgi.prc.dto.com.Recipient;
import org.crue.hercules.sgi.prc.dto.com.Status;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiComService extends SgiApiBaseService {
  private static final String PARAM_SUFFIX = "_DATA";
  private static final String PATH_DELIMITER = "/";
  private static final String PATH_EMAILS = PATH_DELIMITER + "emails";
  private static final String PATH_PARAMETER_ID = PATH_DELIMITER + "{id}";

  private static final String TEMPLATE_PRC_COM_PROCESO_BAREMACION_ERROR = "PRC_COM_PROCESO_BAREMACION_ERROR";
  private static final String TEMPLATE_PRC_COM_PROCESO_BAREMACION_ERROR_PARAM = TEMPLATE_PRC_COM_PROCESO_BAREMACION_ERROR
      + PARAM_SUFFIX;

  private static final String TEMPLATE_PRC_COM_PROCESO_BAREMACION_FIN = "PRC_COM_PROCESO_BAREMACION_FIN";
  private static final String TEMPLATE_PRC_COM_PROCESO_BAREMACION_FIN_PARAM = TEMPLATE_PRC_COM_PROCESO_BAREMACION_FIN
      + PARAM_SUFFIX;

  private static final String TEMPLATE_PRC_COM_VALIDAR_ITEM = "PRC_COM_VALIDAR_ITEM";
  private static final String TEMPLATE_PRC_COM_VALIDAR_ITEM_PARAM = TEMPLATE_PRC_COM_VALIDAR_ITEM + PARAM_SUFFIX;

  private final ObjectMapper mapper;

  public SgiApiComService(RestApiProperties restApiProperties, RestTemplate restTemplate, ObjectMapper mapper) {
    super(restApiProperties, restTemplate);
    this.mapper = mapper;
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
    String relativeUrl = PATH_EMAILS + PATH_PARAMETER_ID + "/send";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final Status response = super.<Status>callEndpoint(mergedURL, httpMethod, new ParameterizedTypeReference<Status>() {
    }, id).getBody();

    log.debug("sendEmail(Long id) - end");
    return response;
  }

  public EmailOutput createComunicadoErrorProcesoBaremacion(PrcComProcesoBaremacionErrorData data,
      List<Recipient> recipients) throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_PRC_COM_PROCESO_BAREMACION_ERROR,
        TEMPLATE_PRC_COM_PROCESO_BAREMACION_ERROR_PARAM);
  }

  public EmailOutput createComunicadoFinProcesoBaremacion(PrcComProcesoBaremacionFinData data,
      List<Recipient> recipients) throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_PRC_COM_PROCESO_BAREMACION_FIN,
        TEMPLATE_PRC_COM_PROCESO_BAREMACION_FIN_PARAM);
  }

  public EmailOutput createComunicadoValidarItem(PrcComValidarItemData data,
      List<Recipient> recipients) throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_PRC_COM_VALIDAR_ITEM,
        TEMPLATE_PRC_COM_VALIDAR_ITEM_PARAM);
  }

  /**
   * Crea un email
   * 
   * @param <T>           Tipo del data
   * @param data          Parametros del email
   * @param recipients    Destinatarios del email
   * @param template      Template del contenido del email
   * @param templateParam Template de los parametros del email
   * @return Email creado
   * @throws JsonProcessingException
   */
  private <T> EmailOutput createComunicado(T data, List<Recipient> recipients, String template, String templateParam)
      throws JsonProcessingException {

    Assert.notNull(data, "ConvocatoriaHito ID is required");
    Assert.notNull(template, "Template is required");
    Assert.notNull(templateParam, "TemplateParam is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = PATH_EMAILS;
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);
    EmailInput request = EmailInput.builder().template(template).recipients(recipients).build();
    request.setParams(Arrays.asList(new EmailParam(templateParam, mapper.writeValueAsString(data))));
    return super.<EmailInput, EmailOutput>callEndpoint(mergedURL, httpMethod, request,
        new ParameterizedTypeReference<EmailOutput>() {
        }).getBody();
  }

}
