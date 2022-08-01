package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoSeguimientoCientificoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConvocatoriaPeriodoSeguimientoCientificoControllerTest
 */
@WebMvcTest(ConvocatoriaPeriodoSeguimientoCientificoController.class)
class ConvocatoriaPeriodoSeguimientoCientificoControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaPeriodoSeguimientoCientificoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaperiodoseguimientocientificos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_ReturnsConvocatoriaPeriodoSeguimientoCientificoList()
      throws Exception {
    // given: una lista con uno de los ConvocatoriaPeriodoSeguimientoCientifico
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico newConvocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        null, 27, 30, 1L);
    ConvocatoriaPeriodoSeguimientoCientifico updatedConvocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        4L, 24, 26, 1L);

    List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificos = Arrays
        .asList(updatedConvocatoriaPeriodoSeguimientoCientifico, newConvocatoriaPeriodoSeguimientoCientifico);

    BDDMockito
        .given(service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ConvocatoriaPeriodoSeguimientoCientifico> periodoSeguimientoCientificos = invocation.getArgument(1);
          return periodoSeguimientoCientificos.stream().map(periodoSeguimientoCientifico -> {
            if (periodoSeguimientoCientifico.getId() == null) {
              periodoSeguimientoCientifico.setId(5L);
            }
            periodoSeguimientoCientifico.setConvocatoriaId(convocatoriaId);
            return periodoSeguimientoCientifico;
          }).collect(Collectors.toList());
        });

    // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaPeriodoSeguimientoCientificos)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se crea el nuevo ConvocatoriaPeriodoSeguimientoCientifico, se actualiza
        // el
        // existe y se eliminan el resto
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].id").value(convocatoriaPeriodoSeguimientoCientificos.get(0).getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].convocatoriaId").value(convocatoriaId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].numPeriodo")
            .value(convocatoriaPeriodoSeguimientoCientificos.get(0).getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].mesInicial")
            .value(convocatoriaPeriodoSeguimientoCientificos.get(0).getMesInicial()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].mesFinal")
            .value(convocatoriaPeriodoSeguimientoCientificos.get(0).getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fechaInicioPresentacion").value("2020-10-10T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fechaFinPresentacion").value("2020-11-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].observaciones")
            .value(convocatoriaPeriodoSeguimientoCientificos.get(0).getObservaciones()))

        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(5))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].convocatoriaId").value(convocatoriaId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].numPeriodo")
            .value(convocatoriaPeriodoSeguimientoCientificos.get(1).getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].mesInicial")
            .value(convocatoriaPeriodoSeguimientoCientificos.get(1).getMesInicial()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].mesFinal")
            .value(convocatoriaPeriodoSeguimientoCientificos.get(1).getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fechaInicioPresentacion").value("2020-10-10T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fechaFinPresentacion").value("2020-11-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].observaciones")
            .value(convocatoriaPeriodoSeguimientoCientificos.get(1).getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithNoExistingId_Returns404()
      throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);

    BDDMockito.willThrow(new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(id)).given(service)
        .updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList());

    // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Arrays.asList(convocatoriaPeriodoSeguimientoCientifico))))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findById_WithExistingId_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    // given: existing id
    Long convocatoriaPeriodoSeguimientoCientificoId = 1L;

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<ConvocatoriaPeriodoSeguimientoCientifico>() {
          @Override
          public ConvocatoriaPeriodoSeguimientoCientifico answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return ConvocatoriaPeriodoSeguimientoCientifico.builder().id(id).build();
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoSeguimientoCientificoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaPeriodoSeguimientoCientifico is resturned as
        // JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaPeriodoSeguimientoCientificoId));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long convocatoriaPeriodoSeguimientoCientificoId = 1L;

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(convocatoriaPeriodoSeguimientoCientificoId);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoSeguimientoCientificoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoSeguimientoCientifico
   * 
   * @param id id del ConvocatoriaPeriodoSeguimientoCientifico
   * @return el objeto ConvocatoriaPeriodoSeguimientoCientifico
   */
  private ConvocatoriaPeriodoSeguimientoCientifico generarMockConvocatoriaPeriodoSeguimientoCientifico(Long id) {
    return generarMockConvocatoriaPeriodoSeguimientoCientifico(id, 1, 2, id);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoSeguimientoCientifico
   * 
   * @param id             id del ConvocatoriaPeriodoSeguimientoCientifico
   * @param mesInicial     Mes inicial
   * @param mesFinal       Mes final
   * @param tipo           Tipo SeguimientoCientifico
   * @param convocatoriaId Id Convocatoria
   * @return el objeto ConvocatoriaPeriodoSeguimientoCientifico
   */
  private ConvocatoriaPeriodoSeguimientoCientifico generarMockConvocatoriaPeriodoSeguimientoCientifico(Long id,
      Integer mesInicial, Integer mesFinal, Long convocatoriaId) {
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = new ConvocatoriaPeriodoSeguimientoCientifico();
    convocatoriaPeriodoSeguimientoCientifico.setId(id);
    convocatoriaPeriodoSeguimientoCientifico.setConvocatoriaId(convocatoriaId == null ? 1 : convocatoriaId);
    convocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(1);
    convocatoriaPeriodoSeguimientoCientifico.setMesInicial(mesInicial);
    convocatoriaPeriodoSeguimientoCientifico.setMesFinal(mesFinal);
    convocatoriaPeriodoSeguimientoCientifico.setFechaInicioPresentacion(Instant.parse("2020-10-10T00:00:00Z"));
    convocatoriaPeriodoSeguimientoCientifico.setFechaFinPresentacion(Instant.parse("2020-11-20T23:59:59Z"));
    convocatoriaPeriodoSeguimientoCientifico.setObservaciones("observaciones-" + id);

    return convocatoriaPeriodoSeguimientoCientifico;
  }

}
