package org.crue.hercules.sgi.prc.service.sgi;

import java.time.Instant;

import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.tp.SgiApiInstantTaskInput;
import org.crue.hercules.sgi.prc.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiTpService extends SgiApiBaseService {

  private static final String CALL_BAREMACION_URL_FORMAT = "/baremacion/%s";
  private static final String CALL_BAREMACION_DEFAULT_DESCRIPTION = "Task for call baremacion";

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
    log.debug("createInstantTask({}, {}, {}, {}, {}) - start", type, method, url, description, instant);

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

    final SgiApiInstantTaskOutput response = super.<SgiApiInstantTaskInput, SgiApiInstantTaskOutput>callEndpoint(
        mergedURL, httpMethod, taskRequest, new ParameterizedTypeReference<SgiApiInstantTaskOutput>() {
        }).getBody();

    log.debug("createInstantTask({}, {}, {}, {}, {}) - end", type, method, url, description, instant);
    return response;
  }

  /**
   * Crea una tarea programada en el modulo TP para la baremación de una
   * convocatoria en un momento dado
   * 
   * @param convocatoriaBaremacionId Identificador de
   *                                 {@link ConvocatoriaBaremacion}
   * @param instant                  Momento en el que realizar el envío
   * @return Identificador de la tarea programada creada
   */
  public Long createCallBaremacionTaskId(Long convocatoriaBaremacionId, Instant instant) {
    log.debug("createCallBaremacionTaskId({}, {}) - start", convocatoriaBaremacionId, instant);

    Assert.notNull(convocatoriaBaremacionId, "ConvocatoriaBaremacion ID is required");
    Assert.notNull(instant, "Instant is required");

    Long id = this.createInstantTask(ServiceType.PRC, HttpMethod.POST,
        String.format(CALL_BAREMACION_URL_FORMAT, convocatoriaBaremacionId),
        CALL_BAREMACION_DEFAULT_DESCRIPTION + convocatoriaBaremacionId, instant).getId();
    log.debug("createCallBaremacionTaskId({}, {}) - end", convocatoriaBaremacionId, instant);
    return id;
  }

}
