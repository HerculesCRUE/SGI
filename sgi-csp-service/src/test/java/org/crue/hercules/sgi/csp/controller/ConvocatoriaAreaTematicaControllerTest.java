package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
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
 * ConvocatoriaAreaTematicaControllerTest
 */

@WebMvcTest(ConvocatoriaAreaTematicaController.class)
public class ConvocatoriaAreaTematicaControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaAreaTematicaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaareatematicas";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_ReturnsConvocatoriaAreaTematica() throws Exception {
    // given: new ConvocatoriaAreaTematica
    ConvocatoriaAreaTematica convocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaAreaTematica>any()))
        .willAnswer(new Answer<ConvocatoriaAreaTematica>() {
          @Override
          public ConvocatoriaAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ConvocatoriaAreaTematica givenData = invocation.getArgument(0, ConvocatoriaAreaTematica.class);
            ConvocatoriaAreaTematica newData = new ConvocatoriaAreaTematica();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ConvocatoriaAreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaAreaTematica)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaAreaTematica is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaAreaTematica.getConvocatoriaId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("areaTematica.id").value(convocatoriaAreaTematica.getAreaTematica().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(convocatoriaAreaTematica.getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaAreaTematica with id filled
    ConvocatoriaAreaTematica convocatoriaAreaTematica = generarConvocatoriaAreaTematica(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaAreaTematica>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaAreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaAreaTematica)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithExistingId_ReturnsConvocatoriaAreaTematica() throws Exception {
    // given: existing ConvocatoriaAreaTematica
    ConvocatoriaAreaTematica updatedConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(1L, 1L, 1L);
    updatedConvocatoriaAreaTematica.setObservaciones("observaciones-modificadas");

    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaAreaTematica>any()))
        .willAnswer(new Answer<ConvocatoriaAreaTematica>() {
          @Override
          public ConvocatoriaAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ConvocatoriaAreaTematica givenData = invocation.getArgument(0, ConvocatoriaAreaTematica.class);
            return givenData;
          }
        });

    // when: update ConvocatoriaAreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedConvocatoriaAreaTematica.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedConvocatoriaAreaTematica)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConvocatoriaAreaTematica is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(updatedConvocatoriaAreaTematica.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(updatedConvocatoriaAreaTematica.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("areaTematica.id")
            .value(updatedConvocatoriaAreaTematica.getAreaTematica().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(updatedConvocatoriaAreaTematica.getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a ConvocatoriaAreaTematica with non existing id
    ConvocatoriaAreaTematica updatedConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(1L, 1L, 1L);

    BDDMockito.willThrow(new ConvocatoriaAreaTematicaNotFoundException(updatedConvocatoriaAreaTematica.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaAreaTematica>any()))
        .willThrow(new ConvocatoriaAreaTematicaNotFoundException(updatedConvocatoriaAreaTematica.getId()));

    // when: update ConvocatoriaAreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedConvocatoriaAreaTematica.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedConvocatoriaAreaTematica)))
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
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaAreaTematicaNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsConvocatoriaAreaTematica() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<ConvocatoriaAreaTematica>() {
      @Override
      public ConvocatoriaAreaTematica answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarConvocatoriaAreaTematica(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaAreaTematica is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaAreaTematicaNotFoundException(id);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto ConvocatoriaAreaTematica
   * 
   * @param convocatoriaAreaTematicaId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto ConvocatoriaAreaTematica
   */
  private ConvocatoriaAreaTematica generarConvocatoriaAreaTematica(Long convocatoriaAreaTematicaId, Long convocatoriaId,
      Long areaTematicaId) {

    return ConvocatoriaAreaTematica.builder().id(convocatoriaAreaTematicaId).convocatoriaId(convocatoriaId)
        .areaTematica(AreaTematica.builder().id(areaTematicaId).build())
        .observaciones("observaciones-" + convocatoriaAreaTematicaId).build();
  }

}
