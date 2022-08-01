package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Programa;
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
 * Test de integracion de ConvocatoriaEntidadConvocante.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConvocatoriaEntidadConvocanteIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaentidadconvocantes";

  private HttpEntity<ConvocatoriaEntidadConvocante> buildRequest(HttpHeaders headers,
      ConvocatoriaEntidadConvocante entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-CON-C", "CSP-CON-E")));

    HttpEntity<ConvocatoriaEntidadConvocante> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsConvocatoriaEntidadConvocante() throws Exception {

    // given: new ConvocatoriaEntidadConvocante
    ConvocatoriaEntidadConvocante newConvocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(null);

    // when: create ConvocatoriaEntidadConvocante
    final ResponseEntity<ConvocatoriaEntidadConvocante> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newConvocatoriaEntidadConvocante), ConvocatoriaEntidadConvocante.class);

    // then: new ConvocatoriaEntidadConvocante is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaEntidadConvocante responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaEntidadConvocante.getConvocatoriaId());
    Assertions.assertThat(responseData.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(newConvocatoriaEntidadConvocante.getEntidadRef());
    Assertions.assertThat(responseData.getPrograma().getId()).as("getPrograma().getId()")
        .isEqualTo(newConvocatoriaEntidadConvocante.getPrograma().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConvocatoriaEntidadConvocante() throws Exception {
    Long idFuenteFinanciacion = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(
        idFuenteFinanciacion);

    final ResponseEntity<ConvocatoriaEntidadConvocante> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, convocatoriaEntidadConvocante),
        ConvocatoriaEntidadConvocante.class, idFuenteFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizado = response.getBody();
    Assertions.assertThat(convocatoriaEntidadConvocanteActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadConvocanteActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEntidadConvocante.getConvocatoriaId());
    Assertions.assertThat(convocatoriaEntidadConvocanteActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(convocatoriaEntidadConvocante.getEntidadRef());
    Assertions.assertThat(convocatoriaEntidadConvocanteActualizado.getPrograma().getId()).as("getPrograma().getId()")
        .isEqualTo(convocatoriaEntidadConvocante.getPrograma().getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing ConvocatoriaEntidadConvocante to be deleted
    Long id = 1L;

    // when: delete ConvocatoriaEntidadConvocante
    final ResponseEntity<ConvocatoriaEntidadConvocante> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaEntidadConvocante.class, id);

    // then: ConvocatoriaEntidadConvocante deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConvocatoriaEntidadConvocante() throws Exception {
    Long idConvocatoriaEntidadConvocante = 1L;

    final ResponseEntity<ConvocatoriaEntidadConvocante> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaEntidadConvocante.class, idConvocatoriaEntidadConvocante);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = response.getBody();
    Assertions.assertThat(convocatoriaEntidadConvocante.getId()).as("getId()")
        .isEqualTo(idConvocatoriaEntidadConvocante);
    Assertions.assertThat(convocatoriaEntidadConvocante.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaEntidadConvocante.getEntidadRef()).as("getEntidadRef()").isEqualTo("entidad-001");
    Assertions.assertThat(convocatoriaEntidadConvocante.getPrograma().getId()).as("getPrograma().getId()")
        .isEqualTo(1L);

  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadConvocante
   * 
   * @param id id del ConvocatoriaEntidadConvocante
   * @return el objeto ConvocatoriaEntidadConvocante
   */
  private ConvocatoriaEntidadConvocante generarMockConvocatoriaEntidadConvocante(Long id) {
    Programa programa = new Programa();
    programa.setId(id == null ? 1 : id);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
    convocatoriaEntidadConvocante.setId(id);
    convocatoriaEntidadConvocante.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 1 : id));
    convocatoriaEntidadConvocante.setPrograma(programa);

    return convocatoriaEntidadConvocante;
  }

}
