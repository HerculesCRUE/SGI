package org.crue.hercules.sgi.eti.integration;

import java.time.Instant;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;

/**
 * Test de integracion de ConflictoInteres.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off      
  "classpath:scripts/formulario.sql",
  "classpath:scripts/comite.sql", 
  "classpath:scripts/cargo_comite.sql",
  "classpath:scripts/evaluador.sql",
  "classpath:scripts/conflicto_interes.sql"
// @formatter:on    
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class ConflictoInteresIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONFLICTO_INTERES_CONTROLLER_BASE_PATH = "/conflictosinteres";

  private HttpEntity<ConflictoInteres> buildRequest(HttpHeaders headers, ConflictoInteres entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }
    HttpEntity<ConflictoInteres> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getConflictoInteres_WithId_ReturnsConflictoInteres() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-C", "ETI-EVR-E")));

    final ResponseEntity<ConflictoInteres> response = restTemplate.exchange(
        CONFLICTO_INTERES_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(headers, null),
        ConflictoInteres.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final ConflictoInteres conflictoInteres = response.getBody();

    Assertions.assertThat(conflictoInteres.getId()).isEqualTo(2L);
    Assertions.assertThat(conflictoInteres.getPersonaConflictoRef()).isEqualTo("user-002");
  }

  @Test
  public void addConflictoInteres_ReturnsConflictoInteres() throws Exception {

    ConflictoInteres nuevoConflictoInteres = generarMockConflictoInteres(2L, "PersonaConflicto");

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-E", "ETI-EVR-C")));

    final ResponseEntity<ConflictoInteres> response = restTemplate.exchange(CONFLICTO_INTERES_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(headers, nuevoConflictoInteres), ConflictoInteres.class);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Test
  public void removeConflictoInteres_Success() throws Exception {

    // when: Delete con id existente
    long id = 2L;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-E", "ETI-EVR-C")));

    final ResponseEntity<ConflictoInteres> response = restTemplate.exchange(
        CONFLICTO_INTERES_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(headers, null),
        ConflictoInteres.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  public void removeConflictoInteres_DoNotGetConflictoInteres() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-E", "ETI-EVR-C")));

    restTemplate.exchange(CONFLICTO_INTERES_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(headers, null), ConflictoInteres.class, 1L);

    final ResponseEntity<ConflictoInteres> response = restTemplate.exchange(
        CONFLICTO_INTERES_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConflictoInteres.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  public void replaceConflictoInteres_ReturnsConflictoInteres() throws Exception {

    ConflictoInteres replaceConflictoInteres = generarMockConflictoInteres(2L, "ConflictoInteres1");

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVR-E", "ETI-EVR-C")));

    final ResponseEntity<ConflictoInteres> response = restTemplate.exchange(
        CONFLICTO_INTERES_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(headers, replaceConflictoInteres), ConflictoInteres.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final ConflictoInteres conflictoInteres = response.getBody();

    Assertions.assertThat(conflictoInteres.getId()).isNotNull();
    Assertions.assertThat(conflictoInteres.getPersonaConflictoRef())
        .isEqualTo(replaceConflictoInteres.getPersonaConflictoRef());
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */
  public Evaluador generarMockEvaluador(Long id, String resumen) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Formulario M10");
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.now());
    evaluador.setFechaBaja(Instant.now());
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-00" + (id != null ? id : "1"));
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Función que devuelve un objeto ConflictoInteres
   * 
   * @param id                  id del ConflictoInteres
   * @param personaConflictoRef la persona del conflicto de interés
   * @return el objeto ConflictoInteres
   */
  public ConflictoInteres generarMockConflictoInteres(Long id, String personaConflictoInteres) {
    ConflictoInteres conflicto = new ConflictoInteres();
    conflicto.setId(id);
    conflicto.setEvaluador(generarMockEvaluador(id, "Resumen" + (id != null ? id : "1")));
    if (personaConflictoInteres != null) {
      conflicto.setPersonaConflictoRef(personaConflictoInteres);
    } else {
      conflicto.setPersonaConflictoRef("user-00" + (id != null ? id : "1"));
    }
    return conflicto;
  }
}