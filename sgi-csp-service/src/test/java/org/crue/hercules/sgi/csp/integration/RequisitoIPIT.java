package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
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
 * Test de integracion de RequisitoIP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequisitoIPIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoria-requisitoips";

  private HttpEntity<RequisitoIP> buildRequest(HttpHeaders headers, RequisitoIP entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "AUTH", "CSP-CON-C", "CSP-CON-E", "CSP-CON-V", "CSP-CON-INV-V")));

    HttpEntity<RequisitoIP> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsRequisitoIP() throws Exception {

    // given: new RequisitoIP
    RequisitoIP newRequisitoIP = generarMockRequisitoIP(null);

    // when: create RequisitoIP
    final ResponseEntity<RequisitoIP> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newRequisitoIP), RequisitoIP.class);

    // then: new RequisitoIP is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    RequisitoIP responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newRequisitoIP.getConvocatoriaId());
    Assertions.assertThat(responseData.getSexo()).as("getSexo()").isEqualTo(newRequisitoIP.getSexo());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsRequisitoIP() throws Exception {
    Long idRequisitoIP = 1L;
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);

    final ResponseEntity<RequisitoIP> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, requisitoIP), RequisitoIP.class, idRequisitoIP);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequisitoIP requisitoIPActualizado = response.getBody();
    Assertions.assertThat(requisitoIPActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(requisitoIPActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(requisitoIP.getConvocatoriaId());
    Assertions.assertThat(requisitoIPActualizado.getSexo()).as("getSexo()").isEqualTo(requisitoIP.getSexo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findRequisitoIPConvocatoria_ReturnsRequisitoIP() throws Exception {
    Long idConvocatoria = 1L;

    final ResponseEntity<RequisitoIP> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), RequisitoIP.class, idConvocatoria);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequisitoIP requisitoIP = response.getBody();
    Assertions.assertThat(requisitoIP.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(idConvocatoria);
    Assertions.assertThat(requisitoIP.getSexo()).as("getSexo()").isEqualTo(requisitoIP.getSexo());

  }

  /**
   * Función que devuelve un objeto RequisitoIP
   * 
   * @param id id del RequisitoIP
   * @return el objeto RequisitoIP
   */
  private RequisitoIP generarMockRequisitoIP(Long id) {
    RequisitoIP requisitoIP = new RequisitoIP();
    requisitoIP.setId(id);
    requisitoIP.setConvocatoriaId(id != null ? id : 1L);
    requisitoIP.setSexo("Hombre");
    return requisitoIP;
  }

}
