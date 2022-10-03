package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.converter.AlegacionRequerimientoConverter;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoInput;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.csp.service.AlegacionRequerimientoService;
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
 * AlegacionRequerimientoControllerTest
 */
@WebMvcTest(AlegacionRequerimientoController.class)
public class AlegacionRequerimientoControllerTest extends BaseControllerTest {

  @MockBean
  private AlegacionRequerimientoService service;
  @MockBean
  private AlegacionRequerimientoConverter converter;

  private static final String CONTROLLER_BASE_PATH = AlegacionRequerimientoController.REQUEST_MAPPING;
  private static final String PATH_ID = AlegacionRequerimientoController.PATH_ID;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void create_ReturnsAlegacionRequerimiento() throws Exception {
    // given: AlegacionRequerimientoInput data
    String suffix = "001";
    AlegacionRequerimientoInput alegacionRequerimientoToCreate = generarMockAlegacionRequerimientoInput(
        suffix, 1L);
    BDDMockito.given(converter.convert(ArgumentMatchers.<AlegacionRequerimientoInput>any()))
        .willAnswer(new Answer<AlegacionRequerimiento>() {
          @Override
          public AlegacionRequerimiento answer(InvocationOnMock invocation) throws Throwable {
            AlegacionRequerimientoInput input = invocation.getArgument(0,
                AlegacionRequerimientoInput.class);
            return generarMockAlegacionRequerimiento(input);
          }
        });
    BDDMockito.given(service.create(ArgumentMatchers.<AlegacionRequerimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          AlegacionRequerimiento input = invocation.getArgument(0);
          input.setId(1L);
          return input;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<AlegacionRequerimiento>any()))
        .willAnswer(new Answer<AlegacionRequerimientoOutput>() {
          @Override
          public AlegacionRequerimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            AlegacionRequerimiento input = invocation.getArgument(0,
                AlegacionRequerimiento.class);
            return generarMockAlegacionRequerimientoOutput(input);
          }
        });

    // when: create AlegacionRequerimiento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(alegacionRequerimientoToCreate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is CREATED
        // and the created AlegacionRequerimiento is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(
            MockMvcResultMatchers.jsonPath("justificanteReintegro").value("Justificante-" + suffix))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value("Observacion-" + suffix));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void update_ReturnsAlegacionRequerimiento() throws Exception {
    // given: AlegacionRequerimientoInputInput data and a
    // alegacionRequerimientoId
    Long alegacionRequerimientoId = 1L;
    String suffix = "002";
    AlegacionRequerimientoInput alegacionRequerimiento = generarMockAlegacionRequerimientoInput(
        suffix, 1L);
    BDDMockito
        .given(converter.convert(ArgumentMatchers.<AlegacionRequerimientoInput>any(),
            ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<AlegacionRequerimiento>() {
          @Override
          public AlegacionRequerimiento answer(InvocationOnMock invocation) throws Throwable {
            AlegacionRequerimientoInput input = invocation.getArgument(0,
                AlegacionRequerimientoInput.class);
            Long id = invocation.getArgument(1,
                Long.class);
            return generarMockAlegacionRequerimiento(input, id);
          }
        });
    BDDMockito.given(service.update(ArgumentMatchers.<AlegacionRequerimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          AlegacionRequerimiento input = invocation.getArgument(0);
          return input;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<AlegacionRequerimiento>any()))
        .willAnswer(new Answer<AlegacionRequerimientoOutput>() {
          @Override
          public AlegacionRequerimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            AlegacionRequerimiento input = invocation.getArgument(0,
                AlegacionRequerimiento.class);
            return generarMockAlegacionRequerimientoOutput(input);
          }
        });

    // when: update AlegacionRequerimiento
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_ID, alegacionRequerimientoId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(alegacionRequerimiento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK and the updated AlegacionRequerimiento is
        // resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(alegacionRequerimientoId))
        .andExpect(
            MockMvcResultMatchers.jsonPath("justificanteReintegro").value("Justificante-" + suffix))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value("Observacion-" + suffix));
  }

  private AlegacionRequerimiento generarMockAlegacionRequerimiento(AlegacionRequerimientoInput input) {
    return generarMockAlegacionRequerimiento(null,
        input.getJustificanteReintegro(), input.getObservaciones(), input.getRequerimientoJustificacionId());
  }

  private AlegacionRequerimiento generarMockAlegacionRequerimiento(AlegacionRequerimientoInput input, Long id) {
    return generarMockAlegacionRequerimiento(id,
        input.getJustificanteReintegro(), input.getObservaciones(), input.getRequerimientoJustificacionId());
  }

  private AlegacionRequerimiento generarMockAlegacionRequerimiento(Long id,
      String justificanteReintegro, String observaciones, Long requerimientoJustificacionId) {
    return AlegacionRequerimiento.builder()
        .id(id)
        .justificanteReintegro(justificanteReintegro)
        .observaciones(observaciones)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private AlegacionRequerimientoInput generarMockAlegacionRequerimientoInput(String suffix,
      Long requerimientoJustificacionId) {
    return generarMockAlegacionRequerimientoInput("Justificante-" + suffix, "Observacion-" + suffix,
        requerimientoJustificacionId);
  }

  private AlegacionRequerimientoInput generarMockAlegacionRequerimientoInput(String justificanteReintegro,
      String observaciones, Long requerimientoJustificacionId) {
    return AlegacionRequerimientoInput.builder()
        .justificanteReintegro(justificanteReintegro)
        .observaciones(observaciones)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private AlegacionRequerimientoOutput generarMockAlegacionRequerimientoOutput(AlegacionRequerimiento input) {
    return generarMockAlegacionRequerimientoOutput(input.getId(), input.getJustificanteReintegro(),
        input.getObservaciones(), input.getRequerimientoJustificacionId());
  }

  private AlegacionRequerimientoOutput generarMockAlegacionRequerimientoOutput(Long id,
      String justificanteReintegro, String observaciones, Long requerimientoJustificacionId) {
    return AlegacionRequerimientoOutput.builder()
        .id(id)
        .justificanteReintegro(justificanteReintegro)
        .observaciones(observaciones)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}
