package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.controller.TipoCaducidadController;
import org.crue.hercules.sgi.pii.model.TipoCaducidad;
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

/**
 * Test de integracion de TipoCaducidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoCaducidadIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = TipoCaducidadController.MAPPING;

  private HttpEntity<TipoCaducidad> buildRequest(HttpHeaders headers,
      TipoCaducidad entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<TipoCaducidad> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_caducidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoCaducidadSubList() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false)
        .toUri();

    final ResponseEntity<List<TipoCaducidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoCaducidad>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoCaducidad> tipoCaducidad = response.getBody();
    Assertions.assertThat(tipoCaducidad.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(tipoCaducidad.get(0).getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-003");
    Assertions.assertThat(tipoCaducidad.get(1).getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-002");
    Assertions.assertThat(tipoCaducidad.get(2).getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-001");

  }

}
