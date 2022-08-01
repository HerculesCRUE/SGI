package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.service.ProgramaService;
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
 * ProgramaControllerTest
 */
@WebMvcTest(ProgramaController.class)
class ProgramaControllerTest extends BaseControllerTest {

  @MockBean
  private ProgramaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/programas";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-C" })
  void create_ReturnsPrograma() throws Exception {
    // given: new Programa
    Programa programa = generarMockPrograma(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Programa>any())).willAnswer((InvocationOnMock invocation) -> {
      Programa newPrograma = new Programa();
      BeanUtils.copyProperties(invocation.getArgument(0), newPrograma);
      newPrograma.setId(1L);
      return newPrograma;
    });

    // when: create Programa
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(programa)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new Programa is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(programa.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(programa.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(programa.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-C" })
  void create_WithId_Returns400() throws Exception {
    // given: a Programa with id filled
    Programa programa = generarMockPrograma(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Programa>any())).willThrow(new IllegalArgumentException());

    // when: create Programa
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(programa)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-E" })
  void update_ReturnsPrograma() throws Exception {
    // given: Existing Programa to be updated
    Programa programaExistente = generarMockPrograma(1L);
    Programa programa = generarMockPrograma(1L);
    programa.setNombre("nuevo-nombre");

    BDDMockito.given(service.update(ArgumentMatchers.<Programa>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update Programa
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, programaExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(programa)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Programa is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(programaExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(programa.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(programa.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(programaExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    Programa programa = generarMockPrograma(1L);

    BDDMockito.willThrow(new ProgramaNotFoundException(id)).given(service).update(ArgumentMatchers.<Programa>any());

    // when: update Programa
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(programa)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-R" })
  void reactivar_WithExistingId_ReturnPrograma() throws Exception {
    // given: existing id
    Programa programa = generarMockPrograma(1L);
    programa.setActivo(false);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Programa programaDisabled = new Programa();
      BeanUtils.copyProperties(programa, programaDisabled);
      programaDisabled.setActivo(true);
      return programaDisabled;
    });

    // when: reactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, programa.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Programa is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(programa.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(programa.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-R" })
  void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProgramaNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-PRG-B" })
  void desactivar_WithExistingId_ReturnTipoFinanciacion() throws Exception {
    // given: existing id
    Programa programa = generarMockPrograma(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Programa programaDisabled = new Programa();
      BeanUtils.copyProperties(programa, programaDisabled);
      programaDisabled.setActivo(false);
      return programaDisabled;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, programa.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Programa is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(programa.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(programa.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-B" })
  void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProgramaNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-PRG-E" })
  void findById_WithExistingId_ReturnsPrograma() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockPrograma(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Programa is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProgramaNotFoundException(1L);
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
  void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 Programa
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i, "Programa" + String.format("%03d", i), null));
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
          toIndex = toIndex > programas.size() ? programas.size() : toIndex;
          List<Programa> content = programas.subList(fromIndex, toIndex);
          Page<Programa> pageResponse = new PageImpl<>(content, pageable, programas.size());
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
    List<Programa> tiposFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Programa>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Programa programa = tiposFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("Programa" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Programa
    List<Programa> programas = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> pageResponse = new PageImpl<>(programas, pageable, 0);
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void findAllPlan_ReturnsPage() throws Exception {
    // given: Una lista con 37 Programa
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i, "Programa" + String.format("%03d", i), null));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllPlan(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > programas.size() ? programas.size() : toIndex;
          List<Programa> content = programas.subList(fromIndex, toIndex);
          Page<Programa> pageResponse = new PageImpl<>(content, pageable, programas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/plan").with(SecurityMockMvcRequestPostProcessors.csrf())
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
    List<Programa> tiposFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Programa>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Programa programa = tiposFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("Programa" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void findAllPlan_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Programa
    List<Programa> programas = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllPlan(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> pageResponse = new PageImpl<>(programas, pageable, 0);
          return pageResponse;
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/plan").with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-V", "CSP-PRG-C", "CSP-PRG-E", "CSP-PRG-B", "CSP-PRG-R" })
  void findAllTodosPlan_ReturnsPage() throws Exception {
    // given: Una lista con 37 Programa
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i, "Programa" + String.format("%03d", i), null));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllTodosPlan(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > programas.size() ? programas.size() : toIndex;
          List<Programa> content = programas.subList(fromIndex, toIndex);
          Page<Programa> pageResponse = new PageImpl<>(content, pageable, programas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/plan/todos")
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
    List<Programa> tiposFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Programa>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Programa programa = tiposFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("Programa" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRG-V", "CSP-PRG-C", "CSP-PRG-E", "CSP-PRG-B", "CSP-PRG-R" })
  void findAllTodosPlan_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Programa
    List<Programa> programas = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(service.findAllTodosPlan(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> pageResponse = new PageImpl<>(programas, pageable, 0);
          return pageResponse;
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/plan/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void findAllHijosPrograma_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadConvocante para la Convocatoria
    Long convocatoriaId = 1L;

    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i, "Programa" + String.format("%03d", i), null));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllHijosPrograma(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > programas.size() ? programas.size() : toIndex;
          List<Programa> content = programas.subList(fromIndex, toIndex);
          Page<Programa> pageResponse = new PageImpl<>(content, pageable, programas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/hijos", convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadConvocante del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<Programa> programasResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Programa>>() {
        });

    for (int i = 31; i <= 37; i++) {
      Programa programa = programasResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("Programa" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void findAllHijosPrograma_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Programa
    Long programaId = 1L;
    List<Programa> programas = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllHijosPrograma(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<Programa> pageResponse = new PageImpl<>(programas, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/hijos", programaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id id del Programa
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id) {
    return generarMockPrograma(id, "nombre-" + id, null);
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id              id del Programa
   * @param nombre          nombre del Programa
   * @param idProgramaPadre id del Programa padre
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id, String nombre, Long idProgramaPadre) {
    Programa programa = new Programa();
    programa.setId(id);
    programa.setNombre(nombre);
    programa.setDescripcion("descripcion-" + id);

    if (idProgramaPadre != null) {
      programa.setPadre(generarMockPrograma(idProgramaPadre));
    }
    programa.setActivo(true);

    return programa;
  }

}
