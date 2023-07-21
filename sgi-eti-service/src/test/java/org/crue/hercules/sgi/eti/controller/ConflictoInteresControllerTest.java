package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;

import org.crue.hercules.sgi.eti.exceptions.ConflictoInteresNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.service.ConflictoInteresService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
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
 * ConflictoInteresControllerTest
 */
@WebMvcTest(ConflictoInteresController.class)
public class ConflictoInteresControllerTest extends BaseControllerTest {

  @MockBean
  private ConflictoInteresService conflictoInteresService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONFLICTO_INTERES_CONTROLLER_BASE_PATH = "/conflictosinteres";

  /* Crear Conflicto interés */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C", "ETI-EVR-E" })
  public void addConflictoInteres_ReturnsConflictoInteres() throws Exception {

    // given: Un conflicto de interés nuevo
    String nuevoConflictoInteresJson = mapper.writeValueAsString(generarMockConflictoInteres(null, null));

    ConflictoInteres returnConflicto = generarMockConflictoInteres(1L, null);

    BDDMockito.given(conflictoInteresService.create(ArgumentMatchers.<ConflictoInteres>any()))
        .willReturn(returnConflicto);

    // when: Creamos un conflicto de interés

    mockMvc
        .perform(MockMvcRequestBuilders.post(CONFLICTO_INTERES_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoConflictoInteresJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo ConflictoInteres y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("personaConflictoRef").value("user-001"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C" })
  public void addConflictoInteres_Error_Returns400() throws Exception {
    // given: Un ConflictoInteres nuevo que produce un error al crearse
    String nuevoConflictoInteresJson = mapper.writeValueAsString(generarMockConflictoInteres(1L, null));

    BDDMockito.given(conflictoInteresService.create(ArgumentMatchers.<ConflictoInteres>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un ConflictoInteres
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONFLICTO_INTERES_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoConflictoInteresJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())

        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  /* Modificar un ConflictoInteres */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C", "ETI-EVR-E" })
  public void replaceConflictoInteres_ReturnsConflictoInteres() throws Exception {
    // given: Un ConflictoInteres a modificar
    String replaceConflictoInteresJson = mapper.writeValueAsString(generarMockConflictoInteres(1L, null));

    ConflictoInteres conflictoInteres = generarMockConflictoInteres(1L, "user-123");

    BDDMockito.given(conflictoInteresService.update(ArgumentMatchers.<ConflictoInteres>any()))
        .willReturn(conflictoInteres);

    mockMvc
        .perform(MockMvcRequestBuilders.put(CONFLICTO_INTERES_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceConflictoInteresJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el ConflictoInteres y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("personaConflictoRef").value("user-123"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C", "ETI-EVR-E" })
  public void replaceConflictoInteres_NotFound() throws Exception {
    // given: Un ConflictoInteres a modificar
    String replaceConflictoInteresJson = mapper.writeValueAsString(generarMockConflictoInteres(2L, null));

    BDDMockito.given(conflictoInteresService.update(ArgumentMatchers.<ConflictoInteres>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ConflictoInteresNotFoundException(((ConflictoInteres) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONFLICTO_INTERES_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceConflictoInteresJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  /* Eliminar un ConflictoInteres */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVR-C", "ETI-EVR-E" })
  public void deleteConflictoInteres() throws Exception {

    BDDMockito.given(conflictoInteresService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockConflictoInteres(1L, null));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONFLICTO_INTERES_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */
  public Evaluador generarMockEvaluador(Long id, String resumen) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.now());
    evaluador.setFechaBaja(Instant.now());
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-00" + (id != null ? id : "1"));
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Función que devuelve un objeto ConflictoInteres
   * 
   * @param id                  id del ConflictoInteres
   * @param personaConflictoRef la persona del conflicto de interés
   * @return el objeto ConflictoInteres
   */
  public ConflictoInteres generarMockConflictoInteres(Long id, String personaConflictoInteres) {
    ConflictoInteres conflicto = new ConflictoInteres();
    conflicto.setId(id);
    conflicto.setEvaluador(generarMockEvaluador(id, "Resumen" + (id != null ? id : "1")));
    if (personaConflictoInteres != null) {
      conflicto.setPersonaConflictoRef(personaConflictoInteres);
    } else {
      conflicto.setPersonaConflictoRef("user-00" + (id != null ? id : "1"));
    }
    return conflicto;
  }

}