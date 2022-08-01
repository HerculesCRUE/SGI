package org.crue.hercules.sgi.eer.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorOutput;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
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
 * Test de integracion de EmpresaEquipoEmprendedor.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmpresaEquipoEmprendedorIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/empresasequiposemprendedores";

  private HttpEntity<EmpresaEquipoEmprendedor> buildRequest(HttpHeaders headers, EmpresaEquipoEmprendedor entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "EER-EER-V", "EER-EER-C",
        "EER-EER-E", "EER-EER-B", "EER-EER-R", "AUTH")));

    HttpEntity<EmpresaEquipoEmprendedor> request = new HttpEntity<>(entity, headers);
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
  public void findById_ReturnsEmpresaEquipoEmprendedor() throws Exception {
    Long id = 1L;

    final ResponseEntity<EmpresaEquipoEmprendedorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), EmpresaEquipoEmprendedorOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    EmpresaEquipoEmprendedorOutput responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getMiembroEquipoRef()).as("getMiembroEquipoRef()")
        .isEqualTo("miembroEquipoRef 1");
  }

}
