package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
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
 * Test de integracion de RequisitoEquipo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })

public class RequisitoEquipoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoria-requisitoequipos";

  private HttpEntity<RequisitoEquipo> buildRequest(HttpHeaders headers, RequisitoEquipo entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CON-C", "CSP-CON-V", "CSP-CON-E", "CSP-CON-INV-V")));

    HttpEntity<RequisitoEquipo> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsRequisitoEquipo() throws Exception {

    // given: new RequisitoEquipo
    RequisitoEquipo newRequisitoEquipo = generarMockRequisitoEquipo(null);

    // when: create RequisitoEquipo
    final ResponseEntity<RequisitoEquipo> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newRequisitoEquipo), RequisitoEquipo.class);

    // then: new RequisitoEquipo is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    RequisitoEquipo responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newRequisitoEquipo.getConvocatoriaId());
    Assertions.assertThat(responseData.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(newRequisitoEquipo.getEdadMaxima());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsRequisitoEquipo() throws Exception {
    Long idConvocatoriaRequisitoEquipo = 1L;
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);

    final ResponseEntity<RequisitoEquipo> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, requisitoEquipo), RequisitoEquipo.class, idConvocatoriaRequisitoEquipo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequisitoEquipo requisitoEquipoActualizado = response.getBody();
    Assertions.assertThat(requisitoEquipoActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(requisitoEquipoActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(requisitoEquipo.getConvocatoriaId());
    Assertions.assertThat(requisitoEquipoActualizado.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(requisitoEquipo.getEdadMaxima());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findRequisitoEquipoConvocatoria_ReturnsRequisitoEquipo() throws Exception {
    Long idConvocatoria = 1L;

    final ResponseEntity<RequisitoEquipo> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), RequisitoEquipo.class, idConvocatoria);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequisitoEquipo requisitoEquipo = response.getBody();
    Assertions.assertThat(requisitoEquipo.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(idConvocatoria);
    Assertions.assertThat(requisitoEquipo.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(requisitoEquipo.getEdadMaxima());

  }

  /**
   * Función que devuelve un objeto RequisitoEquipo
   * 
   * @param id id del RequisitoEquipo
   * @return el objeto RequisitoEquipo
   */
  private RequisitoEquipo generarMockRequisitoEquipo(Long id) {
    RequisitoEquipo requisitoEquipo = new RequisitoEquipo();
    requisitoEquipo.setConvocatoriaId(id == null ? 1 : id);
    requisitoEquipo.setEdadMaxima(50);
    return requisitoEquipo;
  }

}
