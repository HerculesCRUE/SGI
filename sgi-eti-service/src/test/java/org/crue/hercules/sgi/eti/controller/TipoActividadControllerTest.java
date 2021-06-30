package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoActividadNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.service.TipoActividadService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
 * TipoActividadControllerTest
 */
@WebMvcTest(TipoActividadController.class)
public class TipoActividadControllerTest extends BaseControllerTest {

  @MockBean
  private TipoActividadService tipoActividadService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH = "/tipoactividades";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOACTIVIDAD-VER" })
  public void getTipoActividad_WithId_ReturnsTipoActividad() throws Exception {
    BDDMockito.given(tipoActividadService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoActividad(1L, "TipoActividad1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoActividad1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOACTIVIDAD-VER" })
  public void getTipoActividad_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoActividadService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoActividadNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOACTIVIDAD-EDITAR" })
  public void newTipoActividad_ReturnsTipoActividad() throws Exception {
    // given: Un tipo actividad nuevo
    String nuevoTipoActividadJson = "{\"nombre\": \"TipoActividad1\", \"activo\": \"true\"}";

    TipoActividad tipoActividad = generarMockTipoActividad(1L, "TipoActividad1");

    BDDMockito.given(tipoActividadService.create(ArgumentMatchers.<TipoActividad>any())).willReturn(tipoActividad);

    // when: Creamos un tipo actividad
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoActividadJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo actividad y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoActividad1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOACTIVIDAD-EDITAR" })
  public void newTipoActividad_Error_Returns400() throws Exception {
    // given: Un tipo actividad nuevo que produce un error al crearse
    String nuevoTipoActividadJson = "{\"nombre\": \"TipoActividad1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoActividadService.create(ArgumentMatchers.<TipoActividad>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un tipo actividad
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoActividadJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOACTIVIDAD-EDITAR" })
  public void replaceTipoActividad_ReturnsTipoActividad() throws Exception {
    // given: Un tipo actividad a modificar
    String replaceTipoActividadJson = "{\"id\": 1, \"nombre\": \"TipoActividad1\", \"activo\": \"true\"}";

    TipoActividad tipoActividad = generarMockTipoActividad(1L, "Replace TipoActividad1");

    BDDMockito.given(tipoActividadService.update(ArgumentMatchers.<TipoActividad>any())).willReturn(tipoActividad);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoActividadJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el tipo actividad y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoActividad1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOACTIVIDAD-EDITAR" })
  public void replaceTipoActividad_NotFound() throws Exception {
    // given: Un tipo actividad a modificar
    String replaceTipoActividadJson = "{\"id\": 1, \"nombre\": \"TipoActividad1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoActividadService.update(ArgumentMatchers.<TipoActividad>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoActividadNotFoundException(((TipoActividad) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoActividadJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOACTIVIDAD-EDITAR" })
  public void removeTipoActividad_ReturnsOk() throws Exception {
    BDDMockito.given(tipoActividadService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoActividad(1L, "TipoActividad1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findAll_Unlimited_ReturnsFullTipoActividadList() throws Exception {
    // given: One hundred TipoActividad
    List<TipoActividad> tipoActividades = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoActividades.add(generarMockTipoActividad(Long.valueOf(i), "TipoActividad" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoActividadService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoActividades));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoActividad
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findAll_WithPaging_ReturnsTipoActividadSubList() throws Exception {
    // given: One hundred TipoActividad
    List<TipoActividad> tipoActividades = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoActividades.add(generarMockTipoActividad(Long.valueOf(i), "TipoActividad" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoActividadService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoActividad>>() {
          @Override
          public Page<TipoActividad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoActividad> content = tipoActividades.subList(fromIndex, toIndex);
            Page<TipoActividad> page = new PageImpl<>(content, pageable, tipoActividades.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoActividads are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoActividad> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoActividad>>() {
        });

    // containing nombre='TipoActividad031' to 'TipoActividad040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoActividad tipoActividad = actual.get(i);
      Assertions.assertThat(tipoActividad.getNombre()).isEqualTo("TipoActividad" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoActividadList() throws Exception {
    // given: One hundred TipoActividad and a search query
    List<TipoActividad> tipoActividades = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoActividades.add(generarMockTipoActividad(Long.valueOf(i), "TipoActividad" + String.format("%03d", i)));
    }
    String query = "nombre~TipoActividad%,id:5";

    BDDMockito.given(tipoActividadService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoActividad>>() {
          @Override
          public Page<TipoActividad> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoActividad> content = new ArrayList<>();
            for (TipoActividad tipoActividad : tipoActividades) {
              if (tipoActividad.getNombre().startsWith("TipoActividad") && tipoActividad.getId().equals(5L)) {
                content.add(tipoActividad);
              }
            }
            Page<TipoActividad> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoActividad
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoActividad empty
    List<TipoActividad> tipoActividades = new ArrayList<>();

    BDDMockito.given(tipoActividadService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoActividades));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @param id     id del tipoActividad
   * @param nombre la descripción del tipo de actividad
   * @return el objeto tipo actividad
   */

  public TipoActividad generarMockTipoActividad(Long id, String nombre) {

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(id);
    tipoActividad.setNombre(nombre);
    tipoActividad.setActivo(Boolean.TRUE);

    return tipoActividad;
  }

}
