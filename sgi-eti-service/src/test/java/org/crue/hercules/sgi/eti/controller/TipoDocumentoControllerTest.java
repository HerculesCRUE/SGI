package org.crue.hercules.sgi.eti.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.eti.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
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
 * TipoDocumentoControllerTest
 */
@WebMvcTest(TipoDocumentoController.class)
public class TipoDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private TipoDocumentoService tipoDocumentoService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_DOCUMENTO_CONTROLLER_BASE_PATH = "/tipodocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void getTipoDocumento_WithId_ReturnsTipoDocumento() throws Exception {

    BDDMockito.given(tipoDocumentoService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockTipoDocumento(1L, "TipoDocumento1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value").value("TipoDocumento1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void getTipoDocumento_NotFound_Returns404() throws Exception {
    BDDMockito.given(tipoDocumentoService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoDocumentoNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findAll_Unlimited_ReturnsFullTipoDocumentoList() throws Exception {
    // given: One hundred TipoDocumento
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoDocumentos));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoDocumento
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findAll_ReturnsNoContent() throws Exception {
    // given: TipoDocumento empty
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoDocumentos));

    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findAll_WithPaging_ReturnsTipoDocumentoSubList() throws Exception {
    // given: One hundred TipoDocumento
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoDocumento> content = tipoDocumentos.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tipoDocumentos.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked TipoDocumentos are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoDocumento> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoDocumento>>() {
        });

    // containing nombre='TipoDocumento031' to 'TipoDocumento040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoDocumento tipoDocumento = actual.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES))
          .isEqualTo("TipoDocumento" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-TIPODOCUMENTO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredTipoDocumentoList() throws Exception {
    // given: One hundred TipoDocumento and a search query
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }
    String query = "nombre.value~TipoDocumento%,id:5";

    BDDMockito.given(tipoDocumentoService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            List<TipoDocumento> content = new ArrayList<>();
            for (TipoDocumento tipoDocumento : tipoDocumentos) {
              if (I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES).startsWith("TipoDocumento")
                  && tipoDocumento.getId().equals(5L)) {
                content.add(tipoDocumento);
              }
            }
            Page<TipoDocumento> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred TipoDocumento
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id     id del tipoDocumento
   * @param nombre la descripción del tipoDocumento
   * @return el objeto tipoDocumento
   */

  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    Set<TipoDocumentoNombre> nom = new HashSet<>();
    nom.add(new TipoDocumentoNombre(Language.ES, nombre));
    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nom);
    tipoDocumento.setFormularioId(1L);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

}
