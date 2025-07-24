package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoriaNombre;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.MemoriaTitulo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionDisMetodologico;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
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
 * Test de integracion de Memoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/formulario.sql", 
  "classpath:scripts/comite.sql", 
  "classpath:scripts/tipo_evaluacion.sql",
  "classpath:scripts/dictamen.sql",
  "classpath:scripts/tipo_convocatoria_reunion.sql",
  "classpath:scripts/cargo_comite.sql",
  "classpath:scripts/tipo_actividad.sql", 
  "classpath:scripts/estado_retrospectiva.sql", 
  "classpath:scripts/tipo_documento.sql",
  "classpath:scripts/tipo_estado_memoria.sql", 
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/retrospectiva.sql",
  "classpath:scripts/memoria.sql", 
  "classpath:scripts/documentacion_memoria.sql",
  "classpath:scripts/evaluador.sql",
  "classpath:scripts/convocatoria_reunion.sql",
  "classpath:scripts/evaluacion.sql"
// @formatter:on  
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
class MemoriaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_ASIGNABLES = "/asignables/{idConvocatoria}";
  private static final String PATH_PARAMETER_ASIGNABLES_ORDEXT = "/tipo-convocatoria-ord-ext";
  private static final String PATH_PARAMETER_ASIGNABLES_SEG = "/tipo-convocatoria-seg";
  private static final String MEMORIA_CONTROLLER_BASE_PATH = "/memorias";
  private static final String PATH_PARAMETER_EVALUACIONES = "/evaluaciones";

  private HttpEntity<Memoria> buildRequest(HttpHeaders headers, Memoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Memoria> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<MemoriaPeticionEvaluacion> buildRequestMemoriaPeticionEvaluacion(HttpHeaders headers,
      MemoriaPeticionEvaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<MemoriaPeticionEvaluacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<DocumentacionMemoria> buildRequestDocumentacionMemoria(HttpHeaders headers,
      DocumentacionMemoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<DocumentacionMemoria> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  void getMemoria_WithId_ReturnsMemoria() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-VR", "ETI-MEM-V")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(headers, null), Memoria.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Memoria tipoMemoria = response.getBody();

    Assertions.assertThat(tipoMemoria.getId()).isEqualTo(2L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoMemoria.getTitulo(), Language.ES)).isEqualTo("Memoria002");
    Assertions.assertThat(tipoMemoria.getNumReferencia()).isEqualTo("ref-002");
  }

  @Test
  void addMemoria_ReturnsMemoria() throws Exception {

    Memoria nuevaMemoria = generarMockMemoria(1L, "ref-5588", "Memoria1", 1);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(headers, nuevaMemoria),
        Memoria.class);
  }

  @Test
  void removeMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 3L;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-BR")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Memoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  void removeMemoria_DoNotGetMemoria() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-BR")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Memoria.class, 20L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  void replaceMemoria_ReturnsMemoria() throws Exception {

    Memoria replaceMemoria = generarMockMemoria(2L, "ref-5588", "Memoria1", 1);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(headers, replaceMemoria), Memoria.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Memoria tipoMemoria = response.getBody();

    Assertions.assertThat(tipoMemoria.getId()).isNotNull();
    Assertions.assertThat(tipoMemoria.getTitulo()).isEqualTo(replaceMemoria.getTitulo());
  }

  @Test
  void findAll_WithPaging_ReturnsMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-VR", "ETI-MEM-V")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(5);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("16");

  }

  @Test
  void findAll_WithSearchQuery_ReturnsFilteredMemoriaList() throws Exception {
    // when: Búsqueda por titulo like e id equals
    Long id = 5L;
    String query = "titulo.value=ke=Memoria;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-VR", "ETI-MEM-V")));

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(1);
    Assertions.assertThat(tipoMemorias.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoMemorias.get(0).getTitulo(), Language.ES))
        .startsWith("Memoria");
  }

  @Test
  void findAll_WithSortQuery_ReturnsOrderedMemoriaList() throws Exception {
    // when: Ordenación por titulo desc
    String query = "titulo.value,desc";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();
    // when: Búsqueda por query
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-VR", "ETI-MEM-V")));

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(16);
    for (int i = 1; i < 15; i++) {
      MemoriaPeticionEvaluacion tipoMemoria = tipoMemorias.get(i);
      Assertions.assertThat(tipoMemoria.getId()).isEqualTo(17 - i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoMemoria.getTitulo(), Language.ES))
          .isEqualTo("Memoria" + String.format("%03d", 17 - i));
    }
  }

  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-VR", "ETI-MEM-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por titulo desc
    String sort = "titulo.value,desc";
    // when: Filtra por titulo like
    String filter = "titulo.value=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequestMemoriaPeticionEvaluacion(headers, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene titulo='Memoria009' a 'Memoria007'

    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoMemorias.get(0).getTitulo(), Language.ES))
        .isEqualTo("Memoria" + String.format("%03d", 9));
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoMemorias.get(1).getTitulo(),
        Language.ES)).isEqualTo("Memoria" + String.format("%03d", 8));
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoMemorias.get(2).getTitulo(),
        Language.ES)).isEqualTo("Memoria" + String.format("%03d", 7));
  }

  @Test
  void findAllMemoriasAsignablesConvocatoriaOrdExt_Unlimited_ReturnsMemoriaSubList() throws Exception {

    // given: idConvocatoria que es de tipo 1 (ordinaria) o 2 (extraordinaria)
    Long idConvocatoria = 2L;

    // when: Obtiene la memorias asignables para esa convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        }, idConvocatoria);

    // then: Respuesta OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(2);

    // Las memorias 10,11 y 12 tienen estado 3(En Secretaría) y su
    // fecha de envío es menor que la fecha límite por que son asignables.

    // Memoria 13 no tiene estado 3(En Secretaría) pero tiene retrospectiva de tipo
    // 3
    // (En Secretaría) por lo que sí es asignable.

    // Memoria 14 tiene estado 3(En Secretaría) pero su fecha de envío es menor que
    // la fecha límite, por lo que no es asignable.
    List<String> titulos = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      titulos.add(I18nHelper.getValueForLanguage(memorias.get(i).getTitulo(), Language.ES));
    }
    Assertions.assertThat(titulos).contains("Memoria011", "Memoria012");
  }

  @Test
  void findAllMemoriasAsignablesConvocatoriaOrdExt_WithPaging_ReturnsMemoriaSubList() throws Exception {

    // given: idConvocatoria que es de tipo 1 (ordinaria) o 2 (extraordinaria)
    Long idConvocatoria = 2L;

    // when: Obtiene la page=1 con pagesize=2 de la memorias asignables para esa
    // convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        }, idConvocatoria);

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(2);

  }

  @Test
  void findAllMemoriasAsignablesConvocatoriaSeg_WithPaging_ReturnsMemoriaSubList() throws Exception {

    // given: idConvocatoria que es de tipo 3 (Seguimiento)
    Long idConvocatoria = 3L;

    // when: Obtiene la page=1 con pagesize=2 de la memorias asignables para esa
    // convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        }, idConvocatoria);

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(2);

  }

  @Test
  void findAllMemoriasAsignablesConvocatoriaSeg_Unlimited_ReturnsMemoriaSubList() throws Exception {

    // given: idConvocatoria que es de tipo 3 (Seguimiento)
    Long idConvocatoria = 3L;

    // when: Obtiene memorias asignables para esa convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        }, idConvocatoria);

    // then: Respuesta OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(2);

  }

  @Test
  void findAllAsignablesTipoConvocatoriaOrdExt_Unlimited_ReturnsMemoriaSubList() throws Exception {

    // given: search query with comité y fecha límite de una convocatoria de tipo
    // ordinario o extraordinario
    String query = "comite.id==1;fechaEnvioSecretaria=le=2020-08-01T00:00:00Z";
    String sort = "titulo.value,asc";
    // String query = "comite.id:1";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES_ORDEXT)
        .queryParam("s", sort).queryParam("q", query).build(false).toUri();

    // when: find unlimited asignables para tipo convocatoria ordinaria o
    // extraordinaria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        });

    // then: Obtiene las
    // memorias en estado "En secretaria" con la fecha de envío es igual o menor a
    // la fecha límite de la convocatoria de reunión y las que tengan una
    // retrospectiva en estado "En secretaría".
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(2);

    // Las memorias 10,11 y 12 tienen estado 3(En Secretaría) y su
    // fecha de envío es menor que la fecha límite por que son asignables.
    // Memoria 14 tiene estado 3(En Secretaría) pero su fecha de envío es menor que
    // la fecha límite, por lo que no es asignable.
    Assertions.assertThat(I18nHelper.getValueForLanguage(memorias.get(0).getTitulo(), Language.ES))
        .isEqualTo("Memoria011");
    Assertions.assertThat(
        I18nHelper.getValueForLanguage(memorias.get(1).getTitulo(), Language.ES)).isEqualTo("Memoria012");
    // Assertions.assertThat(memorias.get(2).getTitulo()).isEqualTo("Memoria012");
  }

  @Test
  void findAllAsignablesTipoConvocatoriaSeguimiento_Unlimited_ReturnsMemoriaSubList() throws Exception {

    // given: search query with comité y fecha límite de una convocatoria de tipo
    // seguimiento

    String query = "comite.id==1;fechaEnvioSecretaria=le=2020-08-01T00:00:00Z";
    // String query = "comite.id:1";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES_SEG)
        .queryParam("q", query).build(false).toUri();

    // when: Obtiene memorias asignables para esa convocatoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E")));

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        });

    // then: Respuesta OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> memorias = response.getBody();
    Assertions.assertThat(memorias.size()).isEqualTo(3);

    // Las memorias 2, 4 y 6 tienen estados 12 y 17(En Secretaría
    // seguimiento anual/final) y su fecha de envío es menor que la fecha límite por
    // que son asignables.

    List<String> titulos = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      titulos.add(I18nHelper.getValueForLanguage(memorias.get(i).getTitulo(), Language.ES));
    }
    Assertions.assertThat(titulos).contains("Memoria002", "Memoria004", "Memoria006");

    // Memoria 8 tiene estado 17(En Secretaría seguimiento final) pero su fecha de
    // envío es menor que
    // la fecha límite, por lo que no es asignable.

  }

  @Test
  void getDocumentacionFormulario_Unlimited_ReturnsDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-formulario", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 2L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemoria = response.getBody();
    Assertions.assertThat(documentacionMemoria.size()).isEqualTo(5);

    Assertions.assertThat(documentacionMemoria.get(0).getDocumentoRef()).isEqualTo("doc-004");
    Assertions.assertThat(documentacionMemoria.get(1).getDocumentoRef()).isEqualTo("doc-005");
    Assertions.assertThat(documentacionMemoria.get(2).getDocumentoRef()).isEqualTo("doc-006");
    Assertions.assertThat(documentacionMemoria.get(3).getDocumentoRef()).isEqualTo("doc-007");
  }

  @Test
  void getDocumentacionFormulario_Unlimited_ReturnsEmptyDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-formulario", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 4L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Test
  void getDocumentacionSeguimientoAnual_Unlimited_ReturnsDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-anual", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 2L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemoria = response.getBody();
    Assertions.assertThat(documentacionMemoria.size()).isEqualTo(1);

    Assertions.assertThat(documentacionMemoria.get(0).getDocumentoRef()).isEqualTo("doc-001");
  }

  @Test
  void getDocumentacionSeguimientoAnual_Unlimited_ReturnsEmptyDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-anual", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 4L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Test
  void getDocumentacionSeguimientoFinal_Unlimited_ReturnsDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-final", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 2L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemoria = response.getBody();
    Assertions.assertThat(documentacionMemoria.size()).isEqualTo(1);

    Assertions.assertThat(documentacionMemoria.get(0).getDocumentoRef()).isEqualTo("doc-002");
  }

  @Test
  void getDocumentacionSeguimientoFinal_Unlimited_ReturnsEmptyDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-final", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 3L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Test
  void getDocumentacionRetrospectiva_Unlimited_ReturnsDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 9L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemoria = response.getBody();
    Assertions.assertThat(documentacionMemoria.size()).isEqualTo(1);

    Assertions.assertThat(documentacionMemoria.get(0).getDocumentoRef()).isEqualTo("doc-013");
  }

  @Test
  void getDocumentacionRetrospectiva_Unlimited_ReturnsEmptyDocumentacion() throws Exception {

    // when: find unlimited asignables para la memoria
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva", HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        }, 3L);

    // then: Obtiene la documentación de memoria que no se encuentra en estado 1,2 o
    // 3
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Test
  void addDocumentacioSeguimientoAnual_ReturnsDcoumentacionMemoria() throws Exception {

    DocumentacionMemoria nuevaDocumentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "Memoria1", 1), generarMockTipoDocumento(1L));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-anual", HttpMethod.POST,
        buildRequestDocumentacionMemoria(headers, nuevaDocumentacionMemoria), Memoria.class, 1L);
  }

  @Test
  void addDocumentacioRetrospectiva_ReturnsDcoumentacionMemoria() throws Exception {

    DocumentacionMemoria nuevaDocumentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "Memoria1", 1), generarMockTipoDocumento(1L));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva",
        HttpMethod.POST, buildRequestDocumentacionMemoria(headers, nuevaDocumentacionMemoria), Memoria.class, 1L);
  }

  @Test
  void getEvaluacionesMemoria_ReturnsEvaluadorSubList() throws Exception {

    // given: idMemoria
    Long idMemoria = 2L;
    // when: Busca las evaluaciones de la memoria 2L
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_EVALUACIONES, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        }, idMemoria);

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(6);

    // Contiene las evaluaciones con Id '2', '3', '4', '5', '6', '12'
    Assertions.assertThat(evaluaciones.get(0).getId()).isEqualTo(2L);
    Assertions.assertThat(evaluaciones.get(1).getId()).isEqualTo(3L);
    Assertions.assertThat(evaluaciones.get(2).getId()).isEqualTo(4L);
    Assertions.assertThat(evaluaciones.get(3).getId()).isEqualTo(5L);
    Assertions.assertThat(evaluaciones.get(4).getId()).isEqualTo(6L);
    Assertions.assertThat(evaluaciones.get(5).getId()).isEqualTo(12L);
  }

  @Test
  void deleteDocumentacionSeguimientoAnual_ReturnsMemoria() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-anual/{idDocumentacionMemoria}",
        HttpMethod.DELETE, buildRequestDocumentacionMemoria(headers, null), DocumentacionMemoria.class, 15L, 10L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void deleteDocumentacionSeguimientoFinal_ReturnsMemoria() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-final/{idDocumentacionMemoria}",
        HttpMethod.DELETE, buildRequestDocumentacionMemoria(headers, null), DocumentacionMemoria.class, 16L, 14L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getEvaluacionesMemoria_ReturnsEmptyList() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_EVALUACIONES, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        }, 12L);

    // then: La memoria no tiene evaluaciones
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void deleteDocumentacionRetrospectiva_ReturnsMemoria() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva/{idDocumentacionMemoria}",
        HttpMethod.DELETE, buildRequestDocumentacionMemoria(headers, null), DocumentacionMemoria.class, 16L, 11L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void deleteDocumentacionInicial_ReturnsMemoria() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ER")));

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-inicial/{idDocumentacionMemoria}",
        HttpMethod.DELETE, buildRequestDocumentacionMemoria(headers, null), DocumentacionMemoria.class, 3L, 9L);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void enviarSecretaria_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ESCR")));

    // when: Enviar secretaria con id existente
    long id = 9L;
    final ResponseEntity<Memoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/enviar-secretaria", HttpMethod.PUT,
        buildRequest(headers, null), Memoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void enviarSecretaria_DoNotGetMemoria() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ESCR")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/enviar-secretaria", HttpMethod.PUT,
        buildRequest(headers, null), Memoria.class, 20L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void enviarSecretariaRetrospectiva_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ERTR")));

    // when: Enviar secretaria con id existente
    long id = 16L;
    final ResponseEntity<Memoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/enviar-secretaria-retrospectiva", HttpMethod.PUT,
        buildRequest(headers, null), Memoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void enviarSecretariaRetrospectiva_DoNotGetMemoria() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEM-INV-ERTR")));

    final ResponseEntity<Memoria> response = restTemplate.exchange(
        MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/enviar-secretaria-retrospectiva", HttpMethod.PUT,
        buildRequest(headers, null), Memoria.class, 20L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  /**
   * Función que devuelve un objeto DocumentacionMemoria
   * 
   * @param id            id de DocumentacionMemoria
   * @param memoria       la Memoria de DocumentacionMemoria
   * @param tipoDocumento el TipoDocumento de DocumentacionMemoria
   * @return el objeto DocumentacionMemoria
   */

  private DocumentacionMemoria generarMockDocumentacionMemoria(Long id, Memoria memoria, TipoDocumento tipoDocumento) {

    Set<DocumentacionMemoriaNombre> nombre = new HashSet<>();
    nombre.add(new DocumentacionMemoriaNombre(Language.ES, "doc-00" + id));
    DocumentacionMemoria documentacionMemoria = new DocumentacionMemoria();
    documentacionMemoria.setId(id);
    documentacionMemoria.setMemoria(memoria);
    documentacionMemoria.setTipoDocumento(tipoDocumento);
    documentacionMemoria.setDocumentoRef("doc-00" + id);
    documentacionMemoria.setNombre(nombre);

    return documentacionMemoria;
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */

  private TipoDocumento generarMockTipoDocumento(Long id) {

    Set<TipoDocumentoNombre> nombre = new HashSet<>();
    nombre.add(new TipoDocumentoNombre(Language.ES, "TipoDocumento" + id));
    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);

    return tipoDocumento;
  }

  /**
   * Función que devuelve un objeto Memoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto tipo Memoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version) {

    Formulario formularioMemoria = new Formulario();
    formularioMemoria.setId(1L);
    formularioMemoria.setCodigo("M10/2020/001");
    formularioMemoria.setSeguimientoAnualDocumentacionTitle(Formulario.SeguimientoAnualDocumentacionTitle.TITULO_1);
    Formulario formularioSeguimientoAnual = new Formulario();
    formularioSeguimientoAnual.setId(4L);
    formularioSeguimientoAnual.setCodigo("SA/2020/001");
    Formulario formularioSeguimientoFinal = new Formulario();
    formularioSeguimientoFinal.setId(5L);
    formularioSeguimientoFinal.setCodigo("SF/2020/001");

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, titulo));
    Memoria memoria = new Memoria();
    memoria.setId(id);
    memoria.setNumReferencia(numReferencia);
    memoria.setPeticionEvaluacion(generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id));
    memoria.setComite(generarMockComite(id, "comite" + id, true));
    memoria.setFormulario(formularioMemoria);
    memoria.setFormularioSeguimientoAnual(formularioSeguimientoAnual);
    memoria.setFormularioSeguimientoFinal(formularioSeguimientoFinal);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-00" + id);
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE));
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.TRUE);
    memoria.setRetrospectiva(generarMockRetrospectiva(3L));
    memoria.setVersion(version);
    memoria.setActivo(Boolean.TRUE);

    return memoria;
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    Set<PeticionEvaluacionTitulo> tit = new HashSet<>();
    tit.add(new PeticionEvaluacionTitulo(Language.ES, titulo));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen" + id));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos" + id));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico" + id));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(tit);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String codigo, Boolean activo) {
    Comite comite = new Comite();
    comite.setId(id);
    comite.setCodigo(codigo);
    comite.setActivo(activo);

    return comite;

  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria.
   * 
   * @param id     identificador del TipoEstadoMemoria.
   * @param nombre nombre.
   * @param activo indicador de activo.
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoEstadoMemoria(id, nombre, activo);

  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(generarMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }

}
