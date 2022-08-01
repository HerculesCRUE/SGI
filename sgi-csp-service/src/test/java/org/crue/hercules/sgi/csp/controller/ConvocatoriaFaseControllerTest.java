package org.crue.hercules.sgi.csp.controller;

import static org.mockito.ArgumentMatchers.anyLong;

import java.time.Instant;

import org.crue.hercules.sgi.csp.converter.ConvocatoriaFaseConverter;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseOutput;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
import org.crue.hercules.sgi.csp.service.TipoFaseService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConvocatoriaFaseControllerTest
 */
@WebMvcTest(ConvocatoriaFaseController.class)
class ConvocatoriaFaseControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaFaseService service;

  @MockBean
  private TipoFaseService tipoFaseService;

  @MockBean
  private ConvocatoriaFaseConverter convocatoriaFaseConverter;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriafases";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void create_ReturnsModeloConvocatoriaFase() throws Exception {
    // given: new ConvocatoriaFase
    ConvocatoriaFaseInput convocatoriaFaseInput = generarMockConvocatoriaFaseInput();
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L, convocatoriaFaseInput);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaFaseInput>any()))
        .willReturn(convocatoriaFase);

    BDDMockito.given(this.convocatoriaFaseConverter.convert(ArgumentMatchers.<ConvocatoriaFase>any()))
        .willReturn(generarMockConvocatoriaFaseOuput(convocatoriaFase));

    // when: create ConvocatoriaFase
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaFaseInput)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaFase is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaFaseInput.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-10-28T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFase.id").value(convocatoriaFaseInput.getTipoFaseId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_ReturnsConvocatoriaFase() throws Exception {
    // given: Existing ConvocatoriaFase to be updated
    ConvocatoriaFaseInput convocatoriaFaseExistente = generarMockConvocatoriaFaseInput();
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    convocatoriaFase.setTipoFase(TipoFase.builder().id(1L).build());

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any()))
        .willReturn(generarMockConvocatoriaFase(1L, convocatoriaFaseExistente));
    BDDMockito.given(service.update(anyLong(), ArgumentMatchers.<ConvocatoriaFaseInput>any()))
        .willReturn(convocatoriaFase);

    BDDMockito.given(this.convocatoriaFaseConverter.convert(ArgumentMatchers.<ConvocatoriaFase>any()))
        .willReturn(generarMockConvocatoriaFaseOuput(convocatoriaFase));

    // when: update ConvocatoriaFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaFaseExistente)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConvocatoriaFase is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaFaseExistente.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-10-28T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFase.id").value(convocatoriaFase.getTipoFase().getId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaFaseInput convocatoriaFase = generarMockConvocatoriaFaseInput();

    BDDMockito.willThrow(new ConvocatoriaFaseNotFoundException(id)).given(service)
        .update(anyLong(), ArgumentMatchers.<ConvocatoriaFaseInput>any());

    BDDMockito.given(this.convocatoriaFaseConverter.convert(ArgumentMatchers.<ConvocatoriaFase>any()))
        .willReturn(generarMockConvocatoriaFaseOuput(generarMockConvocatoriaFase(1L)));

    // when: update ConvocatoriaFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaFase)))
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

    BDDMockito.willThrow(new ConvocatoriaFaseNotFoundException(id)).given(service).delete(ArgumentMatchers.<Long>any());

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
  void findById_WithExistingId_ReturnsConvocatoriaFase() throws Exception {
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willReturn(convocatoriaFase);
    BDDMockito.given(this.convocatoriaFaseConverter.convert(ArgumentMatchers.<ConvocatoriaFase>any()))
        .willReturn(generarMockConvocatoriaFaseOuput(convocatoriaFase));

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaFase is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-10-28T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFase.id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaFaseNotFoundException(1L);
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
   * Función que devuelve un objeto ConvocatoriaFase
   * 
   * @param id id del ConvocatoriaFase
   * @return el objeto ConvocatoriaFase
   */
  private ConvocatoriaFase generarMockConvocatoriaFase(Long id) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);

    ConvocatoriaFase convocatoriaFase = new ConvocatoriaFase();
    convocatoriaFase.setId(id);
    convocatoriaFase.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaFase.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    convocatoriaFase.setFechaFin(Instant.parse("2020-10-28T23:59:59Z"));
    convocatoriaFase.setTipoFase(tipoFase);
    convocatoriaFase.setObservaciones("observaciones" + id);

    return convocatoriaFase;
  }

  private ConvocatoriaFase generarMockConvocatoriaFase(Long id, ConvocatoriaFaseInput input) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);

    ConvocatoriaFase convocatoriaFase = new ConvocatoriaFase();
    convocatoriaFase.setId(id);
    convocatoriaFase.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaFase.setFechaInicio(input.getFechaInicio());
    convocatoriaFase.setFechaFin(input.getFechaFin());
    convocatoriaFase.setTipoFase(tipoFase);
    convocatoriaFase.setObservaciones(input.getObservaciones());

    return convocatoriaFase;
  }

  private ConvocatoriaFaseOutput generarMockConvocatoriaFaseOuput(ConvocatoriaFase input) {
    ConvocatoriaFaseOutput.TipoFase tipoFaseOutput = ConvocatoriaFaseOutput.TipoFase.builder()
        .id(input.getTipoFase().getId())
        .build();
    ConvocatoriaFaseOutput convocatoriaFase = new ConvocatoriaFaseOutput();
    convocatoriaFase.setId(input.getId());
    convocatoriaFase.setConvocatoriaId(input.getConvocatoriaId());
    convocatoriaFase.setFechaInicio(input.getFechaInicio());
    convocatoriaFase.setFechaFin(input.getFechaFin());
    convocatoriaFase.setTipoFase(tipoFaseOutput);
    convocatoriaFase.setObservaciones(input.getObservaciones());

    return convocatoriaFase;
  }

  private ConvocatoriaFaseInput generarMockConvocatoriaFaseInput() {
    ConvocatoriaFaseInput convocatoriaFase = new ConvocatoriaFaseInput();
    convocatoriaFase.setConvocatoriaId(1L);
    convocatoriaFase.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    convocatoriaFase.setFechaFin(Instant.parse("2020-10-28T23:59:59Z"));
    convocatoriaFase.setTipoFaseId(1L);
    convocatoriaFase.setObservaciones("observaciones" + 1L);

    return convocatoriaFase;
  }

}