package org.crue.hercules.sgi.prc.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.AliasEnumeradoController;
import org.crue.hercules.sgi.prc.dto.AliasEnumeradoOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de AliasEnumerado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AliasEnumeradoIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = AliasEnumeradoController.MAPPING;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/alias_enumerado.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsAliasEnumeradoOutputSubList() throws Exception {
    String[] roles = { "PRC-VAL-V" };
    // first page, 3 elements per page sorted
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "id<=3";

    // when: find AliasEnumerado
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<AliasEnumeradoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<AliasEnumeradoOutput>>() {
        });

    // given: AliasEnumerado data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<AliasEnumeradoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3L);
  }
}
