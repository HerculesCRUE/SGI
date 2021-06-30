package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
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
 * Test de integracion de ConvocatoriaEntidadFinanciadora.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConvocatoriaEntidadFinanciadoraIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaentidadfinanciadoras";

  private HttpEntity<ConvocatoriaEntidadFinanciadora> buildRequest(HttpHeaders headers,
      ConvocatoriaEntidadFinanciadora entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-C", "AUTH", "CSP-CON-E")));

    HttpEntity<ConvocatoriaEntidadFinanciadora> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaEntidadFinanciadora() throws Exception {

    // given: new ConvocatoriaEntidadFinanciadora
    ConvocatoriaEntidadFinanciadora newConvocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(
        null);

    // when: create ConvocatoriaEntidadFinanciadora
    final ResponseEntity<ConvocatoriaEntidadFinanciadora> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newConvocatoriaEntidadFinanciadora), ConvocatoriaEntidadFinanciadora.class);

    // then: new ConvocatoriaEntidadFinanciadora is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaEntidadFinanciadora responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaEntidadFinanciadora.getConvocatoriaId());
    Assertions.assertThat(responseData.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(newConvocatoriaEntidadFinanciadora.getEntidadRef());
    Assertions.assertThat(responseData.getFuenteFinanciacion().getId()).as("getFuenteFinanciacion().getId()")
        .isEqualTo(newConvocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId());
    Assertions.assertThat(responseData.getTipoFinanciacion().getId()).as("getTipoFinanciacion().getId()")
        .isEqualTo(newConvocatoriaEntidadFinanciadora.getTipoFinanciacion().getId());
    Assertions.assertThat(responseData.getPorcentajeFinanciacion()).as("getPorcentajeFinanciacion()")
        .isEqualTo(newConvocatoriaEntidadFinanciadora.getPorcentajeFinanciacion());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaEntidadFinanciadora() throws Exception {
    Long idFuenteFinanciacion = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(
        idFuenteFinanciacion);

    final ResponseEntity<ConvocatoriaEntidadFinanciadora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, convocatoriaEntidadFinanciadora),
        ConvocatoriaEntidadFinanciadora.class, idFuenteFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizado = response.getBody();
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getConvocatoriaId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getEntidadRef());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()").isEqualTo(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion()").isEqualTo(convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ConvocatoriaEntidadFinanciadora to be deleted
    Long id = 1L;

    // when: delete ConvocatoriaEntidadFinanciadora
    final ResponseEntity<ConvocatoriaEntidadFinanciadora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaEntidadFinanciadora.class, id);

    // then: ConvocatoriaEntidadFinanciadora deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaEntidadFinanciadora() throws Exception {
    Long idConvocatoriaEntidadFinanciadora = 1L;

    final ResponseEntity<ConvocatoriaEntidadFinanciadora> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaEntidadFinanciadora.class, idConvocatoriaEntidadFinanciadora);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = response.getBody();
    Assertions.assertThat(convocatoriaEntidadFinanciadora.getId()).as("getId()")
        .isEqualTo(idConvocatoriaEntidadFinanciadora);
    Assertions.assertThat(convocatoriaEntidadFinanciadora.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaEntidadFinanciadora.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo("entidad-001");
    Assertions.assertThat(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion()).as("getPorcentajeFinanciacion()")
        .isEqualTo(20);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadFinanciadora
   * 
   * @param id id del ConvocatoriaEntidadFinanciadora
   * @return el objeto ConvocatoriaEntidadFinanciadora
   */
  private ConvocatoriaEntidadFinanciadora generarMockConvocatoriaEntidadFinanciadora(Long id) {
    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id == null ? 1 : id);
    fuenteFinanciacion.setActivo(true);

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id == null ? 1 : id);
    tipoFinanciacion.setActivo(true);

    Programa programa = new Programa();
    programa.setId(id);

    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = new ConvocatoriaEntidadFinanciadora();
    convocatoriaEntidadFinanciadora.setId(id);
    convocatoriaEntidadFinanciadora.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaEntidadFinanciadora.setEntidadRef("entidad-" + (id == null ? 0 : id));
    convocatoriaEntidadFinanciadora.setFuenteFinanciacion(fuenteFinanciacion);
    convocatoriaEntidadFinanciadora.setTipoFinanciacion(tipoFinanciacion);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(50);

    return convocatoriaEntidadFinanciadora;
  }

}
