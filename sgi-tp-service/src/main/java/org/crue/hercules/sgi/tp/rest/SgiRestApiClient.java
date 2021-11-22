package org.crue.hercules.sgi.tp.rest;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiRestApiClient {
  private final RestTemplate restTemplate;

  public SgiRestApiClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String callEndpoint(String endPoint) {
    log.info("Calling SGI API endpoint: {}", endPoint);
    HttpEntity<?> request = new HttpEntityBuilder<>().withClientAuthorization("tp-service").build();

    ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.PATCH, request, String.class);

    String result = response.getBody();
    log.info("Endpoint response: {}", result);
    return result;
  }
}
