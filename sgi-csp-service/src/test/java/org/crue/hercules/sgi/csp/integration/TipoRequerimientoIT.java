package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.TipoRequerimientoController;
import org.crue.hercules.sgi.csp.dto.TipoRequerimientoOutput;
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
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoRequerimientoIT extends BaseIT {
  private static final String REQUEST_MAPPING = TipoRequerimientoController.REQUEST_MAPPING;

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo_requerimiento.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findActivos_WithPagingSortingAndFiltering_ReturnsTipoRequerimientoSubList()
      throws Exception {
    String roles = "CSP-SJUS-E";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "nombre=ik=documental";

    URI uri = UriComponentsBuilder.fromUriString(REQUEST_MAPPING)
        .queryParam("s", sort).queryParam("q", filter).build().toUri();

    final ResponseEntity<List<TipoRequerimientoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoRequerimientoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<TipoRequerimientoOutput> grupoLineaClasificacion = response.getBody();
    Assertions.assertThat(grupoLineaClasificacion).hasSize(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");
    Assertions.assertThat(grupoLineaClasificacion.get(0).getNombre()).as("get(0).getNombre()")
        .isEqualTo("Resolución requerimiento documental");
    Assertions.assertThat(grupoLineaClasificacion.get(1).getNombre()).as("get(1).getNombre()")
        .isEqualTo("Requerimiento documental");
  }
}
