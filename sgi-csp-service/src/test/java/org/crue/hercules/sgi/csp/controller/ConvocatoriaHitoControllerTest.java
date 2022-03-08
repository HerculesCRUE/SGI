package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.ArrayList;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoAvisoInput.Destinatario;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoInput;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoAviso;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.ConvocatoriaHitoService;
import org.crue.hercules.sgi.csp.service.TipoHitoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConvocatoriaHitoControllerTest
 */
@WebMvcTest(ConvocatoriaHitoController.class)
public class ConvocatoriaHitoControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaHitoService service;

  @MockBean
  private TipoHitoService tipoHitoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriahitos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  public void create_ReturnsConvocatoriaHito() throws Exception {
    // given: new ConvocatoriaHito

    ConvocatoriaHitoInput convocatoriaHitoInput = generarMockConvocatoriaHitoInput();

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaHitoInput>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return generarMockConvocatoriaHito(1L);
        });

    // when: create ConvocatoriaHito
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(
                convocatoriaHitoInput)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConvocatoriaHito is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value("comentario"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.tareaProgramadaRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.comunicadoRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsSolicitud").value(false))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsProyecto").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_ReturnsConvocatoriaHito() throws Exception {
    // given: Existing ConvocatoriaHito to be updated
    ConvocatoriaHito convocatoriaHitoExistente = generarMockConvocatoriaHito(1L);
    ConvocatoriaHitoInput convocatoriaHito = generarMockConvocatoriaHitoInput();

    BDDMockito.given(service.update(
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<ConvocatoriaHitoInput>any()))
        .willAnswer((InvocationOnMock invocation) -> convocatoriaHitoExistente);

    // when: update ConvocatoriaHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaHitoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConvocatoriaHito is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaHitoExistente.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(convocatoriaHitoExistente.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value(convocatoriaHito.getFecha().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value(convocatoriaHito.getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(convocatoriaHito.getTipoHitoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.tareaProgramadaRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.comunicadoRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsSolicitud").value(
            convocatoriaHito.getAviso().getIncluirIpsSolicitud()))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsProyecto")
            .value(convocatoriaHito.getAviso().getIncluirIpsProyecto()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaHitoInput convocatoriaHito = generarMockConvocatoriaHitoInput();

    BDDMockito.willThrow(new ConvocatoriaHitoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<Long>any(), ArgumentMatchers.<ConvocatoriaHitoInput>any());

    // when: update ConvocatoriaHito
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaHito)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
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
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaHitoNotFoundException(id)).given(service).delete(ArgumentMatchers.<Long>any());

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
  public void findById_WithExistingId_ReturnsConvocatoriaHito() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConvocatoriaHito(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaHito is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("fecha").value("2020-10-19T00:00:00Z"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value("comentario"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoHito.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.tareaProgramadaRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.comunicadoRef").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsSolicitud").value(false))
        .andExpect(MockMvcResultMatchers.jsonPath("aviso.incluirIpsProyecto").value(false));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaHitoNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  private ConvocatoriaHito generarMockConvocatoriaHito(Long id) {
    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id == null ? 1 : id);
    tipoHito.setActivo(true);

    ConvocatoriaHito convocatoriaHito = new ConvocatoriaHito();
    convocatoriaHito.setId(id);
    convocatoriaHito.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaHito.setFecha(Instant.parse("2020-10-19T00:00:00Z"));
    convocatoriaHito.setComentario("comentario");
    convocatoriaHito.setConvocatoriaHitoAviso(new ConvocatoriaHitoAviso(
        id == null ? 1 : id, id == null ? "1" : id.toString(), id == null ? "1" : id.toString(), false, false));
    convocatoriaHito.setTipoHito(tipoHito);

    return convocatoriaHito;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaHito
   * 
   * @param id id del ConvocatoriaHito
   * @return el objeto ConvocatoriaHito
   */
  private ConvocatoriaHitoInput generarMockConvocatoriaHitoInput() {

    ConvocatoriaHitoAvisoInput aviso = new ConvocatoriaHitoAvisoInput();
    aviso.setFechaEnvio(Instant.parse("2020-10-19T00:00:00Z"));
    aviso.setAsunto("Asunto");
    aviso.setContenido("Contenido");
    aviso.setDestinatarios(new ArrayList<>());
    aviso.getDestinatarios().add(new Destinatario("test", "test@test.com"));
    aviso.setIncluirIpsProyecto(false);
    aviso.setIncluirIpsSolicitud(false);

    ConvocatoriaHitoInput convocatoriaHito = new ConvocatoriaHitoInput();
    convocatoriaHito.setConvocatoriaId(1L);
    convocatoriaHito.setTipoHitoId(1L);
    convocatoriaHito.setFecha(Instant.parse("2020-10-19T00:00:00Z"));
    convocatoriaHito.setComentario("comentario");
    convocatoriaHito.setAviso(aviso);

    return convocatoriaHito;
  }

}
