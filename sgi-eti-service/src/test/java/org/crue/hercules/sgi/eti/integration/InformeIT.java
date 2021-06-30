package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
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
 * Test de integracion de Informe.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off  
  "classpath:scripts/formulario.sql", 
  "classpath:scripts/comite.sql",
  "classpath:scripts/tipo_actividad.sql",
  "classpath:scripts/tipo_memoria.sql", 
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/estado_retrospectiva.sql", 
  "classpath:scripts/retrospectiva.sql",
  "classpath:scripts/tipo_evaluacion.sql", 
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/memoria.sql",
  "classpath:scripts/informe.sql" 
// @formatter:on  
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class InformeIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String INFORME_CONTROLLER_BASE_PATH = "/informeformularios";

  private HttpEntity<Informe> buildRequest(HttpHeaders headers, Informe entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-INFORMEFORMULARIO-EDITAR", "ETI-INFORMEFORMULARIO-VER")));

    HttpEntity<Informe> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Test
  public void getInforme_WithId_ReturnsInforme() throws Exception {
    final ResponseEntity<Informe> response = restTemplate.exchange(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Informe.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Informe informe = response.getBody();

    Assertions.assertThat(informe.getId()).isEqualTo(1L);
    Assertions.assertThat(informe.getDocumentoRef()).isEqualTo("DocumentoFormulario1");
  }

  @Test
  public void addInforme_ReturnsInforme() throws Exception {
    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setActivo(true);
    tipoEvaluacion.setNombre("Memoria");

    Informe nuevoInforme = new Informe();
    nuevoInforme.setDocumentoRef("DocumentoFormulario1");
    nuevoInforme.setTipoEvaluacion(tipoEvaluacion);

    restTemplate.exchange(INFORME_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, nuevoInforme),
        Informe.class);
  }

  @Test
  public void removeInforme_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Informe> response = restTemplate.exchange(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Informe.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeInforme_DoNotGetInforme() throws Exception {

    final ResponseEntity<Informe> response = restTemplate.exchange(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Informe.class, 9L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceInforme_ReturnsInforme() throws Exception {

    long id = 1L;
    Informe replaceInforme = generarMockInforme(id, "DocumentoFormulario1");

    final ResponseEntity<Informe> response = restTemplate.exchange(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, replaceInforme), Informe.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Informe informe = response.getBody();

    Assertions.assertThat(informe.getId()).isNotNull();
    Assertions.assertThat(informe.getDocumentoRef()).isEqualTo(replaceInforme.getDocumentoRef());
  }

  @Test
  public void findAll_WithPaging_ReturnsInformeSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<Informe>> response = restTemplate.exchange(INFORME_CONTROLLER_BASE_PATH, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Informe>>() {
        });

    // then: Respuesta OK, Informes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Informe> informes = response.getBody();
    Assertions.assertThat(informes.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de documentoRef='DocumentoFormulario6' a 'DocumentoFormulario8'
    Assertions.assertThat(informes.get(0).getDocumentoRef()).isEqualTo("DocumentoFormulario6");
    Assertions.assertThat(informes.get(1).getDocumentoRef()).isEqualTo("DocumentoFormulario7");
    Assertions.assertThat(informes.get(2).getDocumentoRef()).isEqualTo("DocumentoFormulario8");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredInformeList() throws Exception {
    // when: Búsqueda por documentoRef like e id equals
    Long id = 5L;
    String query = "documentoRef=ke=DocumentoFormulario;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(INFORME_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Informe>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Informe>>() {
        });

    // then: Respuesta OK, DocumentoFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Informe> informes = response.getBody();
    Assertions.assertThat(informes.size()).isEqualTo(1);
    Assertions.assertThat(informes.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(informes.get(0).getDocumentoRef()).startsWith("DocumentoFormulario");
  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedInformeList() throws Exception {
    // when: Ordenación por documentoRef desc
    String query = "documentoRef,desc";

    URI uri = UriComponentsBuilder.fromUriString(INFORME_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Informe>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Informe>>() {
        });

    // then: Respuesta OK, DocumentoFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Informe> informes = response.getBody();
    Assertions.assertThat(informes.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Informe informe = informes.get(i);
      Assertions.assertThat(informe.getId()).isEqualTo(8 - i);
      Assertions.assertThat(informe.getDocumentoRef()).isEqualTo("DocumentoFormulario" + String.format("%d", 8 - i));
    }
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsInformeSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por documentoRef desc
    String sort = "documentoRef,desc";
    // when: Filtra por documentoRef like e id equals
    String filter = "documentoRef=ke=Documento";

    URI uri = UriComponentsBuilder.fromUriString(INFORME_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Informe>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Informe>>() {
        });

    // then: Respuesta OK, Informes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Informe> informes = response.getBody();
    Assertions.assertThat(informes.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene documentoRef='DocumentoFormulario8' a 'DocumentoFormulario6'
    Assertions.assertThat(informes.get(0).getDocumentoRef()).isEqualTo("DocumentoFormulario" + String.format("%d", 8));
    Assertions.assertThat(informes.get(1).getDocumentoRef()).isEqualTo("DocumentoFormulario" + String.format("%d", 7));
    Assertions.assertThat(informes.get(2).getDocumentoRef()).isEqualTo("DocumentoFormulario" + String.format("%d", 6));
  }

  /**
   * Función que devuelve un objeto Informe
   * 
   * @param id           id del Informe
   * @param documentoRef la referencia del documento
   * @return el objeto Informe
   */

  public Informe generarMockInforme(Long id, String documentoRef) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(2L);
    peticionEvaluacion.setCodigo("Codigo");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion2");
    peticionEvaluacion.setPersonaRef("user-002");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(id);
    tipoMemoria.setNombre("TipoMemoria001");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(2L, "numRef-002", peticionEvaluacion, comite, "Memoria" + id, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), Instant.now()), 3,
        "CodOrganoCompetente", Boolean.TRUE, null);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setActivo(true);
    tipoEvaluacion.setNombre("Memoria");

    Informe informe = new Informe();
    informe.setId(id);
    informe.setDocumentoRef(documentoRef);
    informe.setMemoria(memoria);
    informe.setVersion(3);
    informe.setTipoEvaluacion(tipoEvaluacion);

    return informe;
  }

}