package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
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
 * ConvocatoriaEnlaceControllerTest
 */
@WebMvcTest(ConvocatoriaEnlaceController.class)
class ConvocatoriaEnlaceControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaEnlaceService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaenlaces";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void create_ReturnsConvocatoriaEnlace() throws Exception {
    // given: new ConvocatoriaEnlace
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaEnlace>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConvocatoriaEnlace newConvocatoriaEnlace = new ConvocatoriaEnlace();
          BeanUtils.copyProperties(invocation.getArgument(0), newConvocatoriaEnlace);
          newConvocatoriaEnlace.setId(1L);
          return newConvocatoriaEnlace;
        });

    // when: create ConvocatoriaEnlace
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaEnlace)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaEnlace is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaEnlace.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(convocatoriaEnlace.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("url").value(convocatoriaEnlace.getUrl()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEnlace").value(convocatoriaEnlace.getTipoEnlace()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaEnlace with id filled
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaEnlace>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaEnlace
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaEnlace)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_ReturnsConvocatoriaEnlace() throws Exception {
    // given: Existing ConvocatoriaEnlace to be updated
    ConvocatoriaEnlace convocatoriaEnlaceExistente = generarMockConvocatoriaEnlace(1L);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);
    convocatoriaEnlace.setDescripcion("nueva-descripcion");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaEnlaceExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaEnlace>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaEnlace
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaEnlaceExistente.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaEnlace)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConvocatoriaEnlace is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaEnlaceExistente.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(convocatoriaEnlace.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEnlace").value(convocatoriaEnlaceExistente.getTipoEnlace()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L);

    BDDMockito.willThrow(new ConvocatoriaEnlaceNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ConvocatoriaEnlace>any());

    // when: update ConvocatoriaEnlace
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaEnlace)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void delete_WithoutId_Return400() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaEnlaceNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaEnlaceNotFoundException(id)).given(service)
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
  void findById_WithExistingId_ReturnsConvocatoriaEnlace() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConvocatoriaEnlace(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaEnlace is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaEnlaceNotFoundException(1L);
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
   * Función que devuelve un objeto ConvocatoriaEnlace
   * 
   * @param id     id del ConvocatoriaEnlace
   * @param nombre nombre del ConvocatoriaEnlace
   * @return el objeto ConvocatoriaEnlace
   */
  private ConvocatoriaEnlace generarMockConvocatoriaEnlace(Long id) {

    ConvocatoriaEnlace convocatoriaEnlace = new ConvocatoriaEnlace();
    convocatoriaEnlace.setId(id);
    convocatoriaEnlace.setConvocatoriaId(id);
    convocatoriaEnlace.setDescripcion("descripcion-" + id);
    convocatoriaEnlace.setUrl("www.url" + id + ".es");
    convocatoriaEnlace.setTipoEnlace(TipoEnlace.builder().nombre("tipoEnlace" + id)
        .descripcion("descripcionEnlace" + id).activo(Boolean.TRUE).build());

    return convocatoriaEnlace;
  }

}