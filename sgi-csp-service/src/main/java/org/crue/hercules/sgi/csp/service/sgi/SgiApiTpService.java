package org.crue.hercules.sgi.csp.service.sgi;

import java.time.Instant;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskInput;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiTpService extends SgiApiBaseService {

  private static final String SEND_EMAIL_URL_FORMAT = "/emails/%s/send";
  private static final String SEND_EMAIL_DEFAULT_DESCRIPTION = "Task for send email ";

  public SgiApiTpService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Crea una tarea progamada en el modulo TP para ejecutarse en un momento dado
   * 
   * @param type        Servicio al que invocará la tarea programada
   * @param method      Método HTTP que utilizará la tarea programada en su
   *                    invocación al servicio
   * @param url         Url sobre la que se realizará la invocación
   * @param description Descripción de la tarea
   * @param instant     Momento en el que ejecutará la tarea dada
   * @return Tarea programada creada
   */
  public SgiApiInstantTaskOutput createInstantTask(ServiceType type, HttpMethod method, String url, String description,
      Instant instant) {
    log.debug("createInstantTask({}, {}, {}, {}, {}) - start", type, method, url, description);

    Assert.notNull(type, "ServiceType is required");
    Assert.notNull(method, "HttpMethod is required");
    Assert.notNull(url, "URL is required");
    Assert.notNull(description, "description is required");
    Assert.notNull(instant, "Instant is required");

    ServiceType serviceType = ServiceType.TP;
    String relativeUrl = "/sgiapitasks/instant";
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    SgiApiInstantTaskInput taskRequest = SgiApiInstantTaskInput.builder().serviceType(type).httpMethod(method)
        .relativeUrl(url).description(description).instant(instant).build();

    final SgiApiInstantTaskOutput response = super.<SgiApiInstantTaskInput, SgiApiInstantTaskOutput>callEndpointWithCurrentUserAuthorization(
        mergedURL,
        httpMethod, taskRequest, new ParameterizedTypeReference<SgiApiInstantTaskOutput>() {
        }).getBody();

    log.debug("createInstantTask({}, {}, {}, {}, {}) - end", type, method, url, description);
    return response;
  }

  /**
   * Actualiza una tarea progamada en el modulo TP para ejecutarse en un momento
   * dado
   * 
   * @param id          Identificador de la tarea programada
   * @param type        Servicio al que invocará la tarea programada
   * @param method      Método HTTP que utilizará la tarea programada en su
   *                    invocación al servicio
   * @param url         Url sobre la que se realizará la invocación
   * @param description Descripción de la tarea
   * @param instant     Momento en el que ejecutará la tarea dada
   * @return Tarea programada actualizada
   */
  public SgiApiInstantTaskOutput updateInstantTask(Long id, ServiceType type, HttpMethod method, String url,
      String description, Instant instant) {
    log.debug("updateInstantTask({}, {}, {}, {}, {}, {}) - start", id, type, method, url, description, instant);

    Assert.notNull(id, "ID is required");
    Assert.notNull(type, "ServiceType is required");
    Assert.notNull(method, "HttpMethod is required");
    Assert.notNull(url, "URL is required");
    Assert.notNull(description, "description is required");
    Assert.notNull(instant, "Instant is required");

    ServiceType serviceType = ServiceType.TP;
    String relativeUrl = "/sgiapitasks/instant/{id}";
    HttpMethod httpMethod = HttpMethod.PUT;
    String mergedURL = buildUri(serviceType, relativeUrl);

    SgiApiInstantTaskInput taskRequest = SgiApiInstantTaskInput.builder().serviceType(type).httpMethod(method)
        .relativeUrl(url).description(description).instant(instant).build();

    final SgiApiInstantTaskOutput response = super.<SgiApiInstantTaskInput, SgiApiInstantTaskOutput>callEndpointWithCurrentUserAuthorization(
        mergedURL,
        httpMethod, taskRequest, new ParameterizedTypeReference<SgiApiInstantTaskOutput>() {
        }, id).getBody();

    log.debug("updateInstantTask({}, {}, {}, {}, {}, {}) - end", id, type, method, url, description, instant);
    return response;
  }

  /**
   * Elimina una tarea programada en el modulo TP
   * 
   * @param id Identificador de la tarea programada
   */
  public void deleteTask(Long id) {
    log.debug("deleteTask({}) - start", id);

    Assert.notNull(id, "ID is required");

    ServiceType serviceType = ServiceType.TP;
    String relativeUrl = "/sgiapitasks/{id}";
    HttpMethod httpMethod = HttpMethod.DELETE;
    String mergedURL = buildUri(serviceType, relativeUrl);

    super.<Void>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod, new ParameterizedTypeReference<Void>() {
    }, id);
    log.debug("deleteTask({}) - end", id);
  }

  /**
   * Obtiene una tarea programada del modulo TP
   * 
   * @param id Identificador de la tarea
   * @return Tarea programada
   */
  public SgiApiInstantTaskOutput findInstantTaskById(Long id) {
    log.debug("findInstantTaskById({}) - start", id);
    ServiceType serviceType = ServiceType.TP;
    String relativeUrl = "/sgiapitasks/{id}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final SgiApiInstantTaskOutput response = super.<SgiApiInstantTaskOutput>callEndpointWithCurrentUserAuthorization(
        mergedURL,
        httpMethod, new ParameterizedTypeReference<SgiApiInstantTaskOutput>() {
        }, id).getBody();

    log.debug("findInstantTaskById({}) - end", id);
    return response;
  }

  /**
   * Crea una tarea programada en el modulo TP para el envío de un email en un
   * momento dado
   * 
   * @param emailId Identificador del email procedente del modulo COM
   * @param instant Momento en el que realizar el envío
   * @return Identificador de la tarea programada creada
   */
  public Long createSendEmailTask(Long emailId, Instant instant) {
    log.debug("createSendEmailTask({}, {}) - start", emailId, instant);

    Assert.notNull(emailId, "Email ID is required");
    Assert.notNull(instant, "Instant is required");

    Long id = this.createInstantTask(ServiceType.COM, HttpMethod.GET, String.format(SEND_EMAIL_URL_FORMAT, emailId),
        SEND_EMAIL_DEFAULT_DESCRIPTION + emailId, instant).getId();
    log.debug("createSendEmailTask({}, {}) - end", emailId, instant);
    return id;
  }

  /**
   * Actualiza una tarea programada en el modulo TP de envío de email
   * 
   * @param id      Identificador de la tarea programada
   * @param emailId Identificador del email procedente del modulo COM
   * @param instant Momento en el que realizar el envío
   */
  public void updateSendEmailTask(Long id, Long emailId, Instant instant) {
    log.debug("updateSendEmailTask({}, {}, {}) - start", id, emailId, instant);

    Assert.notNull(id, "ID is required");
    Assert.notNull(emailId, "Email ID is required");
    Assert.notNull(instant, "Instant is required");

    this.updateInstantTask(id, ServiceType.COM, HttpMethod.GET, String.format(SEND_EMAIL_URL_FORMAT,
        emailId),
        SEND_EMAIL_DEFAULT_DESCRIPTION + emailId, instant);
    log.debug("updateSendEmailTask({}, {}, {}) - end", id, emailId, instant);
  }
}
