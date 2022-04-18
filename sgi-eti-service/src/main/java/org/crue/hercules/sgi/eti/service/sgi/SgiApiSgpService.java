package org.crue.hercules.sgi.eti.service.sgi;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.eti.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiSgpService extends SgiApiBaseService {

  public SgiApiSgpService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Obtiene los datos de una persona, de SGP, a través de su identificador
   * 
   * @param id Identificador de persona
   * @return {@link PersonaOutput}
   */
  public PersonaOutput findById(String id) {
    log.debug("findById({}) - start", id);

    Assert.notNull(id, "ID is required");

    ServiceType serviceType = ServiceType.SGP;
    String relativeUrl = "/personas/{id}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final PersonaOutput response = super.<PersonaOutput>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<PersonaOutput>() {
        }, id).getBody();

    log.debug("findById({}) - end", id);
    return response;
  }

  /**
   * Obtienes los datos de varias personas, de SGP, a través de sus identificadoes
   * 
   * @param ids Listado de identificadores de persona
   * @return Listado de {@link PersonaOutput}
   */
  public List<PersonaOutput> findAllByIdIn(List<String> ids) {
    log.debug("findAllByIdIn({}) - start", ids);

    Assert.notEmpty(ids, "At least one ID is required");
    Assert.noNullElements(ids, "The IDs list must not contain null elements");

    String in = ids.stream().map(id -> StringUtils.wrap(id, "\"")).collect(Collectors.joining(","));

    ServiceType serviceType = ServiceType.SGP;
    String relativeUrl = "/personas/?q=id=in=({in})";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final List<PersonaOutput> response = super.<List<PersonaOutput>>callEndpoint(mergedURL,
        httpMethod,
        new ParameterizedTypeReference<List<PersonaOutput>>() {
        }, in).getBody();

    log.debug("findAllByIdIn({}) - end", ids);
    return response;
  }

}
