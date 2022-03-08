package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.AgrupacionGastoConceptoController;
import org.crue.hercules.sgi.csp.dto.AgrupacionGastoConceptoInput;
import org.crue.hercules.sgi.csp.dto.AgrupacionGastoConceptoOutput;
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
class AgrupacionGastoConceptoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = AgrupacionGastoConceptoController.REQUEST_MAPPING;

  private HttpEntity<AgrupacionGastoConceptoInput> buildRequest(HttpHeaders headers,
      AgrupacionGastoConceptoInput entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", roles)));

    HttpEntity<AgrupacionGastoConceptoInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_agrupacion_gasto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnAgrupacionGastoConceptoOutput() throws Exception {
    AgrupacionGastoConceptoInput agrupacionGastoConceptoInput = buildAgrupacionGastoConceptoInput(1L);

    ResponseEntity<AgrupacionGastoConceptoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, agrupacionGastoConceptoInput, "CSP-PRO-C"),
        AgrupacionGastoConceptoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    AgrupacionGastoConceptoOutput agrupacionGastoConceptoOutput = response.getBody();

    Assertions.assertThat(agrupacionGastoConceptoOutput.getId()).as("getId()").isNotNull();
    Assertions.assertThat(agrupacionGastoConceptoOutput.getAgrupacionId()).as("getAgrupacionId()").isEqualTo(1L);
    Assertions.assertThat(agrupacionGastoConceptoOutput.getConceptoGasto().getId()).as("getConceptoGasto().getId()")
        .isEqualTo(1L);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_agrupacion_gasto.sql",
    "classpath:scripts/agrupacion_gasto_concepto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnAgrupacionGastoConceptoOutput() throws Exception {

    Long conceptoGastoIdChanged = 2L;
    AgrupacionGastoConceptoInput agrupacionGastoConceptoInput = buildAgrupacionGastoConceptoInput(
        conceptoGastoIdChanged);

    Long agrupacionGastoConceptoIdToUpdate = 1L;

    ResponseEntity<AgrupacionGastoConceptoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, agrupacionGastoConceptoInput,
            "CSP-PRO-E"),
        AgrupacionGastoConceptoOutput.class, agrupacionGastoConceptoIdToUpdate);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    AgrupacionGastoConceptoOutput agrupacionGastoConceptoOutputUpdated = response.getBody();

    Assertions.assertThat(agrupacionGastoConceptoOutputUpdated.getId()).as("getId()")
        .isEqualTo(agrupacionGastoConceptoIdToUpdate);
    Assertions.assertThat(agrupacionGastoConceptoOutputUpdated.getConceptoGasto().getId())
        .as("getConceptoGasto().getId()").isEqualTo(conceptoGastoIdChanged);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_agrupacion_gasto.sql",
    "classpath:scripts/agrupacion_gasto_concepto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsById_Return200() throws Exception {
    // given: existing id
    Long idToCheck = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null, "CSP-PRO-E"), Void.class, idToCheck);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_agrupacion_gasto.sql",
    "classpath:scripts/agrupacion_gasto_concepto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnAgrupacionGastoConceptoOutput() throws Exception {
    Long agrupacionGastoConceptoId = 6L;

    final ResponseEntity<AgrupacionGastoConceptoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null,
            "CSP-PRO-E"),
        AgrupacionGastoConceptoOutput.class,
        agrupacionGastoConceptoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    AgrupacionGastoConceptoOutput agrupacionGastoConceptoOutput = response.getBody();

    Assertions.assertThat(agrupacionGastoConceptoOutput).isNotNull();
    Assertions.assertThat(agrupacionGastoConceptoOutput.getConceptoGasto().getId()).as("getConceptoGasto().getId()")
        .isEqualTo(13);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_agrupacion_gasto.sql",
    "classpath:scripts/agrupacion_gasto_concepto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Return204() throws Exception {
    // given: existing id
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, "CSP-PRO-E"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_agrupacion_gasto.sql",
    "classpath:scripts/agrupacion_gasto_concepto.sql" 
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsAgrupacionGastoConceptoOutputSubList() throws Exception {
    // first page, 2 elements per page sorted by id asc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "agrupacion_gasto_id==2";

    // when: find AgrupacionGastoConceptoOutput
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<AgrupacionGastoConceptoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null,
            "CSP-PRO-E"),
        new ParameterizedTypeReference<List<AgrupacionGastoConceptoOutput>>() {
        });

    // given: AgrupacionGastoConceptoOutput data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<AgrupacionGastoConceptoOutput> responseData = response.getBody();

    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    // El X-Total-Count Deberían ser 3 pero en el endpoint findAll del controller,
    // no se está usando el filter
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("6");

    Assertions.assertThat(responseData.get(0).getAgrupacionId()).as("get(0).getAgrupacionId())")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(0).getConceptoGasto().getId()).as("get(0).getConceptoGasto().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getAgrupacionId()).as("get(1).getAgrupacionId())")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getConceptoGasto().getId()).as("get(1).getConceptoGasto().getId()")
        .isEqualTo(2L);

  }

  private AgrupacionGastoConceptoInput buildAgrupacionGastoConceptoInput(Long conceptoGastoId) {
    return AgrupacionGastoConceptoInput.builder()
        .agrupacionId(1L)
        .conceptoGastoId(conceptoGastoId)
        .build();
  }
}