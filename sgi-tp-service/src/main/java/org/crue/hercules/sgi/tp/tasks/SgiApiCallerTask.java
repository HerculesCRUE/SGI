package org.crue.hercules.sgi.tp.tasks;

import org.crue.hercules.sgi.tp.config.RestApiProperties;
import org.crue.hercules.sgi.tp.enums.ServiceType;
import org.crue.hercules.sgi.tp.service.sgi.SgiApiBaseService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiCallerTask extends SgiApiBaseService {
  public SgiApiCallerTask(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public String call(String serviceType, String relativeUrl, String httpMethod) {
    log.debug("call(String serviceType, String relativeUrl, String httpMethod) - start");

    ServiceType type = ServiceType.valueOf(serviceType);
    HttpMethod method = HttpMethod.resolve(httpMethod);
    if (method == null) {
      method = HttpMethod.PATCH;
    }
    String mergedURL = buildUri(type, relativeUrl);

    final String result = super.<String>callEndpoint(mergedURL, method, new ParameterizedTypeReference<String>() {
    }).getBody();

    log.debug("call(String serviceType, String relativeUrl, String httpMethod) - end");
    return result;
  }
}
