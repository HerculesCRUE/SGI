package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadFinanciadoraNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ProgramaService;
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
 * ConvocatoriaEntidadFinanciadoraControllerTest
 */
@WebMvcTest(ConvocatoriaEntidadFinanciadoraController.class)
class ConvocatoriaEntidadFinanciadoraControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaEntidadFinanciadoraService service;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaentidadfinanciadoras";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void create_ReturnsModeloConvocatoriaEntidadFinanciadora() throws Exception {
    // given: new ConvocatoriaEntidadFinanciadora
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaEntidadFinanciadora>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConvocatoriaEntidadFinanciadora newConvocatoriaEntidadFinanciadora = new ConvocatoriaEntidadFinanciadora();
          BeanUtils.copyProperties(invocation.getArgument(0), newConvocatoriaEntidadFinanciadora);
          newConvocatoriaEntidadFinanciadora.setId(1L);
          return newConvocatoriaEntidadFinanciadora;
        });

    // when: create ConvocatoriaEntidadFinanciadora
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaEntidadFinanciadora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaEntidadFinanciadora is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaEntidadFinanciadora.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value(convocatoriaEntidadFinanciadora.getEntidadRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id")
            .value(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id")
            .value(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion")
            .value(convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaEntidadFinanciadora with id filled
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaEntidadFinanciadora>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaEntidadFinanciadora
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaEntidadFinanciadora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_ReturnsConvocatoriaEntidadFinanciadora() throws Exception {
    // given: Existing ConvocatoriaEntidadFinanciadora to be updated
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraExistente = generarMockConvocatoriaEntidadFinanciadora(
        1L);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(20));

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any()))
        .willReturn(convocatoriaEntidadFinanciadoraExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaEntidadFinanciadora>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaEntidadFinanciadora
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaEntidadFinanciadoraExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaEntidadFinanciadora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConvocatoriaEntidadFinanciadora is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaEntidadFinanciadoraExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId")
            .value(convocatoriaEntidadFinanciadoraExistente.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef")
            .value(convocatoriaEntidadFinanciadoraExistente.getEntidadRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id")
            .value(convocatoriaEntidadFinanciadoraExistente.getFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id")
            .value(convocatoriaEntidadFinanciadoraExistente.getTipoFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion")
            .value(convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L);

    BDDMockito.willThrow(new ConvocatoriaEntidadFinanciadoraNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ConvocatoriaEntidadFinanciadora>any());

    // when: update ConvocatoriaEntidadFinanciadora
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaEntidadFinanciadora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaEntidadFinanciadoraNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithExistingId_ReturnsConvocatoriaEntidadFinanciadora() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConvocatoriaEntidadFinanciadora(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaEntidadFinanciadora is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value("entidad-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion").value(50));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaEntidadFinanciadoraNotFoundException(1L);
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
   * Función que devuelve un objeto ConvocatoriaEntidadFinanciadora
   * 
   * @param id id del ConvocatoriaEntidadFinanciadora
   * @return el objeto ConvocatoriaEntidadFinanciadora
   */
  private ConvocatoriaEntidadFinanciadora generarMockConvocatoriaEntidadFinanciadora(Long id) {
    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id == null ? 1 : id);
    fuenteFinanciacion.setActivo(true);

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id == null ? 1 : id);
    tipoFinanciacion.setActivo(true);

    Programa programa = new Programa();
    programa.setId(id);

    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = new ConvocatoriaEntidadFinanciadora();
    convocatoriaEntidadFinanciadora.setId(id);
    convocatoriaEntidadFinanciadora.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaEntidadFinanciadora.setEntidadRef("entidad-" + (id == null ? 0 : id));
    convocatoriaEntidadFinanciadora.setFuenteFinanciacion(fuenteFinanciacion);
    convocatoriaEntidadFinanciadora.setTipoFinanciacion(tipoFinanciacion);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(50));

    return convocatoriaEntidadFinanciadora;
  }

}
