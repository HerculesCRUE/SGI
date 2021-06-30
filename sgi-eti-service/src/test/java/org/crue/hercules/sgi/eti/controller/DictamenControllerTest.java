package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.DictamenNotFoundException;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.service.DictamenService;
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
 * DictamenControllerTest x
 */
@WebMvcTest(DictamenController.class)
public class DictamenControllerTest extends BaseControllerTest {

  @MockBean
  private DictamenService dictamenService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String DICTAMEN_CONTROLLER_BASE_PATH = "/dictamenes";
  private static final String DICTAMEN_MEMORIA_REVISION_MINIMA_PATH = "/memoria-revision-minima";
  private static final String DICTAMEN_MEMORIA_NO_REVISION_MINIMA_PATH = "/memoria-no-revision-minima";
  private static final String DICTAMEN_RETROSPECTIVA_PATH = "/retrospectiva";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void getDictamen_WithId_ReturnsDictamen() throws Exception {
    BDDMockito.given(dictamenService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockDictamen(1L, "Dictamen1")));

    mockMvc.perform(MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Dictamen1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEvaluacion.nombre").value("TipoEvaluacion1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void getDictamen_NotFound_Returns404() throws Exception {
    BDDMockito.given(dictamenService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new DictamenNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-EDITAR" })
  public void newDictamen_ReturnsDictamen() throws Exception {
    // given: Un dictamen nuevo
    String nuevoDictamenJson = "{\"nombre\": \"Dictamen1\", \"tipoEvaluacion\": {\"id\": \"1\", \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"activo\": \"true\"}";

    Dictamen dictamen = generarMockDictamen(1L, "Dictamen1");

    BDDMockito.given(dictamenService.create(ArgumentMatchers.<Dictamen>any())).willReturn(dictamen);

    // when: Creamos un dictamen
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(DICTAMEN_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoDictamenJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo dictamen y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Dictamen1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEvaluacion.nombre").value("TipoEvaluacion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-EDITAR" })
  public void newDictamen_Error_Returns400() throws Exception {
    // given: Un dictamen nuevo que produce un error al crearse
    String nuevoDictamenJson = "{\"nombre\": \"Dictamen1\", \"activo\": \"true\"}";

    BDDMockito.given(dictamenService.create(ArgumentMatchers.<Dictamen>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un dictamen
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(DICTAMEN_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoDictamenJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-EDITAR" })
  public void replaceDictamen_ReturnsDictamen() throws Exception {
    // given: Un dictamen a modificar
    String replaceDictamenJson = "{\"id\": 1, \"nombre\": \"Dictamen1\", \"tipoEvaluacion\": {\"id\": \"1\", \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"activo\": \"true\"}";

    Dictamen dictamen = generarMockDictamen(1L, "Replace Dictamen1");

    BDDMockito.given(dictamenService.update(ArgumentMatchers.<Dictamen>any())).willReturn(dictamen);

    mockMvc
        .perform(MockMvcRequestBuilders.put(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceDictamenJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el dictamen y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace Dictamen1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-EDITAR" })
  public void replaceDictamen_NotFound() throws Exception {
    // given: Un dictamen a modificar
    String replaceDictamenJson = "{\"id\": 1, \"nombre\": \"Dictamen1\", \"tipoEvaluacion\": {\"id\": \"1\", \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"activo\": \"true\"}";

    BDDMockito.given(dictamenService.update(ArgumentMatchers.<Dictamen>any())).will((InvocationOnMock invocation) -> {
      throw new DictamenNotFoundException(((Dictamen) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceDictamenJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-EDITAR" })
  public void removeDictamen_ReturnsOk() throws Exception {
    BDDMockito.given(dictamenService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockDictamen(1L, "Dictamen1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void findAll_Unlimited_ReturnsFullDictamenList() throws Exception {
    // given: One hundred Dictamen
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(dictamenService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(dictamenes));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Dictamen
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void findAll_ReturnsNotContent() throws Exception {
    // given: Dictamen empty
    List<Dictamen> dictamenes = new ArrayList<>();

    BDDMockito.given(dictamenService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(dictamenes));

    mockMvc
        .perform(MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void findAll_WithPaging_ReturnsDictamenSubList() throws Exception {
    // given: One hundred Dictamen
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(dictamenService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Dictamen>>() {
          @Override
          public Page<Dictamen> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Dictamen> content = dictamenes.subList(fromIndex, toIndex);
            Page<Dictamen> page = new PageImpl<>(content, pageable, dictamenes.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Dictamens are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Dictamen> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Dictamen>>() {
        });

    // containing nombre='Dictamen031' to 'Dictamen040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Dictamen dictamen = actual.get(i);
      Assertions.assertThat(dictamen.getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredDictamenList() throws Exception {
    // given: One hundred Dictamen and a search query
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }
    String query = "nombre~Dictamen%,id:5";

    BDDMockito.given(dictamenService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Dictamen>>() {
          @Override
          public Page<Dictamen> answer(InvocationOnMock invocation) throws Throwable {
            List<Dictamen> content = new ArrayList<>();
            for (Dictamen dictamen : dictamenes) {
              if (dictamen.getNombre().startsWith("Dictamen") && dictamen.getId().equals(5L)) {
                content.add(dictamen);
              }
            }
            Page<Dictamen> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Dictamen
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void findAllByMemoriaRevisionMinima_ReturnsDictamenList() throws Exception {
    // given: One hundred Dictamen
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(dictamenService.findAllByMemoriaRevisionMinima()).willReturn(dictamenes);

    // when: find unlimited
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH + DICTAMEN_MEMORIA_REVISION_MINIMA_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Dictamen
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Dictamen> actual = mapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<List<Dictamen>>() {
        });

    // containing tipoEvaluacion.nombre = "TipoEvaluacion1"
    for (int i = 0; i <= 99; i++) {
      Dictamen dictamen = actual.get(i);
      Assertions.assertThat(dictamen.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
      Assertions.assertThat(dictamen.getId()).isEqualTo(i + 1);

    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void findAllByMemoriaNoRevisionMinima_ReturnsDictamenList() throws Exception {
    // given: One hundred Dictamen
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(dictamenService.findAllByMemoriaNoRevisionMinima()).willReturn(dictamenes);

    // when: find unlimited
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH + DICTAMEN_MEMORIA_NO_REVISION_MINIMA_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Dictamen
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Dictamen> actual = mapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<List<Dictamen>>() {
        });

    // containing tipoEvaluacion.nombre = "TipoEvaluacion1"
    for (int i = 0; i <= 99; i++) {
      Dictamen dictamen = actual.get(i);
      Assertions.assertThat(dictamen.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
      Assertions.assertThat(dictamen.getId()).isEqualTo(i + 1);

    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-DICTAMEN-VER" })
  public void findAllByRetrospectiva_ReturnsDictamenList() throws Exception {
    // given: One hundred Dictamen
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(dictamenService.findAllByRetrospectiva()).willReturn(dictamenes);

    // when: find unlimited
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(DICTAMEN_CONTROLLER_BASE_PATH + DICTAMEN_RETROSPECTIVA_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Dictamen
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Dictamen> actual = mapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<List<Dictamen>>() {
        });

    // containing tipoEvaluacion.nombre = "TipoEvaluacion1"
    for (int i = 0; i <= 99; i++) {
      Dictamen dictamen = actual.get(i);
      Assertions.assertThat(dictamen.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
      Assertions.assertThat(dictamen.getId()).isEqualTo(i + 1);

    }
  }

  /**
   * Función que devuelve un objeto Dictamen
   * 
   * @param id     id del dictamen
   * @param nombre la descripción del dictamen
   * @return el objeto dictamen
   */

  public Dictamen generarMockDictamen(Long id, String nombre) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre(nombre);
    dictamen.setTipoEvaluacion(tipoEvaluacion);
    dictamen.setActivo(Boolean.TRUE);

    return dictamen;
  }

}
