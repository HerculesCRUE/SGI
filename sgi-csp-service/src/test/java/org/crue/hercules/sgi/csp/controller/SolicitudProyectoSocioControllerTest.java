package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioEquipoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoPagoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudProyectoSocioControllerTest
 */
@WebMvcTest(SolicitudProyectoSocioController.class)
class SolicitudProyectoSocioControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoSocioService service;

  @MockBean
  private SolicitudProyectoSocioEquipoService solicitudProyectoEquipoSocioService;

  @MockBean
  private SolicitudProyectoSocioPeriodoPagoService solicitudProyectoSocioPeriodoPagoService;

  @MockBean
  private SolicitudProyectoSocioPeriodoJustificacionService solicitudProyectoSocioPeriodoJustificacionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocio";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: new SolicitudProyectoSocio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willAnswer(new Answer<SolicitudProyectoSocio>() {
          @Override
          public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoSocio givenData = invocation.getArgument(0, SolicitudProyectoSocio.class);
            SolicitudProyectoSocio newData = new SolicitudProyectoSocio();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create SolicitudProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoSocio)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new SolicitudProyectoSocio is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("rolSocio.id").value(solicitudProyectoSocio.getRolSocio().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId")
            .value(solicitudProyectoSocio.getSolicitudProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicio").value(solicitudProyectoSocio.getMesInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFin").value(solicitudProyectoSocio.getMesFin()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("numInvestigadores").value(solicitudProyectoSocio.getNumInvestigadores()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("importeSolicitado").value(solicitudProyectoSocio.getImporteSolicitado()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a SolicitudProyectoSocio with id filled
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoSocio)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithExistingId_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: existing SolicitudProyectoSocio
    SolicitudProyectoSocio updatedSolicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);
    updatedSolicitudProyectoSocio.setMesFin(12);

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willAnswer(new Answer<SolicitudProyectoSocio>() {
          @Override
          public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoSocio givenData = invocation.getArgument(0, SolicitudProyectoSocio.class);
            return givenData;
          }
        });

    // when: update SolicitudProyectoSocio
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoSocio.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoSocio)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudProyectoSocio is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("rolSocio.id").value(updatedSolicitudProyectoSocio.getRolSocio().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId")
            .value(updatedSolicitudProyectoSocio.getSolicitudProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicio").value(updatedSolicitudProyectoSocio.getMesInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFin").value(updatedSolicitudProyectoSocio.getMesFin()))
        .andExpect(MockMvcResultMatchers.jsonPath("numInvestigadores")
            .value(updatedSolicitudProyectoSocio.getNumInvestigadores()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeSolicitado")
            .value(updatedSolicitudProyectoSocio.getImporteSolicitado()));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudProyectoSocio with non existing id
    SolicitudProyectoSocio updatedSolicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.willThrow(new SolicitudProyectoSocioNotFoundException(updatedSolicitudProyectoSocio.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willThrow(new SolicitudProyectoSocioNotFoundException(updatedSolicitudProyectoSocio.getId()));

    // when: update SolicitudProyectoSocio
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoSocio.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoSocio)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
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
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudProyectoSocioNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  void findById_WithExistingId_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<SolicitudProyectoSocio>() {
      @Override
      public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarSolicitudProyectoSocio(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoSocio is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoSocioNotFoundException(id);
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
   * 
   * Solicitud proyecto periodo pago
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  void findAllSolicitudProyectoSocioPeriodoPago_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoSocioPeriodoPago para la
    // SolicitudProyectoSocio
    Long solicitudId = 1L;

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPago = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoSocioPeriodoPago.add(generarSolicitudProyectoSocioPeriodoPago(i, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoSocioPeriodoPagoService.findAllBySolicitudProyectoSocio(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoSocioPeriodoPago.size() ? solicitudProyectoSocioPeriodoPago.size()
              : toIndex;
          List<SolicitudProyectoSocioPeriodoPago> content = solicitudProyectoSocioPeriodoPago.subList(fromIndex,
              toIndex);
          Page<SolicitudProyectoSocioPeriodoPago> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoSocioPeriodoPago.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectosocioperiodopago", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudProyectoSocioPeriodoPago del 31
        // al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<SolicitudProyectoSocioPeriodoPago>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPagoRecuperado = solicitudProyectoSocioResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoSocioPeriodoPagoRecuperado.getImporte()).isEqualTo(new BigDecimal(i));
    }
  }

  /**
   * 
   * Solicitud proyecto equipo socio
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  void findAllSolicitudProyectoSocioEquipo_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoEquipo para la Solicitud
    Long solicitudId = 1L;

    List<SolicitudProyectoSocioEquipo> solicitudProyectoSocioEquipo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoSocioEquipo.add(generarSolicitudProyectoSocioEquipo(i, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoEquipoSocioService.findAllBySolicitudProyectoSocio(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoSocioEquipo.size() ? solicitudProyectoSocioEquipo.size() : toIndex;
          List<SolicitudProyectoSocioEquipo> content = solicitudProyectoSocioEquipo.subList(fromIndex, toIndex);
          Page<SolicitudProyectoSocioEquipo> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoSocioEquipo.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectosocioequipo", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudProyectoSocio del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<SolicitudProyectoSocioEquipo>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoSocioEquipo solicitudProyectoEquipoSocioRecuperado = solicitudProyectoEquipoSocioResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoEquipoSocioRecuperado.getId()).isEqualTo(new Long(i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  void findAllSolicitudProyectoSocioEquipo_Returns204() throws Exception {
    // given: Una lista vacia de SolicitudProyectoSocio para la
    // Solicitud
    Long solicitudId = 1L;
    List<SolicitudProyectoSocioEquipo> solicitudProyectoSocio = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoEquipoSocioService.findAllBySolicitudProyectoSocio(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<SolicitudProyectoSocioEquipo> pageResponse = new PageImpl<>(solicitudProyectoSocio, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectosocioequipo", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * Solicitud proyecto periodo justificación
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  void findAllSolicitudProyectoSocioPeriodoJustificacion_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoSocioPeriodoJustificacion para la
    // SolicitudProyectoSocio
    Long solicitudId = 1L;

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      long mesInicio = i + 1;
      long mesFin = mesInicio + 1;
      solicitudProyectoSocioPeriodoJustificacion
          .add(generarMockSolicitudProyectoSocioPeriodoJustificacion(i, (int) mesInicio, (int) mesFin, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoSocioPeriodoJustificacionService.findAllBySolicitudProyectoSocio(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoSocioPeriodoJustificacion.size()
              ? solicitudProyectoSocioPeriodoJustificacion.size()
              : toIndex;
          List<SolicitudProyectoSocioPeriodoJustificacion> content = solicitudProyectoSocioPeriodoJustificacion
              .subList(fromIndex, toIndex);
          Page<SolicitudProyectoSocioPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoSocioPeriodoJustificacion.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectosocioperiodojustificaciones",
                solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudProyectoSocioPeriodoJustificacion
        // del
        // 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<SolicitudProyectoSocioPeriodoJustificacion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacionRecuperado = solicitudProyectoSocioResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacionRecuperado.getId()).isEqualTo(new Long(i));
    }
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocio
   * 
   * @param solicitudProyectoSocioId
   * @param solicitudProyectoId
   * @return el objeto SolicitudProyectoSocio
   */
  private SolicitudProyectoSocio generarSolicitudProyectoSocio(Long solicitudProyectoSocioId, Long solicitudProyectoId,
      Long rolSocioId) {

    SolicitudProyectoSocio solicitudProyectoSocio = SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId)
        .solicitudProyectoId(solicitudProyectoId).rolSocio(RolSocio.builder().id(rolSocioId).build()).mesInicio(1)
        .mesFin(3).numInvestigadores(2).importeSolicitado(new BigDecimal("335")).empresaRef("002").build();

    return solicitudProyectoSocio;
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
        .importe(new BigDecimal(solicitudProyectoSocioPeriodoPagoId)).mes(3).build();
    return solicitudProyectoSocioPeriodoPago;
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioEquipo
   * 
   * @param solicitudProyectoEquipoSocioId
   * @param entidadesRelacionadasId
   * @return el objeto SolicitudProyectoSocioEquipo
   */
  private SolicitudProyectoSocioEquipo generarSolicitudProyectoSocioEquipo(Long solicitudProyectoEquipoSocioId,
      Long entidadesRelacionadasId) {

    SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo = SolicitudProyectoSocioEquipo.builder()
        .id(solicitudProyectoEquipoSocioId).solicitudProyectoSocioId(entidadesRelacionadasId)
        .rolProyecto(RolProyecto.builder().id(entidadesRelacionadasId).build())
        .personaRef("user-" + solicitudProyectoEquipoSocioId).mesInicio(1).mesFin(3).build();

    return solicitudProyectoSocioEquipo;
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioPeriodoJustificacion
   * 
   * @param id                  id del SolicitudProyectoSocioPeriodoJustificacion
   * @param mesInicial          Mes inicial
   * @param mesFinal            Mes final
   * @param tipo                Tipo justificacion
   * @param solicitudProyectoId Id SolicitudProyecto
   * @return el objeto SolicitudProyectoSocioPeriodoJustificacion
   */
  private SolicitudProyectoSocioPeriodoJustificacion generarMockSolicitudProyectoSocioPeriodoJustificacion(Long id,
      Integer mesInicial, Integer mesFinal, Long solicitudProyectoId) {
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = new SolicitudProyectoSocioPeriodoJustificacion();
    solicitudProyectoSocioPeriodoJustificacion.setId(id);
    solicitudProyectoSocioPeriodoJustificacion
        .setSolicitudProyectoSocioId(solicitudProyectoId == null ? 1 : solicitudProyectoId);
    solicitudProyectoSocioPeriodoJustificacion.setNumPeriodo(1);
    solicitudProyectoSocioPeriodoJustificacion.setMesInicial(mesInicial);
    solicitudProyectoSocioPeriodoJustificacion.setMesFinal(mesFinal);
    solicitudProyectoSocioPeriodoJustificacion.setFechaInicio(Instant.parse("2020-10-10T00:00:00Z"));
    solicitudProyectoSocioPeriodoJustificacion.setFechaFin(Instant.parse("2020-11-20T23:59:59Z"));
    solicitudProyectoSocioPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return solicitudProyectoSocioPeriodoJustificacion;
  }
}
