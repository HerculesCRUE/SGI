package org.crue.hercules.sgi.com.service.sgi;

import java.util.List;

import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.dto.Recipient;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiInternetAddressesService extends SgiApiBaseService {

  protected SgiApiInternetAddressesService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public List<Recipient> call(
      ServiceType serviceType, String relativeUrl,
      HttpMethod httpMethod) {
    log.debug("call(ServiceType serviceType, String relativeUrl, HttpMethod httpMethod) - start");
    String mergedURL = buildUri(serviceType, relativeUrl);

    final List<Recipient> response = super.<List<Recipient>>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<List<Recipient>>() {
        }).getBody();

    log.debug("call(ServiceType serviceType, String relativeUrl, HttpMethod httpMethod) - end");
    return response;
  }

}
