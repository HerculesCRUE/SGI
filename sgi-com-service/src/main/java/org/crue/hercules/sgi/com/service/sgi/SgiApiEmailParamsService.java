package org.crue.hercules.sgi.com.service.sgi;

import java.util.List;

import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.dto.EmailParam;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiEmailParamsService extends SgiApiBaseService {

  protected SgiApiEmailParamsService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public List<EmailParam> call(
      ServiceType serviceType, String relativeUrl,
      HttpMethod httpMethod) {
    log.debug("call(ServiceType serviceType, String relativeUrl, HttpMethod httpMethod) - start");
    String mergedURL = buildUri(serviceType, relativeUrl);

    final List<EmailParam> response = super.<List<EmailParam>>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<List<EmailParam>>() {
        }).getBody();

    log.debug("call(ServiceType serviceType, String relativeUrl, HttpMethod httpMethod) - end");
    return response;
  }

}
