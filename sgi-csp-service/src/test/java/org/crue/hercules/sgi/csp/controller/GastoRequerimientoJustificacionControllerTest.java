package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.converter.GastoRequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.service.GastoRequerimientoJustificacionService;
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
 * GastoRequerimientoJustificacionControllerTest
 */
@WebMvcTest(GastoRequerimientoJustificacionController.class)
public class GastoRequerimientoJustificacionControllerTest extends BaseControllerTest {

  @MockBean
  private GastoRequerimientoJustificacionService service;
  @MockBean
  private GastoRequerimientoJustificacionConverter converter;

  private static final String CONTROLLER_BASE_PATH = GastoRequerimientoJustificacionController.REQUEST_MAPPING;
  private static final String PATH_ID = GastoRequerimientoJustificacionController.PATH_ID;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void create_GastoRequerimientoJustificacion() throws Exception {
    // given: GastoRequerimientoJustificacionInput data
    String alegacion = "Alegacion-001";
    Long requerimientoJustificacionId = 1L;
    GastoRequerimientoJustificacionInput gastoRequerimientoJustificacionToCreate = generarMockGastoRequerimientoJustificacionInput(
        requerimientoJustificacionId, alegacion);
    BDDMockito.given(converter.convert(ArgumentMatchers.<GastoRequerimientoJustificacionInput>any()))
        .willAnswer(new Answer<GastoRequerimientoJustificacion>() {
          @Override
          public GastoRequerimientoJustificacion answer(InvocationOnMock invocation) throws Throwable {
            GastoRequerimientoJustificacionInput input = invocation.getArgument(0,
                GastoRequerimientoJustificacionInput.class);
            return generarMockGastoRequerimientoJustificacion(input);
          }
        });
    BDDMockito.given(service.create(ArgumentMatchers.<GastoRequerimientoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          GastoRequerimientoJustificacion gastoRequerimientoJustificacion = invocation.getArgument(0);
          gastoRequerimientoJustificacion.setId(1L);
          return gastoRequerimientoJustificacion;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<GastoRequerimientoJustificacion>any()))
        .willAnswer(new Answer<GastoRequerimientoJustificacionOutput>() {
          @Override
          public GastoRequerimientoJustificacionOutput answer(InvocationOnMock invocation) throws Throwable {
            GastoRequerimientoJustificacion gastoRequerimientoJustificacion = invocation.getArgument(0,
                GastoRequerimientoJustificacion.class);
            return generarMockGastoRequerimientoJustificacionOutput(gastoRequerimientoJustificacion);
          }
        });

    // when: create GastoRequerimientoJustificacionInput
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(gastoRequerimientoJustificacionToCreate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is CREATED
        // and the created GastoRequerimientoJustificacionInput is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("alegacion").value(alegacion));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-E" })
  void update_ReturnsRequerimientoJustificacion() throws Exception {
    // given: GastoRequerimientoJustificacionInput data and a
    // gastoRequerimientoJustificacionId
    Long gastoRequerimientoJustificacionId = 1L;
    String alegacion = "Alegacion-001";
    Long requerimientoJustificacionId = 1L;
    GastoRequerimientoJustificacionInput gastoRequerimientoJustificacionToUpdate = generarMockGastoRequerimientoJustificacionInput(
        requerimientoJustificacionId, alegacion);
    BDDMockito
        .given(
            converter.convert(ArgumentMatchers.<GastoRequerimientoJustificacionInput>any(), ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<GastoRequerimientoJustificacion>() {
          @Override
          public GastoRequerimientoJustificacion answer(InvocationOnMock invocation) throws Throwable {
            GastoRequerimientoJustificacionInput requerimientoJustificacion = invocation.getArgument(0,
                GastoRequerimientoJustificacionInput.class);
            Long id = invocation.getArgument(1,
                Long.class);
            return generarMockGastoRequerimientoJustificacion(requerimientoJustificacion, id);
          }
        });
    BDDMockito.given(service.update(ArgumentMatchers.<GastoRequerimientoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          GastoRequerimientoJustificacion gastoRequerimientoJustificacion = invocation.getArgument(0);
          return gastoRequerimientoJustificacion;
        });
    BDDMockito.given(converter.convert(ArgumentMatchers.<GastoRequerimientoJustificacion>any()))
        .willAnswer(new Answer<GastoRequerimientoJustificacionOutput>() {
          @Override
          public GastoRequerimientoJustificacionOutput answer(InvocationOnMock invocation) throws Throwable {
            GastoRequerimientoJustificacion gastoRequerimientoJustificacion = invocation.getArgument(0,
                GastoRequerimientoJustificacion.class);
            return generarMockGastoRequerimientoJustificacionOutput(gastoRequerimientoJustificacion);
          }
        });

    // when: update GastoRequerimientoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_ID, gastoRequerimientoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(gastoRequerimientoJustificacionToUpdate)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the updated GastoRequerimientoJustificacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(
            gastoRequerimientoJustificacionId))
        .andExpect(MockMvcResultMatchers.jsonPath("alegacion").value(alegacion));
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
  void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 GastoRequerimientoJustificacion
    Long requerimientoJustificacionId = 1L;

    List<GastoRequerimientoJustificacion> gastos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      gastos.add(generarMockGastoRequerimientoJustificacion(i, requerimientoJustificacionId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(service.findAll(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GastoRequerimientoJustificacion>>() {
          @Override
          public Page<GastoRequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
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
        .given(converter.convert(ArgumentMatchers.<Page<GastoRequerimientoJustificacion>>any()))
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
            .get(CONTROLLER_BASE_PATH)
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
    List<GastoRequerimientoJustificacion> gastos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service
        .findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GastoRequerimientoJustificacion>>() {
          @Override
          public Page<GastoRequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<GastoRequerimientoJustificacion> page = new PageImpl<>(gastos, pageable, 0);
            return page;
          }
        });
    BDDMockito
        .given(converter
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
            .get(CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(Long id,
      Long requerimientoJustificacionId) {
    String suffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockGastoRequerimientoJustificacion(id, Boolean.TRUE, "Alegacion-" + suffix,
        "gasto-ref-" + suffix, "11/1111",
        null, null, null,
        "Incidencia-" + suffix, requerimientoJustificacionId);
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(
      GastoRequerimientoJustificacionInput input) {
    return generarMockGastoRequerimientoJustificacion(null, input.getRequerimientoJustificacionId(),
        input.getAlegacion());
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(
      GastoRequerimientoJustificacionInput input, Long id) {
    return generarMockGastoRequerimientoJustificacion(id, input.getRequerimientoJustificacionId(),
        input.getAlegacion());
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(Long id,
      Long requerimientoJustificacionId, String alegacion) {
    return generarMockGastoRequerimientoJustificacion(id, Boolean.TRUE, alegacion,
        null, "11/1111",
        null, null, null,
        null, requerimientoJustificacionId);
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

  private GastoRequerimientoJustificacionInput generarMockGastoRequerimientoJustificacionInput(
      Long requerimientoJustificacionId, String alegacion) {
    return generarMockGastoRequerimientoJustificacionInput(Boolean.TRUE, alegacion,
        null, "11/1111",
        null, null, null,
        null, requerimientoJustificacionId);
  }

  private GastoRequerimientoJustificacionInput generarMockGastoRequerimientoJustificacionInput(Boolean aceptado,
      String alegacion, String gastoRef, String identificadorJustificacion, BigDecimal importeAceptado,
      BigDecimal importeAlegado, BigDecimal importeRechazado,
      String incidencia, Long requerimientoJustificacionId) {
    return GastoRequerimientoJustificacionInput.builder()
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
      GastoRequerimientoJustificacion gastoRequerimientoJustificacion) {
    return generarMockGastoRequerimientoJustificacionOutput(gastoRequerimientoJustificacion.getId(),
        gastoRequerimientoJustificacion.getRequerimientoJustificacionId(),
        gastoRequerimientoJustificacion.getAlegacion());
  }

  private GastoRequerimientoJustificacionOutput generarMockGastoRequerimientoJustificacionOutput(Long id,
      Long requerimientoJustificacionId, String alegacion) {
    return GastoRequerimientoJustificacionOutput.builder()
        .id(id)
        .alegacion(alegacion)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}
