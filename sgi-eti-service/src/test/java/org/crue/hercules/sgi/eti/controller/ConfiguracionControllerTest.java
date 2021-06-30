package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
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
 * ConfiguracionControllerTest
 */
@WebMvcTest(ConfiguracionController.class)
public class ConfiguracionControllerTest extends BaseControllerTest {

  @MockBean
  private ConfiguracionService configuracionService;

  private static final String CONFIGURACION_CONTROLLER_BASE_PATH = "/configuraciones";
  private static final String PATH_PARAMETER_ID = "/{id}";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNF-E" })
  public void getConfiguracion_WithId_ReturnsConfiguracion() throws Exception {
    BDDMockito.given(configuracionService.findConfiguracion()).willReturn((generarMockConfiguracion()));

    mockMvc
        .perform(MockMvcRequestBuilders.get(CONFIGURACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("diasLimiteEvaluador").value(3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNF-E" })
  public void replaceConfiguracion_ReturnsConfiguracion() throws Exception {
    // given: Una configuracion a modificar
    String replaceConfiguracionJson = "{\"id\": 1, \"diasLimiteEvaluador\": 1, \"diasArchivadaPendienteCorrecciones\": 4, \"mesesArchivadaInactivo\":1, \"mesesAvisoProyectoCEEA\":1, \"mesesAvisoProyectoCEISH\":1, \"mesesAvisoProyectoCEIAB\":1}";

    Configuracion configuracion = generarMockConfiguracion();

    BDDMockito.given(configuracionService.update(ArgumentMatchers.<Configuracion>any())).willReturn(configuracion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceConfiguracionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica la configuracion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("diasLimiteEvaluador").value(3));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CNF-E" })
  public void replaceConfiguracion_NotFound() throws Exception {
    // given: Una configuracion a modificar
    String replaceConfiguracionJson = "{\"id\": 1, \"diasLimiteEvaluador\": 1, \"diasArchivadaPendienteCorrecciones\": 4, \"mesesArchivadaInactivo\":1, \"mesesAvisoProyectoCEEA\":1, \"mesesAvisoProyectoCEISH\":1, \"mesesAvisoProyectoCEIAB\":1}";

    BDDMockito.given(configuracionService.update(ArgumentMatchers.<Configuracion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ConfiguracionNotFoundException(((Configuracion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceConfiguracionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracion() {

    Configuracion configuracion = new Configuracion();

    configuracion.setId(1L);
    configuracion.setDiasArchivadaPendienteCorrecciones(20);
    configuracion.setDiasLimiteEvaluador(3);
    configuracion.setMesesArchivadaInactivo(2);
    configuracion.setMesesAvisoProyectoCEEA(1);
    configuracion.setMesesAvisoProyectoCEISH(1);
    configuracion.setMesesAvisoProyectoCEIAB(1);

    return configuracion;
  }

}
