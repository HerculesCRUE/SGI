package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;

import org.crue.hercules.sgi.csp.exceptions.ProyectoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.service.ProyectoFaseService;
import org.crue.hercules.sgi.csp.service.TipoFaseService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ProyectoFaseControllerTest
 */
@WebMvcTest(ProyectoFaseController.class)
public class ProyectoFaseControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoFaseService service;

  @MockBean
  private TipoFaseService tipoFaseService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectofases";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_ReturnsProyectoFase() throws Exception {
    // given: new ProyectoFase
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoFase>any())).willAnswer((InvocationOnMock invocation) -> {
      ProyectoFase newProyectoFase = new ProyectoFase();
      BeanUtils.copyProperties(invocation.getArgument(0), newProyectoFase);
      newProyectoFase.setId(1L);
      return newProyectoFase;
    });

    // when: create ProyectoFase
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoFase)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProyectoFase is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoFase.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-10-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones")
            .value("observaciones-proyecto-fase-" + String.format("%03d", proyectoFase.getId())))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFase.id").value(proyectoFase.getTipoFase().getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ProyectoFase with id filled
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoFase>any())).willThrow(new IllegalArgumentException());

    // when: create ProyectoFase
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoFase)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyectoFase() throws Exception {
    // given: Existing ProyectoFase to be updated
    ProyectoFase proyectoFaseExistente = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);

    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoFase>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProyectoFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoFaseExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoFase)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoFase is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoFaseExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoFaseExistente.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-10-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones")
            .value("observaciones-proyecto-fase-" + String.format("%03d", proyectoFase.getId())))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFase.id").value(proyectoFase.getTipoFase().getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);

    BDDMockito.willThrow(new ProyectoFaseNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProyectoFase>any());

    // when: update ProyectoFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoFase)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.doNothing().when(service).delete(ArgumentMatchers.anyLong());

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(service, Mockito.times(1)).delete(ArgumentMatchers.anyLong());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoFaseNotFoundException(id)).given(service).delete(ArgumentMatchers.<Long>any());

    // when: delete by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithExistingId_ReturnsProyectoFase() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoFase(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoFase is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-10-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones")
            .value("observaciones-proyecto-fase-" + String.format("%03d", id)));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoFaseNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Funci??n que devuelve un objeto ProyectoFase
   * 
   * @param id id del ProyectoFase
   * @return el objeto ProyectoFase
   */
  private ProyectoFase generarMockProyectoFase(Long id) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);
    tipoFase.setActivo(true);

    ProyectoFase proyectoFase = new ProyectoFase();
    proyectoFase.setId(id);
    proyectoFase.setProyectoId(id == null ? 1 : id);
    proyectoFase.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoFase.setFechaFin(Instant.parse("2020-10-20T23:59:59Z"));
    proyectoFase.setObservaciones("observaciones-proyecto-fase-" + String.format("%03d", id));
    proyectoFase.setGeneraAviso(true);
    proyectoFase.setTipoFase(tipoFase);

    return proyectoFase;
  }

}
