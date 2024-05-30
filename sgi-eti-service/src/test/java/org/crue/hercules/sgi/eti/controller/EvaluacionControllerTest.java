package org.crue.hercules.sgi.eti.controller;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.service.ComentarioService;
import org.crue.hercules.sgi.eti.service.DictamenService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
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
 * EvaluacionControllerTest
 */
@WebMvcTest(EvaluacionController.class)
public class EvaluacionControllerTest extends BaseControllerTest {

  @MockBean
  private EvaluacionService evaluacionService;

  @MockBean
  private ComentarioService comentarioService;

  @MockBean
  private DictamenService dictamenService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUACION_CONTROLLER_BASE_PATH = "/evaluaciones";
  private static final String EVALUACION_LIST_PATH = "/evaluables";
  private static final String EVALUACION_SEGUIMIENTO_PATH = "/memorias-seguimiento-final";
  private static final String CONVOCATORIA_REUNION_BASE_PATH = "/convocatoriareunion";
  private static final String CONVOCATORIA_REUNION_NO_REV_MINIMA_BASE_PATH = "/convocatoriareunionnorevminima";
  private static final String NUMERO_COMENTARIOS_BASE_PATH = "/numero-comentarios";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void getEvaluacion_WithId_ReturnsEvaluacion() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockEvaluacion(1L, null)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEvaluacion.nombre").value("TipoEvaluacion1"))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void getEvaluacion_NotFound_Returns404() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EvaluacionNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-C", "ETI-CNV-C", "ETI-CNV-E" })
  public void newEvaluacion_ReturnsEvaluacion() throws Exception {
    // given: Una evaluacion nueva
    String nuevoEvaluacionJson = "{\"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"evaluador1\": {\"id\": 1}, \"evaluador2\": {\"id\": 2},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"tipoEvaluacion\": {\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"},\"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";
    Evaluacion evaluacion = generarMockEvaluacion(1L, null);

    BDDMockito.given(evaluacionService.create(ArgumentMatchers.<Evaluacion>any())).willReturn(evaluacion);

    // when: Creamos una evaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea la nueva evaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEvaluacion.nombre").value("TipoEvaluacion1"))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value("1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-C", "ETI-CNV-C", "ETI-CNV-E" })
  public void newEvaluacion_Error_Returns400() throws Exception {
    // given: Una evaluacion nueva que produce un error al crearse
    String nuevoEvaluacionJson = "{\"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"tipoEvaluacion\": {\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\"}";

    BDDMockito.given(evaluacionService.create(ArgumentMatchers.<Evaluacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un evaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void replaceEvaluacion_ReturnsEvaluacion() throws Exception {
    // given: Una evaluacion a modificar
    String replaceEvaluacionJson = "{\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"evaluador1\": {\"id\": 1}, \"evaluador2\": {\"id\": 2},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"tipoEvaluacion\": {\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";

    Evaluacion evaluacion = generarMockEvaluacion(1L, " Replace");

    BDDMockito.given(evaluacionService.update(ArgumentMatchers.<Evaluacion>any())).willReturn(evaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica la evaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria Replace"))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen Replace"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEvaluacion.nombre").value("TipoEvaluacion1"))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value("1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void replaceEvaluacion_NotFound() throws Exception {
    // given: Una evaluacion a modificar
    String replaceEvaluacionJson = "{\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"evaluador1\": {\"id\": 1}, \"evaluador2\": {\"id\": 2},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"tipoEvaluacion\": {\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";

    BDDMockito.given(evaluacionService.update(ArgumentMatchers.<Evaluacion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new EvaluacionNotFoundException(((Evaluacion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-B", "ETI-CNV-E" })
  public void removeEvaluacion_ReturnsOk() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockEvaluacion(1L, null));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void findAll_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: Evaluacion empty
    List<Evaluacion> evaluaciones = new ArrayList<>();
    evaluaciones.isEmpty();

    BDDMockito.given(evaluacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(evaluaciones));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void findAll_WithPaging_ReturnsEvaluacionSubList() throws Exception {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluacion>>() {
          @Override
          public Page<Evaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluacion> content = evaluaciones.subList(fromIndex, toIndex);
            Page<Evaluacion> page = new PageImpl<>(content, pageable, evaluaciones.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Evaluaciones are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Evaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Evaluacion>>() {
        });

    // containing memoria.titulo='Memoria031' to 'Memoria040'
    // containing dictamen.nombre='Dictamen031' to 'Dictamen040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluacion evaluacion = actual.get(i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluacionList() throws Exception {
    // given: One hundred Evaluacion and a search query
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }
    String query = "esRevMinima:true,id:5";

    BDDMockito.given(evaluacionService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluacion>>() {
          @Override
          public Page<Evaluacion> answer(InvocationOnMock invocation) throws Throwable {
            List<Evaluacion> content = new ArrayList<>();
            for (Evaluacion evaluacion : evaluaciones) {
              if (evaluacion.getEsRevMinima().equals(Boolean.TRUE) && evaluacion.getId().equals(5L)) {
                content.add(evaluacion);
              }
            }
            Page<Evaluacion> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAllActivasByConvocatoriaReunionId_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + CONVOCATORIA_REUNION_BASE_PATH + "/{id}", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAllActivasByConvocatoriaReunionId__ReturnsNoContent() throws Exception {
    // given: Evaluacion empty
    List<Evaluacion> evaluaciones = new ArrayList<>();

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + CONVOCATORIA_REUNION_BASE_PATH + "/{id}", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllByConvocatoriaReunionIdAndNoEsRevMinima_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: One hundred EvaluacionWithIsEliminable
    List<EvaluacionWithIsEliminable> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacionWithIsEliminable(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findAllByConvocatoriaReunionIdAndNoEsRevMinima(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(EVALUACION_CONTROLLER_BASE_PATH + CONVOCATORIA_REUNION_NO_REV_MINIMA_BASE_PATH
                + "/{idConvocatoriaReunion}", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllByConvocatoriaReunionIdAndNoEsRevMinima_Unlimited_ReturnsNoContent() throws Exception {
    // given: One hundred EvaluacionWithIsEliminable
    List<EvaluacionWithIsEliminable> evaluaciones = new ArrayList<>();

    BDDMockito.given(evaluacionService.findAllByConvocatoriaReunionIdAndNoEsRevMinima(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(EVALUACION_CONTROLLER_BASE_PATH + CONVOCATORIA_REUNION_NO_REV_MINIMA_BASE_PATH
                + "/{idConvocatoriaReunion}", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void getComentariosGestorEmptyList() throws Exception {
    // given: Existe la evaluación pero no tiene comentarios
    Long id = 3L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentarios-gestor").toString();

    BDDMockito
        .given(comentarioService.findByEvaluacionIdGestor(ArgumentMatchers.anyLong()))
        .willReturn(Collections.emptyList());

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void getComentariosGestorValid() throws Exception {
    // given: Datos existentes con evaluacion
    Long id = 3L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentarios-gestor").toString();

    List<Comentario> response = new ArrayList<>();
    response.add(generarMockComentario(Long.valueOf(1), "texto", 1L));
    response.add(generarMockComentario(Long.valueOf(3), "texto2", 1L));

    BDDMockito
        .given(comentarioService.findByEvaluacionIdGestor(ArgumentMatchers.anyLong()))
        .willReturn(response);

    // when: Se buscan todos los comentarios
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los comentarios
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Comentario>>() {
        })).isEqualTo(response);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVALR" })
  public void getComentariosEvaluadorEmptyList() throws Exception {
    // given: Existe la evaluación pero no tiene comentarios
    Long id = 3L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentarios-evaluador").toString();

    BDDMockito
        .given(comentarioService.findByEvaluacionIdEvaluador(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Collections.emptyList());

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVALR" })
  public void getComentariosEvaluadorValid() throws Exception {
    // given: Datos existentes con evaluacion
    Long id = 3L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentarios-evaluador").toString();

    List<Comentario> response = new ArrayList<>();
    response.add(generarMockComentario(Long.valueOf(1), "texto", 2L));
    response.add(generarMockComentario(Long.valueOf(3), "texto2", 2L));

    BDDMockito
        .given(comentarioService.findByEvaluacionIdEvaluador(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(response);

    // when: Se buscan los comentarios de tipo Evaluador
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan los comentarios de tipo Evaluador
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Comentario>>() {
        })).isEqualTo(response);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR" })
  public void createComentarioGestorValidComentarios() throws Exception {
    // given: Existe la evaluación y tiene comentarios
    Long id = 3L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentario-gestor").toString();

    Comentario comentario = generarMockComentario(300L, "Comentario1", 1L);

    // given: Un listado de comentarios
    String nuevoComentarioJson = "{\"apartado\": {\"id\": 100},  \"texto\": \"Comentario1\"}";

    BDDMockito
        .given(comentarioService.createComentarioGestor(ArgumentMatchers.anyLong(), ArgumentMatchers.<Comentario>any()))
        .willReturn(comentario);

    // when: Creamos el listado comentario
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.post(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea los comentario y los devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<Comentario>() {
        })).isEqualTo(comentario);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void createComentarioGestor_WithId_Returns400() throws Exception {

    // given: Nueva entidad con Id
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentario-gestor").toString();

    String nuevoComentarioJson = mapper.writeValueAsString(generarMockComentario(1L, "Comentario1", 1L));

    BDDMockito
        .given(comentarioService.createComentarioGestor(ArgumentMatchers.anyLong(), ArgumentMatchers.<Comentario>any()))
        .willThrow(new IllegalArgumentException());

    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    mockMvc
        .perform(MockMvcRequestBuilders.post(url, 1L).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR" })
  public void createComentarioEvaluadorSuccess() throws Exception {
    // given: Existe la evaluación y tiene comentarios
    Long id = 3L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentario-gestor").toString();

    Comentario comentario = generarMockComentario(300L, "Comentario1", 1L);

    // given: Un listado de comentarios
    String nuevoComentarioJson = "{\"apartado\": {\"id\": 100},  \"texto\": \"Comentario1\"}";

    BDDMockito.given(comentarioService.createComentarioEvaluador(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Comentario>any(), ArgumentMatchers.anyString())).willReturn(comentario);

    // when: Creamos el listado comentario
    mockMvc
        .perform(MockMvcRequestBuilders.post(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea los comentario y los devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVALR", "ETI-EVC-INV-EVALR" })
  public void createComentarioEvaluador_WithId_Returns400() throws Exception {

    // given: Nueva entidad con Id
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentario-evaluador").toString();

    String nuevoComentarioJson = mapper.writeValueAsString(generarMockComentario(1L, "Comentario1", 1L));

    BDDMockito
        .given(comentarioService.createComentarioEvaluador(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<Comentario>any(), ArgumentMatchers.anyString()))
        .willThrow(new IllegalArgumentException());

    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    mockMvc
        .perform(MockMvcRequestBuilders.post(url, 1L).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(nuevoComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void updateComentarioGestor_Success() throws Exception {
    // given: Existe la evaluación
    Long id = 3L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentario-gestor").append("/{idComentario}").toString();

    Comentario comentario = generarMockComentario(1L, "Comentario Replace", 1L);

    // given: Comentarios para actualizar
    String replaceComentarioJson = "{\"id\": 1, \"apartado\": {\"id\": 100}, \"evaluacion\": {\"id\": 200}, \"tipoComentario\": {\"id\": 1}, \"texto\": \"Comentario\"}";

    BDDMockito
        .given(comentarioService.updateComentarioGestor(ArgumentMatchers.anyLong(), ArgumentMatchers.<Comentario>any()))
        .willReturn(comentario);

    mockMvc
        .perform(MockMvcRequestBuilders.put(url, id, 1L).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(replaceComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el comentario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("texto").value("Comentario Replace"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void updateComentarioGestor_NotFound() throws Exception {
    // given: Un comentario a modificar
    String replaceComentarioJson = "{\"id\": 1, \"apartado\": {\"id\": 100}, \"evaluacion\": {\"id\": 200}, \"tipoComentario\": {\"id\": 1}, \"texto\": \"Comentario\"}";

    BDDMockito
        .given(comentarioService.updateComentarioGestor(ArgumentMatchers.anyLong(), ArgumentMatchers.<Comentario>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ComentarioNotFoundException((invocation.getArgument(0)));
        });
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/comentario-gestor" + "/{idComentario}", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceComentarioJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL" })
  public void deleteComentarioGestor_Success() throws Exception {
    // given: Existe la evaluación
    Long idEvaluacion = 3L;
    Long idComentario = 4L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentario-gestor").append("/{idComentario}").toString();

    // given: Listado de ids de comentario para eliminar
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url, idEvaluacion, idComentario)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Realiza la petición se realiza correctamente
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVALR" })
  public void deleteComentarioEvaluador_Success() throws Exception {
    // given: Existe la evaluación
    Long idEvaluacion = 3L;
    Long idComentario = 4L;
    final String url = new StringBuffer(EVALUACION_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comentario-evaluador").append("/{idComentario}").toString();

    // given: Listado de ids de comentario para eliminar
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url, idEvaluacion, idComentario)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Realiza la petición se realiza correctamente
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_Unlimited_ReturnsFiltratedEvaluacionList() throws Exception {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findAllByMemoriaAndRetrospectivaEnEvaluacion(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_ReturnsNoContent() throws Exception {
    // given: Evaluacion empty
    List<Evaluacion> evaluaciones = new ArrayList<>();
    evaluaciones.isEmpty();

    BDDMockito.given(evaluacionService.findAllByMemoriaAndRetrospectivaEnEvaluacion(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithPaging_ReturnsFiltratedEvaluacionSubList()
      throws Exception {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findAllByMemoriaAndRetrospectivaEnEvaluacion(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Evaluacion>>() {
          @Override
          public Page<Evaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluacion> content = evaluaciones.subList(fromIndex, toIndex);
            Page<Evaluacion> page = new PageImpl<>(content, pageable, evaluaciones.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_LIST_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Evaluaciones are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Evaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Evaluacion>>() {
        });

    // containing memoria.titulo='Memoria031' to 'Memoria040'
    // containing dictamen.nombre='Dictamen031' to 'Dictamen040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluacion evaluacion = actual.get(i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
      // Assertions.assertThat(evaluacion.getConvocatoriaReunion().getCodigo()).isEqualTo("CR-"
      // + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-EVC-EVAL" })
  public void findByEvaluacionesEnSeguimientoFinal_ReturnsFiltratedEvaluacionList() throws Exception {
    // given: Three Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 3; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionService.findByEvaluacionesEnSeguimientoAnualOrFinal(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_SEGUIMIENTO_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page three Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-EVC-EVAL" })
  public void findByEvaluacionesEnSeguimientoFinal_ReturnsNoContent() throws Exception {
    // given: evaluaciones empty
    List<Evaluacion> evaluaciones = new ArrayList<>();
    evaluaciones.isEmpty();

    BDDMockito.given(evaluacionService.findByEvaluacionesEnSeguimientoAnualOrFinal(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + EVALUACION_SEGUIMIENTO_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-E" })
  public void countNumComentariosEvaluacion_ReturnsOk() throws Exception {

    BDDMockito.given(comentarioService.countByEvaluacionId(ArgumentMatchers.anyLong()))
        .willReturn(ArgumentMatchers.anyInt());

    mockMvc
        .perform(MockMvcRequestBuilders
            .get(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + NUMERO_COMENTARIOS_BASE_PATH, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id     id del Evaluacion
   * @param sufijo el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), Instant.now()), 3, Boolean.TRUE,
        null);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now());
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluador evaluador1 = new Evaluador();
    evaluador1.setId(1L);

    Evaluador evaluador2 = new Evaluador();
    evaluador2.setId(2L);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setVersion(2);
    evaluacion.setEvaluador1(evaluador1);
    evaluacion.setEvaluador2(evaluador2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

  /**
   * Función que devuelve un objeto Comentario
   * 
   * @param id    id de la comentario
   * @param texto texto del comentario
   * @return el objeto Comentario
   */
  private Comentario generarMockComentario(Long id, String texto, Long tipoComentarioId) {
    Apartado apartado = new Apartado();
    apartado.setId(100L);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(200L);

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(tipoComentarioId);

    Comentario comentario = new Comentario();
    comentario.setId(id);
    comentario.setApartado(apartado);
    comentario.setEvaluacion(evaluacion);
    comentario.setTipoComentario(tipoComentario);
    comentario.setTexto(texto);

    return comentario;
  }

  /**
   * Función que devuelve un objeto EvaluacionWithIsEliminable
   * 
   * @param id     id del EvaluacionWithIsEliminable
   * @param sufijo el sufijo para título y nombre
   * @return el objeto EvaluacionWithIsEliminable
   */

  public EvaluacionWithIsEliminable generarMockEvaluacionWithIsEliminable(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), Instant.now()), 3, Boolean.TRUE,
        null);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now());
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluador evaluador1 = new Evaluador();
    evaluador1.setId(1L);

    Evaluador evaluador2 = new Evaluador();
    evaluador2.setId(2L);

    EvaluacionWithIsEliminable evaluacionWithIsEliminable = new EvaluacionWithIsEliminable();
    evaluacionWithIsEliminable.setId(id);
    evaluacionWithIsEliminable.setMemoria(memoria);
    evaluacionWithIsEliminable.setConvocatoriaReunion(convocatoriaReunion);
    evaluacionWithIsEliminable.setTipoEvaluacion(tipoEvaluacion);
    evaluacionWithIsEliminable.setDictamen(dictamen);
    evaluacionWithIsEliminable.setEvaluador1(evaluador1);
    evaluacionWithIsEliminable.setEvaluador2(evaluador2);
    evaluacionWithIsEliminable.setFechaDictamen(Instant.now());
    evaluacionWithIsEliminable.setVersion(2);
    evaluacionWithIsEliminable.setEsRevMinima(Boolean.FALSE);
    evaluacionWithIsEliminable.setActivo(Boolean.TRUE);

    return evaluacionWithIsEliminable;
  }

}
