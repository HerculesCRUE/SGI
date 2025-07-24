package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.MemoriaTitulo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.service.ComiteService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.framework.i18n.Language;
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

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * ComiteControllerTest
 */
@WebMvcTest(ComiteController.class)
public class ComiteControllerTest extends BaseControllerTest {

  @MockBean
  private ComiteService comiteService;

  @MockBean
  private MemoriaService memoriaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_ID_COMITE = "/{idComite}";
  private static final String PATH_PARAMETER_ID_PETICION_EVALUACION = "/{idPeticionEvaluacion}";
  private static final String COMITE_CONTROLLER_BASE_PATH = "/comites";

  /* Retorna una lista Comite y comprueba los datos */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void getComite_ReturnsComiteList() throws Exception {

    List<Comite> comiteLista = new ArrayList<>();
    Comite comite1 = new Comite();
    comite1.setId(1L);
    comite1.setCodigo("Comite1");
    comite1.setActivo(Boolean.TRUE);
    Comite comite2 = new Comite();
    comite2.setId(2L);
    comite2.setCodigo("Comite2");
    comite2.setActivo(Boolean.TRUE);

    comiteLista.add(comite1);
    comiteLista.add(comite2);

    BDDMockito.given(comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteLista));

    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))

        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].codigo").value("Comite1"))

        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].codigo").value("Comite2"));
  }

  /* Retorna una lista vacía */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void getComite_ReturnsEmptyList() throws Exception {

    List<Comite> comiteResponseList = new ArrayList<Comite>();

    BDDMockito.given(comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteResponseList));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /* Retorna un Comite por id y comprueba los datos */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-VER" })
  public void getComite_WithId_ReturnsComite() throws Exception {

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("Comite1");
    comite.setActivo(Boolean.TRUE);

    BDDMockito.given(comiteService.findById(ArgumentMatchers.anyLong())).willReturn(comite);

    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("codigo").value("Comite1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-VER" })
  public void getComite_NotFound_Returns404() throws Exception {
    BDDMockito.given(comiteService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ComiteNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /* Retorna lista paginada Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void findAll_WithPaging_ReturnsComiteSubList() throws Exception {

    // given: Cien Comite
    List<Comite> comiteList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Comite comite = new Comite();
      comite.setId(Long.valueOf(i));
      comite.setCodigo("Comite" + String.format("%03d", i));
      comite.setActivo(Boolean.TRUE);

      comiteList.add(comite);
    }

    BDDMockito.given(comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Comite>>() {
          @Override
          public Page<Comite> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Comite> content = comiteList.subList(fromIndex, toIndex);
            Page<Comite> page = new PageImpl<>(content, pageable, comiteList.size());
            return page;
          }
        });

    // when: Obtiene page=3 con pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: El Comite retorn la información de la página correcta en el
        // header
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // Usa TypeReference para informar Jackson sobre el tipo genérico de la lista
    List<Comite> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Comite>>() {
        });

    // Contiene Comite='Comite031' to 'Comite040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Comite comite = actual.get(i);
      Assertions.assertThat(comite.getCodigo()).isEqualTo("Comite" + String.format("%03d", j));
    }
  }

  /* Retorna lista filtrada Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredComiteList() throws Exception {

    // given: Dos Comite y una query

    List<Comite> comiteList = new ArrayList<>();
    Comite comite1 = new Comite();
    comite1.setId(1L);
    comite1.setCodigo("Comite1");
    comite1.setActivo(Boolean.TRUE);
    Comite comite2 = new Comite();
    comite2.setId(1L);
    comite2.setCodigo("Comite2");
    comite2.setActivo(Boolean.TRUE);

    comiteList.add(comite1);
    comiteList.add(comite2);

    String query = "comite~Comite%";

    BDDMockito.given(comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Comite>>() {
          @Override
          public Page<Comite> answer(InvocationOnMock invocation) throws Throwable {
            List<Comite> content = new ArrayList<>();
            for (Comite comite : comiteList) {
              if (comite.getCodigo().startsWith("Comite")) {
                content.add(comite);
              }
            }
            Page<Comite> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: Encuenta la búsqueda de la query
    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Obtiene la página
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  public void findMemoriasEmtyList() throws Exception {
    // given: El comité no tiene tipos de memoria asociados
    Long id = 3L;
    final String url = new StringBuffer(COMITE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID_COMITE)
        .append("/memorias-peticion-evaluacion").append(PATH_PARAMETER_ID_PETICION_EVALUACION).toString();

    BDDMockito.given(memoriaService.findAllMemoriasPeticionEvaluacionModificables(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id, 1L).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  public void findMemoriaSValid() throws Exception {
    // given: Datos existentes
    Long id = 3L;
    final String url = new StringBuffer(COMITE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID_COMITE)
        .append("/memorias-peticion-evaluacion").append(PATH_PARAMETER_ID_PETICION_EVALUACION).toString();

    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Memoria memoria = generarMockMemoria(Long.valueOf(i), "numRef-55" + String.valueOf(i),
          "Memoria" + String.format("%03d", i), 1);

      memorias.add(memoria);
    }

    BDDMockito.given(memoriaService.findAllMemoriasPeticionEvaluacionModificables(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Memoria>>() {
          @Override
          public Page<Memoria> answer(InvocationOnMock invocation) throws Throwable {
            List<Memoria> content = new ArrayList<>();
            for (Memoria memoria : memorias) {
              content.add(memoria);
            }
            return new PageImpl<>(content);
          }
        });
    // when: Se buscan todos los tipo memoria del comité
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id, 1L).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los tipo memoria relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
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
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("CEI");
    comite.setActivo(Boolean.TRUE);

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, titulo));
    Memoria memoria = new Memoria();
    memoria.setId(1L);
    memoria.setNumReferencia(numReferencia);
    memoria.setPeticionEvaluacion(peticionEvaluacion);
    memoria.setComite(comite);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-00" + id);
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(new TipoEstadoMemoria());
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.TRUE);
    memoria.setVersion(version);
    memoria.setActivo(Boolean.TRUE);

    return memoria;
  }
}