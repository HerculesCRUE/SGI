package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
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
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de ConvocatoriaConceptoGasto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConvocatoriaConceptoGastoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaconceptogastos";

  private HttpEntity<ConvocatoriaConceptoGasto> buildRequest(HttpHeaders headers, ConvocatoriaConceptoGasto entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CON-E", "CSP-SOL-E", "CSP-CON-V", "CSP-CON-INV-V")));

    HttpEntity<ConvocatoriaConceptoGasto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaConceptoGasto() throws Exception {
    // given: new ConvocatoriaConceptoGasto
    ConvocatoriaConceptoGasto newConvocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(null, true);
    // when: create ConvocatoriaConceptoGasto
    final ResponseEntity<ConvocatoriaConceptoGasto> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newConvocatoriaConceptoGasto), ConvocatoriaConceptoGasto.class);

    // then: new ConvocatoriaConceptoGasto is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaConceptoGasto responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaConceptoGasto.getConvocatoriaId());
    Assertions.assertThat(responseData.getConceptoGasto().getId()).as("getConceptoGasto().getId()")
        .isEqualTo(newConvocatoriaConceptoGasto.getConceptoGasto().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaConceptoGasto() throws Exception {
    Long idConvocatoriaConceptoGasto = 1L;
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(
        idConvocatoriaConceptoGasto, true);

    final ResponseEntity<ConvocatoriaConceptoGasto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, convocatoriaConceptoGasto),
        ConvocatoriaConceptoGasto.class, idConvocatoriaConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizado = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaConceptoGasto.getConvocatoriaId());
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConceptoGasto().getId())
        .as("getConceptoGasto().getId()").isEqualTo(convocatoriaConceptoGasto.getConceptoGasto().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idConvocatoriaConceptoGasto = 1L;

    final ResponseEntity<ConvocatoriaConceptoGasto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaConceptoGasto.class, idConvocatoriaConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaConceptoGasto() throws Exception {
    Long idConvocatoriaConceptoGasto = 1L;

    final ResponseEntity<ConvocatoriaConceptoGasto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaConceptoGasto.class, idConvocatoriaConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGasto.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGasto.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsConvocatoriaConceptoGastoSubList() throws Exception {

    // given: data for Convocatoria

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-ME-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "permitido==true";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<ConvocatoriaConceptoGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaConceptoGasto>>() {
        });

    // given: Convocatoria data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaConceptoGasto> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("4");

    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId())").isEqualTo(6);
    Assertions.assertThat(responseData.get(1).getId()).as("get(1).getId())").isEqualTo(4);
    Assertions.assertThat(responseData.get(2).getId()).as("get(2).getId())").isEqualTo(2);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGasto
   * 
   * @param id        id del ConvocatoriaConceptoGasto
   * @param permitido boolean permitido
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarMockConvocatoriaConceptoGasto(Long id, Boolean permitido) {
    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id == null ? 1 : id);
    conceptoGasto.setActivo(true);
    conceptoGasto.setDescripcion("descripcion-00" + (id == null ? 1 : id));
    conceptoGasto.setNombre("nombre-00" + (id == null ? 1 : id));

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
    convocatoriaConceptoGasto.setId(id);
    convocatoriaConceptoGasto.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaConceptoGasto.setConceptoGasto(conceptoGasto);
    convocatoriaConceptoGasto.setPermitido(permitido);

    return convocatoriaConceptoGasto;
  }
}
