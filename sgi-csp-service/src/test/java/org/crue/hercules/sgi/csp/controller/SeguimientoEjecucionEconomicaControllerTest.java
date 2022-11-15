package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.converter.ProyectoPeriodoSeguimientoConverter;
import org.crue.hercules.sgi.csp.converter.ProyectoSeguimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.converter.RequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoSeguimientoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.SeguimientoJustificacionAnualidad;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput.ProyectoProyectoSgeOutput;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionSeguimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoSeguimientoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.RequerimientoJustificacionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * SeguimientoEjecucionEconomicaControllerTest
 */
@WebMvcTest(SeguimientoEjecucionEconomicaController.class)
class SeguimientoEjecucionEconomicaControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoService proyectoService;
  @MockBean
  private ProyectoPeriodoJustificacionService proyectoPeriodoJustificacionService;
  @MockBean
  private ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;
  @MockBean
  private ProyectoPeriodoSeguimientoConverter proyectoPeriodoSeguimientoConverter;
  @MockBean
  private RequerimientoJustificacionService requerimientoJustificacionService;
  @MockBean
  private RequerimientoJustificacionConverter requerimientoJustificacionConverter;
  @MockBean
  private ProyectoSeguimientoJustificacionService proyectoSeguimientoJustificacionService;
  @MockBean
  private ProyectoSeguimientoJustificacionConverter proyectoSeguimientoJustificacionConverter;
  @MockBean
  private ProyectoPeriodoJustificacionSeguimientoService proyectoPeriodoJustificacionSeguimientoService;

  private static final String CONTROLLER_BASE_PATH = SeguimientoEjecucionEconomicaController.REQUEST_MAPPING;
  private static final String PATH_PROYECTOS = SeguimientoEjecucionEconomicaController.PATH_PROYECTOS;
  private static final String PATH_PERIODO_JUSTIFICACION = SeguimientoEjecucionEconomicaController.PATH_PERIODO_JUSTIFICACION;
  private static final String PATH_PERIODO_SEGUIMIENTO = SeguimientoEjecucionEconomicaController.PATH_PERIODO_SEGUIMIENTO;
  private static final String PATH_REQUERIMIENTO_JUSTIFICACION = SeguimientoEjecucionEconomicaController.PATH_REQUERIMIENTO_JUSTIFICACION;
  private static final String PATH_SEGUIMIENTO_JUSTIFICACION = SeguimientoEjecucionEconomicaController.PATH_SEGUIMIENTO_JUSTIFICACION;
  private static final String PATH_SEGUIMIENTO_JUSTIFICACION_ANUALIDAD = SeguimientoEjecucionEconomicaController.PATH_SEGUIMIENTO_JUSTIFICACION_ANUALIDAD;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  void findProyectosSeguimientoEjecucionEconomica_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoSeguimientoEjecucionEconomica
    String proyectoSgeRef = "1";
    List<ProyectoSeguimientoEjecucionEconomica> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyectoSeguimientoEjecucionEconomica(i, "Proyecto-" + String.format("%03d", i)));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(proyectoService.findProyectosSeguimientoEjecucionEconomica(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
          List<ProyectoSeguimientoEjecucionEconomica> content = proyectos.subList(fromIndex, toIndex);
          Page<ProyectoSeguimientoEjecucionEconomica> pageResponse = new PageImpl<>(content, pageable,
              proyectos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PROYECTOS, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoSeguimientoEjecucionEconomica del
        // 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<ProyectoSeguimientoEjecucionEconomica> proyectosResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoSeguimientoEjecucionEconomica>>() {
        });
    for (int i = 31; i <= 37; i++) {
      ProyectoSeguimientoEjecucionEconomica proyecto = proyectosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyecto.getNombre()).isEqualTo("Proyecto-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  void findProyectosSeguimientoEjecucionEconomica_EmptyList_Returns204() throws Exception {
    // given: no data ProyectoSeguimientoEjecucionEconomica
    String proyectoSgeRef = "1";
    BDDMockito
        .given(proyectoService.findProyectosSeguimientoEjecucionEconomica(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoSeguimientoEjecucionEconomica>>() {
          @Override
          public Page<ProyectoSeguimientoEjecucionEconomica> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoSeguimientoEjecucionEconomica> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PROYECTOS, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  void findProyectoPeriodosJustificacion_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoPeriodoJustificacion
    String proyectoSgeRef = "1";
    List<ProyectoPeriodoJustificacion> periodosJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      periodosJustificacion.add(generarMockProyectoPeriodoJustificacion(i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(proyectoPeriodoJustificacionService.findAllByProyectoSgeRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > periodosJustificacion.size() ? periodosJustificacion.size() : toIndex;
          List<ProyectoPeriodoJustificacion> content = periodosJustificacion.subList(fromIndex, toIndex);
          Page<ProyectoPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              periodosJustificacion.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PERIODO_JUSTIFICACION, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoPeriodoJustificacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<ProyectoPeriodoJustificacion> periodosJustificacionResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoPeriodoJustificacion>>() {
        });
    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoJustificacion periodoJustificacion = periodosJustificacionResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(periodoJustificacion.getObservaciones())
          .isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  void findProyectoPeriodosJustificacion_EmptyList_Returns204() throws Exception {
    // given: no data ProyectoSeguimientoEjecucionEconomica
    String proyectoSgeRef = "1";
    BDDMockito
        .given(proyectoPeriodoJustificacionService.findAllByProyectoSgeRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoSeguimientoEjecucionEconomica>>() {
          @Override
          public Page<ProyectoSeguimientoEjecucionEconomica> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoSeguimientoEjecucionEconomica> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PERIODO_JUSTIFICACION, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private ProyectoSeguimientoEjecucionEconomica generarMockProyectoSeguimientoEjecucionEconomica(Long id,
      String nombre) {
    return ProyectoSeguimientoEjecucionEconomica.builder()
        .id(id)
        .nombre(nombre)
        .build();
  }

  private ProyectoPeriodoJustificacion generarMockProyectoPeriodoJustificacion(Long id) {
    final String observacionesSuffix = id != null ? String.format("%03d", id) : "001";
    return ProyectoPeriodoJustificacion.builder()
        .id(id)
        .observaciones("observaciones-" + observacionesSuffix)
        .build();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  void findProyectoPeriodosSeguimiento_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoPeriodoSeguimiento
    String proyectoSgeRef = "1";
    List<ProyectoPeriodoSeguimiento> periodosJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      periodosJustificacion.add(generarMockProyectoPeriodoSeguimiento(i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(proyectoPeriodoSeguimientoService.findAllByProyectoSgeRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > periodosJustificacion.size() ? periodosJustificacion.size() : toIndex;
          List<ProyectoPeriodoSeguimiento> content = periodosJustificacion.subList(fromIndex, toIndex);
          Page<ProyectoPeriodoSeguimiento> pageResponse = new PageImpl<>(content, pageable,
              periodosJustificacion.size());
          return pageResponse;
        });
    BDDMockito
        .given(proyectoPeriodoSeguimientoConverter.convert(ArgumentMatchers.<Page<ProyectoPeriodoSeguimiento>>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoSeguimientoOutput>>() {
          @Override
          public Page<ProyectoPeriodoSeguimientoOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoPeriodoSeguimiento> pageInput = invocation.getArgument(0);
            List<ProyectoPeriodoSeguimientoOutput> content = pageInput.getContent().stream().map(input -> {
              return generarMockProyectoPeriodoSeguimientoOutput(input);
            }).collect(Collectors.toList());
            Page<ProyectoPeriodoSeguimientoOutput> pageOutput = new PageImpl<>(content,
                pageInput.getPageable(),
                pageInput.getTotalElements());
            return pageOutput;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PERIODO_SEGUIMIENTO, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoPeriodoSeguimiento del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<ProyectoPeriodoSeguimientoOutput> periodosJustificacionResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoPeriodoSeguimientoOutput>>() {
        });
    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoSeguimientoOutput periodoJustificacion = periodosJustificacionResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(periodoJustificacion.getObservaciones())
          .isEqualTo("obs-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  void findProyectoPeriodosSeguimiento_EmptyList_Returns204() throws Exception {
    // given: no data ProyectoPeriodoSeguimiento
    String proyectoSgeRef = "1";
    BDDMockito
        .given(proyectoPeriodoSeguimientoService.findAllByProyectoSgeRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoSeguimiento>>() {
          @Override
          public Page<ProyectoPeriodoSeguimiento> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoPeriodoSeguimiento> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PERIODO_SEGUIMIENTO, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimiento
   * 
   * @param id id del ProyectoPeriodoSeguimiento
   * @return el objeto ProyectoPeriodoSeguimiento
   */
  private ProyectoPeriodoSeguimiento generarMockProyectoPeriodoSeguimiento(Long id) {
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = new ProyectoPeriodoSeguimiento();
    proyectoPeriodoSeguimiento.setId(id);
    proyectoPeriodoSeguimiento.setProyectoId(id == null ? 1 : id);
    proyectoPeriodoSeguimiento.setNumPeriodo(1);
    proyectoPeriodoSeguimiento.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoPeriodoSeguimiento.setFechaFin(Instant.parse("2020-12-19T23:59:59Z"));
    proyectoPeriodoSeguimiento.setObservaciones("obs-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimiento.setTipoSeguimiento(TipoSeguimiento.FINAL);

    return proyectoPeriodoSeguimiento;
  }

  private ProyectoPeriodoSeguimientoOutput generarMockProyectoPeriodoSeguimientoOutput(
      ProyectoPeriodoSeguimiento input) {
    return generarMockProyectoPeriodoSeguimientoOutput(input.getId(), input.getFechaFin(), input.getFechaInicio(),
        input.getNumPeriodo(), input.getObservaciones(), input.getProyectoId(), input.getTipoSeguimiento());
  }

  private ProyectoPeriodoSeguimientoOutput generarMockProyectoPeriodoSeguimientoOutput(Long id, Instant fechaFin,
      Instant fechaInicio, Integer numPeriodo, String observaciones, Long proyectoId, TipoSeguimiento tipoSeguimiento) {
    return ProyectoPeriodoSeguimientoOutput.builder()
        .id(id)
        .fechaFin(fechaFin)
        .fechaInicio(fechaInicio)
        .numPeriodo(numPeriodo)
        .observaciones(observaciones)
        .proyectoId(proyectoId)
        .tipoSeguimiento(tipoSeguimiento)
        .build();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  public void findRequerimientosJustificacion_ReturnsPage() throws Exception {
    // given: Una lista con 37 RequerimientoJustificacion
    String proyectoSgeRef = "1";
    List<RequerimientoJustificacion> requerimientosJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      requerimientosJustificacion.add(generarMockRequerimientoJustificacion(i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(requerimientoJustificacionService.findAllByProyectoSgeRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > requerimientosJustificacion.size() ? requerimientosJustificacion.size() : toIndex;
          List<RequerimientoJustificacion> content = requerimientosJustificacion.subList(fromIndex, toIndex);
          Page<RequerimientoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              requerimientosJustificacion.size());
          return pageResponse;
        });

    BDDMockito
        .given(requerimientoJustificacionConverter.convert(ArgumentMatchers.<Page<RequerimientoJustificacion>>any()))
        .willAnswer(new Answer<Page<RequerimientoJustificacionOutput>>() {
          @Override
          public Page<RequerimientoJustificacionOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<RequerimientoJustificacion> pageInput = invocation.getArgument(0);
            List<RequerimientoJustificacionOutput> content = pageInput.getContent().stream().map(input -> {
              return generarMockRequerimientoJustificacionOutput(input);
            }).collect(Collectors.toList());
            Page<RequerimientoJustificacionOutput> pageOutput = new PageImpl<>(content, pageInput.getPageable(),
                pageInput.getTotalElements());
            return pageOutput;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_REQUERIMIENTO_JUSTIFICACION, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los RequerimientoJustificacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<RequerimientoJustificacion> requerimientosJustificacionResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<RequerimientoJustificacion>>() {
        });
    for (int i = 31; i <= 37; i++) {
      RequerimientoJustificacion requerimientoJustificacion = requerimientosJustificacionResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(requerimientoJustificacion.getObservaciones())
          .isEqualTo("RequerimientoJustificacion-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  public void findRequerimientosJustificacion_EmptyList_Returns204() throws Exception {
    // given: no data RequerimientoJustificacion
    String proyectoSgeRef = "1";
    BDDMockito
        .given(requerimientoJustificacionService.findAllByProyectoSgeRef(ArgumentMatchers.anyString(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RequerimientoJustificacion>>() {
          @Override
          public Page<RequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Page<RequerimientoJustificacion> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });
    BDDMockito
        .given(requerimientoJustificacionConverter.convert(ArgumentMatchers.<Page<RequerimientoJustificacion>>any()))
        .willAnswer(new Answer<Page<RequerimientoJustificacionOutput>>() {
          @Override
          public Page<RequerimientoJustificacionOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<RequerimientoJustificacionOutput> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });
    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(
            CONTROLLER_BASE_PATH + PATH_REQUERIMIENTO_JUSTIFICACION, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  public void findSeguimientosJustificacion_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoSeguimientoJustificacion
    String proyectoSgeRef = "1";
    List<ProyectoSeguimientoJustificacion> proyectoSeguimientosJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoSeguimientosJustificacion.add(generarMockProyectoSeguimientoJustificacion(i, i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(proyectoSeguimientoJustificacionService.findAllByProyectoSgeRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoSeguimientosJustificacion.size() ? proyectoSeguimientosJustificacion.size()
              : toIndex;
          List<ProyectoSeguimientoJustificacion> content = proyectoSeguimientosJustificacion.subList(fromIndex,
              toIndex);
          Page<ProyectoSeguimientoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              proyectoSeguimientosJustificacion.size());
          return pageResponse;
        });

    BDDMockito
        .given(proyectoSeguimientoJustificacionConverter
            .convert(ArgumentMatchers.<Page<ProyectoSeguimientoJustificacion>>any()))
        .willAnswer(new Answer<Page<ProyectoSeguimientoJustificacionOutput>>() {
          @Override
          public Page<ProyectoSeguimientoJustificacionOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoSeguimientoJustificacion> pageInput = invocation.getArgument(0);
            List<ProyectoSeguimientoJustificacionOutput> content = pageInput.getContent().stream().map(input -> {
              return generarMockProyectoSeguimientoJustificacionOutput(input);
            }).collect(Collectors.toList());
            Page<ProyectoSeguimientoJustificacionOutput> pageOutput = new PageImpl<>(content, pageInput.getPageable(),
                pageInput.getTotalElements());
            return pageOutput;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_SEGUIMIENTO_JUSTIFICACION, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoSeguimientoJustificacion del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<ProyectoSeguimientoJustificacion> response = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoSeguimientoJustificacion>>() {
        });
    for (int i = 31; i <= 37; i++) {
      ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion = response
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoSeguimientoJustificacion.getJustificanteReintegro())
          .isEqualTo("ProyectoSeguimientoJustificacion-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  public void findSeguimientosJustificacion_EmptyList_Returns204() throws Exception {
    // given: no data ProyectoSeguimientoJustificacion
    String proyectoSgeRef = "1";
    BDDMockito
        .given(proyectoSeguimientoJustificacionService.findAllByProyectoSgeRef(ArgumentMatchers.anyString(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoSeguimientoJustificacion>>() {
          @Override
          public Page<ProyectoSeguimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoSeguimientoJustificacion> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });
    BDDMockito
        .given(
            proyectoSeguimientoJustificacionConverter
                .convert(ArgumentMatchers.<Page<ProyectoSeguimientoJustificacion>>any()))
        .willAnswer(new Answer<Page<ProyectoSeguimientoJustificacionOutput>>() {
          @Override
          public Page<ProyectoSeguimientoJustificacionOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoSeguimientoJustificacionOutput> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });
    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(
            CONTROLLER_BASE_PATH + PATH_SEGUIMIENTO_JUSTIFICACION, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  public void findSeguimientosJustificacionAnualidad_ReturnsList() throws Exception {
    // given: Una lista con 20 SeguimientoJustificacionAnualidad
    String proyectoSgeRef = "1";
    List<SeguimientoJustificacionAnualidad> seguimientosJustificacionAnualidad = new ArrayList<>();
    for (long i = 1; i <= 20; i++) {
      seguimientosJustificacionAnualidad.add(generarMockSeguimientoJustificacionAnualidad(i));
    }
    BDDMockito
        .given(proyectoPeriodoJustificacionSeguimientoService
            .findSeguimientosJustificacionAnualidadByProyectoSgeRef(ArgumentMatchers.<String>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          List<SeguimientoJustificacionAnualidad> listResponse = seguimientosJustificacionAnualidad;
          return listResponse;
        });

    // when: Get SeguimientoJustificacionAnualidad para proyectoSgeRef
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_SEGUIMIENTO_JUSTIFICACION_ANUALIDAD, proyectoSgeRef)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la lista con los SeguimientoJustificacionAnualidad
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(20))).andReturn();
    List<SeguimientoJustificacionAnualidad> response = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<SeguimientoJustificacionAnualidad>>() {
        });
    for (int i = 0; i < 20; i++) {
      SeguimientoJustificacionAnualidad seguimientoJustificacionAnualidad = response.get(i);
      Assertions.assertThat(seguimientoJustificacionAnualidad.getIdentificadorJustificacion())
          .isEqualTo("identificador-justificacion-" + String.format("%03d", i + 1));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  public void findSeguimientosJustificacionAnualidad_EmptyList_Returns204() throws Exception {
    // given: no data SeguimientoJustificacionAnualidad
    String proyectoSgeRef = "1";
    BDDMockito
        .given(proyectoPeriodoJustificacionSeguimientoService
            .findSeguimientosJustificacionAnualidadByProyectoSgeRef(ArgumentMatchers.anyString()))
        .willAnswer(new Answer<List<SeguimientoJustificacionAnualidad>>() {
          @Override
          public List<SeguimientoJustificacionAnualidad> answer(InvocationOnMock invocation) throws Throwable {
            List<SeguimientoJustificacionAnualidad> list = Collections.emptyList();
            return list;
          }
        });
    // when: get SeguimientoJustificacionAnualidad para proyectoSgeRef
    mockMvc
        .perform(MockMvcRequestBuilders.get(
            CONTROLLER_BASE_PATH + PATH_SEGUIMIENTO_JUSTIFICACION_ANUALIDAD, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id) {
    String observacionSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockRequerimientoJustificacion(id, "RequerimientoJustificacion-" + observacionSuffix,
        null);
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id, String observaciones,
      Long requerimientoPrevioId) {
    return RequerimientoJustificacion.builder()
        .id(id)
        .observaciones(observaciones)
        .requerimientoPrevioId(requerimientoPrevioId)
        .build();
  }

  private RequerimientoJustificacionOutput generarMockRequerimientoJustificacionOutput(
      RequerimientoJustificacion requerimientoJustificacion) {
    return generarMockRequerimientoJustificacionOutput(requerimientoJustificacion.getId(),
        requerimientoJustificacion.getObservaciones(), requerimientoJustificacion.getRequerimientoPrevioId());
  }

  private RequerimientoJustificacionOutput generarMockRequerimientoJustificacionOutput(Long id, String observaciones,
      Long requerimientoPrevioId) {
    return RequerimientoJustificacionOutput.builder()
        .id(id)
        .observaciones(observaciones)
        .requerimientoPrevioId(requerimientoPrevioId)
        .build();
  }

  private ProyectoSeguimientoJustificacion generarMockProyectoSeguimientoJustificacion(Long id,
      Long proyectoProyectoSgeId) {
    String justificanteReintegroSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockProyectoSeguimientoJustificacion(id, generarMockProyectoProyectoSge(proyectoProyectoSgeId),
        "ProyectoSeguimientoJustificacion-" + justificanteReintegroSuffix);
  }

  private ProyectoSeguimientoJustificacion generarMockProyectoSeguimientoJustificacion(Long id,
      ProyectoProyectoSge proyectoProyectoSge, String justificanteReintegro) {
    return ProyectoSeguimientoJustificacion.builder()
        .id(id)
        .proyectoProyectoSge(proyectoProyectoSge)
        .justificanteReintegro(justificanteReintegro)
        .build();
  }

  private ProyectoProyectoSge generarMockProyectoProyectoSge(Long id) {
    String proyectoSgeRef = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return ProyectoProyectoSge.builder().id(id).proyectoSgeRef(proyectoSgeRef).build();
  }

  private ProyectoSeguimientoJustificacionOutput generarMockProyectoSeguimientoJustificacionOutput(
      ProyectoSeguimientoJustificacion input) {
    return generarMockProyectoSeguimientoJustificacionOutput(input.getId(),
        generarMockProyectoProyectoSgeOutput(input.getProyectoProyectoSge()),
        input.getJustificanteReintegro());
  }

  private ProyectoSeguimientoJustificacionOutput generarMockProyectoSeguimientoJustificacionOutput(Long id,
      ProyectoProyectoSgeOutput proyectoProyectoSge, String justificanteReintegro) {
    return ProyectoSeguimientoJustificacionOutput.builder()
        .id(id)
        .proyectoProyectoSge(proyectoProyectoSge)
        .justificanteReintegro(justificanteReintegro)
        .build();
  }

  private ProyectoProyectoSgeOutput generarMockProyectoProyectoSgeOutput(ProyectoProyectoSge input) {
    return generarMockProyectoProyectoSgeOutput(input.getId(), input.getProyectoSgeRef());
  }

  private ProyectoProyectoSgeOutput generarMockProyectoProyectoSgeOutput(Long id, String proyectoSgeRef) {
    return ProyectoProyectoSgeOutput.builder().id(id).proyectoSgeRef(proyectoSgeRef).build();
  }

  private SeguimientoJustificacionAnualidad generarMockSeguimientoJustificacionAnualidad(Long index) {
    String justificanteReintegroSuffix = index != null ? String.format("%03d", index) : String.format("%03d", 1);
    return generarMockSeguimientoJustificacionAnualidad(1L, 1L, 1L,
        "identificador-justificacion-" + justificanteReintegroSuffix);
  }

  private SeguimientoJustificacionAnualidad generarMockSeguimientoJustificacionAnualidad(
      Long proyectoId, Long proyectoPeriodoJustificacionId, Long proyectoPeriodoJustificacionSeguimientoId,
      String identificadorJustificacion) {
    return SeguimientoJustificacionAnualidad.builder()
        .proyectoId(proyectoId)
        .proyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId)
        .proyectoPeriodoJustificacionSeguimientoId(proyectoPeriodoJustificacionSeguimientoId)
        .identificadorJustificacion(identificadorJustificacion)
        .build();
  }
}
