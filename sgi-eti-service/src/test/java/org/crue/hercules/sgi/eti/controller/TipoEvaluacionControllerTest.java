package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.service.TipoEvaluacionService;
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
 * TipoEvaluacionControllerTest
 */
@WebMvcTest(TipoEvaluacionController.class)
public class TipoEvaluacionControllerTest extends BaseControllerTest {

  @MockBean
  private TipoEvaluacionService tipoEvaluacionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER = "/{boolean}";
  private static final String TIPO_EVALUACION_CONTROLLER_BASE_PATH = "/tipoevaluaciones";
  private static final String DICTAMENES_REV_MINIMA_PATH = "/dictamenes-revision-minima";
  private static final String MEMORIA_RETROSPECTIVA_PATH = "/memoria-retrospectiva";
  private static final String SEGUIMIENT_ANUAL_FINAL_PATH = "/seguimiento-anual-final";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-VER" })
  public void getTipoEvaluacion_WithId_ReturnsTipoEvaluacion() throws Exception {
    BDDMockito.given(tipoEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoEvaluacion(1L, "TipoEvaluacion1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoEvaluacion1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-VER" })
  public void getTipoEvaluacion_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoEvaluacionService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoEvaluacionNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-EDITAR" })
  public void newTipoEvaluacion_ReturnsTipoEvaluacion() throws Exception {
    // given: Un tipo Evaluacion nuevo
    String nuevoTipoEvaluacionJson = "{\"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}";

    TipoEvaluacion tipoEvaluacion = generarMockTipoEvaluacion(1L, "TipoEvaluacion1");

    BDDMockito.given(tipoEvaluacionService.create(ArgumentMatchers.<TipoEvaluacion>any())).willReturn(tipoEvaluacion);

    // when: Creamos un tipo Evaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo Evaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("TipoEvaluacion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-EDITAR" })
  public void newTipoEvaluacion_Error_Returns400() throws Exception {
    // given: Un tipo Evaluacion nuevo que produce un error al crearse
    String nuevoTipoEvaluacionJson = "{\"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoEvaluacionService.create(ArgumentMatchers.<TipoEvaluacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un tipo Evaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoTipoEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-EDITAR" })
  public void replaceTipoEvaluacion_ReturnsTipoEvaluacion() throws Exception {
    // given: Un tipo Evaluacion a modificar
    String replaceTipoEvaluacionJson = "{\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}";

    TipoEvaluacion tipoEvaluacion = generarMockTipoEvaluacion(1L, "Replace TipoEvaluacion1");

    BDDMockito.given(tipoEvaluacionService.update(ArgumentMatchers.<TipoEvaluacion>any())).willReturn(tipoEvaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el tipo Evaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace TipoEvaluacion1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-EDITAR" })
  public void replaceTipoEvaluacion_NotFound() throws Exception {
    // given: Un tipo Evaluacion a modificar
    String replaceTipoEvaluacionJson = "{\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}";

    BDDMockito.given(tipoEvaluacionService.update(ArgumentMatchers.<TipoEvaluacion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoEvaluacionNotFoundException(((TipoEvaluacion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-EDITAR" })
  public void removeTipoEvaluacion_ReturnsOk() throws Exception {
    BDDMockito.given(tipoEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockTipoEvaluacion(1L, "TipoEvaluacion1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-VER" })
  public void findAll_Unlimited_ReturnsFullTipoEvaluacionList() throws Exception {
    // given: One hundred TipoEvaluacion
    List<TipoEvaluacion> tipoEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEvaluaciones.add(generarMockTipoEvaluacion(Long.valueOf(i), "TipoEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEvaluacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoEvaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-VER" })
  public void findAll_WithPaging_ReturnsTipoEvaluacionSubList() throws Exception {
    // given: One hundred TipoEvaluacion
    List<TipoEvaluacion> tipoEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEvaluaciones.add(generarMockTipoEvaluacion(Long.valueOf(i), "TipoEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEvaluacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoEvaluacion>>() {
          @Override
          public Page<TipoEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoEvaluacion> content = tipoEvaluaciones.subList(fromIndex, toIndex);
            Page<TipoEvaluacion> page = new PageImpl<>(content, pageable, tipoEvaluaciones.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoEvaluaciones are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoEvaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoEvaluacion>>() {
        });

    // containing nombre='TipoEvaluacion031' to 'TipoEvaluacion040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoEvaluacion tipoEvaluacion = actual.get(i);
      Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo("TipoEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEvaluacionList() throws Exception {
    // given: One hundred TipoEvaluacion and a search query
    List<TipoEvaluacion> tipoEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEvaluaciones.add(generarMockTipoEvaluacion(Long.valueOf(i), "TipoEvaluacion" + String.format("%03d", i)));
    }
    String query = "nombre~TipoEvaluacion%,id:5";

    BDDMockito.given(tipoEvaluacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoEvaluacion>>() {
          @Override
          public Page<TipoEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoEvaluacion> content = new ArrayList<>();
            for (TipoEvaluacion tipoEvaluacion : tipoEvaluaciones) {
              if (tipoEvaluacion.getNombre().startsWith("TipoEvaluacion") && tipoEvaluacion.getId().equals(5L)) {
                content.add(tipoEvaluacion);
              }
            }
            Page<TipoEvaluacion> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPOEVALUACION-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoEvaluacion empty
    List<TipoEvaluacion> tipoEvaluaciones = new ArrayList<>();

    BDDMockito.given(tipoEvaluacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoEvaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void findAllDictamenByTipoEvaluacionAndRevisionMinima_WithTipoEvaluacionId_ReturnsListaDictamen()
      throws Exception {

    TipoEvaluacion tipoEvaluacion = generarMockTipoEvaluacion(4L, "Seguimiento final");
    Dictamen dictamen1 = generarMockDictamen(7L, "Favorable", tipoEvaluacion);
    Dictamen dictamen2 = generarMockDictamen(8L, "Solicitud de aclaraciones", tipoEvaluacion);

    List<Dictamen> listaDictamen = new ArrayList<Dictamen>();
    listaDictamen.add(dictamen1);
    listaDictamen.add(dictamen2);

    BDDMockito.given(tipoEvaluacionService.findAllDictamenByTipoEvaluacionAndRevisionMinima(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyBoolean())).willReturn(listaDictamen);

    mockMvc
        .perform(MockMvcRequestBuilders
            .get(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + DICTAMENES_REV_MINIMA_PATH + PATH_PARAMETER,
                4L, true)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(7L))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(8L))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-EVC-VR" })
  public void findTipoEvaluacionMemoriaRetrospectiva() throws Exception {

    TipoEvaluacion tipoEvaluacion1 = generarMockTipoEvaluacion(1L, "Retrospectiva");
    TipoEvaluacion tipoEvaluacion2 = generarMockTipoEvaluacion(2L, "Memoria");

    List<TipoEvaluacion> listaTipoEvaluacion = new ArrayList<TipoEvaluacion>();
    listaTipoEvaluacion.add(tipoEvaluacion1);
    listaTipoEvaluacion.add(tipoEvaluacion2);

    BDDMockito.given(tipoEvaluacionService.findTipoEvaluacionMemoriaRetrospectiva()).willReturn(listaTipoEvaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_EVALUACION_CONTROLLER_BASE_PATH + MEMORIA_RETROSPECTIVA_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-EVC-VR" })
  public void findTipoEvaluacionSeguimientoAnualFinal() throws Exception {

    TipoEvaluacion tipoEvaluacion1 = generarMockTipoEvaluacion(3L, "Seguimiento anual");
    TipoEvaluacion tipoEvaluacion2 = generarMockTipoEvaluacion(4L, "Seguimiento final");

    List<TipoEvaluacion> listaTipoEvaluacion = new ArrayList<TipoEvaluacion>();
    listaTipoEvaluacion.add(tipoEvaluacion1);
    listaTipoEvaluacion.add(tipoEvaluacion2);

    BDDMockito.given(tipoEvaluacionService.findTipoEvaluacionSeguimientoAnualFinal()).willReturn(listaTipoEvaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_EVALUACION_CONTROLLER_BASE_PATH + SEGUIMIENT_ANUAL_FINAL_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(3L))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(4L))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    ;
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @param id     id del tipoEvaluacion
   * @param nombre la descripción del tipo de Evaluacion
   * @return el objeto tipo Evaluacion
   */

  public TipoEvaluacion generarMockTipoEvaluacion(Long id, String nombre) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(id);
    tipoEvaluacion.setNombre(nombre);
    tipoEvaluacion.setActivo(Boolean.TRUE);

    return tipoEvaluacion;
  }

  /**
   * Función que devuelve un objeto Dictamen
   * 
   * @param id             id del Dictamen
   * @param nombre         nombre del Dictamen
   * @param tipoEvaluacion Tipo Evaluación del Dictamen
   * @return el objeto Dictamen
   */

  public Dictamen generarMockDictamen(Long id, String nombre, TipoEvaluacion tipoEvaluacion) {

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre(nombre);
    dictamen.setActivo(Boolean.TRUE);
    dictamen.setTipoEvaluacion(tipoEvaluacion);

    return dictamen;
  }

}
