package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoController;
import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Grupo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GrupoIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = GrupoController.REQUEST_MAPPING;
  private static final String PATH_BAREMABLES_ANIO = GrupoController.PATH_BAREMABLES_ANIO;
  private static final String PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO = GrupoController.PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2, 2021" })
  void is_grupo_baremable_ok(Long grupoRef, Integer anio) throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO,
        HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, grupoRef, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2, 2023" })
  void is_grupo_baremable_ko(Long grupoRef, Integer anio) throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO,
        HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, grupoRef, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2021" })
  public void findAllByAnio_ok(Integer anio)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<GrupoDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMABLES_ANIO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<GrupoDto>>() {
        }, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<GrupoDto> grupos = response.getBody();
    int numElements = grupos.size();
    Assertions.assertThat(numElements).as("numGrupos").isEqualTo(1);

    Assertions.assertThat(grupos.get(0).getId()).as("get(0).getId())").isEqualTo(2L);
    Assertions.assertThat(grupos.get(0).getNombre()).as("get(0).getNombre())").isEqualTo("Grupo investigación 2");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql", 
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2023" })
  public void findAllByAnio_ko(Integer anio)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<GrupoDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMABLES_ANIO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<GrupoDto>>() {
        }, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

}
