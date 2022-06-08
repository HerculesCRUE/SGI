package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.custom.CustomConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.InformeService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.RespuestaService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * MemoriaControllerTest
 */
@WebMvcTest(MemoriaController.class)
public class MemoriaControllerTest extends BaseControllerTest {

  @MockBean
  private MemoriaService memoriaService;

  @MockBean
  private EvaluacionService evaluacionService;

  @MockBean
  private InformeService informeFormularioService;

  @MockBean
  private DocumentacionMemoriaService documentacionMemoriaService;

  @MockBean
  private CustomConvocatoriaReunionRepository convocatoriaReunionRepository;

  @MockBean
  private RespuestaService respuestaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_ASIGNABLES = "/asignables/{idConvocatoria}";
  private static final String PATH_PARAMETER_ASIGNABLES_ORDEXT = "/tipo-convocatoria-ord-ext";
  private static final String PATH_PARAMETER_ASIGNABLES_SEG = "/tipo-convocatoria-seg";
  private static final String MEMORIA_CONTROLLER_BASE_PATH = "/memorias";
  private static final String PATH_PARAMETER_BY_DOCUMENTACION = "/documentaciones";
  private static final String PATH_PARAMETER_EVALUACIONES = "/evaluaciones";
  private static final String PATH_PARAMETER_ENVIAR_SECRETARIA = "/enviar-secretaria";
  private static final String PATH_PARAMETER_ENVIAR_SECRETARIA_RETROSPECTIVA = "/enviar-secretaria-retrospectiva";
  private static final String PATH_PARAMETER_PERSONA = "/persona";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-V", "ETI-MEM-INV-VR" })
  public void getMemoria_WithId_ReturnsMemoria() throws Exception {
    BDDMockito.given(memoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockMemoria(1L, "numRef-5598", "Memoria1", 1)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value("Memoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-V", "ETI-MEM-INV-VR" })
  public void getMemoria_NotFound_Returns404() throws Exception {
    BDDMockito.given(memoriaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new MemoriaNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void newMemoria_ReturnsMemoria() throws Exception {
    // given: Una memoria nueva
    String nuevaMemoriaJson = mapper.writeValueAsString(generarMockMemoria(null, "numRef-5599", "Memoria1", 1));

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1);

    BDDMockito.given(memoriaService.create(ArgumentMatchers.<Memoria>any())).willReturn(memoria);

    // when: Creamos una memoria
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(MEMORIA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevaMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value("Memoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void newMemoria_Error_Returns400() throws Exception {
    // given: Una memoria nueva que produce un error al crearse
    String nuevaMemoriaJson = "{\"numReferencia\": \"numRef-5599\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"19/06/2020\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}";

    BDDMockito.given(memoriaService.create(ArgumentMatchers.<Memoria>any())).willThrow(new IllegalArgumentException());

    // when: Creamos una memoria
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(MEMORIA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevaMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void newMemoriaModificada_ReturnsMemoria() throws Exception {
    // given: Una memoria nueva
    String nuevaMemoriaJson = mapper.writeValueAsString(generarMockMemoria(null, "numRef-5599", "Memoria1", 1));

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1);

    BDDMockito.given(memoriaService.createModificada(ArgumentMatchers.<Memoria>any(), ArgumentMatchers.anyLong()))
        .willReturn(memoria);

    // when: Creamos una memoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/crear-memoria-modificada", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value("Memoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void newMemoriaModificada_Error_Returns400() throws Exception {
    // given: Una memoria nueva que produce un error al crearse
    String nuevaMemoriaJson = "{\"numReferencia\": \"numRef-5599\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"19/06/2020\","
        + "\"tipoMemoria\": {\"id\": 2, \"nombre\": \"Modificada\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}";

    BDDMockito.given(memoriaService.createModificada(ArgumentMatchers.<Memoria>any(), ArgumentMatchers.anyLong()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos una memoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/crear-memoria-modificada", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void replaceMemoria_ReturnsMemoria() throws Exception {
    // given: Una memoria a modificar
    String replaceMemoriaJson = mapper.writeValueAsString(generarMockMemoria(1L, "numRef-5599", "Memoria1", 1));

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1);

    BDDMockito.given(memoriaService.update(ArgumentMatchers.<Memoria>any())).willReturn(memoria);

    mockMvc
        .perform(MockMvcRequestBuilders.put(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica la memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value("Memoria1"))
        .andExpect(MockMvcResultMatchers.jsonPath("numReferencia").value("numRef-5598"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void replaceMemoria_NotFound() throws Exception {
    // given: Una memoria a modificar
    String replaceMemoriaJson = mapper.writeValueAsString(generarMockMemoria(1L, "numRef-5599", "Memoria1", 1));

    BDDMockito.given(memoriaService.update(ArgumentMatchers.<Memoria>any())).will((InvocationOnMock invocation) -> {
      throw new MemoriaNotFoundException(((Memoria) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-BR" })
  public void removeMemoria_ReturnsOk() throws Exception {
    BDDMockito.given(memoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockMemoria(1L, "numRef-5598", "Memoria1", 1));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void deleteDocumentacionSeguimientoAnual_ReturnsOk() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID
                + "/documentacion-seguimiento-anual/{idDocumentacionMemoria}", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void deleteDocumentacionSeguimientoFinal_ReturnsOk() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID
                + "/documentacion-seguimiento-final/{idDocumentacionMemoria}", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void deleteDocumentacionRetrospectiva_ReturnsOk() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID
                + "/documentacion-retrospectiva/{idDocumentacionMemoria}", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void deleteDocumentacionInicial_ReturnsOk() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders
                .delete(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID
                    + "/documentacion-inicial/{idDocumentacionMemoria}", 1L, 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test

  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-VR", "ETI-MEM-V" })
  public void findAll_Unlimited_ReturnsFullMemoriaList() throws Exception { // given: One hundred Memoria

    List<MemoriaPeticionEvaluacion> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i)));
    }

    BDDMockito.given(memoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(memorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-VR", "ETI-MEM-V" })
  public void findAll_WithPaging_ReturnsMemoriaSubList() throws Exception {
    // given: One hundred Memoria
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i));
    }

    BDDMockito.given(memoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Memoria>>() {
          @Override
          public Page<Memoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Memoria> content = memorias.subList(fromIndex, toIndex);
            Page<Memoria> page = new PageImpl<>(content, pageable, memorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Memorias are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Memoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Memoria>>() {
        });

    // containing titulo='Memoria031' to 'Memoria040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Memoria memoria = actual.get(i);
      Assertions.assertThat(memoria.getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-VR", "ETI-MEM-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredMemoriaList() throws Exception {
    // given: One hundred Memoria and a search query
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i));
    }
    String query = "titulo~Memoria%,id:5";

    BDDMockito.given(memoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Memoria>>() {
          @Override
          public Page<Memoria> answer(InvocationOnMock invocation) throws Throwable {
            List<Memoria> content = new ArrayList<>();
            for (Memoria memoria : memorias) {
              if (memoria.getTitulo().startsWith("Memoria") && memoria.getId().equals(5L)) {
                content.add(memoria);
              }
            }
            Page<Memoria> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Memoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-VR", "ETI-MEM-V", "ETI-MEM-E" })
  public void findAll_ReturnsNoContent() throws Exception { // given: One hundred Memoria
    // given: Memorias empty
    List<MemoriaPeticionEvaluacion> memorias = new ArrayList<>();

    BDDMockito.given(memoriaService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(memorias));
    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        // then: Devuelve error No Content
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR" })
  public void getEvaluacionesEmptyList() throws Exception {
    // given: Existe la memoria pero no tiene evaluaciones
    Long idMemoria = 3L;
    Long idEvaluacion = 1L;
    Long idTipoComentario = 1L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/evaluaciones-anteriores").append("/{idEvaluacion}").append("/{idTipoComentario}").toString();

    BDDMockito
        .given(evaluacionService.findEvaluacionesAnterioresByMemoria(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, idMemoria, idEvaluacion, idTipoComentario)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-EVALR" })
  public void getEvaluacionesValid() throws Exception {
    // given: Datos existentes con evaluacion
    Long idMemoria = 3L;
    Long idEvaluacion = 1L;
    Long idTipoComentario = 1L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/evaluaciones-anteriores").append("/{idEvaluacion}").append("/{idTipoComentario}").toString();

    List<EvaluacionWithNumComentario> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      EvaluacionWithNumComentario evaluacionComentario = new EvaluacionWithNumComentario(
          generarMockEvaluacion(Long.valueOf(i), "" + i), Long.valueOf(i));
      evaluaciones.add(evaluacionComentario);
    }

    BDDMockito
        .given(evaluacionService.findEvaluacionesAnterioresByMemoria(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EvaluacionWithNumComentario>>() {
          @Override
          public Page<EvaluacionWithNumComentario> answer(InvocationOnMock invocation) throws Throwable {
            List<EvaluacionWithNumComentario> content = new ArrayList<>();
            for (EvaluacionWithNumComentario evaluacion : evaluaciones) {
              content.add(evaluacion);
            }
            Page<EvaluacionWithNumComentario> page = new PageImpl<>(content);
            return page;
          }
        });
    // when: Se buscan todos las evaluaciones de esa memoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, idMemoria, idEvaluacion, idTipoComentario)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos las evaluaciones relacionadas
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllMemoriasAsignablesConvocatoria_Unlimited_ReturnsFullMemoriaList() throws Exception {
    // given: idConvocatoria, One hundred Memoria
    Long idConvocatoria = 1L;
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i));
    }

    BDDMockito.given(memoriaService.findAllMemoriasAsignablesConvocatoria(ArgumentMatchers.anyLong()))
        .willReturn(memorias);

    // when: find unlimited asignables by convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, idConvocatoria)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Memoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllMemoriasAsignablesConvocatoria_WithPaging_ReturnsMemoriaSubList() throws Exception {
    // given: idConvocatoria, One hundred Memoria
    Long idConvocatoria = 1L;
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i));
    }

    BDDMockito.given(memoriaService.findAllMemoriasAsignablesConvocatoria(ArgumentMatchers.anyLong()))
        .willReturn(memorias);

    // when: get page=3 with pagesize=10 asignables by convocatoria
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, idConvocatoria)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Memorias are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Memoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Memoria>>() {
        });

    for (int i = 0, j = 1; i < 10; i++, j++) {
      Memoria memoria = actual.get(i);
      Assertions.assertThat(memoria.getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-E" })
  public void findAllMemoriasAsignablesConvocatoria_ReturnsNoContent() throws Exception {
    // given: idConvocatoria, Memorias empty
    Long idConvocatoria = 1L;
    List<Memoria> memorias = new ArrayList<>();

    BDDMockito.given(memoriaService.findAllMemoriasAsignablesConvocatoria(ArgumentMatchers.anyLong()))
        .willReturn(memorias);

    // when: find unlimited asignables by convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES, idConvocatoria)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllAsignablesTipoConvocatoriaOrdExt_Unlimited_ReturnsFullMemoriaList() throws Exception {

    // given: search query with comité y fecha límite de una convocatoria de tipo
    // ordinario o extraordinario
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i));
    }
    // String query = "comite.id:1,fechaLimite<:2020-09-10";

    BDDMockito.given(memoriaService.findAllAsignablesTipoConvocatoriaOrdExt(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(memorias));

    // when: find unlimited asignables para tipo convocatoria ordinaria o
    // extraordinaria
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES_ORDEXT)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Obtiene las
        // memorias en estado "En secretaria" con la fecha de envío es igual o menor a
        // la fecha límite de la convocatoria de reunión y las que tengan una
        // retrospectiva en estado "En secretaría".
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C" })
  public void findAllAsignablesTipoConvocatoriaOrdExt_ReturnsNoContent() throws Exception {
    // given: Memorias empty
    List<Memoria> memorias = new ArrayList<>();

    BDDMockito.given(memoriaService.findAllAsignablesTipoConvocatoriaOrdExt(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(memorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES_ORDEXT)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C", "ETI-CNV-E" })
  public void findAllAsignablesTipoConvocatoriaSeguimiento_Unlimited_ReturnsFullMemoriaList() throws Exception {

    // given: search query with comité y fecha límite de una convocatoria de tipo
    // seguimiento
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i));
    }
    // String query = "comite.id:1,fechaLimite<:2020-09-10";

    BDDMockito.given(memoriaService.findAllAsignablesTipoConvocatoriaSeguimiento(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(memorias));

    // when: find unlimited asignables para tipo convocatoria seguimiento
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES_SEG)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Obtiene Memorias en estado
        // "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
        // fecha de envío es igual o menor a la fecha límite de la convocatoria de
        // reunión.
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNV-C" })
  public void findAllAsignablesTipoConvocatoriaSeguimiento_ReturnsNoContent() throws Exception {
    // given: Memorias empty
    List<Memoria> memorias = new ArrayList<>();

    BDDMockito.given(memoriaService.findAllAsignablesTipoConvocatoriaSeguimiento(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(memorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ASIGNABLES_SEG)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getDocumentacionFormularioEmptyList() throws Exception {
    // given: Existe la memoria pero no tiene documentacion
    Long id = 3L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/documentacion-formulario").toString();

    BDDMockito.given(documentacionMemoriaService.findDocumentacionMemoria(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getDocumentacionFormularioValid() throws Exception {
    // given: Datos existentes con memoria
    Long id = 3L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/documentacion-formulario").toString();

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Memoria memoria = generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i);
      DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(Long.valueOf(i), memoria,
          tipoDocumento);
      documentacionMemorias.add(documentacionMemoria);
    }

    BDDMockito.given(documentacionMemoriaService.findDocumentacionMemoria(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<DocumentacionMemoria>>() {
          @Override
          public Page<DocumentacionMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<DocumentacionMemoria> content = new ArrayList<>();
            for (DocumentacionMemoria documentacion : documentacionMemorias) {
              content.add(documentacion);
            }
            return new PageImpl<>(content);
          }
        });
    // when: Se buscan todos los documentos de esa memoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los documentos relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getDocumentacionSeguimientoAnualEmptyList() throws Exception {
    // given: Existe la memoria pero no tiene documentacion
    Long id = 3L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/documentacion-seguimiento-anual").toString();

    BDDMockito.given(documentacionMemoriaService.findDocumentacionSeguimientoAnual(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getDocumentacionSeguimientoAnualValid() throws Exception {
    // given: Datos existentes con memoria
    Long id = 3L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/documentacion-seguimiento-anual").toString();

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Memoria memoria = generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i);
      DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(Long.valueOf(i), memoria,
          tipoDocumento);
      documentacionMemorias.add(documentacionMemoria);
    }

    BDDMockito.given(documentacionMemoriaService.findDocumentacionSeguimientoAnual(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<DocumentacionMemoria>>() {
          @Override
          public Page<DocumentacionMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<DocumentacionMemoria> content = new ArrayList<>();
            for (DocumentacionMemoria documentacion : documentacionMemorias) {
              content.add(documentacion);
            }
            return new PageImpl<>(content);
          }
        });
    // when: Se buscan todos los documentos de esa memoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los documentos relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getDocumentacionSeguimientoFinalEmptyList() throws Exception {
    // given: Existe la memoria pero no tiene documentacion
    Long id = 3L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/documentacion-seguimiento-final").toString();

    BDDMockito.given(documentacionMemoriaService.findDocumentacionSeguimientoFinal(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getDocumentacionSeguimientoFinalValid() throws Exception {
    // given: Datos existentes con memoria
    Long id = 3L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/documentacion-seguimiento-final").toString();

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Memoria memoria = generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i);
      DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(Long.valueOf(i), memoria,
          tipoDocumento);
      documentacionMemorias.add(documentacionMemoria);
    }

    BDDMockito.given(documentacionMemoriaService.findDocumentacionSeguimientoFinal(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<DocumentacionMemoria>>() {
          @Override
          public Page<DocumentacionMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<DocumentacionMemoria> content = new ArrayList<>();
            for (DocumentacionMemoria documentacion : documentacionMemorias) {
              content.add(documentacion);
            }
            return new PageImpl<>(content);
          }
        });
    // when: Se buscan todos los documentos de esa memoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los documentos relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getDocumentacionRetrospectivaEmptyList() throws Exception {
    // given: Existe la memoria pero no tiene documentacion
    Long id = 3L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/documentacion-retrospectiva").toString();

    BDDMockito.given(documentacionMemoriaService.findDocumentacionRetrospectiva(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getDocumentacionRetrospectivaValid() throws Exception {
    // given: Datos existentes con memoria
    Long id = 3L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/documentacion-retrospectiva").toString();

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Memoria memoria = generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i);
      DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(Long.valueOf(i), memoria,
          tipoDocumento);
      documentacionMemorias.add(documentacionMemoria);
    }

    BDDMockito.given(documentacionMemoriaService.findDocumentacionRetrospectiva(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<DocumentacionMemoria>>() {
          @Override
          public Page<DocumentacionMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<DocumentacionMemoria> content = new ArrayList<>();
            for (DocumentacionMemoria documentacion : documentacionMemorias) {
              content.add(documentacion);
            }
            return new PageImpl<>(content);
          }
        });
    // when: Se buscan todos los documentos de esa memoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los documentos relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void newDocumentacionMemoriaInicial_ReturnsMemoria() throws Exception {
    // given: Un documentación memoria nueva
    String nuevaDocumentacionMemoriaJson = mapper.writeValueAsString(generarMockDocumentacionMemoria(1L,
        generarMockMemoria(null, "001", "memoria1", 1), generarMockTipoDocumento(1L)));

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "memoria1", 1), generarMockTipoDocumento(1L));

    BDDMockito
        .given(documentacionMemoriaService.createDocumentacionInicial(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<DocumentacionMemoria>any(), ArgumentMatchers.<Authentication>any()))
        .willReturn(documentacionMemoria);

    // when: Creamos una memoria
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-inicial", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(nuevaDocumentacionMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("memoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void getEvaluacionesMemoria_ReturnsList() throws Exception {
    // given: Existen 100 evaluaciones
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }
    BDDMockito.given(evaluacionService.findAllByMemoriaId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(evaluaciones));

    // when: las recupero sin paginación
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_EVALUACIONES, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: obtengo un listado de 100 conflictos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void newDocumentacionMemoriaSeguimientoAnual_ReturnsMemoria() throws Exception {
    // given: Un documentación memoria nueva
    String nuevaDocumentacionMemoriaJson = mapper.writeValueAsString(generarMockDocumentacionMemoria(1L,
        generarMockMemoria(null, "001", "memoria1", 1), generarMockTipoDocumento(1L)));

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "memoria1", 1), generarMockTipoDocumento(1L));

    BDDMockito.given(documentacionMemoriaService.createSeguimientoAnual(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<DocumentacionMemoria>any())).willReturn(documentacionMemoria);

    // when: Creamos una memoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-anual", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaDocumentacionMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("memoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void newDocumentacionMemoriaSeguimientoFinal_ReturnsMemoria() throws Exception {
    // given: Un documentación memoria nueva
    String nuevaDocumentacionMemoriaJson = mapper.writeValueAsString(generarMockDocumentacionMemoria(1L,
        generarMockMemoria(null, "001", "memoria1", 1), generarMockTipoDocumento(1L)));

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "memoria1", 1), generarMockTipoDocumento(1L));

    BDDMockito.given(documentacionMemoriaService.createSeguimientoFinal(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<DocumentacionMemoria>any())).willReturn(documentacionMemoria);

    // when: Creamos una memoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-seguimiento-final", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaDocumentacionMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("memoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ER" })
  public void newDocumentacionMemoriaRetrospectiva_ReturnsMemoria() throws Exception {
    // given: Un documentación memoria nueva
    String nuevaDocumentacionMemoriaJson = mapper.writeValueAsString(generarMockDocumentacionMemoria(1L,
        generarMockMemoria(null, "001", "memoria1", 1), generarMockTipoDocumento(1L)));

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L,
        generarMockMemoria(1L, "001", "memoria1", 1), generarMockTipoDocumento(1L));

    BDDMockito.given(documentacionMemoriaService.createRetrospectiva(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<DocumentacionMemoria>any())).willReturn(documentacionMemoria);

    // when: Creamos una memoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/documentacion-retrospectiva", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaDocumentacionMemoriaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo tipo memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("memoria1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ESCR" })
  public void enviarSecretaria_WithId() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders
            .put(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_ENVIAR_SECRETARIA, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ERTR" })
  public void enviarSecretariaRetrospectiva_WithId() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders
            .put(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_ENVIAR_SECRETARIA_RETROSPECTIVA, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-VR" })
  public void findAllMemoriasEvaluacionByPersonaRef_Unlimited_ReturnsFullMemoriaPeticionEvaluacionList()
      throws Exception {
    // given: One hundred Memoria
    List<MemoriaPeticionEvaluacion> memoriasPeticionEvaluacion = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memoriasPeticionEvaluacion.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i), "54333933",
          "numRef-55" + String.valueOf(i), "Memoria" + String.format("%03d", i)));
    }

    BDDMockito
        .given(memoriaService.findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willReturn(new PageImpl<>(memoriasPeticionEvaluacion));

    // when: find unlimited asignables by convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_PERSONA)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Memoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-VR", "ETI-MEM-V", "ETI-MEM-INV-CR" })
  public void findAllMemoriasEvaluacionByPersonaRef_WithPaging_ReturnsMemoriaPeticionEvaluacionSubList()
      throws Exception {
    // given: One hundred Memoria
    List<MemoriaPeticionEvaluacion> memoriasPeticionEvaluacion = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memoriasPeticionEvaluacion.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i),
          "45532234", "numRef-55" + String.valueOf(i), "Memoria" + String.format("%03d", i)));
    }
    BDDMockito
        .given(memoriaService.findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willAnswer(new Answer<Page<MemoriaPeticionEvaluacion>>() {
          @Override
          public Page<MemoriaPeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<MemoriaPeticionEvaluacion> content = memoriasPeticionEvaluacion.subList(fromIndex, toIndex);
            Page<MemoriaPeticionEvaluacion> page = new PageImpl<>(content, pageable, memoriasPeticionEvaluacion.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10 asignables by convocatoria
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_PERSONA)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Memorias are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Memoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Memoria>>() {
        });

    // containing titulo='Memoria031' to 'Memoria040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Memoria memoria = actual.get(i);
      Assertions.assertThat(memoria.getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-VR" })
  public void findAllMemoriasEvaluacionByPersonaRef_ReturnsNoContent() throws Exception {
    // given: Memorias empty
    List<MemoriaPeticionEvaluacion> memoriasPeticionEvaluacion = new ArrayList<>();

    BDDMockito
        .given(memoriaService.findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willReturn(new PageImpl<>(memoriasPeticionEvaluacion));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_PERSONA)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-EVC-VR", "ETI-EVC-INV-VR", "ETI-EVC-EVAL",
      "ETI-EVC-EVALR", "ETI-EVC-INV-EVALR" })
  public void getDocumentacionesTipoEvaluacionValid() throws Exception {
    // given: Datos existentes de memoria con documentacion
    Long idMemoria = 3L;
    Long idTipoEvaluacion = 1L;

    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_BY_DOCUMENTACION).append("/{idTipoEvaluacion}").toString();

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Memoria memoria = generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), i);
      DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(Long.valueOf(i), memoria,
          tipoDocumento);
      documentacionMemorias.add(documentacionMemoria);
    }

    BDDMockito
        .given(documentacionMemoriaService.findByMemoriaIdAndTipoEvaluacion(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<TipoEvaluacion.Tipo>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<DocumentacionMemoria>>() {
          @Override
          public Page<DocumentacionMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<DocumentacionMemoria> content = new ArrayList<>();
            for (DocumentacionMemoria evaluacion : documentacionMemorias) {
              content.add(evaluacion);
            }
            Page<DocumentacionMemoria> page = new PageImpl<>(content);
            return page;
          }
        });
    // when: Se buscan todos los datos
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, idMemoria, idTipoEvaluacion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los documentos relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-EVC-VR", "ETI-EVC-INV-VR", "ETI-EVC-EVAL",
      "ETI-EVC-EVALR", "ETI-EVC-INV-EVALR" })
  public void getDocumentacionesTipoEvaluacion_ReturnsNoContent() throws Exception {
    // given: Documentacion empty
    Long idMemoria = 3L;
    Long idTipoEvaluacion = 1L;
    final String url = new StringBuffer(MEMORIA_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append(PATH_PARAMETER_BY_DOCUMENTACION).append("/{idTipoEvaluacion}").toString();

    BDDMockito
        .given(documentacionMemoriaService.findByMemoriaIdAndTipoEvaluacion(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<TipoEvaluacion.Tipo>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, idMemoria, idTipoEvaluacion)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto Memoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto tipo Memoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version) {

    return new Memoria(id, numReferencia, generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id),
        generarMockComite(id, "comite" + id, true), titulo, "user-00" + id,
        generarMockTipoMemoria(1L, "TipoMemoria1", true),
        generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.TRUE,
        generarMockRetrospectiva(1L), version, "CodOrganoCompetente", Boolean.TRUE, null);
  }

  /**
   * Función que devuelve un objeto Memoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto tipo Memoria
   */

  private MemoriaPeticionEvaluacion generarMockMemoriaPeticionEvaluacion(Long id, String responsableRef,
      String numReferencia, String titulo) {

    return new MemoriaPeticionEvaluacion(id, responsableRef, numReferencia, titulo,
        generarMockComite(id, "comite" + id, true),
        generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), false, null,
        Instant.parse("2020-05-15T00:00:00Z"), Instant.now(), false, true, "1111");
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos" + id);
    peticionEvaluacion.setResumen("Resumen" + id);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    return new Comite(id, comite, "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto", "articulo",
        formulario, activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nombre.
   * @param activo indicador de activo.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria.
   * 
   * @param id     identificador del TipoEstadoMemoria.
   * @param nombre nombre.
   * @param activo indicador de activo.
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoEstadoMemoria(id, nombre, activo);

  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(generarMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
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
    peticionEvaluacion.setExterno(Boolean.FALSE);
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
    Comite comite = new Comite(1L, "Comite1", "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto",
        "articulo", formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), Instant.now()), 3,
        "CodOrganoCompetente", Boolean.TRUE, null);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now());
    convocatoriaReunion.setFechaLimite(Instant.now());
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

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setVersion(2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

  /**
   * Función que devuelve un objeto DocumentacionMemoria
   * 
   * @param id            id de DocumentacionMemoria
   * @param memoria       la Memoria de DocumentacionMemoria
   * @param tipoDocumento el TipoDocumento de DocumentacionMemoria
   * @return el objeto DocumentacionMemoria
   */

  private DocumentacionMemoria generarMockDocumentacionMemoria(Long id, Memoria memoria, TipoDocumento tipoDocumento) {

    DocumentacionMemoria documentacionMemoria = new DocumentacionMemoria();
    documentacionMemoria.setId(id);
    documentacionMemoria.setMemoria(memoria);
    documentacionMemoria.setTipoDocumento(tipoDocumento);
    documentacionMemoria.setDocumentoRef("doc-00" + id);
    documentacionMemoria.setNombre("doc-00" + id);

    return documentacionMemoria;
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */

  public TipoDocumento generarMockTipoDocumento(Long id) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre("TipoDocumento" + id);

    return tipoDocumento;
  }

  /**
   * Crea una memoria de petición evaluación.
   * 
   * @param id identificador.
   */
  private MemoriaPeticionEvaluacion generarMockMemoriaPeticionEvaluacion(Long id) {

    MemoriaPeticionEvaluacion memoria = new MemoriaPeticionEvaluacion();
    memoria.setId(id);

    Comite comite = new Comite();
    comite.setId(id);
    comite.setGenero(Genero.M);
    memoria.setComite(comite);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(id);
    memoria.setEstadoActual(tipoEstadoMemoria);

    memoria.setFechaEvaluacion(Instant.parse("2020-05-15T00:00:00Z"));
    memoria.setFechaLimite(Instant.parse("2020-08-18T23:59:59Z"));
    return memoria;
  }

  public Informe generarMockInforme(Long id, Memoria memoria) {
    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setActivo(true);
    tipoEvaluacion.setNombre("Memoria");

    Informe informe = new Informe();
    informe.setId(id);
    informe.setDocumentoRef("TipoDocumento" + id);
    informe.setMemoria(memoria);
    informe.setTipoEvaluacion(tipoEvaluacion);

    return informe;
  }

}
