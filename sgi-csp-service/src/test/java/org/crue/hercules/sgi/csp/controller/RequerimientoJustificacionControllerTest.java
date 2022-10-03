package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.converter.AlegacionRequerimientoConverter;
import org.crue.hercules.sgi.csp.converter.GastoRequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.converter.IncidenciaDocumentacionRequerimientoConverter;
import org.crue.hercules.sgi.csp.converter.RequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.service.AlegacionRequerimientoService;
import org.crue.hercules.sgi.csp.service.GastoRequerimientoJustificacionService;
import org.crue.hercules.sgi.csp.service.IncidenciaDocumentacionRequerimientoService;
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
 * RequerimientoJustificacionControllerTest
 */
@WebMvcTest(RequerimientoJustificacionController.class)
public class RequerimientoJustificacionControllerTest extends BaseControllerTest {

  @MockBean
  private RequerimientoJustificacionService service;
  @MockBean
  private RequerimientoJustificacionConverter converter;
  @MockBean
  private IncidenciaDocumentacionRequerimientoService incidenciaDocumentacionRequerimientoService;
  @MockBean
  private IncidenciaDocumentacionRequerimientoConverter incidenciaDocumentacionRequerimientoConverter;
  @MockBean
  private GastoRequerimientoJustificacionService gastoRequerimientoJustificacionService;
  @MockBean
  private GastoRequerimientoJustificacionConverter gastoRequerimientoJustificacionConverter;
  @MockBean
  private AlegacionRequerimientoService alegacionRequerimientoService;
  @MockBean
  private AlegacionRequerimientoConverter alegacionRequerimientoConverter;

  private static final String CONTROLLER_BASE_PATH = RequerimientoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_ID = RequerimientoJustificacionController.PATH_ID;
  private static final String PATH_INCIDENCIAS_DOCUMENTACION = RequerimientoJustificacionController.PATH_INCIDENCIAS_DOCUMENTACION;
  private static final String PATH_GASTOS = RequerimientoJustificacionController.PATH_GASTOS;
  private static final String PATH_ALEGACION = RequerimientoJustificacionController.PATH_ALEGACION;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V", "CSP-SJUS-E" })
  void findById_WithExistingId_ReturnsRequerimientoJustificacion() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      RequerimientoJustificacion requerimientoJustificacion = generarMockRequerimientoJustificacion(paramId);
      return requerimientoJustificacion;
    });
    BDDMockito.given(converter.convert(ArgumentMatchers.<RequerimientoJustificacion>any()))
        .willAnswer(new Answer<RequerimientoJustificacionOutput>() {
          @Override
          public RequerimientoJustificacionOutput answer(InvocationOnMock invocation) throws Throwable {
            RequerimientoJustificacion requerimientoJustificacion = invocation.getArgument(0,
                RequerimientoJustificacion.class);
            return generarMockRequerimientoJustificacionOutput(requerimientoJustificacion);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested RequerimientoJustificacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void deleteById_WithExistingId_ReturnsNoContent() throws Exception {
    // given: existing id
    Long id = 1L;

    // when: delete by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is NO_CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void create_ReturnsRequerimientoJustificacion() throws Exception {
    // given: RequerimientoJustificacionInput data
    String observaciones = "RequerimientoJustificacion-001";
    RequerimientoJustificacionInput requerimientoJustificacionToCreate = generarMockRequerimientoJustificacionInput(
        observaciones);
    BDDMockito.given(converter.convert(ArgumentMatchers.<RequerimientoJustificacionInput>any()))
        .willAnswer(new Answer<RequerimientoJustificacion>() {
          @Override
          public RequerimientoJustificacion answer(InvocationOnMock invocation) throws Throwable {
            RequerimientoJustificacionInput requerimientoJustificacion = invocation.getArgument(0,
                RequerimientoJustificacionInput.class);
            return generarMockRequerimientoJustificacion(requerimientoJustificacion);
          }
        });
    BDDMockito.given(service.create(ArgumentMatchers.<RequerimientoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          RequerimientoJustificacion requerimientoJustificacion = invocation.getArgument(0);
          requerimientoJustificacion.setId(1L);
          return requerimientoJustificacion;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<RequerimientoJustificacion>any()))
        .willAnswer(new Answer<RequerimientoJustificacionOutput>() {
          @Override
          public RequerimientoJustificacionOutput answer(InvocationOnMock invocation) throws Throwable {
            RequerimientoJustificacion requerimientoJustificacion = invocation.getArgument(0,
                RequerimientoJustificacion.class);
            return generarMockRequerimientoJustificacionOutput(requerimientoJustificacion);
          }
        });

    // when: create RequerimientoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(requerimientoJustificacionToCreate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is CREATED
        // and the created RequerimientoJustificacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(observaciones));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void update_ReturnsRequerimientoJustificacion() throws Exception {
    // given: RequerimientoJustificacionInput data and a
    // requerimientoJustificacionId
    Long requerimientoJustificacionId = 1L;
    String observaciones = "RequerimientoJustificacion-001";
    RequerimientoJustificacionInput requerimientoJustificacionToCreate = generarMockRequerimientoJustificacionInput(
        observaciones);
    BDDMockito
        .given(converter.convert(ArgumentMatchers.<RequerimientoJustificacionInput>any(), ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<RequerimientoJustificacion>() {
          @Override
          public RequerimientoJustificacion answer(InvocationOnMock invocation) throws Throwable {
            RequerimientoJustificacionInput requerimientoJustificacion = invocation.getArgument(0,
                RequerimientoJustificacionInput.class);
            Long id = invocation.getArgument(1,
                Long.class);
            return generarMockRequerimientoJustificacion(requerimientoJustificacion, id);
          }
        });
    BDDMockito.given(service.update(ArgumentMatchers.<RequerimientoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          RequerimientoJustificacion requerimientoJustificacion = invocation.getArgument(0);
          return requerimientoJustificacion;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<RequerimientoJustificacion>any()))
        .willAnswer(new Answer<RequerimientoJustificacionOutput>() {
          @Override
          public RequerimientoJustificacionOutput answer(InvocationOnMock invocation) throws Throwable {
            RequerimientoJustificacion requerimientoJustificacion = invocation.getArgument(0,
                RequerimientoJustificacion.class);
            return generarMockRequerimientoJustificacionOutput(requerimientoJustificacion);
          }
        });

    // when: update RequerimientoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_ID, requerimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(requerimientoJustificacionToCreate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the updated RequerimientoJustificacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(requerimientoJustificacionId))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(observaciones));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void findIncidenciasDocumentacion_ReturnsPage() throws Exception {
    // given: Una lista con 37 IncidenciaDocumentacionRequerimiento para el
    // RequerimientoJustificacion
    Long requerimientoJustificacionId = 1L;

    List<IncidenciaDocumentacionRequerimiento> incidencias = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      incidencias.add(generarMockIncidenciaDocumentacionRequerimiento(i, requerimientoJustificacionId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(incidenciaDocumentacionRequerimientoService.findAllByRequerimientoJustificacionId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<IncidenciaDocumentacionRequerimiento>>() {
          @Override
          public Page<IncidenciaDocumentacionRequerimiento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > incidencias.size() ? incidencias.size() : toIndex;
            List<IncidenciaDocumentacionRequerimiento> content = incidencias.subList(fromIndex, toIndex);
            Page<IncidenciaDocumentacionRequerimiento> page = new PageImpl<>(content, pageable, incidencias.size());
            return page;
          }
        });
    BDDMockito
        .given(incidenciaDocumentacionRequerimientoConverter
            .convert(ArgumentMatchers.<Page<IncidenciaDocumentacionRequerimiento>>any()))
        .willAnswer(new Answer<Page<IncidenciaDocumentacionRequerimientoOutput>>() {
          @Override
          public Page<IncidenciaDocumentacionRequerimientoOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<IncidenciaDocumentacionRequerimiento> pageInput = invocation.getArgument(0);
            List<IncidenciaDocumentacionRequerimientoOutput> content = pageInput.getContent().stream().map(input -> {
              return generarMockIncidenciaDocumentacionRequerimientoOutput(input);
            }).collect(Collectors.toList());
            Page<IncidenciaDocumentacionRequerimientoOutput> pageOutput = new PageImpl<>(content,
                pageInput.getPageable(),
                pageInput.getTotalElements());
            return pageOutput;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_INCIDENCIAS_DOCUMENTACION, requerimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los IncidenciaDocumentacionRequerimiento del
        // 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<IncidenciaDocumentacionRequerimientoOutput> incidenciasResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(),
            new TypeReference<List<IncidenciaDocumentacionRequerimientoOutput>>() {
            });

    for (int i = 31; i <= 37; i++) {
      IncidenciaDocumentacionRequerimientoOutput incidencia = incidenciasResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(incidencia.getNombreDocumento())
          .isEqualTo("IncidenciaDocumentacionRequerimiento-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void findIncidenciasDocumentacion_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de IncidenciaDocumentacionRequerimiento para el
    // RequerimientoJustificacion
    Long requerimientoJustificacionId = 1L;
    List<IncidenciaDocumentacionRequerimiento> requerimientos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(incidenciaDocumentacionRequerimientoService
        .findAllByRequerimientoJustificacionId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<IncidenciaDocumentacionRequerimiento>>() {
          @Override
          public Page<IncidenciaDocumentacionRequerimiento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<IncidenciaDocumentacionRequerimiento> page = new PageImpl<>(requerimientos, pageable, 0);
            return page;
          }
        });
    BDDMockito
        .given(incidenciaDocumentacionRequerimientoConverter
            .convert(ArgumentMatchers.<Page<IncidenciaDocumentacionRequerimiento>>any()))
        .willAnswer(new Answer<Page<IncidenciaDocumentacionRequerimientoOutput>>() {
          @Override
          public Page<IncidenciaDocumentacionRequerimientoOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<IncidenciaDocumentacionRequerimientoOutput> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_INCIDENCIAS_DOCUMENTACION, requerimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void findGastos_ReturnsPage() throws Exception {
    // given: Una lista con 37 GastoRequerimientoJustificacion para el
    // RequerimientoJustificacion
    Long requerimientoJustificacionId = 1L;

    List<GastoRequerimientoJustificacion> gastos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      gastos.add(generarMockGastoRequerimientoJustificacion(i, requerimientoJustificacionId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(gastoRequerimientoJustificacionService.findAllByRequerimientoJustificacionId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GastoRequerimientoJustificacion>>() {
          @Override
          public Page<GastoRequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > gastos.size() ? gastos.size() : toIndex;
            List<GastoRequerimientoJustificacion> content = gastos.subList(fromIndex, toIndex);
            Page<GastoRequerimientoJustificacion> page = new PageImpl<>(content, pageable, gastos.size());
            return page;
          }
        });
    BDDMockito
        .given(gastoRequerimientoJustificacionConverter
            .convert(ArgumentMatchers.<Page<GastoRequerimientoJustificacion>>any()))
        .willAnswer(new Answer<Page<GastoRequerimientoJustificacionOutput>>() {
          @Override
          public Page<GastoRequerimientoJustificacionOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<GastoRequerimientoJustificacion> pageInput = invocation.getArgument(0);
            List<GastoRequerimientoJustificacionOutput> content = pageInput.getContent().stream().map(input -> {
              return generarMockGastoRequerimientoJustificacionOutput(input);
            }).collect(Collectors.toList());
            Page<GastoRequerimientoJustificacionOutput> pageOutput = new PageImpl<>(content,
                pageInput.getPageable(),
                pageInput.getTotalElements());
            return pageOutput;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_GASTOS, requerimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los GastoRequerimientoJustificacion del
        // 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<GastoRequerimientoJustificacionOutput> gastoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(),
            new TypeReference<List<GastoRequerimientoJustificacionOutput>>() {
            });

    for (int i = 31; i <= 37; i++) {
      GastoRequerimientoJustificacionOutput gasto = gastoResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(gasto.getAlegacion())
          .isEqualTo("Alegacion-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void findGastos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de GastoRequerimientoJustificacion para el
    // RequerimientoJustificacion
    Long requerimientoJustificacionId = 1L;
    List<GastoRequerimientoJustificacion> gastos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(gastoRequerimientoJustificacionService
        .findAllByRequerimientoJustificacionId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GastoRequerimientoJustificacion>>() {
          @Override
          public Page<GastoRequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<GastoRequerimientoJustificacion> page = new PageImpl<>(gastos, pageable, 0);
            return page;
          }
        });
    BDDMockito
        .given(gastoRequerimientoJustificacionConverter
            .convert(ArgumentMatchers.<Page<GastoRequerimientoJustificacion>>any()))
        .willAnswer(new Answer<Page<GastoRequerimientoJustificacionOutput>>() {
          @Override
          public Page<GastoRequerimientoJustificacionOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<GastoRequerimientoJustificacionOutput> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_GASTOS, requerimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void findAlegacion_ReturnsAlegacionRequerimiento() throws Exception {
    // given: Un requerimientoJustificacionId con AlegacionRequerimiento asociada
    Long requerimientoJustificacionId = 1L;
    AlegacionRequerimiento alegacionRequerimiento = generarMockAlegacionRequerimiento(1L, requerimientoJustificacionId);
    BDDMockito.given(alegacionRequerimientoService.findByRequerimientoJustificacionId(requerimientoJustificacionId))
        .willReturn(alegacionRequerimiento);
    BDDMockito.given(alegacionRequerimientoConverter.convert(ArgumentMatchers.<AlegacionRequerimiento>any()))
        .willAnswer(new Answer<AlegacionRequerimientoOutput>() {
          @Override
          public AlegacionRequerimientoOutput answer(InvocationOnMock invocation) throws Throwable {
            AlegacionRequerimiento input = invocation.getArgument(0, AlegacionRequerimiento.class);
            AlegacionRequerimientoOutput output = generarMockAlegacionRequerimientoOutput(input);
            return output;
          }
        });
    // when: Buscamos la AlegacionRequerimiento asociada
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ALEGACION, requerimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested AlegacionRequerimiento is resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("requerimientoJustificacionId").value(requerimientoJustificacionId));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void findAlegacion_Returns204() throws Exception {
    // given: Un requerimientoJustificacionId sin AlegacionRequerimiento asociada
    Long requerimientoJustificacionId = 1L;
    BDDMockito.given(alegacionRequerimientoService.findByRequerimientoJustificacionId(requerimientoJustificacionId))
        .willReturn(null);
    // when: Buscamos la AlegacionRequerimiento asociada
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_ALEGACION, requerimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id) {
    String observacionSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockRequerimientoJustificacion(id, "RequerimientoJustificacion-" + observacionSuffix,
        null);
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(RequerimientoJustificacionInput input) {
    return generarMockRequerimientoJustificacion(null, input.getObservaciones(), input.getRequerimientoPrevioId());
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(RequerimientoJustificacionInput input,
      Long id) {
    return generarMockRequerimientoJustificacion(id, input.getObservaciones(), input.getRequerimientoPrevioId());
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

  private RequerimientoJustificacionInput generarMockRequerimientoJustificacionInput(String observaciones) {
    return generarMockRequerimientoJustificacionInput(observaciones, 1L, null, 1L);
  }

  private RequerimientoJustificacionInput generarMockRequerimientoJustificacionInput(String observaciones,
      Long proyectoProyectoSgeId, Long requerimientoPrevioId, Long tipoRequerimientoId) {
    return RequerimientoJustificacionInput.builder()
        .observaciones(observaciones)
        .proyectoProyectoSgeId(proyectoProyectoSgeId)
        .requerimientoPrevioId(requerimientoPrevioId)
        .tipoRequerimientoId(tipoRequerimientoId)
        .build();
  }

  private IncidenciaDocumentacionRequerimiento generarMockIncidenciaDocumentacionRequerimiento(Long id,
      Long requerimientoJustificacionId) {
    String suffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockIncidenciaDocumentacionRequerimiento(id, "Alegacion-" + suffix, "Incidencia-" + suffix,
        "IncidenciaDocumentacionRequerimiento-" + suffix, requerimientoJustificacionId);
  }

  private IncidenciaDocumentacionRequerimientoOutput generarMockIncidenciaDocumentacionRequerimientoOutput(
      IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) {
    return generarMockIncidenciaDocumentacionRequerimientoOutput(incidenciaDocumentacionRequerimiento.getId(),
        incidenciaDocumentacionRequerimiento.getAlegacion(),
        incidenciaDocumentacionRequerimiento.getIncidencia(),
        incidenciaDocumentacionRequerimiento.getNombreDocumento(),
        incidenciaDocumentacionRequerimiento.getRequerimientoJustificacionId());
  }

  private IncidenciaDocumentacionRequerimiento generarMockIncidenciaDocumentacionRequerimiento(Long id,
      String alegacion,
      String incidencia, String nombreDocumento, Long requerimientoJustificacionId) {
    return IncidenciaDocumentacionRequerimiento.builder()
        .id(id)
        .alegacion(alegacion)
        .incidencia(incidencia)
        .nombreDocumento(nombreDocumento)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private IncidenciaDocumentacionRequerimientoOutput generarMockIncidenciaDocumentacionRequerimientoOutput(Long id,
      String alegacion,
      String incidencia, String nombreDocumento, Long requerimientoJustificacionId) {
    return IncidenciaDocumentacionRequerimientoOutput.builder()
        .id(id)
        .alegacion(alegacion)
        .incidencia(incidencia)
        .nombreDocumento(nombreDocumento)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(Long id,
      Long requerimientoJustificacionId) {
    String suffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockGastoRequerimientoJustificacion(id, Boolean.TRUE, "Alegacion-" + suffix,
        "gasto-ref-" + suffix, "11/1111",
        null, null, null,
        "Incidencia-" + suffix, requerimientoJustificacionId);
  }

  private GastoRequerimientoJustificacionOutput generarMockGastoRequerimientoJustificacionOutput(
      GastoRequerimientoJustificacion gastoRequerimientoJustificacion) {
    return generarMockGastoRequerimientoJustificacionOutput(gastoRequerimientoJustificacion.getId(),
        gastoRequerimientoJustificacion.getAceptado(),
        gastoRequerimientoJustificacion.getAlegacion(),
        gastoRequerimientoJustificacion.getGastoRef(),
        gastoRequerimientoJustificacion.getIdentificadorJustificacion(),
        gastoRequerimientoJustificacion.getImporteAceptado(),
        gastoRequerimientoJustificacion.getImporteAlegado(),
        gastoRequerimientoJustificacion.getImporteRechazado(),
        gastoRequerimientoJustificacion.getIncidencia(),
        gastoRequerimientoJustificacion.getRequerimientoJustificacionId());
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(Long id, Boolean aceptado,
      String alegacion, String gastoRef, String identificadorJustificacion, BigDecimal importeAceptado,
      BigDecimal importeAlegado, BigDecimal importeRechazado,
      String incidencia, Long requerimientoJustificacionId) {
    return GastoRequerimientoJustificacion.builder()
        .id(id)
        .aceptado(aceptado)
        .alegacion(alegacion)
        .gastoRef(gastoRef)
        .identificadorJustificacion(identificadorJustificacion)
        .importeAceptado(importeAceptado)
        .importeAlegado(importeAlegado)
        .importeRechazado(importeRechazado)
        .incidencia(incidencia)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private GastoRequerimientoJustificacionOutput generarMockGastoRequerimientoJustificacionOutput(
      Long id, Boolean aceptado, String alegacion, String gastoRef, String identificadorJustificacion,
      BigDecimal importeAceptado, BigDecimal importeAlegado, BigDecimal importeRechazado, String incidencia,
      Long requerimientoJustificacionId) {
    return GastoRequerimientoJustificacionOutput.builder()
        .id(id)
        .aceptado(aceptado)
        .alegacion(alegacion)
        .gastoRef(gastoRef)
        .identificadorJustificacion(identificadorJustificacion)
        .importeAceptado(importeAceptado)
        .importeAlegado(importeAlegado)
        .importeRechazado(importeRechazado)
        .incidencia(incidencia)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private AlegacionRequerimiento generarMockAlegacionRequerimiento(Long id,
      Long requerimientoJustificacionId) {
    String suffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockAlegacionRequerimiento(id, "Justificante-" + suffix, "Observacion-" + suffix,
        requerimientoJustificacionId);
  }

  private AlegacionRequerimiento generarMockAlegacionRequerimiento(Long id,
      String justificanteReintegro, String observaciones, Long requerimientoJustificacionId) {
    return AlegacionRequerimiento.builder()
        .id(id)
        .justificanteReintegro(justificanteReintegro)
        .observaciones(observaciones)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }

  private AlegacionRequerimientoOutput generarMockAlegacionRequerimientoOutput(AlegacionRequerimiento input) {
    return generarMockAlegacionRequerimientoOutput(input.getId(), input.getJustificanteReintegro(),
        input.getObservaciones(), input.getRequerimientoJustificacionId());
  }

  private AlegacionRequerimientoOutput generarMockAlegacionRequerimientoOutput(Long id,
      String justificanteReintegro, String observaciones, Long requerimientoJustificacionId) {
    return AlegacionRequerimientoOutput.builder()
        .id(id)
        .justificanteReintegro(justificanteReintegro)
        .observaciones(observaciones)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}
