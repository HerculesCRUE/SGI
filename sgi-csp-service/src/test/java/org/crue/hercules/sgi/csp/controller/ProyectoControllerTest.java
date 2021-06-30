package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.EstadoProyectoService;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoAnualidadService;
import org.crue.hercules.sgi.csp.service.ProyectoAreaConocimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoClasificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoService;
import org.crue.hercules.sgi.csp.service.ProyectoDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.ProyectoFaseService;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
import org.crue.hercules.sgi.csp.service.ProyectoPaqueteTrabajoService;
import org.crue.hercules.sgi.csp.service.ProyectoPartidaService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoProrrogaService;
import org.crue.hercules.sgi.csp.service.ProyectoProyectoSgeService;
import org.crue.hercules.sgi.csp.service.ProyectoResponsableEconomicoService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioService;
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
 * ProyectoControllerTest
 */
@WebMvcTest(ProyectoController.class)
public class ProyectoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoService service;
  @MockBean
  private ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraservice;
  @MockBean
  private ProyectoHitoService proyectoHitoService;
  @MockBean
  private ProyectoPaqueteTrabajoService proyectoPaqueteTrabajoService;
  @MockBean
  private ProyectoFaseService proyectoFaseService;
  @MockBean
  private ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;
  @MockBean
  private ProyectoProrrogaService proyectoProrrogaService;
  @MockBean
  private ProyectoSocioService proyectoSocioService;
  @MockBean
  private ProyectoEntidadGestoraService proyectoEntidadGestoraService;
  @MockBean
  private EstadoProyectoService estadoProyectoService;

  @MockBean
  private ProyectoEquipoService proyectoEquipoService;

  @MockBean
  private ProyectoDocumentoService proyectoDocumentoService;

  @MockBean
  private ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService;

  @MockBean
  private ProyectoPeriodoSeguimientoDocumentoService proyectoPeriodoSeguimientoDocumentoService;

  @MockBean
  private ProyectoClasificacionService proyectoClasificacionService;

  @MockBean
  private ProyectoProyectoSgeService proyectoProyectoSgeService;

  @MockBean
  private ProyectoPartidaService proyectoPartidaService;

  @MockBean
  private ProrrogaDocumentoService prorrogaDocumentoService;

  @MockBean
  private ProyectoAreaConocimientoService proyectoAreaConocimientoService;

  @MockBean
  private ProyectoConceptoGastoService proyectoConceptoGastoService;

  @MockBean
  private ProyectoAnualidadService proyectoAnualidadService;

  @MockBean
  private ProyectoResponsableEconomicoService proyectoResponsableEconomicoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/proyectos";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_HITO = "/proyectohitos";
  private static final String PATH_FASE = "/proyectofases";
  private static final String PATH_PAQUETE_TRABAJO = "/proyectopaquetetrabajos";
  private static final String PATH_PROYECTO_SOCIO = "/proyectosocios";
  private static final String PATH_ENTIDAD_GESTORA = "/proyectoentidadgestoras";
  private static final String PATH_SEGUIMIENTO = "/proyectoperiodoseguimientos";
  private static final String PATH_PROYECTO_EQUIPO = "/proyectoequipos";
  private static final String PATH_PRORROGA = "/proyecto-prorrogas";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void create_ReturnsProyecto() throws Exception {
    // given: new Proyecto
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Proyecto>any())).willAnswer((InvocationOnMock invocation) -> {
      Proyecto newProyecto = new Proyecto();
      BeanUtils.copyProperties(invocation.getArgument(0), newProyecto);
      newProyecto.setId(1L);
      return newProyecto;
    });

    // when: create Proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new Proyecto is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(proyecto.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(proyecto.getUnidadGestionRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a Proyecto with id filled
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Proyecto>any())).willThrow(new IllegalArgumentException());

    // when: create Proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyecto() throws Exception {
    // given: Existing Proyecto to be updated
    Proyecto proyectoExistente = generarMockProyecto(1L);
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setObservaciones("observaciones actualizadas");

    BDDMockito.given(service.update(ArgumentMatchers.<Proyecto>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update Proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Proyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").value(proyectoExistente.getEstado().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(proyecto.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(proyectoExistente.getUnidadGestionRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.willThrow(new ProyectoNotFoundException(id)).given(service).update(ArgumentMatchers.<Proyecto>any());

    // when: update Proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyecto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-R" })
  public void reactivar_WithExistingId_ReturnProyecto() throws Exception {
    // given: existing id
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setActivo(false);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Proyecto proyectoDisabled = new Proyecto();
      BeanUtils.copyProperties(proyecto, proyectoDisabled);
      proyectoDisabled.setActivo(true);
      return proyectoDisabled;
    });

    // when: reactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, proyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Proyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

    // when: reactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-B" })
  public void desactivar_WithExistingId_ReturnProyecto() throws Exception {
    // given: existing id
    Proyecto proyecto = generarMockProyecto(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Proyecto proyectoDisabled = new Proyecto();
      BeanUtils.copyProperties(proyecto, proyectoDisabled);
      proyectoDisabled.setActivo(false);
      return proyectoDisabled;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, proyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Programa is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

    // when: desactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findById_WithExistingId_ReturnsProyecto() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyecto(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Proyecto is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("observaciones-001"))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value("2"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
          List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
          Page<Proyecto> pageResponse = new PageImpl<>(content, pageable, proyectos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<Proyecto> proyectosResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Proyecto>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = proyectosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data Proyecto
    BDDMockito.given(service.findAllRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Page<Proyecto> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllTodosRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
          List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
          Page<Proyecto> pageResponse = new PageImpl<>(content, pageable, proyectos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los TipoFase del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<Proyecto> proyectosResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Proyecto>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = proyectosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data Proyecto
    BDDMockito.given(service.findAllTodosRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Page<Proyecto> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO HITO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoHito_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoHito para el Proyecto
    Long proyectoId = 1L;

    List<ProyectoHito> proyectoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoHitos.add(generarMockProyectoHito(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(proyectoHitoService.findAllByProyecto(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoHito>>() {
          @Override
          public Page<ProyectoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoHitos.size() ? proyectoHitos.size() : toIndex;
            List<ProyectoHito> content = proyectoHitos.subList(fromIndex, toIndex);
            Page<ProyectoHito> page = new PageImpl<>(content, pageable, proyectoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoHito del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoHito> proyectoHitoResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoHito>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoHito proyectoHito = proyectoHitoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoHito.getComentario())
          .isEqualTo("comentario-proyecto-hito-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoHito_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProyectoHito para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoHito> proyectoHitos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(proyectoHitoService.findAllByProyecto(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoHito>>() {
          @Override
          public Page<ProyectoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProyectoHito> page = new PageImpl<>(proyectoHitos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_HITO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO FASE
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoFase_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoFase para el Proyecto
    Long proyectoId = 1L;

    List<ProyectoFase> proyectoFases = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoFases.add(generarMockProyectoFase(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(proyectoFaseService.findAllByProyecto(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoFase>>() {
          @Override
          public Page<ProyectoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoFases.size() ? proyectoFases.size() : toIndex;
            List<ProyectoFase> content = proyectoFases.subList(fromIndex, toIndex);
            Page<ProyectoFase> page = new PageImpl<>(content, pageable, proyectoFases.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASE, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoFase del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoFase> proyectoFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoFase>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoFase proyectoFase = proyectoFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoFase.getObservaciones())
          .isEqualTo("observaciones-proyecto-fase-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoFase_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProyectoFase para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoFase> proyectoFases = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(proyectoFaseService.findAllByProyecto(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoFase>>() {
          @Override
          public Page<ProyectoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProyectoFase> page = new PageImpl<>(proyectoFases, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_FASE, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO PAQUETE TRABAJO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoPaqueteTrabajo_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoPaqueteTrabajo para el Proyecto
    Long proyectoId = 1L;

    List<ProyectoPaqueteTrabajo> proyectoPaqueteTrabajos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoPaqueteTrabajos.add(generarMockProyectoPaqueteTrabajo(i, proyectoId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoPaqueteTrabajoService.findAllByProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPaqueteTrabajo>>() {
          @Override
          public Page<ProyectoPaqueteTrabajo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoPaqueteTrabajos.size() ? proyectoPaqueteTrabajos.size() : toIndex;
            List<ProyectoPaqueteTrabajo> content = proyectoPaqueteTrabajos.subList(fromIndex, toIndex);
            Page<ProyectoPaqueteTrabajo> page = new PageImpl<>(content, pageable, proyectoPaqueteTrabajos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PAQUETE_TRABAJO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoPaqueteTrabajo del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoPaqueteTrabajo> proyectoPaqueteTrabajoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ProyectoPaqueteTrabajo>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = proyectoPaqueteTrabajoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoPaqueteTrabajo.getNombre())
          .isEqualTo("proyecto-paquete-trabajo-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoPaqueteTrabajo_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProyectoPaqueteTrabajo para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoPaqueteTrabajo> proyectoPaqueteTrabajos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoPaqueteTrabajoService.findAllByProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPaqueteTrabajo>>() {
          @Override
          public Page<ProyectoPaqueteTrabajo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProyectoPaqueteTrabajo> page = new PageImpl<>(proyectoPaqueteTrabajos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PAQUETE_TRABAJO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO SOCIO
   * 
   */
  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoSocio_WithPaging_ReturnsProyectoSocioSubList() throws Exception {
    // given: 37 ProyectoSocio
    Long proyectoId = 1L;

    List<ProyectoSocio> proyectoSocios = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoSocios.add(generarMockProyectoSocio(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(proyectoSocioService.findAllByProyecto(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoSocios.size() ? proyectoSocios.size() : toIndex;
          List<ProyectoSocio> content = proyectoSocios.subList(fromIndex, toIndex);
          Page<ProyectoSocio> pageResponse = new PageImpl<>(content, pageable, proyectoSocios.size());
          return pageResponse;
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked ProyectoSocio are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<ProyectoSocio> actual = mapper.readValue(
        requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<ProyectoSocio>>() {
        });

    // containing id='31' to '37'
    for (int i = 31; i <= 37; i++) {
      ProyectoSocio item = actual.get(i - (page * pageSize) - 1);
      Assertions.assertThat(item.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoSocio_EmptyList_Returns204() throws Exception {
    // given: no data ProyectoSocio
    Long proyectoId = 1L;

    BDDMockito.given(proyectoSocioService.findAllByProyecto(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoSocio>>() {
          @Override
          public Page<ProyectoSocio> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoSocio> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_SOCIO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO Equipo
   * 
   */
  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoEquipo_WithPaging_ReturnsProyectoEquipoSubList() throws Exception {
    // given: 37 ProyectoEquipo
    Long proyectoId = 1L;

    List<ProyectoEquipo> proyectoEquipos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoEquipos.add(generarMockProyectoEquipo(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(proyectoEquipoService.findAllByProyecto(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoEquipos.size() ? proyectoEquipos.size() : toIndex;
          List<ProyectoEquipo> content = proyectoEquipos.subList(fromIndex, toIndex);
          Page<ProyectoEquipo> pageResponse = new PageImpl<>(content, pageable, proyectoEquipos.size());
          return pageResponse;
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_EQUIPO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked ProyectoEquipo are returned with the right page information
        // in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<ProyectoEquipo> actual = mapper.readValue(
        requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<ProyectoEquipo>>() {
        });

    // containing id='31' to '37'
    for (int i = 31; i <= 37; i++) {
      ProyectoEquipo item = actual.get(i - (page * pageSize) - 1);
      Assertions.assertThat(item.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoEquipo_EmptyList_Returns204() throws Exception {
    // given: no data ProyectoEquipo
    Long proyectoId = 1L;

    BDDMockito.given(proyectoEquipoService.findAllByProyecto(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoEquipo>>() {
          @Override
          public Page<ProyectoEquipo> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoEquipo> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PROYECTO_EQUIPO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO PERIODO SEGUIMIENTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoPeriodoSeguimiento_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoPeriodoSeguimiento para el Proyecto
    Long proyectoId = 1L;

    List<ProyectoPeriodoSeguimiento> proyectoPeriodoSeguimientos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoPeriodoSeguimientos.add(generarMockProyectoPeriodoSeguimiento(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoPeriodoSeguimientoService.findAllByProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoSeguimiento>>() {
          @Override
          public Page<ProyectoPeriodoSeguimiento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoPeriodoSeguimientos.size() ? proyectoPeriodoSeguimientos.size() : toIndex;
            List<ProyectoPeriodoSeguimiento> content = proyectoPeriodoSeguimientos.subList(fromIndex, toIndex);
            Page<ProyectoPeriodoSeguimiento> page = new PageImpl<>(content, pageable,
                proyectoPeriodoSeguimientos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SEGUIMIENTO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoPeriodoSeguimiento del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoPeriodoSeguimiento> proyectoPeriodoSeguimientoResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ProyectoPeriodoSeguimiento>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = proyectoPeriodoSeguimientoResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoPeriodoSeguimiento.getObservaciones()).isEqualTo("obs-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoPeriodoSeguimiento_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProyectoPeriodoSeguimiento para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoPeriodoSeguimiento> proyectoPeriodoSeguimientos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoPeriodoSeguimientoService.findAllByProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoSeguimiento>>() {
          @Override
          public Page<ProyectoPeriodoSeguimiento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProyectoPeriodoSeguimiento> page = new PageImpl<>(proyectoPeriodoSeguimientos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SEGUIMIENTO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO ENTIDAD GESTORA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoEntidadGestora_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoEntidadGestora para el Proyecto
    Long proyectoId = 1L;

    List<ProyectoEntidadGestora> proyectoEntidadGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoEntidadGestoras.add(generarMockProyectoEntidadGestora(i, proyectoId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoEntidadGestoraService.findAllByProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoEntidadGestora>>() {
          @Override
          public Page<ProyectoEntidadGestora> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoEntidadGestoras.size() ? proyectoEntidadGestoras.size() : toIndex;
            List<ProyectoEntidadGestora> content = proyectoEntidadGestoras.subList(fromIndex, toIndex);
            Page<ProyectoEntidadGestora> page = new PageImpl<>(content, pageable, proyectoEntidadGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoEntidadGestora del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoEntidadGestora> proyectoEntidadGestoraResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ProyectoEntidadGestora>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoEntidadGestora proyectoEntidadGestora = proyectoEntidadGestoraResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoEntidadGestora.getEntidadRef()).isEqualTo("entidad-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoEntidadGestora_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProyectoEntidadGestora para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoEntidadGestora> proyectoEntidadGestoras = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoEntidadGestoraService.findAllByProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoEntidadGestora>>() {
          @Override
          public Page<ProyectoEntidadGestora> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProyectoEntidadGestora> page = new PageImpl<>(proyectoEntidadGestoras, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO PRÓRROGA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoProrroga_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoProrroga para el Proyecto
    Long proyectoId = 1L;

    List<ProyectoProrroga> proyectoProrrogas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoProrrogas.add(generarMockProyectoProrroga(i, proyectoId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(proyectoProrrogaService.findAllByProyecto(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoProrroga>>() {
          @Override
          public Page<ProyectoProrroga> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoProrrogas.size() ? proyectoProrrogas.size() : toIndex;
            List<ProyectoProrroga> content = proyectoProrrogas.subList(fromIndex, toIndex);
            Page<ProyectoProrroga> page = new PageImpl<>(content, pageable, proyectoProrrogas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRORROGA, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoProrroga del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoProrroga> proyectoPeriodoSeguimientoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ProyectoProrroga>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoProrroga proyectoPeriodoSeguimiento = proyectoPeriodoSeguimientoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoPeriodoSeguimiento.getObservaciones())
          .isEqualTo("observaciones-proyecto-prorroga-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoProrroga_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProyectoProrroga para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoProrroga> proyectoProrrogas = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(proyectoProrrogaService.findAllByProyecto(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoProrroga>>() {
          @Override
          public Page<ProyectoProrroga> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProyectoProrroga> page = new PageImpl<>(proyectoProrrogas, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRORROGA, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * MOCKS
   * 
   */

  /**
   * Función que devuelve un objeto Proyecto
   * 
   * @param id id del Proyecto
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = generarMockEstadoProyecto(1L);

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-" + String.format("%03d", id));
    proyecto.setUnidadGestionRef("2");
    proyecto.setFechaInicio(Instant.now());
    proyecto.setFechaFin(Instant.now());
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setConfidencial(Boolean.FALSE);
    proyecto.setActivo(true);
    proyecto.setFechaBase(Instant.now());

    if (id != null) {
      proyecto.setEstado(estadoProyecto);
    }

    return proyecto;
  }

  /**
   * Función que devuelve un objeto EstadoProyecto
   * 
   * @param id id del EstadoProyecto
   * @return el objeto EstadoProyecto
   */
  private EstadoProyecto generarMockEstadoProyecto(Long id) {
    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario("Estado-" + id);
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    return estadoProyecto;
  }

  /**
   * Función que devuelve un objeto ProyectoHito
   * 
   * @param id id del ProyectoHito
   * @return el objeto ProyectoHito
   */
  private ProyectoHito generarMockProyectoHito(Long id) {
    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id == null ? 1 : id);
    tipoHito.setActivo(true);

    ProyectoHito proyectoHito = new ProyectoHito();
    proyectoHito.setId(id);
    proyectoHito.setProyectoId(id == null ? 1 : id);
    proyectoHito.setFecha(Instant.parse("2020-10-19T23:59:59Z"));
    proyectoHito.setComentario("comentario-proyecto-hito-" + String.format("%03d", id));
    proyectoHito.setGeneraAviso(true);
    proyectoHito.setTipoHito(tipoHito);

    return proyectoHito;
  }

  /**
   * Función que devuelve un objeto ProyectoFase
   * 
   * @param id id del ProyectoFase
   * @return el objeto ProyectoFase
   */
  private ProyectoFase generarMockProyectoFase(Long id) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);
    tipoFase.setActivo(true);

    ProyectoFase proyectoFase = new ProyectoFase();
    proyectoFase.setId(id);
    proyectoFase.setProyectoId(id == null ? 1 : id);
    proyectoFase.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoFase.setFechaFin(Instant.parse("2020-10-20T23:59:59Z"));
    proyectoFase.setObservaciones("observaciones-proyecto-fase-" + String.format("%03d", id));
    proyectoFase.setGeneraAviso(true);
    proyectoFase.setTipoFase(tipoFase);

    return proyectoFase;
  }

  /**
   * Función que devuelve un objeto ProyectoPaqueteTrabajo
   * 
   * @param id         id del ProyectoPaqueteTrabajo
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoPaqueteTrabajo
   */
  private ProyectoPaqueteTrabajo generarMockProyectoPaqueteTrabajo(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoPaqueteTrabajo.builder()
        .id(id)
        .proyectoId(proyectoId)
        .nombre("proyecto-paquete-trabajo-" + (id == null ? "" : String.format("%03d", id)))
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-01-15T23:59:59Z"))
        .personaMes(1D)
        .descripcion("descripcion-proyecto-paquete-trabajo-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }

  /**
   * Función que genera un ProyectoSocio
   * 
   * @param proyectoSocioId Identificador del {@link ProyectoSocio}
   * @return el ProyectoSocio
   */
  private ProyectoSocio generarMockProyectoSocio(Long proyectoSocioId) {

    String suffix = String.format("%03d", proyectoSocioId);

    // @formatter:off
    ProyectoSocio proyectoSocio = ProyectoSocio.builder()
        .id(proyectoSocioId)
        .proyectoId(1L)
        .empresaRef("empresa-" + suffix)
        .rolSocio(RolSocio.builder().id(1L).build())
        .fechaInicio(Instant.parse("2021-01-11T00:00:00Z"))
        .fechaFin(Instant.parse("2022-01-11T23:59:59Z"))
        .numInvestigadores(5)
        .importeConcedido(BigDecimal.valueOf(1000))
        .build();
    // @formatter:on

    return proyectoSocio;
  }

  /**
   * Función que genera un ProyectoEquipo
   * 
   * @param proyectoEquipoId Id proyecto equipo
   * @return proyecto equipo
   */
  private ProyectoEquipo generarMockProyectoEquipo(Long proyectoEquipoId) {

    ProyectoEquipo proyectoEquipo = ProyectoEquipo.builder().id(proyectoEquipoId).proyectoId(1L)
        .rolProyecto(RolProyecto.builder().id(1L).build()).fechaInicio(Instant.now()).fechaFin(Instant.now())
        .personaRef("001").horasDedicacion(new Double(2)).build();

    return proyectoEquipo;

  }

  /*
   * Función que devuelve un objeto ProyectoPeriodoSeguimiento
   * 
   * @param id id del ProyectoPeriodoSeguimiento
   * 
   * @return el objeto ProyectoPeriodoSeguimiento
   */
  private ProyectoPeriodoSeguimiento generarMockProyectoPeriodoSeguimiento(Long id) {
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = new ProyectoPeriodoSeguimiento();
    proyectoPeriodoSeguimiento.setId(id);
    proyectoPeriodoSeguimiento.setProyectoId(id == null ? 1 : id);
    proyectoPeriodoSeguimiento.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoPeriodoSeguimiento.setFechaFin(Instant.parse("2020-12-19T23:59:59Z"));
    proyectoPeriodoSeguimiento.setObservaciones("obs-" + id);

    return proyectoPeriodoSeguimiento;
  }

  /**
   * Función que devuelve un objeto ProyectoEntidadGestora
   * 
   * @param id         id del ProyectoEntidadGestora
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoEntidadGestora
   */
  private ProyectoEntidadGestora generarMockProyectoEntidadGestora(Long id, Long proyectoId) {
    // @formatter:off
    return ProyectoEntidadGestora.builder()
        .id(id)
        .proyectoId(proyectoId)
        .entidadRef("entidad-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ProyectoProrroga
   * 
   * @param id         id del ProyectoProrroga
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoProrroga
   */
  private ProyectoProrroga generarMockProyectoProrroga(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoProrroga.builder()
        .id(id)
        .proyectoId(proyectoId)
        .numProrroga(1)
        .fechaConcesion(Instant.parse("2020-01-01T00:00:00Z"))
        .tipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE)
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .importe(BigDecimal.valueOf(123.45))
        .observaciones("observaciones-proyecto-prorroga-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }

}
