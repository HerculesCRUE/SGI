package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.Optional;

import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionIdentificadorJustificacionInput;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ProyectoPeriodoJustificacionControllerTest
 */
@WebMvcTest(ProyectoPeriodoJustificacionController.class)
class ProyectoPeriodoJustificacionControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoPeriodoJustificacionService service;

  private static final String CONTROLLER_BASE_PATH = ProyectoPeriodoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_PARAMETER_ID = ProyectoPeriodoJustificacionController.PATH_PARAMETER_ID;
  private static final String PATH_IDENTIFICADOR_JUSTIFICACION = ProyectoPeriodoJustificacionController.PATH_IDENTIFICADOR_JUSTIFICACION;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void update_ReturnsFuenteFinanciacion() throws Exception {
    // given: Existing ProyectoPeriodoJustificacion to be updated
    Long id = 1L;
    ProyectoPeriodoJustificacionIdentificadorJustificacionInput newIdentificadorJustificacion = generarMockProyectoPeriodoJustificacionIdentificadorJustificacionInput();
    newIdentificadorJustificacion.setIdentificadorJustificacion("11/1111");

    BDDMockito.given(service.updateIdentificadorJustificacion(ArgumentMatchers.<ProyectoPeriodoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProyectoPeriodoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_IDENTIFICADOR_JUSTIFICACION, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(newIdentificadorJustificacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoPeriodoJustificacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("fechaPresentacionJustificacion")
            .value(newIdentificadorJustificacion.getFechaPresentacionJustificacion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("identificadorJustificacion")
            .value(newIdentificadorJustificacion.getIdentificadorJustificacion()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void findByIdentificadorJustificacion_WithNoIdentificadorJustificacion_Returns400() throws Exception {
    // given: no identificadorJustificacion
    // when: get ProyectoPeriodoJustificacion without identificadorJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_IDENTIFICADOR_JUSTIFICACION)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoPeriodoJustificacion is updated
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void findByIdentificadorJustificacion_WithIdentificadorJustificacionNoExistente_Returns204() throws Exception {
    // given: An identificadorJustificacion no asociado con un
    // ProyectoPeriodoJustificacion
    String identificadorJustificacion = "11/1111";

    BDDMockito.given(service.findByIdentificadorJustificacion(ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());

    // when: get ProyectoPeriodoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_IDENTIFICADOR_JUSTIFICACION)
            .param("identificadorJustificacion", identificadorJustificacion)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoPeriodoJustificacion is updated
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  ProyectoPeriodoJustificacionIdentificadorJustificacionInput generarMockProyectoPeriodoJustificacionIdentificadorJustificacionInput() {
    return ProyectoPeriodoJustificacionIdentificadorJustificacionInput.builder()
        .fechaPresentacionJustificacion(Instant.now())
        .identificadorJustificacion("XX/AAAA")
        .build();
  }
}
