package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;

import org.crue.hercules.sgi.csp.exceptions.ProyectoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
import org.crue.hercules.sgi.csp.service.TipoHitoService;
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
 * ProyectoHitoControllerTest
 */
@WebMvcTest(ProyectoHitoController.class)
public class ProyectoHitoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoHitoService service;

  @MockBean
  private TipoHitoService tipoHitoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectohitos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_ReturnsProyectoHito() throws Exception {
    // given: new ProyectoHito
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setId(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoHito>any())).willAnswer((InvocationOnMock invocation) -> {
      ProyectoHito newProyectoHito = new ProyectoHito();
      BeanUtils.copyProperties(invocation.getArgument(0), newProyectoHito);
      newProyectoHito.setId(1L);
      return newProyectoHito;
    });

    // when: create ProyectoHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProyectoHito is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoHito.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario")
            .value("comentario-proyecto-hito-" + String.format("%03d", proyectoHito.getId())))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(proyectoHito.getTipoHito().getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ProyectoHito with id filled
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoHito>any())).willThrow(new IllegalArgumentException());

    // when: create ProyectoHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyectoHito() throws Exception {
    // given: Existing ProyectoHito to be updated
    ProyectoHito proyectoHitoExistente = generarMockProyectoHito(1L);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);

    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoHito>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProyectoHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoHitoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoHito is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoHitoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoHitoExistente.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario")
            .value("comentario-proyecto-hito-" + String.format("%03d", proyectoHito.getId())))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(proyectoHito.getTipoHito().getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);

    BDDMockito.willThrow(new ProyectoHitoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProyectoHito>any());

    // when: update ProyectoHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoHito)))
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

    BDDMockito.willThrow(new ProyectoHitoNotFoundException(id)).given(service).delete(ArgumentMatchers.<Long>any());

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
  public void findById_WithExistingId_ReturnsProyectoHito() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoHito(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoHito is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario")
            .value("comentario-proyecto-hito-" + String.format("%03d", id)));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoHitoNotFoundException(1L);
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
   * Funci??n que devuelve un objeto ProyectoHito
   * 
   * @param id id del ProyectoHito
   * @return el objeto ProyectoHito
   */
  private ProyectoHito generarMockProyectoHito(Long id) {
    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id == null ? 1 : id);
    tipoHito.setActivo(true);

    ProyectoHito proyectoHito = new ProyectoHito();
    proyectoHito.setId(id);
    proyectoHito.setProyectoId(id == null ? 1 : id);
    proyectoHito.setFecha(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoHito.setComentario("comentario-proyecto-hito-" + String.format("%03d", id));
    proyectoHito.setGeneraAviso(true);
    proyectoHito.setTipoHito(tipoHito);

    return proyectoHito;
  }

}
