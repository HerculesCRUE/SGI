package org.crue.hercules.sgi.pii.integration;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/config";
  private static final String PATH_TIME_ZONE = "/time-zone";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.TEXT_PLAIN);
    headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  void timeZone() throws Exception {

    String[] roles = { "AUTH" };

    final ResponseEntity<String> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_TIME_ZONE, HttpMethod.GET,
        buildRequest(null, null, roles), String.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo("Europe/Madrid");
  }
}