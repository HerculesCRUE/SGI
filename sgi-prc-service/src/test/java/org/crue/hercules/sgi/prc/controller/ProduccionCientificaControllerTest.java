package org.crue.hercules.sgi.prc.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.prc.dto.AcreditacionOutput;
import org.crue.hercules.sgi.prc.dto.ActividadOutput;
import org.crue.hercules.sgi.prc.dto.ActividadResumen;
import org.crue.hercules.sgi.prc.dto.AutorOutput;
import org.crue.hercules.sgi.prc.dto.ComiteEditorialOutput;
import org.crue.hercules.sgi.prc.dto.ComiteEditorialResumen;
import org.crue.hercules.sgi.prc.dto.CongresoOutput;
import org.crue.hercules.sgi.prc.dto.CongresoResumen;
import org.crue.hercules.sgi.prc.dto.DireccionTesisOutput;
import org.crue.hercules.sgi.prc.dto.DireccionTesisResumen;
import org.crue.hercules.sgi.prc.dto.EstadoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.IndiceImpactoOutput;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaOutput;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaResumen;
import org.crue.hercules.sgi.prc.dto.ProyectoOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.service.AcreditacionService;
import org.crue.hercules.sgi.prc.service.AutorService;
import org.crue.hercules.sgi.prc.service.IndiceImpactoService;
import org.crue.hercules.sgi.prc.service.ProduccionCientificaService;
import org.crue.hercules.sgi.prc.service.ProyectoService;
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
 * ProduccionCientificaControllerTest
 */
@WebMvcTest(ProduccionCientificaController.class)
public class ProduccionCientificaControllerTest extends BaseControllerTest {

  @MockBean
  private ProduccionCientificaService service;
  @MockBean
  private IndiceImpactoService indiceImpactoService;
  @MockBean
  private AutorService autorService;
  @MockBean
  private ProyectoService proyectoService;
  @MockBean
  private AcreditacionService acreditacionService;

  private static final String CONTROLLER_BASE_PATH = ProduccionCientificaController.MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PUBLICACIONES = ProduccionCientificaController.PATH_PUBLICACIONES;
  private static final String PATH_COMITES_EDITORIALES = ProduccionCientificaController.PATH_COMITES_EDITORIALES;
  private static final String PATH_CONGRESOS = ProduccionCientificaController.PATH_CONGRESOS;
  private static final String PATH_OBRAS_ARTISTICAS = ProduccionCientificaController.PATH_OBRAS_ARTISTICAS;
  private static final String PATH_ACTIVIDADES = ProduccionCientificaController.PATH_ACTIVIDADES;
  private static final String PATH_DIRECCIONES_TESIS = ProduccionCientificaController.PATH_DIRECCIONES_TESIS;
  private static final String PATH_PARAMETER_VALIDAR = "/validar";
  private static final String PATH_PARAMETER_RECHAZAR = "/rechazar";
  private static final String PATH_INDICES_IMPACTO = ProduccionCientificaController.PATH_INDICES_IMPACTO;
  private static final String PATH_PROYECTOS = ProduccionCientificaController.PATH_PROYECTOS;
  private static final String PATH_ACREDITACIONES = ProduccionCientificaController.PATH_ACREDITACIONES;
  private static final String PATH_AUTORES = ProduccionCientificaController.PATH_AUTORES;
  private static Long estadoProduccionCientificaId = 0L;

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllPublicaciones_ReturnsPage() throws Exception {
    // given: Una lista con 37 PublicacionResumen
    List<PublicacionResumen> publicaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      publicaciones.add(generarMockPublicacionResumen(i, String.valueOf(i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllPublicaciones(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > publicaciones.size() ? publicaciones.size() : toIndex;
          List<PublicacionResumen> content = publicaciones.subList(fromIndex, toIndex);
          Page<PublicacionResumen> pageResponse = new PageImpl<>(content, pageable,
              publicaciones.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PUBLICACIONES)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los PublicacionOutput del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<PublicacionOutput> publicacionesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<PublicacionOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      PublicacionOutput publicacion = publicacionesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(publicacion.getProduccionCientificaRef()).isEqualTo("ProduccionCientifica" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllPublicaciones_ReturnsNoContent() throws Exception {
    // given: Una lista con 0 PublicacionResumen
    List<PublicacionResumen> publicaciones = new ArrayList<>();

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllPublicaciones(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<PublicacionResumen> pageResponse = new PageImpl<>(publicaciones, pageable,
              publicaciones.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PUBLICACIONES)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve 204 - NO CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllComitesEditoriales_ReturnsPage() throws Exception {
    // given: Una lista con 37 ComiteEditorialResumen
    List<ComiteEditorialResumen> comitesEditoriales = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      comitesEditoriales.add(generarMockComiteEditorialResumen(i, String.valueOf(i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllComitesEditoriales(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > comitesEditoriales.size() ? comitesEditoriales.size() : toIndex;
          List<ComiteEditorialResumen> content = comitesEditoriales.subList(fromIndex, toIndex);
          Page<ComiteEditorialResumen> pageResponse = new PageImpl<>(content, pageable,
              comitesEditoriales.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_COMITES_EDITORIALES)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ComiteEditorialOutput del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<ComiteEditorialOutput> comitesEditorialesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ComiteEditorialOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ComiteEditorialOutput comiteEditorial = comitesEditorialesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(comiteEditorial.getProduccionCientificaRef()).isEqualTo("ProduccionCientifica" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllComitesEditoriales_ReturnsNoContent() throws Exception {
    // given: Una lista con 0 PublicacionResumen
    List<ComiteEditorialResumen> produccionCientificas = new ArrayList<>();

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllComitesEditoriales(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ComiteEditorialResumen> pageResponse = new PageImpl<>(produccionCientificas, pageable,
              produccionCientificas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_COMITES_EDITORIALES)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve 204 - NO CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllCongresos_ReturnsPage() throws Exception {
    // given: Una lista con 37 CongresoResumen
    List<CongresoResumen> congresos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      congresos.add(generarMockCongresoResumen(i, String.valueOf(i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllCongresos(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > congresos.size() ? congresos.size() : toIndex;
          List<CongresoResumen> content = congresos.subList(fromIndex, toIndex);
          Page<CongresoResumen> pageResponse = new PageImpl<>(content, pageable,
              congresos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_CONGRESOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los CongresoOutput del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<CongresoOutput> congresosResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<CongresoOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      CongresoOutput congreso = congresosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(congreso.getProduccionCientificaRef()).isEqualTo("ProduccionCientifica" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllCongresos_ReturnsNoContent() throws Exception {
    // given: Una lista con 0 CongresoResumen
    List<CongresoResumen> congresos = new ArrayList<>();

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllCongresos(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<CongresoResumen> pageResponse = new PageImpl<>(congresos, pageable,
              congresos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_CONGRESOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve 204 - NO CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllObrasArtisticas_ReturnsPage() throws Exception {
    // given: Una lista con 37 ObraArtisticaResumen
    List<ObraArtisticaResumen> obrasArtisticas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      obrasArtisticas.add(generarMockObraArtisticaResumen(i, String.valueOf(i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllObrasArtisticas(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > obrasArtisticas.size() ? obrasArtisticas.size() : toIndex;
          List<ObraArtisticaResumen> content = obrasArtisticas.subList(fromIndex, toIndex);
          Page<ObraArtisticaResumen> pageResponse = new PageImpl<>(content, pageable,
              obrasArtisticas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_OBRAS_ARTISTICAS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ObraArtisticaOutput del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<ObraArtisticaOutput> obrasArtisticasResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ObraArtisticaOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ObraArtisticaOutput obraArtistica = obrasArtisticasResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(obraArtistica.getProduccionCientificaRef()).isEqualTo("ProduccionCientifica" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllObrasArtisticas_ReturnsNoContent() throws Exception {
    // given: Una lista con 0 ObraArtisticaResumen
    List<ObraArtisticaResumen> obrasArtisticas = new ArrayList<>();

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllObrasArtisticas(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ObraArtisticaResumen> pageResponse = new PageImpl<>(obrasArtisticas, pageable,
              obrasArtisticas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_OBRAS_ARTISTICAS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve 204 - NO CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllActividades_ReturnsPage() throws Exception {
    // given: Una lista con 37 ActividadResumen
    List<ActividadResumen> actividades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      actividades.add(generarMockActividadResumen(i, String.valueOf(i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllActividades(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > actividades.size() ? actividades.size() : toIndex;
          List<ActividadResumen> content = actividades.subList(fromIndex, toIndex);
          Page<ActividadResumen> pageResponse = new PageImpl<>(content, pageable,
              actividades.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ACTIVIDADES)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ActividadOutput del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<ActividadOutput> actividadesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ActividadOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ActividadOutput actividad = actividadesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(actividad.getProduccionCientificaRef()).isEqualTo("ProduccionCientifica" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllActividades_ReturnsNoContent() throws Exception {
    // given: Una lista con 0 ActividadResumen
    List<ActividadResumen> actividades = new ArrayList<>();

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllActividades(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ActividadResumen> pageResponse = new PageImpl<>(actividades, pageable,
              actividades.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ACTIVIDADES)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve 204 - NO CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllDireccionesTesis_ReturnsPage() throws Exception {
    // given: Una lista con 37 DireccionTesisResumen
    List<DireccionTesisResumen> direccionesTesis = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      direccionesTesis.add(generarMockDireccionTesisResumen(i, String.valueOf(i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllDireccionesTesis(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > direccionesTesis.size() ? direccionesTesis.size() : toIndex;
          List<DireccionTesisResumen> content = direccionesTesis.subList(fromIndex, toIndex);
          Page<DireccionTesisResumen> pageResponse = new PageImpl<>(content, pageable,
              direccionesTesis.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_DIRECCIONES_TESIS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ActividadOutput del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<DireccionTesisOutput> direccionesTesisResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<DireccionTesisOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      DireccionTesisOutput direccionTesis = direccionesTesisResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(direccionTesis.getProduccionCientificaRef()).isEqualTo("ProduccionCientifica" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findAllDireccionesTesis_ReturnsNoContent() throws Exception {
    // given: Una lista con 0 DireccionTesisResumen
    List<DireccionTesisResumen> direccionesTesis = new ArrayList<>();

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllDireccionesTesis(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<DireccionTesisResumen> pageResponse = new PageImpl<>(direccionesTesis, pageable,
              direccionesTesis.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_DIRECCIONES_TESIS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve 204 - NO CONTENT
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findById_WithExistingId_ReturnsProduccionCientifica() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      ProduccionCientifica produccionCientifica = ProduccionCientifica.builder().id(paramId).build();
      return produccionCientifica;
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested ProduccionCientifica is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      throw new ProduccionCientificaNotFoundException(paramId.toString());
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-E" })
  void validar_WithExistingId_ReturnProduccionCientifica() throws Exception {
    // given: existing id
    ProduccionCientifica produccionCientifica = generarMockProduccionCientifica(1L,
        "ProduccionCientifica" + String.format("%03d", 1));

    BDDMockito.given(service.cambiarEstado(ArgumentMatchers.<Long>any(), ArgumentMatchers.<TipoEstadoProduccion>any(),
        ArgumentMatchers.<String>any())).willAnswer((InvocationOnMock invocation) -> {
          ProduccionCientifica produccionCientificaValidated = new ProduccionCientifica();
          BeanUtils.copyProperties(produccionCientifica, produccionCientificaValidated);
          produccionCientificaValidated.setEstado(generarMockEstadoProduccionCientifica(
              produccionCientificaValidated.getId(), TipoEstadoProduccion.VALIDADO));
          return produccionCientificaValidated;
        });

    // when: validar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_VALIDAR, produccionCientifica.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Proyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("estado.estado").value(TipoEstadoProduccion.VALIDADO.toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-E" })
  void validar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProduccionCientificaNotFoundException(id.toString())).given(service).cambiarEstado(
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<TipoEstadoProduccion>any(),
        ArgumentMatchers.<String>any());

    // when: validar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_VALIDAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-E" })
  void rechazar_WithExistingId_ReturnProduccionCientifica() throws Exception {
    // given: existing id
    ProduccionCientifica produccionCientifica = generarMockProduccionCientifica(1L,
        "ProduccionCientifica" + String.format("%03d", 1));
    EstadoProduccionCientificaInput estado = EstadoProduccionCientificaInput.builder().comentario("Motivo rechazo")
        .build();

    BDDMockito.given(service.cambiarEstado(ArgumentMatchers.<Long>any(), ArgumentMatchers.<TipoEstadoProduccion>any(),
        ArgumentMatchers.<String>any())).willAnswer((InvocationOnMock invocation) -> {
          ProduccionCientifica produccionCientificaValidated = new ProduccionCientifica();
          BeanUtils.copyProperties(produccionCientifica, produccionCientificaValidated);
          produccionCientificaValidated.setEstado(generarMockEstadoProduccionCientifica(
              produccionCientificaValidated.getId(), TipoEstadoProduccion.RECHAZADO));
          return produccionCientificaValidated;
        });

    // when: rechazar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_RECHAZAR, produccionCientifica.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(estado)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Proyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("estado.estado").value(TipoEstadoProduccion.RECHAZADO.toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-E" })
  void rechazar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;
    EstadoProduccionCientificaInput estado = EstadoProduccionCientificaInput.builder().comentario("Motivo rechazo")
        .build();

    BDDMockito.willThrow(new ProduccionCientificaNotFoundException(id.toString())).given(service).cambiarEstado(
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<TipoEstadoProduccion>any(),
        ArgumentMatchers.<String>any());

    // when: rechazar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_RECHAZAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(estado))
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * 
   * INDICE IMPACTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  void findIndicesImpacto_ReturnsPage() throws Exception {
    // given: Una lista con 37 IndiceImpacto para la ProduccionCientifica
    Long produccionCientificaId = 1L;

    List<IndiceImpacto> indicesImpacto = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      indicesImpacto.add(generarMockIndiceImpacto(i, produccionCientificaId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(indiceImpactoService
        .findAllByProduccionCientificaId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<IndiceImpacto>>() {
          @Override
          public Page<IndiceImpacto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > indicesImpacto.size() ? indicesImpacto.size() : toIndex;
            List<IndiceImpacto> content = indicesImpacto.subList(fromIndex, toIndex);
            Page<IndiceImpacto> page = new PageImpl<>(content, pageable, indicesImpacto.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_INDICES_IMPACTO, produccionCientificaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los IndiceImpacto del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<IndiceImpactoOutput> indiceImpactoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<IndiceImpactoOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      IndiceImpactoOutput indiceImpacto = indiceImpactoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(indiceImpacto.getOtraFuenteImpacto())
          .isEqualTo("Otras-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  void findIndicesImpacto_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de IndiceImpacto para la ProduccionCientifica
    Long produccionCientificaId = 1L;
    List<IndiceImpacto> indicesImpacto = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(indiceImpactoService.findAllByProduccionCientificaId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<IndiceImpacto>>() {
          @Override
          public Page<IndiceImpacto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<IndiceImpacto> page = new PageImpl<>(indicesImpacto, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_INDICES_IMPACTO, produccionCientificaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * AUTOR
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  void findAutores_ReturnsPage() throws Exception {
    // given: Una lista con 37 Autor para la ProduccionCientifica
    Long produccionCientificaId = 1L;

    List<Autor> autores = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      autores.add(generarMockAutor(i, produccionCientificaId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(autorService
        .findAllByProduccionCientificaId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Autor>>() {
          @Override
          public Page<Autor> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > autores.size() ? autores.size() : toIndex;
            List<Autor> content = autores.subList(fromIndex, toIndex);
            Page<Autor> page = new PageImpl<>(content, pageable, autores.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_AUTORES, produccionCientificaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los Autor del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<AutorOutput> autorResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<AutorOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      AutorOutput autor = autorResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(autor.getFirma())
          .isEqualTo("Firma-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  void findAutores_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Autor para la ProduccionCientifica
    Long produccionCientificaId = 1L;
    List<IndiceImpacto> autores = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(autorService.findAllByProduccionCientificaId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<IndiceImpacto>>() {
          @Override
          public Page<IndiceImpacto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<IndiceImpacto> page = new PageImpl<>(autores, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_AUTORES, produccionCientificaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * PROYECTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  void findProyectos_ReturnsPage() throws Exception {
    // given: Una lista con 37 Proyecto para la ProduccionCientifica
    Long produccionCientificaId = 1L;

    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i, produccionCientificaId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(proyectoService
        .findAllByProduccionCientificaId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
            List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
            Page<Proyecto> page = new PageImpl<>(content, pageable, proyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PROYECTOS, produccionCientificaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los Proyecto del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoOutput> proyectoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ProyectoOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoOutput proyecto = proyectoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyecto.getProyectoRef())
          .isEqualTo(i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  void findProyectos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de IndiceImpacto para la ProduccionCientifica
    Long produccionCientificaId = 1L;
    List<Proyecto> proyectos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(proyectoService.findAllByProduccionCientificaId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<Proyecto> page = new PageImpl<>(proyectos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PROYECTOS, produccionCientificaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * ACREDITACION
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  void findAcreditaciones_ReturnsPage() throws Exception {
    // given: Una lista con 37 Acreditacion para la ProduccionCientifica
    Long produccionCientificaId = 1L;

    List<Acreditacion> acreditaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      acreditaciones.add(generarMockAcreditacion(i, produccionCientificaId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(acreditacionService
        .findAllByProduccionCientificaId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Acreditacion>>() {
          @Override
          public Page<Acreditacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > acreditaciones.size() ? acreditaciones.size() : toIndex;
            List<Acreditacion> content = acreditaciones.subList(fromIndex, toIndex);
            Page<Acreditacion> page = new PageImpl<>(content, pageable, acreditaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_ACREDITACIONES, produccionCientificaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con las Acreditacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<AcreditacionOutput> acreditacionResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<AcreditacionOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      AcreditacionOutput acreditacion = acreditacionResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(acreditacion.getUrl())
          .isEqualTo("url-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  void findAcreditaciones_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de IndiceImpacto para la ProduccionCientifica
    Long produccionCientificaId = 1L;
    List<Acreditacion> indicesImpacto = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(acreditacionService.findAllByProduccionCientificaId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Acreditacion>>() {
          @Override
          public Page<Acreditacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<Acreditacion> page = new PageImpl<>(indicesImpacto, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_ACREDITACIONES, produccionCientificaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private ProduccionCientifica generarMockProduccionCientifica(Long id, String idRef) {
    ProduccionCientifica produccionCientifica = new ProduccionCientifica();
    produccionCientifica.setId(id);
    produccionCientifica.setProduccionCientificaRef(idRef);
    produccionCientifica
        .setEpigrafeCVN(EpigrafeCVN.E060_010_010_000);
    produccionCientifica.setEstado(generarMockEstadoProduccionCientifica(id, TipoEstadoProduccion.PENDIENTE));

    return produccionCientifica;
  }

  private EstadoProduccionCientifica generarMockEstadoProduccionCientifica(Long id,
      TipoEstadoProduccion tipoEstadoProduccion) {
    EstadoProduccionCientifica estadoProduccionCientifica = new EstadoProduccionCientifica();
    estadoProduccionCientifica.setId(estadoProduccionCientificaId++);
    estadoProduccionCientifica.setProduccionCientificaId(id);
    estadoProduccionCientifica.setEstado(tipoEstadoProduccion);
    estadoProduccionCientifica.setFecha(Instant.now());

    return estadoProduccionCientifica;
  }

  private PublicacionResumen generarMockPublicacionResumen(Long id, String idRef) {
    PublicacionResumen publicacion = new PublicacionResumen();
    publicacion.setId(id);
    publicacion.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    publicacion.setEpigrafeCVN(EpigrafeCVN.E060_010_010_000);
    publicacion.setFechaPublicacion(Instant.now());
    publicacion.setTituloPublicacion("Título" + idRef);
    publicacion.setTipoProduccion("Produccion" + idRef);

    return publicacion;
  }

  private ComiteEditorialResumen generarMockComiteEditorialResumen(Long id, String idRef) {
    ComiteEditorialResumen comiteEditorial = new ComiteEditorialResumen();
    comiteEditorial.setId(id);
    comiteEditorial.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    comiteEditorial.setEpigrafeCVN(EpigrafeCVN.E060_030_030_000);
    comiteEditorial.setFechaInicio(Instant.now());
    comiteEditorial.setNombre("Nombre" + idRef);

    return comiteEditorial;
  }

  private CongresoResumen generarMockCongresoResumen(Long id, String idRef) {
    CongresoResumen congreso = new CongresoResumen();
    congreso.setId(id);
    congreso.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    congreso.setEpigrafeCVN(EpigrafeCVN.E060_010_020_000);
    congreso.setFechaCelebracion(Instant.now());
    congreso.setTipoEvento("Tipo-evento" + idRef);
    congreso.setTituloTrabajo("Titulo-trabajo" + idRef);

    return congreso;
  }

  private ObraArtisticaResumen generarMockObraArtisticaResumen(Long id, String idRef) {
    ObraArtisticaResumen obraArtistica = new ObraArtisticaResumen();
    obraArtistica.setId(id);
    obraArtistica.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    obraArtistica.setEpigrafeCVN(EpigrafeCVN.E050_020_030_000);
    obraArtistica.setDescripcion("Descripcion-" + idRef);
    obraArtistica.setFechaInicio(Instant.now());

    return obraArtistica;
  }

  private ActividadResumen generarMockActividadResumen(Long id, String idRef) {
    ActividadResumen actividad = new ActividadResumen();
    actividad.setId(id);
    actividad.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    actividad.setEpigrafeCVN(EpigrafeCVN.E060_020_030_000);
    actividad.setTituloActividad("Título" + idRef);
    actividad.setFechaInicio(Instant.now());

    return actividad;
  }

  private DireccionTesisResumen generarMockDireccionTesisResumen(Long id, String idRef) {
    DireccionTesisResumen actividad = new DireccionTesisResumen();
    actividad.setId(id);
    actividad.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    actividad.setEpigrafeCVN(EpigrafeCVN.E030_040_000_000);
    actividad.setTituloTrabajo("Título" + idRef);
    actividad.setFechaDefensa(Instant.now());

    return actividad;
  }

  private IndiceImpacto generarMockIndiceImpacto(Long id, Long produccionCientificaId) {
    IndiceImpacto indiceImpacto = new IndiceImpacto();
    indiceImpacto.setId(id);
    indiceImpacto.setProduccionCientificaId(produccionCientificaId);
    indiceImpacto.setFuenteImpacto(TipoFuenteImpacto.OTHERS);
    indiceImpacto.setOtraFuenteImpacto("Otras-" + String.format("%03d", id));

    return indiceImpacto;
  }

  private Autor generarMockAutor(Long id, Long produccionCientificaId) {
    Autor autor = new Autor();
    autor.setId(id);
    autor.setProduccionCientificaId(produccionCientificaId);
    autor.setFirma("Firma-" + String.format("%03d", id));

    return autor;
  }

  private Proyecto generarMockProyecto(Long id, Long produccionCientificaId) {
    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setProduccionCientificaId(produccionCientificaId);
    proyecto.setProyectoRef(id);

    return proyecto;
  }

  private Acreditacion generarMockAcreditacion(Long id, Long produccionCientificaId) {
    Acreditacion acreditacion = new Acreditacion();
    acreditacion.setId(id);
    acreditacion.setProduccionCientificaId(produccionCientificaId);
    acreditacion.setUrl("url-" + String.format("%03d", id));

    return acreditacion;
  }
}
