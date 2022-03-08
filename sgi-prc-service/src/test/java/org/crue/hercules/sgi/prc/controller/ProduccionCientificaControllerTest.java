package org.crue.hercules.sgi.prc.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.prc.dto.AcreditacionOutput;
import org.crue.hercules.sgi.prc.dto.AutorOutput;
import org.crue.hercules.sgi.prc.dto.IndiceImpactoOutput;
import org.crue.hercules.sgi.prc.dto.ProyectoOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
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
  private static final String PUBLICACIONES_PATH = CONTROLLER_BASE_PATH + "/publicaciones";
  private static final String PATH_PARAMETER_VALIDAR = "/validar";
  private static final String PATH_INDICES_IMPACTO = ProduccionCientificaController.PATH_INDICES_IMPACTO;
  private static final String PATH_PROYECTOS = ProduccionCientificaController.PATH_PROYECTOS;
  private static final String PATH_ACREDITACIONES = ProduccionCientificaController.PATH_ACREDITACIONES;
  private static final String PATH_AUTORES = ProduccionCientificaController.PATH_AUTORES;
  private static Long estadoProduccionCientificaId = 0L;

  // @Test
  // @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  public void findAllPublicaciones_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProduccionCientifica
    List<ProduccionCientifica> produccionCientificas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      produccionCientificas.add(generarMockProduccionCientifica(i,
          "ProduccionCientifica" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > produccionCientificas.size() ? produccionCientificas.size() : toIndex;
          List<ProduccionCientifica> content = produccionCientificas.subList(fromIndex, toIndex);
          Page<ProduccionCientifica> pageResponse = new PageImpl<>(content, pageable,
              produccionCientificas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(PUBLICACIONES_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProduccionCientifica del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<PublicacionResumen> produccionCientificasResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<PublicacionResumen>>() {
        });

    for (int i = 31; i <= 37; i++) {
      PublicacionResumen produccionCientifica = produccionCientificasResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(produccionCientifica.getProduccionCientificaRef()).isEqualTo("ProduccionCientifica"
          + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V", "PRC-VAL-E" })
  public void findById_WithExistingId_ReturnsProduccionCientifica() throws Exception {
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
  public void findById_WithNoExistingId_Returns404() throws Exception {
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
  public void validar_WithExistingId_ReturnProduccionCientifica() throws Exception {
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
  public void validar_NoExistingId_Return404() throws Exception {
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
  public void rechazar_WithExistingId_ReturnProduccionCientifica() throws Exception {
    // given: existing id
    ProduccionCientifica produccionCientifica = generarMockProduccionCientifica(1L,
        "ProduccionCientifica" + String.format("%03d", 1));

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
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_VALIDAR, produccionCientifica.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Proyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("estado.estado").value(TipoEstadoProduccion.RECHAZADO.toString()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-E" })
  public void rechazar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProduccionCientificaNotFoundException(id.toString())).given(service).cambiarEstado(
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<TipoEstadoProduccion>any(),
        ArgumentMatchers.<String>any());

    // when: rechazar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_VALIDAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
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
  public void findIndicesImpacto_ReturnsPage() throws Exception {
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
  public void findIndicesImpacto_EmptyList_Returns204() throws Exception {
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
  public void findAutores_ReturnsPage() throws Exception {
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
  public void findAutores_EmptyList_Returns204() throws Exception {
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
  public void findProyectos_ReturnsPage() throws Exception {
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
  public void findProyectos_EmptyList_Returns204() throws Exception {
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
  public void findAcreditaciones_ReturnsPage() throws Exception {
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
  public void findAcreditaciones_EmptyList_Returns204() throws Exception {
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
