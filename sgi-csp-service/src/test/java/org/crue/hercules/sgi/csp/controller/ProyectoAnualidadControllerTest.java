package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;

import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadGastosTotales;
import org.crue.hercules.sgi.csp.service.AnualidadGastoService;
import org.crue.hercules.sgi.csp.service.AnualidadIngresoService;
import org.crue.hercules.sgi.csp.service.ProyectoAnualidadService;
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
 * ProyectoAnualidadControllerTest
 */
@WebMvcTest(ProyectoAnualidadController.class)
public class ProyectoAnualidadControllerTest extends BaseControllerTest {

  @MockBean
  private AnualidadGastoService anualidadGastoService;
  @MockBean
  private AnualidadIngresoService anualidadIngresoService;
  @MockBean
  private ProyectoAnualidadService service;

  private static final String CONTROLLER_BASE_PATH = ProyectoAnualidadController.REQUEST_MAPPING;
  private static final String PATH_GASTOS_TOTALES = ProyectoAnualidadController.PATH_GASTOS_TOTALES;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V", "CSP-SJUS-E" })
  void getTotalImportesProyectoAnualidad_ReturnsProyectoAnualidadGastosTotales() throws Exception {
    // given: existing id
    Long id = 1L;
    BigDecimal importeConcendidoAnualidadCostesDirectos = new BigDecimal("400");
    BigDecimal importeConcendidoAnualidadCostesIndirectos = new BigDecimal("200");
    BDDMockito.given(service.getTotalImportesProyectoAnualidad(ArgumentMatchers.anyLong()))
        .willAnswer((InvocationOnMock invocation) -> {
          return generarMockProyectoAnualidadGastosTotales(
              importeConcendidoAnualidadCostesDirectos,
              importeConcendidoAnualidadCostesIndirectos);
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_GASTOS_TOTALES, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested ProyectoAnualidadGastosTotales is resturned as
        // JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("importeConcendidoAnualidadCostesDirectos").value(
            importeConcendidoAnualidadCostesDirectos))
        .andExpect(MockMvcResultMatchers.jsonPath("importeConcendidoAnualidadCostesIndirectos").value(
            importeConcendidoAnualidadCostesIndirectos));
  }

  private ProyectoAnualidadGastosTotales generarMockProyectoAnualidadGastosTotales(
      BigDecimal importeConcendidoAnualidadCostesDirectos, BigDecimal importeConcendidoAnualidadCostesIndirectos) {
    return ProyectoAnualidadGastosTotales.builder()
        .importeConcendidoAnualidadCostesDirectos(importeConcendidoAnualidadCostesDirectos)
        .importeConcendidoAnualidadCostesIndirectos(importeConcendidoAnualidadCostesIndirectos)
        .build();
  }
}
