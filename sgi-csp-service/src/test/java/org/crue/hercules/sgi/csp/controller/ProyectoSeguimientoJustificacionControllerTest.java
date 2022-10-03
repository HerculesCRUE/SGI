package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.converter.ProyectoSeguimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput.ProyectoProyectoSgeOutput;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.crue.hercules.sgi.csp.service.ProyectoSeguimientoJustificacionService;
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
 * ProyectoSeguimientoJustificacionControllerTest
 */
@WebMvcTest(ProyectoSeguimientoJustificacionController.class)
public class ProyectoSeguimientoJustificacionControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoSeguimientoJustificacionService service;
  @MockBean
  private ProyectoSeguimientoJustificacionConverter converter;

  private static final String CONTROLLER_BASE_PATH = ProyectoSeguimientoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_ID = ProyectoSeguimientoJustificacionController.PATH_ID;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void create_ReturnsProyectoSeguimientoJustificacion() throws Exception {
    // given: ProyectoSeguimientoJustificacionInput data
    String justificanteReintegro = "ProyectoSeguimientoJustificacion-001";
    Long proyectoProyectoSgeId = 1L;
    ProyectoSeguimientoJustificacionInput proyectoSeguimientoJustificacionToCreate = generarMockProyectoSeguimientoJustificacionInput(
        proyectoProyectoSgeId, justificanteReintegro);
    BDDMockito.given(converter.convert(ArgumentMatchers.<ProyectoSeguimientoJustificacionInput>any()))
        .willAnswer(new Answer<ProyectoSeguimientoJustificacion>() {
          @Override
          public ProyectoSeguimientoJustificacion answer(InvocationOnMock invocation) throws Throwable {
            ProyectoSeguimientoJustificacionInput input = invocation.getArgument(0,
                ProyectoSeguimientoJustificacionInput.class);
            return generarMockProyectoSeguimientoJustificacion(input);
          }
        });
    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoSeguimientoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoSeguimientoJustificacion input = invocation.getArgument(0);
          input.setId(1L);
          return input;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<ProyectoSeguimientoJustificacion>any()))
        .willAnswer(new Answer<ProyectoSeguimientoJustificacionOutput>() {
          @Override
          public ProyectoSeguimientoJustificacionOutput answer(InvocationOnMock invocation) throws Throwable {
            ProyectoSeguimientoJustificacion input = invocation.getArgument(0,
                ProyectoSeguimientoJustificacion.class);
            return generarMockProyectoSeguimientoJustificacionOutput(input);
          }
        });

    // when: create ProyectoSeguimientoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoSeguimientoJustificacionToCreate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is CREATED
        // and the created ProyectoSeguimientoJustificacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("justificanteReintegro").value(justificanteReintegro));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void update_ReturnsProyectoSeguimientoJustificacion() throws Exception {
    // given: ProyectoSeguimientoJustificacionInput data and a
    // proyectoSeguimientoJustificacionId
    Long proyectoSeguimientoJustificacionId = 1L;
    String justificanteReintegro = "ProyectoSeguimientoJustificacion-001";
    Long proyectoProyectoSgeId = 1L;
    ProyectoSeguimientoJustificacionInput proyectoSeguimientoJustificacionToUpdate = generarMockProyectoSeguimientoJustificacionInput(
        proyectoProyectoSgeId, justificanteReintegro);
    BDDMockito
        .given(converter.convert(ArgumentMatchers.<ProyectoSeguimientoJustificacionInput>any(),
            ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<ProyectoSeguimientoJustificacion>() {
          @Override
          public ProyectoSeguimientoJustificacion answer(InvocationOnMock invocation) throws Throwable {
            ProyectoSeguimientoJustificacionInput input = invocation.getArgument(0,
                ProyectoSeguimientoJustificacionInput.class);
            Long id = invocation.getArgument(1,
                Long.class);
            return generarMockProyectoSeguimientoJustificacion(input, id);
          }
        });
    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoSeguimientoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoSeguimientoJustificacion input = invocation.getArgument(0);
          return input;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<ProyectoSeguimientoJustificacion>any()))
        .willAnswer(new Answer<ProyectoSeguimientoJustificacionOutput>() {
          @Override
          public ProyectoSeguimientoJustificacionOutput answer(InvocationOnMock invocation) throws Throwable {
            ProyectoSeguimientoJustificacion input = invocation.getArgument(0,
                ProyectoSeguimientoJustificacion.class);
            return generarMockProyectoSeguimientoJustificacionOutput(input);
          }
        });

    // when: update ProyectoSeguimientoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_ID, proyectoSeguimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoSeguimientoJustificacionToUpdate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the updated ProyectoSeguimientoJustificacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoSeguimientoJustificacionId))
        .andExpect(MockMvcResultMatchers.jsonPath("justificanteReintegro").value(justificanteReintegro));
  }

  private ProyectoSeguimientoJustificacion generarMockProyectoSeguimientoJustificacion(
      ProyectoSeguimientoJustificacionInput input) {
    return generarMockProyectoSeguimientoJustificacion(null,
        generarMockProyectoProyectoSge(input.getProyectoProyectoSgeId()), input.getJustificanteReintegro());
  }

  private ProyectoSeguimientoJustificacion generarMockProyectoSeguimientoJustificacion(
      ProyectoSeguimientoJustificacionInput input, Long id) {
    return generarMockProyectoSeguimientoJustificacion(id,
        generarMockProyectoProyectoSge(input.getProyectoProyectoSgeId()), input.getJustificanteReintegro());
  }

  private ProyectoSeguimientoJustificacion generarMockProyectoSeguimientoJustificacion(Long id,
      ProyectoProyectoSge proyectoProyectoSge, String justificanteReintegro) {
    return ProyectoSeguimientoJustificacion.builder()
        .id(id)
        .proyectoProyectoSge(proyectoProyectoSge)
        .justificanteReintegro(justificanteReintegro)
        .build();
  }

  private ProyectoProyectoSge generarMockProyectoProyectoSge(Long id) {
    String proyectoSgeRef = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return ProyectoProyectoSge.builder().id(id).proyectoSgeRef(proyectoSgeRef).build();
  }

  private ProyectoSeguimientoJustificacionInput generarMockProyectoSeguimientoJustificacionInput(
      Long proyectoProyectoSgeId, String justificanteReintegro) {
    return ProyectoSeguimientoJustificacionInput.builder()
        .proyectoProyectoSgeId(proyectoProyectoSgeId)
        .justificanteReintegro(justificanteReintegro)
        .build();
  }

  private ProyectoSeguimientoJustificacionOutput generarMockProyectoSeguimientoJustificacionOutput(
      ProyectoSeguimientoJustificacion input) {
    return generarMockProyectoSeguimientoJustificacionOutput(input.getId(),
        generarMockProyectoProyectoSgeOutput(input.getProyectoProyectoSge()),
        input.getJustificanteReintegro());
  }

  private ProyectoSeguimientoJustificacionOutput generarMockProyectoSeguimientoJustificacionOutput(Long id,
      ProyectoProyectoSgeOutput proyectoProyectoSge, String justificanteReintegro) {
    return ProyectoSeguimientoJustificacionOutput.builder()
        .id(id)
        .proyectoProyectoSge(proyectoProyectoSge)
        .justificanteReintegro(justificanteReintegro)
        .build();
  }

  private ProyectoProyectoSgeOutput generarMockProyectoProyectoSgeOutput(ProyectoProyectoSge input) {
    return generarMockProyectoProyectoSgeOutput(input.getId(), input.getProyectoSgeRef());
  }

  private ProyectoProyectoSgeOutput generarMockProyectoProyectoSgeOutput(Long id, String proyectoSgeRef) {
    return ProyectoProyectoSgeOutput.builder().id(id).proyectoSgeRef(proyectoSgeRef).build();
  }
}
