package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de Comite.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/formulario.sql", 
  "classpath:scripts/tipo_actividad.sql",
  "classpath:scripts/estado_retrospectiva.sql",
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/comite.sql",
  "classpath:scripts/tipo_convocatoria_reunion.sql",
  "classpath:scripts/tipo_documento.sql",
  "classpath:scripts/cargo_comite.sql",
  "classpath:scripts/tipo_evaluacion.sql",
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/retrospectiva.sql",
  "classpath:scripts/memoria.sql"
// @formatter:on     
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class ComiteIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_ID_COMITE = "/{idComite}";
  private static final String PATH_PARAMETER_ID_PETICION_EVALUACION = "/{idPeticionEvaluacion}";
  private static final String COMITE_CONTROLLER_BASE_PATH = "/comites";

  private HttpEntity<Comite> buildRequest(HttpHeaders headers, Comite entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Comite> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<Memoria> buildRequestMemoria(HttpHeaders headers, Memoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Memoria> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getComite_WithId_ReturnsComite() throws Exception {
    final ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Comite.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comite comite = response.getBody();

    Assertions.assertThat(comite.getId()).isEqualTo(2L);
    Assertions.assertThat(comite.getCodigo()).isEqualTo("CEEA");
  }

  @Test
  public void findAll_WithPaging_ReturnsComiteSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V", "ETI-CNV-V")));

    final ResponseEntity<List<Comite>> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comite>>() {
        });

    // then: Respuesta OK, ComiteS retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comite> comites = response.getBody();
    Assertions.assertThat(comites.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene de comite='Comite1' a 'Comite3'
    Assertions.assertThat(comites.get(0).getCodigo()).isEqualTo("CEI");
    Assertions.assertThat(comites.get(1).getCodigo()).isEqualTo("CEEA");
    Assertions.assertThat(comites.get(2).getCodigo()).isEqualTo("CBE");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredComiteList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 3L;
    String query = "codigo=ke=C;id==" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V", "ETI-CNV-V")));

    URI uri = UriComponentsBuilder.fromUriString(COMITE_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Comite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comite>>() {
        });

    // then: Respuesta OK, ComiteS retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comite> comites = response.getBody();
    Assertions.assertThat(comites.size()).isEqualTo(1);
    Assertions.assertThat(comites.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(comites.get(0).getCodigo()).isEqualTo("CBE");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedComiteList() throws Exception {
    // when: Ordenación por comite desc
    String query = "codigo,desc";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V", "ETI-CNV-V")));

    URI uri = UriComponentsBuilder.fromUriString(COMITE_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Comite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comite>>() {
        });

    // then: Respuesta OK, Comites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comite> comites = response.getBody();
    Assertions.assertThat(comites.size()).isEqualTo(3);

    Assertions.assertThat(comites.get(0).getCodigo()).isEqualTo("CEI");
    Assertions.assertThat(comites.get(1).getCodigo()).isEqualTo("CEEA");
    Assertions.assertThat(comites.get(2).getCodigo()).isEqualTo("CBE");

  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsComiteSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V", "ETI-CNV-V")));
    // when: Ordena por comite desc
    String sort = "codigo,desc";
    // when: Filtra por comite like e id equals
    String filter = "codigo=ke=C";

    URI uri = UriComponentsBuilder.fromUriString(COMITE_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Comite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comite>>() {
        });

    // then: Respuesta OK, Comites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comite> comites = response.getBody();
    Assertions.assertThat(comites.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene comite='Comite8', 'Comite7',
    // 'Comite6'
    Assertions.assertThat(comites.get(0).getCodigo()).isEqualTo("CEI");
    Assertions.assertThat(comites.get(1).getCodigo()).isEqualTo("CEEA");
    Assertions.assertThat(comites.get(2).getCodigo()).isEqualTo("CBE");

  }

  @Test
  public void findMemoriasPeticionEvaluacionModificables_ReturnsMemoria() throws Exception {

    // when: find unlimited asignables para el comité
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-INV-C", "ETI-PEV-INV-ER")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID_COMITE + "/memorias-peticion-evaluacion/"
            + PATH_PARAMETER_ID_PETICION_EVALUACION,
        HttpMethod.GET, buildRequestMemoria(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        }, 1L, 2L);

    // then: Obtiene las memorias del comité 2.
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memoria = response.getBody();
    Assertions.assertThat(memoria.size()).isEqualTo(7);

    Assertions.assertThat(I18nHelper.getValueForLanguage(memoria.get(0).getTitulo(), Language.ES))
        .isEqualTo("Memoria002");
    Assertions.assertThat(
        I18nHelper.getValueForLanguage(memoria.get(1).getTitulo(), Language.ES)).isEqualTo("Memoria004");
    Assertions.assertThat(
        I18nHelper.getValueForLanguage(memoria.get(2).getTitulo(), Language.ES)).isEqualTo("Memoria006");
    Assertions.assertThat(
        I18nHelper.getValueForLanguage(memoria.get(3).getTitulo(), Language.ES)).isEqualTo("Memoria007");
    Assertions.assertThat(
        I18nHelper.getValueForLanguage(memoria.get(4).getTitulo(), Language.ES)).isEqualTo("Memoria010");
    Assertions.assertThat(
        I18nHelper.getValueForLanguage(memoria.get(5).getTitulo(), Language.ES)).isEqualTo("Memoria014");
    Assertions.assertThat(
        I18nHelper.getValueForLanguage(memoria.get(6).getTitulo(), Language.ES)).isEqualTo("Memoria015");
  }

  @Test
  public void findMemoria_Unlimited_ReturnsEmptyMemoria() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-INV-ER")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID_COMITE + "/memorias-peticion-evaluacion/"
            + PATH_PARAMETER_ID_PETICION_EVALUACION,
        HttpMethod.GET, buildRequestMemoria(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        }, 3L, 2L);

    // then: No existen memorias.
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

}