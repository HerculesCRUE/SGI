package org.crue.hercules.sgi.eer.integration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadInput;
import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadOutput;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad.TipoAdministracion;
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
 * Test de integracion de EmpresaAdministracionSociedad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmpresaAdministracionSociedadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/empresasadministracionessociedades";

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
    "classpath:scripts/empresa_administracion_sociedad.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsEmpresaAdministracionSociedad() throws Exception {
    String[] roles = { "EER-EER-V", "EER-EER-C",
        "EER-EER-E", "EER-EER-B", "EER-EER-R", "AUTH" };
    Long id = 1L;

    final ResponseEntity<EmpresaAdministracionSociedadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), EmpresaAdministracionSociedadOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    EmpresaAdministracionSociedadOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getMiembroEquipoAdministracionRef()).as("getMiembroEquipoAdministracionRef()")
        .isEqualTo("miembroEquipoAdministracionRef 1");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/empresa.sql",
      "classpath:scripts/empresa_administracion_sociedad.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ShouldReturnUpdatedEmpresaAdministracionSociedadOutputList() throws Exception {
    String[] roles = { "EER-EER-E" };
    Long id = 1L;
    EmpresaAdministracionSociedadInput input1 = buildMockEmpresaAdministracionSociedadInput(1L);
    EmpresaAdministracionSociedadInput input2 = buildMockEmpresaAdministracionSociedadInput(2L);
    List<EmpresaAdministracionSociedadInput> input = Arrays.asList(input1, input2);

    final ResponseEntity<List<EmpresaAdministracionSociedadOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PATCH, buildRequest(null, input, roles),
        new ParameterizedTypeReference<List<EmpresaAdministracionSociedadOutput>>() {
        }, id);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).hasSize(input.size());

    List<EmpresaAdministracionSociedadOutput> output = response.getBody().stream()
        .sorted(Comparator.comparing(EmpresaAdministracionSociedadOutput::getId)).collect(Collectors.toList());

    EmpresaAdministracionSociedadOutput output1 = output.get(0);
    EmpresaAdministracionSociedadOutput output2 = output.get(1);
    this.checkOutputAssertions(output1, input1);
    this.checkOutputAssertions(output2, input2);
  }

  private void checkOutputAssertions(EmpresaAdministracionSociedadOutput output,
      EmpresaAdministracionSociedadInput input) {
    Assertions.assertThat(output).isNotNull();
    Assertions.assertThat(output.getId()).isEqualTo(input.getId());
    Assertions.assertThat(output.getFechaInicio()).isEqualTo(input.getFechaInicio());
    Assertions.assertThat(output.getFechaFin()).isEqualTo(input.getFechaFin());
    Assertions.assertThat(output.getMiembroEquipoAdministracionRef())
        .isEqualTo(input.getMiembroEquipoAdministracionRef());
    Assertions.assertThat(output.getTipoAdministracion()).isEqualTo(input.getTipoAdministracion());
  }

  private EmpresaAdministracionSociedadInput buildMockEmpresaAdministracionSociedadInput(Long id) {
    return EmpresaAdministracionSociedadInput.builder()
        .empresaId(1L)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now().plus(40, ChronoUnit.DAYS))
        .id(id)
        .miembroEquipoAdministracionRef("test0001")
        .tipoAdministracion(TipoAdministracion.ADMINISTRADOR_MANCOMUNADO)
        .build();
  }

}
