package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.converter.GrupoEquipoConverter;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoOutput;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo.Dedicacion;
import org.crue.hercules.sgi.csp.service.GrupoEquipoService;
import org.crue.hercules.sgi.csp.service.GrupoLineaInvestigadorService;
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
 * GrupoEquipoControllerTest
 */
@WebMvcTest(GrupoEquipoController.class)
class GrupoEquipoControllerTest extends BaseControllerTest {

  private static final String CONTROLLER_BASE_PATH = GrupoEquipoController.REQUEST_MAPPING;

  @MockBean
  private GrupoEquipoService service;

  @MockBean
  private GrupoEquipoConverter converter;

  @MockBean
  private GrupoLineaInvestigadorService grupoLineaInevestigadorService;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-GIN-C", "CSP-GIN-E" })
  void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 GrupoEquipo
    String personaRef = "personaRef-";
    List<GrupoEquipo> gruposEquipo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      gruposEquipo.add(generarMockGrupoEquipo(i, i, personaRef + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > gruposEquipo.size() ? gruposEquipo.size() : toIndex;
          List<GrupoEquipo> content = gruposEquipo.subList(fromIndex, toIndex);
          Page<GrupoEquipo> pageResponse = new PageImpl<>(content, pageable, gruposEquipo.size());
          return pageResponse;
        });

    BDDMockito.given(converter.convert(ArgumentMatchers.<Page<GrupoEquipo>>any()))
        .willAnswer(new Answer<Page<GrupoEquipoOutput>>() {
          @Override
          public Page<GrupoEquipoOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<GrupoEquipo> pageInput = invocation.getArgument(0);
            List<GrupoEquipoOutput> content = pageInput.getContent().stream().map(input -> {
              return generarMockGrupoEquipoOutput(input);
            }).collect(Collectors.toList());
            Page<GrupoEquipoOutput> pageOutput = new PageImpl<>(content,
                pageInput.getPageable(),
                pageInput.getTotalElements());
            return pageOutput;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los GrupoEquipo del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<GrupoEquipoOutput> gruposEquipoResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<GrupoEquipoOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      GrupoEquipoOutput grupoEquipo = gruposEquipoResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(grupoEquipo.getPersonaRef()).isEqualTo(personaRef + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-GIN-C", "CSP-GIN-E" })
  void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de GrupoEquipo
    List<GrupoEquipo> gruposEquipo = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<GrupoEquipo> pageResponse = new PageImpl<>(gruposEquipo, pageable, 0);
          return pageResponse;
        });

    BDDMockito.given(converter.convert(ArgumentMatchers.<Page<GrupoEquipo>>any()))
        .willAnswer(new Answer<Page<GrupoEquipoOutput>>() {
          @Override
          public Page<GrupoEquipoOutput> answer(InvocationOnMock invocation) throws Throwable {
            Page<GrupoEquipoOutput> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private GrupoEquipo generarMockGrupoEquipo(Long id, Long grupoId, String personaRef) {
    return generarMockGrupoEquipo(id, grupoId, Dedicacion.COMPLETA, new BigDecimal("100"), personaRef,
        Instant.now().plus(id, ChronoUnit.DAYS));
  }

  private GrupoEquipo generarMockGrupoEquipo(Long id, Long grupoId, Dedicacion dedicacion, BigDecimal participacion,
      String personaRef, Instant fechaInicio) {
    return GrupoEquipo.builder()
        .id(id)
        .grupoId(grupoId)
        .dedicacion(dedicacion)
        .participacion(participacion)
        .personaRef(personaRef)
        .fechaInicio(fechaInicio)
        .build();
  }

  private GrupoEquipoOutput generarMockGrupoEquipoOutput(GrupoEquipo input) {
    return generarMockGrupoEquipoOutput(input.getId(), input.getDedicacion(), input.getParticipacion(),
        input.getPersonaRef(), input.getFechaInicio());
  }

  private GrupoEquipoOutput generarMockGrupoEquipoOutput(Long id, Dedicacion dedicacion, BigDecimal participacion,
      String personaRef, Instant fechaInicio) {
    return GrupoEquipoOutput.builder()
        .id(id)
        .dedicacion(dedicacion)
        .participacion(participacion)
        .personaRef(personaRef)
        .fechaInicio(fechaInicio)
        .build();
  }
}
