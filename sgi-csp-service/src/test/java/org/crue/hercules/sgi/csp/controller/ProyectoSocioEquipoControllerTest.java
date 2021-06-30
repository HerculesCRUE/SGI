package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.service.ProyectoSocioEquipoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ProyectoSocioEquipoControllerTest
 */
@WebMvcTest(ProyectoSocioEquipoController.class)
public class ProyectoSocioEquipoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoSocioEquipoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectosocioequipos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void updateProyectoSocioEquipoesConvocatoria_ReturnsProyectoSocioEquipoList() throws Exception {
    // given: una lista con uno de los ProyectoSocioEquipo actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long proyectoSocioId = 1L;
    ProyectoSocioEquipo newProyectoSocioEquipo = generarMockProyectoSocioEquipo(null);
    ProyectoSocioEquipo updatedProyectoSocioEquipo = generarMockProyectoSocioEquipo(4L);

    List<ProyectoSocioEquipo> proyectoSocioEquipos = Arrays.asList(updatedProyectoSocioEquipo, newProyectoSocioEquipo);

    BDDMockito.given(service.update(ArgumentMatchers.anyLong(), ArgumentMatchers.<ProyectoSocioEquipo>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ProyectoSocioEquipo> proyectoSocioEquipoRecuperados = invocation.getArgument(1);
          return proyectoSocioEquipoRecuperados.stream().map(proyectoSocioEquipo -> {
            if (proyectoSocioEquipo.getId() == null) {
              proyectoSocioEquipo.setId(5L);
            }
            proyectoSocioEquipo.setProyectoSocioId(proyectoSocioId);
            return proyectoSocioEquipo;
          }).collect(Collectors.toList());
        });

    // when: updateProyectoSocioEquipoesConvocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoSocioEquipos)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Se crea el nuevo ProyectoSocioEquipo, se actualiza el
        // existe y se eliminan el resto
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(proyectoSocioEquipos.get(0).getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].proyectoSocioId").value(proyectoSocioId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].personaRef").value(proyectoSocioEquipos.get(0).getPersonaRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fechaFin").value(proyectoSocioEquipos.get(0).getFechaFin()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fechaInicio")
            .value(proyectoSocioEquipos.get(0).getFechaInicio().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].rolProyecto.id")
            .value(proyectoSocioEquipos.get(0).getRolProyecto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(5))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].proyectoSocioId").value(proyectoSocioId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].personaRef").value(proyectoSocioEquipos.get(1).getPersonaRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fechaFin").value(proyectoSocioEquipos.get(1).getFechaFin()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fechaInicio")
            .value(proyectoSocioEquipos.get(1).getFechaInicio().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].rolProyecto.id")
            .value(proyectoSocioEquipos.get(1).getRolProyecto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(5));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void updateProyectoSocioEquipoesConvocatoria_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoSocioEquipo proyectoSocioEquipo = generarMockProyectoSocioEquipo(1L);

    BDDMockito.willThrow(new ProyectoSocioEquipoNotFoundException(id)).given(service).update(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<ProyectoSocioEquipo>anyList());

    // when: updateProyectoSocioEquipoesConvocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Arrays.asList(proyectoSocioEquipo))))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithExistingId_ReturnsProyectoSocioEquipo() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoSocioEquipo(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoSocioEquipo is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("proyectoSocioId").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("rolProyecto.id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "AUTH" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoSocioEquipoNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que genera un ProyectoSocioEquipo
   * 
   * @param id Identificador
   * @return el ProyectoSocioEquipo
   */
  private ProyectoSocioEquipo generarMockProyectoSocioEquipo(Long id) {
    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .id(id)
        .abreviatura("001")
        .nombre("nombre-001")
        .descripcion("descripcion-001")
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();

    RolProyecto rolProyecto = RolProyecto.builder()
        .id(id)
        .abreviatura("001")
        .nombre("nombre-001")
        .descripcion("descripcion-001")
        .rolPrincipal(Boolean.FALSE)
        .equipo(RolProyecto.Equipo.INVESTIGACION)
        .activo(Boolean.TRUE)
        .build();

    ProyectoSocio proyectoSocio1 = ProyectoSocio.builder()
        .id(id)
        .proyectoId(id)
        .empresaRef("empresa-0041")
        .rolSocio(rolSocio)
        .build();
    // @formatter:on

    ProyectoSocioEquipo proyectoSocioEquipo = new ProyectoSocioEquipo(id, proyectoSocio1.getId(), rolProyecto, "001",
        Instant.parse("2021-04-10T00:00:00Z"), null);

    return proyectoSocioEquipo;
  }
}