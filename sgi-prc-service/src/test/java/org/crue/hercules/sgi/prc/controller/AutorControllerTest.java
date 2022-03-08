package org.crue.hercules.sgi.prc.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.prc.dto.AutorGrupoOutput;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.service.AutorGrupoService;
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
 * AutorControllerTest
 */
@WebMvcTest(AutorController.class)
public class AutorControllerTest extends BaseControllerTest {

  @MockBean
  private AutorGrupoService autorGrupoService;

  private static final String CONTROLLER_BASE_PATH = AutorController.MAPPING;
  private static final String PATH_GRUPOS = AutorController.PATH_GRUPOS;

  /**
   * 
   * GRUPO
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  public void findAcreditaciones_ReturnsPage() throws Exception {
    // given: Una lista con 37 AutorGrupo para el Autor
    Long autorId = 1L;

    List<AutorGrupo> autorGrupos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      autorGrupos.add(generarMockAutorGrupo(i, autorId));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(autorGrupoService
        .findAllByAutorId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<AutorGrupo>>() {
          @Override
          public Page<AutorGrupo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > autorGrupos.size() ? autorGrupos.size() : toIndex;
            List<AutorGrupo> content = autorGrupos.subList(fromIndex, toIndex);
            Page<AutorGrupo> page = new PageImpl<>(content, pageable, autorGrupos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_GRUPOS, autorId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los AutorGrupo del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<AutorGrupoOutput> autorGrupoResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<AutorGrupoOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      AutorGrupoOutput autorGrupo = autorGrupoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(autorGrupo.getGrupoRef())
          .isEqualTo("Grupo-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "PRC-VAL-V" })
  public void findAcreditaciones_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de AutorGrupo para el Autor
    Long autorId = 1L;
    List<AutorGrupo> indicesImpacto = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(autorGrupoService.findAllByAutorId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<AutorGrupo>>() {
          @Override
          public Page<AutorGrupo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<AutorGrupo> page = new PageImpl<>(indicesImpacto, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_GRUPOS, autorId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private AutorGrupo generarMockAutorGrupo(Long id, Long idRef) {
    AutorGrupo autor = new AutorGrupo();
    autor.setId(id);
    autor.setAutorId(idRef);
    autor.setGrupoRef("Grupo-" + String.format("%03d", id));

    return autor;
  }
}
