package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TareaFormacion;
import org.crue.hercules.sgi.eti.model.TareaNombre;
import org.crue.hercules.sgi.eti.model.TareaOrganismo;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.service.TareaService;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * TareaControllerTest
 */
@WebMvcTest(TareaController.class)
public class TareaControllerTest extends BaseControllerTest {

  @MockBean
  private TareaService tareaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TAREA_CONTROLLER_BASE_PATH = "/tareas";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TAREA-VER" })
  public void getTarea_WithId_ReturnsTarea() throws Exception {
    BDDMockito.given(tareaService.findById(ArgumentMatchers.anyLong())).willReturn((generarMockTarea(1L, "Tarea1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value").value("Tarea1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TAREA-VER" })
  public void getTarea_NotFound_Returns404() throws Exception {
    BDDMockito.given(tareaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TareaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TAREA-EDITAR" })
  public void replaceTarea_ReturnsTarea() throws Exception {
    // given: Una tarea a modificar
    String replaceTareaJson = "{\"id\": 1, \"nombre\": [{\"lang\": \"es\", \"value\": \"Tarea1 actualizada\"}], \"equipoTrabajo\": {\"id\": 100}, \"memoria\": {\"id\": 200}, \"formacion\": [{\"lang\": \"es\", \"value\": \"Formacion1\"}], \"formacionEspecifica\": {\"id\": 300}, \"organismo\": [{\"lang\": \"es\", \"value\": \"Organismo1\"}], \"anio\": 2020}";

    Tarea tareaActualizada = generarMockTarea(1L, "Tarea1 actualizada");

    BDDMockito.given(tareaService.update(ArgumentMatchers.<Tarea>any())).willReturn(tareaActualizada);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica la tarea y la devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value").value("Tarea1 actualizada"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TAREA-EDITAR" })
  public void replaceTarea_NotFound() throws Exception {
    // given: Una tarea a modificar
    String replaceTareaJson = "{\"id\": 1, \"nombre\": [{\"lang\": \"es\", \"value\": \"Tarea1 actualizada\"}], \"equipoTrabajo\": {\"id\": 100}, \"memoria\": {\"id\": 200}, \"formacion\": [{\"lang\": \"es\", \"value\": \"Formacion1\"}], \"formacionEspecifica\": {\"id\": 300}, \"organismo\": [{\"lang\": \"es\", \"value\": \"Organismo1\"}], \"anio\": 2020}";

    BDDMockito.given(tareaService.update(ArgumentMatchers.<Tarea>any())).will((InvocationOnMock invocation) -> {
      throw new TareaNotFoundException(((Tarea) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TAREA-VER" })
  public void findAll_Unlimited_ReturnsFullTareaList() throws Exception {
    // given: One hundred tareas
    List<Tarea> tareas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tareas.add(generarMockTarea(Long.valueOf(i), "Tarea" + String.format("%03d", i)));
    }

    BDDMockito.given(tareaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tareas));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred tareas
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TAREA-VER" })
  public void findAll_WithPaging_ReturnsTareaSubList() throws Exception {
    // given: One hundred tareas
    List<Tarea> tareas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tareas.add(generarMockTarea(Long.valueOf(i), "Tarea" + String.format("%03d", i)));
    }

    BDDMockito.given(tareaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Tarea>>() {
          @Override
          public Page<Tarea> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Tarea> content = tareas.subList(fromIndex, toIndex);
            Page<Tarea> page = new PageImpl<>(content, pageable, tareas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(TAREA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked tareas are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Tarea> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Tarea>>() {
        });

    // containing tarea='Tarea031' to 'Tarea040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Tarea tarea = actual.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tarea.getNombre(), Language.ES))
          .isEqualTo("Tarea" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TAREA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTareaList() throws Exception {
    // given: One hundred tareas and a search query
    List<Tarea> tareas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tareas.add(generarMockTarea(Long.valueOf(i), "Tarea" + String.format("%03d", i)));
    }
    String query = "tarea~Tarea%,id:5";

    BDDMockito.given(tareaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Tarea>>() {
          @Override
          public Page<Tarea> answer(InvocationOnMock invocation) throws Throwable {
            List<Tarea> content = new ArrayList<>();
            for (Tarea tarea : tareas) {
              if (I18nHelper.getValueForLanguage(tarea.getNombre(), Language.ES).startsWith("Tarea")
                  && tarea.getId().equals(5L)) {
                content.add(tarea);
              }
            }
            Page<Tarea> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one tarea
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TAREA-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: Tareas empty
    List<Tarea> tareas = new ArrayList<>();

    BDDMockito.given(tareaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tareas));
    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TAREA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get error No Content.
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto Tarea
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto Tarea
   */
  private Tarea generarMockTarea(Long id, String descripcion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(100L);

    Memoria memoria = new Memoria();
    memoria.setId(200L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(300L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);

    Set<TareaNombre> nombre = new HashSet<>();
    nombre.add(new TareaNombre(Language.ES, descripcion));
    Set<TareaFormacion> formacion = new HashSet<>();
    formacion.add(new TareaFormacion(Language.ES, "Formacion" + id));
    Set<TareaOrganismo> organismo = new HashSet<>();
    organismo.add(new TareaOrganismo(Language.ES, "Organismo" + id));

    Tarea tarea = new Tarea();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setNombre(nombre);
    tarea.setFormacion(formacion);
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo(organismo);
    tarea.setAnio(2020);
    tarea.setTipoTarea(tipoTarea);

    return tarea;
  }

}
