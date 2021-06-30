package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de SolicitudProyecto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudProyectoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyecto";

  private HttpEntity<SolicitudProyecto> buildRequest(HttpHeaders headers, SolicitudProyecto entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudProyecto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudProyecto() throws Exception {
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudProyecto), SolicitudProyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudProyecto solicitudProyectoCreado = response.getBody();
    Assertions.assertThat(solicitudProyectoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudProyectoCreado.getTitulo()).as("getTitulo()")
        .isEqualTo(solicitudProyecto.getTitulo());
    Assertions.assertThat(solicitudProyectoCreado.getColaborativo()).as("getColaborativo()")
        .isEqualTo(solicitudProyecto.getColaborativo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudProyecto() throws Exception {
    Long idSolicitudProyecto = 1L;
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);
    solicitudProyecto.setTitulo("titulo-modificado");

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitudProyecto), SolicitudProyecto.class, idSolicitudProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyecto solicitudProyectoActualizado = response.getBody();
    Assertions.assertThat(solicitudProyectoActualizado.getId()).as("getId()").isEqualTo(solicitudProyecto.getId());
    Assertions.assertThat(solicitudProyectoActualizado.getTitulo()).as("getTitulo()")
        .isEqualTo(solicitudProyecto.getTitulo());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
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
  public void findById_ReturnsSolicitudProyecto() throws Exception {
    Long idSolicitudProyecto = 1L;

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), SolicitudProyecto.class, idSolicitudProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyecto solicitudProyecto = response.getBody();
    Assertions.assertThat(solicitudProyecto.getId()).as("getId()").isEqualTo(idSolicitudProyecto);
    Assertions.assertThat(solicitudProyecto.getTitulo()).as("getTitulo()").isEqualTo("titulo-1");
  }

  /**
   * Función que devuelve un objeto SolicitudProyecto
   * 
   * @param solicitudProyectoId
   * @return el objeto SolicitudProyecto
   */
  private SolicitudProyecto generarSolicitudProyecto(Long solicitudProyectoId) {

    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder().id(solicitudProyectoId)
        .titulo("titulo-" + solicitudProyectoId).acronimo("acronimo-" + solicitudProyectoId).colaborativo(Boolean.TRUE)
        .tipoPresupuesto(TipoPresupuesto.GLOBAL).build();

    return solicitudProyecto;
  }

}
