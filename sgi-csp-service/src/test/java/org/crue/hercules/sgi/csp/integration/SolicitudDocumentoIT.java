package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
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
 * Test de integracion de SolicitudDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SolicitudDocumentoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicituddocumentos";

  private HttpEntity<SolicitudDocumento> buildRequest(HttpHeaders headers, SolicitudDocumento entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudDocumento> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudDocumento() throws Exception {

    // given: new SolicitudDocumento
    SolicitudDocumento newSolicitudDocumento = generarSolicitudDocumento(null, 1L, 1L);

    // when: create SolicitudDocumento
    final ResponseEntity<SolicitudDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newSolicitudDocumento), SolicitudDocumento.class);

    // then: new SolicitudDocumento is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    SolicitudDocumento responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getSolicitudId()).as("getSolicitudId()")
        .isEqualTo(newSolicitudDocumento.getSolicitudId());
    Assertions.assertThat(responseData.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(newSolicitudDocumento.getDocumentoRef());
    Assertions.assertThat(responseData.getComentario()).as("getComentario()")
        .isEqualTo(newSolicitudDocumento.getComentario());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(newSolicitudDocumento.getNombre());
    Assertions.assertThat(responseData.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(newSolicitudDocumento.getTipoDocumento().getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudDocumento() throws Exception {

    // given: Existing SolicitudDocumento to be updated
    SolicitudDocumento solicitudDocumentoExistente = generarSolicitudDocumento(1L, 1L, 1L);
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);
    solicitudDocumento.setComentario("comentario-modificados");

    // when: update SolicitudDocumento
    final ResponseEntity<SolicitudDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitudDocumento), SolicitudDocumento.class,
        solicitudDocumentoExistente.getId());

    // then: SolicitudDocumento is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudDocumento responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getSolicitudId()).as("getSolicitudId()")
        .isEqualTo(solicitudDocumentoExistente.getSolicitudId());
    Assertions.assertThat(responseData.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(solicitudDocumentoExistente.getDocumentoRef());
    Assertions.assertThat(responseData.getComentario()).as("getComentario()").isEqualTo("comentario-modificados");
    Assertions.assertThat(responseData.getNombre()).as("getNombre()")
        .isEqualTo(solicitudDocumentoExistente.getNombre());
    Assertions.assertThat(responseData.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(solicitudDocumentoExistente.getTipoDocumento().getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing SolicitudDocumento to be deleted
    Long id = 1L;

    // when: delete SolicitudDocumento
    final ResponseEntity<SolicitudDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), SolicitudDocumento.class, id);

    // then: SolicitudDocumento deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudDocumento() throws Exception {
    Long id = 1L;

    final ResponseEntity<SolicitudDocumento> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), SolicitudDocumento.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudDocumento responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getSolicitudId()).as("getSolicitudId()").isEqualTo(1L);
  }

  /**
   * Función que devuelve un objeto SolicitudDocumento
   * 
   * @param solicitudDocumentoId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto SolicitudDocumento
   */
  private SolicitudDocumento generarSolicitudDocumento(Long solicitudDocumentoId, Long solicitudId,
      Long tipoDocumentoId) {

    SolicitudDocumento solicitudDocumento = SolicitudDocumento.builder().id(solicitudDocumentoId)
        .solicitudId(solicitudId).comentario("comentarios-" + solicitudDocumentoId)
        .documentoRef("documentoRef-" + solicitudDocumentoId).nombre("nombre-" + solicitudDocumentoId)
        .tipoDocumento(TipoDocumento.builder().id(tipoDocumentoId).build()).build();

    return solicitudDocumento;
  }
}
