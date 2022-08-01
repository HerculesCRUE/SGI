package org.crue.hercules.sgi.eer.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.controller.EmpresaDocumentoController;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoInput;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoOutput;
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
 * Test de integracion de EmpresaDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmpresaDocumentoIT extends BaseIT {
  private static final String REQUEST_MAPPING = EmpresaDocumentoController.REQUEST_MAPPING;
  private static final String PATH_ID = EmpresaDocumentoController.PATH_ID;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/empresa.sql",
      "classpath:scripts/tipo_documento.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsEmpresaDocumento() throws Exception {
    String roles = "EER-EER-E";
    // given: new EmpresaDocumento
    EmpresaDocumentoInput data = generateEmpresaDocumentoInputMock();

    // when: create EmpresaDocumento
    final ResponseEntity<EmpresaDocumentoOutput> response = restTemplate.exchange(REQUEST_MAPPING, HttpMethod.POST,
        buildRequest(null, data, roles), EmpresaDocumentoOutput.class);

    // then: new EmpresaDocumento is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    EmpresaDocumentoOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(data.getNombre());
    Assertions.assertThat(responseData.getDocumentoRef()).as("getDocumentoRef()").isEqualTo(data.getDocumentoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/empresa.sql",
      "classpath:scripts/tipo_documento.sql",
      "classpath:scripts/empresa_documento.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsEmpresaDocumento() throws Exception {
    // given: existing EmpresaDocumento to be updated
    String roles = "EER-EER-E";
    Long empresaDocumentoId = 1L;
    EmpresaDocumentoInput data = generateEmpresaDocumentoInputMock();
    data.setNombre("nombre-actualizado");

    // when: update EmpresaDocumento
    final ResponseEntity<EmpresaDocumentoOutput> response = restTemplate.exchange(
        REQUEST_MAPPING + PATH_ID, HttpMethod.PUT, buildRequest(null, data, roles),
        EmpresaDocumentoOutput.class, empresaDocumentoId);

    // then: EmpresaDocumento is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    EmpresaDocumentoOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(data.getNombre());
    Assertions.assertThat(responseData.getDocumentoRef()).as("getDocumentoRef()").isEqualTo(data.getDocumentoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/empresa.sql",
      "classpath:scripts/tipo_documento.sql",
      "classpath:scripts/empresa_documento.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Returns204() throws Exception {
    // given: existing EmpresaDocumento to be deleted
    String roles = "EER-EER-E";
    Long empresaDocumentoId = 1L;

    // when: delete EmpresaDocumento
    final ResponseEntity<Void> response = restTemplate.exchange(
        REQUEST_MAPPING + PATH_ID, HttpMethod.DELETE, buildRequest(null, null, roles),
        Void.class, empresaDocumentoId);

    // then: EmpresaDocumento is deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/empresa.sql",
      "classpath:scripts/tipo_documento.sql",
      "classpath:scripts/empresa_documento.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsEmpresaDocumento() throws Exception {
    Long empresaDocumentoId = 1L;
    String roles = "EER-EER-V";

    final ResponseEntity<EmpresaDocumentoOutput> response = restTemplate.exchange(
        REQUEST_MAPPING + PATH_ID, HttpMethod.GET, buildRequest(null, null, roles),
        EmpresaDocumentoOutput.class, empresaDocumentoId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    EmpresaDocumentoOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo("Documento de procedimiento 1");
    Assertions.assertThat(responseData.getDocumentoRef()).as("getDocumentoRef()").isEqualTo("documento-ref-1");
  }

  private EmpresaDocumentoInput generateEmpresaDocumentoInputMock() {
    return generateEmpresaDocumentoInputMock(1L,
        1L, "Documento", "Comentario", "documento-ref-1");
  }

  private EmpresaDocumentoInput generateEmpresaDocumentoInputMock(Long empresaId,
      Long tipoDocumentoId, String nombre, String comentarios, String documentoRef) {
    return EmpresaDocumentoInput.builder()
        .comentarios(comentarios)
        .documentoRef(documentoRef)
        .empresaId(empresaId)
        .nombre(nombre)
        .tipoDocumentoId(tipoDocumentoId)
        .build();
  }
}
