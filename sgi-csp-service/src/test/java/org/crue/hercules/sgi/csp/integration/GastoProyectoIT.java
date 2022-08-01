package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GastoProyectoController;
import org.crue.hercules.sgi.csp.dto.GastoProyectoInput;
import org.crue.hercules.sgi.csp.dto.GastoProyectoInput.EstadoGastoProyecto;
import org.crue.hercules.sgi.csp.dto.GastoProyectoOutput;
import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto.TipoEstadoGasto;
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
class GastoProyectoIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = GastoProyectoController.MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_ESTADOS_GASTO_PROYECTO = "/estadosgastoproyecto";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);

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
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/gasto_proyecto.sql",
      "classpath:scripts/estado_gasto_proyecto.sql",
      "classpath:scripts/gasto_proyecto_update_estado.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsGastoProyectoOutputList() throws Exception {
    // first page, 3 elements per page sorted by id asc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    String[] roles = { "CSP-EJEC-E", "CSP-EJEC-V", "CSP-EJEC-INV-VR" };

    final ResponseEntity<List<GastoProyectoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<GastoProyectoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<GastoProyectoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
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
      "classpath:scripts/concepto_gasto.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsGastoProyectoOutput() throws Exception {
    GastoProyectoInput toCreate = buildMockGastoProyectoInput(null);

    final ResponseEntity<GastoProyectoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, "CSP-EJEC-E"),
        GastoProyectoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    GastoProyectoOutput created = response.getBody();

    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getProyectoId()).as("getProyectoId()")
        .isEqualTo(toCreate.getProyectoId());
    Assertions.assertThat(created.getFechaCongreso())
        .as("getFechaCongreso()").isEqualTo(toCreate.getFechaCongreso());
    Assertions.assertThat(created.getImporteInscripcion())
        .as("getImporteInscripcion()").isEqualTo(toCreate.getImporteInscripcion());
    Assertions.assertThat(created.getConceptoGasto().getId())
        .as("getConceptoGasto().getId()").isEqualTo(toCreate.getConceptoGastoId());
    Assertions.assertThat(created.getObservaciones())
        .as("getObservaciones()").isEqualTo(toCreate.getObservaciones());
    Assertions.assertThat(created.getEstado()).isNotNull();
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
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/gasto_proyecto.sql",
      "classpath:scripts/estado_gasto_proyecto.sql",
      "classpath:scripts/gasto_proyecto_update_estado.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllEstadoGastoProyecto_WithPagingSorting_ReturnsGastoProyectoOutputList() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-EJEC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";

    Long gastoProyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ESTADOS_GASTO_PROYECTO)
        .queryParam("s", sort).buildAndExpand(gastoProyectoId).toUri();

    final ResponseEntity<List<EstadoGastoProyecto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "CSP-EJEC-V"), new ParameterizedTypeReference<List<EstadoGastoProyecto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<EstadoGastoProyecto> estadosGastoProyecto = response.getBody();

    Assertions.assertThat(estadosGastoProyecto).hasSize(1);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");
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
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/gasto_proyecto.sql",
      "classpath:scripts/estado_gasto_proyecto.sql",
      "classpath:scripts/gasto_proyecto_update_estado.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsGastoProyectoOutput() throws Exception {

    Long gastoProyectoId = 1L;
    GastoProyectoInput toUpdate = buildMockGastoProyectoInput(gastoProyectoId);
    toUpdate.setEstado(null);
    toUpdate.setObservaciones("ha sido actualizado");

    final ResponseEntity<GastoProyectoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, "CSP-EJEC-E"), GastoProyectoOutput.class, gastoProyectoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    GastoProyectoOutput updated = response.getBody();

    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getObservaciones()).as("getObservaciones()").isEqualTo(updated.getObservaciones());

  }

  private GastoProyectoInput buildMockGastoProyectoInput(Long gastoProyectoId) {
    return GastoProyectoInput.builder()
        .conceptoGastoId(1L)
        .fechaCongreso(Instant.now())
        .gastoRef("00001")
        .proyectoId(1L)
        .observaciones("Testing create")
        .importeInscripcion(new BigDecimal(1111))
        .estado(EstadoGastoProyecto.builder()
            .estado(TipoEstadoGasto.VALIDADO)
            .comentario("Testing creating new GastoProyecto")
            .fechaEstado(Instant.now())
            .build())
        .build();
  }
}