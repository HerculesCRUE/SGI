package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
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
 * Test de integracion de DocumentoRequeridoSolicitud.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class DocumentoRequeridoSolicitudIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/documentorequiridosolicitudes";

  private HttpEntity<DocumentoRequeridoSolicitud> buildRequest(HttpHeaders headers, DocumentoRequeridoSolicitud entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CON-C", "CSP-CON-E", "CSP-CON-B", "CSP-CON-V", "AUTH")));

    HttpEntity<DocumentoRequeridoSolicitud> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsDocumentoRequeridoSolicitud() throws Exception {

    // given: new DocumentoRequeridoSolicitud
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(null, 1L, 1L);

    // when: create DocumentoRequeridoSolicitud
    final ResponseEntity<DocumentoRequeridoSolicitud> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, documentoRequeridoSolicitud), DocumentoRequeridoSolicitud.class);

    // then: new DocumentoRequeridoSolicitud is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    DocumentoRequeridoSolicitud responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConfiguracionSolicitudId()).as("getConfiguracionSolicitudId()")
        .isEqualTo(documentoRequeridoSolicitud.getConfiguracionSolicitudId());
    Assertions.assertThat(responseData.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(documentoRequeridoSolicitud.getTipoDocumento().getId());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(documentoRequeridoSolicitud.getObservaciones());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsDocumentoRequeridoSolicitud() throws Exception {

    // given: existing DocumentoRequeridoSolicitud to be updated
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L, 1L);
    documentoRequeridoSolicitud.getTipoDocumento().setId(2L);
    documentoRequeridoSolicitud.setObservaciones("obbservaciones-modificadas");

    // when: update DocumentoRequeridoSolicitud
    final ResponseEntity<DocumentoRequeridoSolicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, documentoRequeridoSolicitud),
        DocumentoRequeridoSolicitud.class, documentoRequeridoSolicitud.getId());

    // then: DocumentoRequeridoSolicitud is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    DocumentoRequeridoSolicitud responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(documentoRequeridoSolicitud.getId());
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(documentoRequeridoSolicitud.getId());
    Assertions.assertThat(responseData.getConfiguracionSolicitudId()).as("getConfiguracionSolicitudId()")
        .isEqualTo(documentoRequeridoSolicitud.getConfiguracionSolicitudId());
    Assertions.assertThat(responseData.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(documentoRequeridoSolicitud.getTipoDocumento().getId());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(documentoRequeridoSolicitud.getObservaciones());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing DocumentoRequeridoSolicitud to be deleted
    Long documentoRequeridoSolicitudId = 1L;

    // when: delete DocumentoRequeridoSolicitud
    final ResponseEntity<DocumentoRequeridoSolicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        DocumentoRequeridoSolicitud.class, documentoRequeridoSolicitudId);

    // then: DocumentoRequeridoSolicitud deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsDocumentoRequeridoSolicitud() throws Exception {
    Long documentoRequeridoSolicitudId = 2L;

    final ResponseEntity<DocumentoRequeridoSolicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        DocumentoRequeridoSolicitud.class, documentoRequeridoSolicitudId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    DocumentoRequeridoSolicitud responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(documentoRequeridoSolicitudId);
    Assertions.assertThat(responseData.getConfiguracionSolicitudId()).as("getConfiguracionSolicitudId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getTipoDocumento().getId()).as("getTipoDocumento().getId()").isEqualTo(2L);
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo("Observaciones documento 2");
  }

  /**
   * Función que devuelve un objeto DocumentoRequeridoSolicitud
   * 
   * @param id id del DocumentoRequeridoSolicitud
   * @return el objeto DocumentoRequeridoSolicitud
   */
  private DocumentoRequeridoSolicitud generarMockDocumentoRequeridoSolicitud(Long id, Long configuracionSolicitudId,
      Long tipoDocumentoId) {
    // @formatter:off
    return DocumentoRequeridoSolicitud.builder()
        .id(id)
        .configuracionSolicitudId(configuracionSolicitudId)
        .tipoDocumento(TipoDocumento.builder().id(tipoDocumentoId).build())
        .observaciones("observacionesDocumentoRequeridoSolicitud-" + id)
        .build();
    // @formatter:on
  }
}
