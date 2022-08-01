package org.crue.hercules.sgi.csp.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.RequisitoIPController;
import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalInput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoInput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
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
 * Test de integracion de RequisitoIP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RequisitoIPIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";

  private <T> HttpEntity<T> buildRequest(T entity) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CON-C", "CSP-CON-E", "CSP-CON-V", "CSP-CON-INV-V")));

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
  void create_ReturnsRequisitoIP() throws Exception {

    // given: new RequisitoIP
    RequisitoIP newRequisitoIP = generarMockRequisitoIP(null);

    // when: create RequisitoIP
    final ResponseEntity<RequisitoIP> response = restTemplate.exchange(RequisitoIPController.MAPPING, HttpMethod.POST,
        buildRequest(newRequisitoIP), RequisitoIP.class);

    // then: new RequisitoIP is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    RequisitoIP responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(newRequisitoIP.getId());
    Assertions.assertThat(responseData.getSexoRef()).as("getSexoRef()").isEqualTo(newRequisitoIP.getSexoRef());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_ip.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsRequisitoIP() throws Exception {
    Long idRequisitoIP = 1L;
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);

    final ResponseEntity<RequisitoIP> response = restTemplate.exchange(
        RequisitoIPController.MAPPING + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(requisitoIP), RequisitoIP.class,
        idRequisitoIP);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequisitoIP requisitoIPActualizado = response.getBody();
    Assertions.assertThat(requisitoIPActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(requisitoIPActualizado.getId()).as("getId()").isEqualTo(requisitoIP.getId());
    Assertions.assertThat(requisitoIPActualizado.getSexoRef()).as("getSexoRef()").isEqualTo(requisitoIP.getSexoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_ip.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findRequisitoIPConvocatoria_ReturnsRequisitoIP() throws Exception {
    Long idConvocatoria = 1L;

    final ResponseEntity<RequisitoIP> response = restTemplate.exchange(
        RequisitoIPController.MAPPING + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null), RequisitoIP.class,
        idConvocatoria);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RequisitoIP requisitoIP = response.getBody();
    Assertions.assertThat(requisitoIP.getId()).as("getId()").isEqualTo(idConvocatoria);
    Assertions.assertThat(requisitoIP.getSexoRef()).as("getSexoRef()").isEqualTo(requisitoIP.getSexoRef());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_ip.sql",
    "classpath:scripts/requisitoip_nivelacademico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void testFindNivelesAcademicos() throws Exception {
    Long id = 1L;

    final ResponseEntity<List<RequisitoIPNivelAcademicoOutput>> response = restTemplate.exchange(
        RequisitoIPController.MAPPING + RequisitoIPController.PATH_NIVELES, HttpMethod.GET, buildRequest(null),
        new ParameterizedTypeReference<List<RequisitoIPNivelAcademicoOutput>>() {
        }, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<RequisitoIPNivelAcademicoOutput> body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_ip.sql",
    "classpath:scripts/requisitoip_nivelacademico.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void testUpdateNivelesAcademicos() throws Exception {
    Long id = 1L;

    List<RequisitoIPNivelAcademicoInput> requestBody = new ArrayList<RequisitoIPNivelAcademicoInput>();
    for (int i = 0; i < 10; i++) {
      requestBody.add(RequisitoIPNivelAcademicoInput.builder().requisitoIPId(id)
          .nivelAcademicoRef("nivelAcademicoRef" + i).build());
    }

    final ResponseEntity<List<RequisitoIPNivelAcademicoOutput>> response = restTemplate.exchange(
        RequisitoIPController.MAPPING + RequisitoIPController.PATH_NIVELES, HttpMethod.PATCH, buildRequest(requestBody),
        new ParameterizedTypeReference<List<RequisitoIPNivelAcademicoOutput>>() {
        }, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<RequisitoIPNivelAcademicoOutput> body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body).hasSize(10);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_ip.sql",
    "classpath:scripts/requisitoip_categoriaprofesional.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void testFindCategoriasProfesionales() throws Exception {
    Long id = 1L;

    final ResponseEntity<List<RequisitoIPCategoriaProfesionalOutput>> response = restTemplate.exchange(
        RequisitoIPController.MAPPING + RequisitoIPController.PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO,
        HttpMethod.GET, buildRequest(null),
        new ParameterizedTypeReference<List<RequisitoIPCategoriaProfesionalOutput>>() {
        }, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<RequisitoIPCategoriaProfesionalOutput> body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_ip.sql",
    "classpath:scripts/requisitoip_categoriaprofesional.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void testUpdateCategoriasProfesionales() throws Exception {
    Long id = 1L;

    List<RequisitoIPCategoriaProfesionalInput> requestBody = new ArrayList<RequisitoIPCategoriaProfesionalInput>();
    for (int i = 0; i < 10; i++) {
      requestBody.add(RequisitoIPCategoriaProfesionalInput.builder().requisitoIPId(id)
          .categoriaProfesionalRef("categoriaProfesionalRef" + i).build());
    }

    final ResponseEntity<List<RequisitoIPCategoriaProfesionalOutput>> response = restTemplate.exchange(
        RequisitoIPController.MAPPING + RequisitoIPController.PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO,
        HttpMethod.PATCH, buildRequest(requestBody),
        new ParameterizedTypeReference<List<RequisitoIPCategoriaProfesionalOutput>>() {
        }, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<RequisitoIPCategoriaProfesionalOutput> body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body).hasSize(10);
  }

  /**
   * Función que devuelve un objeto RequisitoIP
   * 
   * @param id id del RequisitoIP
   * @return el objeto RequisitoIP
   */
  private RequisitoIP generarMockRequisitoIP(Long id) {
    RequisitoIP requisitoIP = new RequisitoIP();
    requisitoIP.setId(id != null ? id : 1L);
    requisitoIP.setSexoRef("Hombre");
    return requisitoIP;
  }

}
