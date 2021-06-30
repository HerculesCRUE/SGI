package org.crue.hercules.sgi.eti.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Configuracion;
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
 * Test de integracion de Configuracion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off    
  "classpath:scripts/configuracion.sql" 
// @formatter:on  
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
public class ConfiguracionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONFIGURACION_CONTROLLER_BASE_PATH = "/configuraciones";

  private HttpEntity<Configuracion> buildRequest(HttpHeaders headers, Configuracion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNF-E")));

    HttpEntity<Configuracion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Test
  public void getConfiguracion_ReturnsConfiguracion() throws Exception {
    final ResponseEntity<Configuracion> response = restTemplate.exchange(CONFIGURACION_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(null, null), Configuracion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Configuracion configuracion = response.getBody();

    Assertions.assertThat(configuracion.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracion.getDiasLimiteEvaluador()).isEqualTo(3);
  }

  @Test
  public void replaceConfiguracion_ReturnsConfiguracion() throws Exception {

    Configuracion replaceConfiguracion = generarMockConfiguracion(1L);

    final ResponseEntity<Configuracion> response = restTemplate.exchange(
        CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceConfiguracion), Configuracion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Configuracion configuracion = response.getBody();

    Assertions.assertThat(configuracion.getId()).isNotNull();
    Assertions.assertThat(configuracion.getDiasLimiteEvaluador()).isEqualTo(1);
  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracion(Long id) {

    Configuracion configuracion = new Configuracion();

    configuracion.setId(id);
    configuracion.setDiasArchivadaPendienteCorrecciones(20);
    configuracion.setDiasLimiteEvaluador(1);
    configuracion.setMesesArchivadaInactivo(2);
    configuracion.setMesesAvisoProyectoCEEA(1);
    configuracion.setMesesAvisoProyectoCEISH(1);
    configuracion.setMesesAvisoProyectoCEIAB(1);

    return configuracion;
  }

}