package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.ArrayList;

import org.crue.hercules.sgi.csp.dto.SolicitudHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoAvisoInput.Destinatario;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoInput;
import org.crue.hercules.sgi.csp.exceptions.SolicitudHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudHitoAviso;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.SolicitudHitoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
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
 * SolicitudHitoTest
 */
@WebMvcTest(SolicitudHitoController.class)
class SolicitudHitoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudHitoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudhitos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_ReturnsSolicitudHito() throws Exception {
    // given: new SolicitudHito
    SolicitudHitoInput solicitudHitoInput = generarSolicitudHitoInput();

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudHitoInput>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return generarSolicitudHito(1L);
        });

    // when: create SolicitudHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudHitoInput)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new SolicitudHito is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value("comentario"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.tareaProgramadaRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.comunicadoRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsSolicitud").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithId_Returns400() throws Exception {
    // given: a SolicitudHito with id filled
    SolicitudHitoInput solicitudHitoInput = generarSolicitudHitoInput();

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudHitoInput>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudHitoInput)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithExistingId_ReturnsSolicitudHito() throws Exception {
    // given: existing SolicitudHito
    SolicitudHito solicitudHito = generarSolicitudHito(1L);
    SolicitudHitoInput updatedSolicitudHito = generarSolicitudHitoInput();

    BDDMockito.given(service.update(
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<SolicitudHitoInput>any()))
        .willAnswer((InvocationOnMock invocation) -> solicitudHito);

    // when: update SolicitudHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudHito.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: SolicitudHito is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(solicitudHito.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("solicitudId").value(solicitudHito.getSolicitudId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value(updatedSolicitudHito.getFecha().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value(updatedSolicitudHito.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(updatedSolicitudHito.getTipoHitoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.tareaProgramadaRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.comunicadoRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsSolicitud").value(
            updatedSolicitudHito.getAviso().getIncluirIpsSolicitud()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudHito with non existing id
    Long id = 1L;
    SolicitudHitoInput updatedSolicitudHito = generarSolicitudHitoInput();

    BDDMockito.willThrow(new SolicitudHitoNotFoundException(id)).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<Long>any(), ArgumentMatchers.<SolicitudHitoInput>any()))
        .willThrow(new SolicitudHitoNotFoundException(id));

    // when: update SolicitudHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void delete_WithExistingId_Return204() throws Exception {
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
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudHitoNotFoundException(id)).given(service).delete(ArgumentMatchers.<Long>any());

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
  void findById_WithExistingId_ReturnsSolicitudHito() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarSolicitudHito(id);
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudHito is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value("comentario"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.tareaProgramadaRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.comunicadoRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsSolicitud").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudHitoNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudHito
   * 
   * @param solicitudHitoId
   * @param solicitudId
   * @param tipoDocumentoId
   * @return el objeto SolicitudHito
   */
  private SolicitudHito generarSolicitudHito(Long id) {

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id == null ? 1 : id);
    tipoHito.setActivo(true);

    SolicitudHito solicitudHito = new SolicitudHito();
    solicitudHito.setId(id);
    solicitudHito.setSolicitudId(id == null ? 1 : id);
    solicitudHito.setFecha(Instant.parse("2020-10-19T00:00:00Z"));
    solicitudHito.setComentario("comentario");
    solicitudHito.setSolicitudHitoAviso(new SolicitudHitoAviso(
        id == null ? 1 : id, id == null ? "1" : id.toString(), id == null ? "1" : id.toString(), false));
    solicitudHito.setTipoHito(tipoHito);

    return solicitudHito;
  }

  private SolicitudHitoInput generarSolicitudHitoInput() {

    SolicitudHitoAvisoInput aviso = new SolicitudHitoAvisoInput();
    aviso.setFechaEnvio(Instant.parse("2020-10-19T00:00:00Z"));
    aviso.setAsunto("Asunto");
    aviso.setContenido("Contenido");
    aviso.setDestinatarios(new ArrayList<>());
    aviso.getDestinatarios().add(new Destinatario("test", "test@test.com"));
    aviso.setIncluirIpsSolicitud(false);

    SolicitudHitoInput convocatoriaHito = new SolicitudHitoInput();
    convocatoriaHito.setSolicitudId(1L);
    convocatoriaHito.setTipoHitoId(1L);
    convocatoriaHito.setFecha(Instant.parse("2020-10-19T00:00:00Z"));
    convocatoriaHito.setComentario("comentario");
    convocatoriaHito.setAviso(aviso);

    return convocatoriaHito;
  }
}
