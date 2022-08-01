package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;

import org.crue.hercules.sgi.csp.exceptions.ProyectoEntidadFinanciadoraNotFoundException;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
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
 * ProyectoEntidadFinanciadoraControllerTest
 */
@WebMvcTest(ProyectoEntidadFinanciadoraController.class)
class ProyectoEntidadFinanciadoraControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoEntidadFinanciadoraService service;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoentidadfinanciadoras";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void create_ReturnsModeloProyectoEntidadFinanciadora() throws Exception {
    // given: new ProyectoEntidadFinanciadora
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoEntidadFinanciadora>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoEntidadFinanciadora newProyectoEntidadFinanciadora = new ProyectoEntidadFinanciadora();
          BeanUtils.copyProperties(invocation.getArgument(0), newProyectoEntidadFinanciadora);
          newProyectoEntidadFinanciadora.setId(1L);
          return newProyectoEntidadFinanciadora;
        });

    // when: create ProyectoEntidadFinanciadora
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoEntidadFinanciadora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProyectoEntidadFinanciadora is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoEntidadFinanciadora.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value(proyectoEntidadFinanciadora.getEntidadRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id")
            .value(proyectoEntidadFinanciadora.getFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id")
            .value(proyectoEntidadFinanciadora.getTipoFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion")
            .value(proyectoEntidadFinanciadora.getPorcentajeFinanciacion()))
        .andExpect(MockMvcResultMatchers.jsonPath("ajena").value(proyectoEntidadFinanciadora.getAjena()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a ProyectoEntidadFinanciadora with id filled
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoEntidadFinanciadora>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ProyectoEntidadFinanciadora
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoEntidadFinanciadora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void update_ReturnsProyectoEntidadFinanciadora() throws Exception {
    // given: Existing ProyectoEntidadFinanciadora to be updated
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadoraExistente = generarMockProyectoEntidadFinanciadora(1L);
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);
    proyectoEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(20));

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(proyectoEntidadFinanciadoraExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoEntidadFinanciadora>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProyectoEntidadFinanciadora
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoEntidadFinanciadoraExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoEntidadFinanciadora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoEntidadFinanciadora is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoEntidadFinanciadoraExistente.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoEntidadFinanciadoraExistente.getProyectoId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("entidadRef").value(proyectoEntidadFinanciadoraExistente.getEntidadRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id")
            .value(proyectoEntidadFinanciadoraExistente.getFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id")
            .value(proyectoEntidadFinanciadoraExistente.getTipoFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion")
            .value(proyectoEntidadFinanciadora.getPorcentajeFinanciacion()))
        .andExpect(MockMvcResultMatchers.jsonPath("ajena").value(proyectoEntidadFinanciadora.getAjena()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);

    BDDMockito.willThrow(new ProyectoEntidadFinanciadoraNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProyectoEntidadFinanciadora>any());

    // when: update ProyectoEntidadFinanciadora
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoEntidadFinanciadora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void delete_WithExistingId_Return204() throws Exception {
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
  void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoEntidadFinanciadoraNotFoundException(id)).given(service)
        .delete(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findById_WithExistingId_ReturnsProyectoEntidadFinanciadora() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoEntidadFinanciadora(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoEntidadFinanciadora is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value("entidad-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion").value(50))
        .andExpect(MockMvcResultMatchers.jsonPath("ajena").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoEntidadFinanciadoraNotFoundException(1L);
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
   * Función que devuelve un objeto ProyectoEntidadFinanciadora
   * 
   * @param id id del ProyectoEntidadFinanciadora
   * @return el objeto ProyectoEntidadFinanciadora
   */
  private ProyectoEntidadFinanciadora generarMockProyectoEntidadFinanciadora(Long id) {
    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id == null ? 1 : id);
    fuenteFinanciacion.setActivo(true);

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id == null ? 1 : id);
    tipoFinanciacion.setActivo(true);

    Programa programa = new Programa();
    programa.setId(id);

    ProyectoEntidadFinanciadora ProyectoEntidadFinanciadora = new ProyectoEntidadFinanciadora();
    ProyectoEntidadFinanciadora.setId(id);
    ProyectoEntidadFinanciadora.setProyectoId(id == null ? 1 : id);
    ProyectoEntidadFinanciadora.setEntidadRef("entidad-" + (id == null ? 0 : id));
    ProyectoEntidadFinanciadora.setFuenteFinanciacion(fuenteFinanciacion);
    ProyectoEntidadFinanciadora.setTipoFinanciacion(tipoFinanciacion);
    ProyectoEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(50));
    ProyectoEntidadFinanciadora.setAjena(false);

    return ProyectoEntidadFinanciadora;
  }

}
