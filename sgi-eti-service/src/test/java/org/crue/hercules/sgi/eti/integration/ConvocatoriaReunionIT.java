package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.AsistentesMotivo;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteNombre;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunionLugar;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunionOrdenDia;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.EvaluadorResumen;
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
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 * Test de integracion de ConvocatoriaReunion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off    
  "classpath:scripts/tipo_actividad.sql",
  "classpath:scripts/formulario.sql",
  "classpath:scripts/comite.sql", 
  "classpath:scripts/peticion_evaluacion.sql", 
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/estado_retrospectiva.sql", 
  "classpath:scripts/retrospectiva.sql", 
  "classpath:scripts/memoria.sql", 
  "classpath:scripts/tipo_evaluacion.sql",
  "classpath:scripts/dictamen.sql", 
  "classpath:scripts/tipo_convocatoria_reunion.sql", 
  "classpath:scripts/cargo_comite.sql",
  "classpath:scripts/evaluador.sql", 
  "classpath:scripts/tipo_estado_acta.sql", 
  "classpath:scripts/convocatoria_reunion.sql",
  "classpath:scripts/acta.sql",
  "classpath:scripts/asistentes.sql",
  "classpath:scripts/evaluacion.sql"
// @formatter:on    
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class ConvocatoriaReunionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH = "/convocatoriareuniones";
  private static final String PATH_PARAMETER_BY_EVALUACIONES = "/evaluaciones";
  private static final String PATH_PARAMETER_WITH_DATOS_GENERALES = "/datos-generales";

  private HttpEntity<ConvocatoriaReunion> buildRequest(HttpHeaders headers, ConvocatoriaReunion entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<ConvocatoriaReunion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void create_ReturnsConvocatoriaReunion() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C")));

    // given: Nueva entidad
    final ConvocatoriaReunion newConvocatoriaReunion = getMockData(1L, 1L, 1L);
    newConvocatoriaReunion.setAnio(Instant.now().atZone(ZoneOffset.UTC).get(ChronoField.YEAR));
    newConvocatoriaReunion.setId(null);
    newConvocatoriaReunion.setNumeroActa(6L);

    // when: Se crea la entidad
    final ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(headers, newConvocatoriaReunion),
        ConvocatoriaReunion.class);

    // then: La entidad se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final ConvocatoriaReunion convocatoriaReunion = response.getBody();
    Assertions.assertThat(convocatoriaReunion.getId()).isNotNull();
    newConvocatoriaReunion.setId(convocatoriaReunion.getId());
    Assertions.assertThat(convocatoriaReunion).isEqualTo(newConvocatoriaReunion);
  }

  @Test
  public void update_WithExistingId_ReturnsConvocatoriaReunion() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-E")));

    // given: Entidad existente que se va a actualizar
    final ConvocatoriaReunion updatedConvocatoriaReunion = getMockData(2L, 1L, 2L);
    updatedConvocatoriaReunion.setId(3L);

    // when: Se actualiza la entidad
    final ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(headers, updatedConvocatoriaReunion), ConvocatoriaReunion.class,
        updatedConvocatoriaReunion.getId());

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(updatedConvocatoriaReunion);
  }

  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-V", "ETI-CNV-B")));

    // given: Entidad existente con la propiedad activo a true
    Long id = 2L;

    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(headers, null),
        ConvocatoriaReunion.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.TRUE);

    // when: Se elimina la entidad
    response = restTemplate.exchange(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(headers, null), ConvocatoriaReunion.class, id);

    // then: La entidad pasa a tener propiedad activo a false
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    response = restTemplate.exchange(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(headers, null), ConvocatoriaReunion.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void findById_WithExistingId_ReturnsConvocatoriaReunion() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-V", "ETI-CNV-E")));

    // given: Entidad con un determinado Id
    final ConvocatoriaReunion convocatoriaReunion = getMockData(2L, 1L, 2L);

    // when: Se busca la entidad por ese Id
    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(headers, null),
        ConvocatoriaReunion.class, convocatoriaReunion.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(convocatoriaReunion);
  }

  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-V", "ETI-CNV-E")));

    // given: No existe entidad con el id indicado
    Long id = 1L;

    // when: Se busca la entidad por ese Id
    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(headers, null),
        ConvocatoriaReunion.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void findByIdWithDatosGenerales_WithExistingId_ReturnsConvocatoriaReunionDatosGenerales() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-V", "ETI-CNV-E")));

    // given: Entidad con un determinado Id
    final ConvocatoriaReunionDatosGenerales convocatoriaReunion = new ConvocatoriaReunionDatosGenerales(
        getMockData(3L, 1L, 1L), 1L, null);
    convocatoriaReunion.setNumEvaluaciones(2);

    // when: Se busca la entidad por ese Id
    ResponseEntity<ConvocatoriaReunionDatosGenerales> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_WITH_DATOS_GENERALES,
        HttpMethod.GET, buildRequest(headers, null), ConvocatoriaReunionDatosGenerales.class,
        convocatoriaReunion.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(convocatoriaReunion);
  }

  @Test
  public void findByIdWithDatosGenerales_WithNotExistingId_Returns404() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-V", "ETI-CNV-E")));

    // given: No existe entidad con el id indicado
    Long id = 1L;

    // when: Se busca la entidad por ese Id
    ResponseEntity<ConvocatoriaReunionDatosGenerales> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_WITH_DATOS_GENERALES,
        HttpMethod.GET, buildRequest(headers, null), ConvocatoriaReunionDatosGenerales.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullConvocatoriaReunionList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    // response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));
    response.add(getMockData(3L, 1L, 1L));
    response.add(getMockData(4L, 1L, 2L));
    response.add(getMockData(5L, 1L, 1L));

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // when: Se buscan todos los datos
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
  }

  @Test
  public void findAll_WithPaging_ReturnsConvocatoriaReunionSubList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(3L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // when: Ordenación por id asc
    String sort = "id,desc";

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(2);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredConvocatoriaReunionList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(3L, 1L, 1L));

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // search by codigo like, id equals
    Long id = 3L;
    String query = "numeroActa=lt=4;id==" + id;

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

  }

  @Test
  public void findAll_WithSortQuery_ReturnsOrderedConvocatoriaReunionList() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // sort by id desc
    String sort = "id,desc";

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().get(0).getId()).isEqualTo(5L);
    Assertions.assertThat(result.getBody().get(3).getId()).isEqualTo(2L);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("4");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("4");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");
  }

  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsConvocatoriaReunionSubList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(3L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // sort desc
    String sort = "id,desc";

    // search
    String query = "numeroActa=lt=4";

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).query(query)
        .queryParam("s", sort).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).as("size").isEqualTo(2);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).as("X-Page").isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).as("X-Page-Total-Count").isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).as("X-Page-Count").isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("4");
  }

  @Test
  public void findAsistentes_Unlimited_ReturnsFullAsistentesList() throws Exception {

    // given: Datos existentes
    Long convocatoriaReunionId = 2L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append("/asistentes").toString();
    // @formatter:on

    List<Asistentes> result = new LinkedList<>();
    result.add(generarMockAsistentes(2L, 2L, convocatoriaReunionId));
    result.add(generarMockAsistentes(3L, 2L, convocatoriaReunionId));
    result.add(generarMockAsistentes(4L, 2L, convocatoriaReunionId));
    result.add(generarMockAsistentes(5L, 2L, convocatoriaReunionId));
    result.add(generarMockAsistentes(6L, 2L, convocatoriaReunionId));

    // when: Se buscan todos los datos

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan todos los datos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
  }

  @Test
  public void findAsistentes_WithPaging_ReturnsAsistentesSubList() throws Exception {

    Long convocatoriaReunionId = 2L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append("/asistentes").toString();
    // @formatter:on

    List<Asistentes> result = new LinkedList<>();
    result.add(generarMockAsistentes(1L, 1L, convocatoriaReunionId));
    result.add(generarMockAsistentes(2L, 1L, convocatoriaReunionId));
    result.add(generarMockAsistentes(3L, 1L, convocatoriaReunionId));
    result.add(generarMockAsistentes(4L, 2L, convocatoriaReunionId));
    result.add(generarMockAsistentes(5L, 2L, convocatoriaReunionId));

    // página 1 con 2 elementos por página

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    Pageable pageable = PageRequest.of(1, 2);
    Page<Asistentes> pageResult = new PageImpl<>(result.subList(3, 5), pageable, result.size());
    // when: Se buscan los datos paginados
    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page"))
        .isEqualTo(String.valueOf(pageResult.getPageable().getPageNumber()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size"))
        .isEqualTo(String.valueOf(pageResult.getPageable().getPageSize()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Total-Count"))
        .isEqualTo(String.valueOf(pageResult.getNumberOfElements()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Count"))
        .isEqualTo(String.valueOf(pageResult.getTotalPages()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count"))
        .isEqualTo(String.valueOf(pageResult.getTotalElements()));
    Assertions.assertThat(response.getBody().size()).isEqualTo(pageResult.getContent().size());
    Assertions.assertThat(response.getBody()).isEqualTo(pageResult.getContent());
  }

  @Test
  public void findEvaluacionesActivas_Unlimited_ReturnsFullEvaluacionList() throws Exception {

    // given: Datos existentes
    Long convocatoriaReunionId = 2L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append("/evaluaciones-activas").toString();
    // @formatter:on

    List<Evaluacion> result = new ArrayList<>();
    Evaluacion evaluacion3 = generarMockEvaluacion(Long.valueOf(3), 1, 7);
    evaluacion3.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion3);
    Evaluacion evaluacion4 = generarMockEvaluacion(Long.valueOf(4), 4, 7);
    evaluacion4.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion4);
    Evaluacion evaluacion5 = generarMockEvaluacion(Long.valueOf(5), 5, 7);
    evaluacion5.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion5);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));

    // when: Se buscan todos los datos
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan todos los datos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
  }

  @Test
  public void findEvaluacionesActivas_WithPaging_ReturnsEvaluacionSubList() throws Exception {

    Long convocatoriaReunionId = 2L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID)
        .append("/evaluaciones-activas").toString();
    // @formatter:on

    List<Evaluacion> result = new LinkedList<>();
    Evaluacion evaluacion1 = generarMockEvaluacion(Long.valueOf(1), 3, 7);
    evaluacion1.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion1);
    Evaluacion evaluacion2 = generarMockEvaluacion(Long.valueOf(3), 1, 7);
    evaluacion2.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion2);
    Evaluacion evaluacion3 = generarMockEvaluacion(Long.valueOf(5), 5, 7);
    evaluacion3.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion3);

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResult = new PageImpl<>(result.subList(2, 3), pageable, result.size());

    // when: Se buscan los datos paginados
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page"))
        .isEqualTo(String.valueOf(pageResult.getPageable().getPageNumber()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size"))
        .isEqualTo(String.valueOf(pageResult.getPageable().getPageSize()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Total-Count"))
        .isEqualTo(String.valueOf(pageResult.getNumberOfElements()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Count"))
        .isEqualTo(String.valueOf(pageResult.getTotalPages()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count"))
        .isEqualTo(String.valueOf(pageResult.getTotalElements()));
    Assertions.assertThat(response.getBody().size()).isEqualTo(pageResult.getContent().size());
    Assertions.assertThat(response.getBody()).isEqualTo(pageResult.getContent());

  }

  @Test
  public void getEvaluaciones_Unlimited_ReturnsFullEvaluacionList() throws Exception {

    // given: Datos existentes
    Long convocatoriaReunionId = 2L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append(PATH_PARAMETER_BY_EVALUACIONES)
        .toString();
    // @formatter:on

    List<Evaluacion> result = new LinkedList<>();
    Evaluacion evaluacion3 = generarMockEvaluacion(Long.valueOf(3), 1, 7);
    evaluacion3.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion3);
    Evaluacion evaluacion4 = generarMockEvaluacion(Long.valueOf(4), 4, 7);
    evaluacion4.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion4);
    Evaluacion evaluacion5 = generarMockEvaluacion(Long.valueOf(5), 5, 7);
    evaluacion5.setEsRevMinima(Boolean.FALSE);
    result.add(evaluacion5);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    // when: Se buscan todos los datos
    ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan todos los datos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
  }

  @Test
  public void getEvaluaciones_WithPaging_ReturnsEvaluacionSubList() throws Exception {

    Long convocatoriaReunionId = 3L;
    // @formatter:off
    final String url = new StringBuffer(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .append(PATH_PARAMETER_ID).append(PATH_PARAMETER_BY_EVALUACIONES)
        .toString();
    // @formatter:on

    List<Evaluacion> result = new ArrayList<>();
    Evaluacion evaluacion12 = generarMockEvaluacionId(Long.valueOf(12));
    evaluacion12.setEsRevMinima(Boolean.FALSE);
    Evaluacion evaluacion13 = generarMockEvaluacionId(Long.valueOf(13));
    evaluacion13.getMemoria().setId(17L);
    evaluacion13.getMemoria().setNumReferencia("ref-017");
    evaluacion13.getMemoria().getTitulo().stream().findFirst().get().setValue("Memoria017");
    evaluacion13.getMemoria().setPersonaRef("userref-017");
    evaluacion13.getMemoria().getComite().setId(2L);
    evaluacion13.getMemoria().getComite().setCodigo("CEEA");
    evaluacion13.getMemoria().getComite().setFormularioMemoriaId(2L);
    evaluacion13.getMemoria().getComite().setPrefijoReferencia("M20");
    evaluacion13.getMemoria().getComite().setRequiereRetrospectiva(Boolean.TRUE);
    evaluacion13.getMemoria().getComite().setPermitirRatificacion(Boolean.FALSE);
    evaluacion13.getMemoria().getComite().setTareaNombreLibre(Boolean.FALSE);
    evaluacion13.getMemoria().getComite().setTareaExperienciaLibre(Boolean.FALSE);
    evaluacion13.getMemoria().getComite().setTareaExperienciaDetalle(Boolean.TRUE);
    evaluacion13.getMemoria().getComite().setMemoriaTituloLibre(Boolean.TRUE);
    evaluacion13.getMemoria().getFormulario().setId(2L);
    evaluacion13.getMemoria().getFormulario().setCodigo("M20/2020/001");
    evaluacion13.getMemoria().getFormulario()
        .setSeguimientoAnualDocumentacionTitle(Formulario.SeguimientoAnualDocumentacionTitle.TITULO_1);
    evaluacion13.getMemoria().setRequiereRetrospectiva(Boolean.TRUE);
    evaluacion13.getMemoria()
        .setFormularioRetrospectiva(new Formulario(6L, Formulario.Tipo.RETROSPECTIVA, "R/2020/001", null));
    evaluacion13.getMemoria().getEstadoActual().setId(13L);
    evaluacion13.getMemoria().getEstadoActual().setNombre("En evaluación seguimiento anual");
    evaluacion13.getMemoria().setFechaEnvioSecretaria(null);
    evaluacion13.getMemoria().setRetrospectiva(new Retrospectiva(4L,
        new EstadoRetrospectiva(2L, "EstadoRetrospectiva2", Boolean.TRUE), Instant.parse("2020-07-04T00:00:00Z")));
    evaluacion13.getMemoria().setVersion(1);
    evaluacion13.setVersion(1);
    evaluacion13.setEsRevMinima(Boolean.FALSE);
    evaluacion13.getTipoEvaluacion().setId(3L);
    evaluacion13.getTipoEvaluacion().setNombre("TipoEvaluacion3");

    result.add(evaluacion12);
    result.add(evaluacion13);

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "2");

    Pageable pageable = PageRequest.of(0, 2);
    Page<Evaluacion> pageResult = new PageImpl<>(result.subList(0, 2), pageable, result.size());

    // when: Se buscan los datos paginados
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page"))
        .isEqualTo(String.valueOf(pageResult.getPageable().getPageNumber()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size"))
        .isEqualTo(String.valueOf(pageResult.getPageable().getPageSize()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Total-Count"))
        .isEqualTo(String.valueOf(pageResult.getNumberOfElements()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Count"))
        .isEqualTo(String.valueOf(pageResult.getTotalPages()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count"))
        .isEqualTo(String.valueOf(pageResult.getTotalElements()));
    Assertions.assertThat(response.getBody().size()).isEqualTo(pageResult.getContent().size());
    Assertions.assertThat(response.getBody()).isEqualTo(pageResult.getContent());
  }

  @Test
  public void findConvocatoriasSinActa() throws Exception {
    // when: Obtiene page=1 con pagesize=5
    HttpHeaders headers = new HttpHeaders();

    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + "/acta-no-asignada")
        .build(false).toUri();

    final ResponseEntity<List<ConvocatoriaReunion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Respuesta OK

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().size()).isEqualTo(3);
  }

  /**
   * Genera un objeto {@link ConvocatoriaReunion}
   *
   * @param id
   * @param comiteId
   * @param tipoId
   * @return ConvocatoriaReunion
   */
  private ConvocatoriaReunion getMockData(Long id, Long comiteId, Long tipoId) {

    Set<ComiteNombre> nombre = new HashSet<>();
    nombre.add(new ComiteNombre(Language.ES, "nombreInvestigacion", ComiteNombre.Genero.M));
    Comite comite = new Comite();
    comite.setId(comiteId);
    comite.setCodigo("CEI");
    comite.setNombre(nombre);
    comite.setFormularioMemoriaId(1L);
    comite.setFormularioSeguimientoAnualId(4L);
    comite.setFormularioSeguimientoFinalId(5L);
    comite.setFormularioRetrospectivaId(6L);
    comite.setRequiereRetrospectiva(Boolean.FALSE);
    comite.setPrefijoReferencia("M10");
    comite.setPermitirRatificacion(Boolean.TRUE);
    comite.setTareaNombreLibre(Boolean.TRUE);
    comite.setTareaExperienciaLibre(Boolean.TRUE);
    comite.setTareaExperienciaDetalle(Boolean.FALSE);
    comite.setMemoriaTituloLibre(Boolean.FALSE);
    comite.setActivo(Boolean.TRUE);

    String tipo_txt = (tipoId == 1L) ? "Ordinaria" : (tipoId == 2L) ? "Extraordinaria" : "Seguimiento";
    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(tipoId, tipo_txt, Boolean.TRUE);

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    Set<ConvocatoriaReunionLugar> lugar = new HashSet<>();
    lugar.add(new ConvocatoriaReunionLugar(Language.ES, "Lugar " + txt));
    Set<ConvocatoriaReunionOrdenDia> ordenDia = new HashSet<>();
    ordenDia.add(new ConvocatoriaReunionOrdenDia(Language.ES,
        "Orden del día convocatoria reunión " + txt));
    final ConvocatoriaReunion data = new ConvocatoriaReunion();
    data.setId(id);
    data.setComite(comite);
    data.setFechaEvaluacion(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());
    data.setFechaLimite(
        LocalDate.of(2020, 8, id.intValue()).atStartOfDay(ZoneOffset.UTC).with(LocalTime.of(23, 59, 59)).toInstant());
    data.setVideoconferencia(false);
    data.setLugar(lugar);
    data.setOrdenDia(ordenDia);
    data.setAnio(2020);
    data.setNumeroActa(id);
    data.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    data.setHoraInicio(7 + id.intValue());
    data.setMinutoInicio(30);
    data.setFechaEnvio(Instant.parse("2020-07-13T00:00:00Z"));
    data.setActivo(Boolean.TRUE);

    return data;
  }

  /**
   * Función que devuelve un objeto Asistentes
   *
   * @param id                    id del asistentes
   * @param evaluadorId           id del evaluador
   * @param convocatoriaReunionId id de la convocatoria
   * @return el objeto Asistentes
   */

  private Asistentes generarMockAsistentes(Long id, Long evaluadorId, Long convocatoriaReunionId) {

    Set<AsistentesMotivo> motivo = new HashSet<>();
    motivo.add(new AsistentesMotivo(Language.ES, "Motivo" + id));
    Asistentes asistentes = new Asistentes();
    asistentes.setId(id);
    asistentes.setEvaluador(generarMockEvaluador(evaluadorId));
    asistentes.setConvocatoriaReunion(getMockData(convocatoriaReunionId, 1L, 2L));
    asistentes.setMotivo(motivo);
    asistentes.setAsistencia(Boolean.TRUE);

    return asistentes;
  }

  /**
   * Función que devuelve un objeto Evaluador
   *
   * @param id id del Evaluador
   * @return el objeto Evaluador
   */

  private Evaluador generarMockEvaluador(Long id) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("PRESIDENTE");
    cargoComite.setActivo(Boolean.TRUE);

    Set<ComiteNombre> nombre = new HashSet<>();
    nombre.add(new ComiteNombre(Language.ES, "nombreInvestigacion", ComiteNombre.Genero.M));
    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("CEI");
    comite.setNombre(nombre);
    comite.setFormularioMemoriaId(1L);
    comite.setFormularioSeguimientoAnualId(4L);
    comite.setFormularioSeguimientoFinalId(5L);
    comite.setFormularioRetrospectivaId(6L);
    comite.setRequiereRetrospectiva(Boolean.FALSE);
    comite.setPrefijoReferencia("M10");
    comite.setPermitirRatificacion(Boolean.TRUE);
    comite.setTareaNombreLibre(Boolean.TRUE);
    comite.setTareaExperienciaLibre(Boolean.TRUE);
    comite.setTareaExperienciaDetalle(Boolean.FALSE);
    comite.setMemoriaTituloLibre(Boolean.FALSE);
    comite.setActivo(Boolean.TRUE);

    Set<EvaluadorResumen> resumen = new HashSet<>();
    resumen.add(new EvaluadorResumen(Language.ES, "Evaluador" + id));
    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.parse("2020-07-01T00:00:00Z"));
    evaluador.setFechaBaja(Instant.parse("2021-07-01T23:59:59Z"));
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-00" + id);
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Función que devuelve un objeto Evaluacion
   *
   * @param id             id del Evaluacion
   * @param sufijo         el sufijo para título y nombre
   * @param version        version evaluación
   * @param versionMemoria version memoria
   * @return el objeto Evaluacion
   */

  private Evaluacion generarMockEvaluacion(Long id, Integer version, Integer versionMemoria) {

    TipoEvaluacion tipoEvaluacionDictamen = new TipoEvaluacion(2L, "TipoEvaluacion2", Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(1L);
    dictamen.setNombre("Favorable");
    dictamen.setTipoEvaluacion(tipoEvaluacionDictamen);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("Proyecto de investigacion");
    tipoActividad.setActivo(Boolean.TRUE);

    Set<PeticionEvaluacionTitulo> peTitulo = new HashSet<>();
    peTitulo.add(new PeticionEvaluacionTitulo(Language.ES, "PeticionEvaluacion2"));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen"));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos"));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "Metodologico"));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(2L);
    peticionEvaluacion.setCodigo("Codigo");
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.parse("2021-07-09T23:59:59Z"));
    peticionEvaluacion.setFechaInicio(Instant.parse("2020-07-09T00:00:00Z"));
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(peTitulo);
    peticionEvaluacion.setPersonaRef("user");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Set<ComiteNombre> nombre = new HashSet<>();
    nombre.add(new ComiteNombre(Language.ES, "nombreInvestigacion", ComiteNombre.Genero.M));
    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("CEI");
    comite.setNombre(nombre);
    comite.setFormularioMemoriaId(1L);
    comite.setFormularioSeguimientoAnualId(4L);
    comite.setFormularioSeguimientoFinalId(5L);
    comite.setFormularioRetrospectivaId(6L);
    comite.setRequiereRetrospectiva(Boolean.FALSE);
    comite.setPrefijoReferencia("M10");
    comite.setPermitirRatificacion(Boolean.TRUE);
    comite.setTareaNombreLibre(Boolean.TRUE);
    comite.setTareaExperienciaLibre(Boolean.TRUE);
    comite.setTareaExperienciaDetalle(Boolean.FALSE);
    comite.setMemoriaTituloLibre(Boolean.FALSE);
    comite.setActivo(Boolean.TRUE);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(12L);
    tipoEstadoMemoria.setNombre("En secretaría seguimiento anual");
    tipoEstadoMemoria.setActivo(Boolean.TRUE);

    Formulario formularioMemoria = new Formulario();
    formularioMemoria.setId(1L);
    formularioMemoria.setTipo(Formulario.Tipo.MEMORIA);
    formularioMemoria.setCodigo("M10/2020/001");
    formularioMemoria.setSeguimientoAnualDocumentacionTitle(Formulario.SeguimientoAnualDocumentacionTitle.TITULO_1);
    Formulario formularioSeguimientoAnual = new Formulario();
    formularioSeguimientoAnual.setId(4L);
    formularioSeguimientoAnual.setTipo(Formulario.Tipo.SEGUIMIENTO_ANUAL);
    formularioSeguimientoAnual.setCodigo("SA/2020/001");
    Formulario formularioSeguimientoFinal = new Formulario();
    formularioSeguimientoFinal.setId(5L);
    formularioSeguimientoFinal.setTipo(Formulario.Tipo.SEGUIMIENTO_FINAL);
    formularioSeguimientoFinal.setCodigo("SF/2020/001");

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria002"));
    Memoria memoria = new Memoria();
    memoria.setId(2L);
    memoria.setNumReferencia("ref-002");
    memoria.setPeticionEvaluacion(peticionEvaluacion);
    memoria.setComite(comite);
    memoria.setFormulario(formularioMemoria);
    memoria.setFormularioSeguimientoAnual(formularioSeguimientoAnual);
    memoria.setFormularioSeguimientoFinal(formularioSeguimientoFinal);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("userref-002");
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setFechaEnvioSecretaria(Instant.parse("2020-08-01T00:00:00Z"));
    memoria.setRequiereRetrospectiva(Boolean.FALSE);
    memoria.setVersion(versionMemoria);
    memoria.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(2L);
    tipoEvaluacion.setNombre("TipoEvaluacion2");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.parse("2020-07-13T00:00:00Z"));
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(getMockData(2L, 1L, 2L));
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(generarMockEvaluador(2L));
    evaluacion.setEvaluador2(generarMockEvaluador(3L));
    evaluacion.setVersion(version);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

  /**
   * Función que devuelve un objeto Evaluacion
   *
   * @param id id del Evaluacion
   * @return el objeto Evaluacion
   */

  private Evaluacion generarMockEvaluacionId(Long id) {

    TipoEvaluacion tipoEvaluacionDictamen = new TipoEvaluacion(2L, "TipoEvaluacion2", Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(1L);
    dictamen.setNombre("Favorable");
    dictamen.setTipoEvaluacion(tipoEvaluacionDictamen);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("Proyecto de investigacion");
    tipoActividad.setActivo(Boolean.TRUE);

    Set<PeticionEvaluacionTitulo> peTitulo = new HashSet<>();
    peTitulo.add(new PeticionEvaluacionTitulo(Language.ES, "PeticionEvaluacion2"));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen"));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos"));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "Metodologico"));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(2L);
    peticionEvaluacion.setCodigo("Codigo");
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.parse("2021-07-09T23:59:59Z"));
    peticionEvaluacion.setFechaInicio(Instant.parse("2020-07-09T00:00:00Z"));
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(peTitulo);
    peticionEvaluacion.setPersonaRef("user");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Set<ComiteNombre> nombre = new HashSet<>();
    nombre.add(new ComiteNombre(Language.ES, "nombreInvestigacion", ComiteNombre.Genero.M));
    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("CEI");
    comite.setNombre(nombre);
    comite.setFormularioMemoriaId(1L);
    comite.setFormularioSeguimientoAnualId(4L);
    comite.setFormularioSeguimientoFinalId(5L);
    comite.setFormularioRetrospectivaId(6L);
    comite.setRequiereRetrospectiva(Boolean.FALSE);
    comite.setPrefijoReferencia("M10");
    comite.setPermitirRatificacion(Boolean.TRUE);
    comite.setTareaNombreLibre(Boolean.TRUE);
    comite.setTareaExperienciaLibre(Boolean.TRUE);
    comite.setTareaExperienciaDetalle(Boolean.FALSE);
    comite.setMemoriaTituloLibre(Boolean.FALSE);
    comite.setActivo(Boolean.TRUE);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(12L);
    tipoEstadoMemoria.setNombre("En secretaría seguimiento anual");
    tipoEstadoMemoria.setActivo(Boolean.TRUE);

    Formulario formularioMemoria = new Formulario();
    formularioMemoria.setId(1L);
    formularioMemoria.setTipo(Formulario.Tipo.MEMORIA);
    formularioMemoria.setCodigo("M10/2020/001");
    formularioMemoria.setSeguimientoAnualDocumentacionTitle(Formulario.SeguimientoAnualDocumentacionTitle.TITULO_1);
    Formulario formularioSeguimientoAnual = new Formulario();
    formularioSeguimientoAnual.setId(4L);
    formularioSeguimientoAnual.setTipo(Formulario.Tipo.SEGUIMIENTO_ANUAL);
    formularioSeguimientoAnual.setCodigo("SA/2020/001");
    Formulario formularioSeguimientoFinal = new Formulario();
    formularioSeguimientoFinal.setId(5L);
    formularioSeguimientoFinal.setTipo(Formulario.Tipo.SEGUIMIENTO_FINAL);
    formularioSeguimientoFinal.setCodigo("SF/2020/001");

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria002"));
    Memoria memoria = new Memoria();
    memoria.setId(2L);
    memoria.setNumReferencia("ref-002");
    memoria.setPeticionEvaluacion(peticionEvaluacion);
    memoria.setComite(comite);
    memoria.setFormulario(formularioMemoria);
    memoria.setFormularioSeguimientoAnual(formularioSeguimientoAnual);
    memoria.setFormularioSeguimientoFinal(formularioSeguimientoFinal);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("userref-002");
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setFechaEnvioSecretaria(Instant.parse("2020-08-01T00:00:00Z"));
    memoria.setRequiereRetrospectiva(Boolean.FALSE);
    memoria.setVersion(7);
    memoria.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.parse("2020-07-10T00:00:00Z"));
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(getMockData(3L, 1L, 1L));
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(generarMockEvaluador(2L));
    evaluacion.setEvaluador2(generarMockEvaluador(3L));
    evaluacion.setVersion(7);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

}
