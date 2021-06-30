package org.crue.hercules.sgi.usr.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.usr.exceptions.UnidadNotFoundException;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.service.UnidadService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
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
 * UnidadControllerTest
 */
@WebMvcTest(UnidadController.class)
class UnidadControllerTest extends BaseControllerTest {

  @MockBean
  private UnidadService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/unidades";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V", "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E",
      "CSP-PRO-B", "CSP-PRO-R", "CSP-ME-C", "CSP-ME-E" })
  void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 Unidad
    List<Unidad> unidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      unidades.add(generarMockUnidad(i, "Unidad" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > unidades.size() ? unidades.size() : toIndex;
          List<Unidad> content = unidades.subList(fromIndex, toIndex);
          Page<Unidad> pageResponse = new PageImpl<>(content, pageable, unidades.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los Unidad del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<Unidad> unidadesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Unidad>>() {
        });

    for (int i = 31; i <= 37; i++) {
      Unidad unidad = unidadesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(unidad.getNombre()).isEqualTo("Unidad" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V", "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E",
      "CSP-PRO-B", "CSP-PRO-R", "CSP-ME-C", "CSP-ME-E" })
  void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Unidad
    List<Unidad> unidades = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Unidad> pageResponse = new PageImpl<>(unidades, pageable, 0);
          return pageResponse;
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V", "CSP-CON-INV-V", "CSP-SOL-C", "CSP-SOL-E", "CSP-SOL-V",
      "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E" })
  void findById_WithExistingId_ReturnsUnidad() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockUnidad(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Unidad is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V", "CSP-CON-INV-V", "CSP-SOL-C", "CSP-SOL-E", "CSP-SOL-V",
      "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new UnidadNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-V", "CSP-CON-C", "CSP-CON-E", "CSP-SOL-C", "CSP-SOL-E",
      "CSP-SOL-V", "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E" })
  void findAllTodosRestringidos_ReturnsPage() throws Exception {
    // given: Una lista con 37 Unidad
    List<Unidad> unidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      unidades.add(generarMockUnidad(i, "Unidad" + String.format("%03d", i)));
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
          toIndex = toIndex > unidades.size() ? unidades.size() : toIndex;
          List<Unidad> content = unidades.subList(fromIndex, toIndex);
          Page<Unidad> pageResponse = new PageImpl<>(content, pageable, unidades.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/restringidos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los Unidad del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<Unidad> unidadesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Unidad>>() {
        });

    for (int i = 31; i <= 37; i++) {
      Unidad unidad = unidadesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(unidad.getNombre()).isEqualTo("Unidad" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V", "CSP-CON-C", "CSP-CON-E", "CSP-SOL-C", "CSP-SOL-E",
      "CSP-SOL-V", "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E" })
  void findAllTodosRestringidos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Unidad
    List<Unidad> unidades = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllRestringidos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Unidad> pageResponse = new PageImpl<>(unidades, pageable, 0);
          return pageResponse;
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/restringidos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto Unidad
   * 
   * @param id id del Unidad
   * @return el objeto Unidad
   */
  private Unidad generarMockUnidad(Long id) {
    return generarMockUnidad(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto Unidad
   * 
   * @param id     id del Unidad
   * @param nombre nombre del Unidad
   * @return el objeto Unidad
   */
  private Unidad generarMockUnidad(Long id, String nombre) {

    Unidad unidad = new Unidad();
    unidad.setId(id);
    unidad.setNombre(nombre);
    unidad.setAcronimo("OPE");
    unidad.setDescripcion("descripcion-" + id);
    unidad.setActivo(true);

    return unidad;
  }

}