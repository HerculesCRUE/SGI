package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.service.TipoFaseService;
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
 * TipoFaseControllerTest
 */
@WebMvcTest(TipoFaseController.class)
public class TipoFaseControllerTest extends BaseControllerTest {

  @MockBean
  private TipoFaseService tipoFaseService;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String TIPO_FASE_CONTROLLER_BASE_PATH = "/tipofases";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-C" })
  public void create_ReturnsTipoFase() throws Exception {
    // given: Un TipoFase nuevo
    String tipoFaseJson = "{ \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\",  \"activo\": \"true\"  }";
    BDDMockito.given(tipoFaseService.create(ArgumentMatchers.<TipoFase>any())).will((InvocationOnMock invocation) -> {
      TipoFase tipoFaseCreado = invocation.getArgument(0);
      tipoFaseCreado.setId(1L);
      return tipoFaseCreado;
    });
    // when: Creamos un TipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_FASE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFaseJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo TipoFase y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: Un TipoFase que produce un error al crearse porque ya tiene id
    String tipoFaseJson = "{ \"id\": \"1\", \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";
    BDDMockito.given(tipoFaseService.create(ArgumentMatchers.<TipoFase>any()))
        .willThrow(new IllegalArgumentException());
    // when: Creamos un TipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_FASE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFaseJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-E" })
  public void update_ReturnsTipoFase() throws Exception {
    // given: Un TipoFase a modificar
    String tipoFaseJson = "{\"id\": \"1\", \"nombre\": \"nombre-1-modificado\", \"descripcion\": \"descripcion-1\", \"activo\": true }";

    BDDMockito.given(tipoFaseService.update(ArgumentMatchers.<TipoFase>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    // when: Actualizamos el TipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFaseJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el TipoFase y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1-modificado"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-E" })
  public void update_WithIdNotExist_ReturnsNotFound() throws Exception {
    // given: Un TipoFase a modificar
    String replaceTipoFaseJson = "{\"id\": \"1\", \"nombre\": \"nombre-1-modificado\", \"descripcion\": \"descripcion-1\", \"activo\": true }";
    BDDMockito.given(tipoFaseService.update(ArgumentMatchers.<TipoFase>any())).will((InvocationOnMock invocation) -> {
      throw new TipoFaseNotFoundException(((TipoFase) invocation.getArgument(0)).getId());
    });
    // when: Actualizamos el TipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoFaseJson))
        // then: No encuentra el TipoFase y devuelve un 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-E" })
  public void update_WithNombreRepetido_Returns400() throws Exception {
    // given: Un TipoFase que produce un error porque ya existe otro con el
    // mismo nombre
    String tipoFaseJson = "{ \"id\": \"2\", \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";
    BDDMockito.given(tipoFaseService.update(ArgumentMatchers.<TipoFase>any()))
        .willThrow(new IllegalArgumentException());
    // when: Actualizamos el TipoFase
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFaseJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 TipoFase
    List<TipoFase> tiposFase = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposFase.add(generarMockTipoFase(i, "TipoFase" + String.format("%03d", i)));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(tipoFaseService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFase>>() {
          @Override
          public Page<TipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposFase.size() ? tiposFase.size() : toIndex;
            List<TipoFase> content = tiposFase.subList(fromIndex, toIndex);
            Page<TipoFase> page = new PageImpl<>(content, pageable, tiposFase.size());
            return page;
          }
        });
    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(TIPO_FASE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
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
    List<TipoFase> tiposFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoFase>>() {
        });
    for (int i = 31; i <= 37; i++) {
      TipoFase tipoFase = tiposFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(tipoFase.getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de TipoFase
    List<TipoFase> tiposFase = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(tipoFaseService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFase>>() {
          @Override
          public Page<TipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<TipoFase> page = new PageImpl<>(tiposFase, pageable, 0);
            return page;
          }
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(TIPO_FASE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-V", "CSP-TFASE-C", "CSP-TFASE-E", "CSP-TFASE-B",
      "CSP-TFASE-R" })
  public void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 TipoFase
    List<TipoFase> tiposFase = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposFase.add(generarMockTipoFase(i, "TipoFase" + String.format("%03d", i)));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(tipoFaseService.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFase>>() {
          @Override
          public Page<TipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposFase.size() ? tiposFase.size() : toIndex;
            List<TipoFase> content = tiposFase.subList(fromIndex, toIndex);
            Page<TipoFase> page = new PageImpl<>(content, pageable, tiposFase.size());
            return page;
          }
        });
    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_FASE_CONTROLLER_BASE_PATH + "/todos")
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
    List<TipoFase> tiposFaseResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoFase>>() {
        });
    for (int i = 31; i <= 37; i++) {
      TipoFase tipoFase = tiposFaseResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(tipoFase.getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-V", "CSP-TFASE-C", "CSP-TFASE-E", "CSP-TFASE-B",
      "CSP-TFASE-R" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de TipoFase
    List<TipoFase> tiposFase = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(tipoFaseService.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFase>>() {
          @Override
          public Page<TipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<TipoFase> page = new PageImpl<>(tiposFase, pageable, 0);
            return page;
          }
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_FASE_CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_ReturnsTipoFase() throws Exception {
    // given: Un TipoFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(tipoFaseService.findById(ArgumentMatchers.anyLong())).willReturn((generarMockTipoFase(idBuscado)));
    // when: Buscamos el TipoFase por su id
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        // then: Devuelve TipoFase
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-R" })
  public void reactivar_WithExistingId_ReturnTipoFase() throws Exception {
    // given: existing id
    TipoFase tipoFase = generarMockTipoFase(1L);
    tipoFase.setActivo(Boolean.FALSE);
    BDDMockito.given(tipoFaseService.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      TipoFase tipoFaseEnabled = new TipoFase();
      BeanUtils.copyProperties(tipoFase, tipoFaseEnabled);
      tipoFaseEnabled.setActivo(Boolean.TRUE);
      return tipoFaseEnabled;
    });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, tipoFase.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return enabled TipoFase
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(tipoFase.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoFase.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoFase.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new TipoFaseNotFoundException(id)).given(tipoFaseService).enable(ArgumentMatchers.<Long>any());

    // when: enable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-B" })
  public void desactivar_WithExistingId_ReturnTipoFase() throws Exception {
    // given: existing id
    Long idBuscado = 1L;
    TipoFase tipoFase = generarMockTipoFase(idBuscado);

    BDDMockito.given(tipoFaseService.disable(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          TipoFase tipoFaseDisabled = new TipoFase();
          BeanUtils.copyProperties(tipoFase, tipoFaseDisabled);
          tipoFaseDisabled.setActivo(false);
          return tipoFaseDisabled;
        });

    // when: disable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return disabled TipoFase
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(idBuscado))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoFase.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoFase.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TFASE-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;
    BDDMockito.willThrow(new TipoFaseNotFoundException(id)).given(tipoFaseService)
        .disable(ArgumentMatchers.<Long>any());

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_FASE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoFase
   * @return el objeto TipoFase
   */
  public TipoFase generarMockTipoFase(Long id) {
    return generarMockTipoFase(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoFase
   * @return el objeto TipoFase
   */
  public TipoFase generarMockTipoFase(Long id, String nombre) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombre);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(Boolean.TRUE);
    return tipoFase;
  }
}
