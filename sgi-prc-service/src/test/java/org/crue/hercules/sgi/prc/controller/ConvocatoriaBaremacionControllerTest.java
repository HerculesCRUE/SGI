package org.crue.hercules.sgi.prc.controller;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.prc.converter.ModuladorConverter;
import org.crue.hercules.sgi.prc.converter.RangoConverter;
import org.crue.hercules.sgi.prc.dto.BaremoInput;
import org.crue.hercules.sgi.prc.dto.BaremoOutput;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionInput;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionOutput;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotUpdatableException;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.Baremo.TipoCuantia;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.service.BaremoService;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionService;
import org.crue.hercules.sgi.prc.service.ModuladorService;
import org.crue.hercules.sgi.prc.service.RangoService;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConvocatoriaBaremacionControllerTest
 */
@WebMvcTest(ConvocatoriaBaremacionController.class)
class ConvocatoriaBaremacionControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaBaremacionService service;
  @MockBean
  private BaremoService baremoService;
  @MockBean
  private ModuladorService moduladorService;
  @MockBean
  private RangoService rangoService;
  @MockBean
  private ModuladorConverter moduladorConverter;
  @MockBean
  private RangoConverter rangoConverter;

  private static final String CONTROLLER_BASE_PATH = ConvocatoriaBaremacionController.MAPPING;
  private static final String PATH_ID = ConvocatoriaBaremacionController.PATH_ID;
  private static final String PATH_BAREMOS = ConvocatoriaBaremacionController.PATH_BAREMOS;
  private static final Integer DEFAULT_DATA_PESO = 100;
  private static final BigDecimal DEFAULT_DATA_PUNTOS = new BigDecimal(20.5);
  private static final BigDecimal DEFAULT_DATA_CUANTIA = new BigDecimal(50.20);
  private static final TipoCuantia DEFAULT_DATA_TIPO_CUANTIA = TipoCuantia.PUNTOS;
  private static final Long DEFAULT_DATA_CONFIGURACION_BAREMO_ID = 1L;
  private static final Long DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID = 1L;

  private static final String NOMBRE_PREFIX = "Convocatoria baremación ";

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-E", "PRC-CON-B", "PRC-CON-R" })
  void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaBaremacion
    List<ConvocatoriaBaremacion> convocatoriasBaremacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasBaremacion.add(generarMockConvocatoriaBaremacion(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasBaremacion.size() ? convocatoriasBaremacion.size() : toIndex;
          List<ConvocatoriaBaremacion> content = convocatoriasBaremacion.subList(fromIndex, toIndex);
          Page<ConvocatoriaBaremacion> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasBaremacion.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaBaremacionOutput del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<ConvocatoriaBaremacionOutput> response = mapper.readValue(
        requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<ConvocatoriaBaremacionOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaBaremacionOutput convocatoriaBaremacion = response.get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaBaremacion.getNombre())
          .isEqualTo(NOMBRE_PREFIX + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-E", "PRC-CON-B", "PRC-CON-R" })
  void findAll_ReturnsNoContent() throws Exception {
    // given: Una lista con 0 ConvocatoriaBaremacion
    List<ConvocatoriaBaremacion> convocatoriasBaremacion = new ArrayList<>();

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaBaremacion> pageResponse = new PageImpl<>(convocatoriasBaremacion, pageable,
              convocatoriasBaremacion.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve 204 - NO CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-E", "PRC-CON-B", "PRC-CON-R" })
  void findById_WithExistingId_ReturnsConvocatoriaBaremacion() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      ConvocatoriaBaremacion convocatoriaBaremacion = ConvocatoriaBaremacion.builder().id(paramId).build();
      return convocatoriaBaremacion;
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested ConvocatoriaBaremacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-E", "PRC-CON-B", "PRC-CON-R" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      throw new ConvocatoriaBaremacionNotFoundException(paramId);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound present
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-R" })
  void activar_WithExistingId_ReturnsConvocatoriaBaremacion() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.activar(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      ConvocatoriaBaremacion convocatoriaBaremacion = generarMockConvocatoriaBaremacion(paramId, Boolean.TRUE);
      return convocatoriaBaremacion;
    });

    // when: activar by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_ID + "/activar", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested ConvocatoriaBaremacion is returned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-B" })
  void desactivar_WithExistingId_ReturnsConvocatoriaBaremacion() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.desactivar(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      ConvocatoriaBaremacion convocatoriaBaremacion = generarMockConvocatoriaBaremacion(paramId, Boolean.FALSE);
      return convocatoriaBaremacion;
    });

    // when: activar by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_ID + "/desactivar", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested ConvocatoriaBaremacion is returned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-C" })
  void create_ReturnsConvocatoriaBaremacion() throws Exception {
    // given: new ConvocatoriaBaremacion
    ConvocatoriaBaremacionInput convocatoriaBaremacion = generarMockConvocatoriaBaremacionInput();

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaBaremacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConvocatoriaBaremacion newConvocatoriaBaremacion = new ConvocatoriaBaremacion();
          BeanUtils.copyProperties(invocation.getArgument(0), newConvocatoriaBaremacion);
          newConvocatoriaBaremacion.setId(1L);
          return newConvocatoriaBaremacion;
        });

    // when: create ConvocatoriaBaremacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaBaremacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaBaremacion is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(convocatoriaBaremacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(convocatoriaBaremacion.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("aniosBaremables").value(convocatoriaBaremacion.getAniosBaremables()))
        .andExpect(MockMvcResultMatchers.jsonPath("ultimoAnio").value(convocatoriaBaremacion.getUltimoAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeTotal").value(convocatoriaBaremacion.getImporteTotal()))
        .andExpect(MockMvcResultMatchers.jsonPath("partidaPresupuestaria")
            .value(convocatoriaBaremacion.getPartidaPresupuestaria()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-E" })
  void update_ReturnsConvocatoriaBaremacion() throws Exception {
    // given: Existing ConvocatoriaBaremacion to be updated
    Long id = 1L;
    ConvocatoriaBaremacionInput convocatoriaBaremacion = generarMockConvocatoriaBaremacionInput();
    convocatoriaBaremacion.setNombre("nuevo-nombre");

    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaBaremacion>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaBaremacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaBaremacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConvocatoriaBaremacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(convocatoriaBaremacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(convocatoriaBaremacion.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("aniosBaremables").value(convocatoriaBaremacion.getAniosBaremables()))
        .andExpect(MockMvcResultMatchers.jsonPath("ultimoAnio").value(convocatoriaBaremacion.getUltimoAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeTotal").value(convocatoriaBaremacion.getImporteTotal()))
        .andExpect(MockMvcResultMatchers.jsonPath("partidaPresupuestaria")
            .value(convocatoriaBaremacion.getPartidaPresupuestaria()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-C" })
  void clone_WithNoExistingId_Returns500() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      throw new ConvocatoriaBaremacionNotFoundException(paramId);
    });

    // when: clone by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH + PATH_ID + "/clone", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound present
        andExpect(MockMvcResultMatchers.status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-C" })
  void clone_ok() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.clone(id, "Clonada - ", 1)).will((InvocationOnMock invocation) -> {
      return ConvocatoriaBaremacion.builder().id(2L).build();
    });

    // when: clone by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH + PATH_ID + "/clone", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(2L));
  }

  private ConvocatoriaBaremacion generarMockConvocatoriaBaremacion(Long id, String idRef, Boolean activo) {
    ConvocatoriaBaremacion convocatoriaBaremacion = new ConvocatoriaBaremacion();
    convocatoriaBaremacion.setId(id);
    convocatoriaBaremacion.setNombre(NOMBRE_PREFIX + idRef);
    convocatoriaBaremacion.setAnio(2022);
    convocatoriaBaremacion.setUltimoAnio(2023);
    convocatoriaBaremacion.setImporteTotal(new BigDecimal(50000));
    convocatoriaBaremacion.setActivo(activo);

    return convocatoriaBaremacion;
  }

  private ConvocatoriaBaremacion generarMockConvocatoriaBaremacion(Long id) {
    return generarMockConvocatoriaBaremacion(id, String.format("%03d", id), Boolean.TRUE);
  }

  private ConvocatoriaBaremacion generarMockConvocatoriaBaremacion(Long id, Boolean activo) {
    return generarMockConvocatoriaBaremacion(id, String.format("%03d", id), activo);
  }

  private ConvocatoriaBaremacionInput generarMockConvocatoriaBaremacionInput(String nombreSuffix, Integer anio) {
    ConvocatoriaBaremacionInput convocatoriaBaremacion = new ConvocatoriaBaremacionInput();
    convocatoriaBaremacion.setNombre(NOMBRE_PREFIX + nombreSuffix);
    convocatoriaBaremacion.setAnio(anio);
    convocatoriaBaremacion.setAniosBaremables(3);
    convocatoriaBaremacion.setUltimoAnio(2021);
    convocatoriaBaremacion.setImporteTotal(new BigDecimal(50000));
    convocatoriaBaremacion.setPartidaPresupuestaria("1234567890");

    return convocatoriaBaremacion;
  }

  private ConvocatoriaBaremacionInput generarMockConvocatoriaBaremacionInput() {
    return generarMockConvocatoriaBaremacionInput("001", 2022);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-C", "PRC-CON-E" })
  void findBaremos_ReturnsPage() throws Exception {
    // given: Una lista con 37 Baremo
    Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    List<Baremo> baremos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      baremos.add(generarMockBaremo(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(
            baremoService.findByConvocatoriaBaremacionId(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
                ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > baremos.size() ? baremos.size() : toIndex;
          List<Baremo> content = baremos.subList(fromIndex, toIndex);
          Page<Baremo> pageResponse = new PageImpl<>(content, pageable,
              baremos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_BAREMOS, convocatoriaBaremacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los BaremoOutput del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<BaremoOutput> response = mapper.readValue(
        requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<BaremoOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      BaremoOutput baremo = response.get(i - (page * pageSize) - 1);
      Assertions.assertThat(baremo.getId()).as("getId()").isEqualTo(i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-C", "PRC-CON-E" })
  void updateBaremos_Returns403ConvocatoriaBaremacionNotUpdatableException() throws Exception {
    // given: Una lista de Baremo asignados una ConvocatoriaBaremacion no editable
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    List<BaremoInput> baremos = new ArrayList<>();
    baremos.add(generarMockBaremoInputPeso(convocatoriaBaremacionId));

    BDDMockito.doThrow(new ConvocatoriaBaremacionNotUpdatableException()).when(service)
        .checkConvocatoriaBaremacionUpdatable(ArgumentMatchers.anyLong());

    // when: update Baremo
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_BAREMOS, convocatoriaBaremacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(baremos)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 403 error
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-C", "PRC-CON-E" })
  void updateBaremos_Returns400NoRelatedEntitiesException() throws Exception {
    // given: Una lista de Baremo asignados una ConvocatoriaBaremacion diferente de
    // la esperada
    final Long convocatoriaBaremacionIdExpected = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    final Long convocatoriaBaremacionIdActual = 2L;
    List<BaremoInput> baremos = new ArrayList<>();
    baremos.add(generarMockBaremoInputPeso(convocatoriaBaremacionIdActual));

    BDDMockito.doNothing().when(service).checkConvocatoriaBaremacionUpdatable(ArgumentMatchers.anyLong());

    // when: update Baremo
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_BAREMOS, convocatoriaBaremacionIdExpected)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(baremos)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-C", "PRC-CON-E" })
  void updateBaremos_Returns400() throws Exception {
    // given: Una lista de Baremo asignados una ConvocatoriaBaremacion con peso
    // total menor que 100
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    List<BaremoInput> baremos = new ArrayList<>();
    baremos.add(generarMockBaremoInputPeso(convocatoriaBaremacionId, 50));
    baremos.add(generarMockBaremoInputPeso(convocatoriaBaremacionId, 40));

    BDDMockito.doNothing().when(service).checkConvocatoriaBaremacionUpdatable(ArgumentMatchers.anyLong());

    // when: update Baremo
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_BAREMOS, convocatoriaBaremacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(baremos)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-V", "PRC-CON-C", "PRC-CON-E" })
  void updateBaremos_ReturnsBaremoList() throws Exception {
    // given: Una lista de Baremo asignados una ConvocatoriaBaremacion con peso
    // total menor que 100
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    List<BaremoInput> baremos = new ArrayList<>();
    baremos.add(generarMockBaremoInputPeso(convocatoriaBaremacionId, 50, 1L));
    baremos.add(generarMockBaremoInputPeso(convocatoriaBaremacionId, 50, 2L));
    baremos.add(generarMockBaremoInputPuntos(convocatoriaBaremacionId, 3L));

    BDDMockito.doNothing().when(service).checkConvocatoriaBaremacionUpdatable(ArgumentMatchers.anyLong());
    BDDMockito.given(baremoService.updateBaremos(ArgumentMatchers.anyLong(), ArgumentMatchers.<Baremo>anyList()))
        .willAnswer(new Answer<List<Baremo>>() {
          @Override
          public List<Baremo> answer(InvocationOnMock invocation) throws Throwable {
            List<Baremo> givenData = invocation.getArgument(1);
            return givenData.stream().map((givenBaremo) -> {
              Baremo newBaremo = new Baremo();
              BeanUtils.copyProperties(givenBaremo, newBaremo);
              return newBaremo;
            }).collect(Collectors.toList());
          }
        });

    // when: update Baremo
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_BAREMOS, convocatoriaBaremacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(baremos)))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve los Baremo actualizados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(3)));
  }

  private Baremo generarMockBaremo(Long id) {
    return this.generarMockBaremo(
        id, DEFAULT_DATA_PESO, DEFAULT_DATA_PUNTOS, DEFAULT_DATA_CUANTIA, DEFAULT_DATA_TIPO_CUANTIA,
        DEFAULT_DATA_CONFIGURACION_BAREMO_ID, DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID);
  }

  private Baremo generarMockBaremo(
      Long id, Integer peso, BigDecimal puntos, BigDecimal cuantia,
      TipoCuantia tipoCuantia, Long configuracionBaremoId, Long convocatoriaBaremacionId) {
    return Baremo.builder()
        .id(id)
        .peso(peso)
        .cuantia(cuantia)
        .puntos(puntos)
        .tipoCuantia(tipoCuantia)
        .configuracionBaremoId(configuracionBaremoId)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .build();
  }

  private BaremoInput generarMockBaremoInputPeso(Long convocatoriaBaremacionId) {
    return this.generarMockBaremoInput(
        DEFAULT_DATA_PESO, null, null, null,
        DEFAULT_DATA_CONFIGURACION_BAREMO_ID, convocatoriaBaremacionId);
  }

  private BaremoInput generarMockBaremoInputPeso(Long convocatoriaBaremacionId, Integer peso) {
    return this.generarMockBaremoInput(
        peso, null, null, null,
        DEFAULT_DATA_CONFIGURACION_BAREMO_ID, convocatoriaBaremacionId);
  }

  private BaremoInput generarMockBaremoInputPeso(Long convocatoriaBaremacionId, Integer peso,
      Long configuracionBaremoId) {
    return this.generarMockBaremoInput(
        peso, null, null, null,
        configuracionBaremoId, convocatoriaBaremacionId);
  }

  private BaremoInput generarMockBaremoInputPuntos(Long convocatoriaBaremacionId, Long configuracionBaremoId) {
    return this.generarMockBaremoInput(
        null, DEFAULT_DATA_PUNTOS, null, null,
        configuracionBaremoId, convocatoriaBaremacionId);
  }

  private BaremoInput generarMockBaremoInput(
      Integer peso, BigDecimal puntos, BigDecimal cuantia,
      TipoCuantia tipoCuantia, Long configuracionBaremoId, Long convocatoriaBaremacionId) {
    return BaremoInput.builder()
        .peso(peso)
        .cuantia(cuantia)
        .puntos(puntos)
        .tipoCuantia(tipoCuantia)
        .configuracionBaremoId(configuracionBaremoId)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .build();
  }
}
