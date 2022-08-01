package org.crue.hercules.sgi.csp.integration;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoAgrupacionGastoController;
import org.crue.hercules.sgi.csp.dto.AgrupacionGastoConceptoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAgrupacionGastoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoAgrupacionGastoOutput;
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

import java.net.URI;
import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoAgrupacionGastoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoAgrupacionGastoController.REQUEST_MAPPING;
  private static final String PATH_AGRUPACION_GASTO_CONCEPTO = "/agrupaciongastoconceptos";

  private HttpEntity<ProyectoAgrupacionGastoInput> buildRequest(HttpHeaders headers,
      ProyectoAgrupacionGastoInput entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "CSP-PRO-C", "CSP-PRO-E", "CSP-EJEC-E",
                "CSP-EJEC-V",
                "CSP-EJEC-INV-VR")));

    HttpEntity<ProyectoAgrupacionGastoInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoAgrupacionGastoOutput() throws Exception {
    ProyectoAgrupacionGastoInput proyectoAgrupacionGasto = buildMockProyectoAgrupacionGastoInput();

    final ResponseEntity<ProyectoAgrupacionGastoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, proyectoAgrupacionGasto),
        ProyectoAgrupacionGastoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoAgrupacionGastoOutput proyectoAgrupacionGastoCreated = response.getBody();

    Assertions.assertThat(proyectoAgrupacionGastoCreated.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoAgrupacionGastoCreated.getNombre()).as("getNombre()")
        .isEqualTo(proyectoAgrupacionGasto.getNombre());
    Assertions.assertThat(proyectoAgrupacionGastoCreated.getProyectoId())
        .as("getProyectoId()").isEqualTo(proyectoAgrupacionGasto.getProyectoId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto_agrupacion_gasto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoAgrupacionGastoOutput() throws Exception {

    String updatedNombre = "was updated";

    Long idToUpdate = 2L;
    ProyectoAgrupacionGastoInput proyectoAgrupacionGastoToUpdate = buildMockProyectoAgrupacionGastoInput();
    proyectoAgrupacionGastoToUpdate.setNombre(updatedNombre);

    final ResponseEntity<ProyectoAgrupacionGastoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, proyectoAgrupacionGastoToUpdate),
        ProyectoAgrupacionGastoOutput.class, idToUpdate);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoAgrupacionGastoOutput proyectoAgrupacionGastoUpdated = response.getBody();

    Assertions.assertThat(proyectoAgrupacionGastoUpdated.getId()).as("getId()").isEqualTo(idToUpdate);
    Assertions.assertThat(proyectoAgrupacionGastoUpdated.getNombre()).as("getNombre()")
        .isEqualTo(updatedNombre);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto_agrupacion_gasto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void existsById_Return200() throws Exception {
    // given: existing id
    Long id = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Void.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto_agrupacion_gasto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnProyectoAgrupacionGasto() throws Exception {
    Long proyectoAgrupacionGastoId = 1L;

    final ResponseEntity<ProyectoAgrupacionGastoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ProyectoAgrupacionGastoOutput.class,
        proyectoAgrupacionGastoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoAgrupacionGastoOutput proyectoAgrupacionGastoOutput = response.getBody();

    Assertions.assertThat(proyectoAgrupacionGastoOutput).isNotNull();
    Assertions.assertThat(proyectoAgrupacionGastoOutput.getNombre()).as("getNombre()")
        .isEqualTo("Ejecución");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto_agrupacion_gasto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Return204() throws Exception {
    // given: existing id
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto_agrupacion_gasto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsProyectoSubList() throws Exception {
    // first page, 2 elements per page sorted by id asc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "2");
    String sort = "id,asc";
    String filter = "";

    // when: find ProyectoAgrupacionGastoOutput
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<ProyectoAgrupacionGastoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null),
        new ParameterizedTypeReference<List<ProyectoAgrupacionGastoOutput>>() {
        });

    // given: ProyectoAgrupacionGastoOutput data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoAgrupacionGastoOutput> responseData = response.getBody();

    Assertions.assertThat(responseData).hasSize(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("2");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(responseData.get(0).getProyectoId()).as("get(0).getProyectoId())")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("Ejecución");
    Assertions.assertThat(responseData.get(1).getProyectoId()).as("get(1).getProyectoId())")
        .isEqualTo(2);
    Assertions.assertThat(responseData.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("Personal");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
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
      "classpath:scripts/agrupacion_gasto_concepto.sql", })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllById_WithPagingSortingAndFilter_ReturnsAgrupacionGastoConceptoOutputSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "2");
    String sort = "id,asc";
    String filter = "id>=1;id<=2";

    Long ProyectoAgrupacionGastoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_AGRUPACION_GASTO_CONCEPTO).queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(ProyectoAgrupacionGastoId).toUri();

    final ResponseEntity<List<AgrupacionGastoConceptoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null),
        new ParameterizedTypeReference<List<AgrupacionGastoConceptoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("2");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    List<AgrupacionGastoConceptoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).hasSize(2);

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);
  }

  private ProyectoAgrupacionGastoInput buildMockProyectoAgrupacionGastoInput() {
    return ProyectoAgrupacionGastoInput.builder()
        .nombre("Ejecucion Test")
        .proyectoId(1L)
        .build();
  }
}