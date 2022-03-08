package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.SolicitudProyectoAreaConocimientoController;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SolicitudProyectoAreaConocimientoIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = SolicitudProyectoAreaConocimientoController.REQUEST_MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";

  private HttpEntity<SolicitudProyectoAreaConocimiento> buildRequest(HttpHeaders headers,
      SolicitudProyectoAreaConocimiento entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<SolicitudProyectoAreaConocimiento> request = new HttpEntity<>(entity, headers);
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
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_area.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsSolicitudProyectoAreaConocimiento() throws Exception {
    Long solicitudProyectoId = 1L;
    SolicitudProyectoAreaConocimiento toCreate = buildMockSolicitudProyectoAreaConocimiento(null, "000-4",
        solicitudProyectoId);

    String[] roles = { "CSP-SOL-E" };

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<SolicitudProyectoAreaConocimiento> response = restTemplate.exchange(uri, HttpMethod.POST,
        buildRequest(null, toCreate, roles), SolicitudProyectoAreaConocimiento.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudProyectoAreaConocimiento created = response.getBody();
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getAreaConocimientoRef()).as("getAreaConocimientoRef()")
        .isEqualTo(toCreate.getAreaConocimientoRef());
    Assertions.assertThat(created.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(toCreate.getSolicitudProyectoId());
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/solicitud_proyecto_area.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCodeNoContent() throws Exception {
    Long toDeleteId = 3L;

    String[] roles = { "CSP-SOL-E" };

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(toDeleteId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  private SolicitudProyectoAreaConocimiento buildMockSolicitudProyectoAreaConocimiento(Long id,
      String areaConocimientoRef, Long solicitudProyectoId) {
    return SolicitudProyectoAreaConocimiento.builder()
        .id(id)
        .areaConocimientoRef(areaConocimientoRef)
        .solicitudProyectoId(solicitudProyectoId)
        .build();
  }
}