package org.crue.hercules.sgi.pii.service.sgi;

import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.pii.config.RestApiProperties;
import org.crue.hercules.sgi.pii.dto.com.EmailInput;
import org.crue.hercules.sgi.pii.dto.com.EmailOutput;
import org.crue.hercules.sgi.pii.dto.com.EmailParam;
import org.crue.hercules.sgi.pii.dto.com.PiiComFechaLimiteProcedimientoData;
import org.crue.hercules.sgi.pii.dto.com.PiiComMesesHastaFinPrioridadSolicitudProteccionData;
import org.crue.hercules.sgi.pii.dto.com.Recipient;
import org.crue.hercules.sgi.pii.dto.com.Status;
import org.crue.hercules.sgi.pii.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiComService extends SgiApiBaseService {

  private static final String DATA = "_DATA";
  private static final String PATH_SEPARATOR = "/";
  private static final String PATH_EMAILS = PATH_SEPARATOR + "emails";
  private static final String PATH_PARAMETER_ID = PATH_SEPARATOR + "{id}";

  private static final String TEMPLATE_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION = "PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION";
  private static final String TEMPLATE_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_PARAM = TEMPLATE_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION
      + DATA;

  private static final String TEMPLATE_PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION = "PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION";
  private static final String TEMPLATE_PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_PARAM = TEMPLATE_PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION
      + DATA;

  private static final String TEMPLATE_PII_COM_FECHA_LIMITE_PROCEDIMIENTO = "PII_COM_FECHA_LIMITE_PROCEDIMIENTO";
  private static final String TEMPLATE_PII_COM_FECHA_LIMITE_PROCEDIMIENTO_PARAM = TEMPLATE_PII_COM_FECHA_LIMITE_PROCEDIMIENTO
      + DATA;

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

  private <T> EmailOutput createComunicado(T data, List<Recipient> recipients, String template, String templateParam)
      throws JsonProcessingException {
    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = PATH_EMAILS;
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);
    EmailInput request = EmailInput.builder().template(template).recipients(recipients).build();
    request.setParams(Arrays.asList(
        new EmailParam(templateParam, mapper.writeValueAsString(data))));
    return super.<EmailInput, EmailOutput>callEndpoint(mergedURL, httpMethod, request,
        new ParameterizedTypeReference<EmailOutput>() {
        }).getBody();
  }

  public EmailOutput createComunicadoMesesHastaFinPrioridadSolicitudProteccion(
      PiiComMesesHastaFinPrioridadSolicitudProteccionData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION,
        TEMPLATE_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_PARAM);
  }

  public EmailOutput createComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion(
      PiiComMesesHastaFinPrioridadSolicitudProteccionData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION,
        TEMPLATE_PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_PARAM);
  }

  public EmailOutput createComunicadoFechaLimiteProcedimiento(PiiComFechaLimiteProcedimientoData data,
      List<Recipient> recipients) throws JsonProcessingException {

    return this.createComunicado(data, recipients,
        TEMPLATE_PII_COM_FECHA_LIMITE_PROCEDIMIENTO,
        TEMPLATE_PII_COM_FECHA_LIMITE_PROCEDIMIENTO_PARAM);
  }
}
