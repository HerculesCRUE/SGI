package org.crue.hercules.sgi.eti.service.sgi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.com.EmailInput;
import org.crue.hercules.sgi.eti.dto.com.EmailInput.Deferrable;
import org.crue.hercules.sgi.eti.dto.com.EmailOutput;
import org.crue.hercules.sgi.eti.dto.com.EmailParam;
import org.crue.hercules.sgi.eti.dto.com.EtiComActaFinalizarActaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComAsignacionEvaluacionData;
import org.crue.hercules.sgi.eti.dto.com.EtiComAvisoRetrospectivaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComDictamenEvaluacionRevMinData;
import org.crue.hercules.sgi.eti.dto.com.EtiComDictamenEvaluacionSeguimientoRevMinData;
import org.crue.hercules.sgi.eti.dto.com.EtiComEvaluacionModificadaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComInformeSegAnualPendienteData;
import org.crue.hercules.sgi.eti.dto.com.EtiComInformeSegFinalPendienteData;
import org.crue.hercules.sgi.eti.dto.com.EtiComMemoriaIndicarSubsanacionData;
import org.crue.hercules.sgi.eti.dto.com.EtiComMemoriaRevisionMinArchivadaData;
import org.crue.hercules.sgi.eti.dto.com.EtiComRevisionActaData;
import org.crue.hercules.sgi.eti.dto.com.Recipient;
import org.crue.hercules.sgi.eti.dto.com.Status;
import org.crue.hercules.sgi.eti.enums.ServiceType;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.context.MessageSource;
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
  private static final String MSG_KEY_PATH_SEPARATOR = ".";

  private static final String MSG_FIELD_ASUNTO = "email.asunto";
  private static final String MSG_FIELD_CONTENIDO = "email.contenido";
  private static final String MSG_FIELD_DESTINATARIOS = "email.destinatarios";

  private static final String PATH_SEPARATOR = "/";
  private static final String DATA = "_DATA";
  private static final String PATH_EMAILS = PATH_SEPARATOR + "emails";

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
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_LUGAR = "ETI_CONVOCATORIA_REUNION_LUGAR";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_VIDEOCONFERENCIA = "ETI_CONVOCATORIA_REUNION_VIDEOCONFERENCIA";
  private static final String TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_TIPO_CONVOCATORIA = "ETI_CONVOCATORIA_REUNION_TIPO_CONVOCATORIA";

  private static final String TEMPLATE_ETI_COM_ACTA_SIN_REV_MINIMA = "ETI_COM_ACTA_SIN_REV_MINIMA";
  private static final String TEMPLATE_ETI_COM_ACTA_SIN_REV_MINIMA_PARAM = TEMPLATE_ETI_COM_ACTA_SIN_REV_MINIMA
      + DATA;

  private static final String TEMPLATE_ETI_COM_DICT_EVA_REV_MINIMA = "ETI_COM_DICT_EVA_REV_MINIMA";
  private static final String TEMPLATE_ETI_COM_DICT_EVA_REV_MINIMA_PARAM = TEMPLATE_ETI_COM_DICT_EVA_REV_MINIMA
      + DATA;

  private static final String TEMPLATE_ETI_COM_INF_RETRO_PENDIENTE = "ETI_COM_INF_RETRO_PENDIENTE";
  private static final String TEMPLATE_ETI_COM_INF_RETRO_PENDIENTE_PARAM = TEMPLATE_ETI_COM_INF_RETRO_PENDIENTE
      + DATA;

  private static final String TEMPLATE_ETI_COM_DICT_EVA_SEG_REV_MINIMA = "ETI_COM_DICT_EVA_SEG_REV_MINIMA";
  private static final String TEMPLATE_ETI_COM_DICT_EVA_SEG_REV_MINIMA_PARAM = TEMPLATE_ETI_COM_DICT_EVA_SEG_REV_MINIMA
      + DATA;

  private static final String TEMPLATE_ETI_COM_EVA_MODIFICADA = "ETI_COM_EVA_MODIFICADA";
  private static final String TEMPLATE_ETI_COM_EVA_MODIFICADA_PARAM = TEMPLATE_ETI_COM_EVA_MODIFICADA
      + DATA;

  private static final String TEMPLATE_ETI_COM_INF_SEG_ANU = "ETI_COM_INF_SEG_ANU";
  private static final String TEMPLATE_ETI_COM_INF_SEG_ANU_PARAM = TEMPLATE_ETI_COM_INF_SEG_ANU
      + DATA;

  private static final String TEMPLATE_ETI_COM_DICT_MEM_REV_MINIMA_ARCH = "ETI_COM_DICT_MEM_REV_MINIMA_ARCH";
  private static final String TEMPLATE_ETI_COM_DICT_MEM_REV_MINIMA_ARCH_PARAM = TEMPLATE_ETI_COM_DICT_MEM_REV_MINIMA_ARCH
      + DATA;

  private static final String TEMPLATE_ETI_COM_INF_SEG_FIN = "ETI_COM_INF_SEG_FIN";
  private static final String TEMPLATE_ETI_COM_INF_SEG_FIN_PARAM = TEMPLATE_ETI_COM_INF_SEG_FIN
      + DATA;

  private static final String TEMPLATE_ETI_COM_MEM_ARCHIVADA_AUT = "ETI_COM_MEM_ARCHIVADA_AUT";
  private static final String TEMPLATE_ETI_COM_MEM_ARCHIVADA_AUT_PARAM = TEMPLATE_ETI_COM_MEM_ARCHIVADA_AUT
      + DATA;

  private static final String TEMPLATE_ETI_COM_MEN_INDICAR_SUBSANACION = "ETI_COM_MEM_INDICAR_SUBSANACION";
  private static final String TEMPLATE_ETI_COM_MEN_INDICAR_SUBSANACION_PARAM = TEMPLATE_ETI_COM_MEN_INDICAR_SUBSANACION
      + DATA;

  private static final String TEMPLATE_ETI_COM_ASIGNACION_EVALUACION = "ETI_COM_ASIGNACION_EVALUACION";
  private static final String TEMPLATE_ETI_COM_ASIGNACION_EVALUACION_PARAM = TEMPLATE_ETI_COM_ASIGNACION_EVALUACION
      + DATA;

  private static final String TEMPLATE_ETI_COM_REVISION_ACTA = "ETI_COM_REVISION_ACTA";
  private static final String TEMPLATE_ETI_COM_REVISION_ACTA_PARAM = TEMPLATE_ETI_COM_REVISION_ACTA
      + DATA;

  private final ObjectMapper mapper;
  private final MessageSource messageSource;
  private final I18nConfig i18nConfig;

  public SgiApiComService(RestApiProperties restApiProperties, RestTemplate restTemplate,
      ObjectMapper mapper, MessageSource messageSource,
      I18nConfig i18nConfig) {
    super(restApiProperties, restTemplate);
    this.mapper = mapper;
    this.messageSource = messageSource;
    this.i18nConfig = i18nConfig;
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

    this.validateComunicados(subject, content, recipients);

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

    AssertHelper.idNotNull(id, EmailOutput.class);
    this.validateComunicados(subject, content, recipients);

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

    AssertHelper.idNotNull(id, EmailOutput.class);

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
    String relativeUrl = PATH_EMAILS;
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    String hora = String.format("%02d", convocatoriaReunion.getHoraInicio());
    String minuto = String.format("%02d", convocatoriaReunion.getMinutoInicio());
    String horaSegunda = String.format("%02d", convocatoriaReunion.getHoraInicioSegunda());
    String minutoSegunda = String.format("%02d", convocatoriaReunion.getMinutoInicioSegunda());

    String messageKey = convocatoriaReunion.getTipoConvocatoriaReunion().getClass().getName()
        + MSG_KEY_PATH_SEPARATOR + convocatoriaReunion.getTipoConvocatoriaReunion().getTipo().name();

    List<I18nFieldValue> i18nTipoConvocatoria = new ArrayList<>();
    for (Language language : i18nConfig.getEnabledLanguages()) {
      i18nTipoConvocatoria.add(new I18nFieldValueDto(language,
          messageSource.getMessage(messageKey, null, Locale.forLanguageTag(language.getCode()))));
    }

    EmailInput request = EmailInput.builder().template(
        TEMPLATE_ETI_COM_CONVOCATORIA_REUNION).recipients(recipients)
        .build();
    request.setParams(Arrays.asList(
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_NOMBRE_INVESTIGACION,
            convocatoriaReunion.getComite().getNombre()),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_COMITE,
            convocatoriaReunion.getComite().getCodigo()),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_FECHA_EVALUACION,
            convocatoriaReunion.getFechaEvaluacion().toString()),
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
            convocatoriaReunion.getOrdenDia()),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_LUGAR,
            convocatoriaReunion.getLugar().isEmpty() ? new HashSet<>() : convocatoriaReunion.getLugar()),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_VIDEOCONFERENCIA,
            convocatoriaReunion.getVideoconferencia().toString()),
        new EmailParam(
            TEMPLATE_ETI_COM_CONVOCATORIA_REUNION_PARAM_TIPO_CONVOCATORIA,
            i18nTipoConvocatoria)));

    final EmailOutput response = super.<EmailInput, EmailOutput>callEndpoint(mergedURL,
        httpMethod, request,
        new ParameterizedTypeReference<EmailOutput>() {
        }).getBody();

    log.debug(
        "createComunicadoConvocatoriaReunionEti(ConvocatoriaReunion convocatoriaReunion, List<Recipient> recipients) - end");
    return response;
  }

  /**
   * Crea el comunicado {@code ETI_COM_ACTA_SIN_REV_MINIMA}
   * 
   * @param data       Información para rellenar la plantilla del comunicado
   * @param recipients remitentes del comunicado
   * @return el email creado
   * @throws JsonProcessingException
   */
  public EmailOutput createComunicadoActaFinalizada(
      EtiComActaFinalizarActaData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_ACTA_SIN_REV_MINIMA,
        TEMPLATE_ETI_COM_ACTA_SIN_REV_MINIMA_PARAM);
  }

  /**
   * Crea el comunicado {@code ETI_COM_DICT_EVA_REV_MINIMA}
   * 
   * @param data       Información para rellenar la plantilla del comunicado
   * @param recipients remitentes del comunicado
   * @return el email creado
   * @throws JsonProcessingException
   */
  public EmailOutput createComunicadoEvaluacionMemoriaRevMin(
      EtiComDictamenEvaluacionRevMinData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_DICT_EVA_REV_MINIMA,
        TEMPLATE_ETI_COM_DICT_EVA_REV_MINIMA_PARAM);
  }

  /**
   * Crea el comunicado {@code ETI_COM_INF_RETRO_PENDIENTE}
   * 
   * @param data       Información para rellenar la plantilla del comunicado
   * @param recipients remitentes del comunicado
   * @return el email creado
   * @throws JsonProcessingException
   */
  public EmailOutput createComunicadoAvisoRetrospectiva(EtiComAvisoRetrospectivaData data,
      List<Recipient> recipients) throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_INF_RETRO_PENDIENTE, TEMPLATE_ETI_COM_INF_RETRO_PENDIENTE_PARAM);
  }

  public EmailOutput createComunicadoEvaluacionSeguimientoMemoriaRevMin(
      EtiComDictamenEvaluacionSeguimientoRevMinData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_DICT_EVA_SEG_REV_MINIMA,
        TEMPLATE_ETI_COM_DICT_EVA_SEG_REV_MINIMA_PARAM);
  }

  public EmailOutput createComunicadoCambiosEvaluacion(
      EtiComEvaluacionModificadaData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_EVA_MODIFICADA,
        TEMPLATE_ETI_COM_EVA_MODIFICADA_PARAM);
  }

  /**
   * Crea el comunicado {@code ETI_COM_INF_SEG_ANU}
   * 
   * @param data       Información para rellenar la plantilla del comunicado
   * @param recipients remitentes del comunicado
   * @return el email creado
   * @throws JsonProcessingException
   */
  public EmailOutput createComunicadoInformeSeguimientoAnualPendiente(
      EtiComInformeSegAnualPendienteData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_INF_SEG_ANU,
        TEMPLATE_ETI_COM_INF_SEG_ANU_PARAM);
  }

  /**
   * Crea el comunicado {@code ETI_COM_INF_SEG_FIN}
   * 
   * @param data       Información para rellenar la plantilla del comunicado
   * @param recipients remitentes del comunicado
   * @return el email creado
   * @throws JsonProcessingException
   */
  public EmailOutput createComunicadoInformeSeguimientoFinalPendiente(
      EtiComInformeSegFinalPendienteData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_INF_SEG_FIN,
        TEMPLATE_ETI_COM_INF_SEG_FIN_PARAM);
  }

  public EmailOutput createComunicadoMemoriaRevisionMinArchivada(
      EtiComMemoriaRevisionMinArchivadaData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_DICT_MEM_REV_MINIMA_ARCH,
        TEMPLATE_ETI_COM_DICT_MEM_REV_MINIMA_ARCH_PARAM);
  }

  public EmailOutput createComunicadoMemoriaArchivadaPorInactividad(
      EtiComInformeSegFinalPendienteData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_MEM_ARCHIVADA_AUT,
        TEMPLATE_ETI_COM_MEM_ARCHIVADA_AUT_PARAM);
  }

  public EmailOutput createComunicadoMemoriaIndicarSubsanacion(
      EtiComMemoriaIndicarSubsanacionData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_MEN_INDICAR_SUBSANACION,
        TEMPLATE_ETI_COM_MEN_INDICAR_SUBSANACION_PARAM);
  }

  public EmailOutput createComunicadoAsignacionEvaluacion(
      EtiComAsignacionEvaluacionData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_ASIGNACION_EVALUACION,
        TEMPLATE_ETI_COM_ASIGNACION_EVALUACION_PARAM);
  }

  /**
   * Crea el comunicado {@code ETI_COM_REVISION_ACTA}
   * 
   * @param data       Información para rellenar la plantilla del comunicado
   * @param recipients remitentes del comunicado
   * @return el email creado
   * @throws JsonProcessingException
   */
  public EmailOutput createComunicadoRevisionActa(
      EtiComRevisionActaData data, List<Recipient> recipients)
      throws JsonProcessingException {
    return this.createComunicado(data, recipients,
        TEMPLATE_ETI_COM_REVISION_ACTA,
        TEMPLATE_ETI_COM_REVISION_ACTA_PARAM);
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

  private void validateComunicados(String subject, String content, List<Recipient> recipients) {
    AssertHelper.fieldNotNull(subject, EmailOutput.class, MSG_FIELD_ASUNTO);
    AssertHelper.fieldNotNull(content, EmailOutput.class, MSG_FIELD_CONTENIDO);
    AssertHelper.fieldNotEmpty(recipients, EmailOutput.class, MSG_FIELD_DESTINATARIOS);
    AssertHelper.fieldNoNullElements(recipients, EmailOutput.class, MSG_FIELD_DESTINATARIOS);
  }

}
