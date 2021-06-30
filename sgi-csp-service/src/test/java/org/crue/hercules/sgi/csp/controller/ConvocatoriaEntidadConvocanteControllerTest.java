package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadConvocanteNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
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
 * ConvocatoriaEntidadConvocanteControllerTest
 */
@WebMvcTest(ConvocatoriaEntidadConvocanteController.class)
public class ConvocatoriaEntidadConvocanteControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaEntidadConvocanteService service;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaentidadconvocantes";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void create_ReturnsModeloConvocatoriaEntidadConvocante() throws Exception {
    // given: new ConvocatoriaEntidadConvocante
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaEntidadConvocante>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConvocatoriaEntidadConvocante newConvocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
          BeanUtils.copyProperties(invocation.getArgument(0), newConvocatoriaEntidadConvocante);
          newConvocatoriaEntidadConvocante.setId(1L);
          return newConvocatoriaEntidadConvocante;
        });

    // when: create ConvocatoriaEntidadConvocante
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaEntidadConvocante)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaEntidadConvocante is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaEntidadConvocante.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value(convocatoriaEntidadConvocante.getEntidadRef()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("programa.id").value(convocatoriaEntidadConvocante.getPrograma().getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaEntidadConvocante with id filled
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaEntidadConvocante>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaEntidadConvocante
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaEntidadConvocante)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_ReturnsConvocatoriaEntidadConvocante() throws Exception {
    // given: Existing ConvocatoriaEntidadConvocante to be updated
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteExistente = generarMockConvocatoriaEntidadConvocante(1L);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L);
    convocatoriaEntidadConvocante.setPrograma(new Programa(2L, null, null, null, null));

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaEntidadConvocanteExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaEntidadConvocante>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaEntidadConvocante
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaEntidadConvocanteExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaEntidadConvocante)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConvocatoriaEntidadConvocante is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaEntidadConvocanteExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId")
            .value(convocatoriaEntidadConvocanteExistente.getConvocatoriaId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("entidadRef").value(convocatoriaEntidadConvocanteExistente.getEntidadRef()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("programa.id").value(convocatoriaEntidadConvocante.getPrograma().getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L);

    BDDMockito.willThrow(new ConvocatoriaEntidadConvocanteNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ConvocatoriaEntidadConvocante>any());

    // when: update ConvocatoriaEntidadConvocante
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaEntidadConvocante)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaEntidadConvocanteNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsConvocatoriaEntidadConvocante() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConvocatoriaEntidadConvocante(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaEntidadConvocante is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value("entidad-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("programa.id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaEntidadConvocanteNotFoundException(1L);
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
   * Función que devuelve un objeto ConvocatoriaEntidadConvocante
   * 
   * @param id id del ConvocatoriaEntidadConvocante
   * @return el objeto ConvocatoriaEntidadConvocante
   */
  private ConvocatoriaEntidadConvocante generarMockConvocatoriaEntidadConvocante(Long id) {
    Programa programa = new Programa();
    programa.setId(id == null ? 1 : id);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
    convocatoriaEntidadConvocante.setId(id);
    convocatoriaEntidadConvocante.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 1 : id));
    convocatoriaEntidadConvocante.setPrograma(programa);

    return convocatoriaEntidadConvocante;
  }

}
