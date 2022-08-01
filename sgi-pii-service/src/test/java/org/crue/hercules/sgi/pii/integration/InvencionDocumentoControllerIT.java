package org.crue.hercules.sgi.pii.integration;

import java.util.Collections;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.InvencionDocumentoInput;
import org.crue.hercules.sgi.pii.dto.InvencionDocumentoOutput;
import org.crue.hercules.sgi.pii.model.InvencionDocumento;
import org.crue.hercules.sgi.pii.repository.InvencionDocumentoRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InvencionDocumentoControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/invenciondocumentos";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private InvencionDocumentoRepository invencionDocumentoRepository;

  private HttpEntity<InvencionDocumentoInput> buildRequest(HttpHeaders headers,
      InvencionDocumentoInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<InvencionDocumentoInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/invencion_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnInvencionDocumentoInput() throws Exception {
    String[] roles = { "PII-INV-C", "PII-INV-E" };

    InvencionDocumentoInput toCreate = this.buildMockInvencionDocumentoInput();
    Long lastId = this.invencionDocumentoRepository.count();

    final ResponseEntity<InvencionDocumentoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, roles), InvencionDocumentoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    InvencionDocumentoOutput created = response.getBody();

    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).as("id").isEqualTo(lastId + 1);
    Assertions.assertThat(created.getDocumentoRef()).as("getDocumentoRef()").isEqualTo(toCreate.getDocumentoRef());
    Assertions.assertThat(created.getNombre()).as("getNombre()").isEqualTo(toCreate.getNombre());
    Assertions.assertThat(created.getInvencionId()).as("getInvencionId()").isEqualTo(toCreate.getInvencionId());
    Assertions.assertThat(created.getFechaAnadido()).isNotNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/invencion_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnUpdatedInvencionDocumentoOutput() throws Exception {
    String[] roles = { "PII-INV-E" };

    Long toUpdateId = 5L;
    Optional<InvencionDocumento> informeDocumento5 = this.invencionDocumentoRepository.findById(toUpdateId);

    final String updatedSuffix = "-updated";

    InvencionDocumentoInput toUpdate = this.modelMapper.map(informeDocumento5.get(), InvencionDocumentoInput.class);
    toUpdate.setNombre(informeDocumento5.get().getNombre() + updatedSuffix);
    toUpdate.setDocumentoRef(informeDocumento5.get().getDocumentoRef() + updatedSuffix);

    final ResponseEntity<InvencionDocumentoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, roles), InvencionDocumentoOutput.class, toUpdateId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    InvencionDocumentoOutput updated = response.getBody();
    InvencionDocumento updatedFromDb = this.invencionDocumentoRepository.findById(toUpdateId).get();

    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).as("id").isEqualTo(toUpdateId);
    Assertions.assertThat(updated.getDocumentoRef()).as("getDocumentoRef()").isEqualTo(updatedFromDb.getDocumentoRef());
    Assertions.assertThat(updated.getNombre()).as("getNombre()").isEqualTo(updatedFromDb.getNombre());
    Assertions.assertThat(updated.getInvencionId()).as("getInvencionId()").isEqualTo(updatedFromDb.getInvencionId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql",
      "classpath:scripts/invencion_documento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnStatusCodeNOT_CONTENT() throws Exception {
    String[] roles = { "PII-INV-E", "PII-INV-C" };

    Long toDeleteId = 3L;

    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, roles), Void.class, toDeleteId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(this.invencionDocumentoRepository.findById(toDeleteId)).isNotPresent();
  }

  private InvencionDocumentoInput buildMockInvencionDocumentoInput() {
    return InvencionDocumentoInput.builder()
        .documentoRef("DOC-06")
        .invencionId(1L)
        .nombre("documento mock 01")
        .build();
  }
}