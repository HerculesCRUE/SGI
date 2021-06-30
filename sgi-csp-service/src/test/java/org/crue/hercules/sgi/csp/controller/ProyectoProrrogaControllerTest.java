package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoProrrogaNotFoundException;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoProrrogaService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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
 * ProyectoProrrogaControllerTest
 */
@WebMvcTest(ProyectoProrrogaController.class)
public class ProyectoProrrogaControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoProrrogaService service;
  @MockBean
  private ProrrogaDocumentoService prorrogaDocumentoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyecto-prorrogas";
  private static final String PATH_ENTIDAD_DOCUMENTO = "/prorrogadocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_ReturnsProyectoProrroga() throws Exception {
    // given: new ProyectoProrroga
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setId(null);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoProrroga>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoProrroga newProyectoProrroga = new ProyectoProrroga();
          BeanUtils.copyProperties(invocation.getArgument(0), newProyectoProrroga);
          newProyectoProrroga.setId(1L);
          return newProyectoProrroga;
        });

    // when: create ProyectoProrroga
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoProrroga)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProyectoProrroga is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoProrroga.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("numProrroga").value(proyectoProrroga.getNumProrroga()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaConcesion").value(proyectoProrroga.getFechaConcesion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipo").value(proyectoProrroga.getTipo().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value(proyectoProrroga.getFechaFin().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("importe").value(proyectoProrroga.getImporte()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(proyectoProrroga.getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ProyectoProrroga with id filled
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoProrroga>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ProyectoProrroga
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoProrroga)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyectoProrroga() throws Exception {
    // given: Existing ProyectoProrroga to be updated
    ProyectoProrroga proyectoProrrogaExistente = generarMockProyectoProrroga(1L, 1L);
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");

    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoProrroga>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProyectoProrroga
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoProrrogaExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoProrroga)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoProrroga is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoProrrogaExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoProrrogaExistente.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("numProrroga").value(proyectoProrrogaExistente.getNumProrroga()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaConcesion")
            .value(proyectoProrrogaExistente.getFechaConcesion().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipo").value(proyectoProrrogaExistente.getTipo().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value(proyectoProrrogaExistente.getFechaFin().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("importe").value(proyectoProrrogaExistente.getImporte()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(proyectoProrroga.getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);

    BDDMockito.willThrow(new ProyectoProrrogaNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProyectoProrroga>any());

    // when: update ProyectoProrroga
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoProrroga)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.doNothing().when(service).delete(ArgumentMatchers.anyLong());

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(service, Mockito.times(1)).delete(ArgumentMatchers.anyLong());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoProrrogaNotFoundException(id)).given(service).delete(ArgumentMatchers.<Long>any());

    // when: delete by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void existsById_WithExistingId_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 200 OK
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void existsById_WithNoExistingId_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findById_WithExistingId_ReturnsProyectoProrroga() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoProrroga(invocation.getArgument(0), 1L);
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoProrroga is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("numProrroga").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaConcesion").value("2020-01-01T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-12-31T23:59:59Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("importe").value(BigDecimal.valueOf(123.45)))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones")
            .value("observaciones-proyecto-prorroga-" + String.format("%03d", id)));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoProrrogaNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * 
   * PRORROGA DOCUMENTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProrrogaDocumento_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProrrogaDocumento para la Prorroga
    Long proyectoProrrogaId = 1L;

    List<ProrrogaDocumento> prorrogaDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      prorrogaDocumentos.add(generarMockProrrogaDocumento(i, proyectoProrrogaId, 1L));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(prorrogaDocumentoService.findAllByProyectoProrroga(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProrrogaDocumento>>() {
          @Override
          public Page<ProrrogaDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > prorrogaDocumentos.size() ? prorrogaDocumentos.size() : toIndex;
            List<ProrrogaDocumento> content = prorrogaDocumentos.subList(fromIndex, toIndex);
            Page<ProrrogaDocumento> page = new PageImpl<>(content, pageable, prorrogaDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_DOCUMENTO, proyectoProrrogaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProrrogaDocumento del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProrrogaDocumento> prorrogaDocumentoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ProrrogaDocumento>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProrrogaDocumento prorrogaDocumento = prorrogaDocumentoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(prorrogaDocumento.getDocumentoRef()).isEqualTo("documentoRef-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProrrogaDocumento_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProrrogaDocumento para la Prorroga
    Long proyectoProrrogaId = 1L;
    List<ProrrogaDocumento> prorrogaDocumentos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(prorrogaDocumentoService.findAllByProyectoProrroga(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProrrogaDocumento>>() {
          @Override
          public Page<ProrrogaDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProrrogaDocumento> page = new PageImpl<>(prorrogaDocumentos, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_DOCUMENTO, proyectoProrrogaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ProyectoProrroga
   * 
   * @param id         id del ProyectoProrroga
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoProrroga
   */
  private ProyectoProrroga generarMockProyectoProrroga(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoProrroga.builder()
        .id(id)
        .proyectoId(proyectoId)
        .numProrroga(1)
        .fechaConcesion(Instant.parse("2020-01-01T00:00:00Z"))
        .tipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE)
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .importe(BigDecimal.valueOf(123.45))
        .observaciones("observaciones-proyecto-prorroga-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ProrrogaDocumento
   * 
   * @param id                 id del ProrrogaDocumento
   * @param proyectoProrrogaId id del ProyectoProrroga
   * @return el objeto ProrrogaDocumento
   */
  private ProrrogaDocumento generarMockProrrogaDocumento(Long id, Long proyectoProrrogaId, Long tipoDocumentoId) {

    // @formatter:off
    return ProrrogaDocumento.builder()
        .id(id)
        .proyectoProrrogaId(proyectoProrrogaId)
        .nombre("prorroga-documento-" + (id == null ? "" : String.format("%03d", id)))
        .documentoRef("documentoRef-" + (id == null ? "" : String.format("%03d", id)))
        .tipoDocumento(TipoDocumento.builder().id(tipoDocumentoId).build())
        .comentario("comentario-prorroga-documento-" + (id == null ? "" : String.format("%03d", id)))
        .visible(Boolean.TRUE)
        .build();
    // @formatter:on
  }

}
