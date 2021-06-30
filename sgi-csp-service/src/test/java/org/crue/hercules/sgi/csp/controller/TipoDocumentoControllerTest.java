package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.TipoDocumentoService;
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
 * TipoDocumentoControllerTest
 */
@WebMvcTest(TipoDocumentoController.class)
public class TipoDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private TipoDocumentoService tipoDocumentoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String TIPO_DOCUMENTO_CONTROLLER_BASE_PATH = "/tipodocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-C" })
  public void create_ReturnsTipoDocumento() throws Exception {
    // given: Un TipoDocumento nuevo
    String tipoDocumentoJson = "{ \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";

    BDDMockito.given(tipoDocumentoService.create(ArgumentMatchers.<TipoDocumento>any()))
        .will((InvocationOnMock invocation) -> {
          TipoDocumento tipoDocumentoCreado = invocation.getArgument(0);
          tipoDocumentoCreado.setId(1L);
          if (tipoDocumentoCreado.getActivo() == null) {
            tipoDocumentoCreado.setActivo(true);
          }
          return tipoDocumentoCreado;
        });

    // when: Creamos un TipoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoDocumentoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo TipoDocumento y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: Un TipoDocumento que produce un error al crearse porque ya tiene id
    String tipoDocumentoJson = "{ \"id\": \"1\", \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";

    BDDMockito.given(tipoDocumentoService.create(ArgumentMatchers.<TipoDocumento>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un TipoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoDocumentoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-E" })
  public void update_ReturnsTipoDocumento() throws Exception {
    // given: Un TipoDocumento a modificar
    String tipoDocumentoJson = "{\"id\": \"1\", \"nombre\": \"nombre-1-modificado\", \"descripcion\": \"descripcion-1\", \"activo\": true }";

    BDDMockito.given(tipoDocumentoService.update(ArgumentMatchers.<TipoDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el TipoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoDocumentoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el TipoDocumento y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1-modificado"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-E" })
  public void update_WithIdNotExist_Returns404() throws Exception {
    // given: Un TipoDocumento a modificar
    String tipoDocumentoJson = "{\"id\": \"1\", \"nombre\": \"nombre-1-modificado\", \"descripcion\": \"descripcion-1\", \"activo\": true }";

    BDDMockito.given(tipoDocumentoService.update(ArgumentMatchers.<TipoDocumento>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoDocumentoNotFoundException(((TipoDocumento) invocation.getArgument(0)).getId());
        });

    // when: Actualizamos el TipoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoDocumentoJson))
        // then: No encuentra el TipoDocumento y devuelve un 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-E" })
  public void update_WithNombreRepetido_Returns400() throws Exception {
    // given: Un TipoDocumento que produce un error porque ya existe otro con el
    // mismo nombre
    String tipoDocumentoJson = "{ \"id\": \"2\", \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";

    BDDMockito.given(tipoDocumentoService.update(ArgumentMatchers.<TipoDocumento>any()))
        .willThrow(new IllegalArgumentException());

    // when: Actualizamos el TipoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoDocumentoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-R" })
  public void reactivar_WithExistingId_ReturnTipoDocumento() throws Exception {
    // given: existing id
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    tipoDocumento.setActivo(Boolean.FALSE);
    BDDMockito.given(tipoDocumentoService.enable(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          TipoDocumento tipoDocumentoEnabled = new TipoDocumento();
          BeanUtils.copyProperties(tipoDocumento, tipoDocumentoEnabled);
          tipoDocumentoEnabled.setActivo(Boolean.TRUE);
          return tipoDocumentoEnabled;
        });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR,
                tipoDocumento.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return enabled TipoDocumento
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(tipoDocumento.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoDocumento.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoDocumento.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new TipoDocumentoNotFoundException(id)).given(tipoDocumentoService)
        .enable(ArgumentMatchers.<Long>any());

    // when: enable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-B" })
  public void desactivar_WithExistingId_ReturnTipoDocumento() throws Exception {
    // given: existing id
    Long idBuscado = 1L;
    TipoDocumento tipoDocumento = generarMockTipoDocumento(idBuscado);

    BDDMockito.given(tipoDocumentoService.disable(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          TipoDocumento tipoDocumentoDisabled = new TipoDocumento();
          BeanUtils.copyProperties(tipoDocumento, tipoDocumentoDisabled);
          tipoDocumentoDisabled.setActivo(false);
          return tipoDocumentoDisabled;
        });

    // when: disable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return disabled TipoDocumento
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(idBuscado))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoDocumento.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoDocumento.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;
    BDDMockito.willThrow(new TipoDocumentoNotFoundException(id)).given(tipoDocumentoService)
        .disable(ArgumentMatchers.<Long>any());

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 TipoDocumento
    List<TipoDocumento> tiposDocumento = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposDocumento.add(generarMockTipoDocumento(i, "TipoDocumento" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposDocumento.size() ? tiposDocumento.size() : toIndex;
            List<TipoDocumento> content = tiposDocumento.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tiposDocumento.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los TipoDocumento del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<TipoDocumento> tiposDocumentoResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoDocumento>>() {
        });

    for (int i = 31; i <= 37; i++) {
      TipoDocumento tipoDocumento = tiposDocumentoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C", "CSP-ME-E" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de TipoDocumento
    List<TipoDocumento> tiposDocumento = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<TipoDocumento> page = new PageImpl<>(tiposDocumento, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 TipoDocumento
    List<TipoDocumento> tiposDocumento = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposDocumento.add(generarMockTipoDocumento(i, "TipoDocumento" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(tipoDocumentoService.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposDocumento.size() ? tiposDocumento.size() : toIndex;
            List<TipoDocumento> content = tiposDocumento.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tiposDocumento.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los TipoDocumento del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<TipoDocumento> tiposDocumentoResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoDocumento>>() {
        });

    for (int i = 31; i <= 37; i++) {
      TipoDocumento tipoDocumento = tiposDocumentoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de TipoDocumento
    List<TipoDocumento> tiposDocumento = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(tipoDocumentoService.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<TipoDocumento> page = new PageImpl<>(tiposDocumento, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_ReturnsTipoDocumento() throws Exception {
    // given: Un TipoDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(tipoDocumentoService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoDocumento(idBuscado)));

    // when: Buscamos el TipoDocumento por su id
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        // then: Devuelve TipoDocumento
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithIdNotExist_Returns404() throws Exception {
    // given: Ningun TipoDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(tipoDocumentoService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoDocumentoNotFoundException(invocation.getArgument(0));
    });

    // when: Buscamos el TipoDocumento por su id
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 404
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {
    return generarMockTipoDocumento(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setDescripcion("descripcion-" + id);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

}