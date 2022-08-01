package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.service.ConfiguracionSolicitudService;
import org.crue.hercules.sgi.csp.service.DocumentoRequeridoSolicitudService;
import org.crue.hercules.sgi.csp.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
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
 * ConfiguracionSolicitudControllerTest
 */
@WebMvcTest(ConfiguracionSolicitudController.class)
class ConfiguracionSolicitudControllerTest extends BaseControllerTest {

  @MockBean
  private ConfiguracionSolicitudService service;
  @MockBean
  private DocumentoRequeridoSolicitudService documentoRequeridoSolicitudService;

  @MockBean
  private TipoDocumentoService tipoDocumentoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DOCUMENTOS = "/documentorequiridosolicitudes";
  private static final String CONTROLLER_BASE_PATH = "/convocatoria-configuracionsolicitudes";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void create_ReturnsConfiguracionSolicitud() throws Exception {
    // given: new ConfiguracionSolicitud
    ConfiguracionSolicitud newConfiguracionSolicitud = generarMockConfiguracionSolicitud(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConfiguracionSolicitud>any()))
        .willAnswer(new Answer<ConfiguracionSolicitud>() {
          @Override
          public ConfiguracionSolicitud answer(InvocationOnMock invocation) throws Throwable {
            ConfiguracionSolicitud givenData = invocation.getArgument(0, ConfiguracionSolicitud.class);
            ConfiguracionSolicitud newData = new ConfiguracionSolicitud();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ConfiguracionSolicitud
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newConfiguracionSolicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new ConfiguracionSolicitud is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(newConfiguracionSolicitud.getConvocatoriaId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("tramitacionSGI").value(newConfiguracionSolicitud.getTramitacionSGI()))
        .andExpect(MockMvcResultMatchers.jsonPath("fasePresentacionSolicitudes.id")
            .value(newConfiguracionSolicitud.getFasePresentacionSolicitudes().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeMaximoSolicitud")
            .value(newConfiguracionSolicitud.getImporteMaximoSolicitud()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-C" })
  void create_WithId_Returns400() throws Exception {
    // given: a ConfiguracionSolicitud with id filled
    ConfiguracionSolicitud newConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConfiguracionSolicitud>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConfiguracionSolicitud
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newConfiguracionSolicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithExistingId_ReturnsConfiguracionSolicitud() throws Exception {
    // given: existing ConfiguracionSolicitud
    ConfiguracionSolicitud configuracionSolicitudExistente = generarMockConfiguracionSolicitud(1L, 1L, 1L);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 2L);
    configuracionSolicitud.setTramitacionSGI(Boolean.FALSE);
    configuracionSolicitud.setImporteMaximoSolicitud(BigDecimal.valueOf(54321));

    BDDMockito.given(service.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(configuracionSolicitudExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ConfiguracionSolicitud>any(), ArgumentMatchers.anyLong()))
        .willReturn(configuracionSolicitud);

    // when: update ConfiguracionSolicitud
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, configuracionSolicitudExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(configuracionSolicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ConfiguracionSolicitud is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(configuracionSolicitudExistente.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("convocatoriaId").value(configuracionSolicitudExistente.getConvocatoriaId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tramitacionSGI").value(configuracionSolicitud.getTramitacionSGI()))
        .andExpect(MockMvcResultMatchers.jsonPath("fasePresentacionSolicitudes.id")
            .value(configuracionSolicitud.getFasePresentacionSolicitudes().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeMaximoSolicitud")
            .value(configuracionSolicitud.getImporteMaximoSolicitud()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void update_WithNoExistingId_Returns404() throws Exception {
    // given: a ConfiguracionSolicitud with non existing id
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 1L);

    BDDMockito.willThrow(new ConfiguracionSolicitudNotFoundException(configuracionSolicitud.getId())).given(service)
        .findByConvocatoriaId(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<ConfiguracionSolicitud>any(), ArgumentMatchers.anyLong()))
        .willThrow(new ConfiguracionSolicitudNotFoundException(configuracionSolicitud.getId()));

    // when: update ConfiguracionSolicitud
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, configuracionSolicitud.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(configuracionSolicitud)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findByConvocatoriaId_WithExistingId_ReturnsConfiguracionSolicitud() throws Exception {
    // given: existing convocatoriaId
    ConfiguracionSolicitud configuracionSolicitudExistente = generarMockConfiguracionSolicitud(2L, 1L, 1L);
    BDDMockito.given(service.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(configuracionSolicitudExistente);

    // when: find by existing convocatoriaId
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConfiguracionSolicitud is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(2L))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaId").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing convocatoriaId
    BDDMockito.given(service.findByConvocatoriaId(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaNotFoundException(1L);
    });

    // when: find by non existing convocatoriaId
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findById_WithNoExistingConfiguracionSolicitudByConvocatoria_Returns204() throws Exception {
    // given: no existing convocatoriaId
    BDDMockito.given(service.findByConvocatoriaId(ArgumentMatchers.<Long>any())).willReturn(null);

    // when: find by non existing convocatoriaId
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 204 No Content
        andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllDocumentoRequeridoSolicitud_ReturnsPage() throws Exception {
    // given: Una lista con 37 DocumentoRequeridoSolicitud para la Convocatoria
    Long convocatoriaId = 1L;

    List<DocumentoRequeridoSolicitud> convocatoriasEntidadesGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesGestoras.add(generarDocumentoRequeridoSolicitud(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(documentoRequeridoSolicitudService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<DocumentoRequeridoSolicitud>>() {
          @Override
          public Page<DocumentoRequeridoSolicitud> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasEntidadesGestoras.size() ? convocatoriasEntidadesGestoras.size() : toIndex;
            List<DocumentoRequeridoSolicitud> content = convocatoriasEntidadesGestoras.subList(fromIndex, toIndex);
            Page<DocumentoRequeridoSolicitud> page = new PageImpl<>(content, pageable,
                convocatoriasEntidadesGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DOCUMENTOS, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los DocumentoRequeridoSolicitud del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<DocumentoRequeridoSolicitud> doDocumentoRequeridoSolicitudResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<DocumentoRequeridoSolicitud>>() {
        });

    for (int i = 31; i <= 37; i++) {
      DocumentoRequeridoSolicitud DocumentoRequeridoSolicitud = doDocumentoRequeridoSolicitudResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(DocumentoRequeridoSolicitud.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V" })
  void findAllDocumentoRequeridoSolicitud_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de DocumentoRequeridoSolicitud para la Convocatoria
    Long convocatoriaId = 1L;
    List<DocumentoRequeridoSolicitud> convocatoriasEntidadesGestoras = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(documentoRequeridoSolicitudService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<DocumentoRequeridoSolicitud>>() {
          @Override
          public Page<DocumentoRequeridoSolicitud> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<DocumentoRequeridoSolicitud> page = new PageImpl<>(convocatoriasEntidadesGestoras, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DOCUMENTOS, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private ModeloEjecucion generarMockModeloEjecucion(Long modeloEjecucionId) {
    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .id(modeloEjecucionId)
        .nombre("nombreModeloEjecucion-1")
        .descripcion("descripcionModeloEjecucion-1")
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return modeloEjecucion;
  }

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoriaId
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId, Long convocatoriaId,
      Long convocatoriaFaseId) {

    // @formatter:off
    TipoFase tipoFase = TipoFase.builder()
        .id(convocatoriaFaseId)
        .nombre("nombre-1")
        .activo(Boolean.TRUE)
        .build();

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .id(convocatoriaFaseId)
        .convocatoriaId(convocatoriaId)
        .tipoFase(tipoFase)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-15T00:00:00Z"))
        .observaciones("observaciones")
        .build();

    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .id(configuracionSolicitudId)
        .convocatoriaId(convocatoriaId)
        .tramitacionSGI(Boolean.TRUE)
        .fasePresentacionSolicitudes(convocatoriaFase)
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))
        .build();
    // @formatter:on

    return configuracionSolicitud;
  }

  /**
   * Genera un objeto generarDocumentoRequeridoSolicitud
   * 
   * @param documentoRequeridoSolicitudId
   * @return
   */
  private DocumentoRequeridoSolicitud generarDocumentoRequeridoSolicitud(Long documentoRequeridoSolicitudId) {
    // @formatter:off
    TipoFase tipoFase = TipoFase.builder()
        .id(1L)
        .nombre("nombre-1")
        .activo(Boolean.TRUE)
        .build();

    ModeloTipoFase.builder()
        .id(1L)
        .modeloEjecucion(generarMockModeloEjecucion(1L))
        .tipoFase(tipoFase)
        .solicitud(Boolean.TRUE)
        .convocatoria(Boolean.TRUE)
        .proyecto(Boolean.TRUE)
        .activo(Boolean.TRUE)
        .build();

    TipoDocumento tipoDocumento = TipoDocumento.builder()
        .id(1L)
        .nombre("nombre-1")
        .activo(Boolean.TRUE)
        .build();

    return DocumentoRequeridoSolicitud.builder()
        .id(documentoRequeridoSolicitudId)
        .configuracionSolicitudId(1L)
        .tipoDocumento(tipoDocumento)
        .observaciones("observaciones-" + documentoRequeridoSolicitudId)
        .build();
    // @formatter:on

  }
}