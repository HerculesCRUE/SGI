package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
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
  "classpath:scripts/tipo_memoria.sql",
  "classpath:scripts/tipo_actividad.sql",
  "classpath:scripts/estado_retrospectiva.sql",
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/comite.sql",
  "classpath:scripts/tipo_memoria_comite.sql",
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

  private HttpEntity<TipoMemoria> buildRequestTipoMemoria(HttpHeaders headers, TipoMemoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<TipoMemoria> request = new HttpEntity<>(entity, headers);
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
    Assertions.assertThat(comite.getComite()).isEqualTo("CEEA");
  }

  @Test
  public void addComite_ReturnsComite() throws Exception {
    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");

    Comite nuevoComite = new Comite();
    nuevoComite.setComite("Comite1");
    nuevoComite.setActivo(Boolean.TRUE);
    nuevoComite.setFormulario(formulario);

    ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, nuevoComite), Comite.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Comite comite = response.getBody();

    Assertions.assertThat(comite.getId()).isNotNull();
    Assertions.assertThat(comite.getComite()).isEqualTo(nuevoComite.getComite());
  }

  @Test
  public void removeComite_Success() throws Exception {

    // when: Delete con id existente
    long id = 2L;
    final ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Comite.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeComite_DoNotGetComite() throws Exception {

    final ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Comite.class, 9L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceComite_ReturnsComite() throws Exception {

    Comite replaceComite = new Comite();
    replaceComite.setComite("Comite2");
    replaceComite.setFormulario(new Formulario(2L, "M20", "Descripcion"));
    replaceComite.setActivo(Boolean.TRUE);

    final ResponseEntity<Comite> response = restTemplate.exchange(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, replaceComite), Comite.class, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comite comite = response.getBody();

    Assertions.assertThat(comite.getId()).isNotNull();
    Assertions.assertThat(comite.getComite()).isEqualTo(replaceComite.getComite());
  }

  @Test
  public void findAll_WithPaging_ReturnsComiteSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
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
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de comite='Comite7' a 'Comite8'
    Assertions.assertThat(comites.get(0).getComite()).isEqualTo("Comite6");
    Assertions.assertThat(comites.get(1).getComite()).isEqualTo("Comite7");
    Assertions.assertThat(comites.get(2).getComite()).isEqualTo("Comite8");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredComiteList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "comite=ke=Comite;id==" + id;

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
    Assertions.assertThat(comites.get(0).getComite()).startsWith("Comite");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedComiteList() throws Exception {
    // when: Ordenación por comite desc
    String query = "comite,desc";

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
    Assertions.assertThat(comites.size()).isEqualTo(8);

    Assertions.assertThat(comites.get(0).getComite()).isEqualTo("Comite8");
    Assertions.assertThat(comites.get(1).getComite()).isEqualTo("Comite7");
    Assertions.assertThat(comites.get(2).getComite()).isEqualTo("Comite6");
    Assertions.assertThat(comites.get(3).getComite()).isEqualTo("Comite5");
    Assertions.assertThat(comites.get(4).getComite()).isEqualTo("Comite4");
    Assertions.assertThat(comites.get(5).getComite()).isEqualTo("CEI");
    Assertions.assertThat(comites.get(6).getComite()).isEqualTo("CEEA");
    Assertions.assertThat(comites.get(7).getComite()).isEqualTo("CBE");

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
    String sort = "comite,desc";
    // when: Filtra por comite like e id equals
    String filter = "comite=ke=Comite";

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
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("5");

    // Contiene comite='Comite8', 'Comite7',
    // 'Comite6'
    Assertions.assertThat(comites.get(0).getComite()).isEqualTo("Comite" + String.format("%d", 8));
    Assertions.assertThat(comites.get(1).getComite()).isEqualTo("Comite" + String.format("%d", 7));
    Assertions.assertThat(comites.get(2).getComite()).isEqualTo("Comite" + String.format("%d", 6));

  }

  @Test
  public void findMemorias_ReturnsMemoria() throws Exception {

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
    Assertions.assertThat(memoria.size()).isEqualTo(13);

    Assertions.assertThat(memoria.get(0).getTitulo()).isEqualTo("Memoria002");
    Assertions.assertThat(memoria.get(1).getTitulo()).isEqualTo("Memoria003");
    Assertions.assertThat(memoria.get(2).getTitulo()).isEqualTo("Memoria004");
    Assertions.assertThat(memoria.get(3).getTitulo()).isEqualTo("Memoria005");
    Assertions.assertThat(memoria.get(4).getTitulo()).isEqualTo("Memoria006");
    Assertions.assertThat(memoria.get(5).getTitulo()).isEqualTo("Memoria007");
    Assertions.assertThat(memoria.get(6).getTitulo()).isEqualTo("Memoria008");
    Assertions.assertThat(memoria.get(7).getTitulo()).isEqualTo("Memoria010");
    Assertions.assertThat(memoria.get(8).getTitulo()).isEqualTo("Memoria011");
    Assertions.assertThat(memoria.get(9).getTitulo()).isEqualTo("Memoria012");
    Assertions.assertThat(memoria.get(10).getTitulo()).isEqualTo("Memoria013");
    Assertions.assertThat(memoria.get(11).getTitulo()).isEqualTo("Memoria014");
    Assertions.assertThat(memoria.get(12).getTitulo()).isEqualTo("Memoria015");
  }

  @Test
  public void findMemoria_Unlimited_ReturnsEmptyMemoria() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-INV-C")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID_COMITE + "/memorias-peticion-evaluacion/"
            + PATH_PARAMETER_ID_PETICION_EVALUACION,
        HttpMethod.GET, buildRequestMemoria(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        }, 3L, 2L);

    // then: No existen memorias.
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Test
  public void findTipoMemoria_ReturnsTipoMemoria() throws Exception {

    // when: find unlimited asignables para el comité
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-INV-C")));

    final ResponseEntity<List<TipoMemoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tipo-memorias", HttpMethod.GET,
        buildRequestTipoMemoria(headers, null), new ParameterizedTypeReference<List<TipoMemoria>>() {
        }, 2L);

    // then: Obtiene los tipos de memoria del comité 1
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoMemoria> tipoMemoria = response.getBody();
    Assertions.assertThat(tipoMemoria.size()).isEqualTo(7);

    Assertions.assertThat(tipoMemoria.get(0).getNombre()).isEqualTo("TipoMemoria001");
    Assertions.assertThat(tipoMemoria.get(1).getNombre()).isEqualTo("TipoMemoria001");
    Assertions.assertThat(tipoMemoria.get(2).getNombre()).isEqualTo("TipoMemoria001");
    Assertions.assertThat(tipoMemoria.get(3).getNombre()).isEqualTo("TipoMemoria001");
    Assertions.assertThat(tipoMemoria.get(4).getNombre()).isEqualTo("TipoMemoria001");
    Assertions.assertThat(tipoMemoria.get(5).getNombre()).isEqualTo("TipoMemoria001");
    Assertions.assertThat(tipoMemoria.get(6).getNombre()).isEqualTo("TipoMemoria001");

  }

  @Test
  public void findTipoMemoria_Unlimited_ReturnsEmptyTipoMemoria() throws Exception {

    // when: find unlimited asignables para el comité
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-PEV-INV-C", "ETI-PEV-INV-ER")));

    final ResponseEntity<List<TipoMemoria>> response = restTemplate.exchange(
        COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tipo-memorias", HttpMethod.GET,
        buildRequestTipoMemoria(headers, null), new ParameterizedTypeReference<List<TipoMemoria>>() {
        }, 3L);

    // then: No obtiene ningún tipo de memoria para el comité 3.
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

}