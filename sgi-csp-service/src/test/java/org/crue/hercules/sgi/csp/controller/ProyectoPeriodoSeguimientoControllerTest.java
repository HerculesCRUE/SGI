package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoSeguimientoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.csp.service.TipoHitoService;
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
 * ProyectoPeriodoSeguimientoControllerTest
 */
@WebMvcTest(ProyectoPeriodoSeguimientoController.class)
public class ProyectoPeriodoSeguimientoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoPeriodoSeguimientoService service;

  @MockBean
  private TipoHitoService tipoHitoService;

  @MockBean
  private ProyectoPeriodoSeguimientoDocumentoService proyectoPeriodoSeguimientoDocumentoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoperiodoseguimientos";
  private static final String PATH_DOCUMENTO = "/proyectoperiodoseguimientodocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_ReturnsProyectoPeriodoSeguimiento() throws Exception {
    // given: new ProyectoPeriodoSeguimiento
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoPeriodoSeguimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoPeriodoSeguimiento newProyectoPeriodoSeguimiento = new ProyectoPeriodoSeguimiento();
          BeanUtils.copyProperties(invocation.getArgument(0), newProyectoPeriodoSeguimiento);
          newProyectoPeriodoSeguimiento.setId(1L);
          return newProyectoPeriodoSeguimiento;
        });

    // when: create ProyectoPeriodoSeguimiento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoPeriodoSeguimiento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProyectoPeriodoSeguimiento is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoPeriodoSeguimiento.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones")
            .value("obs-" + String.format("%03d", proyectoPeriodoSeguimiento.getId())));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ProyectoPeriodoSeguimiento with id filled
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoPeriodoSeguimiento>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ProyectoPeriodoSeguimiento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoPeriodoSeguimiento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyectoPeriodoSeguimiento() throws Exception {
    // given: Existing ProyectoPeriodoSeguimiento to be updated
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoExistente = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);

    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoPeriodoSeguimiento>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProyectoPeriodoSeguimiento
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoPeriodoSeguimientoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoPeriodoSeguimiento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoPeriodoSeguimiento is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoPeriodoSeguimientoExistente.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("proyectoId").value(proyectoPeriodoSeguimientoExistente.getProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones")
            .value("obs-" + String.format("%03d", proyectoPeriodoSeguimiento.getId())));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);

    BDDMockito.willThrow(new ProyectoPeriodoSeguimientoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProyectoPeriodoSeguimiento>any());

    // when: update ProyectoPeriodoSeguimiento
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoPeriodoSeguimiento)))
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

    BDDMockito.willThrow(new ProyectoPeriodoSeguimientoNotFoundException(id)).given(service)
        .delete(ArgumentMatchers.<Long>any());

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
  public void findById_WithExistingId_ReturnsProyectoPeriodoSeguimiento() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoPeriodoSeguimiento(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoPeriodoSeguimiento is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("obs-001"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoPeriodoSeguimientoNotFoundException(1L);
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
   * PROYECTO PERIODO SEGUIMIENTO DOCUMENTO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoPeriodoSeguimientoDocumento_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoPeriodoSeguimiento para el Proyecto
    Long proyectoId = 1L;

    List<ProyectoPeriodoSeguimientoDocumento> proyectoPeriodoSeguimientoDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoPeriodoSeguimientoDocumentos.add(generarMockProyectoPeriodoSeguimientoDocumento(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoPeriodoSeguimientoDocumentoService.findAllByProyectoPeriodoSeguimiento(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoSeguimientoDocumento>>() {
          @Override
          public Page<ProyectoPeriodoSeguimientoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoPeriodoSeguimientoDocumentos.size()
                ? proyectoPeriodoSeguimientoDocumentos.size()
                : toIndex;
            List<ProyectoPeriodoSeguimientoDocumento> content = proyectoPeriodoSeguimientoDocumentos.subList(fromIndex,
                toIndex);
            Page<ProyectoPeriodoSeguimientoDocumento> page = new PageImpl<>(content, pageable,
                proyectoPeriodoSeguimientoDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DOCUMENTO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoPeriodoSeguimiento del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ProyectoPeriodoSeguimientoDocumento> proyectoPeriodoSeguimientoDocumentoResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoPeriodoSeguimientoDocumento>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = proyectoPeriodoSeguimientoDocumentoResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getComentario())
          .isEqualTo("comentario-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllProyectoPeriodoSeguimientoDocumento_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ProyectoPeriodoSeguimientoDocumento para la
    // Proyecto
    Long proyectoId = 1L;
    List<ProyectoPeriodoSeguimientoDocumento> proyectoPeriodoSeguimientoDocumentos = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(proyectoPeriodoSeguimientoDocumentoService.findAllByProyectoPeriodoSeguimiento(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoSeguimientoDocumento>>() {
          @Override
          public Page<ProyectoPeriodoSeguimientoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProyectoPeriodoSeguimientoDocumento> page = new PageImpl<>(proyectoPeriodoSeguimientoDocumentos,
                pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DOCUMENTO, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void existsDocumentosById_WithExistingId_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito
        .given(
            proyectoPeriodoSeguimientoDocumentoService.existsByProyectoPeriodoSeguimiento(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.TRUE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DOCUMENTO, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 200 OK
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void existsDocumentosById_WithNoExistingId_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito
        .given(
            proyectoPeriodoSeguimientoDocumentoService.existsByProyectoPeriodoSeguimiento(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.FALSE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DOCUMENTO, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimiento
   * 
   * @param id id del ProyectoPeriodoSeguimiento
   * @return el objeto ProyectoPeriodoSeguimiento
   */
  private ProyectoPeriodoSeguimiento generarMockProyectoPeriodoSeguimiento(Long id) {
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = new ProyectoPeriodoSeguimiento();
    proyectoPeriodoSeguimiento.setId(id);
    proyectoPeriodoSeguimiento.setProyectoId(id == null ? 1 : id);
    proyectoPeriodoSeguimiento.setNumPeriodo(1);
    proyectoPeriodoSeguimiento.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoPeriodoSeguimiento.setFechaFin(Instant.parse("2020-12-19T23:59:59Z"));
    proyectoPeriodoSeguimiento.setObservaciones("obs-" + String.format("%03d", (id != null ? id : 1)));

    return proyectoPeriodoSeguimiento;
  }

  private ProyectoPeriodoSeguimientoDocumento generarMockProyectoPeriodoSeguimientoDocumento(Long id) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId((id != null ? id : 1));
    tipoDocumento.setNombre("TipoDocumento" + (id != null ? id : 1));
    tipoDocumento.setDescripcion("descripcion-" + (id != null ? id : 1));
    tipoDocumento.setActivo(Boolean.TRUE);

    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = new ProyectoPeriodoSeguimientoDocumento();
    proyectoPeriodoSeguimientoDocumento.setId(id);
    proyectoPeriodoSeguimientoDocumento.setProyectoPeriodoSeguimientoId(id == null ? 1 : id);
    proyectoPeriodoSeguimientoDocumento.setNombre("Nombre-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setDocumentoRef("Doc-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setComentario("comentario-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setTipoDocumento(tipoDocumento);
    proyectoPeriodoSeguimientoDocumento.setVisible(Boolean.TRUE);

    return proyectoPeriodoSeguimientoDocumento;
  }

}
