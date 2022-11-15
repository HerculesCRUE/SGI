package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.converter.ProyectoPeriodoJustificacionSeguimientoConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionSeguimientoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionSeguimientoOutput;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionSeguimientoService;
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
 * ProyectoPeriodoJustificacionSeguimientoControllerTest
 */
@WebMvcTest(ProyectoPeriodoJustificacionSeguimientoController.class)
public class ProyectoPeriodoJustificacionSeguimientoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoPeriodoJustificacionSeguimientoService service;
  @MockBean
  private ProyectoPeriodoJustificacionSeguimientoConverter converter;

  private static final String CONTROLLER_BASE_PATH = ProyectoPeriodoJustificacionSeguimientoController.REQUEST_MAPPING;
  private static final String PATH_ID = ProyectoPeriodoJustificacionSeguimientoController.PATH_ID;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V", "CSP-SJUS-E" })
  void findById_WithExistingId_ReturnsRequerimientoJustificacion() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      ProyectoPeriodoJustificacionSeguimiento requerimientoJustificacion = generarMockProyectoPeriodoJustificacionSeguimiento(
          paramId);
      return requerimientoJustificacion;
    });
    BDDMockito.given(converter.convert(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimiento>any()))
        .willAnswer(new Answer<ProyectoPeriodoJustificacionSeguimientoOutput>() {
          @Override
          public ProyectoPeriodoJustificacionSeguimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoJustificacionSeguimiento input = invocation.getArgument(0,
                ProyectoPeriodoJustificacionSeguimiento.class);
            return generarMockProyectoPeriodoJustificacionSeguimientoOutput(input);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested ProyectoPeriodoJustificacionSeguimiento is resturned as
        // JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void create_ReturnsProyectoPeriodoJustificacionSeguimiento() throws Exception {
    // given: ProyectoPeriodoJustificacionSeguimientoInput data
    String justificanteReintegro = "ProyectoPeriodoJustificacionSeguimiento-001";
    ProyectoPeriodoJustificacionSeguimientoInput proyectoPeriodoJustificacionSeguimientoToCreate = generarMockProyectoPeriodoJustificacionSeguimientoInput(
        justificanteReintegro);
    BDDMockito.given(converter.convert(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimientoInput>any()))
        .willAnswer(new Answer<ProyectoPeriodoJustificacionSeguimiento>() {
          @Override
          public ProyectoPeriodoJustificacionSeguimiento answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoJustificacionSeguimientoInput input = invocation.getArgument(0,
                ProyectoPeriodoJustificacionSeguimientoInput.class);
            return generarMockProyectoPeriodoJustificacionSeguimiento(input);
          }
        });
    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoPeriodoJustificacionSeguimiento input = invocation.getArgument(0);
          input.setId(1L);
          return input;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimiento>any()))
        .willAnswer(new Answer<ProyectoPeriodoJustificacionSeguimientoOutput>() {
          @Override
          public ProyectoPeriodoJustificacionSeguimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoJustificacionSeguimiento input = invocation.getArgument(0,
                ProyectoPeriodoJustificacionSeguimiento.class);
            return generarMockProyectoPeriodoJustificacionSeguimientoOutput(input);
          }
        });

    // when: create ProyectoPeriodoJustificacionSeguimiento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoPeriodoJustificacionSeguimientoToCreate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is CREATED
        // and the created ProyectoPeriodoJustificacionSeguimiento is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("justificanteReintegro").value(justificanteReintegro));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void update_ReturnsProyectoPeriodoJustificacionSeguimiento() throws Exception {
    // given: ProyectoPeriodoJustificacionSeguimientoInput data and a
    // proyectoPeriodoJustificacionSeguimientoId
    Long proyectoPeriodoJustificacionSeguimientoId = 1L;
    String justificanteReintegro = "ProyectoPeriodoJustificacionSeguimiento-001";
    ProyectoPeriodoJustificacionSeguimientoInput proyectoPeriodoJustificacionSeguimientoToUpdate = generarMockProyectoPeriodoJustificacionSeguimientoInput(
        justificanteReintegro);
    BDDMockito
        .given(converter.convert(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimientoInput>any(),
            ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<ProyectoPeriodoJustificacionSeguimiento>() {
          @Override
          public ProyectoPeriodoJustificacionSeguimiento answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoJustificacionSeguimientoInput input = invocation.getArgument(0,
                ProyectoPeriodoJustificacionSeguimientoInput.class);
            Long id = invocation.getArgument(1,
                Long.class);
            return generarMockProyectoPeriodoJustificacionSeguimiento(input, id);
          }
        });
    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoPeriodoJustificacionSeguimiento input = invocation.getArgument(0);
          return input;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimiento>any()))
        .willAnswer(new Answer<ProyectoPeriodoJustificacionSeguimientoOutput>() {
          @Override
          public ProyectoPeriodoJustificacionSeguimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoJustificacionSeguimiento input = invocation.getArgument(0,
                ProyectoPeriodoJustificacionSeguimiento.class);
            return generarMockProyectoPeriodoJustificacionSeguimientoOutput(input);
          }
        });

    // when: update ProyectoPeriodoJustificacionSeguimiento
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_ID, proyectoPeriodoJustificacionSeguimientoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoPeriodoJustificacionSeguimientoToUpdate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the updated ProyectoPeriodoJustificacionSeguimiento is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoPeriodoJustificacionSeguimientoId))
        .andExpect(MockMvcResultMatchers.jsonPath("justificanteReintegro").value(justificanteReintegro));
  }

  private ProyectoPeriodoJustificacionSeguimiento generarMockProyectoPeriodoJustificacionSeguimiento(Long id) {
    return generarMockProyectoPeriodoJustificacionSeguimiento(id, "justificante-reintegro", 1L, 1L);
  }

  private ProyectoPeriodoJustificacionSeguimiento generarMockProyectoPeriodoJustificacionSeguimiento(
      ProyectoPeriodoJustificacionSeguimientoInput input) {
    return generarMockProyectoPeriodoJustificacionSeguimiento(input, null);
  }

  private ProyectoPeriodoJustificacionSeguimiento generarMockProyectoPeriodoJustificacionSeguimiento(
      ProyectoPeriodoJustificacionSeguimientoInput input, Long id) {
    return generarMockProyectoPeriodoJustificacionSeguimiento(
        id, input.getJustificanteReintegro(), input.getProyectoAnualidadId(),
        input.getProyectoPeriodoJustificacionId());
  }

  private ProyectoPeriodoJustificacionSeguimiento generarMockProyectoPeriodoJustificacionSeguimiento(Long id,
      String justificanteReintegro, Long proyectoAnualidadId, Long proyectoPeriodoJustificacionId) {
    return ProyectoPeriodoJustificacionSeguimiento.builder()
        .id(id)
        .justificanteReintegro(justificanteReintegro)
        .proyectoAnualidadId(proyectoAnualidadId)
        .proyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId)
        .build();
  }

  private ProyectoPeriodoJustificacionSeguimientoInput generarMockProyectoPeriodoJustificacionSeguimientoInput(
      String justificanteReintegro) {
    return generarMockProyectoPeriodoJustificacionSeguimientoInput(justificanteReintegro, 1L, 1L);
  }

  private ProyectoPeriodoJustificacionSeguimientoInput generarMockProyectoPeriodoJustificacionSeguimientoInput(
      String justificanteReintegro, Long proyectoAnualidadId, Long proyectoPeriodoJustificacionId) {
    return ProyectoPeriodoJustificacionSeguimientoInput.builder()
        .justificanteReintegro(justificanteReintegro)
        .proyectoAnualidadId(proyectoAnualidadId)
        .proyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId)
        .build();
  }

  private ProyectoPeriodoJustificacionSeguimientoOutput generarMockProyectoPeriodoJustificacionSeguimientoOutput(
      ProyectoPeriodoJustificacionSeguimiento input) {
    return generarMockProyectoPeriodoJustificacionSeguimientoOutput(
        input.getId(), input.getJustificanteReintegro(),
        input.getProyectoAnualidadId(), input.getProyectoPeriodoJustificacionId());
  }

  private ProyectoPeriodoJustificacionSeguimientoOutput generarMockProyectoPeriodoJustificacionSeguimientoOutput(
      Long id,
      String justificanteReintegro, Long proyectoAnualidadId, Long proyectoPeriodoJustificacionId) {
    return ProyectoPeriodoJustificacionSeguimientoOutput.builder()
        .id(id)
        .justificanteReintegro(justificanteReintegro)
        .proyectoAnualidadId(proyectoAnualidadId)
        .proyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId)
        .build();
  }
}
