package org.crue.hercules.sgi.pii.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ComunicadosControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/comunicados";
  private static final String PATH_MESES_HASTA_FECHA_FIN_PRIORIDAD_SOLICITUD_PROTECCION = "/meses-hasta-fecha-fin-prioridad-solicitud-proteccion";
  private static final String PATH_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION = "/aviso-fin-plazo-presentacion-fases-nacionales-regionales-solicitud-proteccion";
  private static final String PATH_AVISO_FECHA_LIMITE_PROCEDIMIENTO = "/aviso-fecha-limite-procedimiento";

  @Autowired
  private MockMvc mockMvc;

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  // @Test
  void enviarComunicadoFechaLimiteProcedimiento_ReturnsStatusOK() throws Exception {
    String roles[] = { "SCOPE_sgi-pii" };

    ResponseEntity<Void> response = this.restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_MESES_HASTA_FECHA_FIN_PRIORIDAD_SOLICITUD_PROTECCION, HttpMethod.GET,
        this.buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  // @Test
  void enviarComunicadoMesesHastaFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion_ReturnsStatusOK()
      throws Exception {
    String roles[] = { "SCOPE_sgi-pii" };

    this.mockMvc
        .perform(
            get(CONTROLLER_BASE_PATH + PATH_MESES_HASTA_FECHA_FIN_PRIORIDAD_SOLICITUD_PROTECCION)
                .with(oauth2Client("sgi-pii"))
                .with(oidcLogin().authorities(new SimpleGrantedAuthority("SCOPE_sgi-pii"))))
        .andExpect(status().isOk());
  }

  // @Test
  void enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion_ReturnsStatusOK() throws Exception {
    String roles[] = { "SCOPE_sgi-pii" };
  }
}
