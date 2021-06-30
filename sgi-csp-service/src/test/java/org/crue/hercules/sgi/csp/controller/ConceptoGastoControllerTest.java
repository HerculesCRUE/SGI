package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.service.ConceptoGastoService;
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
 * ConceptoGastoControllerTest
 */
@WebMvcTest(ConceptoGastoController.class)
public class ConceptoGastoControllerTest extends BaseControllerTest {

  @MockBean
  private ConceptoGastoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/conceptogastos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-C" })
  public void create_ReturnsConceptoGasto() throws Exception {
    // given: new ConceptoGasto
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConceptoGasto>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConceptoGasto newConceptoGasto = new ConceptoGasto();
          BeanUtils.copyProperties(invocation.getArgument(0), newConceptoGasto);
          newConceptoGasto.setId(1L);
          return newConceptoGasto;
        });

    // when: create ConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(conceptoGasto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConceptoGasto is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(conceptoGasto.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(conceptoGasto.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(conceptoGasto.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConceptoGasto with id filled
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConceptoGasto>any())).willThrow(new IllegalArgumentException());

    // when: create ConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(conceptoGasto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-E" })
  public void update_ReturnsConceptoGasto() throws Exception {
    // given: Existing ConceptoGasto to be updated
    ConceptoGasto conceptoGastoExistente = generarMockConceptoGasto(1L);
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);
    conceptoGasto.setNombre("nuevo-nombre");

    BDDMockito.given(service.update(ArgumentMatchers.<ConceptoGasto>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, conceptoGastoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(conceptoGasto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConceptoGasto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(conceptoGasto.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(conceptoGastoExistente.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(conceptoGastoExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);

    BDDMockito.willThrow(new ConceptoGastoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ConceptoGasto>any());

    // when: update ConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(conceptoGasto)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-R" })
  public void reactivar_ReturnsConvocatoria() throws Exception {
    // given: existing ConceptoGasto disabled
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);
    conceptoGasto.setActivo(false);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).will((InvocationOnMock invocation) -> {
      ConceptoGasto conceptoGastoEnabled = new ConceptoGasto();
      BeanUtils.copyProperties(conceptoGasto, conceptoGastoEnabled);
      conceptoGastoEnabled.setActivo(true);
      return conceptoGastoEnabled;
    });

    // when: registrar ConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, conceptoGasto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConceptoGasto enabled
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(conceptoGasto.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(conceptoGasto.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(conceptoGasto.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConceptoGastoNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-B" })
  public void desactivar_WithExistingId_ReturnConceptoGasto() throws Exception {
    // given: existing id
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      ConceptoGasto conceptoGastoDisabled = new ConceptoGasto();
      BeanUtils.copyProperties(conceptoGasto, conceptoGastoDisabled);
      conceptoGastoDisabled.setActivo(false);
      return conceptoGastoDisabled;
    });

    // when: desactivar ConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, conceptoGasto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConceptoGasto disabled
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(conceptoGasto.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(conceptoGasto.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(conceptoGasto.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConceptoGastoNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-CON-E", "CSP-CON-V", "CSP-CON-INV-V", "CSP-SOL-E",
      "CSP-SOL-V" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConceptoGasto
    List<ConceptoGasto> conceptoGastoes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      conceptoGastoes.add(generarMockConceptoGasto(i, "ConceptoGasto" + String.format("%03d", i)));
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
          toIndex = toIndex > conceptoGastoes.size() ? conceptoGastoes.size() : toIndex;
          List<ConceptoGasto> content = conceptoGastoes.subList(fromIndex, toIndex);
          Page<ConceptoGasto> pageResponse = new PageImpl<>(content, pageable, conceptoGastoes.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConceptoGasto del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConceptoGasto> conceptoGastoesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ConceptoGasto>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConceptoGasto conceptoGasto = conceptoGastoesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(conceptoGasto.getNombre()).isEqualTo("ConceptoGasto" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E", "CSP-CON-V", "CSP-CON-INV-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConceptoGasto
    List<ConceptoGasto> conceptoGastoes = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConceptoGasto> pageResponse = new PageImpl<>(conceptoGastoes, pageable, 0);
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
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-V", "CSP-TGTO-C", "CSP-TGTO-E", "CSP-TGTO-B",
      "CSP-TGTO-R" })
  public void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConceptoGasto
    List<ConceptoGasto> conceptoGastoes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      conceptoGastoes.add(generarMockConceptoGasto(i, "ConceptoGasto" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > conceptoGastoes.size() ? conceptoGastoes.size() : toIndex;
          List<ConceptoGasto> content = conceptoGastoes.subList(fromIndex, toIndex);
          Page<ConceptoGasto> pageResponse = new PageImpl<>(content, pageable, conceptoGastoes.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ConceptoGasto del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConceptoGasto> conceptoGastoesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ConceptoGasto>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConceptoGasto conceptoGasto = conceptoGastoesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(conceptoGasto.getNombre()).isEqualTo("ConceptoGasto" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TGTO-V", "CSP-TGTO-C", "CSP-TGTO-E", "CSP-TGTO-B", "CSP-TGTO-R",
      "CSP-TGTO-R" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConceptoGasto
    List<ConceptoGasto> conceptoGastoes = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConceptoGasto> pageResponse = new PageImpl<>(conceptoGastoes, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithExistingId_ReturnsConceptoGasto() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConceptoGasto(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConceptoGasto is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConceptoGastoNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto ConceptoGasto
   * 
   * @param id id del ConceptoGasto
   * @return el objeto ConceptoGasto
   */
  private ConceptoGasto generarMockConceptoGasto(Long id) {
    return generarMockConceptoGasto(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ConceptoGasto
   * 
   * @param id     id del ConceptoGasto
   * @param nombre nombre del ConceptoGasto
   * @return el objeto ConceptoGasto
   */
  private ConceptoGasto generarMockConceptoGasto(Long id, String nombre) {

    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id);
    conceptoGasto.setNombre(nombre);
    conceptoGasto.setDescripcion("descripcion-" + id);
    conceptoGasto.setActivo(true);

    return conceptoGasto;
  }

}
