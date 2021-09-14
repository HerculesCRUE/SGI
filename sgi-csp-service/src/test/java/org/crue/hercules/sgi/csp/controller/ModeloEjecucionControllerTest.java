package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.ModeloEjecucionService;
import org.crue.hercules.sgi.csp.service.ModeloTipoDocumentoService;
import org.crue.hercules.sgi.csp.service.ModeloTipoEnlaceService;
import org.crue.hercules.sgi.csp.service.ModeloTipoFaseService;
import org.crue.hercules.sgi.csp.service.ModeloTipoFinalidadService;
import org.crue.hercules.sgi.csp.service.ModeloTipoHitoService;
import org.crue.hercules.sgi.csp.service.ModeloUnidadService;
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
 * ModeloEjecucionControllerTest
 */
@WebMvcTest(ModeloEjecucionController.class)
public class ModeloEjecucionControllerTest extends BaseControllerTest {

  @MockBean
  private ModeloEjecucionService modeloEjecucionService;

  @MockBean
  private ModeloTipoEnlaceService modeloTipoEnlaceService;

  @MockBean
  private ModeloTipoFaseService modeloTipoFaseService;

  @MockBean
  private ModeloTipoDocumentoService modeloTipoDocumentoService;

  @MockBean
  private ModeloTipoFinalidadService modeloTipoFinalidadService;

  @MockBean
  private ModeloTipoHitoService modeloTipoHitoService;

  @MockBean
  private ModeloUnidadService modeloUnidadService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String MODELO_EJECUCION_CONTROLLER_BASE_PATH = "/modeloejecuciones";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_ReturnsModeloEjecucion() throws Exception {
    // given: Un ModeloEjecucion nuevo
    String modeloEjecucionJson = "{ \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";

    BDDMockito.given(modeloEjecucionService.create(ArgumentMatchers.<ModeloEjecucion>any()))
        .will((InvocationOnMock invocation) -> {
          ModeloEjecucion modeloEjecucionCreado = invocation.getArgument(0);
          modeloEjecucionCreado.setId(1L);
          if (modeloEjecucionCreado.getActivo() == null) {
            modeloEjecucionCreado.setActivo(true);
          }
          return modeloEjecucionCreado;
        });

    // when: Creamos un ModeloEjecucion
    mockMvc
        .perform(MockMvcRequestBuilders.post(MODELO_EJECUCION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(modeloEjecucionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo ModeloEjecucion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: Un ModeloEjecucion que produce un error al crearse porque ya tiene id
    String modeloEjecucionJson = "{ \"id\": \"1\", \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";

    BDDMockito.given(modeloEjecucionService.create(ArgumentMatchers.<ModeloEjecucion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un ModeloEjecucion
    mockMvc
        .perform(MockMvcRequestBuilders.post(MODELO_EJECUCION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(modeloEjecucionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsModeloEjecucion() throws Exception {
    // given: Un ModeloEjecucion a modificar
    String modeloEjecucionJson = "{\"id\": \"1\", \"nombre\": \"nombre-1-modificado\", \"descripcion\": \"descripcion-1\", \"activo\": true }";
    ModeloEjecucion modeloEjecucionSinModificar = generarMockModeloEjecucion(1L, "nombre-1");

    BDDMockito.given(modeloEjecucionService.findById(ArgumentMatchers.<Long>any()))
        .willReturn(modeloEjecucionSinModificar);
    BDDMockito.given(modeloEjecucionService.update(ArgumentMatchers.<ModeloEjecucion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ModeloEjecucion
    mockMvc
        .perform(MockMvcRequestBuilders.put(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(modeloEjecucionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el ModeloEjecucion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1-modificado"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithIdNotExist_Returns404() throws Exception {
    // given: Un ModeloEjecucion a modificar
    String modeloEjecucionJson = "{\"id\": \"1\", \"nombre\": \"nombre-1-modificado\", \"descripcion\": \"descripcion-1\", \"activo\": true }";

    BDDMockito.given(modeloEjecucionService.update(ArgumentMatchers.<ModeloEjecucion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ModeloEjecucionNotFoundException(((ModeloEjecucion) invocation.getArgument(0)).getId());
        });

    // when: Actualizamos el ModeloEjecucion
    mockMvc
        .perform(MockMvcRequestBuilders.put(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(modeloEjecucionJson))
        // then: No encuentra el ModeloEjecucion y devuelve un 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void update_WithNombreRepetido_Returns400() throws Exception {
    // given: Un ModeloEjecucion que produce un error porque ya existe otro con el
    // mismo nombre
    String modeloEjecucionJson = "{ \"id\": \"2\", \"nombre\": \"nombre-1\", \"descripcion\": \"descripcion-1\" }";

    BDDMockito.given(modeloEjecucionService.update(ArgumentMatchers.<ModeloEjecucion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Actualizamos el ModeloEjecucion
    mockMvc
        .perform(MockMvcRequestBuilders.put(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(modeloEjecucionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-R" })
  public void reactivar_WithExistingId_ReturnModeloEjecucion() throws Exception {
    // given: existing id
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);
    modeloEjecucion.setActivo(Boolean.FALSE);
    BDDMockito.given(modeloEjecucionService.enable(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ModeloEjecucion modeloEjecucionEnabled = new ModeloEjecucion();
          BeanUtils.copyProperties(modeloEjecucion, modeloEjecucionEnabled);
          modeloEjecucionEnabled.setActivo(Boolean.TRUE);
          return modeloEjecucionEnabled;
        });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR,
                modeloEjecucion.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return enabled ModeloEjecucion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(modeloEjecucion.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(modeloEjecucion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(modeloEjecucion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-R" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ModeloEjecucionNotFoundException(id)).given(modeloEjecucionService)
        .enable(ArgumentMatchers.<Long>any());

    // when: enable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B", "CSP-PRO-V" })
  public void desactivar_WithExistingId_ReturnModeloEjecucion() throws Exception {
    // given: existing id
    Long idBuscado = 1L;
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(idBuscado);

    BDDMockito.given(modeloEjecucionService.disable(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ModeloEjecucion modeloEjecucionDisabled = new ModeloEjecucion();
          BeanUtils.copyProperties(modeloEjecucion, modeloEjecucionDisabled);
          modeloEjecucionDisabled.setActivo(false);
          return modeloEjecucionDisabled;
        });

    // when: disable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: return disabled ModeloEjecucion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(idBuscado))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(modeloEjecucion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(modeloEjecucion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;
    BDDMockito.willThrow(new ModeloEjecucionNotFoundException(id)).given(modeloEjecucionService)
        .disable(ArgumentMatchers.<Long>any());

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-MOD-V" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloEjecucion
    List<ModeloEjecucion> modelosEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modelosEjecucion.add(generarMockModeloEjecucion(i, "ModeloEjecucion" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(modeloEjecucionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloEjecucion>>() {
          @Override
          public Page<ModeloEjecucion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modelosEjecucion.size() ? modelosEjecucion.size() : toIndex;
            List<ModeloEjecucion> content = modelosEjecucion.subList(fromIndex, toIndex);
            Page<ModeloEjecucion> page = new PageImpl<>(content, pageable, modelosEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(MODELO_EJECUCION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloEjecucion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloEjecucion> modelosEjecucionResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloEjecucion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloEjecucion modeloEjecucion = modelosEjecucionResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloEjecucion.getNombre()).isEqualTo("ModeloEjecucion" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-MOD-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloEjecucion
    List<ModeloEjecucion> modelosEjecucion = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(modeloEjecucionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloEjecucion>>() {
          @Override
          public Page<ModeloEjecucion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<ModeloEjecucion> page = new PageImpl<>(modelosEjecucion, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(MODELO_EJECUCION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V", "CSP-ME-C", "CSP-ME-E", "CSP-ME-B", "CSP-ME-R" })
  public void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloEjecucion
    List<ModeloEjecucion> modelosEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modelosEjecucion.add(generarMockModeloEjecucion(i, "ModeloEjecucion" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloEjecucionService.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloEjecucion>>() {
          @Override
          public Page<ModeloEjecucion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modelosEjecucion.size() ? modelosEjecucion.size() : toIndex;
            List<ModeloEjecucion> content = modelosEjecucion.subList(fromIndex, toIndex);
            Page<ModeloEjecucion> page = new PageImpl<>(content, pageable, modelosEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloEjecucion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloEjecucion> modelosEjecucionResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloEjecucion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloEjecucion modeloEjecucion = modelosEjecucionResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloEjecucion.getNombre()).isEqualTo("ModeloEjecucion" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V", "CSP-ME-C", "CSP-ME-E", "CSP-ME-B", "CSP-ME-R" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloEjecucion
    List<ModeloEjecucion> modelosEjecucion = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloEjecucionService.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloEjecucion>>() {
          @Override
          public Page<ModeloEjecucion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            Page<ModeloEjecucion> page = new PageImpl<>(modelosEjecucion, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void findById_ReturnsModeloEjecucion() throws Exception {
    // given: Un ModeloEjecucion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloEjecucionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockModeloEjecucion(idBuscado)));

    // when: Buscamos el ModeloEjecucion por su id
    mockMvc
        .perform(MockMvcRequestBuilders.get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        // then: Devuelve ModeloEjecucion
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void findById_WithIdNotExist_Returns404() throws Exception {
    // given: Ningun ModeloEjecucion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloEjecucionService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new ModeloEjecucionNotFoundException(invocation.getArgument(0));
        });

    // when: Buscamos el ModeloEjecucion por su id
    mockMvc
        .perform(MockMvcRequestBuilders.get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 404
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * 
   * MODELO TIPO ENLACE
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoEnlaces_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoEnlace para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoEnlace> modeloTipoEnlaces = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoEnlaces.add(generarMockModeloTipoEnlace(i, "TipoEnlace" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoEnlaceService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoEnlace>>() {
          @Override
          public Page<ModeloTipoEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoEnlaces.size() ? modeloTipoEnlaces.size() : toIndex;
            List<ModeloTipoEnlace> content = modeloTipoEnlaces.subList(fromIndex, toIndex);
            Page<ModeloTipoEnlace> page = new PageImpl<>(content, pageable, modeloTipoEnlaces.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipoenlaces", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los TipoEnlace del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoEnlace> modeloTipoEnlacesResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ModeloTipoEnlace>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoEnlace modeloTipoEnlace = modeloTipoEnlacesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoEnlace.getTipoEnlace().getNombre())
          .isEqualTo("TipoEnlace" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void findAllModeloTipoEnlaces_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoEnlace del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoEnlace> modeloTipoEnlaces = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoEnlaceService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoEnlace>>() {
          @Override
          public Page<ModeloTipoEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoEnlace> page = new PageImpl<>(modeloTipoEnlaces, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipoenlaces", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * MODELO TIPO FASE
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void findAllModeloTipoFases_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoFase para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoFase> modeloTipoFases = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFases.add(generarMockModeloTipoFase(i, "TipoFase" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoFaseService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFases.size() ? modeloTipoFases.size() : toIndex;
            List<ModeloTipoFase> content = modeloTipoFases.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, modeloTipoFases.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoFase> modeloTipoFasesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloTipoFase>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = modeloTipoFasesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoFases_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoFase del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> modeloTipoFases = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoFaseService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoFase> page = new PageImpl<>(modeloTipoFases, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoFasesConvocatoria_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoFase para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoFase> modeloTipoFases = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFases.add(generarMockModeloTipoFase(i, "TipoFase" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoFaseService.findAllByModeloEjecucionActivosConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFases.size() ? modeloTipoFases.size() : toIndex;
            List<ModeloTipoFase> content = modeloTipoFases.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, modeloTipoFases.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases/convocatoria",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoFase> modeloTipoFasesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloTipoFase>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = modeloTipoFasesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoFasesConvocatoria_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoFase del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> modeloTipoFases = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoFaseService.findAllByModeloEjecucionActivosConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoFase> page = new PageImpl<>(modeloTipoFases, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases/convocatoria",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllModeloTipoFasesProyecto_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoFase para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoFase> modeloTipoFases = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFases.add(generarMockModeloTipoFase(i, "TipoFase" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoFaseService.findAllByModeloEjecucionActivosProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFases.size() ? modeloTipoFases.size() : toIndex;
            List<ModeloTipoFase> content = modeloTipoFases.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, modeloTipoFases.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases/proyecto",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoFase> modeloTipoFasesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloTipoFase>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = modeloTipoFasesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllModeloTipoFasesProyecto_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoFase del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> modeloTipoFases = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoFaseService.findAllByModeloEjecucionActivosProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoFase> page = new PageImpl<>(modeloTipoFases, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases/proyecto",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * MODELO TIPO FASE DOCUMENTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoDocumentos_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoDocumento para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoDocumento> modeloTipoDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoDocumentos.add(generarMockModeloTipoDocumento(i, "TipoDocumento" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoDocumentoService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoDocumento>>() {
          @Override
          public Page<ModeloTipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoDocumentos.size() ? modeloTipoDocumentos.size() : toIndex;
            List<ModeloTipoDocumento> content = modeloTipoDocumentos.subList(fromIndex, toIndex);
            Page<ModeloTipoDocumento> page = new PageImpl<>(content, pageable, modeloTipoDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipodocumentos", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoDocumento del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoDocumento> tiposDocumentoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ModeloTipoDocumento>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoDocumento modeloTipoDocumento = tiposDocumentoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoDocumento.getTipoDocumento().getNombre())
          .isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoDocumentos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoDocumento del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoDocumento> modeloTipoDocumentos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoDocumentoService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoDocumento>>() {
          @Override
          public Page<ModeloTipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoDocumento> page = new PageImpl<>(modeloTipoDocumentos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipodocumentos", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * MODELO TIPO FINALIDAD
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoFinalidades_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoFinalidad para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoFinalidad> modeloTipoFinalidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFinalidades.add(generarMockModeloTipoFinalidad(i, "TipoFinalidad" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoFinalidadService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFinalidad>>() {
          @Override
          public Page<ModeloTipoFinalidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFinalidades.size() ? modeloTipoFinalidades.size() : toIndex;
            List<ModeloTipoFinalidad> content = modeloTipoFinalidades.subList(fromIndex, toIndex);
            Page<ModeloTipoFinalidad> page = new PageImpl<>(content, pageable, modeloTipoFinalidades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofinalidades",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoFinalidad del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoFinalidad> modeloTipoFinalidadesResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ModeloTipoFinalidad>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoFinalidad modeloTipoFinalidad = modeloTipoFinalidadesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoFinalidad.getTipoFinalidad().getNombre())
          .isEqualTo("TipoFinalidad" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoFinalidades_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoFinalidad del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFinalidad> modeloTipoFinalidades = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoFinalidadService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFinalidad>>() {
          @Override
          public Page<ModeloTipoFinalidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoFinalidad> page = new PageImpl<>(modeloTipoFinalidades, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofinalidades",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * MODELO TIPO HITO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void findAllModeloTipoHitos_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoHito para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarMockModeloTipoHito(i, "TipoHito" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoHitoService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoHito> modeloTipoHitosResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloTipoHito>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = modeloTipoHitosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getNombre()).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void findAllModeloTipoHitos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoHito del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoHitoService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoHito> page = new PageImpl<>(modeloTipoHitos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  public void findAllModeloTipoHitosConvocatoria_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoHito para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarMockModeloTipoHito(i, "TipoHito" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoHitoService.findAllByModeloEjecucionActivosConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/convocatoria",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoHito> modeloTipoHitosResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloTipoHito>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = modeloTipoHitosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getNombre()).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void findAllModeloTipoHitosConvocatoria_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoHito del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoHitoService.findAllByModeloEjecucionActivosConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoHito> page = new PageImpl<>(modeloTipoHitos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/convocatoria",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllModeloTipoHitosProyecto_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoHito para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarMockModeloTipoHito(i, "TipoHito" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoHitoService.findAllByModeloEjecucionActivosProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/proyecto",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoHito> modeloTipoHitosResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloTipoHito>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = modeloTipoHitosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getNombre()).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllModeloTipoHitosProyecto_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoHito del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoHitoService.findAllByModeloEjecucionActivosProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoHito> page = new PageImpl<>(modeloTipoHitos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/proyecto",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllModeloTipoHitosSolicitud_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloTipoHito para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarMockModeloTipoHito(i, "TipoHito" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoHitoService.findAllByModeloEjecucionActivosSolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/solicitud",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloTipoHito> modeloTipoHitosResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloTipoHito>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = modeloTipoHitosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getNombre()).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-V" })
  public void findAllModeloTipoHitosSolicitud_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloTipoHito del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloTipoHitoService.findAllByModeloEjecucionActivosSolicitud(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloTipoHito> page = new PageImpl<>(modeloTipoHitos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/solicitud",
                idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * MODELO UNIDAD
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findAllModeloUnidades_ReturnsPage() throws Exception {
    // given: Una lista con 37 ModeloUnidad para el ModeloEjecucion
    Long idModeloEjecucion = 1L;

    List<ModeloUnidad> unidadesModelo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      unidadesModelo.add(generarMockModeloUnidad(i, "Unidad" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloUnidadService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloUnidad>>() {
          @Override
          public Page<ModeloUnidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > unidadesModelo.size() ? unidadesModelo.size() : toIndex;
            List<ModeloUnidad> content = unidadesModelo.subList(fromIndex, toIndex);
            Page<ModeloUnidad> page = new PageImpl<>(content, pageable, unidadesModelo.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelounidades", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ModeloUnidad del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ModeloUnidad> unidadesModeloResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ModeloUnidad>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ModeloUnidad modeloUnidad = unidadesModeloResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(modeloUnidad.getUnidadGestionRef()).isEqualTo("Unidad" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findAllModeloUnidades_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ModeloUnidad del ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloUnidad> unidadesModelo = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(modeloUnidadService.findAllByModeloEjecucion(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloUnidad>>() {
          @Override
          public Page<ModeloUnidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ModeloUnidad> page = new PageImpl<>(unidadesModelo, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelounidades", idModeloEjecucion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ModeloEjecucion
   * 
   * @param id id del ModeloEjecucion
   * @return el objeto ModeloEjecucion
   */
  private ModeloEjecucion generarMockModeloEjecucion(Long id) {
    return generarMockModeloEjecucion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ModeloEjecucion
   * 
   * @param id     id del ModeloEjecucion
   * @param nombre nombre
   * @return el objeto ModeloEjecucion
   */
  private ModeloEjecucion generarMockModeloEjecucion(Long id, String nombre) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(id);
    modeloEjecucion.setNombre(nombre);
    modeloEjecucion.setDescripcion("descripcion-" + id);
    modeloEjecucion.setActivo(Boolean.TRUE);

    return modeloEjecucion;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase
   * 
   * @param id     id del ModeloTipoFase
   * @param nombre nombre del TipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarMockModeloTipoFase(Long id, String nombre) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombre);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(Boolean.TRUE);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setModeloEjecucion(modeloEjecucion);
    modeloTipoFase.setTipoFase(tipoFase);
    modeloTipoFase.setConvocatoria(true);
    modeloTipoFase.setProyecto(true);
    modeloTipoFase.setSolicitud(false);
    modeloTipoFase.setActivo(true);

    return modeloTipoFase;
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento
   * 
   * @param id     id del ModeloTipoDocumento
   * @param nombre nombre del TipoDocumento
   * @return el objeto ModeloTipoDocumento
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(Long id, String nombre) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setDescripcion("descripcion-" + id);
    tipoDocumento.setActivo(Boolean.TRUE);

    ModeloTipoDocumento modeloTipoDocumento = new ModeloTipoDocumento();
    modeloTipoDocumento.setId(id);
    modeloTipoDocumento.setModeloEjecucion(modeloEjecucion);
    modeloTipoDocumento.setTipoDocumento(tipoDocumento);
    modeloTipoDocumento.setModeloTipoFase(null);
    modeloTipoDocumento.setActivo(true);

    return modeloTipoDocumento;
  }

  /**
   * Función que devuelve un objeto ModeloTipoEnlace
   * 
   * @param id     id del ModeloTipoEnlace
   * @param nombre nombre del TipoEnlace
   * @return el objeto ModeloTipoEnlace
   */
  private ModeloTipoEnlace generarMockModeloTipoEnlace(Long id, String nombre) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoEnlace tipoEnlace = new TipoEnlace();
    tipoEnlace.setId(id);
    tipoEnlace.setNombre(nombre);
    tipoEnlace.setDescripcion("descripcion-" + id);
    tipoEnlace.setActivo(Boolean.TRUE);

    ModeloTipoEnlace modeloTipoEnlace = new ModeloTipoEnlace();
    modeloTipoEnlace.setId(id);
    modeloTipoEnlace.setModeloEjecucion(modeloEjecucion);
    modeloTipoEnlace.setTipoEnlace(tipoEnlace);

    return modeloTipoEnlace;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFinalidad
   * 
   * @param id     id del ModeloTipoFinalidad
   * @param nombre nombre del TipoFinalidad
   * @return el objeto ModeloTipoFinalidad
   */
  private ModeloTipoFinalidad generarMockModeloTipoFinalidad(Long id, String nombre) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(id);
    tipoFinalidad.setNombre(nombre);
    tipoFinalidad.setDescripcion("descripcion-" + id);
    tipoFinalidad.setActivo(Boolean.TRUE);

    ModeloTipoFinalidad modeloTipoFinalidad = new ModeloTipoFinalidad();
    modeloTipoFinalidad.setId(id);
    modeloTipoFinalidad.setModeloEjecucion(modeloEjecucion);
    modeloTipoFinalidad.setTipoFinalidad(tipoFinalidad);

    return modeloTipoFinalidad;
  }

  /**
   * Función que devuelve un objeto ModeloTipoHito
   * 
   * @param id     id del ModeloTipoHito
   * @param nombre nombre del TipoHito
   * @return el objeto ModeloTipoHito
   */
  private ModeloTipoHito generarMockModeloTipoHito(Long id, String nombre) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre(nombre);
    tipoHito.setDescripcion("descripcion-" + id);
    tipoHito.setActivo(Boolean.TRUE);

    ModeloTipoHito modeloTipoHito = new ModeloTipoHito();
    modeloTipoHito.setId(id);
    modeloTipoHito.setModeloEjecucion(modeloEjecucion);
    modeloTipoHito.setTipoHito(tipoHito);
    modeloTipoHito.setConvocatoria(true);
    modeloTipoHito.setProyecto(true);
    modeloTipoHito.setSolicitud(true);

    return modeloTipoHito;
  }

  /**
   * Función que devuelve un objeto ModeloUnidad
   * 
   * @param id     id del ModeloUnidad
   * @param nombre nombre
   * @return el objeto ModeloUnidad
   */
  private ModeloUnidad generarMockModeloUnidad(Long id, String nombre) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(id);
    modeloUnidad.setModeloEjecucion(modeloEjecucion);
    modeloUnidad.setUnidadGestionRef(nombre);
    modeloUnidad.setActivo(true);

    return modeloUnidad;
  }

}