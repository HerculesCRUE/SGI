package org.crue.hercules.sgi.prc.controller;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionInput;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionOutput;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionService;
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
 * ConvocatoriaBaremacionControllerTest
 */
@WebMvcTest(ConvocatoriaBaremacionController.class)
public class ConvocatoriaBaremacionControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaBaremacionService service;

  private static final String CONTROLLER_BASE_PATH = ConvocatoriaBaremacionController.REQUEST_MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";

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
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
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
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
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
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/activar", id)
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
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/desactivar", id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested ConvocatoriaBaremacion is returned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-CON-C" })
  public void create_ReturnsConvocatoriaBaremacion() throws Exception {
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
  public void update_ReturnsConvocatoriaBaremacion() throws Exception {
    // given: Existing ConvocatoriaBaremacion to be updated
    Long id = 1L;
    ConvocatoriaBaremacionInput convocatoriaBaremacion = generarMockConvocatoriaBaremacionInput();
    convocatoriaBaremacion.setNombre("nuevo-nombre");

    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaBaremacion>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaBaremacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
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
}
