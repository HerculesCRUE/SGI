package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoOutput;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
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
 * Test de integracion de SolicitudHito.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudHitoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudhitos";

  private HttpEntity<SolicitudHitoInput> buildRequest(HttpHeaders headers, SolicitudHitoInput entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudHitoInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsSolicitudHito() throws Exception {
    SolicitudHitoInput solicitudHito = generarSolicitudHito(1L, 1L);

    final ResponseEntity<SolicitudHitoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudHito), SolicitudHitoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudHitoOutput solicitudHitoCreado = response.getBody();
    Assertions.assertThat(solicitudHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudHitoCreado.getSolicitudId()).as("getSolicitudId()")
        .isEqualTo(solicitudHito.getSolicitudId());
    Assertions.assertThat(solicitudHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(solicitudHito.getTipoHitoId());
    Assertions.assertThat(solicitudHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(solicitudHito.getComentario());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsSolicitudHito() throws Exception {
    Long idSolicitudHito = 1L;
    SolicitudHitoInput solicitudHito = generarSolicitudHito(1L, 1L);
    solicitudHito.setComentario("comentario-modificado");

    final ResponseEntity<SolicitudHitoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitudHito), SolicitudHitoOutput.class, idSolicitudHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudHitoOutput solicitudHitoActualizado = response.getBody();
    Assertions.assertThat(solicitudHitoActualizado.getId()).as("getId()").isEqualTo(idSolicitudHito);
    Assertions.assertThat(solicitudHitoActualizado.getComentario()).as("getComentario()")
        .isEqualTo(solicitudHito.getComentario());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing SolicitudHito to be deleted
    Long id = 1L;

    // when: delete SolicitudHito
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, id);

    // then: SolicitudHito deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitudHito() throws Exception {
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
  private SolicitudHitoInput generarSolicitudHito(Long solicitudId, Long tipoDocumentoId) {

    SolicitudHitoInput solicitudHito = SolicitudHitoInput.builder().solicitudId(solicitudId)
        .comentario("comentario").fecha(Instant.now())
        .tipoHitoId(tipoDocumentoId).build();

    return solicitudHito;
  }

}
