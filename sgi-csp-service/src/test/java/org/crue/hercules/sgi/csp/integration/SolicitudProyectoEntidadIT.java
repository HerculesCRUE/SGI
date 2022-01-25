package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.SolicitudProyectoEntidadController;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudProyectoEntidadIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = SolicitudProyectoEntidadController.MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_SOLICITUD_PROYECTO_PRESUPUESTO = "/solicitudproyectopresupuestos";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitudProyectoEntidad() throws Exception {
    Long solicitudProyectoEntidadId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProyectoEntidadId).toUri();

    final ResponseEntity<SolicitudProyectoEntidad> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, "CSP-SOL-E"), SolicitudProyectoEntidad.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoEntidad solicitudProyectoEntidad = response.getBody();
    Assertions.assertThat(solicitudProyectoEntidad.getId()).as("getId()").isEqualTo(solicitudProyectoEntidadId);
    Assertions.assertThat(solicitudProyectoEntidad.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(1);
    Assertions.assertThat(solicitudProyectoEntidad.getConvocatoriaEntidadFinanciadora().getId())
        .as("getConvocatoriaEntidadFinanciadora().getId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoEntidad.getConvocatoriaEntidadGestora().getId())
        .as("getConvocatoriaEntidadGestora().getId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoEntidad.getSolicitudProyectoEntidadFinanciadoraAjena()).isNull();
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllBySolicitudProyectoEntidad_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoPresupuestoSubList()
      throws Exception {

    String[] roles = { "CSP-SOL-E" };
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "id>=21;id<=23";

    Long solicitudProyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTO)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudProyectoId).toUri();

    ResponseEntity<List<SolicitudProyectoPresupuesto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<SolicitudProyectoPresupuesto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoPresupuesto> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).as("get(0)").isNotNull();
    Assertions.assertThat(responseData.get(1)).as("get(1)").isNotNull();
    Assertions.assertThat(responseData.get(2)).as("get(2)").isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(21);
    Assertions.assertThat(responseData.get(1).getId()).as("get(1).getId()").isEqualTo(22);
    Assertions.assertThat(responseData.get(2).getId()).as("get(2).getId()").isEqualTo(23);

    Assertions.assertThat(responseData.get(0).getSolicitudProyectoEntidadId())
        .as("get(0).getSolicitudProyectoEntidadId()").isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getSolicitudProyectoEntidadId())
        .as("get(1).getSolicitudProyectoEntidadId()").isEqualTo(1);
    Assertions.assertThat(responseData.get(2).getSolicitudProyectoEntidadId())
        .as("get(2).getSolicitudProyectoEntidadId()").isEqualTo(1);

    Assertions.assertThat(responseData.get(0).getImporteSolicitado().doubleValue())
        .as("get(0).getImporteSolicitado()").isEqualTo(12000d);
    Assertions.assertThat(responseData.get(1).getImporteSolicitado().doubleValue())
        .as("get(1).getImporteSolicitado()").isEqualTo(11000d);
    Assertions.assertThat(responseData.get(2).getImporteSolicitado().doubleValue())
        .as("get(2).getImporteSolicitado()").isEqualTo(10000d);

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 21));
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 22));
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 23));

    Assertions.assertThat(responseData.get(0).getAnualidad()).as("get(0).getAnualidad()").isEqualTo(2022);
    Assertions.assertThat(responseData.get(1).getAnualidad()).as("get(1).getAnualidad()").isEqualTo(2022);
    Assertions.assertThat(responseData.get(2).getAnualidad()).as("get(2).getAnualidad()").isEqualTo(2022);
  }
}