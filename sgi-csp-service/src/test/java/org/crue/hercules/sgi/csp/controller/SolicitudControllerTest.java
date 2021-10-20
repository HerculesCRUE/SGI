package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.EstadoSolicitudService;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;
import org.crue.hercules.sgi.csp.service.SolicitudHitoService;
import org.crue.hercules.sgi.csp.service.SolicitudModalidadService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoAreaConocimientoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoClasificacionService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoResponsableEconomicoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
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
 * SolicitudControllerTest
 */
@WebMvcTest(SolicitudController.class)
public class SolicitudControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudService service;

  @MockBean
  private SolicitudModalidadService solicitudModalidadService;

  @MockBean
  private EstadoSolicitudService estadoSolicitudService;

  @MockBean
  private SolicitudDocumentoService solicitudDocumentoService;

  @MockBean
  private SolicitudHitoService solicitudHitoService;

  @MockBean
  private SolicitudProyectoSocioService solicitudProyectoSocioService;

  @MockBean
  private SolicitudProyectoService solicitudProyectoService;

  @MockBean
  private SolicitudProyectoEquipoService solicitudProyectoEquipoService;

  @MockBean
  private SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService;

  @MockBean
  private SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;

  @MockBean
  private SolicitudProyectoClasificacionService solicitudProyectoClasificacionService;

  @MockBean
  private SolicitudProyectoAreaConocimientoService solicitudProyectoAreaConocimientoService;

  @MockBean
  private SolicitudProyectoResponsableEconomicoService solicitudProyectoResponsableEconomicoService;

  @MockBean
  private SolicitudProyectoEntidadService solicitudProyectoEntidadService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/solicitudes";
  private static final String PATH_SOLICITUD_MODALIDADES = "/solicitudmodalidades";
  private static final String PATH_ESTADOS_SOLICITUD = "/estadosolicitudes";
  private static final String PATH_ENTIDAD_FINANCIADORA_AJENA = "/solicitudproyectoentidadfinanciadoraajenas";
  private static final String PATH_SOLICITUD_PROYECTO_PRESUPUESTOS = "/solicitudproyectopresupuestos";
  private static final String PATH_TODOS = "/todos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  public void create_ReturnsSolicitud() throws Exception {
    // given: new Solicitud
    Solicitud solicitud = generarMockSolicitud(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Solicitud>any())).willAnswer((InvocationOnMock invocation) -> {
      Solicitud newSolicitud = new Solicitud();
      BeanUtils.copyProperties(invocation.getArgument(0), newSolicitud);
      newSolicitud.setId(1L);
      newSolicitud.setTitulo("titulo");
      return newSolicitud;
    });

    // when: create Solicitud
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new Solicitud is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("codigoRegistroInterno").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(solicitud.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("creadorRef").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitanteRef").value(solicitud.getSolicitanteRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(solicitud.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(solicitud.getUnidadGestionRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a Solicitud with id filled
    Solicitud solicitud = generarMockSolicitud(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Solicitud>any())).willThrow(new IllegalArgumentException());

    // when: create Solicitud
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_ReturnsSolicitud() throws Exception {
    // given: Existing Solicitud to be updated
    Solicitud solicitudExistente = generarMockSolicitud(1L);
    Solicitud solicitud = generarMockSolicitud(1L);
    solicitud.setObservaciones("observaciones actualizadas");

    BDDMockito.given(service.update(ArgumentMatchers.<Solicitud>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update Solicitud
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Solicitud is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(solicitudExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(solicitudExistente.getTitulo()))
        .andExpect(MockMvcResultMatchers.jsonPath("codigoRegistroInterno")
            .value(solicitudExistente.getCodigoRegistroInterno()))
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").value(solicitudExistente.getEstado().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(solicitudExistente.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("creadorRef").value(solicitudExistente.getCreadorRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitanteRef").value(solicitudExistente.getSolicitanteRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(solicitud.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(solicitudExistente.getUnidadGestionRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    Solicitud solicitud = generarMockSolicitud(1L);

    BDDMockito.willThrow(new SolicitudNotFoundException(id)).given(service).update(ArgumentMatchers.<Solicitud>any());

    // when: update Solicitud
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-R" })
  public void reactivar_WithExistingId_ReturnSolicitud() throws Exception {
    // given: existing id
    Solicitud solicitud = generarMockSolicitud(1L);
    solicitud.setActivo(false);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Solicitud solicitudDisabled = new Solicitud();
      BeanUtils.copyProperties(solicitud, solicitudDisabled);
      solicitudDisabled.setActivo(true);
      return solicitudDisabled;
    });

    // when: reactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, solicitud.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Solicitud is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-SOL-B" })
  public void desactivar_WithExistingId_ReturnSolicitud() throws Exception {
    // given: existing id
    Solicitud solicitud = generarMockSolicitud(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Solicitud solicitudDisabled = new Solicitud();
      BeanUtils.copyProperties(solicitud, solicitudDisabled);
      solicitudDisabled.setActivo(false);
      return solicitudDisabled;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, solicitud.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Programa is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void findById_WithExistingId_ReturnsSolicitud() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockSolicitud(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Solicitud is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("codigoRegistroInterno").value("SGI_SLC1202011061027"))
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("creadorRef").value("usr-001"))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitanteRef").value("usr-002"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("observaciones-001"))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value("2"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 Solicitud
    List<Solicitud> solicitudes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudes.add(generarMockSolicitud(i));
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
          toIndex = toIndex > solicitudes.size() ? solicitudes.size() : toIndex;
          List<Solicitud> content = solicitudes.subList(fromIndex, toIndex);
          Page<Solicitud> pageResponse = new PageImpl<>(content, pageable, solicitudes.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los TipoFase del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<Solicitud> solicitudesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Solicitud>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Solicitud solicitud = solicitudesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitud.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data Solicitud
    BDDMockito.given(service.findAllRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Solicitud>>() {
          @Override
          public Page<Solicitud> answer(InvocationOnMock invocation) throws Throwable {
            Page<Solicitud> page = new PageImpl<>(Collections.emptyList());
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
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V", "CSP-SOL-B", "CSP-SOL-R" })
  public void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 Solicitud
    List<Solicitud> solicitudes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudes.add(generarMockSolicitud(i));
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
          toIndex = toIndex > solicitudes.size() ? solicitudes.size() : toIndex;
          List<Solicitud> content = solicitudes.subList(fromIndex, toIndex);
          Page<Solicitud> pageResponse = new PageImpl<>(content, pageable, solicitudes.size());
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
    List<Solicitud> solicitudesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Solicitud>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Solicitud solicitud = solicitudesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitud.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V", "CSP-SOL-B", "CSP-SOL-R" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data Solicitud
    BDDMockito.given(service.findAllTodosRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Solicitud>>() {
          @Override
          public Page<Solicitud> answer(InvocationOnMock invocation) throws Throwable {
            Page<Solicitud> page = new PageImpl<>(Collections.emptyList());
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
   * Solicitud modalidad
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudModalidad_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudModalidad para la Solicitud
    Long solicitudId = 1L;

    List<SolicitudModalidad> solicitudModalidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudModalidades.add(generarMockSolicitudModalidad(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudModalidadService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudModalidades.size() ? solicitudModalidades.size() : toIndex;
          List<SolicitudModalidad> content = solicitudModalidades.subList(fromIndex, toIndex);
          Page<SolicitudModalidad> pageResponse = new PageImpl<>(content, pageable, solicitudModalidades.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_MODALIDADES, solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudModalidad del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudModalidad> solicitudModalidadResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<SolicitudModalidad>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudModalidad solicitudModalidad = solicitudModalidadResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudModalidad.getEntidadRef()).isEqualTo("entidad-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudModalidad_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de SolicitudModalidad para la
    // Solicitud
    Long solicitudId = 1L;
    List<SolicitudModalidad> solicitudModalidades = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudModalidadService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<SolicitudModalidad> pageResponse = new PageImpl<>(solicitudModalidades, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_MODALIDADES, solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * SOLICITUD ESTADOS
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllEstadoSolicitud_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudModalidad para la Solicitud
    Long solicitudId = 1L;

    List<EstadoSolicitud> estadosSolicitud = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      estadosSolicitud.add(generarMockEstadoSolicitud(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(
            estadoSolicitudService.findAllBySolicitud(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > estadosSolicitud.size() ? estadosSolicitud.size() : toIndex;
          List<EstadoSolicitud> content = estadosSolicitud.subList(fromIndex, toIndex);
          Page<EstadoSolicitud> pageResponse = new PageImpl<>(content, pageable, estadosSolicitud.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ESTADOS_SOLICITUD, solicitudId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudModalidad del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<EstadoSolicitud> estadosSolicitudResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<EstadoSolicitud>>() {
        });

    for (int i = 31; i <= 37; i++) {
      EstadoSolicitud estadoSolicitud = estadosSolicitudResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(estadoSolicitud.getComentario()).isEqualTo("Estado-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllEstadoSolicitud_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de EstadoSolicitud para la
    // Solicitud
    Long solicitudId = 1L;
    List<EstadoSolicitud> estadosSolicitud = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(
            estadoSolicitudService.findAllBySolicitud(ArgumentMatchers.<Long>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<EstadoSolicitud> pageResponse = new PageImpl<>(estadosSolicitud, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ESTADOS_SOLICITUD, solicitudId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * Solicitud documentos
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudDocumento_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudDocumentos para la Solicitud
    Long solicitudId = 1L;

    List<SolicitudDocumento> solicitudDocumento = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudDocumento.add(generarSolicitudDocumento(i, i, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudDocumentoService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudDocumento.size() ? solicitudDocumento.size() : toIndex;
          List<SolicitudDocumento> content = solicitudDocumento.subList(fromIndex, toIndex);
          Page<SolicitudDocumento> pageResponse = new PageImpl<>(content, pageable, solicitudDocumento.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicituddocumentos", solicitudId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudDocumentos del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudDocumento> solicitudDocumentoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<SolicitudDocumento>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudDocumento solicitudDocumentoRecuperado = solicitudDocumentoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudDocumentoRecuperado.getDocumentoRef())
          .isEqualTo("documentoRef-" + String.format("%02d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudDocumento_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de SolicitudDocumentos para la
    // Solicitud
    Long solicitudId = 1L;
    List<SolicitudDocumento> solicitudDocumentos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudDocumentoService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<SolicitudDocumento> pageResponse = new PageImpl<>(solicitudDocumentos, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicituddocumentos", solicitudId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * SOLICITUD HITO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudHito_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudHito para la Solicitud
    Long solicitudId = 1L;

    List<SolicitudHito> solicitudHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudHitos.add(generarSolicitudHito(i, i, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(solicitudHitoService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudHitos.size() ? solicitudHitos.size() : toIndex;
          List<SolicitudHito> content = solicitudHitos.subList(fromIndex, toIndex);
          Page<SolicitudHito> pageResponse = new PageImpl<>(content, pageable, solicitudHitos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudhitos", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudModalidad del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudHito> solicitudHitoResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<SolicitudHito>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudHito solicitudHito = solicitudHitoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudHito.getComentario()).isEqualTo("comentario-" + String.format("%02d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudHito_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de SolicitudHito para la
    // Solicitud
    Long solicitudId = 1L;
    List<SolicitudHito> solicitudHitos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(solicitudHitoService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<SolicitudHito> pageResponse = new PageImpl<>(solicitudHitos, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudhitos", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void findSolicitudProyecto_ReturnsSolicitudProyecto() throws Exception {
    // given: existing id
    BDDMockito.given(solicitudProyectoService.findBySolicitud(ArgumentMatchers.anyLong()))
        .willAnswer((InvocationOnMock invocation) -> {
          return generarSolicitudProyecto(invocation.getArgument(0), invocation.getArgument(0));
        });

    // when: find by not existing solicitud id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Solicitud is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("colaborativo").value(Boolean.TRUE))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoPresupuesto").value(TipoPresupuesto.GLOBAL.toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void findSolicitudProyecto_Returns404() throws Exception {
    // given: existing id
    BDDMockito.given(solicitudProyectoService.findBySolicitud(ArgumentMatchers.anyLong()))
        .willAnswer((InvocationOnMock invocation) -> {
          throw new SolicitudNotFoundException(1L);
        });

    // when: find by existing solicitud id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", 2L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void existSolictudProyectoDatos_ReturnsOK() throws Exception {
    // given: Existing solicitud datos proyecto by solicitud
    Long id = 1L;
    BDDMockito.given(solicitudProyectoService.existsBySolicitudId(ArgumentMatchers.<Long>any()))
        .willReturn(Boolean.TRUE);

    // when: check exist solicitud datos proyecto by solicitud
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void existSolictudProyectoDatos_Returns204() throws Exception {
    // given: not Existing solicitud datos proyecto by solicitud
    Long id = 1L;
    BDDMockito.given(solicitudProyectoService.findBySolicitud(ArgumentMatchers.<Long>any())).willReturn(null);

    // when: check exist solicitud datos proyecto by solicitud
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyecto", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * Solicitud proyecto equipo
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudProyectoSocio_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoEquipo para la Solicitud
    Long solicitudId = 1L;

    List<SolicitudProyectoSocio> solicitudProyectoSocio = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoSocio.add(generarSolicitudProyectoSocio(i, i, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoSocioService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoSocio.size() ? solicitudProyectoSocio.size() : toIndex;
          List<SolicitudProyectoSocio> content = solicitudProyectoSocio.subList(fromIndex, toIndex);
          Page<SolicitudProyectoSocio> pageResponse = new PageImpl<>(content, pageable, solicitudProyectoSocio.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectosocio", solicitudId)
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

    List<SolicitudProyectoSocio> solicitudProyectoSocioResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<SolicitudProyectoSocio>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoSocio solicitudProyectoSocioRecuperado = solicitudProyectoSocioResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoSocioRecuperado.getImporteSolicitado()).isEqualTo(new BigDecimal(i));
    }
  }

  /**
   * 
   * Solicitud proyecto entidad financiadora ajena
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void findAllSolicitudProyectoEntidadFinanciadoraAjena_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoEntidadFinanciadoraAjena para la
    // Solicitud
    Long solicitudId = 1L;

    List<SolicitudProyectoEntidadFinanciadoraAjena> solicitudProyectoEntidadFinanciadoraAjenas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoEntidadFinanciadoraAjenas.add(generarMockSolicitudProyectoEntidadFinanciadoraAjena(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoEntidadFinanciadoraAjenaService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoEntidadFinanciadoraAjenas.size()
              ? solicitudProyectoEntidadFinanciadoraAjenas.size()
              : toIndex;
          List<SolicitudProyectoEntidadFinanciadoraAjena> content = solicitudProyectoEntidadFinanciadoraAjenas
              .subList(fromIndex, toIndex);
          Page<SolicitudProyectoEntidadFinanciadoraAjena> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoEntidadFinanciadoraAjenas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA_AJENA, solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudProyectoEntidadFinanciadoraAjena
        // del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudProyectoEntidadFinanciadoraAjena> solicitudProyectoEntidadFinanciadoraAjenasResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(),
            new TypeReference<List<SolicitudProyectoEntidadFinanciadoraAjena>>() {
            });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = solicitudProyectoEntidadFinanciadoraAjenasResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getEntidadRef())
          .isEqualTo("entidad-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudProyectoSocio_Returns204() throws Exception {
    // given: Una lista vacia de SolicitudProyectoSocio para la
    // Solicitud
    Long solicitudId = 1L;
    List<SolicitudProyectoSocio> solicitudProyectoSocio = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoSocioService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<SolicitudProyectoSocio> pageResponse = new PageImpl<>(solicitudProyectoSocio, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectosocio", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * Solicitud proyecto presupuestos
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void findAllSolicitudProyectoPresupuesto_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoPresupuesto para la Solicitud
    Long solicitudId = 1L;

    List<SolicitudProyectoPresupuesto> solicitudProyectoPresupuestos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoPresupuestos.add(generarSolicitudProyectoPresupuesto(i, i, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoPresupuestoService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoPresupuestos.size() ? solicitudProyectoPresupuestos.size() : toIndex;
          List<SolicitudProyectoPresupuesto> content = solicitudProyectoPresupuestos.subList(fromIndex, toIndex);
          Page<SolicitudProyectoPresupuesto> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoPresupuestos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTOS, solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los SolicitudProyectoPresupuesto
        // del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudProyectoPresupuesto> solicitudProyectoEntidadFinanciadoraAjenasResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<SolicitudProyectoPresupuesto>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoPresupuesto solicitudProyectoEntidadFinanciadoraAjena = solicitudProyectoEntidadFinanciadoraAjenasResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getObservaciones())
          .isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllSolicitudProyectoPresupuesto_Returns204() throws Exception {
    // given: Una lista vacia de SolicitudProyectoPresupuesto para la Solicitud
    Long solicitudId = 1L;
    List<SolicitudProyectoPresupuesto> solicitudProyectoPresupuestos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoPresupuestoService.findAllBySolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<SolicitudProyectoPresupuesto> pageResponse = new PageImpl<>(solicitudProyectoPresupuestos, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PROYECTO_PRESUPUESTOS, solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto Solicitud
   * 
   * @param id id del Solicitud
   * @return el objeto Solicitud
   */
  private Solicitud generarMockSolicitud(Long id) {
    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setId(1L);

    Programa programa = new Programa();
    programa.setId(1L);

    Solicitud solicitud = new Solicitud();
    solicitud.setId(id);
    solicitud.setTitulo("titulo");
    solicitud.setCodigoExterno(null);
    solicitud.setConvocatoriaId(1L);
    solicitud.setSolicitanteRef("usr-002");
    solicitud.setObservaciones("observaciones-" + String.format("%03d", id));
    solicitud.setConvocatoriaExterna(null);
    solicitud.setUnidadGestionRef("2");
    solicitud.setFormularioSolicitud(FormularioSolicitud.GRUPO);
    solicitud.setActivo(true);

    if (id != null) {
      solicitud.setEstado(estadoSolicitud);
      solicitud.setCodigoRegistroInterno("SGI_SLC1202011061027");
      solicitud.setCreadorRef("usr-001");
    }

    return solicitud;
  }

  /**
   * Función que devuelve un objeto SolicitudModalidad
   * 
   * @param id id del SolicitudModalidad
   * @return el objeto SolicitudModalidad
   */
  private SolicitudModalidad generarMockSolicitudModalidad(Long id) {
    Programa programa = new Programa();
    programa.setId(1L);

    SolicitudModalidad solicitudModalidad = new SolicitudModalidad();
    solicitudModalidad.setId(id);
    solicitudModalidad.setEntidadRef("entidad-" + String.format("%03d", id));
    solicitudModalidad.setSolicitudId(1L);
    solicitudModalidad.setPrograma(programa);

    return solicitudModalidad;
  }

  /**
   * Función que devuelve un objeto SolicitudDocumento
   * 
   * @param solicitudDocumentoId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto SolicitudDocumento
   */
  private SolicitudDocumento generarSolicitudDocumento(Long solicitudDocumentoId, Long solicitudId,
      Long tipoDocumentoId) {

    SolicitudDocumento solicitudDocumento = SolicitudDocumento.builder().id(solicitudDocumentoId)
        .solicitudId(solicitudId).comentario("comentarios-" + solicitudDocumentoId)
        .documentoRef("documentoRef-" + solicitudDocumentoId).nombre("nombreDocumento-" + solicitudDocumentoId)
        .tipoDocumento(TipoDocumento.builder().id(tipoDocumentoId).build()).build();
    return solicitudDocumento;
  }

  /**
   * Función que devuelve un objeto EstadoSolicitud
   * 
   * @param id id del EstadoSolicitud
   * @return el objeto EstadoSolicitud
   */
  private EstadoSolicitud generarMockEstadoSolicitud(Long id) {
    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setId(id);
    estadoSolicitud.setComentario("Estado-" + id);
    estadoSolicitud.setEstado(EstadoSolicitud.Estado.BORRADOR);
    estadoSolicitud.setFechaEstado(Instant.now());
    estadoSolicitud.setSolicitudId(1L);

    return estadoSolicitud;
  }

  /**
   * Función que devuelve un objeto SolicitudHito
   * 
   * @param solicitudHitoId
   * @param solicitudId
   * @param tipoDocumentoId
   * @return el objeto SolicitudHito
   */
  private SolicitudHito generarSolicitudHito(Long solicitudHitoId, Long solicitudId, Long tipoDocumentoId) {

    SolicitudHito solicitudHito = SolicitudHito.builder().id(solicitudHitoId).solicitudId(solicitudId)
        .comentario("comentario-" + solicitudHitoId).fecha(Instant.now()).generaAviso(Boolean.TRUE)
        .tipoHito(TipoHito.builder().id(tipoDocumentoId).build()).build();

    return solicitudHito;
  }

  /**
   * Función que devuelve un objeto SolicitudProyecto
   * 
   * @param solicitudProyectoId
   * @param solicitudId
   * @return el objeto SolicitudProyecto
   */
  private SolicitudProyecto generarSolicitudProyecto(Long solicitudProyectoId, Long solicitudId) {

    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder().id(solicitudProyectoId)
        .acronimo("acronimo-" + solicitudProyectoId).colaborativo(Boolean.TRUE).tipoPresupuesto(TipoPresupuesto.GLOBAL)
        .build();

    return solicitudProyecto;
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
        .mesFin(3).numInvestigadores(2).importeSolicitado(new BigDecimal(solicitudProyectoSocioId)).empresaRef("002")
        .build();

    return solicitudProyectoSocio;
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
