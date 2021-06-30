package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadGestoraNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConvocatoriaEntidadGestoraControllerTest
 */

@WebMvcTest(ConvocatoriaEntidadGestoraController.class)
public class ConvocatoriaEntidadGestoraControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaEntidadGestoraService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaentidadgestoras";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void create_ReturnsConvocatoriaEntidadGestora() throws Exception {
    // given: new ConvocatoriaEntidadGestora
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora = generarConvocatoriaEntidadGestora(null, 1L, "entidad-001");

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaEntidadGestora>any()))
        .willAnswer(new Answer<ConvocatoriaEntidadGestora>() {
          @Override
          public ConvocatoriaEntidadGestora answer(InvocationOnMock invocation) throws Throwable {
            ConvocatoriaEntidadGestora givenData = invocation.getArgument(0, ConvocatoriaEntidadGestora.class);
            ConvocatoriaEntidadGestora newData = new ConvocatoriaEntidadGestora();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ConvocatoriaEntidadGestora
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaEntidadGestora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaEntidadGestora is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaEntidadGestora.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value(convocatoriaEntidadGestora.getEntidadRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaEntidadGestora with id filled
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora = generarConvocatoriaEntidadGestora(1L, 1L, "entidad-001");

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaEntidadGestora>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaEntidadGestora
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaEntidadGestora)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
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
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaEntidadGestoraNotFoundException(id)).given(service)
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

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadGestora
   * 
   * @param convocatoriaEntidadGestoraId
   * @param convocatoriaId
   * @param entidadRef
   * @return el objeto ConvocatoriaEntidadGestora
   */
  private ConvocatoriaEntidadGestora generarConvocatoriaEntidadGestora(Long convocatoriaEntidadGestoraId,
      Long convocatoriaId, String entidadRef) {

    return ConvocatoriaEntidadGestora.builder().id(convocatoriaEntidadGestoraId).convocatoriaId(convocatoriaId)
        .entidadRef(entidadRef).build();

  }

}
