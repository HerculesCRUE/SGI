package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.ProyectoEntidadConvocanteDto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadConvocanteService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ProyectoEntidadConvocanteController.class)
class ProyectoEntidadConvocanteControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoEntidadConvocanteService service;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findAllProyectoEntidadConvocantes_EmptyList_Returns204() throws Exception {
    // given: empty ProyectoEntidadConvocante list
    Long proyectoId = 1L;
    List<ProyectoEntidadConvocante> proyectoEntidadConvocantes = new ArrayList<>();

    BDDMockito.given(service.findAllByProyecto(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoEntidadConvocante>>() {
          @Override
          public Page<ProyectoEntidadConvocante> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ProyectoEntidadConvocante> page = new PageImpl<>(proyectoEntidadConvocantes, pageable, 0);
            return page;
          }
        });

    // when: request the ProyectoEntidadConvocanteDto list for proyectoId
    mockMvc
        .perform(MockMvcRequestBuilders.get(ProyectoEntidadConvocanteController.REQUEST_MAPPING, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findAllProyectoEntidadConvocantes_ReturnsPage() throws Exception {
    // given: ProyectoEntidadConvocante list
    Long proyectoId = 1L;

    List<ProyectoEntidadConvocante> proyectoEntidadConvocantes = new ArrayList<>();
    for (long i = 1; i <= 10; i++) {
      ProyectoEntidadConvocante proyectoEntidadConvocante = ProyectoEntidadConvocante.builder().id(i)
          .proyectoId(proyectoId).entidadRef("entidadRef" + i).build();
      proyectoEntidadConvocantes.add(proyectoEntidadConvocante);
    }

    BDDMockito.given(service.findAllByProyecto(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoEntidadConvocante>>() {
          @Override
          public Page<ProyectoEntidadConvocante> answer(InvocationOnMock invocation) throws Throwable {
            return new PageImpl<>(proyectoEntidadConvocantes);
          }
        });

    // when: request the ProyectoEntidadConvocanteDto list for proyectoId
    mockMvc
        .perform(MockMvcRequestBuilders.get(ProyectoEntidadConvocanteController.REQUEST_MAPPING, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: ProyectoEntidadConvocanteDto list is returned
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "0"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "10"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void updateConvocatoriaEntidadesConvocantes_Returns200() throws Exception {
    // given: Una lista con una ProyectoEntidadConvocante
    Long proyectoId = 1L;
    String entidadRef = "entidadRef";
    ProyectoEntidadConvocanteDto entidadConvocante = ProyectoEntidadConvocanteDto.builder().id(1L)
        .entidadRef(entidadRef)
        .build();
    List<ProyectoEntidadConvocanteDto> entidadesConvocantesToCreate = Arrays.asList(entidadConvocante);

    BDDMockito.given(service
        .updateEntidadesConvocantesProyecto(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<ProyectoEntidadConvocante>>any()))
        .will((InvocationOnMock invocation) -> {
          Long proyectoIdParam = invocation.getArgument(0, Long.class);
          List<ProyectoEntidadConvocante> entidadConvocantesCreados = invocation.getArgument(1);
          AtomicLong index = new AtomicLong();
          return entidadConvocantesCreados.stream().map(proyectoEntidadConvocanteCreado -> {
            proyectoEntidadConvocanteCreado.setProyectoId(proyectoIdParam);
            proyectoEntidadConvocanteCreado.setId(index.incrementAndGet());

            return proyectoEntidadConvocanteCreado;
          }).collect(Collectors.toList());
        });
    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders
                .patch(ProyectoEntidadConvocanteController.REQUEST_MAPPING, proyectoId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(entidadesConvocantesToCreate)))
        // then: Devuelve un 200
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].entidadRef").value(entidadRef));
  }

}
