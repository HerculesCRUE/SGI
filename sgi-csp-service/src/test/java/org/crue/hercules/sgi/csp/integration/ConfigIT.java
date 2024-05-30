package org.crue.hercules.sgi.csp.integration;

import java.net.URI;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ConfigController;
import org.crue.hercules.sgi.csp.dto.ConfigParamOutput;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = ConfigController.MAPPING;
  private static final String PATH_TIME_ZONE = "/time-zone";
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_NAME = "/{name}";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Test
  void timeZone_ReturnsString() throws Exception {
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TIME_ZONE).build(false).toUri();

    String[] roles = { "CSP-PRO-E" };

    final ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), String.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/configuracion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void get_ShouldReturnConfigOutput() throws Exception {
    String[] roles = { "ADM-CNF-E" };
    String configName = "validacionClasificacionGastos";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_NAME).buildAndExpand(configName).toUri();

    ResponseEntity<ConfigParamOutput> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(null, null, roles), ConfigParamOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getValue()).isEqualTo("VALIDACION");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/configuracion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ShouldReturnModifiedConfigOutput() throws Exception {
    String[] roles = { "ADM-CNF-E" };
    String configName = "validacionClasificacionGastos";
    String newValue = "CLASIFICACION";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_NAME).buildAndExpand(configName).toUri();

    ResponseEntity<ConfigParamOutput> response = this.restTemplate.exchange(uri, HttpMethod.PATCH,
        this.buildRequest(null, newValue, roles), ConfigParamOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    ConfigParamOutput output = response.getBody();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(output.getName()).isEqualTo(configName);
    Assertions.assertThat(output.getValue()).isEqualTo(newValue);
  }

}