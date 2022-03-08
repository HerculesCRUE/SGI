package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de SolicitudProyecto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudProyectoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyecto";
  private static final String PATH_SOLICITUD_PRESUPUESTO = "/solicitudpresupuesto";
  private static final String PATH_SOLICITUD_SOCIO = "/solicitudsocio";
  private static final String PATH_PARAMETER_SOLICITUD_PROYECTO_ID = "/{solicitudProyectoId}";
  private static final String PATH_SOLICITUD_PROYECTO_SOCIOS = "/solicitudproyectosocios";
  private static final String PATH_PERIODOS_PAGO = "/periodospago";
  private static final String PATH_PERIODOS_JUSTIFICACION = "/periodosjustificacion";
  private static final String PATH_COORDINADOR = "/coordinador";

  private HttpEntity<SolicitudProyecto> buildRequest(HttpHeaders headers, SolicitudProyecto entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "AUTH", "CSP-SOL-C", "CSP-SOL-E", "CSP-PRO-E", "CSP-PRO-MOD-V")));

    HttpEntity<SolicitudProyecto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsSolicitudProyecto() throws Exception {
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudProyecto), SolicitudProyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudProyecto solicitudProyectoCreado = response.getBody();
    Assertions.assertThat(solicitudProyectoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudProyectoCreado.getColaborativo()).as("getColaborativo()")
        .isEqualTo(solicitudProyecto.getColaborativo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsSolicitudProyecto() throws Exception {
    Long idSolicitudProyecto = 1L;
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitudProyecto), SolicitudProyecto.class, idSolicitudProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyecto solicitudProyectoActualizado = response.getBody();
    Assertions.assertThat(solicitudProyectoActualizado.getId()).as("getId()").isEqualTo(solicitudProyecto.getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing SolicitudProyecto to be deleted
    Long id = 1L;

    // when: delete SolicitudProyecto
    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), SolicitudProyecto.class, id);

    // then: SolicitudProyecto deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitudProyecto() throws Exception {
    Long idSolicitudProyecto = 1L;

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), SolicitudProyecto.class, idSolicitudProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyecto solicitudProyecto = response.getBody();
    Assertions.assertThat(solicitudProyecto.getId()).as("getId()").isEqualTo(idSolicitudProyecto);
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
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudPresupuesto_ReturnsHttpStatus200() throws Exception {
    Long solicitudProyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PRESUPUESTO)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudSocio_ReturnsHttpStatus200() throws Exception {
    Long solicitudProyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_SOCIO)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio_periodo_pago.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudProyectoSocioPeriodosPago_ReturnsHttpStatus200() throws Exception {
    Long solicitudProyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_SOLICITUD_PROYECTO_ID + PATH_SOLICITUD_PROYECTO_SOCIOS
            + PATH_PERIODOS_PAGO)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio_periodo_justificacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudProyectoSocioPeriodosJustificacion_ReturnsHttpStatus200() throws Exception {
    Long solicitudProyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_SOLICITUD_PROYECTO_ID + PATH_SOLICITUD_PROYECTO_SOCIOS
            + PATH_PERIODOS_JUSTIFICACION)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasAnySolicitudProyectoSocioWithRolCoordinador_ReturnsHttpStatus200() throws Exception {
    Long solicitudProyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_SOLICITUD_PROYECTO_ID + PATH_SOLICITUD_PROYECTO_SOCIOS
            + PATH_COORDINADOR)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  /**
   * Función que devuelve un objeto SolicitudProyecto
   * 
   * @param solicitudProyectoId
   * @return el objeto SolicitudProyecto
   */
  private SolicitudProyecto generarSolicitudProyecto(Long solicitudProyectoId) {
    // formatter: off
    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder().id(solicitudProyectoId)
        .acronimo("acronimo-" + solicitudProyectoId).colaborativo(Boolean.TRUE).tipoPresupuesto(TipoPresupuesto.GLOBAL)
        .coordinado(Boolean.TRUE).build();
    // formatter: on
    return solicitudProyecto;
  }

}
