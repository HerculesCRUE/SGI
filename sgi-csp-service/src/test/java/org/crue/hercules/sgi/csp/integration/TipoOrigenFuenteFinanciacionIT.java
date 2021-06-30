package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
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
 * Test de integracion de TipoOrigenFuenteFinanciacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoOrigenFuenteFinanciacionIT extends BaseIT {

  private static final String TIPO_ORIGEN_FUENTE_FINANCIACION_CONTROLLER_BASE_PATH = "/tipoorigenfuentefinanciaciones";

  private HttpEntity<TipoOrigenFuenteFinanciacion> buildRequest(HttpHeaders headers,
      TipoOrigenFuenteFinanciacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-THIT-B", "CSP-THIT-C", "CSP-THIT-E", "CSP-THIT-V")));

    HttpEntity<TipoOrigenFuenteFinanciacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoOrigenFuenteFinanciacionSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "nombre=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ORIGEN_FUENTE_FINANCIACION_CONTROLLER_BASE_PATH)
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoOrigenFuenteFinanciacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoOrigenFuenteFinanciacion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoOrigenFuenteFinanciacion> tipoAmbitoGeograficos = response.getBody();
    Assertions.assertThat(tipoAmbitoGeograficos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tipoAmbitoGeograficos.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tipoAmbitoGeograficos.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tipoAmbitoGeograficos.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }
}
