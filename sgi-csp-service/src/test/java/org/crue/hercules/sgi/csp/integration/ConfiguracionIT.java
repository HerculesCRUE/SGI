package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Configuracion;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de Configuracion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfiguracionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/configuraciones";

  private HttpEntity<Configuracion> buildRequest(HttpHeaders headers, Configuracion entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Configuracion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(scripts = {
// @formatter:off
  "classpath:scripts/configuracion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void find_ReturnsConfiguracion() throws Exception {

    String roles = "CSP-PRO-E";

    // when: find Configuracion
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<Configuracion> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), Configuracion.class);

    // given: Configuracion
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Configuracion responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getFormatoPartidaPresupuestaria())
        .as("getFormatoPartidaPresupuestaria()")
        .isEqualTo("formato-partida-presupuestaria");
    Assertions.assertThat(responseData.getPlantillaFormatoPartidaPresupuestaria())
        .as("getPlantillaFormatoPartidaPresupuestaria()")
        .isEqualTo("xx.xxxx.xxxx.xxxxx");

  }

  @Sql(scripts = {
// @formatter:off
  "classpath:scripts/configuracion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConfiguracion() throws Exception {

    String roles = "CSP-CNF-E";

    // given: existing Configuracion to be updated
    Configuracion configuracion = new Configuracion();
    configuracion.setId(1L);
    configuracion.setFormatoPartidaPresupuestaria("formato-partida-presupuestaria-modificado");
    configuracion.setPlantillaFormatoPartidaPresupuestaria("xx.xxxx.xxxx.xxxxx");

    // when: update Configuracion
    final ResponseEntity<Configuracion> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, configuracion, roles), Configuracion.class, configuracion.getId());

    // then: Configuracion is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Configuracion configuracionActualizada = response.getBody();
    Assertions.assertThat(configuracionActualizada.getId()).as("getId()").isEqualTo(configuracion.getId());
    Assertions.assertThat(configuracionActualizada.getFormatoPartidaPresupuestaria())
        .as("getFormatoPartidaPresupuestaria()")
        .isEqualTo(configuracion.getFormatoPartidaPresupuestaria());
    Assertions.assertThat(configuracionActualizada.getPlantillaFormatoPartidaPresupuestaria())
        .as("getPlantillaFormatoPartidaPresupuestaria()").isEqualTo(
            configuracion.getPlantillaFormatoPartidaPresupuestaria());

  }

}
