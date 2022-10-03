package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.converter.IncidenciaDocumentacionRequerimientoConverter;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoAlegacionInput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoInput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.service.IncidenciaDocumentacionRequerimientoService;
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
 * IncidenciaDocumentacionRequerimientoControllerTest
 */
@WebMvcTest(IncidenciaDocumentacionRequerimientoController.class)
public class IncidenciaDocumentacionRequerimientoControllerTest extends BaseControllerTest {

  @MockBean
  private IncidenciaDocumentacionRequerimientoService service;
  @MockBean
  private IncidenciaDocumentacionRequerimientoConverter converter;

  private static final String CONTROLLER_BASE_PATH = IncidenciaDocumentacionRequerimientoController.REQUEST_MAPPING;
  private static final String PATH_ID = IncidenciaDocumentacionRequerimientoController.PATH_ID;
  private static final String PATH_ALEGAR = IncidenciaDocumentacionRequerimientoController.PATH_ALEGAR;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void deleteById_WithExistingId_ReturnsNoContent() throws Exception {
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

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void create_ReturnsIncidenciaDocumentacionRequerimiento() throws Exception {
    // given: IncidenciaDocumentacionRequerimientoInput data
    String suffix = "001";
    IncidenciaDocumentacionRequerimientoInput requerimientoJustificacionToCreate = generarMockIncidenciaDocumentacionRequerimientoInput(
        suffix);
    BDDMockito.given(converter.convert(ArgumentMatchers.<IncidenciaDocumentacionRequerimientoInput>any()))
        .willAnswer(new Answer<IncidenciaDocumentacionRequerimiento>() {
          @Override
          public IncidenciaDocumentacionRequerimiento answer(InvocationOnMock invocation) throws Throwable {
            IncidenciaDocumentacionRequerimientoInput requerimientoJustificacion = invocation.getArgument(0,
                IncidenciaDocumentacionRequerimientoInput.class);
            return generarMockIncidenciaDocumentacionRequerimiento(requerimientoJustificacion);
          }
        });
    BDDMockito.given(service.create(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          IncidenciaDocumentacionRequerimiento requerimientoJustificacion = invocation.getArgument(0);
          requerimientoJustificacion.setId(1L);
          return requerimientoJustificacion;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willAnswer(new Answer<IncidenciaDocumentacionRequerimientoOutput>() {
          @Override
          public IncidenciaDocumentacionRequerimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            IncidenciaDocumentacionRequerimiento requerimientoJustificacion = invocation.getArgument(0,
                IncidenciaDocumentacionRequerimiento.class);
            return generarMockIncidenciaDocumentacionRequerimientoOutput(requerimientoJustificacion);
          }
        });

    // when: create IncidenciaDocumentacionRequerimiento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(requerimientoJustificacionToCreate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is CREATED
        // and the created IncidenciaDocumentacionRequerimiento is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(
            MockMvcResultMatchers.jsonPath("incidencia").value("Incidencia-" + suffix))
        .andExpect(
            MockMvcResultMatchers.jsonPath("nombreDocumento").value("IncidenciaDocumentacionRequerimiento-" + suffix));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void update_ReturnsIncidenciaDocumentacionRequerimiento() throws Exception {
    // given: IncidenciaDocumentacionRequerimientoInputInput data and a
    // incidenciaDocumentacionRequerimientoId
    Long incidenciaDocumentacionRequerimientoId = 1L;
    String suffix = "002";
    IncidenciaDocumentacionRequerimientoInput input = generarMockIncidenciaDocumentacionRequerimientoInput(
        suffix);
    BDDMockito
        .given(converter.convert(ArgumentMatchers.<IncidenciaDocumentacionRequerimientoInput>any(),
            ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<IncidenciaDocumentacionRequerimiento>() {
          @Override
          public IncidenciaDocumentacionRequerimiento answer(InvocationOnMock invocation) throws Throwable {
            IncidenciaDocumentacionRequerimientoInput input = invocation.getArgument(0,
                IncidenciaDocumentacionRequerimientoInput.class);
            Long id = invocation.getArgument(1,
                Long.class);
            return generarMockIncidenciaDocumentacionRequerimiento(input, id);
          }
        });
    BDDMockito.given(service.update(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento = invocation.getArgument(0);
          return incidenciaDocumentacionRequerimiento;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willAnswer(new Answer<IncidenciaDocumentacionRequerimientoOutput>() {
          @Override
          public IncidenciaDocumentacionRequerimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            IncidenciaDocumentacionRequerimiento requerimientoJustificacion = invocation.getArgument(0,
                IncidenciaDocumentacionRequerimiento.class);
            return generarMockIncidenciaDocumentacionRequerimientoOutput(requerimientoJustificacion);
          }
        });

    // when: update IncidenciaDocumentacionRequerimiento
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_ID, incidenciaDocumentacionRequerimientoId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK and the updated IncidenciaDocumentacionRequerimiento is
        // resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(incidenciaDocumentacionRequerimientoId))
        .andExpect(
            MockMvcResultMatchers.jsonPath("incidencia").value("Incidencia-" + suffix))
        .andExpect(
            MockMvcResultMatchers.jsonPath("nombreDocumento").value("IncidenciaDocumentacionRequerimiento-" + suffix));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void updateAlegacion_ReturnsIncidenciaDocumentacionRequerimiento() throws Exception {
    // given: IncidenciaDocumentacionRequerimientoInputInput data and a
    // incidenciaDocumentacionRequerimientoId
    Long incidenciaDocumentacionRequerimientoId = 1L;
    String alegacion = "Alegacion-1";
    IncidenciaDocumentacionRequerimientoAlegacionInput input = generarMockIncidenciaDocumentacionRequerimientoAlegacionInput(
        alegacion);
    BDDMockito
        .given(converter.convert(ArgumentMatchers.<IncidenciaDocumentacionRequerimientoAlegacionInput>any(),
            ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<IncidenciaDocumentacionRequerimiento>() {
          @Override
          public IncidenciaDocumentacionRequerimiento answer(InvocationOnMock invocation) throws Throwable {
            IncidenciaDocumentacionRequerimientoAlegacionInput input = invocation.getArgument(0,
                IncidenciaDocumentacionRequerimientoAlegacionInput.class);
            Long id = invocation.getArgument(1,
                Long.class);
            return generarMockIncidenciaDocumentacionRequerimiento(input, id);
          }
        });
    BDDMockito.given(service.updateAlegacion(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento = invocation.getArgument(0);
          return incidenciaDocumentacionRequerimiento;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willAnswer(new Answer<IncidenciaDocumentacionRequerimientoOutput>() {
          @Override
          public IncidenciaDocumentacionRequerimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            IncidenciaDocumentacionRequerimiento requerimientoJustificacion = invocation.getArgument(0,
                IncidenciaDocumentacionRequerimiento.class);
            return generarMockIncidenciaDocumentacionRequerimientoOutput(requerimientoJustificacion);
          }
        });

    // when: update IncidenciaDocumentacionRequerimiento
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_ALEGAR, incidenciaDocumentacionRequerimientoId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK and the updated IncidenciaDocumentacionRequerimiento is
        // resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(incidenciaDocumentacionRequerimientoId))
        .andExpect(
            MockMvcResultMatchers.jsonPath("alegacion").value(alegacion));
  }

  private IncidenciaDocumentacionRequerimiento generarMockIncidenciaDocumentacionRequerimiento(
      IncidenciaDocumentacionRequerimientoInput input) {
    return generarMockIncidenciaDocumentacionRequerimiento(null, null, input.getIncidencia(),
        input.getNombreDocumento(), input.getRequerimientoJustificacionId());
  }

  private IncidenciaDocumentacionRequerimiento generarMockIncidenciaDocumentacionRequerimiento(
      IncidenciaDocumentacionRequerimientoInput input, Long id) {
    return generarMockIncidenciaDocumentacionRequerimiento(id, null, input.getIncidencia(),
        input.getNombreDocumento(), input.getRequerimientoJustificacionId());
  }

  private IncidenciaDocumentacionRequerimiento generarMockIncidenciaDocumentacionRequerimiento(
      IncidenciaDocumentacionRequerimientoAlegacionInput input, Long id) {
    return generarMockIncidenciaDocumentacionRequerimiento(id, input.getAlegacion(), null, null, null);
  }

  private IncidenciaDocumentacionRequerimiento generarMockIncidenciaDocumentacionRequerimiento(Long id,
      String alegacion,
      String incidencia, String nombreDocumento, Long requerimientoJustificacionId) {
    return IncidenciaDocumentacionRequerimiento.builder()
        .id(id)
        .alegacion(alegacion)
        .incidencia(incidencia)
        .nombreDocumento(nombreDocumento)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private IncidenciaDocumentacionRequerimientoOutput generarMockIncidenciaDocumentacionRequerimientoOutput(
      IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) {
    return generarMockIncidenciaDocumentacionRequerimientoOutput(incidenciaDocumentacionRequerimiento.getId(),
        incidenciaDocumentacionRequerimiento.getAlegacion(),
        incidenciaDocumentacionRequerimiento.getIncidencia(),
        incidenciaDocumentacionRequerimiento.getNombreDocumento(),
        incidenciaDocumentacionRequerimiento.getRequerimientoJustificacionId());
  }

  private IncidenciaDocumentacionRequerimientoOutput generarMockIncidenciaDocumentacionRequerimientoOutput(Long id,
      String alegacion,
      String incidencia, String nombreDocumento, Long requerimientoJustificacionId) {
    return IncidenciaDocumentacionRequerimientoOutput.builder()
        .id(id)
        .alegacion(alegacion)
        .incidencia(incidencia)
        .nombreDocumento(nombreDocumento)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private IncidenciaDocumentacionRequerimientoInput generarMockIncidenciaDocumentacionRequerimientoInput(
      String suffix) {
    return generarMockIncidenciaDocumentacionRequerimientoInput("Incidencia-" + suffix,
        "IncidenciaDocumentacionRequerimiento-" + suffix, 1L);
  }

  private IncidenciaDocumentacionRequerimientoInput generarMockIncidenciaDocumentacionRequerimientoInput(
      String incidencia, String nombreDocumento, Long requerimientoJustificacionId) {
    return IncidenciaDocumentacionRequerimientoInput.builder()
        .incidencia(incidencia)
        .nombreDocumento(nombreDocumento)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private IncidenciaDocumentacionRequerimientoAlegacionInput generarMockIncidenciaDocumentacionRequerimientoAlegacionInput(
      String alegacion) {
    return IncidenciaDocumentacionRequerimientoAlegacionInput.builder()
        .alegacion(alegacion)
        .build();
  }
}
