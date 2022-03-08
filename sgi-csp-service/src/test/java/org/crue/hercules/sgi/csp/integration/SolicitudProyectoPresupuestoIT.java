package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de SolicitudProyectoPresupuesto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudProyectoPresupuestoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectopresupuestos";

  private HttpEntity<SolicitudProyectoPresupuesto> buildRequest(HttpHeaders headers,
      SolicitudProyectoPresupuesto entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-SOL-B", "CSP-SOL-C", "CSP-SOL-E", "CSP-SOL-V", "AUTH")));

    HttpEntity<SolicitudProyectoPresupuesto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudProyectoPresupuesto() throws Exception {
    // given: new SolicitudProyectoPresupuesto
    SolicitudProyectoPresupuesto newSolicitudProyectoPresupuesto = generarMockSolicitudProyectoPresupuesto(1L, 1L, 1L);
    newSolicitudProyectoPresupuesto.setId(null);

    // when: create SolicitudProyectoPresupuesto
    final ResponseEntity<SolicitudProyectoPresupuesto> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newSolicitudProyectoPresupuesto), SolicitudProyectoPresupuesto.class);

    // then: new SolicitudProyectoPresupuesto is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    SolicitudProyectoPresupuesto responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(newSolicitudProyectoPresupuesto.getSolicitudProyectoId());
    Assertions.assertThat(responseData.getConceptoGasto().getId()).as("getConceptoGasto().getId()")
        .isEqualTo(newSolicitudProyectoPresupuesto.getConceptoGasto().getId());
    Assertions.assertThat(responseData.getAnualidad()).as("getAnualidad()")
        .isEqualTo(newSolicitudProyectoPresupuesto.getAnualidad());
    Assertions.assertThat(responseData.getImporteSolicitado()).as("getImporteSolicitado()")
        .isEqualTo(newSolicitudProyectoPresupuesto.getImporteSolicitado());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(newSolicitudProyectoPresupuesto.getObservaciones());
    Assertions.assertThat(responseData.getSolicitudProyectoEntidadId()).as("getSolicitudProyectoEntidadId()").isNull();

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudProyectoPresupuesto() throws Exception {
    Long idSolicitudProyectoPresupuesto = 1L;
    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = generarMockSolicitudProyectoPresupuesto(1L, 1L, 1L);
    solicitudProyectoPresupuesto.setObservaciones("actualizado");

    final ResponseEntity<SolicitudProyectoPresupuesto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, solicitudProyectoPresupuesto),
        SolicitudProyectoPresupuesto.class, idSolicitudProyectoPresupuesto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoPresupuesto responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(solicitudProyectoPresupuesto.getSolicitudProyectoId());
    Assertions.assertThat(responseData.getConceptoGasto().getId()).as("getConceptoGasto().getId()")
        .isEqualTo(solicitudProyectoPresupuesto.getConceptoGasto().getId());
    Assertions.assertThat(responseData.getAnualidad()).as("getAnualidad()")
        .isEqualTo(solicitudProyectoPresupuesto.getAnualidad());
    Assertions.assertThat(responseData.getImporteSolicitado()).as("getImporteSolicitado()")
        .isEqualTo(solicitudProyectoPresupuesto.getImporteSolicitado());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitudProyectoPresupuesto.getObservaciones());
    Assertions.assertThat(responseData.getSolicitudProyectoEntidadId()).as("getSolicitudProyectoEntidadId()").isNull();

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idSolicitudProyectoPresupuesto = 1L;

    final ResponseEntity<SolicitudProyectoPresupuesto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        SolicitudProyectoPresupuesto.class, idSolicitudProyectoPresupuesto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Proyecto.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoPresupuesto() throws Exception {
    Long idSolicitudProyectoPresupuesto = 1L;

    final ResponseEntity<SolicitudProyectoPresupuesto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoPresupuesto.class, idSolicitudProyectoPresupuesto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoPresupuesto responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(idSolicitudProyectoPresupuesto);
    Assertions.assertThat(responseData.getSolicitudProyectoId()).as("getSolicitudProyectoId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getConceptoGasto().getId()).as("getConceptoGasto().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getAnualidad()).as("getAnualidad()").isEqualTo(2020);
    Assertions.assertThat(responseData.getImporteSolicitado()).as("getImporteSolicitado()")
        .isEqualTo(new BigDecimal("1000.00"));
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-001");
    Assertions.assertThat(responseData.getSolicitudProyectoEntidadId()).as("getSolicitudProyectoEntidadId()").isNull();
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPresupuesto
   * 
   * @param id                  Id {@link SolicitudProyectoPresupuesto}.
   * @param solicitudProyectoId Id {@link SolicitudProyecto}.
   * @param conceptoGastoId     Id {@link ConceptoGasto}.
   * @return el objeto {@link SolicitudProyectoPresupuesto}.
   */
  private SolicitudProyectoPresupuesto generarMockSolicitudProyectoPresupuesto(Long id, Long solicitudProyectoId,
      Long conceptoGastoId) {

    String suffix = String.format("%03d", id);

    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = SolicitudProyectoPresupuesto
        .builder()// @formatter:off
        .id(id)
        .solicitudProyectoId(solicitudProyectoId)
        .conceptoGasto(ConceptoGasto.builder().id(conceptoGastoId).build())
        .anualidad(2020)
        .importeSolicitado(new BigDecimal("1000.00"))
        .observaciones("observaciones-" + suffix)
        .solicitudProyectoEntidadId(null)
        .build();// @formatter:on

    return solicitudProyectoPresupuesto;
  }
}
