package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.InformeNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.service.InformeService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * InformeControllerTest
 */
@WebMvcTest(InformeController.class)
public class InformeControllerTest extends BaseControllerTest {

  @MockBean
  private InformeService informeService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String INFORME_CONTROLLER_BASE_PATH = "/informeformularios";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void getInforme_WithId_ReturnsInforme() throws Exception {
    BDDMockito.given(informeService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockInforme(1L, "Documento1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("Documento1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void getInforme_NotFound_Returns404() throws Exception {
    BDDMockito.given(informeService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new InformeNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void newInforme_ReturnsInforme() throws Exception {
    // given: Un informe nuevo
    String nuevoInformeJson = "{\"documentoRef\": \"Documento1\", \"version\": \"2\", \"idTipoEvaluacion\": \"1\"}";

    Informe informe = generarMockInforme(1L, "Documento1");

    BDDMockito.given(informeService.create(ArgumentMatchers.<Informe>any())).willReturn(informe);

    // when: Creamos un informe
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(INFORME_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoInformeJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo informe y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("Documento1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void newInforme_Error_Returns400() throws Exception {
    // given: Un informe nuevo que produce un error al crearse
    String nuevoInformeJson = "{\"documentoRef\": \"Documento1\", \"version\": \"2\", \"idTipoEvaluacion\": \"1\"}";

    BDDMockito.given(informeService.create(ArgumentMatchers.<Informe>any())).willThrow(new IllegalArgumentException());

    // when: Creamos un informe
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(INFORME_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoInformeJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void replaceInforme_ReturnsInforme() throws Exception {
    // given: Un informe a modificar
    String replaceInformeJson = "{\"id\": 1, \"documentoRef\": \"Documento1\", \"version\": \"2\", \"idTipoEvaluacion\": \"1\"}";

    Informe informe = generarMockInforme(1L, "Replace Documento1");

    BDDMockito.given(informeService.update(ArgumentMatchers.<Informe>any())).willReturn(informe);

    mockMvc
        .perform(MockMvcRequestBuilders.put(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceInformeJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el informe y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("Replace Documento1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void replaceInforme_NotFound() throws Exception {
    // given: Un informe a modificar
    String replaceInformeJson = "{\"id\": 1, \"documentoRef\": \"Documento1\", \"version\": \"2\", \"idTipoEvaluacion\": \"1\"}";

    BDDMockito.given(informeService.update(ArgumentMatchers.<Informe>any())).will((InvocationOnMock invocation) -> {
      throw new InformeNotFoundException(((Informe) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceInformeJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void removeInforme_ReturnsOk() throws Exception {
    BDDMockito.given(informeService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockInforme(1L, "Documento1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(INFORME_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void findAll_Unlimited_ReturnsFullInformeList() throws Exception {
    // given: One hundred Informe
    List<Informe> informes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informes.add(generarMockInforme(Long.valueOf(i), "Documento" + String.format("%03d", i)));
    }

    BDDMockito.given(informeService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(informes));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(INFORME_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void findAll_WithPaging_ReturnsInformeSubList() throws Exception {
    // given: One hundred Informe
    List<Informe> informes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informes.add(generarMockInforme(Long.valueOf(i), "Documento" + String.format("%03d", i)));
    }

    BDDMockito.given(informeService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Informe>>() {
          @Override
          public Page<Informe> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Informe> content = informes.subList(fromIndex, toIndex);
            Page<Informe> page = new PageImpl<>(content, pageable, informes.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(INFORME_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked Informes are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Informe> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Informe>>() {
        });

    // containing documentoRef='Documento031' to 'Documento040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Informe informe = actual.get(i);
      Assertions.assertThat(informe.getDocumentoRef()).isEqualTo("Documento" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredInformeList() throws Exception {
    // given: One hundred Informe and a search query
    List<Informe> informes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informes.add(generarMockInforme(Long.valueOf(i), "Documento" + String.format("%03d", i)));
    }
    String query = "documentoRef~Documento%,id:5";

    BDDMockito.given(informeService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Informe>>() {
          @Override
          public Page<Informe> answer(InvocationOnMock invocation) throws Throwable {
            List<Informe> content = new ArrayList<>();
            for (Informe informe : informes) {
              if (informe.getDocumentoRef().startsWith("Documento") && informe.getId().equals(5L)) {
                content.add(informe);
              }
            }
            Page<Informe> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(INFORME_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred Informe
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: Informe empty
    List<Informe> informes = new ArrayList<>();

    BDDMockito.given(informeService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(informes));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(INFORME_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve error No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto Informe
   * 
   * @param id           id del Informe
   * @param documentoRef la referencia del documento
   * @return el objeto Informe
   */

  public Informe generarMockInforme(Long id, String documentoRef) {
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
    tipoMemoria.setId(id);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + id, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), Instant.now()), 3,
        "CodOrganoCompetente", Boolean.TRUE, null);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setActivo(true);
    tipoEvaluacion.setNombre("Memoria");

    Informe informe = new Informe();
    informe.setId(id);
    informe.setDocumentoRef(documentoRef);
    informe.setMemoria(memoria);
    informe.setVersion(3);
    informe.setTipoEvaluacion(tipoEvaluacion);

    return informe;
  }

}
