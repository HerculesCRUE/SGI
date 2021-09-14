package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.service.ComiteService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.TipoMemoriaComiteService;
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
 * ComiteControllerTest
 */
@WebMvcTest(ComiteController.class)
public class ComiteControllerTest extends BaseControllerTest {

  @MockBean
  private ComiteService comiteService;

  @MockBean
  private TipoMemoriaComiteService tipoMemoriaComiteService;

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

    Formulario formulario1 = new Formulario(1L, "M10", "Descripcion");
    comiteLista.add(new Comite(1L, "Comite1", formulario1, Boolean.TRUE));
    Formulario formulario2 = new Formulario(2L, "M20", "Descripcion");
    comiteLista.add(new Comite(2L, "Comite2", formulario2, Boolean.TRUE));

    BDDMockito.given(comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteLista));

    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))

        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].comite").value("Comite1"))

        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].comite").value("Comite2"));
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

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    BDDMockito.given(comiteService.findById(ArgumentMatchers.anyLong()))
        .willReturn((new Comite(1L, "Comite1", formulario, Boolean.TRUE)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value("Comite1"));
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

  /* Crear Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void addComite_ReturnsComite() throws Exception {

    // given: Un Comite nuevo
    String nuevoComiteJson = "{\"comite\": \"Comite1\",\"formulario\": {\"id\": 1,\"nombre\": \"M10\",\"descripcion\": \"Descripcion\"}}";

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setFormulario(new Formulario(1L, "M10", "Descripcion"));
    comite.setComite("Comite1");

    BDDMockito.given(comiteService.create(ArgumentMatchers.<Comite>any())).willReturn(comite);

    // when: Creamos un Comite

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(COMITE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoComiteJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo Comite y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value("Comite1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void addComite_Error_Returns400() throws Exception {
    // given: Un Comite nuevo que produce un error al crearse
    String nuevoComiteJson = "{\"id\": 1, \"comite\": \"Comite1\"}";

    BDDMockito.given(comiteService.create(ArgumentMatchers.<Comite>any())).willThrow(new IllegalArgumentException());

    // when: Creamos un Comite
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(COMITE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoComiteJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())

        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  /* Modificar un Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void replaceComite_ReturnsComite() throws Exception {
    // given: Un Comite a modificar
    String replaceComiteJson = "{\"comite\": \"Comite1\",\"formulario\": {\"id\": 1,\"nombre\": \"M10\",\"descripcion\": \"Descripcion\"}}";

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setFormulario(new Formulario(1L, "M10", "Descripcion"));
    comite.setComite("Replace Comite1");

    BDDMockito.given(comiteService.update(ArgumentMatchers.<Comite>any())).willReturn(comite);

    mockMvc
        .perform(MockMvcRequestBuilders.put(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceComiteJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el Comite y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value("Replace Comite1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void replaceComite_NotFound() throws Exception {
    // given: Un Comite a modificar
    String replaceComiteJson = "{\"id\": \"1\",\"comite\": \"Comite1\",\"formulario\": {\"id\": 1,\"nombre\": \"M10\",\"descripcion\": \"Descripcion\"}}";

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setFormulario(new Formulario(1L, "M10", "Descripcion"));
    comite.setComite("Replace Comite1");

    BDDMockito.given(comiteService.update(ArgumentMatchers.<Comite>any())).will((InvocationOnMock invocation) -> {
      throw new ComiteNotFoundException(((Comite) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceComiteJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  /* Eliminar un Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void deleteComite() throws Exception {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    BDDMockito.given(comiteService.findById(ArgumentMatchers.anyLong()))
        .willReturn(new Comite(1L, "Comite1", formulario, Boolean.TRUE));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  /* Retorna lista paginada Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void findAll_WithPaging_ReturnsComiteSubList() throws Exception {

    // given: Cien Comite
    List<Comite> comiteList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Formulario formulario = new Formulario(Long.valueOf(i), "M" + i, "Descripcion");
      comiteList.add(new Comite(Long.valueOf(i), "Comite" + String.format("%03d", i), formulario, Boolean.TRUE));
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
      Assertions.assertThat(comite.getComite()).isEqualTo("Comite" + String.format("%03d", j));
    }
  }

  /* Retorna lista filtrada Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredComiteList() throws Exception {

    // given: Dos Comite y una query

    List<Comite> comiteList = new ArrayList<>();
    Formulario formulario1 = new Formulario(1L, "M10", "Descripcion");
    comiteList.add(new Comite(1L, "Comite1", formulario1, Boolean.TRUE));
    Formulario formulario2 = new Formulario(1L, "M20", "Descripcion");
    comiteList.add(new Comite(2L, "Comite2", formulario2, Boolean.TRUE));

    String query = "comite~Comite%";

    BDDMockito.given(comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Comite>>() {
          @Override
          public Page<Comite> answer(InvocationOnMock invocation) throws Throwable {
            List<Comite> content = new ArrayList<>();
            for (Comite comite : comiteList) {
              if (comite.getComite().startsWith("Comite")) {
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
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findTipoMemoriaEmtyList() throws Exception {
    // given: El comité no tiene tipos de memoria asociados
    Long id = 3L;
    final String url = new StringBuffer(COMITE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID).append("/tipo-memorias")
        .toString();

    BDDMockito
        .given(tipoMemoriaComiteService.findByComite(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findTipoMemoriaValid() throws Exception {
    // given: Datos existentes
    Long id = 3L;
    final String url = new StringBuffer(COMITE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID).append("/tipo-memorias")
        .toString();

    List<TipoMemoria> tipoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      TipoMemoria tipoMemoria = generarMockTipoMemoria(Long.valueOf(i), "tipo-" + String.format("%03d", i),
          Boolean.TRUE);

      tipoMemorias.add(tipoMemoria);
    }

    BDDMockito
        .given(tipoMemoriaComiteService.findByComite(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoMemoria>>() {
          @Override
          public Page<TipoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoMemoria> content = new ArrayList<>();
            for (TipoMemoria tipoMemoria : tipoMemorias) {
              content.add(tipoMemoria);
            }
            return new PageImpl<>(content);
          }
        });
    // when: Se buscan todos los tipo memoria del comité
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recuperan todos los tipo memoria relacionados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100))).andReturn();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
  public void findMemoriasEmtyList() throws Exception {
    // given: El comité no tiene tipos de memoria asociados
    Long id = 3L;
    final String url = new StringBuffer(COMITE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID_COMITE)
        .append("/memorias-peticion-evaluacion").append(PATH_PARAMETER_ID_PETICION_EVALUACION).toString();

    BDDMockito.given(memoriaService.findByComiteAndPeticionEvaluacion(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id, 1L).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se recupera lista vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-C-INV", "ETI-PEV-ER-INV" })
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

    BDDMockito.given(memoriaService.findByComiteAndPeticionEvaluacion(ArgumentMatchers.anyLong(),
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
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

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

    return new Memoria(id, numReferencia, peticionEvaluacion, new Comite(1L, "CEISH", new Formulario(), Boolean.TRUE),
        titulo, "user-00" + id, generarMockTipoMemoria(1L, "TipoMemoria1", true), new TipoEstadoMemoria(),
        Instant.now(), Boolean.TRUE, null, version, "CodOrganoCompetente", Boolean.TRUE, null);
  }

}