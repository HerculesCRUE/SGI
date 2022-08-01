package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.RequisitoIPNotFoundException;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.service.RequisitoIPCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoIPNivelAcademicoService;
import org.crue.hercules.sgi.csp.service.RequisitoIPService;
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
 * RequisitoIPControllerTest
 */
@WebMvcTest(RequisitoIPController.class)
class RequisitoIPControllerTest extends BaseControllerTest {

  @MockBean
  private RequisitoIPService service;

  @MockBean
  private RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService;

  @MockBean
  private RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoria-requisitoips";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void create_ReturnsModeloRequisitoIP() throws Exception {
    // given: new RequisitoIP
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<RequisitoIP>any())).willAnswer((InvocationOnMock invocation) -> {
      RequisitoIP newRequisitoIP = new RequisitoIP();
      BeanUtils.copyProperties(invocation.getArgument(0), newRequisitoIP);
      newRequisitoIP.setId(1L);
      return newRequisitoIP;
    });

    // when: create RequisitoIP
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(requisitoIP)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new RequisitoIP is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("sexoRef").value(requisitoIP.getSexoRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a RequisitoIP with id filled
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<RequisitoIP>any())).willThrow(new IllegalArgumentException());

    // when: create RequisitoIP
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(requisitoIP)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_ReturnsRequisitoIP() throws Exception {
    // given: Existing RequisitoIP to be updated
    RequisitoIP requisitoIPExistente = generarMockRequisitoIP(1L);
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);
    requisitoIP.setSexoRef("Mujer");

    BDDMockito.given(service.findByConvocatoria(ArgumentMatchers.<Long>any())).willReturn(requisitoIPExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<RequisitoIP>any(), ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update RequisitoIP
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, requisitoIPExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(requisitoIP)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: RequisitoIP is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(requisitoIPExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("sexoRef").value(requisitoIP.getSexoRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);

    BDDMockito.willThrow(new RequisitoIPNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<RequisitoIP>any(), ArgumentMatchers.<Long>any());

    // when: update RequisitoIP
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(requisitoIP)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findByConvocatoriaRequisitoIP_WithExistingId_ReturnsRequisitoIP() throws Exception {
    // given: existing id
    RequisitoIP requisitoIP = generarMockRequisitoIP(1L);
    BDDMockito.given(service.findByConvocatoria(ArgumentMatchers.<Long>any())).willReturn(requisitoIP);

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Convocatoria is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findByConvocatoriaRequisitoIP_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findByConvocatoria(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new RequisitoIPNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findByConvocatoriaRequisitoIP_WithNoExistingRequisitoIP_Returns204() throws Exception {
    // given: Existing convocatoriaId and no existing RequisitoIP
    BDDMockito.given(service.findByConvocatoria(ArgumentMatchers.<Long>any())).willReturn(null);

    // when: find
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 204 No Content
        andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto RequisitoIP
   * 
   * @param id id del RequisitoIP
   * @return el objeto RequisitoIP
   */
  private RequisitoIP generarMockRequisitoIP(Long id) {
    RequisitoIP requisitoIP = new RequisitoIP();
    requisitoIP.setId(id);
    requisitoIP.setSexoRef("Hombre");
    return requisitoIP;
  }

}
