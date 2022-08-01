package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioPeriodoPagoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoPagoService;
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
 * SolicitudProyectoSocioPeriodoPagoControllerTest
 */
@WebMvcTest(SolicitudProyectoSocioPeriodoPagoController.class)
class SolicitudProyectoSocioPeriodoPagoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoSocioPeriodoPagoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocioperiodopago";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithExistingId_ReturnsSolicitudProyectoSocioPeriodoPago() throws Exception {
    // given: una lista con uno de los SolicitudProyectoSocioPeriodoPago
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioPeriodoPago newSolicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        null, 1L);
    SolicitudProyectoSocioPeriodoPago updatedSolicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        4L, 1L);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagos = Arrays
        .asList(updatedSolicitudProyectoSocioPeriodoPago, newSolicitudProyectoSocioPeriodoPago);

    BDDMockito
        .given(
            service.update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoSocioPeriodoPago>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoList = invocation.getArgument(1);
          return solicitudProyectoSocioPeriodoPagoList.stream().map(solicitudProyectoSocioPeriodoPago -> {
            if (solicitudProyectoSocioPeriodoPago.getId() == null) {
              solicitudProyectoSocioPeriodoPago.setId(5L);
            }
            solicitudProyectoSocioPeriodoPago.setSolicitudProyectoSocioId(solicitudProyectoSocioId);
            return solicitudProyectoSocioPeriodoPago;
          }).collect(Collectors.toList());
        });

    // when: update SolicitudProyectoSocioPeriodoPago
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudProyectoSocioPeriodoPagos)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudProyectoSocioPeriodoPago is updated
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].solicitudProyectoSocioId")
            .value(solicitudProyectoSocioPeriodoPagos.get(0).getSolicitudProyectoSocioId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].numPeriodo")
            .value(solicitudProyectoSocioPeriodoPagos.get(0).getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].importe")
            .value(solicitudProyectoSocioPeriodoPagos.get(0).getImporte()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].mes").value(solicitudProyectoSocioPeriodoPagos.get(0).getMes()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].solicitudProyectoSocioId")
            .value(solicitudProyectoSocioPeriodoPagos.get(1).getSolicitudProyectoSocioId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].numPeriodo")
            .value(solicitudProyectoSocioPeriodoPagos.get(1).getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].importe")
            .value(solicitudProyectoSocioPeriodoPagos.get(1).getImporte()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[1].mes").value(solicitudProyectoSocioPeriodoPagos.get(1).getMes()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(1L,
        1L);
    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoUpdate = Arrays
        .asList(solicitudProyectoSocioPeriodoPago);

    BDDMockito.willThrow(new SolicitudProyectoSocioPeriodoPagoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoSocioPeriodoPago>anyList());

    // when: update SolicitudProyectoSocioPeriodoPago
    mockMvc.perform(MockMvcRequestBuilders
        .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoSocioPeriodoPago.getId())
        .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudProyectoSocioPeriodoPagoUpdate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void findById_WithExistingId_ReturnsSolicitudProyectoSocioPeriodoPago() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<SolicitudProyectoSocioPeriodoPago>() {
          @Override
          public SolicitudProyectoSocioPeriodoPago answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return generarSolicitudProyectoSocioPeriodoPago(id, 1L);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoSocioPeriodoPago is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoSocioPeriodoPagoNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudProyectoSocioPeriodoPago
   * 
   * @param solicitudProyectoSocioPeriodoPagoId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoSocioPeriodoPago
   */
  private SolicitudProyectoSocioPeriodoPago generarSolicitudProyectoSocioPeriodoPago(
      Long solicitudProyectoSocioPeriodoPagoId, Long solicitudProyectoSocioId) {

    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = SolicitudProyectoSocioPeriodoPago.builder()
        .id(solicitudProyectoSocioPeriodoPagoId).solicitudProyectoSocioId(solicitudProyectoSocioId).numPeriodo(3)
        .importe(new BigDecimal(358)).mes(3).build();

    return solicitudProyectoSocioPeriodoPago;
  }
}
