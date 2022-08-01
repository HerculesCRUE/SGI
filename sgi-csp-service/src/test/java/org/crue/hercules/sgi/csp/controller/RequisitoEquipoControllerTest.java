package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequisitoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoNivelAcademicoService;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * RequisitoEquipoControllerTest
 */
@WebMvcTest(RequisitoEquipoController.class)
@Import(SecurityConfig.class)

class RequisitoEquipoControllerTest extends BaseControllerTest {

  @MockBean
  private RequisitoEquipoService service;

  @MockBean
  private RequisitoEquipoNivelAcademicoService requisitoEquipoNivelAcademicoService;

  @MockBean
  private RequisitoEquipoCategoriaProfesionalService requisitoEquipoCategoriaProfesionalService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoria-requisitoequipos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void create_ReturnsModeloRequisitoEquipo() throws Exception {
    // given: new RequisitoEquipo
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<RequisitoEquipo>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          RequisitoEquipo newRequisitoEquipo = new RequisitoEquipo();
          BeanUtils.copyProperties(invocation.getArgument(0), newRequisitoEquipo);
          newRequisitoEquipo.setId(1L);
          return newRequisitoEquipo;
        });

    // when: create RequisitoEquipo
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(requisitoEquipo)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new RequisitoEquipo is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("edadMaxima").value(requisitoEquipo.getEdadMaxima()))
        .andExpect(MockMvcResultMatchers.jsonPath("sexoRef").value(requisitoEquipo.getSexoRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a RequisitoEquipo with id filled
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<RequisitoEquipo>any())).willThrow(new IllegalArgumentException());

    // when: create RequisitoEquipo
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(requisitoEquipo)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_ReturnsRequisitoEquipo() throws Exception {
    // given: Existing RequisitoEquipo to be updated
    RequisitoEquipo requisitoEquipoExistente = generarMockRequisitoEquipo(1L);
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);
    requisitoEquipo.setEdadMaxima(48);

    BDDMockito.given(service.findByConvocatoriaId(ArgumentMatchers.<Long>any())).willReturn(requisitoEquipoExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<RequisitoEquipo>any(), ArgumentMatchers.anyLong()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update RequisitoEquipo
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, requisitoEquipoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(requisitoEquipo)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: RequisitoEquipo is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(requisitoEquipoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("edadMaxima").value(requisitoEquipo.getEdadMaxima()))
        .andExpect(MockMvcResultMatchers.jsonPath("sexoRef").value(requisitoEquipo.getSexoRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);

    BDDMockito.willThrow(new RequisitoEquipoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<RequisitoEquipo>any(), ArgumentMatchers.anyLong());

    // when: update RequisitoEquipo
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(requisitoEquipo)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findByConvocatoriaRequisitoEquipo_WithExistingId_ReturnsRequisitoEquipo() throws Exception {
    // given: existing id
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(1L);
    BDDMockito.given(service.findByConvocatoriaId(ArgumentMatchers.<Long>any())).willReturn(requisitoEquipo);

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
  void findByConvocatoriaRequisitoEquipo_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findByConvocatoriaId(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaNotFoundException(1L);
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
  void findByConvocatoriaRequisitoEquipo_WithNoExistingRequisitoEquipo_Returns204() throws Exception {
    // given: Existing convocatoriaId and no existing RequisitoEquipo
    BDDMockito.given(service.findByConvocatoriaId(ArgumentMatchers.<Long>any())).willReturn(null);

    // when: find
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 204 No Content
        andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto RequisitoEquipo
   * 
   * @param id id del RequisitoEquipo
   * @return el objeto RequisitoEquipo
   */
  private RequisitoEquipo generarMockRequisitoEquipo(Long id) {
    RequisitoEquipo requisitoEquipo = new RequisitoEquipo();
    requisitoEquipo.setId(id);
    requisitoEquipo.setEdadMaxima(50);
    requisitoEquipo.setSexoRef("sexo-ref");
    return requisitoEquipo;
  }

}
