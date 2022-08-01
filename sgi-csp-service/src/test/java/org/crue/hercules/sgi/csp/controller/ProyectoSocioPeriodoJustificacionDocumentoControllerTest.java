package org.crue.hercules.sgi.csp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionDocumentoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ProyectoSocioPeriodoJustificacionDocumentoControllerTest
 */
@WebMvcTest(ProyectoSocioPeriodoJustificacionDocumentoController.class)
class ProyectoSocioPeriodoJustificacionDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoSocioPeriodoJustificacionDocumentoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectosocioperiodojustificaciondocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void update_ReturnsProyectoSocioPeriodoJustificacionDocumentoList() throws Exception {
    // given: una lista con uno de los ProyectoSocioPeriodoJustificacionDocumento
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacionDocumento newProyectoSocioPeriodoJustificacionDocumento = generarMockProyectoSocioPeriodoJustificacionDocumento(
        null);
    ProyectoSocioPeriodoJustificacionDocumento updatedProyectoSocioPeriodoJustificacionDocumento = generarMockProyectoSocioPeriodoJustificacionDocumento(
        4L);

    List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificacionDocumento = Arrays
        .asList(updatedProyectoSocioPeriodoJustificacionDocumento, newProyectoSocioPeriodoJustificacionDocumento);

    BDDMockito
        .given(service.update(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<ProyectoSocioPeriodoJustificacionDocumento>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ProyectoSocioPeriodoJustificacionDocumento> periodoJustificaciones = invocation.getArgument(1);
          return periodoJustificaciones.stream().map(periodoJustificacion -> {
            if (periodoJustificacion.getId() == null) {
              periodoJustificacion.setId(5L);
            }
            periodoJustificacion.setProyectoSocioPeriodoJustificacionId(proyectoSocioPeriodoJustificacionId);
            return periodoJustificacion;
          }).collect(Collectors.toList());
        });

    // when: updateProyectoSocioPeriodoJustificacionDocumentoesProyectoSocio
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoSocioPeriodoJustificacionId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(proyectoSocioPeriodoJustificacionDocumento)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se crea el nuevo ProyectoSocioPeriodoJustificacionDocumento, se
        // actualiza el
        // existe y se eliminan el resto
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].id").value(proyectoSocioPeriodoJustificacionDocumento.get(0).getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(0).getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].comentario")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(0).getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].documentoRef")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(0).getDocumentoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].tipoDocumento.id")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(0).getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].visible")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(0).getVisible()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(5L))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].nombre")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(1).getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].comentario")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(1).getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].documentoRef")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(1).getDocumentoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].tipoDocumento.id")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(1).getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].visible")
            .value(proyectoSocioPeriodoJustificacionDocumento.get(1).getVisible()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void updateProyectoSocioPeriodoJustificacionDocumentoesProyectoSocio_WithNoExistingId_Returns404()
      throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumento = generarMockProyectoSocioPeriodoJustificacionDocumento(
        1L);

    BDDMockito.willThrow(new ProyectoSocioPeriodoJustificacionDocumentoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.anyLong(), ArgumentMatchers.<ProyectoSocioPeriodoJustificacionDocumento>anyList());

    // when: updateProyectoSocioPeriodoJustificacionDocumentoesProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Arrays.asList(proyectoSocioPeriodoJustificacionDocumento))))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithExistingId_ReturnsProyectoSocioPeriodoJustificacionDocumento() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoSocioPeriodoJustificacionDocumento(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoSocioPeriodoJustificacionDocumento is resturned as
        // JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value("comentario"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoSocioPeriodoJustificacionDocumentoNotFoundException(1L);
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
   * Función que devuelve un objeto ProyectoSocioPeriodoJustificacionDocumento
   * 
   * @param id id del ProyectoSocioPeriodoJustificacionDocumento
   * 
   * @return el objeto ProyectoSocioPeriodoJustificacionDocumento
   */
  private ProyectoSocioPeriodoJustificacionDocumento generarMockProyectoSocioPeriodoJustificacionDocumento(Long id) {
    TipoDocumento tipoDocumento = TipoDocumento.builder().id(1L).nombre("tipo1").activo(Boolean.TRUE).build();
    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumento = ProyectoSocioPeriodoJustificacionDocumento
        .builder().id(id).nombre("nombre-" + id).comentario("comentario").documentoRef("001")
        .proyectoSocioPeriodoJustificacionId(1L).tipoDocumento(tipoDocumento).visible(Boolean.TRUE).build();
    return proyectoSocioPeriodoJustificacionDocumento;
  }

}
