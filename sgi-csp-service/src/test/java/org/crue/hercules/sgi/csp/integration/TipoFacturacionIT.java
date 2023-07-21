package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFacturacion;
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
 * Test de integracion de TipoFacturacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class TipoFacturacionIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/tiposfacturacion";

  private HttpEntity<TipoFacturacion> buildRequest(HttpHeaders headers, TipoFacturacion entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<TipoFacturacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(scripts = {
// @formatter:off
  "classpath:scripts/tipo_facturacion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_SortingAndFiltering_ReturnsTipoFacturacionSubList() throws Exception {

    // sorted by nombre asc and filter
    String[] roles = { "CSP-TFAC-V", "CSP-TFAC-C", "CSP-TFAC-E", "CSP-TFAC-B", "CSP-TFAC-V" };
    String sort = "nombre,asc";
    // String filter = "tipoComunicado=ke=00";
    String filter = "";

    // when: find TipoFacturacion
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<TipoFacturacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<TipoFacturacion>>() {
        });

    // given: TipoFacturacion data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoFacturacion> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);

    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-facturacion-" + String.format("%03d", 1));
    Assertions.assertThat(responseData.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-facturacion-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-facturacion-" + String.format("%03d", 3));
  }

  @Test
  void findAll_SortingAndFiltering_ReturnsStatusCode204() throws Exception {

    // sorted by nombre asc and filter
    String[] roles = { "CSP-TFAC-V", "CSP-TFAC-C", "CSP-TFAC-E", "CSP-TFAC-B", "CSP-TFAC-V" };
    String sort = "nombre,asc";
    // String filter = "tipoComunicado=ke=00";
    String filter = "";

    // when: find TipoFacturacion
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<TipoFacturacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<TipoFacturacion>>() {
        });

    // given: TipoFacturacion data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}
