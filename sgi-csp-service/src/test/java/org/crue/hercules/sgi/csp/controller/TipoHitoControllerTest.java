package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHitoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoHitoNombre;
import org.crue.hercules.sgi.csp.service.TipoHitoService;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * TipoHitoControllerTest
 */
@WebMvcTest(TipoHitoController.class)
class TipoHitoControllerTest extends BaseControllerTest {

  @MockBean
  private TipoHitoService tipoHitoService;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String TIPO_HITO_CONTROLLER_BASE_PATH = "/tipohitos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-C" })
  void create_ReturnsTipoHito() throws Exception {
    // given: Un TipoHito nuevo
    TipoHito data = generarMockTipoHito(1L, "nombre-1");
    data.setId(null);

    BDDMockito.given(tipoHitoService.create(ArgumentMatchers.<TipoHito>any()))
        .willAnswer(new Answer<TipoHito>() {
          @Override
          public TipoHito answer(InvocationOnMock invocation) throws Throwable {
            TipoHito givenData = invocation.getArgument(0, TipoHito.class);
            TipoHito newData = new TipoHito();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: Creamos un TipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_HITO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())

        // then: Crea el nuevo TipoHito y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion[0].value").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-C" })
  void create_WithId_Returns400() throws Exception {
    // given: Un TipoHito que produce un error al crearse porque ya tiene id
    String tipoHitoJson = "{ \"id\": \"1\", \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";
    BDDMockito.given(tipoHitoService.create(ArgumentMatchers.<TipoHito>any()))
        .willThrow(new IllegalArgumentException());
    // when: Creamos un TipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_HITO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoHitoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-E" })
  void update_ReturnsTipoHito() throws Exception {
    // given: Un TipoHito a modificar
    TipoHito data = generarMockTipoHito(1L, "nombre-1-modificado");

    BDDMockito.given(tipoHitoService.update(ArgumentMatchers.<TipoHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el TipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())

        // then: Modifica el TipoHito y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value").value("nombre-1-modificado"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion[0].value").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-E" })
  void update_WithIdNotExist_ReturnsNotFound() throws Exception {
    // given: Un TipoHito a modificar
    TipoHito data = generarMockTipoHito(1L, "nombre-1-modificado");

    BDDMockito.given(tipoHitoService.update(ArgumentMatchers.<TipoHito>any())).will((InvocationOnMock invocation) -> {
      throw new TipoFaseNotFoundException(((TipoHito) invocation.getArgument(0)).getId());
    });

    // when: Actualizamos el TipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: No encuentra el TipoHito y devuelve un 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-E" })
  void update_WithNombreRepetido_Returns400() throws Exception {
    // given: Un TipoHito que produce un error porque ya existe otro con el
    // mismo nombre
    String tipoHitoJson = "{ \"id\": \"2\", \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";
    BDDMockito.given(tipoHitoService.update(ArgumentMatchers.<TipoHito>any()))
        .willThrow(new IllegalArgumentException());
    // when: Actualizamos el TipoHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoHitoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 TipoHito
    List<TipoHito> tiposHito = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposHito.add(generarMockTipoHito(i, "TipoHito" + String.format("%03d", i)));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(tipoHitoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoHito>>() {
          @Override
          public Page<TipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposHito.size() ? tiposHito.size() : toIndex;
            List<TipoHito> content = tiposHito.subList(fromIndex, toIndex);
            Page<TipoHito> page = new PageImpl<>(content, pageable, tiposHito.size());
            return page;
          }
        });
    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(TIPO_HITO_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los TipoHito del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<TipoHito> tiposHitoResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoHito>>() {
        });
    for (int i = 31; i <= 37; i++) {
      TipoHito tipoHito = tiposHitoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES))
          .isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de TipoHito
    List<TipoHito> tiposHito = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(tipoHitoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoHito>>() {
          @Override
          public Page<TipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<TipoHito> page = new PageImpl<>(tiposHito, pageable, 0);
            return page;
          }
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(TIPO_HITO_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-V", "CSP-THITO-C", "CSP-THITO-E", "CSP-THITO-B",
      "CSP-THITO-R", "CSP-ME-C", "CSP-ME-E" })
  void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 TipoHito
    List<TipoHito> tiposHito = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposHito.add(generarMockTipoHito(i, "TipoHito" + String.format("%03d", i)));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito.given(tipoHitoService.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoHito>>() {
          @Override
          public Page<TipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposHito.size() ? tiposHito.size() : toIndex;
            List<TipoHito> content = tiposHito.subList(fromIndex, toIndex);
            Page<TipoHito> page = new PageImpl<>(content, pageable, tiposHito.size());
            return page;
          }
        });
    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_HITO_CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los TipoHito del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<TipoHito> tiposHitoResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoHito>>() {
        });
    for (int i = 31; i <= 37; i++) {
      TipoHito tipoHito = tiposHitoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHito.getNombre(),
          Language.ES)).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-V", "CSP-THITO-C", "CSP-THITO-E", "CSP-THITO-B",
      "CSP-THITO-R", "CSP-ME-C", "CSP-ME-E" })
  void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de TipoHito
    List<TipoHito> tiposHito = new ArrayList<>();
    Integer page = 0;
    Integer pageSize = 10;
    BDDMockito.given(tipoHitoService.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoHito>>() {
          @Override
          public Page<TipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<TipoHito> page = new PageImpl<>(tiposHito, pageable, 0);
            return page;
          }
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_HITO_CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_ReturnsTipoHito() throws Exception {
    // given: Un TipoHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(tipoHitoService.findById(ArgumentMatchers.anyLong())).willReturn((generarMockTipoHito(idBuscado)));
    // when: Buscamos el TipoHito por su id
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        // then: Devuelve TipoHito
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion[0].value").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-R" })
  void reactivar_WithExistingId_ReturnTipoHito() throws Exception {
    // given: existing id
    TipoHito tipoHito = generarMockTipoHito(1L);
    tipoHito.setActivo(Boolean.FALSE);
    BDDMockito.given(tipoHitoService.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      TipoHito tipoHitoEnabled = new TipoHito();
      BeanUtils.copyProperties(tipoHito, tipoHitoEnabled);
      tipoHitoEnabled.setActivo(Boolean.TRUE);
      return tipoHitoEnabled;
    });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, tipoHito.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return enabled TipoHito
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(tipoHito.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value")
            .value(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES)))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion[0].value")
            .value(I18nHelper.getValueForLanguage(tipoHito.getDescripcion(), Language.ES)))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-R" })
  void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new TipoHitoNotFoundException(id)).given(tipoHitoService).enable(ArgumentMatchers.<Long>any());

    // when: enable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-B" })
  void desactivar_WithExistingId_ReturnTipoHito() throws Exception {
    // given: existing id
    Long idBuscado = 1L;
    TipoHito tipoHito = generarMockTipoHito(idBuscado);

    BDDMockito.given(tipoHitoService.disable(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          TipoHito tipoHitoDisabled = new TipoHito();
          BeanUtils.copyProperties(tipoHito, tipoHitoDisabled);
          tipoHitoDisabled.setActivo(false);
          return tipoHitoDisabled;
        });

    // when: disable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return disabled TipoHito
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(idBuscado))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value")
            .value(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES)))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion[0].value")
            .value(I18nHelper.getValueForLanguage(tipoHito.getDescripcion(), Language.ES)))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-THITO-B" })
  void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;
    BDDMockito.willThrow(new TipoHitoNotFoundException(id)).given(tipoHitoService)
        .disable(ArgumentMatchers.<Long>any());

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_HITO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  TipoHito generarMockTipoHito(Long id) {
    return generarMockTipoHito(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  TipoHito generarMockTipoHito(Long id, String nombre) {
    Set<TipoHitoNombre> nombreTipoHito = new HashSet<>();
    nombreTipoHito.add(new TipoHitoNombre(Language.ES, nombre));

    Set<TipoHitoDescripcion> descripcionTipoHito = new HashSet<>();
    descripcionTipoHito.add(new TipoHitoDescripcion(Language.ES, "descripcion-" + id));

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre(nombreTipoHito);
    tipoHito.setDescripcion(descripcionTipoHito);
    tipoHito.setActivo(Boolean.TRUE);
    return tipoHito;
  }
}
