package org.crue.hercules.sgi.eer.integration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadInput;
import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadOutput;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad.TipoAportacion;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de EmpresaComposicionSociedad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmpresaComposicionSociedadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/empresascomposicionessociedades";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/empresa.sql",
    "classpath:scripts/empresa_composicion_sociedad.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsEmpresaComposicionSociedad() throws Exception {
    String[] roles = { "EER-EER-E" };
    Long id = 1L;

    final ResponseEntity<EmpresaComposicionSociedadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), EmpresaComposicionSociedadOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    EmpresaComposicionSociedadOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getMiembroSociedadPersonaRef()).as("getMiembroSociedadPersonaRef()")
        .isEqualTo("miembroSociedadPersonaRef 1");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/empresa.sql",
      "classpath:scripts/empresa_composicion_sociedad.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ShouldReturnUpdatedEmpresaComposicionSociedadOutputList() throws Exception {
    String[] roles = { "EER-EER-E" };
    Long id = 1L;
    EmpresaComposicionSociedadInput input1 = buildMockEmpresaComposicionSociedadInput(1L);
    EmpresaComposicionSociedadInput input2 = buildMockEmpresaComposicionSociedadInput(2L);
    List<EmpresaComposicionSociedadInput> input = Arrays.asList(input1, input2);

    final ResponseEntity<List<EmpresaComposicionSociedadOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PATCH, buildRequest(null, input, roles),
        new ParameterizedTypeReference<List<EmpresaComposicionSociedadOutput>>() {
        }, id);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).hasSize(input.size());

    List<EmpresaComposicionSociedadOutput> output = response.getBody().stream()
        .sorted(Comparator.comparing(EmpresaComposicionSociedadOutput::getId)).collect(Collectors.toList());

    EmpresaComposicionSociedadOutput output1 = output.get(0);
    EmpresaComposicionSociedadOutput output2 = output.get(1);
    this.checkOutputAssertions(output1, input1);
    this.checkOutputAssertions(output2, input2);
  }

  private void checkOutputAssertions(EmpresaComposicionSociedadOutput output,
      EmpresaComposicionSociedadInput input) {
    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(input.getId());
    Assertions.assertThat(output.getFechaInicio()).isEqualTo(input.getFechaInicio());
    Assertions.assertThat(output.getFechaFin()).isEqualTo(input.getFechaFin());
    Assertions.assertThat(output.getCapitalSocial()).isEqualTo(input.getCapitalSocial());
    Assertions.assertThat(output.getMiembroSociedadPersonaRef()).isEqualTo(input.getMiembroSociedadPersonaRef());
    Assertions.assertThat(output.getTipoAportacion()).isEqualTo(input.getTipoAportacion());
  }

  private EmpresaComposicionSociedadInput buildMockEmpresaComposicionSociedadInput(Long id) {
    return EmpresaComposicionSociedadInput.builder()
        .empresaId(1L)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now().plus(40, ChronoUnit.DAYS))
        .id(id)
        .capitalSocial(new BigDecimal(6666))
        .miembroSociedadPersonaRef("test-ref-001")
        .participacion(new BigDecimal(50))
        .tipoAportacion(TipoAportacion.DINERARIA)
        .build();
  }
}
