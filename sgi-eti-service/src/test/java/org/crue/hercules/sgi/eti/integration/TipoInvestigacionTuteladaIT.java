package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de TipoInvestigacionTutelada.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/tipo_investigacion_tutelada.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })

public class TipoInvestigacionTuteladaIT extends BaseIT {

  private static final String TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH = "/tipoinvestigaciontuteladas";

  private HttpEntity<TipoInvestigacionTutelada> buildRequest(HttpHeaders headers, TipoInvestigacionTutelada entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-TIPOACTIVIDAD-EDITAR",
        "ETI-TIPOACTIVIDAD-VER", "ETI-PEV-CR", "ETI-MEM-CR", "ETI-PEV-C-INV", "ETI-PEV-ER-INV")));

    HttpEntity<TipoInvestigacionTutelada> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void findAll_WithPaging_ReturnsTipoInvestigacionTuteladaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoInvestigacionTutelada>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoInvestigacionTutelada>>() {
        });

    // then: Respuesta OK, TipoInvestigacionTuteladas retorna la información de la
    // página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = response.getBody();
    Assertions.assertThat(tipoInvestigacionTuteladas.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene de nombre='Trabajo Fin de Grado'
    Assertions.assertThat(tipoInvestigacionTuteladas.get(0).getNombre()).isEqualTo("Trabajo Fin de Grado");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoInvestigacionTuteladaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 1L;
    String query = "nombre=ke=Tesis;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH)
        .queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoInvestigacionTutelada>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoInvestigacionTutelada>>() {
        });

    // then: Respuesta OK, TipoInvestigacionTuteladas retorna la información de la
    // página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = response.getBody();
    Assertions.assertThat(tipoInvestigacionTuteladas.size()).isEqualTo(1);
    Assertions.assertThat(tipoInvestigacionTuteladas.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoInvestigacionTuteladas.get(0).getNombre()).startsWith("Tesis doctoral");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoInvestigacionTuteladaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre,desc";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH)
        .queryParam("s", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoInvestigacionTutelada>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<TipoInvestigacionTutelada>>() {
        });

    // then: Respuesta OK, TipoInvestigacionTuteladas retorna la información de la
    // página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = response.getBody();
    Assertions.assertThat(tipoInvestigacionTuteladas.size()).isEqualTo(3);
    Assertions.assertThat(tipoInvestigacionTuteladas.get(0).getId()).isEqualTo(2);
    Assertions.assertThat(tipoInvestigacionTuteladas.get(0).getNombre()).isEqualTo("Trabajo Fin de Máster");
    Assertions.assertThat(tipoInvestigacionTuteladas.get(2).getId()).isEqualTo(1);
    Assertions.assertThat(tipoInvestigacionTuteladas.get(2).getNombre()).isEqualTo("Tesis doctoral");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoInvestigacionTuteladaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "4");
    // when: Ordena por nombre desc
    String sort = "nombre,desc";
    // when: Filtra por nombre like
    String filter = "nombre=ke=Trabajo";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_INVESTIGACION_TUTELADA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoInvestigacionTutelada>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoInvestigacionTutelada>>() {
        });

    // then: Respuesta OK, TipoInvestigacionTuteladas retorna la información de la
    // página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = response.getBody();
    Assertions.assertThat(tipoInvestigacionTuteladas.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("4");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene nombre='Trabajo Fin de Grado', 'Trabajo Fin de Máster'
    Assertions.assertThat(tipoInvestigacionTuteladas.get(0).getNombre()).isEqualTo("Trabajo Fin de Máster");
    Assertions.assertThat(tipoInvestigacionTuteladas.get(1).getNombre()).isEqualTo("Trabajo Fin de Grado");

  }

  /**
   * Función que devuelve un objeto TipoInvestigacionTutelada
   * 
   * @param id     id del tipoInvestigacionTutelada
   * @param nombre la descripción del tipo de Investigacion Tutelada
   * @return el objeto tipo Investigacion Tutelada
   */

  public TipoInvestigacionTutelada generarMockTipoInvestigacionTutelada(Long id, String nombre) {

    TipoInvestigacionTutelada tipoInvestigacionTutelada = new TipoInvestigacionTutelada();
    tipoInvestigacionTutelada.setId(id);
    tipoInvestigacionTutelada.setNombre(nombre);
    tipoInvestigacionTutelada.setActivo(Boolean.TRUE);

    return tipoInvestigacionTutelada;
  }

}