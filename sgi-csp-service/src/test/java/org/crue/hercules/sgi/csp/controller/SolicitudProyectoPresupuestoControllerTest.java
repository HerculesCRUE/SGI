package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPresupuestoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
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
 * SolicitudProyectoPresupuestoControllerTest
 */
@WebMvcTest(SolicitudProyectoPresupuestoController.class)
public class SolicitudProyectoPresupuestoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoPresupuestoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectopresupuestos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void create_ReturnsSolicitudProyectoPresupuesto() throws Exception {
    // given: new SolicitudProyectoPresupuesto
    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = generarSolicitudProyectoPresupuesto(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoPresupuesto>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          SolicitudProyectoPresupuesto givenData = invocation.getArgument(0, SolicitudProyectoPresupuesto.class);
          SolicitudProyectoPresupuesto newData = new SolicitudProyectoPresupuesto();
          BeanUtils.copyProperties(givenData, newData);
          newData.setId(1L);
          return newData;
        });

    // when: create SolicitudProyectoPresupuesto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoPresupuesto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new SolicitudProyectoPresupuesto is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId")
            .value(solicitudProyectoPresupuesto.getSolicitudProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("conceptoGasto.id")
            .value(solicitudProyectoPresupuesto.getConceptoGasto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("anualidad").value(solicitudProyectoPresupuesto.getAnualidad()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeSolicitado")
            .value(solicitudProyectoPresupuesto.getImporteSolicitado()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(solicitudProyectoPresupuesto.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoEntidadId").isEmpty());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a SolicitudProyectoPresupuesto with id filled
    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = generarSolicitudProyectoPresupuesto(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoPresupuesto>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudProyectoPresupuesto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoPresupuesto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithExistingId_ReturnsSolicitudProyectoPresupuesto() throws Exception {
    // given: existing SolicitudProyectoPresupuesto
    SolicitudProyectoPresupuesto updatedSolicitudProyectoPresupuesto = generarSolicitudProyectoPresupuesto(1L, 1L, 1L);
    // updatedSolicitudProyectoPresupuesto.setMesFin(12);

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoPresupuesto>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          SolicitudProyectoPresupuesto givenData = invocation.getArgument(0, SolicitudProyectoPresupuesto.class);
          return givenData;
        });

    // when: update SolicitudProyectoPresupuesto
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoPresupuesto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoPresupuesto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudProyectoPresupuesto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId")
            .value(updatedSolicitudProyectoPresupuesto.getSolicitudProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("conceptoGasto.id")
            .value(updatedSolicitudProyectoPresupuesto.getConceptoGasto().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("anualidad").value(updatedSolicitudProyectoPresupuesto.getAnualidad()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeSolicitado")
            .value(updatedSolicitudProyectoPresupuesto.getImporteSolicitado()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones")
            .value(updatedSolicitudProyectoPresupuesto.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoEntidadId").isEmpty());
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudProyectoPresupuesto with non existing id
    SolicitudProyectoPresupuesto updatedSolicitudProyectoPresupuesto = generarSolicitudProyectoPresupuesto(1L, 1L, 1L);

    BDDMockito.willThrow(new SolicitudProyectoPresupuestoNotFoundException(updatedSolicitudProyectoPresupuesto.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoPresupuesto>any()))
        .willThrow(new SolicitudProyectoPresupuestoNotFoundException(updatedSolicitudProyectoPresupuesto.getId()));

    // when: update SolicitudProyectoPresupuesto
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoPresupuesto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoPresupuesto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
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
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudProyectoPresupuestoNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsSolicitudProyectoPresupuesto() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<SolicitudProyectoPresupuesto>() {
          @Override
          public SolicitudProyectoPresupuesto answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return generarSolicitudProyectoPresupuesto(id, 1L, 1L);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoPresupuesto is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoPresupuestoNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudProyectoPresupuesto
   * 
   * @param id                  Id {@link SolicitudProyectoPresupuesto}.
   * @param solicitudProyectoId Id {@link SolicitudProyecto}.
   * @param conceptoGastoId     Id {@link ConceptoGasto}.
   * @return el objeto {@link SolicitudProyectoPresupuesto}.
   */
  private SolicitudProyectoPresupuesto generarSolicitudProyectoPresupuesto(Long id, Long solicitudProyectoId,
      Long conceptoGastoId) {

    String suffix = String.format("%03d", id);

    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = SolicitudProyectoPresupuesto
        .builder()// @formatter:off
        .id(id)
        .solicitudProyectoId(solicitudProyectoId)
        .conceptoGasto(ConceptoGasto.builder().id(conceptoGastoId).build())
        .anualidad(1000)
        .importeSolicitado(new BigDecimal("335"))
        .observaciones("observaciones-" + suffix)
        .solicitudProyectoEntidadId(null)
        .build();// @formatter:on

    return solicitudProyectoPresupuesto;
  }

}
