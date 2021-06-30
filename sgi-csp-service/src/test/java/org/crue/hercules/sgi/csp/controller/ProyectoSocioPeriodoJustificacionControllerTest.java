package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
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
 * ProyectoSocioPeriodoJustificacionControllerTest
 */
@WebMvcTest(ProyectoSocioPeriodoJustificacionController.class)
public class ProyectoSocioPeriodoJustificacionControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoSocioPeriodoJustificacionService service;

  @MockBean
  private ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectosocioperiodojustificaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_ReturnsProyectoSocioPeriodoJustificacion() throws Exception {
    // given: new ProyectoSocioPeriodoJustificacion
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoSocioPeriodoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoSocioPeriodoJustificacion givenData = invocation.getArgument(0,
              ProyectoSocioPeriodoJustificacion.class);
          ProyectoSocioPeriodoJustificacion newData = new ProyectoSocioPeriodoJustificacion();
          BeanUtils.copyProperties(givenData, newData);
          newData.setId(proyectoSocioPeriodoJustificacion.getId());
          return newData;
        });

    // when: create ProyectoSocioPeriodoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoSocioPeriodoJustificacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProyectoSocioPeriodoJustificacion is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoSocioId")
            .value(proyectoSocioPeriodoJustificacion.getProyectoSocioId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio")
            .value(proyectoSocioPeriodoJustificacion.getFechaInicio().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin")
            .value(proyectoSocioPeriodoJustificacion.getFechaFin().toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ProyectoSocioPeriodoJustificacion with id filled
    ProyectoSocioPeriodoJustificacion newProyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoSocioPeriodoJustificacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ProyectoSocioPeriodoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newProyectoSocioPeriodoJustificacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyectoSocioPeriodoJustificacionList() throws Exception {
    // given: ProyectoSocioPeriodoJustificacion / actualizado,

    ProyectoSocioPeriodoJustificacion updatedProyectoSocioPeriodoJustificacionExistente = generarMockProyectoSocioPeriodoJustificacion(
        4L);
    ProyectoSocioPeriodoJustificacion updatedProyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        4L);

    updatedProyectoSocioPeriodoJustificacion.setFechaRecepcion(Instant.now());

    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedProyectoSocioPeriodoJustificacionExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updatedProyectoSocioPeriodoJustificacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoSocioPeriodoJustificacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void updateProyectoSocioPeriodoJustificacionesProyectoSocio_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    BDDMockito.willThrow(new ProyectoSocioPeriodoJustificacionNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProyectoSocioPeriodoJustificacion>any(), ArgumentMatchers.anyLong());

    // when: updateProyectoSocioPeriodoJustificacionesProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoSocioPeriodoJustificacion.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoSocioPeriodoJustificacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findById_WithExistingId_ReturnsProyectoSocioPeriodoJustificacion() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoSocioPeriodoJustificacion(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoSocioPeriodoJustificacion is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoSocioId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("numPeriodo").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-10T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2021-10-10T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicioPresentacion").value("2020-10-10T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFinPresentacion").value("2020-11-20T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("observaciones-1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoSocioPeriodoJustificacionNotFoundException(1L);
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
   * 
   * SOCIO PERIODO JUSTIFICACION DOCUMENTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoSocioPeriodoJustificacionDocumento_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoSocioPeriodoJustificacionDocumento para la
    // ProyectoSocioPeriodoJustificacion
    Long proyectoSocioId = 1L;

    List<ProyectoSocioPeriodoJustificacion> proyectoSociosPeriodoJustificaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoSociosPeriodoJustificaciones.add(generarMockProyectoSocioPeriodoJustificacion(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoSocioPeriodoJustificacionDocumentoService.findAllByProyectoSocioPeriodoJustificacion(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoSociosPeriodoJustificaciones.size() ? proyectoSociosPeriodoJustificaciones.size()
              : toIndex;
          List<ProyectoSocioPeriodoJustificacion> content = proyectoSociosPeriodoJustificaciones.subList(fromIndex,
              toIndex);
          Page<ProyectoSocioPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              proyectoSociosPeriodoJustificaciones.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/proyectosocioperiodojustificaciondocumentos",
                proyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoSocioEntidadConvocante del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoSocioPeriodoJustificacion> proyectoSociosPeriodoJustificacionesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ProyectoSocioPeriodoJustificacion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = proyectoSociosPeriodoJustificacionesResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoSocioPeriodoJustificacion_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProyectoSocioPeriodoJustificacion para la
    // ProyectoSocio
    Long proyectoSocioId = 1L;
    List<ProyectoSocioPeriodoJustificacion> proyectoSociosPeriodoJustificaciones = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoSocioPeriodoJustificacionDocumentoService.findAllByProyectoSocioPeriodoJustificacion(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<ProyectoSocioPeriodoJustificacion> pageResponse = new PageImpl<>(proyectoSociosPeriodoJustificaciones,
              pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/proyectosocioperiodojustificaciondocumentos",
                proyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ProyectoSocioPeriodoJustificacion
   * 
   * @param id id del ProyectoSocioPeriodoJustificacion
   * 
   * @return el objeto ProyectoSocioPeriodoJustificacion
   */
  private ProyectoSocioPeriodoJustificacion generarMockProyectoSocioPeriodoJustificacion(Long id) {
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = new ProyectoSocioPeriodoJustificacion();
    proyectoSocioPeriodoJustificacion.setId(id);
    proyectoSocioPeriodoJustificacion.setProyectoSocioId(id == null ? 1 : id);
    proyectoSocioPeriodoJustificacion.setNumPeriodo(1);
    proyectoSocioPeriodoJustificacion.setFechaInicio(Instant.parse("2020-10-10T00:00:00Z"));
    proyectoSocioPeriodoJustificacion.setFechaFin(Instant.parse("2021-10-10T23:59:59Z"));
    proyectoSocioPeriodoJustificacion.setFechaInicioPresentacion(Instant.parse("2020-10-10T00:00:00Z"));
    proyectoSocioPeriodoJustificacion.setFechaFinPresentacion(Instant.parse("2020-11-20T23:59:59Z"));
    proyectoSocioPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return proyectoSocioPeriodoJustificacion;
  }

}
