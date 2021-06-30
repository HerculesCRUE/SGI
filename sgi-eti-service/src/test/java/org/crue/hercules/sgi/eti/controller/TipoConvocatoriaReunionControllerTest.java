package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.service.TipoConvocatoriaReunionService;
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
 * TipoConvocatoriaReunionControllerTest
 */
@WebMvcTest(TipoConvocatoriaReunionController.class)
public class TipoConvocatoriaReunionControllerTest extends BaseControllerTest {

  @MockBean
  private TipoConvocatoriaReunionService tipoConvocatoriaReunionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH = "/tipoconvocatoriareuniones";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCONVOCATORIAREUNION-VER" })
  public void getTipoConvocatoriaReunion_WithId_ReturnsTipoConvocatoriaReunion() throws Exception {
    BDDMockito.given(tipoConvocatoriaReunionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoConvocatoriaReunion(1L, "TipoConvocatoriaReunion1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoConvocatoriaReunion1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCONVOCATORIAREUNION-VER" })
  public void getTipoConvocatoriaReunion_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoConvocatoriaReunionService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoConvocatoriaReunionNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCONVOCATORIAREUNION-EDITAR" })
  public void newTipoConvocatoriaReunion_ReturnsTipoConvocatoriaReunion() throws Exception {
    // given: Un TipoConvocatoriaReunion nuevo
    String nuevoTipoConvocatoriaReunionJson = "{\"nombre\": \"TipoConvocatoriaReunion1\", \"activo\": \"true\"}";

    TipoConvocatoriaReunion tipoConvocatoriaReunion = generarMockTipoConvocatoriaReunion(1L,
        "TipoConvocatoriaReunion1");

    BDDMockito.given(tipoConvocatoriaReunionService.create(ArgumentMatchers.<TipoConvocatoriaReunion>any()))
        .willReturn(tipoConvocatoriaReunion);

    // when: Creamos un TipoConvocatoriaReunion
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoConvocatoriaReunionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo TipoConvocatoriaReunion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoConvocatoriaReunion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCONVOCATORIAREUNION-EDITAR" })
  public void newTipoConvocatoriaReunion_Error_Returns400() throws Exception {
    // given: Un TipoConvocatoriaReunion nuevo que produce un error al crearse
    String nuevoTipoConvocatoriaReunionJson = "{\"nombre\": \"TipoConvocatoriaReunion1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoConvocatoriaReunionService.create(ArgumentMatchers.<TipoConvocatoriaReunion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un TipoConvocatoriaReunion
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoConvocatoriaReunionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCONVOCATORIAREUNION-EDITAR" })
  public void replaceTipoConvocatoriaReunion_ReturnsTipoConvocatoriaReunion() throws Exception {
    // given: Un TipoConvocatoriaReunion a modificar
    String replaceTipoConvocatoriaReunionJson = "{\"id\": 1, \"nombre\": \"TipoConvocatoriaReunion1\", \"activo\": \"true\"}";

    TipoConvocatoriaReunion tipoConvocatoriaReunion = generarMockTipoConvocatoriaReunion(1L,
        "Replace TipoConvocatoriaReunion1");

    BDDMockito.given(tipoConvocatoriaReunionService.update(ArgumentMatchers.<TipoConvocatoriaReunion>any()))
        .willReturn(tipoConvocatoriaReunion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoConvocatoriaReunionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el TipoConvocatoriaReunion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoConvocatoriaReunion1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCONVOCATORIAREUNION-EDITAR" })
  public void replaceTipoConvocatoriaReunion_NotFound() throws Exception {
    // given: Un TipoConvocatoriaReunion a modificar
    String replaceTipoConvocatoriaReunionJson = "{\"id\": 1, \"nombre\": \"TipoConvocatoriaReunion1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoConvocatoriaReunionService.update(ArgumentMatchers.<TipoConvocatoriaReunion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoConvocatoriaReunionNotFoundException(
              ((TipoConvocatoriaReunion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoConvocatoriaReunionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOCONVOCATORIAREUNION-EDITAR" })
  public void removeTipoConvocatoriaReunion_ReturnsOk() throws Exception {
    BDDMockito.given(tipoConvocatoriaReunionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoConvocatoriaReunion(1L, "TipoConvocatoriaReunion1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-V" })
  public void findAll_Unlimited_ReturnsFullTipoConvocatoriaReunionList() throws Exception {
    // given: One hundred TipoConvocatoriaReunion
    List<TipoConvocatoriaReunion> tipoConvocatoriaReuniones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoConvocatoriaReuniones.add(
          generarMockTipoConvocatoriaReunion(Long.valueOf(i), "TipoConvocatoriaReunion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(tipoConvocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoConvocatoriaReuniones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoConvocatoriaReunion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-V" })
  public void findAll_Unlimited_ReturnsNoContent() throws Exception {
    // given: TipoConvocatoriaReunion empty
    List<TipoConvocatoriaReunion> tipoConvocatoriaReuniones = new ArrayList<>();

    BDDMockito
        .given(tipoConvocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoConvocatoriaReuniones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-V" })
  public void findAll_WithPaging_ReturnsTipoConvocatoriaReunionSubList() throws Exception {
    // given: One hundred TipoConvocatoriaReunion
    List<TipoConvocatoriaReunion> tipoConvocatoriaReuniones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoConvocatoriaReuniones.add(
          generarMockTipoConvocatoriaReunion(Long.valueOf(i), "TipoConvocatoriaReunion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(tipoConvocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoConvocatoriaReunion>>() {
          @Override
          public Page<TipoConvocatoriaReunion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoConvocatoriaReunion> content = tipoConvocatoriaReuniones.subList(fromIndex, toIndex);
            Page<TipoConvocatoriaReunion> page = new PageImpl<>(content, pageable, tipoConvocatoriaReuniones.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoConvocatoriaReuniones are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoConvocatoriaReunion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoConvocatoriaReunion>>() {
        });

    // containing nombre='TipoConvocatoriaReunion031' to
    // 'TipoConvocatoriaReunion040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoConvocatoriaReunion tipoConvocatoriaReunion = actual.get(i);
      Assertions.assertThat(tipoConvocatoriaReunion.getNombre())
          .isEqualTo("TipoConvocatoriaReunion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoConvocatoriaReunionList() throws Exception {
    // given: One hundred TipoConvocatoriaReunion and a search query
    List<TipoConvocatoriaReunion> tipoConvocatoriaReuniones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoConvocatoriaReuniones.add(
          generarMockTipoConvocatoriaReunion(Long.valueOf(i), "TipoConvocatoriaReunion" + String.format("%03d", i)));
    }
    String query = "nombre~TipoConvocatoriaReunion%,id:5";

    BDDMockito
        .given(tipoConvocatoriaReunionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoConvocatoriaReunion>>() {
          @Override
          public Page<TipoConvocatoriaReunion> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoConvocatoriaReunion> content = new ArrayList<>();
            for (TipoConvocatoriaReunion tipoConvocatoriaReunion : tipoConvocatoriaReuniones) {
              if (tipoConvocatoriaReunion.getNombre().startsWith("TipoConvocatoriaReunion")
                  && tipoConvocatoriaReunion.getId().equals(5L)) {
                content.add(tipoConvocatoriaReunion);
              }
            }
            Page<TipoConvocatoriaReunion> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoConvocatoriaReunion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto TipoConvocatoriaReunion
   * 
   * @param id     id del TipoConvocatoriaReunion
   * @param nombre la descripción de TipoConvocatoriaReunion
   * @return el objeto TipoConvocatoriaReunion
   */

  public TipoConvocatoriaReunion generarMockTipoConvocatoriaReunion(Long id, String nombre) {

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion();
    tipoConvocatoriaReunion.setId(id);
    tipoConvocatoriaReunion.setNombre(nombre);
    tipoConvocatoriaReunion.setActivo(Boolean.TRUE);

    return tipoConvocatoriaReunion;
  }

}
