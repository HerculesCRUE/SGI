package org.crue.hercules.sgi.csp.integration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoOutput;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de SolicitudHito.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudHitoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudhitos";

  private HttpEntity<SolicitudHitoInput> buildRequest(HttpHeaders headers, SolicitudHitoInput entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudHitoInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsSolicitudHito() throws Exception {
    SolicitudHitoInput solicitudHito = generarSolicitudHito(1L, 1L);

    final ResponseEntity<SolicitudHitoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudHito), SolicitudHitoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudHitoOutput solicitudHitoCreado = response.getBody();
    Assertions.assertThat(solicitudHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudHitoCreado.getSolicitudId()).as("getSolicitudId()")
        .isEqualTo(solicitudHito.getSolicitudId());
    Assertions.assertThat(solicitudHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(solicitudHito.getTipoHitoId());
    Assertions.assertThat(I18nHelper.getValueForLanguage(solicitudHitoCreado.getComentario(), Language.ES))
        .as("getComentario()").isEqualTo("comentario");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsSolicitudHito() throws Exception {
    Long idSolicitudHito = 1L;

    List<I18nFieldValueDto> hitoComentario = new ArrayList<I18nFieldValueDto>();
    hitoComentario.add(new I18nFieldValueDto(Language.ES, "comentario-modificado"));

    SolicitudHitoInput solicitudHito = generarSolicitudHito(1L, 1L);
    solicitudHito.setComentario(hitoComentario);

    final ResponseEntity<SolicitudHitoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitudHito), SolicitudHitoOutput.class, idSolicitudHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudHitoOutput solicitudHitoActualizado = response.getBody();
    Assertions.assertThat(solicitudHitoActualizado.getId()).as("getId()").isEqualTo(idSolicitudHito);
    Assertions
        .assertThat(
            I18nHelper.getValueForLanguage(solicitudHitoActualizado.getComentario(), Language.ES))
        .as("getComentario()")
        .isEqualTo(I18nHelper.getValueForLanguage(solicitudHito.getComentario(), Language.ES));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete_Return204() throws Exception {
    // given: existing SolicitudHito to be deleted
    Long id = 1L;

    // when: delete SolicitudHito
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, id);

    // then: SolicitudHito deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * Función que devuelve un objeto SolicitudHito
   * 
   * @param solicitudHitoId
   * @param solicitudId
   * @param tipoDocumentoId
   * @return el objeto SolicitudHito
   */
  private SolicitudHitoInput generarSolicitudHito(Long solicitudId, Long tipoDocumentoId) {

    List<I18nFieldValueDto> hitoComentario = new ArrayList<I18nFieldValueDto>();
    hitoComentario.add(new I18nFieldValueDto(Language.ES, "comentario"));

    SolicitudHitoInput solicitudHito = SolicitudHitoInput.builder().solicitudId(solicitudId)
        .comentario(
            hitoComentario)
        .fecha(Instant.now())
        .tipoHitoId(tipoDocumentoId).build();

    return solicitudHito;
  }

}
