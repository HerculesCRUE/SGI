package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.converter.RequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.service.RequerimientoJustificacionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * RequerimientoJustificacionControllerTest
 */
@WebMvcTest(RequerimientoJustificacionController.class)
public class RequerimientoJustificacionControllerTest extends BaseControllerTest {

  @MockBean
  private RequerimientoJustificacionService service;
  @MockBean
  private RequerimientoJustificacionConverter converter;

  private static final String CONTROLLER_BASE_PATH = RequerimientoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_ID = RequerimientoJustificacionController.PATH_ID;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V", "CSP-SJUS-E" })
  void findById_WithExistingId_ReturnsRequerimientoJustificacion() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      RequerimientoJustificacion requerimientoJustificacion = generarMockRequerimientoJustificacion(paramId);
      return requerimientoJustificacion;
    });
    BDDMockito.given(converter.convert(ArgumentMatchers.<RequerimientoJustificacion>any()))
        .willAnswer(new Answer<RequerimientoJustificacionOutput>() {
          @Override
          public RequerimientoJustificacionOutput answer(InvocationOnMock invocation) throws Throwable {
            RequerimientoJustificacion requerimientoJustificacion = invocation.getArgument(0,
                RequerimientoJustificacion.class);
            return generarMockRequerimientoJustificacionOutput(requerimientoJustificacion);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested RequerimientoJustificacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void deleteById_WithExistingId_ReturnsRequerimientoJustificacion() throws Exception {
    // given: existing id
    Long id = 1L;

    // when: delete by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is NO_CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id) {
    String observacionSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockRequerimientoJustificacion(id, "RequerimientoJustificacion-" + observacionSuffix,
        null);
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id, String observaciones,
      Long requerimientoPrevioId) {
    return RequerimientoJustificacion.builder()
        .id(id)
        .observaciones(observaciones)
        .requerimientoPrevioId(requerimientoPrevioId)
        .build();
  }

  private RequerimientoJustificacionOutput generarMockRequerimientoJustificacionOutput(
      RequerimientoJustificacion requerimientoJustificacion) {
    return generarMockRequerimientoJustificacionOutput(requerimientoJustificacion.getId(),
        requerimientoJustificacion.getObservaciones(), requerimientoJustificacion.getRequerimientoPrevioId());
  }

  private RequerimientoJustificacionOutput generarMockRequerimientoJustificacionOutput(Long id, String observaciones,
      Long requerimientoPrevioId) {
    return RequerimientoJustificacionOutput.builder()
        .id(id)
        .observaciones(observaciones)
        .requerimientoPrevioId(requerimientoPrevioId)
        .build();
  }
}
