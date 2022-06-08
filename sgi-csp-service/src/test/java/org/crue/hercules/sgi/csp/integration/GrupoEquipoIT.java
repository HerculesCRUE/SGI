package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoEquipoController;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoDto;
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
 * Test de integracion de GrupoEquipo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GrupoEquipoIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = GrupoEquipoController.REQUEST_MAPPING;
  private static final String PATH_BAREMABLES_GRUPO_REF_ANIO = GrupoEquipoController.PATH_BAREMABLES_GRUPO_REF_ANIO;
  private static final String PATH_PERSONA_BAREMABLE_PERSONA_REF_ANIO = GrupoEquipoController.PATH_PERSONA_BAREMABLE_PERSONA_REF_ANIO;
  private static final String PATH_GRUPOS_PERSONA_REF_ANIO = GrupoEquipoController.PATH_GRUPOS_PERSONA_REF_ANIO;

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
  @CsvSource({ "'22932567', 2021" })
  void is_persona_baremable_ok(String personaRef, Integer anio) throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PERSONA_BAREMABLE_PERSONA_REF_ANIO,
        HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, personaRef, anio);

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
  @CsvSource({ "'22932567', 2021" })
  void findGruposEquiposByPersonaAndAnio(String personaRef, Integer anio) throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<Long>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_GRUPOS_PERSONA_REF_ANIO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<Long>>() {
        }, personaRef, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Long> gruposEquipo = response.getBody();
    int numElements = gruposEquipo.size();
    Assertions.assertThat(numElements).as("numGruposEquipo").isEqualTo(2);

    Assertions.assertThat(gruposEquipo.get(0)).as("get(0))").isEqualTo(1L);
    Assertions.assertThat(gruposEquipo.get(1)).as("get(1))").isEqualTo(2L);
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
  public void findByGrupoIdAndAnio_ok(Long grupoRef, Integer anio)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<GrupoEquipoDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMABLES_GRUPO_REF_ANIO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<GrupoEquipoDto>>() {
        }, grupoRef, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<GrupoEquipoDto> gruposEquipo = response.getBody();
    int numElements = gruposEquipo.size();
    Assertions.assertThat(numElements).as("numGruposEquipo").isEqualTo(2);

    Assertions.assertThat(gruposEquipo.get(0).getPersonaRef()).as("get(0).getPersonaRef())").isEqualTo("22932567");
    Assertions.assertThat(gruposEquipo.get(1).getPersonaRef()).as("get(1).getPersonaRef())").isEqualTo("01889311");
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
  @CsvSource({ "'22932567', 2023" })
  void is_persona_baremable_ko(String personaRef, Integer anio) throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PERSONA_BAREMABLE_PERSONA_REF_ANIO,
        HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, personaRef, anio);

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
  @CsvSource({ "2, 2023" })
  public void findByGrupoIdAndAnio_ko(Long grupoRef, Integer anio)
      throws Exception {
    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<GrupoEquipoDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMABLES_GRUPO_REF_ANIO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<GrupoEquipoDto>>() {
        }, grupoRef, anio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}
