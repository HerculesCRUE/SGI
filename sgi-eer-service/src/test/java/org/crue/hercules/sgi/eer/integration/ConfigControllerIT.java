package org.crue.hercules.sgi.eer.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/config";
  private static final String PATH_TIME_ZONE = "/time-zone";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, MediaType mediaType, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(mediaType == null ? MediaType.APPLICATION_JSON : mediaType));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  void timeZone_ReturnsEuropeMadridTimeZone() throws Exception {

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TIME_ZONE).build(false).toUri();

    String[] roles = { "CSP-COM-E" };

    final ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, MediaType.TEXT_PLAIN, roles), String.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo("Europe/Madrid");
  }
}