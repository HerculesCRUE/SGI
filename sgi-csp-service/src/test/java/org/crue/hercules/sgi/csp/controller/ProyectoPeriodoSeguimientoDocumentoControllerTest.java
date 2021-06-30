package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoSeguimientoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoDocumentoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ProyectoPeriodoSeguimientoDocumentoControllerTest
 */

@WebMvcTest(ProyectoPeriodoSeguimientoDocumentoController.class)
public class ProyectoPeriodoSeguimientoDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoPeriodoSeguimientoDocumentoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoperiodoseguimientodocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {
    // given: new ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        null);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoPeriodoSeguimientoDocumento>any()))
        .willAnswer(new Answer<ProyectoPeriodoSeguimientoDocumento>() {
          @Override
          public ProyectoPeriodoSeguimientoDocumento answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoSeguimientoDocumento givenData = invocation.getArgument(0,
                ProyectoPeriodoSeguimientoDocumento.class);
            ProyectoPeriodoSeguimientoDocumento newData = new ProyectoPeriodoSeguimientoDocumento();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ProyectoPeriodoSeguimientoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoPeriodoSeguimientoDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ProyectoPeriodoSeguimientoDocumento is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoPeriodoSeguimientoId")
            .value(proyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id")
            .value(proyectoPeriodoSeguimientoDocumento.getTipoDocumento().getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("comentario").value(proyectoPeriodoSeguimientoDocumento.getComentario()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("documentoRef").value(proyectoPeriodoSeguimientoDocumento.getDocumentoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(proyectoPeriodoSeguimientoDocumento.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ProyectoPeriodoSeguimientoDocumento with id filled
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoPeriodoSeguimientoDocumento>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ProyectoPeriodoSeguimientoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoPeriodoSeguimientoDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithExistingId_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {
    // given: existing ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento updatedProyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);
    updatedProyectoPeriodoSeguimientoDocumento.setDocumentoRef("documentoRef-modificado");

    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoPeriodoSeguimientoDocumento>any()))
        .willAnswer(new Answer<ProyectoPeriodoSeguimientoDocumento>() {
          @Override
          public ProyectoPeriodoSeguimientoDocumento answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoSeguimientoDocumento givenData = invocation.getArgument(0,
                ProyectoPeriodoSeguimientoDocumento.class);
            return givenData;
          }
        });

    // when: update ProyectoPeriodoSeguimientoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedProyectoPeriodoSeguimientoDocumento.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updatedProyectoPeriodoSeguimientoDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoPeriodoSeguimientoDocumento is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(updatedProyectoPeriodoSeguimientoDocumento.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoPeriodoSeguimientoId")
            .value(updatedProyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id")
            .value(updatedProyectoPeriodoSeguimientoDocumento.getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario")
            .value(updatedProyectoPeriodoSeguimientoDocumento.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef")
            .value(updatedProyectoPeriodoSeguimientoDocumento.getDocumentoRef()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("nombre").value(updatedProyectoPeriodoSeguimientoDocumento.getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a ProyectoPeriodoSeguimientoDocumento with non existing id
    ProyectoPeriodoSeguimientoDocumento updatedProyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);

    BDDMockito.willThrow(
        new ProyectoPeriodoSeguimientoDocumentoNotFoundException(updatedProyectoPeriodoSeguimientoDocumento.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoPeriodoSeguimientoDocumento>any())).willThrow(
        new ProyectoPeriodoSeguimientoDocumentoNotFoundException(updatedProyectoPeriodoSeguimientoDocumento.getId()));

    // when: update ProyectoPeriodoSeguimientoDocumento
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedProyectoPeriodoSeguimientoDocumento.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updatedProyectoPeriodoSeguimientoDocumento)))
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
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoPeriodoSeguimientoDocumentoNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithExistingId_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<ProyectoPeriodoSeguimientoDocumento>() {
          @Override
          public ProyectoPeriodoSeguimientoDocumento answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return generarMockProyectoPeriodoSeguimientoDocumento(id);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoPeriodoSeguimientoDocumento is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoPeriodoSeguimientoDocumentoNotFoundException(id);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimientoDocumento
   * 
   * @param id id del ProyectoPeriodoSeguimientoDocumento
   * @return el objeto ProyectoPeriodoSeguimientoDocumento
   */
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
