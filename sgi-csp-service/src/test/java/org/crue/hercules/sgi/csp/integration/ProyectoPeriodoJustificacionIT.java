package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoPeriodoJustificacionController;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionOutput;
import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
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
class ProyectoPeriodoJustificacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_PROYECTO_ID = "/{proyectoId}";
  private static final String CONTROLLER_BASE_PATH = ProyectoPeriodoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_PROYECTO_PERIODO_JUSTIFICACION = "/proyectoperiodojustificacion";
  
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
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsProyectoPeriodoJustificacionOutputList() throws Exception {
    String roles = "CSP-PRO-E";
    Long proyectoId = 1L;
    ProyectoPeriodoJustificacionInput periodo1 = buildMockProyectoPeriodoJusitificacionInput(1L);
    ProyectoPeriodoJustificacionInput periodo2 = buildMockProyectoPeriodoJusitificacionInput(2L);
    ProyectoPeriodoJustificacionInput periodo3 = buildMockProyectoPeriodoJusitificacionInput(3L);
    
    periodo1.setObservaciones("Actualizado perido 1");
    periodo2.setObservaciones("Actualizado perido 2");
    periodo3.setObservaciones("Actualizado perido 3");

    periodo1.setNumPeriodo(1L);
    periodo2.setNumPeriodo(2L);
    periodo3.setNumPeriodo(3L);

    periodo1.setFechaInicio(Instant.now());
    periodo1.setFechaFin(Instant.now().plusSeconds(86399));
    periodo2.setFechaInicio(Instant.now().plusSeconds(86400));
    periodo2.setFechaFin(Instant.now().plusSeconds(86400 * 2 -1));
    periodo3.setFechaInicio(Instant.now().plusSeconds(86400*2));
    periodo3.setFechaFin(Instant.now().plusSeconds(86400*3));

    periodo3.setTipoJustificacion(TipoJustificacion.FINAL);

    List<ProyectoPeriodoJustificacionInput>toUpdate = Arrays.asList(periodo1, periodo2, periodo3);

    final ResponseEntity<List<ProyectoPeriodoJustificacionOutput>> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_PROYECTO_ID,
        HttpMethod.PATCH, buildRequest(null, toUpdate, roles), new ParameterizedTypeReference<List<ProyectoPeriodoJustificacionOutput>>() {}, proyectoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody()).size().as("size()").isEqualTo(3);
    
    ProyectoPeriodoJustificacionOutput updatedPeriodo1 = response.getBody().get(0);
    ProyectoPeriodoJustificacionOutput updatedPeriodo2 = response.getBody().get(1);
    ProyectoPeriodoJustificacionOutput updatedPeriodo3 = response.getBody().get(2);

    Assertions.assertThat(updatedPeriodo1.getObservaciones()).isEqualTo(periodo1.getObservaciones());
    Assertions.assertThat(updatedPeriodo2.getObservaciones()).isEqualTo(periodo2.getObservaciones());
    Assertions.assertThat(updatedPeriodo3.getObservaciones()).isEqualTo(periodo3.getObservaciones());
    
    Assertions.assertThat(updatedPeriodo1.getFechaInicio()).isEqualTo(periodo1.getFechaInicio());
    Assertions.assertThat(updatedPeriodo2.getFechaInicio()).isEqualTo(periodo2.getFechaInicio());
    Assertions.assertThat(updatedPeriodo3.getFechaInicio()).isEqualTo(periodo3.getFechaInicio());

    Assertions.assertThat(updatedPeriodo1.getFechaFin()).isEqualTo(periodo1.getFechaFin());
    Assertions.assertThat(updatedPeriodo2.getFechaFin()).isEqualTo(periodo2.getFechaFin());
    Assertions.assertThat(updatedPeriodo3.getFechaFin()).isEqualTo(periodo3.getFechaFin());

    Assertions.assertThat(updatedPeriodo1.getTipoJustificacion()).isEqualTo(periodo1.getTipoJustificacion());
    Assertions.assertThat(updatedPeriodo2.getTipoJustificacion()).isEqualTo(periodo2.getTipoJustificacion());
    Assertions.assertThat(updatedPeriodo3.getTipoJustificacion()).isEqualTo(periodo3.getTipoJustificacion());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoPeriodoJustificacionOutput() throws Exception {
    String roles = "CSP-PRO-E";
    Long idPeriodo = 1L;

    final ResponseEntity<ProyectoPeriodoJustificacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), ProyectoPeriodoJustificacionOutput.class, idPeriodo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPeriodoJustificacionOutput periodo = response.getBody();

    Assertions.assertThat(periodo.getId()).as("getId()").isEqualTo(idPeriodo);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsProyectoPeriodoJustificacionOutputSubList() throws Exception {
    String[] roles = {"CSP-PRO-E"};
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "";
    Long proyectoId = 1L;

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_PERIODO_JUSTIFICACION).queryParam("s", sort).queryParam("q", filter)
        .buildAndExpand(proyectoId).toUri();
    
    final ResponseEntity<List<ProyectoPeriodoJustificacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoPeriodoJustificacionOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoPeriodoJustificacionOutput> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).as("get(0)").isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(3);
  }

  private ProyectoPeriodoJustificacionInput buildMockProyectoPeriodoJusitificacionInput(Long id) {
    return ProyectoPeriodoJustificacionInput.builder()
    .id(id)
    .convocatoriaPeriodoJustificacionId(1L)
    .fechaFin(Instant.now().plusSeconds(36000 * 10000))
    .fechaInicio(Instant.now())
    .numPeriodo(1L)
    .observaciones("testing ProyectoPeriodoJusitificacion")
    .proyectoId(1L)
    .tipoJustificacion(TipoJustificacion.PERIODICO)
    .build();
  }
}