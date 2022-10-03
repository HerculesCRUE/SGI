package org.crue.hercules.sgi.eer.integration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorInput;
import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorOutput;
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
 * Test de integracion de EmpresaEquipoEmprendedor.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmpresaEquipoEmprendedorIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/empresasequiposemprendedores";

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
    "classpath:scripts/empresa_equipo_emprendedor.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsEmpresaEquipoEmprendedor() throws Exception {
    String[] roles = { "EER-EER-E" };
    Long id = 1L;

    final ResponseEntity<EmpresaEquipoEmprendedorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), EmpresaEquipoEmprendedorOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    EmpresaEquipoEmprendedorOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getMiembroEquipoRef()).as("getMiembroEquipoRef()")
        .isEqualTo("miembroEquipoRef 1");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/empresa.sql",
    "classpath:scripts/empresa_equipo_emprendedor.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsEmpresaEquipoEmprendedor() throws Exception {
    String[] roles = { "EER-EER-E" };
    Long empresaId = 1L;
    EmpresaEquipoEmprendedorInput input = this.buildMockEmpresaEquipoEmprendedorInput(1L);

    final ResponseEntity<List<EmpresaEquipoEmprendedorOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PATCH, buildRequest(null, Arrays.asList(input), roles),
        new ParameterizedTypeReference<List<EmpresaEquipoEmprendedorOutput>>() {
        }, empresaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<EmpresaEquipoEmprendedorOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(input.getId());
    Assertions.assertThat(responseData.get(0).getMiembroEquipoRef()).isEqualTo(input.getMiembroEquipoRef());
  }

  private EmpresaEquipoEmprendedorInput buildMockEmpresaEquipoEmprendedorInput(Long id) {

    return EmpresaEquipoEmprendedorInput.builder()
        .empresaId(1L)
        .miembroEquipoRef("test-0001")
        .id(id)
        .build();
  }
}
