package org.crue.hercules.sgi.prc.service.sgi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.rel.RelacionOutput;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiRelService extends SgiApiBaseService {

  public static final String CLIENT_REGISTRATION_ID = "rel-service";

  public SgiApiRelService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public List<RelacionOutput> findAllRelaciones(String query) {
    List<RelacionOutput> result = new ArrayList<>();

    try {
      ServiceType serviceType = ServiceType.REL;
      String sort = "id,asc";
      String relativeUrl = "/relaciones?s=" + sort + "&q=" + query;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<RelacionOutput>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<RelacionOutput>>() {
          }).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

}
