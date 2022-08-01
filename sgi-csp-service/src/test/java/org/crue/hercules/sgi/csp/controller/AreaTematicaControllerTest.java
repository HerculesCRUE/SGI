package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.AreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.service.AreaTematicaService;
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
 * AreaTematicaControllerTest
 */
@WebMvcTest(AreaTematicaController.class)
class AreaTematicaControllerTest extends BaseControllerTest {

  @MockBean
  private AreaTematicaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_GRUPO = "/grupo";
  private static final String PATH_PARAMETER_HIJOS = "/hijos";
  private static final String PATH_PARAMETER_TODOS = "/todos";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/areatematicas";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-C" })
  void create_ReturnsModeloAreaTematica() throws Exception {
    // given: new AreaTematica
    AreaTematica areaTematica = generarMockAreaTematica(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<AreaTematica>any())).willAnswer((InvocationOnMock invocation) -> {
      AreaTematica newAreaTematica = new AreaTematica();
      BeanUtils.copyProperties(invocation.getArgument(0), newAreaTematica);
      newAreaTematica.setId(1L);
      return newAreaTematica;
    });

    // when: create AreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(areaTematica)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new AreaTematica is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(areaTematica.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(areaTematica.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("padre").isEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(areaTematica.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-C" })
  void create_WithId_Returns400() throws Exception {
    // given: a AreaTematica with id filled
    AreaTematica areaTematica = generarMockAreaTematica(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<AreaTematica>any())).willThrow(new IllegalArgumentException());

    // when: create AreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(areaTematica)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-E" })
  void update_ReturnsAreaTematica() throws Exception {
    // given: Existing AreaTematica to be updated
    AreaTematica areaTematicaExistente = generarMockAreaTematica(1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L);
    areaTematica.setNombre("nuevo-nombre");

    BDDMockito.given(service.update(ArgumentMatchers.<AreaTematica>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update AreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, areaTematicaExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(areaTematica)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: AreaTematica is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(areaTematicaExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(areaTematica.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(areaTematica.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("padre").isEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(areaTematicaExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    AreaTematica areaTematica = generarMockAreaTematica(1L);

    BDDMockito.willThrow(new AreaTematicaNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<AreaTematica>any());

    // when: update AreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(areaTematica)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-R" })
  void reactivar_WithExistingId_ReturnAreaTematica() throws Exception {
    // given: existing id
    AreaTematica areaTematica = generarMockAreaTematica(1L);
    areaTematica.setActivo(Boolean.FALSE);
    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      AreaTematica areaTematicaEnabled = new AreaTematica();
      BeanUtils.copyProperties(areaTematica, areaTematicaEnabled);
      areaTematicaEnabled.setActivo(Boolean.TRUE);
      return areaTematicaEnabled;
    });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, areaTematica.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return enabled AreaTematica
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(areaTematica.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(areaTematica.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(areaTematica.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("padre").isEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-R" })
  void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new AreaTematicaNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

    // when: enable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-B" })
  void desactivar_WithExistingId_ReturnAreaTematica() throws Exception {
    // given: existing id
    AreaTematica areaTematica = generarMockAreaTematica(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      AreaTematica areaTematicaDisabled = new AreaTematica();
      BeanUtils.copyProperties(areaTematica, areaTematicaDisabled);
      areaTematicaDisabled.setActivo(false);
      return areaTematicaDisabled;
    });

    // when: disable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, areaTematica.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return disabled AreaTematica
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(areaTematica.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(areaTematica.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(areaTematica.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("padre").isEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-B" })
  void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new AreaTematicaNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-E" })
  void findById_WithExistingId_ReturnsAreaTematica() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockAreaTematica(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested AreaTematica is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("padre").isEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new AreaTematicaNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-INV-V" })
  void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 AreaTematica
    List<AreaTematica> areasTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areasTematicas.add(generarMockAreaTematica(i, "AreaTematica" + String.format("%03d", i), null));
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
          toIndex = toIndex > areasTematicas.size() ? areasTematicas.size() : toIndex;
          List<AreaTematica> content = areasTematicas.subList(fromIndex, toIndex);
          Page<AreaTematica> pageResponse = new PageImpl<>(content, pageable, areasTematicas.size());
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
    List<AreaTematica> tiposFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<AreaTematica>>() {
        });
    for (int i = 31; i <= 37; i++) {
      AreaTematica areaTematica = tiposFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(areaTematica.getNombre()).isEqualTo("AreaTematica" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-INV-V" })
  void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de AreaTematica
    List<AreaTematica> areasTematicas = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> pageResponse = new PageImpl<>(areasTematicas, pageable, 0);
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-V", "CSP-CON-C", "CSP-CON-V" })
  void findAllGrupo_ReturnsPage() throws Exception {
    // given: Una lista con 37 AreaTematica
    List<AreaTematica> areasTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areasTematicas.add(generarMockAreaTematica(i, "AreaTematica" + String.format("%03d", i), null));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllGrupo(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > areasTematicas.size() ? areasTematicas.size() : toIndex;
          List<AreaTematica> content = areasTematicas.subList(fromIndex, toIndex);
          Page<AreaTematica> pageResponse = new PageImpl<>(content, pageable, areasTematicas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_GRUPO)
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
    List<AreaTematica> tiposFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<AreaTematica>>() {
        });
    for (int i = 31; i <= 37; i++) {
      AreaTematica areaTematica = tiposFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(areaTematica.getNombre()).isEqualTo("AreaTematica" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V", "CSP-CON-C", "CSP-CON-V" })
  void findAllGrupo_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de AreaTematica
    List<AreaTematica> AreaTematicas = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllGrupo(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> pageResponse = new PageImpl<>(AreaTematicas, pageable, 0);
          return pageResponse;
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_GRUPO)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-V", "CSP-AREA-C", "CSP-AREA-E", "CSP-AREA-B",
      "CSP-AREA-R" })
  void findAllTodosGrupo_ReturnsPage() throws Exception {
    // given: Una lista con 37 AreaTematica
    List<AreaTematica> areasTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areasTematicas.add(generarMockAreaTematica(i, "AreaTematica" + String.format("%03d", i), null));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllTodosGrupo(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > areasTematicas.size() ? areasTematicas.size() : toIndex;
          List<AreaTematica> content = areasTematicas.subList(fromIndex, toIndex);
          Page<AreaTematica> pageResponse = new PageImpl<>(content, pageable, areasTematicas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_GRUPO + PATH_PARAMETER_TODOS)
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
    List<AreaTematica> tiposFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<AreaTematica>>() {
        });
    for (int i = 31; i <= 37; i++) {
      AreaTematica areaTematica = tiposFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(areaTematica.getNombre()).isEqualTo("AreaTematica" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-AREA-V", "CSP-AREA-C", "CSP-AREA-E", "CSP-AREA-B",
      "CSP-AREA-R" })
  void findAllTodosGrupo_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de AreaTematica
    List<AreaTematica> areasTematicas = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllTodosGrupo(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> pageResponse = new PageImpl<>(areasTematicas, pageable, 0);
          return pageResponse;
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_GRUPO + PATH_PARAMETER_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C", "CSP-CON-E" })
  void findAllHijosAreaTematica_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadConvocante para la Convocatoria
    Long convocatoriaId = 1L;

    List<AreaTematica> areasTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areasTematicas.add(generarMockAreaTematica(i, "AreaTematica" + String.format("%03d", i), null));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllHijosAreaTematica(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > areasTematicas.size() ? areasTematicas.size() : toIndex;
          List<AreaTematica> content = areasTematicas.subList(fromIndex, toIndex);
          Page<AreaTematica> pageResponse = new PageImpl<>(content, pageable, areasTematicas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_HIJOS, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadConvocante del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<AreaTematica> AreaTematicasResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<AreaTematica>>() {
        });

    for (int i = 31; i <= 37; i++) {
      AreaTematica areaTematica = AreaTematicasResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(areaTematica.getNombre()).isEqualTo("AreaTematica" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C", "CSP-CON-E" })
  void findAllHijosAreaTematica_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de AreaTematica
    Long AreaTematicaId = 1L;
    List<AreaTematica> areasTematicas = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllHijosAreaTematica(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<AreaTematica> pageResponse = new PageImpl<>(areasTematicas, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_HIJOS, AreaTematicaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto AreaTematica
   * 
   * @param id id del AreaTematica
   * @return el objeto AreaTematica
   */
  private AreaTematica generarMockAreaTematica(Long id) {
    return generarMockAreaTematica(id, "nombre-" + id, null);
  }

  /**
   * Función que devuelve un objeto AreaTematica
   * 
   * @param id                  id del AreaTematica
   * @param nombre              nombre del AreaTematica
   * @param idAreaTematicaPadre id del AreaTematica padre
   * @return el objeto AreaTematica
   */
  private AreaTematica generarMockAreaTematica(Long id, String nombre, Long idAreaTematicaPadre) {
    AreaTematica areaTematica = new AreaTematica();
    areaTematica.setId(id);
    areaTematica.setNombre(nombre);
    areaTematica.setDescripcion("descripcion-" + id);

    if (idAreaTematicaPadre != null) {
      areaTematica.setPadre(generarMockAreaTematica(idAreaTematicaPadre));
    }
    areaTematica.setActivo(true);

    return areaTematica;
  }

}
