package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
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
 * Test de integracion de Evaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/formulario.sql", 
  "classpath:scripts/comite.sql", 
  "classpath:scripts/bloque.sql", 
  "classpath:scripts/apartado.sql",
  "classpath:scripts/tipo_actividad.sql", 
  "classpath:scripts/tipo_memoria.sql",
  "classpath:scripts/tipo_estado_memoria.sql", 
  "classpath:scripts/estado_retrospectiva.sql",
  "classpath:scripts/tipo_convocatoria_reunion.sql", 
  "classpath:scripts/tipo_evaluacion.sql",
  "classpath:scripts/cargo_comite.sql", 
  "classpath:scripts/tipo_comentario.sql", 
  "classpath:scripts/dictamen.sql", 
  "classpath:scripts/evaluador.sql", 
  "classpath:scripts/convocatoria_reunion.sql", 
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/retrospectiva.sql",
  "classpath:scripts/memoria.sql",
  "classpath:scripts/evaluacion.sql",
  "classpath:scripts/comentario.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class EvaluacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUACION_CONTROLLER_BASE_PATH = "/evaluaciones";
  private static final String EVALUACION_LIST_PATH = "/evaluables";
  private static final String EVALUACION_SEGUIMIENTO_PATH = "/memorias-seguimiento-final";

  private HttpEntity<Evaluacion> buildRequest(HttpHeaders headers, Evaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Evaluacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<Comentario> buildRequestComentario(HttpHeaders headers, Comentario entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Comentario> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getEvaluacion_WithId_ReturnsEvaluacion() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(headers, null),
        Evaluacion.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Evaluacion evaluacion = response.getBody();

    Assertions.assertThat(evaluacion.getId()).isEqualTo(2L);
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria002");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Favorable");
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Test
  public void addEvaluacion_ReturnsEvaluacion() throws Exception {

    Evaluacion nuevoEvaluacion = generarMockEvaluacion(null, "1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-C", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(EVALUACION_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(headers, nuevoEvaluacion), Evaluacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Test
  public void removeEvaluacion_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    // when: Delete
    long id = 2L;
    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(headers, null),
        Evaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeEvaluacion_DoNotGetEvaluacion() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-B", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(headers, null),
        Evaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  public void replaceEvaluacion_ReturnsEvaluacion() throws Exception {

    Evaluacion replaceEvaluacion = generarMockEvaluacion(1L, null);

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVAL")));

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(headers, replaceEvaluacion),
        Evaluacion.class, 5L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Evaluacion evaluacion = response.getBody();

    Assertions.assertThat(evaluacion.getId()).isNotNull();
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Test
  public void findAll_WithPaging_ReturnsEvaluacionSubList() throws Exception {
    // when: Obtiene la page=1 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    String sort = "id,asc";
    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false)
        .toUri();

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(5);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("11");

    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria014");
    Assertions.assertThat(evaluaciones.get(1).getMemoria().getTitulo()).isEqualTo("Memoria007");
    Assertions.assertThat(evaluaciones.get(2).getMemoria().getTitulo()).isEqualTo("Memoria008");
    Assertions.assertThat(evaluaciones.get(3).getMemoria().getTitulo()).isEqualTo("Memoria009");
    Assertions.assertThat(evaluaciones.get(4).getMemoria().getTitulo()).isEqualTo("Memoria010");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluacionList() throws Exception {
    // when: Búsqueda por esRevMinima equals e id equals
    Long id = 6L;
    String query = "esRevMinima==true;id==" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(evaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).startsWith("Memoria");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre()).startsWith("Favorable");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEvaluacionList() throws Exception {
    // when: Ordenación por id desc
    String query = "id,desc";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(11);
    for (int i = 0; i < 11; i++) {
      Evaluacion evaluacion = evaluaciones.get(i);
      Assertions.assertThat(evaluacion.getId()).isEqualTo(12 - i);
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEvaluacionSubList() throws Exception {
    // when: Obtiene page=0 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));
    // when: Ordena por id desc
    String sort = "id,desc";
    // when: Filtra por version equals
    String filter = "version==3";

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene memoria.titulo='Memoria002'
    // Contiene dictamen.nombre='Favorable'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria002");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre()).isEqualTo("Favorable");

  }

  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithPaging_ReturnsEvaluacionSubList() throws Exception {
    // when: Obtiene la page=0 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene memoria.titulo='Memoria009'
    // Contiene dictamen.nombre='Favorable pendiente de revisión mínima'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria009");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre())
        .isEqualTo("Favorable pendiente de revisión mínima");
  }

  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithSearchQuery_ReturnsFilteredEvaluacionList()
      throws Exception {
    // when: Búsqueda por esRevMinima equals e id equals
    Long id = 10L;
    String query = "esRevMinima==true;id==" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
        .queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(evaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria009");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre())
        .isEqualTo("Favorable pendiente de revisión mínima");
  }

  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithSortQuery_ReturnsOrderedEvaluacionList()
      throws Exception {
    // when: Ordenación por id desc
    String query = "id,desc";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
        .queryParam("s", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 9));
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre())
        .isEqualTo("Favorable pendiente de revisión mínima");
  }

  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithPagingSortingAndFiltering_ReturnsEvaluacionSubList()
      throws Exception {
    // when: Obtiene page=0 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));
    // when: Ordena por id desc
    String sort = "id,desc";
    // when: Filtra por version equals
    String filter = "version==1";

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene memoria.titulo='Memoria009'
    // Contiene dictamen.nombre='Favorable pendiente de revisión mínima'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 9));
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre())
        .isEqualTo("Favorable pendiente de revisión mínima");

  }

  @Test
  @Sql(scripts = {
  // @formatter:off  
  "classpath:scripts/evaluacion_seguimiento.sql",
// @formatter:on  
  })
  public void findByEvaluacionesEnSeguimientoFinal_ReturnsEvaluacionList() throws Exception {
    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-002", "ETI-EVC-V", "ETI-EVC-EVAL")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_SEGUIMIENTO_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene de memoria.titulo='Memoria010'

    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria016");

  }

  @Test
  public void getComentariosGestor_WithId_ReturnsComentario() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");

    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVAL")));

    final ResponseEntity<List<Comentario>> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentarios-gestor", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comentario>>() {
        }, 6L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<Comentario> comentarios = response.getBody();

    Assertions.assertThat(comentarios.size()).isEqualTo(2);

    Assertions.assertThat(comentarios.get(0).getId()).isEqualTo(4L);
  }

  @Test
  public void getComentariosEvaluador_WithId_ReturnsComentario() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");

    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-002", "ETI-EVC-EVALR")));

    final ResponseEntity<List<Comentario>> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentarios-evaluador", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Comentario>>() {
        }, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  public void addComentarioGestor_ReturnsComentario() throws Exception {

    Apartado apartado = new Apartado();
    apartado.setId(1L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    Memoria memoria = new Memoria();
    memoria.setId(2L);

    Comentario comentario = new Comentario();
    comentario.setApartado(apartado);
    comentario.setMemoria(memoria);
    comentario.setTexto("comentario1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-EVC-EVAL")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor", HttpMethod.POST,
        buildRequestComentario(headers, comentario), Comentario.class, 7L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Test
  public void addComentarioEvaluador_ReturnsError400() throws Exception {

    Evaluacion evaluacion = generarMockEvaluacion(13L, "eval");
    Apartado apartado = new Apartado();
    apartado.setId(1L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    Comentario comentario = new Comentario();
    comentario.setApartado(apartado);
    comentario.setEvaluacion(evaluacion);
    comentario.setTexto("comentario1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVALR", "ETI-EVC-INV-EVALR")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-evaluador", HttpMethod.POST,
        buildRequestComentario(headers, comentario), Comentario.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Test
  public void addComentarioEvaluador_ReturnsComentario() throws Exception {

    Apartado apartado = new Apartado();
    apartado.setId(1L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    Memoria memoria = new Memoria();
    memoria.setId(2L);

    Comentario comentario = new Comentario();
    comentario.setApartado(apartado);
    comentario.setMemoria(memoria);
    comentario.setTexto("comentario1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-002", "ETI-EVC-EVALR", "ETI-EVC-INV-EVALR")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-evaluador", HttpMethod.POST,
        buildRequestComentario(headers, comentario), Comentario.class, 7L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Test
  public void addComentarioGestor_ReturnsError400() throws Exception {

    Evaluacion evaluacion = generarMockEvaluacion(7L, "eval");
    Apartado apartado = new Apartado();
    apartado.setId(1L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    Comentario comentario = new Comentario();
    comentario.setApartado(apartado);
    comentario.setEvaluacion(evaluacion);
    comentario.setTexto("comentario1");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVAL")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor", HttpMethod.POST,
        buildRequestComentario(headers, comentario), Comentario.class, 7L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Test
  public void replaceComentarioGestor_ReturnsComentario() throws Exception {

    Evaluacion evaluacion = generarMockEvaluacion(8L, null);
    Apartado apartado = new Apartado();
    apartado.setId(1L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(1L);

    Comentario comentarioReplace = new Comentario();
    comentarioReplace.setId(7L);
    comentarioReplace.setApartado(apartado);
    comentarioReplace.setEvaluacion(evaluacion);
    comentarioReplace.setTipoComentario(tipoComentario);
    comentarioReplace.setTexto("Actualizado");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user-001", "ETI-EVC-EVAL")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor" + "/{idComentario}", HttpMethod.PUT,
        buildRequestComentario(headers, comentarioReplace), Comentario.class, 7L, 6L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comentario comentario = response.getBody();

    Assertions.assertThat(comentario.getId()).isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).isEqualTo(8L);
    Assertions.assertThat(comentario.getTexto()).isEqualTo("Actualizado");
  }

  @Test
  public void replaceComentarioEvaluador_ReturnsComentario() throws Exception {

    Evaluacion evaluacion = generarMockEvaluacion(3L, null);
    Apartado apartado = new Apartado();
    apartado.setId(1L);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Bloque Bloque = new Bloque(1L, formulario, "Bloque 1", 1);
    apartado.setBloque(Bloque);

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(2L);

    Comentario comentarioReplace = new Comentario();
    comentarioReplace.setId(1L);
    comentarioReplace.setApartado(apartado);
    comentarioReplace.setEvaluacion(evaluacion);
    comentarioReplace.setTipoComentario(tipoComentario);
    comentarioReplace.setTexto("Actualizado");

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-002", "ETI-EVC-EVALR", "ETI-EVC-INV-EVALR")));

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-evaluador" + "/{idComentario}",
        HttpMethod.PUT, buildRequestComentario(headers, comentarioReplace), Comentario.class, 3L, 3L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comentario comentario = response.getBody();

    Assertions.assertThat(comentario.getId()).isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).isEqualTo(3L);
    Assertions.assertThat(comentario.getTexto()).isEqualTo("Actualizado");
  }

  @Test
  public void removeComentarioGestor_Success() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-EVAL")));

    // when: Delete con id existente
    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor" + "/{idComentario}",
        HttpMethod.DELETE, buildRequestComentario(headers, null), Comentario.class, 7L, 6L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeComentarioEvaluador_Success() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user-002", "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR")));

    // when: Delete con id existente
    final ResponseEntity<Comentario> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-evaluador" + "/{idComentario}",
        HttpMethod.DELETE, buildRequestComentario(headers, null), Comentario.class, 3L, 3L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id     id del Evaluacion
   * @param sufijo el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(1L);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setTipoEvaluacion(tipoEvaluacion);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("Proyecto de investigacion");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(2L);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "CEI", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria001");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(11L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(3L, new EstadoRetrospectiva(3L, "En evaluación", Boolean.TRUE), Instant.now()), 3,
        Boolean.TRUE, null);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(2L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now());
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Evaluador evaluador1 = new Evaluador();
    evaluador1.setId(2L);
    evaluador1.setResumen("Evaluador2");
    evaluador1.setComite(comite);
    evaluador1.setCargoComite(cargoComite);
    evaluador1.setFechaAlta(Instant.parse("2020-07-01T00:00:00Z"));
    evaluador1.setFechaBaja(Instant.parse("2021-07-01T00:00:00Z"));
    evaluador1.setPersonaRef("user-001");
    evaluador1.setActivo(Boolean.TRUE);

    Evaluador evaluador2 = new Evaluador();
    evaluador2.setId(3L);
    evaluador2.setResumen("Evaluador3");
    evaluador2.setComite(comite);
    evaluador2.setCargoComite(cargoComite);
    evaluador2.setFechaAlta(Instant.parse("2020-07-01T00:00:00Z"));
    evaluador2.setFechaBaja(Instant.parse("2021-07-01T00:00:00Z"));
    evaluador2.setPersonaRef("user");
    evaluador2.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(evaluador1);
    evaluacion.setEvaluador2(evaluador2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

}