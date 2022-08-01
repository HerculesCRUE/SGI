package org.crue.hercules.sgi.csp.service.sgi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComAddModCertAutorizacionPartProyectoExtData;
import org.crue.hercules.sgi.csp.dto.com.CspComCalendarioFacturacionNotificarData;
import org.crue.hercules.sgi.csp.dto.com.CspComCalendarioFacturacionValidarIPData;
import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoParticipacionAutorizacionProyectoExternoData;
import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoSolicitadaSolTipoRrhhData;
import org.crue.hercules.sgi.csp.dto.com.CspComInicioPresentacionGastoData;
import org.crue.hercules.sgi.csp.dto.com.CspComInicioPresentacionSeguimientoCientificoData;
import org.crue.hercules.sgi.csp.dto.com.CspComModificacionEstadoParticipacionProyectoExternoData;
import org.crue.hercules.sgi.csp.dto.com.CspComPeriodoJustificacionSocioData;
import org.crue.hercules.sgi.csp.dto.com.CspComPresentacionSeguimientoCientificoIpData;
import org.crue.hercules.sgi.csp.dto.com.CspComRecepcionNotificacionesCVNProyectoExtData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoAlegacionesData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoDefinitivoData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoProvisionalData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoSolicitadaData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudPeticionEvaluacionData;
import org.crue.hercules.sgi.csp.dto.com.CspComVencimientoPeriodoPagoSocioData;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiComService extends SgiApiBaseService {
  private static final String DATA = "_DATA";
  private static final String PATH_EMAILS = "/emails";
  private static final String PATH_PARAMETER_ID = "/{id}";

  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_NAME = "GENERIC_EMAIL_TEXT";
  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_CONTENT = "GENERIC_CONTENT_TEXT";
  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_SUBJECT = "GENERIC_SUBJECT";

  private static final String TEMPLATE_CSP_COM_INICIO_PRESENTACION_GASTO = "CSP_COM_INICIO_PRESENTACION_GASTO";
  private static final String TEMPLATE_CSP_COM_INICIO_PRESENTACION_GASTO_PARAM = TEMPLATE_CSP_COM_INICIO_PRESENTACION_GASTO
      + DATA;
  private static final String TEMPLATE_CSP_COM_SOLICITUD_PETICION_EVALUACION = "CSP_COM_SOLICITUD_PETICION_EVALUACION";
  private static final String TEMPLATE_CSP_COM_SOLICITUD_PETICION_EVALUACION_PARAM_PETICION_EVALUACION_CODIGO = "ETI_PETICION_EVALUACION_CODIGO";
  private static final String TEMPLATE_CSP_COM_SOLICITUD_PETICION_EVALUACION_PARAM_SOLICITUD_CODIGO = "CSP_SOLICITUD_CODIGO";

  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_SOLICITADA = "CSP_COM_SOL_CAMB_EST_SOLICITADA";
  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_SOLICITADA_PARAM = TEMPLATE_CSP_COM_SOL_CAMB_EST_SOLICITADA
      + DATA;

  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_ALEGACIONES = "CSP_COM_SOL_CAMB_EST_ALEGACIONES";
  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_ALEGACIONES_PARAM = TEMPLATE_CSP_COM_SOL_CAMB_EST_ALEGACIONES
      + DATA;

  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_PROV = "CSP_COM_SOL_CAMB_EST_EXCL_PROV";
  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_PROV_PARAM = TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_PROV
      + DATA;

  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_DEF = "CSP_COM_SOL_CAMB_EST_EXCL_DEF";
  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_DEF_PARAM = TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_DEF
      + DATA;

  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC = "CSP_COM_SOL_CAMB_EST_CONC";
  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC_PARAM = TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC
      + DATA;

  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC_PROV = "CSP_COM_SOL_CAMB_EST_CONC_PROV";
  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC_PROV_PARAM = TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC_PROV
      + DATA;

  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN = "CSP_COM_SOL_CAMB_EST_DEN";
  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN_PARAM = TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN
      + DATA;

  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN_PROV = "CSP_COM_SOL_CAMB_EST_DEN_PROV";
  private static final String TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN_PROV_PARAM = TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN_PROV
      + DATA;

  private static final String TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO = "CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO";
  private static final String TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_PARAM = TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO
      + DATA;

  private static final String TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP = "CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP";
  private static final String TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_PARAM = TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP
      + DATA;

  private static final String TEMPLATE_CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP = "CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP";
  private static final String TEMPLATE_CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_PARAM = TEMPLATE_CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP
      + DATA;

  private static final String TEMPLATE_CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO = "CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO";
  private static final String TEMPLATE_CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_PARAM = TEMPLATE_CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO
      + DATA;

  private static final String TEMPLATE_CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO = "CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO";
  private static final String TEMPLATE_CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_PARAM = TEMPLATE_CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO
      + DATA;

  private static final String TEMPLATE_CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO = "CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO";
  private static final String TEMPLATE_CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_PARAM = TEMPLATE_CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA = "CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA = "CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO = "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA = "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST = "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST = "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO = "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO = "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO
      + DATA;

  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST = "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST";
  private static final String TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_PARAM = TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST
      + DATA;

  private static final String CONVOCATORIA_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT = "/convocatoriahitos/%s/deferrable-recipients";
  private static final String SOLICITUD_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT = "/solicitudhitos/%s/deferrable-recipients";
  private static final String PROYECTO_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT = "/proyectohitos/%s/deferrable-recipients";
  private static final String CONVOCATORIA_FASE_DEFERRABLE_RECIPIENTS_URI_FORMAT = "/convocatoriafases/%s/deferrable-recipients";
  private static final String PROYECTO_FASE_DEFERRABLE_RECIPIENTS_URI_FORMAT = "/proyectofases/%s/deferrable-recipients";

  private static final String TEMPLATE_CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO = "CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO";
  private static final String TEMPLATE_CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_PARAM = TEMPLATE_CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO
      + DATA;

  private static final String TEMPLATE_CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO = "CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO";
  private static final String TEMPLATE_CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_PARAM = TEMPLATE_CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO
      + DATA;

  private static final String TEMPLATE_CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO = "CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO";
  private static final String TEMPLATE_CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_PARAM = TEMPLATE_CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO
      + DATA;

  private static final String TEMPLATE_CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO = "CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO";
  private static final String TEMPLATE_CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_PARAM = TEMPLATE_CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO
      + DATA;

  private static final String TEMPLATE_CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH = "CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH";
  private static final String TEMPLATE_CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_PARAM = TEMPLATE_CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH
      + DATA;

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
    String relativeUrl = PATH_EMAILS;
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
    String relativeUrl = PATH_EMAILS + PATH_PARAMETER_ID;
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
    String relativeUrl = PATH_EMAILS + PATH_PARAMETER_ID;
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
   * Crea un email en el modulo COM para el aviso de un hito de solicitud
   *
   * @param solicitudHitoId Identificador del hito de solicitud
   * @param subject         Asunto del email
   * @param content         Contenido del email
   * @param recipients      Destinatarios del email
   * @return Identificador del email creado
   */
  public Long createSolicitudHitoEmail(Long solicitudHitoId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("createSolicitudHitoEmail({}, {}, {}, {}) - start", solicitudHitoId, subject, content, recipients);

    Assert.notNull(solicitudHitoId, "SolicitudHito ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    Long id = this.createGenericEmailText(subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(
            SOLICITUD_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT,
            solicitudHitoId),
        HttpMethod.GET))
        .getId();
    log.debug("createSolicitudHitoEmail({}, {}, {}, {}) - end", solicitudHitoId, subject, content, recipients);
    return id;
  }

  /**
   * Actualiza un email en el modulo COM para el aviso de un hito de solicitud
   *
   * @param id              Identificador el email
   * @param solicitudHitoId Identificador del hito de solicitud
   * @param subject         Asunto del email
   * @param content         Contenido del email
   * @param recipients      Destinatarios del email
   */
  public void updateSolicitudHitoEmail(Long id, Long solicitudHitoId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("updateSolicitudHitoEmail({}, {}, {}, {}) - start", id, solicitudHitoId, subject, content,
        recipients);

    Assert.notNull(id, "ID is required");
    Assert.notNull(solicitudHitoId, "SolicitudHito ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    this.updateGenericEmailText(id, subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(SOLICITUD_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT, solicitudHitoId), HttpMethod.GET));
    log.debug("updateSolicitudHitoEmail({}, {}, {}, {}) - end", id,
        solicitudHitoId, subject, content,
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
    String relativeUrl = PATH_EMAILS;
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
   * Crea un email en el modulo COM para el aviso de Alta de solicitud de petición
   * de evaluación de ética
   *
   * @param data       información a incluir en el email
   * @param recipients lista de destinatarios
   * @return EmailOutput información del email creado
   * @throws JsonProcessingException en caso de que se produzca un error
   *                                 convirtiendo data a JSON
   */
  public EmailOutput createComunicadoSolicitudPeticionEvaluacionEti(CspComSolicitudPeticionEvaluacionData data,
      List<Recipient> recipients) throws JsonProcessingException {
    log.debug(
        "createComunicadoSolicitudPeticionEvaluacionEti(CspComSolicitudPeticionEvaluacionData data, List<Recipient> recipients) - start");

    ServiceType serviceType = ServiceType.COM;
    String relativeUrl = PATH_EMAILS;
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    EmailInput request = EmailInput.builder().template(
        TEMPLATE_CSP_COM_SOLICITUD_PETICION_EVALUACION).recipients(recipients)
        .build();
    request.setParams(Arrays.asList(
        new EmailParam(TEMPLATE_CSP_COM_SOLICITUD_PETICION_EVALUACION_PARAM_PETICION_EVALUACION_CODIGO,
            data.getCodigoPeticionEvaluacion()),
        new EmailParam(TEMPLATE_CSP_COM_SOLICITUD_PETICION_EVALUACION_PARAM_SOLICITUD_CODIGO,
            data.getCodigoSolicitud())));

    final EmailOutput response = super.<EmailInput, EmailOutput>callEndpoint(mergedURL, httpMethod, request,
        new ParameterizedTypeReference<EmailOutput>() {
        }).getBody();

    log.debug(
        "createComunicadoSolicitudPeticionEvaluacionEti(CspComSolicitudPeticionEvaluacionData data, List<Recipient> recipients) - end");
    return response;
  }

  /**
   * Crea un email en el modulo COM para el aviso de inicio del per&iacute;odo de
   * presentaci&oacute;n de justificaci&oacute;n de seguimiento científico UG
   * 
   * @param data       informaci&oacute;n a incluir en el email
   * @param recipients lista de destinatarios
   * @return EmailOutput informaci&oacute;n del email creado
   * @throws JsonProcessingException en caso de que se produzca un error
   *                                 convirtiendo data a JSON
   */
  public EmailOutput createComunicadoInicioPresentacionSeguimientoCientificoEmail(
      CspComInicioPresentacionSeguimientoCientificoData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients, TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO,
        TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_PARAM);

  }

  /**
   * Crea un email en el modulo COM para el aviso de inicio del per&iacute;odo de
   * presentaci&oacute;n de justificaci&oacute;n de seguimiento científico IP
   *
   * @param data       informaci&oacute;n a incluir en el email
   * @param recipients lista de destinatarios
   * @return EmailOutput informaci&oacute;n del email creado
   * @throws JsonProcessingException en caso de que se produzca un error
   *                                 convirtiendo data a JSON
   */
  public EmailOutput createComunicadoInicioPresentacionSeguimientoCientificoIPEmail(
      CspComPresentacionSeguimientoCientificoIpData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP,
        TEMPLATE_CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_PARAM);

  }

  public EmailOutput createComunicadoFinPresentacionSeguimientoCientificoIPEmail(
      CspComPresentacionSeguimientoCientificoIpData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP,
        TEMPLATE_CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_PARAM);
  }

  public EmailOutput createComunicadoVencimientoPeriodoPagoSocioEmail(CspComVencimientoPeriodoPagoSocioData data,
      List<Recipient> recipients) throws JsonProcessingException {
    return this.createComunicado(data, recipients, TEMPLATE_CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO,
        TEMPLATE_CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_PARAM);

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

  public EmailOutput createComunicadoSolicitudCambioEstadoSolicitada(
      CspComSolicitudCambioEstadoSolicitadaData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients, TEMPLATE_CSP_COM_SOL_CAMB_EST_SOLICITADA,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_SOLICITADA_PARAM);
  }

  public EmailOutput createComunicadoSolicitudCambioEstadoAlegaciones(
      CspComSolicitudCambioEstadoAlegacionesData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_ALEGACIONES,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_ALEGACIONES_PARAM);
  }

  public EmailOutput createComunicadoSolicitudCambioEstadoExclProv(
      CspComSolicitudCambioEstadoProvisionalData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_PROV,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_PROV_PARAM);
  }

  public EmailOutput createComunicadoSolicitudCambioEstadoConc(
      CspComSolicitudCambioEstadoDefinitivoData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC_PARAM);
  }

  public EmailOutput createComunicadoSolicitudCambioEstadoDen(
      CspComSolicitudCambioEstadoDefinitivoData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN_PARAM);
  }

  public EmailOutput createComunicadoInicioPresentacionPeriodoJustificacionSocioEmail(
      CspComPeriodoJustificacionSocioData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO,
        TEMPLATE_CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_PARAM);
  }

  public EmailOutput createComunicadoFinPresentacionPeriodoJustificacionSocioEmail(
      CspComPeriodoJustificacionSocioData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO,
        TEMPLATE_CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionValidarIpValidada(
      CspComCalendarioFacturacionValidarIPData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionValidarIpRechazada(
      CspComCalendarioFacturacionValidarIPData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_PARAM);
  }

  public EmailOutput createComunicadoSolicitudCambioEstadoExclDef(
      CspComSolicitudCambioEstadoDefinitivoData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_DEF,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_EXCL_DEF_PARAM);
  }

  public EmailOutput createComunicadoSolicitudCambioEstadoDenProv(
      CspComSolicitudCambioEstadoProvisionalData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN_PROV,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_DEN_PROV_PARAM);
  }

  public EmailOutput createComunicadoSolicitudCambioEstadoConcProv(
      CspComSolicitudCambioEstadoProvisionalData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC_PROV,
        TEMPLATE_CSP_COM_SOL_CAMB_EST_CONC_PROV_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionNotificarFacturaUnicaNoProrrogaNoRequisito(
      CspComCalendarioFacturacionNotificarData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionNotificarFacturaUnicaNoProrroga(
      CspComCalendarioFacturacionNotificarData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionNotificarFacturaFirstNoProrrogaNoLast(
      CspComCalendarioFacturacionNotificarData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsLastNoRequisitos(
      CspComCalendarioFacturacionNotificarData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsLast(
      CspComCalendarioFacturacionNotificarData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsNotLastNoRequisito(
      CspComCalendarioFacturacionNotificarData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_PARAM);
  }

  public EmailOutput createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsNotLast(
      CspComCalendarioFacturacionNotificarData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST,
        TEMPLATE_CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_PARAM);
  }

  public Long createProyectoHitoEmail(Long proyectoHitoId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("createProyectoHitoEmail({}, {}, {}, {}) - start", proyectoHitoId, subject, content, recipients);

    Assert.notNull(proyectoHitoId, "ProyectoHito ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    Long id = this.createGenericEmailText(subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(
            PROYECTO_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT,
            proyectoHitoId),
        HttpMethod.GET))
        .getId();
    log.debug("createProyectoHitoEmail({}, {}, {}, {}) - end", proyectoHitoId, subject, content, recipients);
    return id;
  }

  public EmailOutput createComunicadoModificacionAutorizacionParticipacionProyectoExterno(
      CspComModificacionEstadoParticipacionProyectoExternoData data, List<Recipient> recipients)
      throws JsonProcessingException {

    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO,
        TEMPLATE_CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_PARAM);
  }

  public EmailOutput createComunicadoCambioEstadoAutorizacionParticipacionProyectoExterno(
      CspComCambioEstadoParticipacionAutorizacionProyectoExternoData data, List<Recipient> recipients)
      throws JsonProcessingException {

    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO,
        TEMPLATE_CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_PARAM);
  }

  public EmailOutput createComunicadoAddModificarCertificadoAutorizacionParticipacionProyectoExterno(
      CspComAddModCertAutorizacionPartProyectoExtData data, List<Recipient> recipients)
      throws JsonProcessingException {

    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO,
        TEMPLATE_CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_PARAM);
  }

  public EmailOutput createComunicadoRecepcionNotificacionCVNProyectoExterno(
      CspComRecepcionNotificacionesCVNProyectoExtData data, List<Recipient> recipients)
      throws JsonProcessingException {

    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO,
        TEMPLATE_CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_PARAM);
  }

  public EmailOutput createComunicadoCambioEstadoSolicitadaSolTipoRrhh(
      CspComCambioEstadoSolicitadaSolTipoRrhhData data, List<Recipient> recipients)
      throws JsonProcessingException {

    return this.createComunicado(data, recipients,
        TEMPLATE_CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH,
        TEMPLATE_CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_PARAM);
  }

  public Long createConvocatoriaFaseEmail(Long convocatoriaFaseId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("createConvocatoriaFaseEmail({}, {}, {}, {}) - start", convocatoriaFaseId, subject, content, recipients);

    Assert.notNull(convocatoriaFaseId, "ConvocatoriaFase ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    Long id = this.createGenericEmailText(subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(CONVOCATORIA_FASE_DEFERRABLE_RECIPIENTS_URI_FORMAT, convocatoriaFaseId), HttpMethod.GET))
        .getId();
    log.debug("createConvocatoriaFaseEmail({}, {}, {}, {}) - end", convocatoriaFaseId, subject, content, recipients);
    return id;
  }

  public Long createProyectoFaseEmail(Long proyectoFaseId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("createConvocatoriaFaseEmail({}, {}, {}, {}) - start", proyectoFaseId, subject, content, recipients);

    Assert.notNull(proyectoFaseId, "ProyectoFase ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    Long id = this.createGenericEmailText(subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(PROYECTO_FASE_DEFERRABLE_RECIPIENTS_URI_FORMAT, proyectoFaseId), HttpMethod.GET))
        .getId();
    log.debug("createProyectoFaseEmail({}, {}, {}, {}) - end", proyectoFaseId, subject, content, recipients);
    return id;
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

}
