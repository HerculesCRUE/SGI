package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
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
 * SolicitudProyectoEntidadFinanciadoraAjenaControllerTest
 */
@WebMvcTest(SolicitudProyectoEntidadFinanciadoraAjenaController.class)
public class SolicitudProyectoEntidadFinanciadoraAjenaControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoEntidadFinanciadoraAjenaService service;
  @MockBean
  private SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;
  @MockBean
  private SolicitudProyectoEntidadService solicitudProyectoEntidadService;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoentidadfinanciadoraajenas";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void create_ReturnsModeloSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {
    // given: new SolicitudProyectoEntidadFinanciadoraAjena
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoEntidadFinanciadoraAjena>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          SolicitudProyectoEntidadFinanciadoraAjena newSolicitudProyectoEntidadFinanciadoraAjena = new SolicitudProyectoEntidadFinanciadoraAjena();
          BeanUtils.copyProperties(invocation.getArgument(0), newSolicitudProyectoEntidadFinanciadoraAjena);
          newSolicitudProyectoEntidadFinanciadoraAjena.setId(1L);
          return newSolicitudProyectoEntidadFinanciadoraAjena;
        });

    // when: create SolicitudProyectoEntidadFinanciadoraAjena
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoEntidadFinanciadoraAjena)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new SolicitudProyectoEntidadFinanciadoraAjena is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId")
            .value(solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef")
            .value(solicitudProyectoEntidadFinanciadoraAjena.getEntidadRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id")
            .value(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id")
            .value(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion")
            .value(solicitudProyectoEntidadFinanciadoraAjena.getPorcentajeFinanciacion()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a SolicitudProyectoEntidadFinanciadoraAjena with id filled
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoEntidadFinanciadoraAjena>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudProyectoEntidadFinanciadoraAjena
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoEntidadFinanciadoraAjena)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {
    // given: Existing SolicitudProyectoEntidadFinanciadoraAjena to be updated
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaExistente = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    solicitudProyectoEntidadFinanciadoraAjena.setPorcentajeFinanciacion(BigDecimal.valueOf(20));

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any()))
        .willReturn(solicitudProyectoEntidadFinanciadoraAjenaExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoEntidadFinanciadoraAjena>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update SolicitudProyectoEntidadFinanciadoraAjena
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoEntidadFinanciadoraAjenaExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoEntidadFinanciadoraAjena)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudProyectoEntidadFinanciadoraAjena is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("id").value(solicitudProyectoEntidadFinanciadoraAjenaExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId")
            .value(solicitudProyectoEntidadFinanciadoraAjenaExistente.getSolicitudProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef")
            .value(solicitudProyectoEntidadFinanciadoraAjenaExistente.getEntidadRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id")
            .value(solicitudProyectoEntidadFinanciadoraAjenaExistente.getFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id")
            .value(solicitudProyectoEntidadFinanciadoraAjenaExistente.getTipoFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion")
            .value(solicitudProyectoEntidadFinanciadoraAjena.getPorcentajeFinanciacion()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);

    BDDMockito.willThrow(new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<SolicitudProyectoEntidadFinanciadoraAjena>any());

    // when: update SolicitudProyectoEntidadFinanciadoraAjena
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoEntidadFinanciadoraAjena)))
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

    Mockito.verify(service, Mockito.times(1)).delete(ArgumentMatchers.anyLong());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockSolicitudProyectoEntidadFinanciadoraAjena(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoEntidadFinanciadoraAjena is resturned as
        // JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("entidadRef").value("entidad-001"))
        .andExpect(MockMvcResultMatchers.jsonPath("fuenteFinanciacion.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoFinanciacion.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("porcentajeFinanciacion").value(50));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(1L);
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
   * Función que devuelve un objeto SolicitudProyectoEntidadFinanciadoraAjena
   * 
   * @param id id del SolicitudProyectoEntidadFinanciadoraAjena
   * @return el objeto SolicitudProyectoEntidadFinanciadoraAjena
   */
  private SolicitudProyectoEntidadFinanciadoraAjena generarMockSolicitudProyectoEntidadFinanciadoraAjena(Long id) {
    // @formatter:off
    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder()
        .id(id == null ? 1 : id)
        .build();

    FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder()
        .id(id == null ? 1 : id)
        .activo(true)
        .build();

    TipoFinanciacion tipoFinanciacion = TipoFinanciacion.builder()
        .id(id == null ? 1 : id)
        .activo(true)
        .build();

    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = SolicitudProyectoEntidadFinanciadoraAjena
        .builder()
        .id(id)
        .solicitudProyectoId(solicitudProyecto.getId())
        .entidadRef("entidad-" + (id == null ? 0 : String.format("%03d", id)))
        .fuenteFinanciacion(fuenteFinanciacion)
        .tipoFinanciacion(tipoFinanciacion)
        .porcentajeFinanciacion(BigDecimal.valueOf(50))
        .build();
    // @formatter:on

    return solicitudProyectoEntidadFinanciadoraAjena;
  }

}
