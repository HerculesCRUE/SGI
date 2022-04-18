package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ProyectoEquipoControllerTest
 */
@WebMvcTest(ProyectoEquipoController.class)
public class ProyectoEquipoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoEquipoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoequipos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyectoEquipoList() throws Exception {
    // given: una lista con uno de los ProyectoEquipo
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long proyectoId = 1L;
    ProyectoEquipo newProyectoEquipo = generarMockProyectoEquipo(null, Instant.parse("2020-12-16T00:00:00Z"),
        Instant.parse("2020-12-18T23:59:59Z"), 1L);
    ProyectoEquipo updatedProyectoEquipo = generarMockProyectoEquipo(4L, Instant.parse("2020-04-02T00:00:00Z"),
        Instant.parse("2020-04-15T23:59:59Z"), 1L);

    List<ProyectoEquipo> proyectoEquipos = Arrays.asList(updatedProyectoEquipo, newProyectoEquipo);

    BDDMockito.given(service.update(ArgumentMatchers.anyLong(), ArgumentMatchers.<ProyectoEquipo>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ProyectoEquipo> proyectoEquiposRecuperados = invocation.getArgument(1);
          return proyectoEquiposRecuperados.stream().map(proyectoEquipo -> {
            if (proyectoEquipo.getId() == null) {
              proyectoEquipo.setId(5L);
            }
            proyectoEquipo.setProyectoId(proyectoId);
            return proyectoEquipo;
          }).collect(Collectors.toList());
        });

    // when: update
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoEquipos)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se crea el nuevo ProyectoEquipo, se actualiza
        // el
        // existe y se eliminan el resto
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(proyectoEquipos.get(0).getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].proyectoId").value(proyectoId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].personaRef").value(proyectoEquipos.get(0).getPersonaRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].rolProyecto.id")
            .value(proyectoEquipos.get(0).getRolProyecto().getId()))

        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(5))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].proyectoId").value(proyectoId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].personaRef").value(proyectoEquipos.get(0).getPersonaRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].rolProyecto.id")
            .value(proyectoEquipos.get(0).getRolProyecto().getId()));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoEquipo proyectoEquipo = generarMockProyectoEquipo(1L, Instant.parse("2020-04-02T00:00:00Z"),
        Instant.parse("2020-04-15T23:59:59Z"), 1L);

    BDDMockito.willThrow(new ProyectoEquipoNotFoundException(id)).given(service).update(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<ProyectoEquipo>anyList());

    // when: update
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Arrays.asList(proyectoEquipo))))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithExistingId_ReturnsProyectoEquipo() throws Exception {
    // given: existing id
    Long proyectoEquipoId = 1L;

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<ProyectoEquipo>() {
      @Override
      public ProyectoEquipo answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return ProyectoEquipo.builder().id(id).build();
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoEquipoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoEquipo is resturned as
        // JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoEquipoId));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long proyectoEquipoId = 1L;

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoEquipoNotFoundException(proyectoEquipoId);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoEquipoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto ProyectoEquipo
   * 
   * @param id         id del ProyectoEquipo
   * @param mesInicial Mes inicial
   * @param mesFinal   Mes final
   * @param proyectoId Id Proyecto
   * @return el objeto ProyectoEquipo
   */
  private ProyectoEquipo generarMockProyectoEquipo(Long id, Instant fechaInicio, Instant fechaFin, Long proyectoId) {

    ProyectoEquipo proyectoEquipo = ProyectoEquipo.builder().id(id).proyectoId(proyectoId)
        .rolProyecto(RolProyecto.builder().id(1L).build()).fechaInicio(fechaInicio).fechaFin(fechaFin).personaRef("001")
        .build();

    return proyectoEquipo;

  }

}
