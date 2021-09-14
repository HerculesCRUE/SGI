package org.crue.hercules.sgi.csp.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.RequisitoEquipoController;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoCategoriaProfesionalInput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoNivelAcademicoInput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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

  private <T> HttpEntity<T> buildRequest(T entity) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CON-C", "CSP-CON-V", "CSP-CON-E", "CSP-CON-INV-V")));

    HttpEntity<T> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsRequisitoEquipo() throws Exception {

    // given: new RequisitoEquipo
    RequisitoEquipo newRequisitoEquipo = generarMockRequisitoEquipo(null);

    // when: create RequisitoEquipo
    final ResponseEntity<RequisitoEquipo> response = restTemplate.exchange(RequisitoEquipoController.MAPPING,
        HttpMethod.POST, buildRequest(newRequisitoEquipo), RequisitoEquipo.class);

    // then: new RequisitoEquipo is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    RequisitoEquipo responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(newRequisitoEquipo.getId());
    Assertions.assertThat(responseData.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(newRequisitoEquipo.getEdadMaxima());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsRequisitoEquipo() throws Exception {
    Long idConvocatoriaRequisitoEquipo = 1L;
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);

    final ResponseEntity<RequisitoEquipo> response = restTemplate.exchange(
        RequisitoEquipoController.MAPPING + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(requisitoEquipo),
        RequisitoEquipo.class, idConvocatoriaRequisitoEquipo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequisitoEquipo requisitoEquipoActualizado = response.getBody();
    Assertions.assertThat(requisitoEquipoActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(requisitoEquipoActualizado.getId()).as("getId()").isEqualTo(requisitoEquipo.getId());
    Assertions.assertThat(requisitoEquipoActualizado.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(requisitoEquipo.getEdadMaxima());
    Assertions.assertThat(requisitoEquipo.getSexoRef()).as("getSexoRef()").isEqualTo(requisitoEquipo.getSexoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findRequisitoEquipoConvocatoria_ReturnsRequisitoEquipo() throws Exception {
    Long idConvocatoria = 1L;

    final ResponseEntity<RequisitoEquipo> response = restTemplate.exchange(
        RequisitoEquipoController.MAPPING + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null),
        RequisitoEquipo.class, idConvocatoria);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequisitoEquipo requisitoEquipo = response.getBody();
    Assertions.assertThat(requisitoEquipo.getId()).as("getId()").isEqualTo(idConvocatoria);
    Assertions.assertThat(requisitoEquipo.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(requisitoEquipo.getEdadMaxima());
    Assertions.assertThat(requisitoEquipo.getSexoRef()).as("getSexoRef()").isEqualTo(requisitoEquipo.getSexoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql",
    "classpath:scripts/requisitoequipo_nivelacademico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void testFindNivelesAcademicos() throws Exception {
    Long id = 1L;

    final ResponseEntity<List<RequisitoEquipoNivelAcademicoOutput>> response = restTemplate.exchange(
        RequisitoEquipoController.MAPPING + RequisitoEquipoController.PATH_NIVELES, HttpMethod.GET, buildRequest(null),
        new ParameterizedTypeReference<List<RequisitoEquipoNivelAcademicoOutput>>() {
        }, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<RequisitoEquipoNivelAcademicoOutput> body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.size()).isEqualTo(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql",
    "classpath:scripts/requisitoequipo_nivelacademico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void testUpdateNivelesAcademicos() throws Exception {
    Long id = 1L;

    List<RequisitoEquipoNivelAcademicoInput> requestBody = new ArrayList<RequisitoEquipoNivelAcademicoInput>();
    for (int i = 0; i < 10; i++) {
      requestBody.add(RequisitoEquipoNivelAcademicoInput.builder().requisitoEquipoId(id)
          .nivelAcademicoRef("nivelAcademicoRef" + i).build());
    }

    final ResponseEntity<List<RequisitoEquipoNivelAcademicoOutput>> response = restTemplate.exchange(
        RequisitoEquipoController.MAPPING + RequisitoEquipoController.PATH_NIVELES, HttpMethod.PATCH,
        buildRequest(requestBody), new ParameterizedTypeReference<List<RequisitoEquipoNivelAcademicoOutput>>() {
        }, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<RequisitoEquipoNivelAcademicoOutput> body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.size()).isEqualTo(10);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql",
    "classpath:scripts/requisitoequipo_categoriaprofesional.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void testFindCategoriasProfesionales() throws Exception {
    Long id = 1L;

    final ResponseEntity<List<RequisitoEquipoCategoriaProfesionalOutput>> response = restTemplate.exchange(
        RequisitoEquipoController.MAPPING + RequisitoEquipoController.PATH_CATEGORIAS, HttpMethod.GET,
        buildRequest(null), new ParameterizedTypeReference<List<RequisitoEquipoCategoriaProfesionalOutput>>() {
        }, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<RequisitoEquipoCategoriaProfesionalOutput> body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.size()).isEqualTo(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql",
    "classpath:scripts/requisitoequipo_categoriaprofesional.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void testUpdateCategoriasProfesionales() throws Exception {
    Long id = 1L;

    List<RequisitoEquipoCategoriaProfesionalInput> requestBody = new ArrayList<RequisitoEquipoCategoriaProfesionalInput>();
    for (int i = 0; i < 10; i++) {
      requestBody.add(RequisitoEquipoCategoriaProfesionalInput.builder().requisitoEquipoId(id)
          .categoriaProfesionalRef("categoriaProfesionalRef" + i).build());
    }

    final ResponseEntity<List<RequisitoEquipoCategoriaProfesionalOutput>> response = restTemplate.exchange(
        RequisitoEquipoController.MAPPING + RequisitoEquipoController.PATH_CATEGORIAS, HttpMethod.PATCH,
        buildRequest(requestBody), new ParameterizedTypeReference<List<RequisitoEquipoCategoriaProfesionalOutput>>() {
        }, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<RequisitoEquipoCategoriaProfesionalOutput> body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.size()).isEqualTo(10);
  }

  /**
   * Función que devuelve un objeto RequisitoEquipo
   * 
   * @param id id del RequisitoEquipo
   * @return el objeto RequisitoEquipo
   */
  private RequisitoEquipo generarMockRequisitoEquipo(Long id) {
    RequisitoEquipo requisitoEquipo = new RequisitoEquipo();
    requisitoEquipo.setId(id == null ? 1 : id);
    requisitoEquipo.setEdadMaxima(50);
    requisitoEquipo.setSexoRef("sexo-ref");
    return requisitoEquipo;
  }

}
