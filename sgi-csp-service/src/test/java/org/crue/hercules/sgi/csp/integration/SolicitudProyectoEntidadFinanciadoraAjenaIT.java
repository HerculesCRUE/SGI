package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de SolicitudProyectoEntidadFinanciadoraAjena.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudProyectoEntidadFinanciadoraAjenaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoentidadfinanciadoraajenas";
  private static final String PATH_SOLICITUD_PROYECTO_PRESUPUESTOS = "/solicitudproyectopresupuestos";
  private static final String PATH_SOLICITUD_PROYECTO_ENTIDAD = "/solicitudproyectoentidad";

  private HttpEntity<SolicitudProyectoEntidadFinanciadoraAjena> buildRequest(HttpHeaders headers,
      SolicitudProyectoEntidadFinanciadoraAjena entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E", "CSP-SOL-V", "AUTH")));

    HttpEntity<SolicitudProyectoEntidadFinanciadoraAjena> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {

    // given: new SolicitudProyectoEntidadFinanciadoraAjena
    SolicitudProyectoEntidadFinanciadoraAjena newSolicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        null);

    // when: create SolicitudProyectoEntidadFinanciadoraAjena
    final ResponseEntity<SolicitudProyectoEntidadFinanciadoraAjena> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, newSolicitudProyectoEntidadFinanciadoraAjena),
        SolicitudProyectoEntidadFinanciadoraAjena.class);

    // then: new SolicitudProyectoEntidadFinanciadoraAjena is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    SolicitudProyectoEntidadFinanciadoraAjena responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(newSolicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId());
    Assertions.assertThat(responseData.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(newSolicitudProyectoEntidadFinanciadoraAjena.getEntidadRef());
    Assertions.assertThat(responseData.getFuenteFinanciacion().getId()).as("getFuenteFinanciacion().getId()")
        .isEqualTo(newSolicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().getId());
    Assertions.assertThat(responseData.getTipoFinanciacion().getId()).as("getTipoFinanciacion().getId()")
        .isEqualTo(newSolicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion().getId());
    Assertions.assertThat(responseData.getPorcentajeFinanciacion()).as("getPorcentajeFinanciacion()")
        .isEqualTo(newSolicitudProyectoEntidadFinanciadoraAjena.getPorcentajeFinanciacion());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {
    Long idFuenteFinanciacion = 1L;
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        idFuenteFinanciacion);

    final ResponseEntity<SolicitudProyectoEntidadFinanciadoraAjena> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, solicitudProyectoEntidadFinanciadoraAjena), SolicitudProyectoEntidadFinanciadoraAjena.class,
        idFuenteFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizado = response.getBody();
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getSolicitudProyectoId())
        .as("getSolicitudProyectoId()").isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getEntidadRef());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().getId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion().getId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getPorcentajeFinanciacion());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing SolicitudProyectoEntidadFinanciadoraAjena to be deleted
    Long id = 1L;

    // when: delete SolicitudProyectoEntidadFinanciadoraAjena
    final ResponseEntity<SolicitudProyectoEntidadFinanciadoraAjena> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        SolicitudProyectoEntidadFinanciadoraAjena.class, id);

    // then: SolicitudProyectoEntidadFinanciadoraAjena deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {
    Long idSolicitudProyectoEntidadFinanciadoraAjena = 1L;

    final ResponseEntity<SolicitudProyectoEntidadFinanciadoraAjena> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoEntidadFinanciadoraAjena.class, idSolicitudProyectoEntidadFinanciadoraAjena);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = response.getBody();
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getId()).as("getId()")
        .isEqualTo(idSolicitudProyectoEntidadFinanciadoraAjena);
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId())
        .as("getSolicitudProyectoId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo("entidad-001");
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getPorcentajeFinanciacion().floatValue())
        .as("getPorcentajeFinanciacion()").isEqualTo(20F);
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
  void hasSolicitudProyectoPresupuestoEntidad_ReturnsHttpStatusCode200() throws Exception {
    Long solicitudProyectoEntidadFinanciadoraAjenaId = 1L;
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTOS)
        .buildAndExpand(solicitudProyectoEntidadFinanciadoraAjenaId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD, buildRequest(null, null),
        Void.class);

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
    "classpath:scripts/solicitud_proyecto_entidad.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findBySolicitudProyectoEntidadFinanciadoraAjena_ReturnsSolicitudProyectoEntidad() throws Exception {
    Long solicitudProyectoEntidadFinanciadoraAjenaId = 1L;
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_ENTIDAD)
        .buildAndExpand(solicitudProyectoEntidadFinanciadoraAjenaId).toUri();

    final ResponseEntity<SolicitudProyectoEntidad> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null),
        SolicitudProyectoEntidad.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getId()).isEqualTo(1L);
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoEntidadFinanciadoraAjena
   * 
   * @param id id del SolicitudProyectoEntidadFinanciadoraAjena
   * @return el objeto SolicitudProyectoEntidadFinanciadoraAjena
   */
  private SolicitudProyectoEntidadFinanciadoraAjena generarMockSolicitudProyectoEntidadFinanciadoraAjena(Long id) {

    // @formatter:off
    FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder()
        .id(id == null ? 1 : id)
        .activo(true)
        .build();

    TipoFinanciacion tipoFinanciacion = TipoFinanciacion.builder()
        .id(id == null ? 1 : id)
        .activo(true)
        .build();

    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = SolicitudProyectoEntidadFinanciadoraAjena
        .builder()
        .id(id)
        .solicitudProyectoId(id == null ? 1 : id)
        .entidadRef("entidad-" + (id == null ? 0 : String.format("%03d", id)))
        .fuenteFinanciacion(fuenteFinanciacion)
        .tipoFinanciacion(tipoFinanciacion)
        .porcentajeFinanciacion(BigDecimal.valueOf(50))
        .build();
    // @formatter:on

    return solicitudProyectoEntidadFinanciadoraAjena;
  }

}
