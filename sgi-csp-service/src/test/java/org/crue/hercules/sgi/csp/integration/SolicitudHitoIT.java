package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
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
 * Test de integracion de SolicitudHito.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudHitoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudhitos";

  private HttpEntity<SolicitudHito> buildRequest(HttpHeaders headers, SolicitudHito entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudHito> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudHito() throws Exception {
    SolicitudHito solicitudHito = generarSolicitudHito(null, 1L, 1L);

    final ResponseEntity<SolicitudHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudHito), SolicitudHito.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudHito solicitudHitoCreado = response.getBody();
    Assertions.assertThat(solicitudHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudHitoCreado.getSolicitudId()).as("getSolicitudId()")
        .isEqualTo(solicitudHito.getSolicitudId());
    Assertions.assertThat(solicitudHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(solicitudHito.getTipoHito().getId());
    Assertions.assertThat(solicitudHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(solicitudHito.getComentario());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudHito() throws Exception {
    Long idSolicitudHito = 1L;
    SolicitudHito solicitudHito = generarSolicitudHito(1L, 1L, 1L);
    solicitudHito.setComentario("comentario-modificado");

    final ResponseEntity<SolicitudHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitudHito), SolicitudHito.class, idSolicitudHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudHito solicitudHitoActualizado = response.getBody();
    Assertions.assertThat(solicitudHitoActualizado.getId()).as("getId()").isEqualTo(solicitudHito.getId());
    Assertions.assertThat(solicitudHitoActualizado.getComentario()).as("getComentario()")
        .isEqualTo(solicitudHito.getComentario());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing SolicitudHito to be deleted
    Long id = 1L;

    // when: delete SolicitudHito
    final ResponseEntity<SolicitudHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), SolicitudHito.class, id);

    // then: SolicitudHito deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudHito() throws Exception {
    Long idSolicitudHito = 1L;

    final ResponseEntity<SolicitudHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), SolicitudHito.class, idSolicitudHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudHito solicitudHito = response.getBody();
    Assertions.assertThat(solicitudHito.getId()).as("getId()").isEqualTo(idSolicitudHito);
    Assertions.assertThat(solicitudHito.getSolicitudId()).as("getSolicitudId()").isEqualTo(1);
    Assertions.assertThat(solicitudHito.getComentario()).as("getComentario()").isEqualTo("comentario-001");
  }

  /**
   * Función que devuelve un objeto SolicitudHito
   * 
   * @param solicitudHitoId
   * @param solicitudId
   * @param tipoDocumentoId
   * @return el objeto SolicitudHito
   */
  private SolicitudHito generarSolicitudHito(Long solicitudHitoId, Long solicitudId, Long tipoDocumentoId) {

    SolicitudHito solicitudHito = SolicitudHito.builder().id(solicitudHitoId).solicitudId(solicitudId)
        .comentario("comentario-" + solicitudHitoId).fecha(Instant.now()).generaAviso(Boolean.TRUE)
        .tipoHito(TipoHito.builder().id(tipoDocumentoId).build()).build();

    return solicitudHito;
  }

}
