package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
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

/**
 * Test de integracion de SolicitudProyectoEntidadFinanciadoraAjena.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SolicitudProyectoEntidadFinanciadoraAjenaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoentidadfinanciadoraajenas";

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
  public void create_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {

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
  public void update_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {
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
  public void delete_Return204() throws Exception {
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
  public void findById_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {
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
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion()").isEqualTo(20);
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
        .porcentajeFinanciacion(50)
        .build();
    // @formatter:on

    return solicitudProyectoEntidadFinanciadoraAjena;
  }

}
